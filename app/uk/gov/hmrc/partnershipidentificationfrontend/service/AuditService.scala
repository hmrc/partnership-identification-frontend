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

import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.models.{BusinessVerificationFail, BusinessVerificationPass, BusinessVerificationUnchallenged, JourneyConfig, PartnershipFullJourneyData, Registered, RegistrationFailed, RegistrationNotCalled}
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuditService @Inject()(auditConnector: AuditConnector,
                             partnershipIdentificationService: PartnershipIdentificationService,
                             appConfig: AppConfig)(implicit ec: ExecutionContext) {
  def auditPartnershipInformation(journeyId: String, journeyConfig: JourneyConfig)(implicit hc: HeaderCarrier): Future[Unit] = {
    for {
      partnershipData <- partnershipIdentificationService.retrievePartnershipFullJourneyData(journeyId)
    } yield partnershipData match {
      case Some(PartnershipFullJourneyData(optPostcode, optSautr, _, identifiersMatch, businessVerification, registrationStatus)) =>
        val sautrJson = optSautr match {
          case Some(sautr) => Json.obj(
            "SAUTR" -> sautr
          )
          case None => Json.obj()
        }
        val postcodeJson = optPostcode match {
          case Some(postcode) => Json.obj(
            "SApostcode" -> postcode
          )
          case None => Json.obj()
        }
        val identifiersMatchJson = Json.obj {
          "isMatch" -> identifiersMatch
        }
        val businessTypeJson = Json.obj(
          "businessType" -> "General Partnership" //TODO - Update to be dynamic for different business types
        )
        val verificationStatusJson = Json.obj(
          "VerificationStatus" -> (businessVerification match {
            case BusinessVerificationPass =>
              "success"
            case BusinessVerificationFail =>
              "fail"
            case BusinessVerificationUnchallenged =>
              "Not Enough Information to challenge"
          })
        )
        val registerApiStatusJson = Json.obj(
          "RegisterApiStatus" -> (registrationStatus match {
            case Registered(_) =>
              "success"
            case RegistrationFailed =>
              "fail"
            case RegistrationNotCalled =>
              "not called"
          })
        )
        val serviceName = journeyConfig.pageConfig.optServiceName.getOrElse(appConfig.defaultServiceName)
        val callingServiceJson = Json.obj(
          "callingService" -> serviceName
        )

        val auditType = "GeneralPartnershipEntityRegistration" //TODO - Update to be dynamic for different business types

        val auditJson = (sautrJson
          ++ postcodeJson
          ++ identifiersMatchJson
          ++ businessTypeJson
          ++ verificationStatusJson
          ++ registerApiStatusJson
          ++ callingServiceJson)

        auditConnector.sendExplicitAudit(auditType, auditJson)
      case None =>
    }
  }
}
