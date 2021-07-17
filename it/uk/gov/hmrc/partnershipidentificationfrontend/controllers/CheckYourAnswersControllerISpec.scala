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
import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.featureswitch.core.config.FeatureSwitching
import uk.gov.hmrc.partnershipidentificationfrontend.models.{BusinessVerificationUnchallenged, RegistrationNotCalled, SaInformation}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, PartnershipIdentificationStub, ValidatePartnershipInformationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.CheckYourAnswersViewTests

class CheckYourAnswersControllerISpec extends ComponentSpecHelper
  with CheckYourAnswersViewTests
  with PartnershipIdentificationStub
  with AuthStub
  with FeatureSwitching
  with ValidatePartnershipInformationStub {

  "GET /check-your-answers-business" when {
    "the applicant has an sautr and a postcode" should {
      lazy val result = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationJson)
        get(s"$baseUrl/$testJourneyId/check-your-answers-business")
      }

      "return OK" in {
        result.status mustBe OK
      }

      "return a view which" should {
        testCheckYourAnswersView(result, testJourneyId, Some(SaInformation(testSautr, testPostcode)))
      }
    }

    "the applicant does not have an SAUTR" should {
      lazy val result = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationNoSautrJson)
        get(s"$baseUrl/$testJourneyId/check-your-answers-business")
      }

      "return OK" in {
        result.status mustBe OK
      }

      "return a view which" should {
        testCheckYourAnswersView(result, testJourneyId, optSaInformation = None)
      }
    }

    "the user is not signed in" should {
      "redirect to the sign in page" in {
        stubAuthFailure()
        lazy val result = get(s"$baseUrl/$testJourneyId/check-your-answers-business")

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe Some(s"/bas-gateway/sign-in?continue_url=%2Fidentify-your-partnership%2F$testJourneyId%2Fcheck-your-answers-business&origin=partnership-identification-frontend")
      }
    }
  }

  "POST /check-your-answers-business" should {
    "redirect to the Business Verification Journey" when {
      "the applicant's known facts successfully match" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationJson)
        stubValidate(testPartnershipInformation)(OK, body = Json.obj("identifiersMatch" -> true))
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = true)(OK)

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.BusinessVerificationController.startBusinessVerificationJourney(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = true)
      }
    }

    "redirect to the continueUrl" when {
      "the applicant's known facts do not match" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationJson)
        stubValidate(testPartnershipInformation)(OK, body = Json.obj("identifiersMatch" -> false))
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(OK)
        stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationUnchallenged)(OK)
        stubStoreRegistrationStatus(testJourneyId, RegistrationNotCalled)(OK)

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.JourneyRedirectController.redirectToContinueUrl(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
      }

      "the applicant does not have a sautr" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationNoSautrJson)
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(OK)
        stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationUnchallenged)(OK)
        stubStoreRegistrationStatus(testJourneyId, RegistrationNotCalled)(OK)

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.JourneyRedirectController.redirectToContinueUrl(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
      }
    }

    "throw an internal server error" when {
      "no data is stored for the applicant's journeyId" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(NOT_FOUND)

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

    "the user is not signed in" should {
      "redirect to the sign in page" in {
        stubAuthFailure()
        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe Some(s"/bas-gateway/sign-in?continue_url=%2Fidentify-your-partnership%2F$testJourneyId%2Fcheck-your-answers-business&origin=partnership-identification-frontend")
      }
    }
  }
}
