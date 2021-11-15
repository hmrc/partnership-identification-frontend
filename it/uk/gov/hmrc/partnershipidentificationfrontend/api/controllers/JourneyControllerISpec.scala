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

package uk.gov.hmrc.partnershipidentificationfrontend.api.controllers

import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.{routes => appRoutes}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, JourneyStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

import scala.concurrent.ExecutionContext.Implicits.global

class JourneyControllerISpec extends ComponentSpecHelper with JourneyStub with AuthStub with PartnershipIdentificationStub {

  val testJourneyConfigJson: JsObject = Json.obj(
    "continueUrl" -> testContinueUrl,
    "deskProServiceId" -> testDeskProServiceId,
    "signOutUrl" -> testSignOutUrl
  )

  "POST /api/general-partnership-journey" should {
    "return a created journey" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/general-partnership-journey", testJourneyConfigJson)

      (result.json \ "journeyStartUrl").as[String] must include(appRoutes.CaptureSautrController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testGeneralPartnershipJourneyConfig)
    }

    "redirect to the Sign In page when the user is not logged in" in {
      stubAuthFailure()
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/general-partnership-journey", testJourneyConfigJson)

      result.status mustBe SEE_OTHER
      result.header(LOCATION) mustBe
        Some(s"/bas-gateway/sign-in?continue_url=%2Fpartnership-identification%2Fapi%2Fgeneral-partnership-journey&origin=partnership-identification-frontend")
    }
  }

  "POST /api/scottish-partnership-journey" should {
    "return a created journey" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/scottish-partnership-journey", testJourneyConfigJson)

      (result.json \ "journeyStartUrl").as[String] must include(appRoutes.CaptureSautrController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testScottishPartnershipJourneyConfig)
    }

    "redirect to the Sign In page when the user is not logged in" in {
      stubAuthFailure()
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/scottish-partnership-journey", testJourneyConfigJson)

      result.status mustBe SEE_OTHER
      result.header(LOCATION) mustBe
        Some(s"/bas-gateway/sign-in?continue_url=%2Fpartnership-identification%2Fapi%2Fscottish-partnership-journey&origin=partnership-identification-frontend")
    }
  }

  "POST /api/scottish-limited-partnership-journey" should {
    "return a created journey" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/scottish-limited-partnership-journey", testJourneyConfigJson)

      (result.json \ "journeyStartUrl").as[String] must include(appRoutes.CaptureCompanyNumberController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testScottishLimitedPartnershipJourneyConfig)

      verifyPost(1, "/partnership-identification/journey")
    }

    "redirect to the Sign In page when the user is not logged in" in {
      stubAuthFailure()
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/scottish-limited-partnership-journey", testJourneyConfigJson)

      result.status mustBe SEE_OTHER

      result.header(LOCATION) mustBe
        Some(s"/bas-gateway/sign-in?continue_url=%2Fpartnership-identification%2Fapi%2Fscottish-limited-partnership-journey&origin=partnership-identification-frontend")
    }
  }

  "POST /api/limited-partnership-journey" should {
    "return a created journey" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/limited-partnership-journey", testJourneyConfigJson)

      (result.json \ "journeyStartUrl").as[String] must include(appRoutes.CaptureCompanyNumberController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testLimitedPartnershipJourneyConfig)
    }

    "redirect to the Sign In page when the user is not logged in" in {
      stubAuthFailure()
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/limited-partnership-journey", testJourneyConfigJson)

      result.status mustBe SEE_OTHER
      result.header(LOCATION) mustBe
        Some(s"/bas-gateway/sign-in?continue_url=%2Fpartnership-identification%2Fapi%2Flimited-partnership-journey&origin=partnership-identification-frontend")
    }
  }

  "POST /api/limited-liability-partnership-journey" should {
    "return a created journey" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/limited-liability-partnership-journey", testJourneyConfigJson)

      (result.json \ "journeyStartUrl" ).as[String] must include(appRoutes.CaptureCompanyNumberController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testLimitedLiabilityPartnershipJourneyConfig)

      verifyPost(1, "/partnership-identification/journey")
    }

    "return SEE_OTHER when authentication fails" in {
      stubAuthFailure()
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/limited-liability-partnership-journey", testJourneyConfigJson)

      result.status mustBe SEE_OTHER

      verifyPost(0, "/partnership-identification/journey")
    }

    "return INTERNAL_SERVER_ERROR when journey connector raises an internal server error" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubFailedCreateJourney(INTERNAL_SERVER_ERROR)

      lazy val result = post("/partnership-identification/api/limited-liability-partnership-journey", testJourneyConfigJson)

      result.status mustBe INTERNAL_SERVER_ERROR
    }

    "return INTERNAL_SERVER_ERROR when invalid json is returned" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj())

      lazy val result = post("/partnership-identification/api/limited-liability-partnership-journey", testJourneyConfigJson)

      result.status mustBe INTERNAL_SERVER_ERROR
    }

  }

  "GET /api/journey/:journeyId" should {
    "return captured data" when {
      "the no Company Profile exists" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(
          status = OK,
          body = testPartnershipFullJourneyDataJson
        )

        lazy val result = get(s"/partnership-identification/api/journey/$testJourneyId")

        result.status mustBe OK
        result.json mustBe Json.toJsObject(testPartnershipFullJourneyData)
      }
      "the Company Profile exists" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(
          status = OK,
          body = testPartnershipFullJourneyDataJsonWithCompanyProfile
        )

        lazy val result = get(s"/partnership-identification/api/journey/$testJourneyId")

        result.status mustBe OK
        result.json mustBe Json.toJsObject(testPartnershipFullJourneyDataWithCompanyProfile(Some(testCompanyProfile), identifiersMatch = false))
      }
    }

    "return not found" when {
      "the journey Id does not exist" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(status = NOT_FOUND)

        lazy val result = get(s"/partnership-identification/api/journey/$testJourneyId")

        result.status mustBe NOT_FOUND
      }
    }

    "redirect to Sign In Page" when {
      "the user is not signed in" in {
        stubAuthFailure()

        lazy val result = get(s"/partnership-identification/api/journey/$testJourneyId")

        result must have(
          httpStatus(SEE_OTHER),
          redirectUri("/bas-gateway/sign-in" +
            s"?continue_url=%2Fpartnership-identification%2Fapi%2Fjourney%2F$testJourneyId" +
            "&origin=partnership-identification-frontend"
          )
        )
      }
    }
  }

}