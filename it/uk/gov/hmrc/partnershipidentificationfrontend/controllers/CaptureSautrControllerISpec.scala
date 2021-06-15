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

package uk.gov.hmrc.partnershipidentificationfrontend.controllers

import play.api.libs.ws.WSResponse
import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.models.PageConfig
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.CaptureSautrViewTests


class CaptureSautrControllerISpec extends ComponentSpecHelper
  with CaptureSautrViewTests
  with PartnershipIdentificationStub
  with AuthStub {

  "GET /sa-utr" should {
    lazy val result = {
      await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      get(s"$baseUrl/$testJourneyId/sa-utr")
    }

    "return OK" in {
      result.status mustBe OK
    }

    "return a view" when {
      "there is no serviceName passed in the journeyConfig" should {
        testCaptureSautrView(result)
      }

      "there is a serviceName passed in the journeyConfig" should {
        lazy val result = {
          val config = testJourneyConfig.copy(pageConfig = PageConfig(Some(testCallingServiceName), testDeskProServiceId, testSignOutUrl))
          await(insertJourneyConfig(testJourneyId, testInternalId, config))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(s"$baseUrl/$testJourneyId/sa-utr")
        }

        testCaptureSautrView(result, testCallingServiceName)
      }
    }

    "redirect to sign in page" when {
      "the user is not logged in" in {
        lazy val result = {
          stubAuthFailure()
          get(s"$baseUrl/$testJourneyId/sa-utr")
        }

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(s"/bas-gateway/sign-in?continue_url=%2Fidentify-your-partnership%2F$testJourneyId%2Fsa-utr&origin=partnership-identification-frontend")
        }
      }
    }

    "throw an InternalServerException" when {
      "an internal id cannot be retrieved from auth" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(None))
          get(s"$baseUrl/$testJourneyId/sa-utr")
        }

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "POST /sa-utr" when {
    "a valid sautr is submitted" should {
      "store the sautr and redirect to Capture PostCode page" in {
        lazy val result: WSResponse = {
          await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubStoreSautr(testJourneyId, testSautr)(OK)
          post(s"$baseUrl/$testJourneyId/sa-utr")("sa-utr" -> testSautr)
        }

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.CapturePostCodeController.show(testJourneyId).url)
        }
      }
    }

    "no sautr is submitted" should {
      lazy val result: WSResponse = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        post(s"$baseUrl/$testJourneyId/sa-utr")("sa-utr" -> "")
      }

      "return a bad request" in {
        result.status mustBe BAD_REQUEST
      }

      testCaptureSautrViewWithErrorMessages(result)
    }

    "an invalid sautr is submitted" should {
      lazy val result: WSResponse = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        post(s"$baseUrl/$testJourneyId/sa-utr")("sa-utr" -> "123456789")
      }

      "return a bad request" in {
        result.status mustBe BAD_REQUEST
      }

      testCaptureSautrViewWithErrorMessages(result)
    }
  }

}
