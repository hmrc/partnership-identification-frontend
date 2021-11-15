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

import play.api.libs.json.Json
import play.api.test.Helpers.{INTERNAL_SERVER_ERROR, LOCATION, NOT_FOUND, OK, SEE_OTHER, await, defaultAwaitTimeout}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.featureswitch.core.config.FeatureSwitching
import uk.gov.hmrc.partnershipidentificationfrontend.models.PageConfig
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.ConfirmPartnershipNameViewTests

class ConfirmPartnershipNameControllerISpec extends ComponentSpecHelper
  with ConfirmPartnershipNameViewTests
  with PartnershipIdentificationStub
  with AuthStub
  with FeatureSwitching {

  "GET /confirm-company-name" when {
    "the company exists in Companies House" should {
      lazy val result = {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        await(insertJourneyConfig(
          journeyId = testJourneyId,
          authInternalId = testInternalId,
          journeyConfig = testLimitedPartnershipJourneyConfig
        ))
        stubRetrieveCompanyProfile(testJourneyId)(status = OK, body = Json.toJsObject(testCompanyProfile))
        get(s"$baseUrl/$testJourneyId/confirm-company-name")
      }
      "return OK" in {
        result.status mustBe OK
      }

      "return a view" when {
        "there is no serviceName passed in the journeyConfig" should {
          testConfirmPartnershipNameView(result, testCompanyName)
        }

        "there is a serviceName passed in the journeyConfig" should {
          lazy val result = {
            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
            await(insertJourneyConfig(
              journeyId = testJourneyId,
              authInternalId = testInternalId,
              journeyConfig = testLimitedPartnershipJourneyConfig.copy(
                pageConfig = PageConfig(Some(testCallingServiceName), testDeskProServiceId, testSignOutUrl))
            ))
            stubRetrieveCompanyProfile(testJourneyId)(status = OK, body = Json.toJsObject(testCompanyProfile))
            get(s"$baseUrl/$testJourneyId/confirm-company-name")
          }

          testConfirmPartnershipNameView(result, testCompanyName)
        }
      }
    }

    "the company doesn't exist in the backend database" should {
      "throw an Internal Server Exception" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          await(insertJourneyConfig(
            journeyId = testJourneyId,
            authInternalId = testInternalId,
            journeyConfig = testLimitedPartnershipJourneyConfig
          ))
          stubRetrieveCompanyProfile(testJourneyId)(status = NOT_FOUND)
          get(s"$baseUrl/$testJourneyId/confirm-company-name")
        }

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

    "the user is not logged in" should {
      "redirect to sign in page" in {
        lazy val result = {
          stubAuthFailure()
          get(s"$baseUrl/$testJourneyId/confirm-company-name")
        }

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe
          Some(s"/bas-gateway/sign-in?continue_url=%2Fidentify-your-partnership%2F$testJourneyId%2Fconfirm-company-name&origin=partnership-identification-frontend")
      }
    }

    "the journeyId does not match what is stored in the journey config database" should {
      "return NOT_FOUND" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          await(insertJourneyConfig(
            journeyId = testJourneyId + "1",
            authInternalId = testInternalId,
            journeyConfig = testLimitedPartnershipJourneyConfig
          ))
          stubRetrieveCompanyProfile(testJourneyId)(status = NOT_FOUND)
          get(s"$baseUrl/$testJourneyId/confirm-company-name")
        }

        result.status mustBe NOT_FOUND
      }

      "the auth internal ID does not match what is stored in the journey config database" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          await(insertJourneyConfig(
            journeyId = testJourneyId,
            authInternalId = testInternalId + "1",
            journeyConfig = testLimitedPartnershipJourneyConfig
          ))
          stubRetrieveCompanyProfile(testJourneyId)(status = NOT_FOUND)
          get(s"$baseUrl/$testJourneyId/confirm-company-name")
        }

        result.status mustBe NOT_FOUND
      }

      "neither the journey ID or auth internal ID are found in the journey config database" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          await(insertJourneyConfig(
            journeyId = testJourneyId + "1",
            authInternalId = testInternalId + "1",
            journeyConfig = testLimitedPartnershipJourneyConfig
          ))
          stubRetrieveCompanyProfile(testJourneyId)(status = NOT_FOUND)
          get(s"$baseUrl/$testJourneyId/confirm-company-name")
        }

        result.status mustBe NOT_FOUND
      }
    }

    "throw an Internal Server Exception" when {
      "the user does not have an internal ID" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(None))
          get(s"$baseUrl/$testJourneyId/confirm-company-name")
        }

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

  }

  "POST /confirm-company-name" should {
    "redirect to Capture UTR Page" in {
      lazy val result = {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        await(insertJourneyConfig(
          journeyId = testJourneyId,
          authInternalId = testInternalId,
          journeyConfig = testLimitedPartnershipJourneyConfig
        ))
        post(s"$baseUrl/$testJourneyId/confirm-company-name")()
      }

      result must have(
        httpStatus(SEE_OTHER),
        redirectUri(routes.CaptureSautrController.show(testJourneyId).url)
      )
    }
    "throw an internal server exception" when {
      "the user does not have an internal ID" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(None))
          post(s"$baseUrl/$testJourneyId/confirm-company-name")()
        }

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

}
