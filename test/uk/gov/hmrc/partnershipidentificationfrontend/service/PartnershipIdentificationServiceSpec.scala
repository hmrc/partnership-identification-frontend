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

package uk.gov.hmrc.partnershipidentificationfrontend.service

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.test.Helpers._
import uk.gov.hmrc.http.{GatewayTimeoutException, HeaderCarrier}
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.mocks.MockPartnershipIdentificationConnector
import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.RemovePartnershipDetailsHttpParser.SuccessfullyRemoved

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PartnershipIdentificationServiceSpec extends AnyWordSpec with Matchers with MockPartnershipIdentificationConnector {

  object TestService extends PartnershipIdentificationService(mockPartnershipIdentificationConnector)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  val dataKey = "sautr"

  "retrieveSautr" should {
    "return Some(sautr)" when {
      "the sautr exists in the database for a given journey id" in {
        mockRetrievePartnershipInformation[String](
          testJourneyId,
          dataKey
        )(Future.successful(Some(testSautr)))

        val result = await(TestService.retrieveSautr(testJourneyId))

        result mustBe Some(testSautr)
        verifyRetrievePartnershipInformation[String](testJourneyId, dataKey)
      }
    }

    "return None" when {
      "the sautr does not exist in the database for a given journey id" in {
        mockRetrievePartnershipInformation[String](
          testJourneyId,
          dataKey
        )(Future.successful(None))

        val result = await(TestService.retrieveSautr(testJourneyId))

        result mustBe None
        verifyRetrievePartnershipInformation[String](testJourneyId, dataKey)
      }
    }

    "surface an exception" when {
      "the call to the database times out" in {
        mockRetrievePartnershipInformation[String](
          journeyId = testJourneyId,
          dataKey = "sautr"
        )(Future.failed(new GatewayTimeoutException("GET of '/testUrl' timed out with message 'testError'")))

        intercept[GatewayTimeoutException](
          await(TestService.retrieveSautr(testJourneyId))
        )
        verifyRetrievePartnershipInformation[String](testJourneyId, dataKey)
      }
    }
  }

  "removeSaInformation" should {
    "call to remove both SAUTR and postcode" in {
      mockRemovePartnershipInformation(testJourneyId, "sautr")(Future.successful(SuccessfullyRemoved))
      mockRemovePartnershipInformation(testJourneyId, "postcode")(Future.successful(SuccessfullyRemoved))

      val result = await(TestService.removeSaInformation(testJourneyId))

      result mustBe SuccessfullyRemoved
      verifyRemovePartnershipInformation(testJourneyId, "sautr")
      verifyRemovePartnershipInformation(testJourneyId, "postcode")
    }
  }

}
