/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.partnershipidentificationfrontend.service

import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.partnershipidentificationfrontend.models._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ValidationOrchestrationService @Inject()(partnershipIdentificationService: PartnershipIdentificationService,
                                               validatePartnershipInformationService: ValidatePartnershipInformationService
                                              )(implicit ec: ExecutionContext) {

  def orchestrate(journeyId: String, businessVerificationCheck: Boolean)(implicit hc: HeaderCarrier): Future[ValidationResponse] = {
    partnershipIdentificationService.retrievePartnershipInformation(journeyId).flatMap {
      case Some(PartnershipInformation(optSaInformation, optCompanyProfile)) => for {
        identifiersMatch <- (optSaInformation, optCompanyProfile) match {
          case (Some(SaInformation(sautr, postcode)), None) =>
            validatePartnershipInformationService.validateIdentifiers(sautr, postcode)
          case (Some(SaInformation(sautr, postcode)), Some(companyProfile)) =>
            companyProfile.optRegisteredOfficePostcode match {
              case Some(registeredOfficePostcode) => for {
                saPostcodeMatches <- validatePartnershipInformationService.validateIdentifiers(sautr, postcode)
                registeredOfficePostcodeMatches <- validatePartnershipInformationService.validateIdentifiers(sautr, registeredOfficePostcode)
              } yield saPostcodeMatches && registeredOfficePostcodeMatches
              case None =>
                Future.successful(false) //If there is no address held on Companies House, fail to match
            }
          case (None, _) =>
            Future.successful(false)
        }
        _ <- partnershipIdentificationService.storeIdentifiersMatch(journeyId, identifiersMatch = identifiersMatch)
        _ <- if (businessVerificationCheck && !identifiersMatch) {
          partnershipIdentificationService.storeBusinessVerificationStatus(journeyId, BusinessVerificationUnchallenged)
        } else {
          Future.successful(())
        }
        _ <- if (!identifiersMatch) {
          partnershipIdentificationService.storeRegistrationStatus(journeyId, RegistrationNotCalled)
        } else {
          Future.successful(())
        }
      } yield if (optSaInformation.isDefined) {
        if (identifiersMatch) {
          IdentifiersMatched
        } else {
          IdentifiersMismatch
        }
      } else {
        NoSautrProvided
      }
      case _ =>
        throw new InternalServerException(s"No data stored for journeyId: $journeyId")
    }
  }
}
