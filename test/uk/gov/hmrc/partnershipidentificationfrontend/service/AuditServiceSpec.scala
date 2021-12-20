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
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType._
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuditServiceSpec extends AnyWordSpec with Matchers with IdiomaticMockito {

  trait Setup {
    val defaultScottishPartnershipJourneyConfig: JourneyConfig = testDefaultGeneralPartnershipJourneyConfig.copy(partnershipType = ScottishPartnership)
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
              businessVerification = Some(BusinessVerificationPass),
              registrationStatus = Registered(testBusinessPartnerId)
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("General Partnership", "success", "success")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            testDefaultGeneralPartnershipJourneyConfig,
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
              businessVerification = Some(BusinessVerificationFail),
              registrationStatus = RegistrationNotCalled
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("General Partnership", "fail", "not called")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            testDefaultGeneralPartnershipJourneyConfig
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
              businessVerification = Some(BusinessVerificationPass),
              registrationStatus = RegistrationFailed
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("General Partnership", "success", "fail")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            testDefaultGeneralPartnershipJourneyConfig
          ))
          mockAuditConnector.sendExplicitAudit("GeneralPartnershipEntityRegistration", expectedAuditModel) was called
        }
      }
      "there is no business verification status" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            PartnershipFullJourneyData(
              optPostcode = Some(testPostcode),
              optSautr = Some(testSautr),
              companyProfile = None,
              identifiersMatch = true,
              businessVerification = None,
              registrationStatus = RegistrationFailed
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("General Partnership", "not requested", "fail")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            testDefaultGeneralPartnershipJourneyConfig
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
            businessVerification = Some(BusinessVerificationUnchallenged),
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
          testDefaultGeneralPartnershipJourneyConfig.copy(pageConfig = testDefaultPageConfig.copy(optServiceName = None))
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
              businessVerification = Some(BusinessVerificationPass),
              registrationStatus = Registered(testBusinessPartnerId)
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("Scottish Partnership", "success", "success")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            testDefaultGeneralPartnershipJourneyConfig.copy(partnershipType = ScottishPartnership)
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
              businessVerification = Some(BusinessVerificationFail),
              registrationStatus = RegistrationNotCalled
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("Scottish Partnership", "fail", "not called")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            defaultScottishPartnershipJourneyConfig
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
              businessVerification = Some(BusinessVerificationPass),
              registrationStatus = RegistrationFailed
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("Scottish Partnership", "success", "fail")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            defaultScottishPartnershipJourneyConfig,
          ))
          mockAuditConnector.sendExplicitAudit("ScottishPartnershipEntityRegistration", expectedAuditModel) was called
        }
      }
      "there is no business verification status" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            PartnershipFullJourneyData(
              optPostcode = Some(testPostcode),
              optSautr = Some(testSautr),
              companyProfile = None,
              identifiersMatch = true,
              businessVerification = None,
              registrationStatus = RegistrationFailed
            )
          ))

          val expectedAuditModel: JsObject = expectedAuditJson("Scottish Partnership", "not requested", "fail")

          await(TestAuditService.auditPartnershipInformation(
            testJourneyId,
            defaultScottishPartnershipJourneyConfig
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
            businessVerification = Some(BusinessVerificationUnchallenged),
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
          defaultScottishPartnershipJourneyConfig.copy(pageConfig = testDefaultPageConfig.copy(optServiceName = None))
        ))
        mockAuditConnector.sendExplicitAudit("ScottishPartnershipEntityRegistration", expectedAuditModel) was called
      }
    }

    "the user is identifying a limited partnership with all information" when {

      val journeyConfig = testDefaultGeneralPartnershipJourneyConfig.copy(partnershipType = LimitedPartnership)

      val expectedAuditModel: JsObject = expectedLimitedPartnershipAuditJson(businessType = "Limited Partnership")

      "identifiersMatch is false" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            limitedPartnershipFullJourneyData
          ))

          await(TestAuditService.auditPartnershipInformation(
            journeyId = testJourneyId,
            journeyConfig = journeyConfig
          ))
          mockAuditConnector.sendExplicitAudit("LimitedPartnershipRegistration", expectedAuditModel) was called
        }
      }
      "identifiersMatch is false and service name has not provided" should {
        "audit the correct information" in new Setup {
          mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
            limitedPartnershipFullJourneyData
          ))

          mockAppConfig.defaultServiceName returns testServiceName

          await(TestAuditService.auditPartnershipInformation(
            journeyId = testJourneyId,
            journeyConfig = journeyConfig.copy(pageConfig = testDefaultPageConfig.copy(optServiceName = None))
          ))
          mockAuditConnector.sendExplicitAudit("LimitedPartnershipRegistration", expectedAuditModel) was called
        }
      }
    }
  }

  "the user is identifying a limited liability partnership with all information" when {

    val journeyConfig = testDefaultGeneralPartnershipJourneyConfig.copy(partnershipType = LimitedLiabilityPartnership)

    val expectedAuditModel: JsObject = expectedLimitedPartnershipAuditJson(businessType = "Limited Liability Partnership")

    "identifiersMatch is false" should {
      "audit the correct information" in new Setup {
        mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
          limitedPartnershipFullJourneyData
        ))

        await(TestAuditService.auditPartnershipInformation(
          journeyId = testJourneyId,
          journeyConfig = journeyConfig
        ))

        mockAuditConnector.sendExplicitAudit(
          "LimitedLiabilityPartnershipRegistration",
          expectedAuditModel
        ) was called
      }
    }

    "identifiersMatch is false and service name has not provided" should {
      "audit the correct information" in new Setup {
        mockPartnershipIdentificationService.retrievePartnershipFullJourneyData(testJourneyId) returns Future.successful(Some(
          limitedPartnershipFullJourneyData
        ))

        mockAppConfig.defaultServiceName returns testServiceName

        await(TestAuditService.auditPartnershipInformation(
          journeyId = testJourneyId,
          journeyConfig = journeyConfig.copy(pageConfig = testDefaultPageConfig.copy(optServiceName = None))
        ))

        mockAuditConnector.sendExplicitAudit(
          "LimitedLiabilityPartnershipRegistration",
          expectedAuditModel
        ) was called
      }
    }
  }

  val limitedPartnershipFullJourneyData: PartnershipFullJourneyData = PartnershipFullJourneyData(
    optPostcode = Some(testPostcode),
    optSautr = Some(testSautr),
    companyProfile = Some(testCompanyProfile),
    identifiersMatch = false,
    businessVerification = Some(BusinessVerificationUnchallenged),
    registrationStatus = RegistrationNotCalled
  )

  private def expectedAuditJson(partnershipType: String, verificationStatus: String, registerStatus: String, isMatch: Boolean = true): JsObject =
    Json.obj(
      "SAUTR" -> testSautr,
      "SApostcode" -> testPostcode,
      "isMatch" -> isMatch,
      "businessType" -> partnershipType,
      "VerificationStatus" -> verificationStatus,
      "RegisterApiStatus" -> registerStatus,
      "callingService" -> testServiceName
    )

  private def expectedLimitedPartnershipAuditJson(businessType: String): JsObject = {
    Json.obj(
      "SAUTR" -> testSautr,
      "SApostcode" -> testPostcode,
      "isMatch" -> false,
      "companyNumber" -> testCompanyProfile.companyNumber,
      "businessType" -> businessType,
      "VerificationStatus" -> "Not Enough Information to challenge",
      "RegisterApiStatus" -> "not called",
      "callingService" -> testServiceName,
    )
  }
}
