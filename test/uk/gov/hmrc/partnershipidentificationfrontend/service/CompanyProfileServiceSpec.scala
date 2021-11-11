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

import org.mockito.IdiomaticMockito
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.Helpers._
import uk.gov.hmrc.http.{GatewayTimeoutException, HeaderCarrier}
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.{CompanyProfileConnector, PartnershipIdentificationConnector}
import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
import uk.gov.hmrc.partnershipidentificationfrontend.models.CompanyProfile
import uk.gov.hmrc.partnershipidentificationfrontend.service.CompanyProfileService.CompanyProfileWrites

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CompanyProfileServiceSpec extends AnyWordSpec with Matchers with IdiomaticMockito {

  trait Setup {
    val mockCompanyProfileConnector: CompanyProfileConnector = mock[CompanyProfileConnector]
    val mockPartnershipIdentificationConnector: PartnershipIdentificationConnector = mock[PartnershipIdentificationConnector]

    object TestService extends CompanyProfileService(mockPartnershipIdentificationConnector, mockCompanyProfileConnector)

  }

  val dataKey = "companyProfile"
  val testShortCompanyNumber = "1234567"
  val testPaddedCompanyNumber = "01234567"
  val testPrefixedCompanyNumber = "SC12"
  val testPrefixedPaddedCompanyNumber = "SC000012"
  val testSuffixedCompanyNumber = "1234567R"
  val testPrefixSuffixCompanyNumber = "IP1234RS"
  val testInvalidCompanyNumber = "123456 7"


  implicit val hc: HeaderCarrier = HeaderCarrier()

  "retrieveAndStoreCompanyProfile" when {
    "the company number is 8 characters long" should {
      "store and return the company profile" in new Setup {
        mockCompanyProfileConnector.getCompanyProfile(testCompanyNumber) returns Future.successful(Some(testCompanyProfile))
        mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) returns Future.successful(SuccessfullyStored)

        await(TestService.retrieveAndStoreCompanyProfile(testJourneyId, testCompanyNumber)) mustBe Some(testCompanyProfile)

        mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) was called
      }
    }

    "the company number is shorter than 8 characters" when {
      "the CRN has no prefix" should {
        "pad the CRN with 0s, store and return the company profile" in new Setup {
          mockCompanyProfileConnector.getCompanyProfile(testPaddedCompanyNumber) returns Future.successful(Some(testCompanyProfile))
          mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) returns Future.successful(SuccessfullyStored)

          await(TestService.retrieveAndStoreCompanyProfile(testJourneyId, testShortCompanyNumber)) mustBe Some(testCompanyProfile)

          mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) was called
        }
      }

      "the CRN has a prefix" should {
        "pad the CRN with 0s after the prefix, store and return the company profile" in new Setup {
          mockCompanyProfileConnector.getCompanyProfile(testPrefixedPaddedCompanyNumber) returns Future.successful(Some(testCompanyProfile))
          mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) returns Future.successful(SuccessfullyStored)

          await(TestService.retrieveAndStoreCompanyProfile(testJourneyId, testPrefixedCompanyNumber)) mustBe Some(testCompanyProfile)

          mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) was called
        }
      }
    }

    "the company number has a suffix" should {
      "return the result of the connector" in new Setup {
        mockCompanyProfileConnector.getCompanyProfile(testSuffixedCompanyNumber) returns Future.successful(Some(testCompanyProfile))
        mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) returns Future.successful(SuccessfullyStored)

        await(TestService.retrieveAndStoreCompanyProfile(testJourneyId, testSuffixedCompanyNumber)) mustBe Some(testCompanyProfile)

        mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) was called
      }
    }

    "the company number has a prefix and a suffix" should {
      "return the result of the connector" in new Setup {
        mockCompanyProfileConnector.getCompanyProfile(testPrefixSuffixCompanyNumber) returns Future.successful(Some(testCompanyProfile))
        mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) returns Future.successful(SuccessfullyStored)

        await(TestService.retrieveAndStoreCompanyProfile(testJourneyId, testPrefixSuffixCompanyNumber)) mustBe Some(testCompanyProfile)

        mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) was called
      }
    }
  }

  "return None" when {
    "there is no company profile for the given company number" in new Setup {
      mockCompanyProfileConnector.getCompanyProfile(testCompanyNumber) returns Future.successful(None)

      await(TestService.retrieveAndStoreCompanyProfile(testJourneyId, testCompanyNumber)) mustBe None
    }
  }

  "throw an exception" when {
    "the call to the database times out" in new Setup {
      mockCompanyProfileConnector.getCompanyProfile(testCompanyNumber) returns Future.successful(Some(testCompanyProfile))
      mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) returns Future.failed(
        new GatewayTimeoutException("GET of '/testUrl' timed out with message 'testError'")
      )

      intercept[GatewayTimeoutException](
        await(TestService.retrieveAndStoreCompanyProfile(testJourneyId, testCompanyNumber))
      )

      mockPartnershipIdentificationConnector.storeData[CompanyProfile](testJourneyId, dataKey, testCompanyProfile) was called
    }

    "the company number is invalid" in new Setup {
      intercept[IllegalArgumentException](
        await(TestService.retrieveAndStoreCompanyProfile(testJourneyId, testInvalidCompanyNumber))
      )
    }
  }
}
