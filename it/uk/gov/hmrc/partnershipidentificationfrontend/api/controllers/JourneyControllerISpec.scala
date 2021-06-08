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

import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants.{testDeskProServiceId, testInternalId, testJourneyId, testSignOutUrl}
import uk.gov.hmrc.partnershipidentificationfrontend.models.{JourneyConfig, PageConfig}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, JourneyStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.{routes => appRoutes}

import scala.concurrent.ExecutionContext.Implicits.global


class JourneyControllerISpec extends ComponentSpecHelper with JourneyStub with AuthStub with PartnershipIdentificationStub {

  "POST /api/journey" should {
    "return a created journey" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      val testJourneyConfig = JourneyConfig(
        continueUrl = "/testContinueUrl",
        pageConfig = PageConfig(
          optServiceName = None,
          deskProServiceId = testDeskProServiceId,
          signOutUrl = testSignOutUrl
        )
      )

      lazy val result = post("/partnership-identification/api/journey", Json.toJson(testJourneyConfig))

      (result.json \ "journeyStartUrl").as[String] must include(appRoutes.CaptureSautrController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testJourneyConfig)
    }

    "return See Other" in {
      stubAuthFailure()
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      val testJourneyConfig = JourneyConfig(
        continueUrl = "/testContinueUrl",
        pageConfig = PageConfig(
          optServiceName = None,
          deskProServiceId = testDeskProServiceId,
          signOutUrl = testSignOutUrl
        )
      )

      lazy val result = post("/partnership-identification/api/journey", Json.toJson(testJourneyConfig))

      result.status mustBe SEE_OTHER
    }
  }

}