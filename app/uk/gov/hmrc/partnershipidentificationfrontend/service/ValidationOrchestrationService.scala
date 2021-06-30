/*
 * Copyright 2021 HM Revenue & Customs
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

  def orchestrate(journeyId: String)(implicit hc: HeaderCarrier): Future[ValidationResponse] =
    partnershipIdentificationService.retrievePartnershipInformation(journeyId).flatMap {
      case Some(PartnershipInformation(Some(SaInformation(sautr, postcode)))) =>
        validatePartnershipInformationService.validateIdentifiers(sautr, postcode).flatMap {
          identifiersMatch =>
            if (identifiersMatch) {
              partnershipIdentificationService.storeIdentifiersMatch(journeyId, identifiersMatch).map {
                _ => IdentifiersMatched
              }
            }
            else {
              for {
                _ <- partnershipIdentificationService.storeIdentifiersMatch(journeyId, identifiersMatch)
                _ <- partnershipIdentificationService.storeBusinessVerificationStatus(journeyId, BusinessVerificationUnchallenged)
              } yield
                IdentifiersMismatch
            }
        }
      case Some(PartnershipInformation(None)) =>
        for {
          _ <- partnershipIdentificationService.storeIdentifiersMatch(journeyId, identifiersMatch = false)
          _ <- partnershipIdentificationService.storeBusinessVerificationStatus(journeyId, BusinessVerificationUnchallenged)
          _ <- partnershipIdentificationService.storeRegistrationStatus(journeyId, RegistrationNotCalled)
        } yield
          NoSautrProvided
      case _ =>
        throw new InternalServerException(s"No data stored for journeyId: $journeyId")
    }

}
