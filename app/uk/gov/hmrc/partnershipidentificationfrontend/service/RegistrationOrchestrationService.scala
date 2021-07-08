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
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.RegistrationConnector
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.{GeneralPartnership, PartnershipType, ScottishPartnership}
import uk.gov.hmrc.partnershipidentificationfrontend.models.{BusinessVerificationPass, RegistrationNotCalled, RegistrationStatus}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegistrationOrchestrationService @Inject()(partnershipIdentificationService: PartnershipIdentificationService,
                                                 registrationConnector: RegistrationConnector
                                                )(implicit ec: ExecutionContext) {

  def register(journeyId: String, partnershipType: PartnershipType)(implicit hc: HeaderCarrier): Future[RegistrationStatus] = for {
    registrationStatus <- partnershipIdentificationService.retrieveBusinessVerificationStatus(journeyId).flatMap {
      case Some(BusinessVerificationPass) => for {
        optSautr <- partnershipIdentificationService.retrieveSautr(journeyId)
        sautr = optSautr.getOrElse(throw new InternalServerException(s"SAUTR for registration cannot be found in the database for $journeyId"))
        registrationStatus <- partnershipType match {
          case GeneralPartnership => registrationConnector.registerGeneralPartnership(sautr)
          case ScottishPartnership => registrationConnector.registerScottishPartnership(sautr)
        }
      } yield registrationStatus
      case Some(_) =>
        Future.successful(RegistrationNotCalled)
      case None =>
        throw new InternalServerException(s"Missing business verification state in database for $journeyId")
    }
    _ <- partnershipIdentificationService.storeRegistrationStatus(journeyId, registrationStatus)
  } yield registrationStatus

}
