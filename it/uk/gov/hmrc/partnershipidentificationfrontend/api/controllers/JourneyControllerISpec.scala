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
    "continueUrl" -> testJourneyConfig.continueUrl,
    "deskProServiceId" -> testJourneyConfig.pageConfig.deskProServiceId,
    "signOutUrl" -> testJourneyConfig.pageConfig.signOutUrl
  )

  "POST /api/general-partnership-journey" should {
    "return a created journey" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/general-partnership-journey", testJourneyConfigJson)

      (result.json \ "journeyStartUrl").as[String] must include(appRoutes.CaptureSautrController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testJourneyConfig)
    }

    "return See Other" in {
      stubAuthFailure()
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/general-partnership-journey", testJourneyConfigJson)

      result.status mustBe SEE_OTHER
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

    "return See Other" in {
      stubAuthFailure()
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/scottish-partnership-journey", testJourneyConfigJson)

      result.status mustBe SEE_OTHER
    }
  }

  "POST /api/scottish-limited-partnership-journey" should {
    "return a created journey" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/scottish-limited-partnership-journey", testJourneyConfigJson)

      (result.json \ "journeyStartUrl").as[String] must include(appRoutes.CaptureSautrController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testScottishLimitedPartnershipJourneyConfig)

      verifyPost(1, "/partnership-identification/journey")
    }

    "return See Other when authentication fails" in {
      stubAuthFailure()
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/scottish-limited-partnership-journey", testJourneyConfigJson)

      result.status mustBe SEE_OTHER

      verifyPost(0, "/partnership-identification/journey")
    }
  }

  "GET /api/journey/:journeyId" should {
    "return captured data" when {
      "the journeyId exists" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(
          status = OK,
          body = testPartnershipFullJourneyDataJson
        )

        lazy val result = get(s"/partnership-identification/api/journey/$testJourneyId")

        result.status mustBe OK
        result.json mustBe Json.toJsObject(testPartnershipFullJourneyData)
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