/*
 * Copyright 2023 HM Revenue & Customs
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
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.mocks.MockRegistrationConnector
import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType._
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.partnershipidentificationfrontend.service.mocks.MockPartnershipIdentificationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RegistrationOrchestrationServiceSpec extends AnyWordSpec
  with Matchers
  with MockPartnershipIdentificationService
  with MockRegistrationConnector {

  object TestService extends RegistrationOrchestrationService(
    mockPartnershipIdentificationService,
    mockRegistrationConnector
  )

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "register" should {
    "store the registration response" when {
      "the General Partnership is successfully verified and then registered" when {
        "the businessVerificationCheck is enabled" in {
          mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
          mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
          mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
          mockRegisterGeneralPartnership(testSautr, testRegime)(Future.successful(Registered(testSafeId)))
          mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

          await(TestService.register(testJourneyId, GeneralPartnership, businessVerificationCheck = true, testRegime)) mustBe Registered(testSafeId)

          verifyRegisterGeneralPartnership(testSautr, testRegime)
          verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
        }
        "the businessVerificationCheck is not enabled" in {
          mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
          mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
          mockRegisterGeneralPartnership(testSautr, testRegime)(Future.successful(Registered(testSafeId)))
          mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

          await(TestService.register(testJourneyId, GeneralPartnership, businessVerificationCheck = false, testRegime)) mustBe Registered(testSafeId)

          verifyRegisterGeneralPartnership(testSautr, testRegime)
          verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
        }
      }

      "the General Partnership is verified but fails to register" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
        mockRegisterGeneralPartnership(testSautr, testRegime)(Future.successful(testRegistrationFailedWithSingleFailure))
        mockStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, GeneralPartnership, businessVerificationCheck = true, testRegime)) mustBe testRegistrationFailedWithSingleFailure

        verifyRegisterGeneralPartnership(testSautr, testRegime)
        verifyStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)
      }

      "the Scottish Partnership is successfully verified and then registered" when {
        "the businessVerificationCheck is enabled" in {
          mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
          mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
          mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
          mockRegisterScottishPartnership(testSautr, testRegime)(Future.successful(Registered(testSafeId)))
          mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

          await(TestService.register(testJourneyId, ScottishPartnership, businessVerificationCheck = true, testRegime)) mustBe Registered(testSafeId)

          verifyRegisterScottishPartnership(testSautr, testRegime)
          verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
        }
        "the businessVerificationCheck is not enabled" in {
          mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
          mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
          mockRegisterScottishPartnership(testSautr, testRegime)(Future.successful(Registered(testSafeId)))
          mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

          await(TestService.register(testJourneyId, ScottishPartnership, businessVerificationCheck = false, testRegime)) mustBe Registered(testSafeId)

          verifyRegisterScottishPartnership(testSautr, testRegime)
          verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
        }
      }

      "when the Scottish Partnership is verified but fails to register" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
        mockRegisterScottishPartnership(testSautr, testRegime)(Future.successful(testRegistrationFailedWithSingleFailure))
        mockStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, ScottishPartnership, businessVerificationCheck = true, testRegime)) mustBe testRegistrationFailedWithSingleFailure

        verifyRegisterScottishPartnership(testSautr, testRegime)
        verifyStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)
      }
    }

    "the Limited Partnership is successfully verified and then registered" when {
      "the businessVerificationCheck is enabled" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(testCompanyProfile)))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
        mockRegisterLimitedPartnership(testSautr, testCompanyNumber, testRegime)(Future.successful(Registered(testSafeId)))
        mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, LimitedPartnership, businessVerificationCheck = true, testRegime)) mustBe Registered(testSafeId)

        verifyRegisterLimitedPartnership(testSautr, testCompanyNumber, testRegime)
        verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
      }
      "the businessVerificationCheck is not enabled" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(testCompanyProfile)))
        mockRegisterLimitedPartnership(testSautr, testCompanyNumber, testRegime)(Future.successful(Registered(testSafeId)))
        mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, LimitedPartnership, businessVerificationCheck = false, testRegime)) mustBe Registered(testSafeId)

        verifyRegisterLimitedPartnership(testSautr, testCompanyNumber, testRegime)
        verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
      }
    }

    "when the Limited Partnership is verified but fails to register" in {
      mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
      mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(testCompanyProfile)))
      mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
      mockRegisterLimitedPartnership(testSautr, testCompanyNumber, testRegime)(Future.successful(testRegistrationFailedWithSingleFailure))
      mockStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)(Future.successful(SuccessfullyStored))

      await(TestService.register(testJourneyId, LimitedPartnership, businessVerificationCheck = true, testRegime)) mustBe testRegistrationFailedWithSingleFailure

      verifyRegisterLimitedPartnership(testSautr, testCompanyNumber, testRegime)
      verifyStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)
    }

    "the Limited Liability Partnership is successfully verified and then registered" when {
      "the businessVerificationCheck is enabled" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(testCompanyProfile)))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
        mockRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime)(Future.successful(Registered(testSafeId)))
        mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, LimitedLiabilityPartnership, businessVerificationCheck = true, testRegime)) mustBe Registered(testSafeId)

        verifyRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime)
        verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
      }
      "the businessVerificationCheck is not enabled" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(testCompanyProfile)))
        mockRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime)(Future.successful(Registered(testSafeId)))
        mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, LimitedLiabilityPartnership, businessVerificationCheck = false, testRegime)) mustBe Registered(testSafeId)

        verifyRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime)
        verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
      }
    }

    "when the Limited Liability Partnership is verified but fails to register" in {
      mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
      mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(testCompanyProfile)))
      mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
      mockRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime)(Future.successful(testRegistrationFailedWithSingleFailure))
      mockStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)(Future.successful(SuccessfullyStored))

      await(TestService.register(testJourneyId, LimitedLiabilityPartnership, businessVerificationCheck = true, testRegime)) mustBe testRegistrationFailedWithSingleFailure

      verifyRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime)
      verifyStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)
    }

    "the Scottish Limited Partnership is successfully verified and then registered" when {
      "the businessVerificationCheck is enabled" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(testCompanyProfile)))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
        mockRegisterScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime)(Future.successful(Registered(testSafeId)))
        mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, ScottishLimitedPartnership, businessVerificationCheck = true, testRegime)) mustBe Registered(testSafeId)

        verifyRegisterScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime)
        verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
      }
      "the businessVerificationCheck is not enabled" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(testCompanyProfile)))
        mockRegisterScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime)(Future.successful(Registered(testSafeId)))
        mockStoreRegistrationResponse(testJourneyId, Registered(testSafeId))(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, ScottishLimitedPartnership, businessVerificationCheck = false, testRegime)) mustBe Registered(testSafeId)

        verifyRegisterScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime)
        verifyStoreRegistrationResponse(testJourneyId, Registered(testSafeId))
      }
    }

    "when the Scottish Limited Partnership is verified but fails to register" in {
      mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
      mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(testCompanyProfile)))
      mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))
      mockRegisterScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime)(Future.successful(testRegistrationFailedWithSingleFailure))
      mockStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)(Future.successful(SuccessfullyStored))

      await(TestService.register(testJourneyId, ScottishLimitedPartnership, businessVerificationCheck = true, testRegime)) mustBe testRegistrationFailedWithSingleFailure

      verifyRegisterScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime)
      verifyStoreRegistrationResponse(testJourneyId, testRegistrationFailedWithSingleFailure)
    }

    "store a registration state of registration not called" when {
      "the business entity did not pass verification" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationFail)))
        mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, GeneralPartnership, businessVerificationCheck = true, testRegime)) mustBe RegistrationNotCalled

        verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
      }

      "the business entity was not challenged to verify" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationNotEnoughInformationToCallBV)))
        mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

        await(TestService.register(testJourneyId, GeneralPartnership, businessVerificationCheck = true, testRegime)) mustBe RegistrationNotCalled

        verifyStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)
      }
    }

    "throw an Internal Server Exception" when {
      "there is no sautr in the database" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(None))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(Some(BusinessVerificationPass)))

        intercept[InternalServerException](
          await(TestService.register(testJourneyId, GeneralPartnership, businessVerificationCheck = true, testRegime))
        )
      }

      "there is no business verification response in the database and the businessVerificationCheck flag is true" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(None))

        intercept[InternalServerException](
          await(TestService.register(testJourneyId, GeneralPartnership, businessVerificationCheck = true, testRegime))
        )
      }

      "there is nothing in the database" in {
        mockRetrieveSautr(testJourneyId)(Future.successful(None))
        mockRetrieveCompanyProfile(testJourneyId)(Future.successful(None))
        mockRetrieveBusinessVerificationResponse(testJourneyId)(Future.successful(None))

        intercept[InternalServerException](
          await(TestService.register(testJourneyId, GeneralPartnership, businessVerificationCheck = true, testRegime))
        )
      }
    }
  }

}
