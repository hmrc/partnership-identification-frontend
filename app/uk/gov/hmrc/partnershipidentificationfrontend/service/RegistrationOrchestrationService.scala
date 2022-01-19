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
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.RegistrationConnector
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.{GeneralPartnership, LimitedLiabilityPartnership, LimitedPartnership, PartnershipType, ScottishLimitedPartnership, ScottishPartnership}
import uk.gov.hmrc.partnershipidentificationfrontend.models.{BusinessVerificationPass, RegistrationNotCalled, RegistrationStatus}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegistrationOrchestrationService @Inject()(partnershipIdentificationService: PartnershipIdentificationService,
                                                 registrationConnector: RegistrationConnector
                                                )(implicit ec: ExecutionContext) {

  def register(journeyId: String,
               partnershipType: PartnershipType,
               businessVerificationCheck: Boolean,
               regime: String)(implicit hc: HeaderCarrier): Future[RegistrationStatus] = for {
    registerEntity <-
      if (businessVerificationCheck) {
      partnershipIdentificationService.retrieveBusinessVerificationStatus(journeyId).map {
        case Some(BusinessVerificationPass) => true
        case Some(_) => false
        case None => throw new InternalServerException(s"Missing business verification state in database for $journeyId")
      }
    }
      else Future.successful(true)
    registrationStatus <-
      if (registerEntity) {
        for {
          optSautr <- partnershipIdentificationService.retrieveSautr(journeyId)
          optCompanyProfile <- partnershipIdentificationService.retrieveCompanyProfile(journeyId)
          registrationStatus <- (partnershipType, optSautr, optCompanyProfile) match {
            case (GeneralPartnership, Some(sautr), None) =>
              registrationConnector.registerGeneralPartnership(sautr, regime)
            case (ScottishPartnership, Some(sautr), None) =>
              registrationConnector.registerScottishPartnership(sautr, regime)
            case (LimitedPartnership, Some(sautr), Some(companyProfile)) =>
              registrationConnector.registerLimitedPartnership(sautr, companyProfile.companyNumber, regime)
            case (LimitedLiabilityPartnership, Some(sautr), Some(companyProfile)) =>
              registrationConnector.registerLimitedLiabilityPartnership(sautr, companyProfile.companyNumber, regime)
            case (ScottishLimitedPartnership, Some(sautr), Some(companyProfile)) =>
              registrationConnector.registerScottishLimitedPartnership(sautr, companyProfile.companyNumber, regime)
            case _ =>
              throw new InternalServerException(s"Invalid registration data in the database for $journeyId")
          }
        } yield registrationStatus
      }
      else {
        Future.successful(RegistrationNotCalled)
      }
    _ <- partnershipIdentificationService.storeRegistrationStatus(journeyId, registrationStatus)
  } yield registrationStatus

}
