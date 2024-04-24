/*
 * Copyright 2024 HM Revenue & Customs
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
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.errorpages.CannotConfirmBusinessErrorViewTests
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.{routes => appRoutes}

class CannotConfirmBusinessErrorControllerISpec extends ComponentSpecHelper
  with CannotConfirmBusinessErrorViewTests
  with PartnershipIdentificationStub
  with AuthStub {

  val testFirstName = "John"
  val testLastName = "Smith"

  "GET /cannot-confirm-business" should {
    lazy val result = {
      await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
      stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
      stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationWithCompanyProfile)
      get(s"$baseUrl/$testJourneyId/cannot-confirm-business")
    }

    "return OK" in {
      result.status mustBe OK
    }

    "return a view which" should {
      testCannotConfirmBusinessView(result)
    }

    "redirect to sign in page" when {
      "the user is UNAUTHORISED" in {
        stubAuthFailure()
        lazy val result: WSResponse = get(s"$baseUrl/$testJourneyId/cannot-confirm-business")
        result must have(
          httpStatus(SEE_OTHER),
          redirectUri("/bas-gateway/sign-in" +
            s"?continue_url=%2Fidentify-your-partnership%2F$testJourneyId%2Fcannot-confirm-business" +
            "&origin=partnership-identification-frontend"
          )
        )
      }
    }
  }

  "POST /cannot-confirm-business" when {

      "the user selects yes radio button" should {

        "redirect to the continue url" in {
          await(insertJourneyConfig(
            testJourneyId,
            testInternalId,
            testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)
          ))

          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

          lazy val result = post(s"$baseUrl/$testJourneyId/cannot-confirm-business")(
            "yes_no" -> "yes"
          )

          result must have(
            httpStatus(SEE_OTHER),
            redirectUri(testContinueUrl + s"?journeyId=$testJourneyId")
          )

        }
      }

      "the user selects no radio button" when {

        "business entity is Scottish and General Partnership" should {

          "redirect to the capture SA utr page" in {

            await(insertJourneyConfig(
              testJourneyId,
              testInternalId,
              testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)
            ))

            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

            lazy val result = post(s"$baseUrl/$testJourneyId/cannot-confirm-business")(
              "yes_no" -> "no"
            )

            result must have(
              httpStatus(SEE_OTHER),
              redirectUri(appRoutes.CaptureSautrController.show(testJourneyId).url)
            )

          }
        }

        "business entity is Limited Partnership" should {

          "redirect to the capture Company Number page" in {

            await(insertJourneyConfig(
              testJourneyId,
              testInternalId,
              testLimitedLiabilityPartnershipJourneyConfig(businessVerificationCheck = true)
            ))

            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

            lazy val result = post(s"$baseUrl/$testJourneyId/cannot-confirm-business")(
              "yes_no" -> "no"
            )

            result must have(
              httpStatus(SEE_OTHER),
              redirectUri(appRoutes.CaptureCompanyNumberController.show(testJourneyId).url)
            )

          }
        }
      }

      "the user selects none of the radio button" should {

        lazy val result = {
          await(journeyConfigRepository.insertJourneyConfig(
            journeyId = testJourneyId,
            authInternalId = testInternalId,
            journeyConfig = testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)
          ))

          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))

          post(s"$baseUrl/$testJourneyId/cannot-confirm-business")()
        }

        "return a bad request" in {
          result.status mustBe BAD_REQUEST
        }

        testCannotConfirmBusinessErrorView(result)
      }

      "redirect to sign in page" when {

        "the user is UNAUTHORISED" in {

          stubAuthFailure()

          lazy val result: WSResponse = post(s"$baseUrl/$testJourneyId/cannot-confirm-business")(
            "yes_no" -> "no"
          )

          result must have(
            httpStatus(SEE_OTHER),
            redirectUri("/bas-gateway/sign-in" +
              s"?continue_url=%2Fidentify-your-partnership%2F$testJourneyId%2Fcannot-confirm-business" +
              "&origin=partnership-identification-frontend"
            )
          )

        }
      }
  }

}
