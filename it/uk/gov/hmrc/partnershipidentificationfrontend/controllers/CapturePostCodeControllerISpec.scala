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

package uk.gov.hmrc.partnershipidentificationfrontend.controllers

import play.api.libs.ws.WSResponse
import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.models.{JourneyLabels, PageConfig}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.CapturePostCodeViewTests


class CapturePostCodeControllerISpec extends ComponentSpecHelper
  with CapturePostCodeViewTests
  with PartnershipIdentificationStub
  with AuthStub {

  "GET /self-assessment-postcode" should {
    lazy val result = {
      await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      get(s"$baseUrl/$testJourneyId/self-assessment-postcode")
    }

    "return OK" in {
      result.status mustBe OK
    }

    "return a view" when {
      "there is no serviceName passed in the journeyConfig" should {
        testCapturePostCodeView(result)
      }

      "there is a serviceName passed in the journeyConfig" should {
        lazy val result = {
          val config = testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)
            .copy(pageConfig = PageConfig(Some(testCallingServiceName), testDeskProServiceId, testSignOutUrl, testAccessibilityUrl))
          await(insertJourneyConfig(testJourneyId, testInternalId, config))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(s"$baseUrl/$testJourneyId/self-assessment-postcode")
        }

        testCapturePostCodeView(result, testCallingServiceName)
      }

      "there is a serviceName passed in the journeyConfig labels object" should {
        lazy val result = {
          val config = testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)
            .copy(pageConfig = PageConfig(Some(testCallingServiceName), testDeskProServiceId, testSignOutUrl, testAccessibilityUrl, Some(JourneyLabels(Some(testWelshServiceName),None))))
          await(insertJourneyConfig(
            journeyId = testJourneyId,
            authInternalId = testInternalId,
            journeyConfig = config
          ))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(s"$baseUrl/$testJourneyId/self-assessment-postcode")
        }

        testCapturePostCodeView(result, testCallingServiceName)
      }
    }

    "redirect to sign in page" when {
      "the user is not logged in" in {
        lazy val result = {
          stubAuthFailure()
          get(s"$baseUrl/$testJourneyId/self-assessment-postcode")
        }

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(signInRedirectUrl(testJourneyId, "self-assessment-postcode"))
        }
      }
    }

    "throw an InternalServerException" when {
      "an internal id cannot be retrieved from auth" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(None))
          get(s"$baseUrl/$testJourneyId/self-assessment-postcode")
        }

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "POST /self-assessment-postcode" when {
    "a valid postcode is submitted" should {
      "store the postcode and redirect to the Check Your Answers page" in {
        lazy val result: WSResponse = {
          await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubStorePostCode(testJourneyId, testPostcode)(OK)
          post(s"$baseUrl/$testJourneyId/self-assessment-postcode")("postcode" -> testPostcode)
        }

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.CheckYourAnswersController.show(testJourneyId).url)
        }
      }
    }

    "no postcode is submitted" should {
      lazy val result: WSResponse = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        post(s"$baseUrl/$testJourneyId/self-assessment-postcode")("postcode" -> "")
      }

      "return a bad request" in {
        result.status mustBe BAD_REQUEST
      }

      testCapturePostCodeViewWithNoPostCodeErrorMessages(result)
    }

    "an invalid postcode is submitted" should {
      lazy val result: WSResponse = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        post(s"$baseUrl/$testJourneyId/self-assessment-postcode")("postcode" -> "AAA1 1AA")
      }

      "return a bad request" in {
        result.status mustBe BAD_REQUEST
      }

      testCapturePostCodeViewWithInvalidPostCodeErrorMessages(result)
    }

    "the user is not logged in" should {
      "redirect to sign in page" in {
        lazy val result = {
          stubAuthFailure()
          post(s"$baseUrl/$testJourneyId/self-assessment-postcode")("postcode" -> testPostcode)
        }

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(signInRedirectUrl(testJourneyId, "self-assessment-postcode"))
        }
      }
    }

    "an internal id cannot be retrieved from auth" should {
      "throw an InternalServerException" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(None))
          post(s"$baseUrl/$testJourneyId/self-assessment-postcode")("postcode" -> testPostcode)
        }

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

}
