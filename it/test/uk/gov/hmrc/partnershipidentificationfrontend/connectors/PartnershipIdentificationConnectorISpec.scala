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

package uk.gov.hmrc.partnershipidentificationfrontend.connectors

import play.api.libs.json.Json
import play.api.test.Helpers.{BAD_REQUEST, INTERNAL_SERVER_ERROR, NO_CONTENT, NOT_FOUND, OK, await, defaultAwaitTimeout}
import uk.gov.hmrc.http.{InternalServerException, HeaderCarrier, UpstreamErrorResponse}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.RemovePartnershipDetailsHttpParser.SuccessfullyRemoved
import uk.gov.hmrc.partnershipidentificationfrontend.models.{BusinessVerificationPass, BusinessVerificationStatus, CompanyProfile, PartnershipInformation, SaInformation}
import uk.gov.hmrc.partnershipidentificationfrontend.models.BusinessVerificationStatus.format
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.PartnershipIdentificationStub
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

class PartnershipIdentificationConnectorISpec extends ComponentSpecHelper with PartnershipIdentificationStub {

  private val partnershipInformationConnector = app.injector.instanceOf[PartnershipIdentificationConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  val sautrKey: String = "sautr"
  val companyProfileKey: String = "companyProfile"
  val verificationStatusKey: String = "businessVerification"
  val identifiersMatchKey: String = "identifiersMatch"

  s"retrievePartnershipInformation($testJourneyId, $sautrKey)" should {
    "return Sautr" when {
      "the sautr key is given and a sautr is stored against the journeyId" in {
        stubRetrieveSautr(testJourneyId)(OK, testSautr)

        val result = await(partnershipInformationConnector.retrievePartnershipInformation[String](testJourneyId, sautrKey))

        result mustBe Some(testSautr)
      }
    }
    "return None" when {
      "no sautr is stored against the journeyId" in {
        stubRetrieveSautr(testJourneyId)(NOT_FOUND)

        val result = await(partnershipInformationConnector.retrievePartnershipInformation[String](testJourneyId, sautrKey))

        result mustBe None
      }
    }
    "return UpstreamErrorResponse with status 400" when {
      "a status of bad request is returned" in {
        stubRetrieveSautr(testJourneyId)(BAD_REQUEST)

        val exception = intercept[UpstreamErrorResponse](await(partnershipInformationConnector.retrievePartnershipInformation[String](testJourneyId, sautrKey)))

        exception.statusCode mustBe BAD_REQUEST
      }
    }
    "return UpstreamErrorResponse with status 500" when {
      "a status of internal server error is returned" in {
        stubRetrieveSautr(testJourneyId)(INTERNAL_SERVER_ERROR)

        val exception = intercept[UpstreamErrorResponse](await(partnershipInformationConnector.retrievePartnershipInformation[String](testJourneyId, sautrKey)))

        exception.statusCode mustBe INTERNAL_SERVER_ERROR
      }
    }
    "retrieve the status of business verification" when {
      "the business verification status key is given and a status is stored against the journeyId" in {
        stubRetrieveBusinessVerificationStatus(testJourneyId)(status=OK, Json.toJson[BusinessVerificationStatus](BusinessVerificationPass))

        val result = await(partnershipInformationConnector.retrievePartnershipInformation[BusinessVerificationStatus](testJourneyId, verificationStatusKey))

        result mustBe Some(BusinessVerificationPass)
      }
    }
  }

  s"retrievePartnershipInformation($testJourneyId)" should {
    "return an instance of PartnershipInformation" when {
      "the required information is stored in the journey data" in {

        stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipInformationWithCompanyProfile)

        val result = await(partnershipInformationConnector.retrievePartnershipInformation(testJourneyId))

        result match {
          case Some(partnershipInformation: PartnershipInformation) =>
            partnershipInformation.optSaInformation mustBe Some(SaInformation(testSautr, testPostcode))
            partnershipInformation.optCompanyProfile match {
              case Some(companyProfile: CompanyProfile) =>
                companyProfile.companyName mustBe testCompanyName
                companyProfile.companyNumber mustBe testCompanyNumber
                companyProfile.dateOfIncorporation mustBe testDateOfIncorporation
                companyProfile.unsanitisedCHROAddress.equals(testAddress) mustBe true
              case None => fail("An instance of CompanyProfile is expected")
            }
          case None => fail("An instance of PartnershipInformation should be returned")
        }

      }
    }
    "return None" when {
      "no partnership information is stored against the journey id" in {

        stubRetrievePartnershipDetails(testJourneyId)(NOT_FOUND)

        val result = await(partnershipInformationConnector.retrievePartnershipInformation(testJourneyId))

        result mustBe None

      }
    }
    "return UpstreamErrorResponse with status 400" when {
      "a status of bad request is returned" in {

        stubRetrievePartnershipDetails(testJourneyId)(BAD_REQUEST)

        val exception = intercept[UpstreamErrorResponse](await(partnershipInformationConnector.retrievePartnershipInformation(testJourneyId)))

        exception.statusCode mustBe BAD_REQUEST
      }
     }
    "return UpstreamErrorResponse" when {
      "a status of internal server error is returned" in {

        stubRetrievePartnershipDetails(testJourneyId)(INTERNAL_SERVER_ERROR)

        val exception = intercept[UpstreamErrorResponse](await(partnershipInformationConnector.retrievePartnershipInformation(testJourneyId)))

        exception.statusCode mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  s"storeData($testJourneyId, $sautrKey)" should {
    "return SuccessfullyStored" in {
      stubStoreSautr(testJourneyId, testSautr)(status = OK)

      val result = await(partnershipInformationConnector.storeData[String](testJourneyId, sautrKey, testSautr))

      result mustBe SuccessfullyStored
    }
    "raise an exception" when {
      "an unexpected status is returned" in {
        stubStoreSautr(testJourneyId, testSautr)(status = NOT_FOUND)

        intercept[InternalServerException](await(partnershipInformationConnector.storeData[String](testJourneyId, sautrKey, testSautr)))
      }
    }
  }

  s"retrievePartnershipFullJourneyData($testJourneyId)" should {
    "return all the data stored against the journeyId" in {
      stubRetrievePartnershipDetails(testJourneyId)(OK, testPartnershipFullJourneyDataJson)

      val result = await(partnershipInformationConnector.retrievePartnershipFullJourneyData(testJourneyId))

      result mustBe Some(testPartnershipFullJourneyData)
    }
    "return None" when {
      "no data is stored against the journeyId" in {
        stubRetrievePartnershipDetails(testJourneyId)(NOT_FOUND)

        val result = await(partnershipInformationConnector.retrievePartnershipFullJourneyData(testJourneyId))

        result mustBe None
      }
    }
    "raise an exception" when {
      "an unexpected status is returned" in {
        stubRetrievePartnershipDetails(testJourneyId)(INTERNAL_SERVER_ERROR)

        intercept[InternalServerException](await(partnershipInformationConnector.retrievePartnershipFullJourneyData(testJourneyId)))
      }
    }
  }

  s"removePartnershipInformation($testJourneyId, $testSautr)" should {
    "return successfully removed" when {
      "an SA Utr key is given and the journey data contains a SA Utr value" in {

        stubRemoveSautr(testJourneyId)(NO_CONTENT)

        val result = await(partnershipInformationConnector.removePartnershipInformation(testJourneyId, sautrKey))

        result mustBe SuccessfullyRemoved
      }
    }
    "raise an exception" when {
      "an unexpected status is returned" in {

        stubRemoveSautr(testJourneyId)(INTERNAL_SERVER_ERROR)

        intercept[InternalServerException](await(partnershipInformationConnector.removePartnershipInformation(testJourneyId, sautrKey)))
      }
    }
  }

}
