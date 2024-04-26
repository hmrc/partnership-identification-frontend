/*
 * Copyright 2024 HM Revenue & Customs
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

package test.uk.gov.hmrc.partnershipidentificationfrontend.controllers

import play.api.test.Helpers.{BAD_REQUEST, INTERNAL_SERVER_ERROR, LOCATION, NOT_FOUND, OK, SEE_OTHER, await, defaultAwaitTimeout}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.featureswitch.core.config.{CompaniesHouseStub, FeatureSwitching}
import uk.gov.hmrc.partnershipidentificationfrontend.models.{JourneyLabels, PageConfig}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, CompaniesHouseApiStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.CaptureCompanyNumberViewTests

class CaptureCompanyNumberControllerISpec extends ComponentSpecHelper
  with CaptureCompanyNumberViewTests
  with CompaniesHouseApiStub
  with PartnershipIdentificationStub
  with FeatureSwitching
  with AuthStub {

  private val companyNumberKey: String = "companyNumber"

  "GET /company-registration-number" should {
    lazy val result = {
      await(insertJourneyConfig(
        journeyId = testJourneyId,
        authInternalId = testInternalId,
        journeyConfig = testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
      ))
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      get(s"$baseUrl/$testJourneyId/company-registration-number")
    }

    "return OK" in {
      result.status mustBe OK
    }

    "return a view" when {
      "there is no serviceName passed in the journeyConfig" should {
        testCaptureCompanyNumberView(result)
      }

      "there is a serviceName passed in the journeyConfig" should {
        lazy val result = {
          val config = testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
            .copy(pageConfig = PageConfig(Some(testCallingServiceName), testDeskProServiceId, testSignOutUrl, testAccessibilityUrl))
          await(insertJourneyConfig(
            journeyId = testJourneyId,
            authInternalId = testInternalId,
            journeyConfig = config
          ))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(s"$baseUrl/$testJourneyId/company-registration-number")
        }

        testCaptureCompanyNumberView(result, testCallingServiceName)
      }

      "there is a serviceName passed in the journeyConfig labels object" should {
        lazy val result = {
          val config = testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
            .copy(pageConfig = PageConfig(Some(testCallingServiceName), testDeskProServiceId, testSignOutUrl, testAccessibilityUrl, Some(JourneyLabels(Some(testWelshServiceName),None))))
          await(insertJourneyConfig(
            journeyId = testJourneyId,
            authInternalId = testInternalId,
            journeyConfig = config
          ))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(s"$baseUrl/$testJourneyId/company-registration-number")
        }

        testCaptureCompanyNumberView(result, testCallingServiceName)
      }


    }

    "redirect to sign in page" when {
      "the user is not logged in" in {
        lazy val result = {
          stubAuthFailure()
          get(s"$baseUrl/$testJourneyId/company-registration-number")
        }

        result must have(
          httpStatus(SEE_OTHER),
          redirectUri(s"/bas-gateway/sign-in?continue_url=%2Fidentify-your-partnership%2F$testJourneyId%2Fcompany-registration-number&origin=partnership-identification-frontend")
        )
      }
    }

    "return NOT_FOUND" when {
      "the journeyId does not match what is stored in the journey config database" in {
        lazy val result = {
          await(insertJourneyConfig(
            journeyId = testJourneyId + "1",
            authInternalId = testInternalId,
            journeyConfig = testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
          ))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(s"$baseUrl/$testJourneyId/company-registration-number")
        }

        result.status mustBe NOT_FOUND
      }

      "the auth internal ID does not match what is stored in the journey config database" in {
        lazy val result = {
          await(insertJourneyConfig(
            journeyId = testJourneyId,
            authInternalId = testInternalId + "1",
            journeyConfig = testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
          ))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(s"$baseUrl/$testJourneyId/company-registration-number")
        }

        result.status mustBe NOT_FOUND
      }

      "neither the journey ID or auth internal ID are found in the journey config database" in {
        lazy val result = {
          await(insertJourneyConfig(
            journeyId = testJourneyId + "1",
            authInternalId = testInternalId + "1",
            journeyConfig = testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
          ))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(s"$baseUrl/$testJourneyId/company-registration-number")
        }

        result.status mustBe NOT_FOUND
      }
    }

    "redirect to the sign in page" when {
      "the user is not logged in" in {
        lazy val result = {
          stubAuthFailure()
          get(s"$baseUrl/$testJourneyId/company-registration-number")
        }

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe Some(signInRedirectUrl(testJourneyId, "company-registration-number"))
      }
    }

    "throw an InternalServerException" when {
      "an internal id cannot be retrieved from auth" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(None))
          get(s"$baseUrl/$testJourneyId/company-registration-number")
        }

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

  }

  "POST /company-registration-number" when {

    "the company number is missing" should {
      lazy val result = {
        await(insertJourneyConfig(
          journeyId = testJourneyId,
          authInternalId = testInternalId,
          journeyConfig = testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        post(s"$baseUrl/$testJourneyId/company-registration-number")(companyNumberKey -> "")
      }

      "return a bad request" in {
        result.status mustBe BAD_REQUEST
      }

      "return a view" when {
        testCaptureCompanyNumberEmpty(result)
      }
    }

    "the company number has more than 8 characters" should {
      lazy val result = {
        await(insertJourneyConfig(
          journeyId = testJourneyId,
          authInternalId = testInternalId,
          journeyConfig = testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        post(s"$baseUrl/$testJourneyId/company-registration-number")(companyNumberKey -> "0123456789")
      }

      "return a bad request" in {
        result.status mustBe BAD_REQUEST
      }

      "return a view" when {
        testCaptureCompanyNumberWrongLength(result)
      }
    }

    "company number is not in the correct format" should {
      lazy val result = {
        await(insertJourneyConfig(
          journeyId = testJourneyId,
          authInternalId = testInternalId,
          journeyConfig = testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        post(s"$baseUrl/$testJourneyId/company-registration-number")(companyNumberKey -> "13E!!!%")
      }
      "return a bad request" in {
        result.status mustBe BAD_REQUEST
      }

      "return a view" when {
        testCaptureCompanyNumberWrongFormat(result)
      }
    }

    "the feature switch is enabled" should {
      "retrieve and store companies house profile and redirect to the Confirm Business Name page" when {
        "the company number is correct" in {
          enable(CompaniesHouseStub)
          lazy val result = {
            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
            stubRetrieveCompanyProfileFromStub(testCompanyNumber)(
              status = OK,
              body = companyProfileJson(testCompanyNumber, testCompanyName, testDateOfIncorporation, testAddress)
            )
            stubStoreCompanyProfile(testJourneyId, testCompanyProfile)(status = OK)
            post(s"$baseUrl/$testJourneyId/company-registration-number")(companyNumberKey -> testCompanyNumber)
          }

          result must have(
            httpStatus(SEE_OTHER),
            redirectUri(routes.ConfirmPartnershipNameController.show(testJourneyId).url)
          )
        }
      }
      "redirect to the Company Number not found error page" when {
        "the company number is not found" in {
          enable(CompaniesHouseStub)
          lazy val result = {
            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
            stubRetrieveCompanyProfileFromStub(testCompanyNumber)(status = NOT_FOUND)
            post(s"$baseUrl/$testJourneyId/company-registration-number")(companyNumberKey -> testCompanyNumber)
          }

          result.status mustBe SEE_OTHER
        }
      }
    }

    "the feature switch is disabled" should {
      "retrieve and store companies house profile and redirect to the Confirm Business Name page" when {
        "the company number is correct" in {
          disable(CompaniesHouseStub)
          lazy val result = {
            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
            stubRetrieveCompanyProfileFromCoHo(testCompanyNumber)(
              status = OK,
              body = companyProfileJson(testCompanyNumber, testCompanyName, testDateOfIncorporation, testAddress)
            )
            stubStoreCompanyProfile(testJourneyId, testCompanyProfile)(status = OK)
            post(s"$baseUrl/$testJourneyId/company-registration-number")(companyNumberKey -> testCompanyNumber)
          }

          result must have(
            httpStatus(SEE_OTHER),
            redirectUri(routes.ConfirmPartnershipNameController.show(testJourneyId).url)
          )
        }
      }
      "redirect to the Company Number not found error page" when {
        "the company number is not found" in {
          disable(CompaniesHouseStub)
          lazy val result = {
            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
            stubRetrieveCompanyProfileFromCoHo(testCompanyNumber)(status = NOT_FOUND)
            post(s"$baseUrl/$testJourneyId/company-registration-number")(companyNumberKey -> testCompanyNumber)
          }

          result.status mustBe SEE_OTHER
        }
      }
    }

    "the user does not have an internal ID" should {
      "throw an Internal Server Exception" in {
        stubAuth(OK, successfulAuthResponse(None))

        lazy val result = post(s"$baseUrl/$testJourneyId/company-registration-number")(companyNumberKey -> testCompanyNumber)

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

    "the user is not logged in" should {
      "redirect to the sign in page" in {
        lazy val result = {
          stubAuthFailure()
          post(s"$baseUrl/$testJourneyId/company-registration-number")(companyNumberKey -> testCompanyNumber)
        }

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe Some(signInRedirectUrl(testJourneyId, "company-registration-number"))
      }
    }
  }
}
