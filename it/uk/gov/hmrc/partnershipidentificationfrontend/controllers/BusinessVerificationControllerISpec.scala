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

import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.featureswitch.core.config.{BusinessVerificationStub, FeatureSwitching}
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuthStub, BusinessVerificationStub, PartnershipIdentificationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

class BusinessVerificationControllerISpec extends ComponentSpecHelper with FeatureSwitching with AuthStub with BusinessVerificationStub
  with PartnershipIdentificationStub with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    disable(BusinessVerificationStub)
  }

  private lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  "GET /:journeyId/start-business-verification" when {
    s"the $BusinessVerificationStub feature switch is enabled" should {
      "redirect to business verification redirectUri" when {
        "business verification returns a journey to redirect to" when {
          "the journey configuration does not define a calling service" in {
            enable(BusinessVerificationStub)
            await(journeyConfigRepository.insertJourneyConfig(
              journeyId = testJourneyId,
              authInternalId = testInternalId,
              journeyConfig = testGeneralPartnershipJourneyConfig(true)
            ))
            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
            stubRetrieveSautr(testJourneyId)(OK, testSautr)
            stubCreateBusinessVerificationJourneyFromStub(
              testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(CREATED, Json.obj("redirectUri" -> testContinueUrl))

            lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

            result must have(
              httpStatus(SEE_OTHER),
              redirectUri(testContinueUrl)
            )
          }
          "the journey configuration defines the name of the calling service" in {
            enable(BusinessVerificationStub)
            await(journeyConfigRepository.insertJourneyConfig(
              journeyId = testJourneyId,
              authInternalId = testInternalId,
              journeyConfig = testGeneralPartnershipJourneyConfigWithCallingService(true)
            ))
            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
            stubRetrieveSautr(testJourneyId)(OK, testSautr)
            stubCreateBusinessVerificationJourneyFromStub(
              testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfigWithCallingService(true))(CREATED, Json.obj("redirectUri" -> testContinueUrl))

            lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

            result must have(
              httpStatus(SEE_OTHER),
              redirectUri(testContinueUrl)
            )

          }
        }
      }

      "store a verification state of NOT_ENOUGH_INFORMATION_TO_CHALLENGE and redirect to the registration controller" when {
        "business verification does not have enough information to create a verification journey" in {
          await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
          enable(BusinessVerificationStub)
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubRetrieveSautr(testJourneyId)(OK, testSautr)
          stubCreateBusinessVerificationJourneyFromStub(testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(NOT_FOUND)
          stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToChallenge)(OK)

          lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

          result must have(
            httpStatus(SEE_OTHER),
            redirectUri(routes.RegistrationController.register(testJourneyId).url)
          )
          verifyStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToChallenge)
        }
      }

      "store a verification state of FAIL and redirect to the registration controller" when {
        "business verification reports the user is locked out" in {
          await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
          enable(BusinessVerificationStub)
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubRetrieveSautr(testJourneyId)(OK, testSautr)
          stubCreateBusinessVerificationJourneyFromStub(testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(FORBIDDEN)
          stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationFail)(OK)

          lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

          result must have(
            httpStatus(SEE_OTHER),
            redirectUri(routes.RegistrationController.register(testJourneyId).url)
          )
          verifyStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationFail)
        }
      }
    }

    s"the $BusinessVerificationStub feature switch is disabled" should {
      "redirect to business verification redirectUri" when {
        "business verification returns a journey to redirect to" when {
          "the journey configuration does not define a calling service" in {
            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
            await(journeyConfigRepository.insertJourneyConfig(
              journeyId = testJourneyId,
              authInternalId = testInternalId,
              journeyConfig = testGeneralPartnershipJourneyConfig(true)
            ))
            stubRetrieveSautr(testJourneyId)(OK, testSautr)
            stubCreateBusinessVerificationJourney(
              testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(CREATED, Json.obj("redirectUri" -> testContinueUrl))

            lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

            result must have(
              httpStatus(SEE_OTHER),
              redirectUri(testContinueUrl)
            )
          }
          "the journey configuration defines a calling service" in {
            stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
            await(journeyConfigRepository.insertJourneyConfig(
              journeyId = testJourneyId,
              authInternalId = testInternalId,
              journeyConfig = testGeneralPartnershipJourneyConfigWithCallingService(true)
            ))
            stubRetrieveSautr(testJourneyId)(OK, testSautr)
            stubCreateBusinessVerificationJourney(
              testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfigWithCallingService(true))(CREATED, Json.obj("redirectUri" -> testContinueUrl))

            lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

            result must have(
              httpStatus(SEE_OTHER),
              redirectUri(testContinueUrl)
            )
          }
        }
      }

      "store a verification state of NOT_ENOUGH_INFORMATION_TO_CHALLENGE and redirect to the registration controller" when {
        "business verification does not have enough information to create a verification journey" in {
          await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubRetrieveSautr(testJourneyId)(OK, testSautr)
          stubCreateBusinessVerificationJourney(testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(NOT_FOUND)
          stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToChallenge)(OK)

          lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

          result must have(
            httpStatus(SEE_OTHER),
            redirectUri(routes.RegistrationController.register(testJourneyId).url)
          )
          verifyStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToChallenge)
        }
      }

      "store a verification state of FAIL and redirect to the registration controller" when {
        "business verification reports the user is locked out" in {
          await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubRetrieveSautr(testJourneyId)(OK, testSautr)
          stubCreateBusinessVerificationJourney(testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(FORBIDDEN)
          stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationFail)(OK)

          lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

          result must have(
            httpStatus(SEE_OTHER),
            redirectUri(routes.RegistrationController.register(testJourneyId).url)
          )
          verifyStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationFail)
        }
      }
    }

    "the user is Unauthorised" in {
      stubAuthFailure()

      lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

      result.status mustBe SEE_OTHER
      result.header(LOCATION) mustBe Some(signInRedirectUrl(testJourneyId, "start-business-verification"))
    }

    "the user does not have an internal ID" should {
      "throw an Internal Server Exception" in {
        stubAuth(OK, successfulAuthResponse(None))

        lazy val result = get(s"$baseUrl/$testJourneyId/start-business-verification")

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "GET /business-verification-result" when {
    s"the $BusinessVerificationStub feature switch is enabled" should {
      "redirect to the continueUrl if BV status is stored successfully" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
        enable(BusinessVerificationStub)
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveBusinessVerificationResultFromStub(testBusinessVerificationJourneyId)(OK, Json.obj("verificationStatus" -> "PASS"))
        stubStoreBusinessVerificationStatus(journeyId = testJourneyId, businessVerificationStatus = BusinessVerificationPass)(status = OK)

        lazy val result = get(s"$baseUrl/$testJourneyId/business-verification-result" + s"?journeyId=$testBusinessVerificationJourneyId")

        result must have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.RegistrationController.register(testJourneyId).url)
        )
        verifyStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationPass)
      }

      "throw an exception when the query string is missing" in {
        enable(BusinessVerificationStub)
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveBusinessVerificationResultFromStub(testBusinessVerificationJourneyId)(OK, Json.obj("verificationStatus" -> "PASS"))
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))

        lazy val result = get(s"$baseUrl/$testJourneyId/business-verification-result")

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

    s"the $BusinessVerificationStub feature switch is disabled" should {
      "redirect to the continueUrl if BV status is stored successfully" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveBusinessVerificationResult(testBusinessVerificationJourneyId)(OK, Json.obj("verificationStatus" -> "PASS"))
        stubStoreBusinessVerificationStatus(journeyId = testJourneyId, businessVerificationStatus = BusinessVerificationPass)(status = OK)

        lazy val result = get(s"$baseUrl/$testJourneyId/business-verification-result" + s"?journeyId=$testBusinessVerificationJourneyId")

        result must have(
          httpStatus(SEE_OTHER),
          redirectUri(routes.RegistrationController.register(testJourneyId).url)
        )
        verifyStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationPass)
      }

      "throw an exception when the query string is missing" in {
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrieveBusinessVerificationResult(testBusinessVerificationJourneyId)(OK, Json.obj("verificationStatus" -> "PASS"))

        lazy val result = get(s"$baseUrl/$testJourneyId/business-verification-result")

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }

    "the user is Unauthorised" in {
      stubAuthFailure()

      lazy val result = get(s"$baseUrl/$testJourneyId/business-verification-result" + s"?journeyId=$testBusinessVerificationJourneyId")

      result.status mustBe SEE_OTHER
      result.header(LOCATION) mustBe Some(signInRedirectUrl(testJourneyId, "business-verification-result"))
    }

    "the user does not have an internal ID" should {
      "throw an Internal Server Exception" in {
        stubAuth(OK, successfulAuthResponse(None))

        lazy val result = get(s"$baseUrl/$testJourneyId/business-verification-result" + s"?journeyId=$testBusinessVerificationJourneyId")

        result.status mustBe INTERNAL_SERVER_ERROR
      }
    }
  }
}
