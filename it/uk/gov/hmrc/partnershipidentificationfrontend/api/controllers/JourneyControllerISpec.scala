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

package uk.gov.hmrc.partnershipidentificationfrontend.api.controllers

import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.{routes => appRoutes}
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType._
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, JourneyStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

import scala.concurrent.ExecutionContext.Implicits.global

class JourneyControllerISpec extends ComponentSpecHelper with JourneyStub with AuthStub with PartnershipIdentificationStub {

  val testJourneyConfigJson: JsObject = Json.obj(
    JourneyController.continueUrlKey -> testContinueUrl,
    JourneyController.deskProServiceIdKey -> testDeskProServiceId,
    JourneyController.signOutUrlKey -> testSignOutUrl,
    JourneyController.accessibilityUrlKey -> testAccessibilityUrl,
    JourneyController.regimeKey -> testRegime
  )

  val expectedBusinessVerificationCheckJsonKey = "businessVerificationCheck"

  "businessVerificationCheck field in the incoming json: POST to /api/<url suffix PartnershipType specific>" should {
    "return (for all PartnershipType) a created journey with businessVerificationCheck true" when {
      "the incoming json DOES NOT have a businessVerificationCheck field" in {
        def assertTheCreatedJourneyConfigHasBVCTrueFor(postToApiUrlSuffix: String,
                                                       expectedJourneyConfigPartnershipType: PartnershipType,
                                                       expectedJourneyStartUrl: Call): Unit = {
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

          lazy val result = post(s"/partnership-identification/api/$postToApiUrlSuffix", testJourneyConfigJson - expectedBusinessVerificationCheckJsonKey)

          (result.json \ "journeyStartUrl").as[String] must include(expectedJourneyStartUrl.url)

          await(journeyConfigRepository.findById(testJourneyId)) mustBe
            Some(testJourneyConfig(expectedJourneyConfigPartnershipType, businessVerificationCheck = true, regime = testRegime))
          await(journeyConfigRepository.drop)
        }

        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "general-partnership-journey",
          expectedJourneyConfigPartnershipType = GeneralPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureSautrController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "scottish-partnership-journey",
          expectedJourneyConfigPartnershipType = ScottishPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureSautrController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "scottish-limited-partnership-journey",
          expectedJourneyConfigPartnershipType = ScottishLimitedPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "limited-partnership-journey",
          expectedJourneyConfigPartnershipType = LimitedPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "limited-liability-partnership-journey",
          expectedJourneyConfigPartnershipType = LimitedLiabilityPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )
      }
    }
    "return (for all PartnershipType) a created journey with businessVerificationCheck false" when {
      "the incoming json has a businessVerificationCheck field set to false" in {
        def assertTheCreatedJourneyConfigHasBVCFalseFor(postToApiUrlSuffix: String,
                                                        expectedJourneyConfigPartnershipType: PartnershipType,
                                                        expectedJourneyStartUrl: Call): Unit = {
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

          val incomingJson = testJourneyConfigJson ++ Json.obj(expectedBusinessVerificationCheckJsonKey -> false)

          lazy val result = post(s"/partnership-identification/api/$postToApiUrlSuffix", incomingJson)

          (result.json \ "journeyStartUrl").as[String] must include(expectedJourneyStartUrl.url)

          val expectedJourneyConfig = testJourneyConfig(expectedJourneyConfigPartnershipType, businessVerificationCheck = true, regime = testRegime)
            .copy(businessVerificationCheck = false)

          await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(expectedJourneyConfig)
          await(journeyConfigRepository.drop)

        }

        assertTheCreatedJourneyConfigHasBVCFalseFor(
          postToApiUrlSuffix = "general-partnership-journey",
          expectedJourneyConfigPartnershipType = GeneralPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureSautrController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCFalseFor(
          postToApiUrlSuffix = "scottish-partnership-journey",
          expectedJourneyConfigPartnershipType = ScottishPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureSautrController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCFalseFor(
          postToApiUrlSuffix = "scottish-limited-partnership-journey",
          expectedJourneyConfigPartnershipType = ScottishLimitedPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCFalseFor(
          postToApiUrlSuffix = "limited-partnership-journey",
          expectedJourneyConfigPartnershipType = LimitedPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCFalseFor(
          postToApiUrlSuffix = "limited-liability-partnership-journey",
          expectedJourneyConfigPartnershipType = LimitedLiabilityPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )

      }
    }
    "return (for all PartnershipType) a created journey with businessVerificationCheck true" when {
      "the incoming json has a businessVerificationCheck field set to true" in {
        def assertTheCreatedJourneyConfigHasBVCTrueFor(postToApiUrlSuffix: String,
                                                       expectedJourneyConfigPartnershipType: PartnershipType,
                                                       expectedJourneyStartUrl: Call): Unit = {
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

          val incomingJson = testJourneyConfigJson ++ Json.obj(expectedBusinessVerificationCheckJsonKey -> true)

          lazy val result = post(s"/partnership-identification/api/$postToApiUrlSuffix", incomingJson)

          (result.json \ "journeyStartUrl").as[String] must include(expectedJourneyStartUrl.url)

          await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testJourneyConfig(expectedJourneyConfigPartnershipType, businessVerificationCheck = true, regime = testRegime))
          await(journeyConfigRepository.drop)
        }

        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "general-partnership-journey",
          expectedJourneyConfigPartnershipType = GeneralPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureSautrController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "scottish-partnership-journey",
          expectedJourneyConfigPartnershipType = ScottishPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureSautrController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "scottish-limited-partnership-journey",
          expectedJourneyConfigPartnershipType = ScottishLimitedPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "limited-partnership-journey",
          expectedJourneyConfigPartnershipType = LimitedPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )
        assertTheCreatedJourneyConfigHasBVCTrueFor(
          postToApiUrlSuffix = "limited-liability-partnership-journey",
          expectedJourneyConfigPartnershipType = LimitedLiabilityPartnership,
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )
      }
    }

    "allow journey creation for all partnership types" when {
      "the incoming journey configuration contains a continue url with the host set to localhost" in {

        def assertJourneyConfigWithAllowedHostIsAccepted(postToApiUrlSuffix: String,
                                                         expectedJourneyStartUrl: Call): Unit = {

          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

          val incomingJson = testJourneyConfigJson ++
            Json.obj((JourneyController.continueUrlKey -> "http://localhost:9000/continue"))

          lazy val result = post(s"/partnership-identification/api/$postToApiUrlSuffix", incomingJson)

          (result.json \ "journeyStartUrl").as[String] must include(expectedJourneyStartUrl.url)

          await(journeyConfigRepository.drop)
        }

        assertJourneyConfigWithAllowedHostIsAccepted(
          postToApiUrlSuffix = "general-partnership-journey",
          expectedJourneyStartUrl = appRoutes.CaptureSautrController.show(testJourneyId)
        )

        assertJourneyConfigWithAllowedHostIsAccepted(
          postToApiUrlSuffix = "scottish-partnership-journey",
          expectedJourneyStartUrl = appRoutes.CaptureSautrController.show(testJourneyId)
        )

        assertJourneyConfigWithAllowedHostIsAccepted(
          postToApiUrlSuffix = "scottish-limited-partnership-journey",
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )

        assertJourneyConfigWithAllowedHostIsAccepted(
          postToApiUrlSuffix = "limited-partnership-journey",
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )

        assertJourneyConfigWithAllowedHostIsAccepted(
          postToApiUrlSuffix = "limited-liability-partnership-journey",
          expectedJourneyStartUrl = appRoutes.CaptureCompanyNumberController.show(testJourneyId)
        )

      }
    }

    "return a bad request for all partnership types" when {
      "the incoming journey configuration contains a disallowed host in the continue url field" in {

        def assertIllegalJourneyConfigIsRejected(postToApiUrlSuffix: String): Unit = {

          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

          val incomingJson = testJourneyConfigJson ++
            Json.obj(JourneyController.continueUrlKey -> "http://somehost:9000")

          lazy val result = post(s"/partnership-identification/api/$postToApiUrlSuffix", incomingJson)

          result.status mustBe BAD_REQUEST

          result.json mustBe Json.toJson("JourneyConfig contained non-relative urls")
        }

        assertIllegalJourneyConfigIsRejected(postToApiUrlSuffix = "general-partnership-journey")
        assertIllegalJourneyConfigIsRejected(postToApiUrlSuffix = "scottish-partnership-journey")
        assertIllegalJourneyConfigIsRejected(postToApiUrlSuffix = "scottish-limited-partnership-journey")
        assertIllegalJourneyConfigIsRejected(postToApiUrlSuffix = "limited-partnership-journey")
        assertIllegalJourneyConfigIsRejected(postToApiUrlSuffix = "limited-liability-partnership-journey")
      }
    }
  }

  "POST /api/general-partnership-journey" should {
    "return a created journey" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      lazy val result = post("/partnership-identification/api/general-partnership-journey", testJourneyConfigJson)

      (result.json \ "journeyStartUrl").as[String] must include(appRoutes.CaptureSautrController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testGeneralPartnershipJourneyConfig(businessVerificationCheck = true))
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

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testScottishPartnershipJourneyConfig(businessVerificationCheck = true))
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

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testScottishLimitedPartnershipJourneyConfig(businessVerificationCheck = true))

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

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testLimitedPartnershipJourneyConfig(businessVerificationCheck = true))
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

      (result.json \ "journeyStartUrl").as[String] must include(appRoutes.CaptureCompanyNumberController.show(testJourneyId).url)

      await(journeyConfigRepository.findById(testJourneyId)) mustBe Some(testLimitedLiabilityPartnershipJourneyConfig(businessVerificationCheck = true))

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
      "no Company Profile exists" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(
          status = OK,
          body = testPartnershipFullJourneyDataJson
        )

        lazy val result = get(s"/partnership-identification/api/journey/$testJourneyId")

        result.status mustBe OK

        val testJson: JsObject =  Json.obj(
          "sautr" -> testSautr,
          "postcode" -> testPostcode,
          "identifiersMatch" -> true,
          "businessVerification" -> Json.obj(
            "verificationStatus" -> "PASS"
          ),
          "registration" -> Json.obj(
            "registrationStatus" -> "REGISTERED",
            "registeredBusinessPartnerId" -> testSafeId
          )
        )

        result.json mustBe testJson
      }
      "the Company Profile exists" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(
          status = OK,
          body = testPartnershipFullJourneyDataJsonWithCompanyProfile
        )

        lazy val result = get(s"/partnership-identification/api/journey/$testJourneyId")

        result.status mustBe OK

        val testJson: JsObject = Json.obj(
          "sautr" -> testSautr,
          "postcode" -> testPostcode,
          "companyProfile" -> Json.toJson(testCompanyProfile),
          "identifiersMatch" -> false,
          "businessVerification" -> Json.obj(
            "verificationStatus" -> "UNCHALLENGED"
          ),
          "registration" -> Json.obj(
            "registrationStatus" -> "REGISTRATION_NOT_CALLED"
          )
        )

        result.json mustBe testJson
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
