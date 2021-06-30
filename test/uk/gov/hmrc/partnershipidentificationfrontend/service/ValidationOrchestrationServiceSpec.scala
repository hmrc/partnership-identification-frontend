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
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.partnershipidentificationfrontend.service.mocks.{MockPartnershipIdentificationService, MockValidatePartnershipInformationService}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ValidationOrchestrationServiceSpec extends AnyWordSpec
    with Matchers
    with MockPartnershipIdentificationService
    with MockValidatePartnershipInformationService {

  object TestService extends ValidationOrchestrationService(mockPartnershipIdentificationService, mockValidatePartnershipInformationService)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "orchestrate" should {
    "return IdentifiersMatch" when {
      "the provided details are successfully matched" in {
        mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformation)))
        mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(true))
        mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = true)(Future.successful(SuccessfullyStored))

        lazy val result = await(TestService.orchestrate(testJourneyId))

        result mustBe IdentifiersMatched
      }
    }

    "return IdentifiersMismatch" when {
      "the provided details are successfully matched" in {
        mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(testPartnershipInformation)))
        mockValidateIdentifiers(testSautr, testPostcode)(Future.successful(false))
        mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
        mockStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)(Future.successful(SuccessfullyStored))

        lazy val result = await(TestService.orchestrate(testJourneyId))

        result mustBe IdentifiersMismatch
      }
    }

    "return NoSautrProvided" when {
      "the provided details are successfully matched" in {
        mockRetrievePartnershipInformation(testJourneyId)(Future.successful(Some(PartnershipInformation(None))))
        mockStoreIdentifiersMatch(testJourneyId, identifiersMatch = false)(Future.successful(SuccessfullyStored))
        mockStoreBusinessVerificationResponse(testJourneyId, BusinessVerificationUnchallenged)(Future.successful(SuccessfullyStored))
        mockStoreRegistrationResponse(testJourneyId, RegistrationNotCalled)(Future.successful(SuccessfullyStored))

        lazy val result = await(TestService.orchestrate(testJourneyId))

        result mustBe NoSautrProvided
      }
    }

    "throw an exception" when {
      "there is no data stored for the provided journey id" in {
        mockRetrievePartnershipInformation(testJourneyId)(Future.successful(None))

        intercept[InternalServerException](await(TestService.orchestrate(testJourneyId)))
      }
    }
  }

}
