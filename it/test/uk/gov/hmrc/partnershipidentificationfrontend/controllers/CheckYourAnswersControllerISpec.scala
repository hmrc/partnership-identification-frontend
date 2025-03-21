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

package uk.gov.hmrc.partnershipidentificationfrontend.controllers

import com.github.tomakehurst.wiremock.client.WireMock
import org.scalatest.concurrent.Eventually.eventually
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.errorpages.{routes => errorRoutes}
import uk.gov.hmrc.partnershipidentificationfrontend.featureswitch.core.config.FeatureSwitching
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType._
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.{AuditStub, AuthStub, PartnershipIdentificationStub, ValidatePartnershipInformationStub}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.CheckYourAnswersViewTests

class CheckYourAnswersControllerISpec extends ComponentSpecHelper
  with CheckYourAnswersViewTests
  with PartnershipIdentificationStub
  with AuthStub
  with FeatureSwitching
  with ValidatePartnershipInformationStub
  with AuditStub {

  override lazy val extraConfig: Map[String, String] = Map(
    "auditing.enabled" -> "true",
    "auditing.consumer.baseUri.host" -> mockHost,
    "auditing.consumer.baseUri.port" -> mockPort
  )

  override def beforeEach(): Unit = {
    super.beforeEach()
    WireMock.resetAllScenarios()
    stubAudit()
  }

  override protected def afterEach(): Unit = {
    WireMock.resetAllScenarios()
    super.afterEach()
  }

  "GET /check-your-answers-business" when {
    "the applicant has a company number a sautr and postcode" should {
      lazy val result = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationWithCompanyProfile)
        get(s"$baseUrl/$testJourneyId/check-your-answers-business")
      }

      "return OK" in {
        result.status mustBe OK
      }

      "return a view which" should {
        testCheckYourAnswersView(result, testJourneyId, (Some((testSautr, testPostcode)), Some(testCompanyNumber)))
      }
    }

    "the applicant does not have a SAUTR and does not have a company number" should {
      lazy val result = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, Json.obj())
        get(s"$baseUrl/$testJourneyId/check-your-answers-business")
      }

      "return OK" in {
        result.status mustBe OK
      }

      "return a view which" should {
        testCheckYourAnswersView(result, testJourneyId, expectedData = (None, None))
      }
    }

    "the backend service returns a bad request status" should {
      lazy val result = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(BAD_REQUEST)
        get(s"$baseUrl/$testJourneyId/check-your-answers-business")
      }

      "return INTERNAL_SERVER_ERROR" in {
        result.status mustBe INTERNAL_SERVER_ERROR
      }

      "return a view which" should {
        testCheckYourAnswersInternalServerErrorView(result)
      }
    }

    "the backend service returns an internal server error" should {
      lazy val result = {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(INTERNAL_SERVER_ERROR)
        get(s"$baseUrl/$testJourneyId/check-your-answers-business")
      }

      "return INTERNAL_SERVER_ERROR" in {
        result.status mustBe INTERNAL_SERVER_ERROR
      }

      "return a view which" should {
        testCheckYourAnswersInternalServerErrorView(result)
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
    "redirect to the start Business Verification Journey" when {
      "the applicant's known facts successfully match for a general partnership" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationJson)
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> true))
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)(OK)

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.BusinessVerificationController.startBusinessVerificationJourney(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)
      }
      "the applicant's known facts successfully match for an incorporated partnership" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testLimitedPartnershipJourneyConfig(businessVerificationCheck = true)))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipFullJourneyDataJsonWithCompanyProfile)
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> true))
        stubValidate(testSautr, testRegisteredOfficePostcode)(OK, body = Json.obj("identifiersMatch" -> true))
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)(OK)

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.BusinessVerificationController.startBusinessVerificationJourney(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)
      }
    }

    "redirect to the Registration Controller" when {
      "the applicant's known facts successfully match and the businessVerificationCheck is disabled" when {
        "the business entity is General Partnership" in {
          await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = false)))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationJson)
          stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> true))
          stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)(OK)

          lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

          result must have {
            httpStatus(SEE_OTHER)
            redirectUri(routes.RegistrationController.register(testJourneyId).url)
          }

          verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)
        }
        "the business entity is Scottish Partnership" in {
          await(insertJourneyConfig(testJourneyId, testInternalId, testScottishPartnershipJourneyConfig(businessVerificationCheck = false)))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationJson)
          stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> true))
          stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)(OK)

          lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

          result must have {
            httpStatus(SEE_OTHER)
            redirectUri(routes.RegistrationController.register(testJourneyId).url)
          }

          verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)
        }
        "the business entity is a limited Partnership" in {
          await(insertJourneyConfig(testJourneyId, testInternalId, testLimitedPartnershipJourneyConfig(businessVerificationCheck = false)))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipFullJourneyDataJsonWithCompanyProfile)
          stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> true))
          stubValidate(testSautr, testRegisteredOfficePostcode)(OK, body = Json.obj("identifiersMatch" -> true))
          stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)(OK)

          lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

          result must have {
            httpStatus(SEE_OTHER)
            redirectUri(routes.RegistrationController.register(testJourneyId).url)
          }

          verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)
        }

        "the business entity is a limited liability Partnership" in {
          await(insertJourneyConfig(testJourneyId, testInternalId, testLimitedLiabilityPartnershipJourneyConfig(businessVerificationCheck = false)))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipFullJourneyDataJsonWithCompanyProfile)
          stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> true))
          stubValidate(testSautr, testRegisteredOfficePostcode)(OK, body = Json.obj("identifiersMatch" -> true))
          stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)(OK)

          lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

          result must have {
            httpStatus(SEE_OTHER)
            redirectUri(routes.RegistrationController.register(testJourneyId).url)
          }

          verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)
        }

        "the business entity is a Scottish limited Partnership" in {
          await(insertJourneyConfig(testJourneyId, testInternalId, testScottishLimitedPartnershipJourneyConfig(businessVerificationCheck = false)))
          stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
          stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipFullJourneyDataJsonWithCompanyProfile)
          stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> true))
          stubValidate(testSautr, testRegisteredOfficePostcode)(OK, body = Json.obj("identifiersMatch" -> true))
          stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)(OK)

          lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

          result must have {
            httpStatus(SEE_OTHER)
            redirectUri(routes.RegistrationController.register(testJourneyId).url)
          }

          verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMatched)
        }
      }
    }

    "redirect to the cannot confirm business page" when {
      "the applicant's known facts do not match" in {
        await(insertJourneyConfig(
          testJourneyId,
          testInternalId,
          testJourneyConfig(GeneralPartnership, Some(testCallingServiceName), businessVerificationCheck = true, testRegime)
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationJson).setNewScenarioState("auditing")
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> false))
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)(OK)
        stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToCallBV)(OK)
        stubStoreRegistrationStatus(testJourneyId, RegistrationNotCalled)(OK)
        stubRetrievePartnershipDetails(testJourneyId)(OK,
          Json.obj(
            "sautr" -> testSautr,
            "postcode" -> testPostcode,
            "identifiersMatch" -> "IdentifiersMismatch",
            "businessVerification" -> Json.obj(
              "verificationStatus" -> "NOT_ENOUGH_INFORMATION_TO_CALL_BV"
            ),
            "registration" -> Json.obj(
              "registrationStatus" -> "REGISTRATION_NOT_CALLED"
            )
          )
        ).setRequiredScenarioState("auditing")

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(errorRoutes.CannotConfirmBusinessErrorController.show(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)
        eventually {
          verifyAuditDetail(Json.obj(
            "SAUTR" -> testSautr,
            "SApostcode" -> testPostcode,
            "isMatch" -> "false",
            "businessType" -> "General Partnership",
            "VerificationStatus" -> "Not Enough Information to call BV",
            "RegisterApiStatus" -> "not called",
            "callingService" -> testCallingServiceName
          ))
        }
      }

      "the business entity Limited Partnership has a Company Profile stored and the identifiersMatch is false" in {
        await(insertJourneyConfig(
          testJourneyId,
          testInternalId,
          testLimitedPartnershipJourneyConfig(businessVerificationCheck = true))
        )
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationWithCompanyProfile).setNewScenarioState("auditing")
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> false))
        stubValidate(testSautr, testRegisteredOfficePostcode)(OK, body = Json.obj("identifiersMatch" -> false))
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)(OK)
        stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToCallBV)(OK)
        stubStoreRegistrationStatus(testJourneyId, RegistrationNotCalled)(OK)
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipFullJourneyDataJsonWithCompanyProfile).setRequiredScenarioState("auditing")

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(errorRoutes.CannotConfirmBusinessErrorController.show(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)
        eventually {
          verifyAuditDetail(expectedAudit = testLimitedPartnershipAuditJson(businessType = "Limited Partnership"))
        }
      }

      "the business entity Limited Liability Partnership has a Company Profile stored and the identifiersMatch is false" in {
        await(insertJourneyConfig(
          testJourneyId,
          testInternalId,
          testLimitedLiabilityPartnershipJourneyConfig(businessVerificationCheck = true)
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationWithCompanyProfile).setNewScenarioState("auditing")
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> false))
        stubValidate(testSautr, testRegisteredOfficePostcode)(OK, body = Json.obj("identifiersMatch" -> false))
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)(OK)
        stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToCallBV)(OK)
        stubStoreRegistrationStatus(testJourneyId, RegistrationNotCalled)(OK)
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipFullJourneyDataJsonWithCompanyProfile).setRequiredScenarioState("auditing")

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(errorRoutes.CannotConfirmBusinessErrorController.show(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)
        eventually {
          verifyAuditDetail(expectedAudit = testLimitedPartnershipAuditJson(businessType = "Limited Liability Partnership"))
        }
      }

      "the business entity Scottish Limited Partnership has a Company Profile stored and the identifiersMatch is false" in {
        await(insertJourneyConfig(
          testJourneyId,
          testInternalId,
          testScottishLimitedPartnershipJourneyConfig(businessVerificationCheck = true)
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationWithCompanyProfile).setNewScenarioState("auditing")
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> false))
        stubValidate(testSautr, testRegisteredOfficePostcode)(OK, body = Json.obj("identifiersMatch" -> false))
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)(OK)
        stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToCallBV)(OK)
        stubStoreRegistrationStatus(testJourneyId, RegistrationNotCalled)(OK)
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipFullJourneyDataJsonWithCompanyProfile).setRequiredScenarioState("auditing")

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(errorRoutes.CannotConfirmBusinessErrorController.show(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)
        eventually {
          verifyAuditDetail(expectedAudit = testLimitedPartnershipAuditJson(businessType = "Scottish LTD Partnership"))
        }
      }
    }

    "redirect to calling service" when {
      "the regim is not VATC and not enough information for a match" in {
        await(insertJourneyConfig(
          testJourneyId,
          testInternalId,
          testJourneyConfig(LimitedPartnership, Some(testCallingServiceName), businessVerificationCheck = true, "PPT")
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, Json.obj()).setNewScenarioState("auditing")
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = UnMatchable)(OK)
        stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToCallBV)(OK)
        stubStoreRegistrationStatus(testJourneyId, RegistrationNotCalled)(OK)
        stubRetrievePartnershipDetails(testJourneyId)(OK,
          Json.obj(
            "identifiersMatch" -> "UnMatchable",
            "businessVerification" -> Json.obj(
              "verificationStatus" -> "NOT_ENOUGH_INFORMATION_TO_CALL_BV"
            ),
            "registration" -> Json.obj(
              "registrationStatus" -> "REGISTRATION_NOT_CALLED"
            )
          )
        ).setRequiredScenarioState("auditing")

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.JourneyRedirectController.redirectToContinueUrl(testJourneyId).url)
        }
        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = UnMatchable)
      }

      "the regim is not VATC and the details do not match" in {
        await(insertJourneyConfig(
          testJourneyId,
          testInternalId,
          testJourneyConfig(LimitedPartnership, Some(testCallingServiceName), businessVerificationCheck = true, "PPT")
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationWithCompanyProfile).setNewScenarioState("auditing")
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> false))
        stubValidate(testSautr, testRegisteredOfficePostcode)(OK, body = Json.obj("identifiersMatch" -> false))
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)(OK)
        stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToCallBV)(OK)
        stubStoreRegistrationStatus(testJourneyId, RegistrationNotCalled)(OK)
        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipFullJourneyDataJsonWithCompanyProfile).setRequiredScenarioState("auditing")

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.JourneyRedirectController.redirectToContinueUrl(testJourneyId).url)
        }
        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = IdentifiersMismatch)
      }

      for(partnershipType <- Seq((GeneralPartnership, "General Partnership"), (ScottishPartnership, "Scottish Partnership")))
      s"the applicant does not have an sautr and is $partnershipType" in {
        await(insertJourneyConfig(
          testJourneyId,
          testInternalId,
          testJourneyConfig(partnershipType._1, Some(testCallingServiceName), businessVerificationCheck = true, testRegime)
        ))
        stubAuth(OK, successfulAuthResponse(Some(testInternalId)))
        stubRetrievePartnershipDetails(testJourneyId)(OK, Json.obj()).setNewScenarioState("auditing")
        stubStoreIdentifiersMatch(testJourneyId, identifiersMatch = UnMatchable)(OK)
        stubStoreBusinessVerificationStatus(testJourneyId, BusinessVerificationNotEnoughInformationToCallBV)(OK)
        stubStoreRegistrationStatus(testJourneyId, RegistrationNotCalled)(OK)
        stubRetrievePartnershipDetails(testJourneyId)(OK,
          Json.obj(
            "identifiersMatch" -> "UnMatchable",
            "businessVerification" -> Json.obj(
              "verificationStatus" -> "NOT_ENOUGH_INFORMATION_TO_CALL_BV"
            ),
            "registration" -> Json.obj(
              "registrationStatus" -> "REGISTRATION_NOT_CALLED"
            )
          )
        ).setRequiredScenarioState("auditing")

        lazy val result = post(s"$baseUrl/$testJourneyId/check-your-answers-business")()

        result must have {
          httpStatus(SEE_OTHER)
          redirectUri(routes.JourneyRedirectController.redirectToContinueUrl(testJourneyId).url)
        }

        verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = UnMatchable)
        eventually {
          verifyAuditDetail(Json.obj(
            "isMatch" -> "unmatchable",
            "businessType" -> partnershipType._2,
            "VerificationStatus" -> "Not Enough Information to call BV",
            "RegisterApiStatus" -> "not called",
            "callingService" -> testCallingServiceName
          ))
        }
      }
    }

    "throw an internal server error" when {
      "no data is stored for the applicant's journeyId" in {
        await(insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig(businessVerificationCheck = true)))
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
