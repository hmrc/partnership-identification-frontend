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
import uk.gov.hmrc.partnershipidentificationfrontend.models.BusinessVerificationStatus.format
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.partnershipidentificationfrontend.stubs._
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

class RegistrationControllerISpec extends ComponentSpecHelper with AuthStub with PartnershipIdentificationStub with RegisterStub {

  "GET /:journeyId/register" should {
    "redirect to continueUrl" when {
      "a General Partnership registration is successful and registration status is successfully stored" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveSautr(testJourneyId)(status = OK, body = testSautr)
        stubRetrieveBusinessVerificationStatus(testJourneyId)(status = OK, body = Json.toJson[BusinessVerificationStatus](BusinessVerificationPass))
        stubRegisterGeneralPartnership(testSautr)(status = OK, body = Registered(testSafeId))
        stubStoreRegistrationStatus(testJourneyId, Registered(testSafeId))(status = OK)

        val result = get(s"$baseUrl/$testJourneyId/register")

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe Some(routes.JourneyRedirectController.redirectToContinueUrl(testJourneyId).url)
        verifyRegisterGeneralPartnership(testSautr)
        verifyStoreRegistrationStatus(testJourneyId, Registered(testSafeId))
      }

      "a General Partnership registration failed and registration status is successfully stored" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveSautr(testJourneyId)(status = OK, body = testSautr)
        stubRetrieveBusinessVerificationStatus(testJourneyId)(status = OK, body = Json.toJson[BusinessVerificationStatus](BusinessVerificationPass))
        stubRegisterGeneralPartnership(testSautr)(status = OK, body = RegistrationFailed)
        stubStoreRegistrationStatus(testJourneyId, RegistrationFailed)(status = OK)

        val result = get(s"$baseUrl/$testJourneyId/register")

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe Some(routes.JourneyRedirectController.redirectToContinueUrl(testJourneyId).url)
        verifyRegisterGeneralPartnership(testSautr)
        verifyStoreRegistrationStatus(testJourneyId, RegistrationFailed)
      }

      "a Scottish Partnership registration is successful and registration status is successfully stored" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testScottishPartnershipJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveSautr(testJourneyId)(status = OK, body = testSautr)
        stubRetrieveBusinessVerificationStatus(testJourneyId)(status = OK, body = Json.toJson[BusinessVerificationStatus](BusinessVerificationPass))
        stubRegisterScottishPartnership(testSautr)(status = OK, body = Registered(testSafeId))
        stubStoreRegistrationStatus(testJourneyId, Registered(testSafeId))(status = OK)

        val result = get(s"$baseUrl/$testJourneyId/register")

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe Some(routes.JourneyRedirectController.redirectToContinueUrl(testJourneyId).url)
        verifyRegisterScottishPartnership(testSautr)
        verifyStoreRegistrationStatus(testJourneyId, Registered(testSafeId))
      }

      "a Scottish Partnership registration failed and registration status is successfully stored" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testScottishPartnershipJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveSautr(testJourneyId)(status = OK, body = testSautr)
        stubRetrieveBusinessVerificationStatus(testJourneyId)(status = OK, body = Json.toJson[BusinessVerificationStatus](BusinessVerificationPass))
        stubRegisterScottishPartnership(testSautr)(status = OK, body = RegistrationFailed)
        stubStoreRegistrationStatus(testJourneyId, RegistrationFailed)(status = OK)

        val result = get(s"$baseUrl/$testJourneyId/register")

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe Some(routes.JourneyRedirectController.redirectToContinueUrl(testJourneyId).url)
        verifyRegisterScottishPartnership(testSautr)
        verifyStoreRegistrationStatus(testJourneyId, RegistrationFailed)
      }
    }

    "redirect to SignInPage" when {
      "the user is unauthorised" in {
        stubAuthFailure()

        val result = get(s"$baseUrl/$testJourneyId/register")

        result.status mustBe SEE_OTHER
        result.header(LOCATION) mustBe Some(s"/bas-gateway/sign-in?continue_url=%2Fidentify-your-partnership%2F$testJourneyId%2Fregister&origin=partnership-identification-frontend")
      }
    }

    "throw an exception" when {
      "business verification is in an invalid state" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveSautr(testJourneyId)(status = OK, body = testSautr)
        stubRetrieveBusinessVerificationStatus(testJourneyId)(status = OK, body = Json.toJson[BusinessVerificationStatus](BusinessVerificationFail))

        val result = get(s"$baseUrl/$testJourneyId/register")

        result.status mustBe INTERNAL_SERVER_ERROR
      }

      "sautr is missing" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveSautr(testJourneyId)(status = NOT_FOUND)
        stubRetrieveBusinessVerificationStatus(testJourneyId)(status = OK, body = Json.toJson[BusinessVerificationStatus](BusinessVerificationFail))

        val result = get(s"$baseUrl/$testJourneyId/register")

        result.status mustBe INTERNAL_SERVER_ERROR
      }

      "business verification status is missing" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveSautr(testJourneyId)(status = OK, body = testSautr)
        stubRetrieveBusinessVerificationStatus(testJourneyId)(status = NOT_FOUND)

        val result = get(s"$baseUrl/$testJourneyId/register")

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
