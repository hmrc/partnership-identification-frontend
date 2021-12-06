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

import org.mockito.IdiomaticMockito
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.{GeneralPartnership, ScottishPartnership}
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuditServiceSpec extends AnyWordSpec with Matchers with IdiomaticMockito {

  trait Setup {
    val mockAuditConnector: AuditConnector = mock[AuditConnector]
    val mockPartnershipIdentificationService: PartnershipIdentificationService = mock[PartnershipIdentificationService]
    val mockAppConfig: AppConfig = mock[AppConfig]

    implicit val hc: HeaderCarrier = HeaderCarrier()

    object TestAuditService extends AuditService(
      mockAuditConnector,
      mockPartnershipIdentificationService,
      mockAppConfig
    )

  }

  def expectedAuditJson(partnershipType: String, verificationStatus: String, registerStatus: String, isMatch: Boolean = true): JsObject =
    Json.obj(
      "SAUTR" -> testSautr,
      "SApostcode" -> testPostcode,
      "isMatch" -> isMatch,
      "businessType" -> partnershipType,
      "VerificationStatus" -> verificationStatus,
      "RegisterApiStatus" -> registerStatus,
      "callingService" -> testServiceName
    )

  "auditPartnershipInformation" when {
    "the user is identifying a general partnership with all information" when {
      "the user has successfully passed business verification" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            PartnershipFullJourneyData(
              optPostcode = Some(testPostcode),
              optSautr = Some(testSautr),
              companyProfile = None,
              identifiersMatch = true,
              businessVerification = BusinessVerificationPass,
              registrationStatus = Registered(testBusinessPartnerId)
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("General Partnership", "success", "success")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            JourneyConfig(
              continueUrl = testContinueUrl,
              pageConfig = PageConfig(
                Some(testServiceName),
                testDeskProServiceId,
                testSignOutUrl
              ),
              partnershipType = GeneralPartnership
            )
          ))

          mockAuditConnector.sendExplicitAudit("GeneralPartnershipEntityRegistration", expectedAuditModel) was called
        }
      }
      "the user has failed business verification" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            PartnershipFullJourneyData(
              optPostcode = Some(testPostcode),
              optSautr = Some(testSautr),
              companyProfile = None,
              identifiersMatch = true,
              businessVerification = BusinessVerificationFail,
              registrationStatus = RegistrationNotCalled
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("General Partnership", "fail", "not called")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            JourneyConfig(
              continueUrl = testContinueUrl,
              pageConfig = PageConfig(
                Some(testServiceName),
                testDeskProServiceId,
                testSignOutUrl
              ),
              partnershipType = GeneralPartnership
            )
          ))
          mockAuditConnector.sendExplicitAudit("GeneralPartnershipEntityRegistration", expectedAuditModel) was called
        }
      }
      "the user has passed business verification but the registration call failed" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            PartnershipFullJourneyData(
              optPostcode = Some(testPostcode),
              optSautr = Some(testSautr),
              companyProfile = None,
              identifiersMatch = true,
              businessVerification = BusinessVerificationPass,
              registrationStatus = RegistrationFailed
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("General Partnership", "success", "fail")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            JourneyConfig(
              continueUrl = testContinueUrl,
              pageConfig = PageConfig(
                Some(testServiceName),
                testDeskProServiceId,
                testSignOutUrl
              ),
              partnershipType = GeneralPartnership
            )
          ))
          mockAuditConnector.sendExplicitAudit("GeneralPartnershipEntityRegistration", expectedAuditModel) was called
        }
      }
    }
    "the user is identifying a general partnership with no SAUTR and the calling service has not provided a service name" should {
      "audit the correct information" in new Setup {
        mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
          PartnershipFullJourneyData(
            optPostcode = None,
            optSautr = None,
            companyProfile = None,
            identifiersMatch = false,
            businessVerification = BusinessVerificationUnchallenged,
            registrationStatus = RegistrationNotCalled
          )
        ))
        mockAppConfig.defaultServiceName returns testServiceName

        val expectedAuditModel: JsObject = Json.obj(
          "isMatch" -> false,
          "businessType" -> "General Partnership",
          "VerificationStatus" -> "Not Enough Information to challenge",
          "RegisterApiStatus" -> "not called",
          "callingService" -> testServiceName
        )

        await(TestAuditService.auditPartnershipInformation(
          testJourneyId,
          JourneyConfig(
            continueUrl = testContinueUrl,
            pageConfig = PageConfig(
              None,
              testDeskProServiceId,
              testSignOutUrl
            ),
            partnershipType = GeneralPartnership
          )
        ))
        mockAuditConnector.sendExplicitAudit("GeneralPartnershipEntityRegistration", expectedAuditModel) was called
      }
    }
    "the user is identifying a scottish partnership with all information" when {
      "the user has successfully passed business verification" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            PartnershipFullJourneyData(
              optPostcode = Some(testPostcode),
              optSautr = Some(testSautr),
              companyProfile = None,
              identifiersMatch = true,
              businessVerification = BusinessVerificationPass,
              registrationStatus = Registered(testBusinessPartnerId)
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("Scottish Partnership", "success", "success")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            JourneyConfig(
              continueUrl = testContinueUrl,
              pageConfig = PageConfig(
                Some(testServiceName),
                testDeskProServiceId,
                testSignOutUrl
              ),
              partnershipType = ScottishPartnership
            )
          ))

          mockAuditConnector.sendExplicitAudit("ScottishPartnershipEntityRegistration", expectedAuditModel) was called
        }
      }
      "the user has failed business verification" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            PartnershipFullJourneyData(
              optPostcode = Some(testPostcode),
              optSautr = Some(testSautr),
              companyProfile = None,
              identifiersMatch = true,
              businessVerification = BusinessVerificationFail,
              registrationStatus = RegistrationNotCalled
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("Scottish Partnership", "fail", "not called")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            JourneyConfig(
              continueUrl = testContinueUrl,
              pageConfig = PageConfig(
                Some(testServiceName),
                testDeskProServiceId,
                testSignOutUrl
              ),
              partnershipType = ScottishPartnership
            )
          ))
          mockAuditConnector.sendExplicitAudit("ScottishPartnershipEntityRegistration", expectedAuditModel) was called
        }
      }
      "the user has passed business verification but the registration call failed" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            PartnershipFullJourneyData(
              optPostcode = Some(testPostcode),
              optSautr = Some(testSautr),
              companyProfile = None,
              identifiersMatch = true,
              businessVerification = BusinessVerificationPass,
              registrationStatus = RegistrationFailed
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("Scottish Partnership", "success", "fail")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            JourneyConfig(
              continueUrl = testContinueUrl,
              pageConfig = PageConfig(
                Some(testServiceName),
                testDeskProServiceId,
                testSignOutUrl
              ),
              partnershipType = ScottishPartnership
            )
          ))
          mockAuditConnector.sendExplicitAudit("ScottishPartnershipEntityRegistration", expectedAuditModel) was called
        }
      }
    }
    "the user is identifying a scottish partnership with no SAUTR and the calling service has not provided a service name" should {
      "audit the correct information" in new Setup {
        mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
          PartnershipFullJourneyData(
            optPostcode = None,
            optSautr = None,
            companyProfile = None,
            identifiersMatch = false,
            businessVerification = BusinessVerificationUnchallenged,
            registrationStatus = RegistrationNotCalled
          )
        ))
        mockAppConfig.defaultServiceName returns testServiceName

        val expectedAuditModel: JsObject = Json.obj(
          "isMatch" -> false,
          "businessType" -> "Scottish Partnership",
          "VerificationStatus" -> "Not Enough Information to challenge",
          "RegisterApiStatus" -> "not called",
          "callingService" -> testServiceName
        )

        await(TestAuditService.auditPartnershipInformation(
          testJourneyId,
          JourneyConfig(
            continueUrl = testContinueUrl,
            pageConfig = PageConfig(
              None,
              testDeskProServiceId,
              testSignOutUrl
            ),
            partnershipType = ScottishPartnership
          )
        ))
        mockAuditConnector.sendExplicitAudit("ScottishPartnershipEntityRegistration", expectedAuditModel) was called
      }
    }
  }
}

