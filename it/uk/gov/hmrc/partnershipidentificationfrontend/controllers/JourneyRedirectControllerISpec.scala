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

import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.AuthStub
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

class JourneyRedirectControllerISpec extends ComponentSpecHelper with AuthStub {

  "GET /journey/redirect/:journeyId" should {
    "redirect to the journey config continue url" in {
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      await(journeyConfigRepository.insertJourneyConfig(
        testJourneyId,
        testInternalId,
        testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)
      ))

      lazy val result = get(s"$baseUrl/journey/redirect/$testJourneyId")

      result.status mustBe SEE_OTHER
      result.header(LOCATION) mustBe Some(testContinueUrl + s"?journeyId=$testJourneyId")
    }

    "redirect to sign in page" when {
      "the user is not logged in" in {
        lazy val result = {
          stubAuthFailure()
          get(s"$baseUrl/journey/redirect/$testJourneyId")
        }

        val signInUrl = s"/bas-gateway/sign-in?continue_url=%2Fidentify-your-partnership%2Fjourney%2Fredirect%2F$testJourneyId&origin=partnership-identification-frontend"

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(signInUrl)
        }
      }
    }

    "throw an InternalServerException" when {
      "an internal id cannot be retrieved from auth" in {
        lazy val result = {
          stubAuth(OK, successfulAuthResponse(None))
          get(s"$baseUrl/journey/redirect/$testJourneyId")
        }

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

}
