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

import play.api.libs.json.JsString
import play.api.test.Helpers.{NOT_FOUND, OK, await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
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

        val result = await(partnershipInformationConnector.retrievePartnershipInformation[JsString](testJourneyId, sautrKey))

        result mustBe Some(JsString(testSautr))
      }
    }
    "return None" when {
      "no sautr is stored against the journeyId" in {
        stubRetrieveSautr(testJourneyId)(NOT_FOUND)

        val result = await(partnershipInformationConnector.retrievePartnershipInformation[JsString](testJourneyId, sautrKey))

        result mustBe None
      }
    }
  }

  s"storeData($testJourneyId, $sautrKey)" should {
    "return SuccessfullyStored" in {
      stubStoreSautr(testJourneyId, testSautr)(status = OK)

      val result = await(partnershipInformationConnector.storeData[String](testJourneyId, sautrKey, testSautr))

      result mustBe SuccessfullyStored
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
  }

}
