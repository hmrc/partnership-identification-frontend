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

import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.CaptureSautrViewTests

import scala.concurrent.ExecutionContext.Implicits.global


class CaptureSautrControllerISpec extends ComponentSpecHelper
  with CaptureSautrViewTests
  with PartnershipIdentificationStub
  with AuthStub {

  override def afterEach(): Unit = {
    super.afterEach()
    journeyConfigRepository.drop
  }

  "GET /sa-utr" should {
    "return OK" in {
      await(insertJourneyConfig(
        journeyId = testJourneyId,
        continueUrl = testContinueUrl,
        optServiceName = None,
        deskProServiceId = testDeskProServiceId,
        signOutUrl = testSignOutUrl
      ))
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      lazy val result = get(s"$baseUrl/$testJourneyId/sa-utr")

      result.status mustBe OK
    }

    "return a view" when {
      "there is no serviceName passed in the journeyConfig" should {
        lazy val insertConfig = insertJourneyConfig(
          journeyId = testJourneyId,
          continueUrl = testContinueUrl,
          optServiceName = None,
          deskProServiceId = testDeskProServiceId,
          signOutUrl = testSignOutUrl
        )
        lazy val authStub = stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        lazy val result = get(s"$baseUrl/$testJourneyId/sa-utr")

        testCaptureSautrView(result, authStub, insertConfig)
        testServiceName(testDefaultServiceName, result, authStub, insertConfig)
      }

      "there is a serviceName passed in the journeyConfig" should {
        lazy val insertConfig = insertJourneyConfig(
          journeyId = testJourneyId,
          continueUrl = testContinueUrl,
          optServiceName = Some(testCallingServiceName),
          deskProServiceId = testDeskProServiceId,
          signOutUrl = testSignOutUrl
        )
        lazy val authStub = stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        lazy val result = get(s"$baseUrl/$testJourneyId/sa-utr")

        testCaptureSautrView(result, authStub, insertConfig)
        testServiceName(testCallingServiceName, result, authStub, insertConfig)
      }
    }

    "redirect to sign in page" when {
      "the user is UNAUTHORISED" in {
        await(insertJourneyConfig(
          journeyId = testJourneyId,
          continueUrl = testContinueUrl,
          optServiceName = None,
          deskProServiceId = testDeskProServiceId,
          signOutUrl = testSignOutUrl
        ))
        stubAuthFailure()
        lazy val result = get(s"$baseUrl/$testJourneyId/sa-utr")

        result.status mustBe SEE_OTHER
      }
    }
  }

  "POST /sa-utr" when {
    "a valid sautr is submitted" should {
      "store sautr" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubStoreSautr(testJourneyId, testSautr)(status = OK)

        val result = post(s"$baseUrl/$testJourneyId/sa-utr")("sa-utr" -> testSautr)

        result.status mustBe NOT_IMPLEMENTED
      }
    }

    "no sautr is submitted" should {
      "return a bad request" in {
        lazy val result = {
          await(insertJourneyConfig(
            journeyId = testJourneyId,
            continueUrl = testContinueUrl,
            optServiceName = None,
            deskProServiceId = testDeskProServiceId,
            signOutUrl = testSignOutUrl
          ))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          post(s"$baseUrl/$testJourneyId/sa-utr")("sa-utr" -> "")
        }
        result.status mustBe BAD_REQUEST
      }
      lazy val result = {
        await(insertJourneyConfig(
          journeyId = testJourneyId,
          continueUrl = testContinueUrl,
          optServiceName = None,
          deskProServiceId = testDeskProServiceId,
          signOutUrl = testSignOutUrl
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        post(s"$baseUrl/$testJourneyId/sa-utr")("sa-utr" -> "")
      }
      testCaptureSautrErrorMessages(result)
    }

    "an invalid sautr is submitted" should {
      "return a bad request" in {
        lazy val result = {
          await(insertJourneyConfig(
            journeyId = testJourneyId,
            continueUrl = testContinueUrl,
            optServiceName = None,
            deskProServiceId = testDeskProServiceId,
            signOutUrl = testSignOutUrl
          ))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          post(s"$baseUrl/$testJourneyId/sa-utr")("sa-utr" -> "123456789")
        }

        result.status mustBe BAD_REQUEST
      }
      lazy val result = {
        await(insertJourneyConfig(
          journeyId = testJourneyId,
          continueUrl = testContinueUrl,
          optServiceName = None,
          deskProServiceId = testDeskProServiceId,
          signOutUrl = testSignOutUrl
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        post(s"$baseUrl/$testJourneyId/sa-utr")("sa-utr" -> "123456789")
      }
      testCaptureSautrErrorMessages(result)
    }
  }

}
