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

package uk.gov.hmrc.partnershipidentificationfrontend.controllers.errorpages

import play.api.libs.ws.WSResponse
import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.{routes => appRoutes}
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.LimitedPartnership
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.AuthStub
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.errorpages.CompanyNumberNotFoundViewTests


class CompanyNumberNotFoundControllerISpec extends ComponentSpecHelper
  with AuthStub
  with CompanyNumberNotFoundViewTests {

  val controllerUrl: String = s"$baseUrl/$testJourneyId/company-number-not-found"

  "GET /company-number-not-found" should {

    "return OK" in {

      lazy val result: WSResponse = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testLimitedPartnershipJourneyConfig))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        get(controllerUrl)
      }

      result.status mustBe OK
    }

    "return a view" when {

      "no service name is passed in by the journey config" should {

        lazy val result: WSResponse = {
          await(insertJourneyConfig(testJourneyId, testInternalId, testLimitedPartnershipJourneyConfig))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(controllerUrl)
        }

        testCompanyNumberNotFoundView(result)
      }

      "service name is passed in by the journey config" should {

        lazy val result: WSResponse = {
          await(insertJourneyConfig(testDefaultJourneyConfig.copy(partnershipType = LimitedPartnership)))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          get(controllerUrl)
        }

        testCompanyNumberNotFoundView(result, testCallingServiceName)
      }

    }

    "throw an internal server exception" when {

      "the user does not have an internal id" in {

        stubAuth(OK, successfulAuthResponse(None))

        lazy val result = get(controllerUrl)

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

    "redirect a user" when {

      "the user is not authorised" in {

        stubAuthFailure()

        lazy val result = get(controllerUrl)

        result.status mustBe SEE_OTHER

        result.header(LOCATION).getOrElse("") must startWith("/bas-gateway/sign-in")
      }

    }
  }

  "POST /company-number-not-found" should {

    "redirect to find company number page" in {

      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

      lazy val result = post(controllerUrl)()

      result must have {
        httpStatus(SEE_OTHER)
        redirectUri(appRoutes.CaptureCompanyNumberController.show(testJourneyId).url)
      }

    }

    "redirect to bas gateway sign in" when {

      "the user is not authorised" in {

        stubAuthFailure()

        lazy val result = post(controllerUrl)()

        result.status mustBe SEE_OTHER

        result.header(LOCATION).getOrElse("") must startWith("/bas-gateway/sign-in")
      }

    }

  }

}
