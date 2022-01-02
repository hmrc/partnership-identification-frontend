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

package uk.gov.hmrc.partnershipidentificationfrontend.service

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.partnershipidentificationfrontend.service.mocks.{MockPartnershipIdentificationService, MockValidatePartnershipInformationService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ValidationOrchestrationServiceSpec extends AnyWordSpec
  with Matchers
  with MockPartnershipIdentificationService
  with MockValidatePartnershipInformationService {

  object TestService extends ValidationOrchestrationService(mockPartnershipIdentificationService, mockValidatePartnershipInformationService)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "orchestrate" when {
    "the user has provided an SAUTR and a postcode" when {
      "the postcode successfully matches" when {
        "the user has not provided any company information" should {
          s"return $IdentifiersMatched" in {
            mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformation)))
            mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(true))
            mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = true)(Future.successful(SuccessfullyStored))

            lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = true))

            result mustBe IdentifiersMatched
            verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = true)
          }
        }
        "the user has provided company information" when {
          "the company information registered office postcode successfully matches" should {
            s"return $IdentifiersMatched" in {
              mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformationWithCompanyProfile)))
              mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(true))
              mockValidateIdentifiers(testSautr, testRegisteredOfficePostcode)(Future.successful(true))
              mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = true)(Future.successful(SuccessfullyStored))

              lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = true))

              result mustBe IdentifiersMatched
              verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = true)
            }
          }
          "the company information registered office postcode fails to match" when {
            "the calling service has requested business verification" should {
              s"return $IdentifiersMismatch and store $BusinessVerificationUnchallenged" in {
                mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformationWithCompanyProfile)))
                mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(true))
                mockValidateIdentifiers(testSautr, testRegisteredOfficePostcode)(Future.successful(false))
                mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
                mockStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)(Future.successful(SuccessfullyStored))
                mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

                lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = true))

                result mustBe IdentifiersMismatch
                verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
                verifyStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)
                verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
              }
            }
            "the calling service has not requested business verification" should {
              s"return $IdentifiersMismatch" in {
                mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformationWithCompanyProfile)))
                mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(true))
                mockValidateIdentifiers(testSautr, testRegisteredOfficePostcode)(Future.successful(false))
                mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
                mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

                lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = false))

                result mustBe IdentifiersMismatch
                verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
                verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
              }
            }
          }
          "the company information contains no registered office address" when {
            "the calling service has requested business verification" should {
              s"return $IdentifiersMismatch and store $BusinessVerificationUnchallenged" in {
                mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformation.copy(
                  optCompanyProfile = Some(testCompanyProfile.copy(
                    unsanitisedCHROAddress = Json.obj()
                  ))
                ))))
                mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(true))
                mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
                mockStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)(Future.successful(SuccessfullyStored))
                mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

                lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = true))

                result mustBe IdentifiersMismatch
                verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
                verifyStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)
                verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
              }
            }
            "the calling service has not requested business verification" should {
              s"return $IdentifiersMismatch" in {
                mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformationWithCompanyProfile)))
                mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(true))
                mockValidateIdentifiers(testSautr, testRegisteredOfficePostcode)(Future.successful(false))
                mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
                mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

                lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = false))

                result mustBe IdentifiersMismatch
                verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
                verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
              }
            }
          }
        }
      }
      "the postcode fails to match" when {
        "the calling service has requested business verification" should {
          s"return $IdentifiersMismatch and store $BusinessVerificationUnchallenged" in {
            mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformation)))
            mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(false))
            mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
            mockStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)(Future.successful(SuccessfullyStored))
            mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

            lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = true))

            result mustBe IdentifiersMismatch
            verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
            verifyStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)
            verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
          }
        }
        "the calling service has not requested business verification" should {
          s"return $IdentifiersMismatch" in {
            mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformation)))
            mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(false))
            mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
            mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

            lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = false))

            result mustBe IdentifiersMismatch
            verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
            verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
          }
        }
      }
    }
    "no Sautr is provided" when {
      "the businessVerificationCheck is enabled" should {
        s"return $NoSautrProvided and store $BusinessVerificationUnchallenged" in {
          mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(PartnershipInformation(None, None))))
          mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
          mockStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)(Future.successful(SuccessfullyStored))
          mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

          lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = true))

          result mustBe NoSautrProvided
          verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
          verifyStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)
          verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
        }
      }
      "the businessVerificationCheck is disabled" should {
        s"return $NoSautrProvided" in {
          mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(PartnershipInformation(None, None))))
          mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
          mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

          lazy val result = await(TestService.orchestrate(testJourneyId, businessVerificationCheck = false))

          result mustBe NoSautrProvided
          verifyStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)
          verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
        }
      }
    }
    "throw an exception" when {
      "there is no data stored for the provided journey id" in {
        mockRetrievePartnershipInformation(testJourneyId)(Future.successful(None))

        intercept[InternalServerException](await(TestService.orchestrate(testJourneyId, businessVerificationCheck = true)))
      }
    }
  }
}
