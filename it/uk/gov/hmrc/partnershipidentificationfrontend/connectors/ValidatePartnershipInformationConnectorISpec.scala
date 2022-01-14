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

package uk.gov.hmrc.partnershipidentificationfrontend.connectors

import play.api.libs.json.Json
import play.api.test.Helpers.{GATEWAY_TIMEOUT, OK, await, defaultAwaitTimeout}
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants.{testPartnershipInformation, testPostcode, testSautr}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.ValidatePartnershipInformationStub
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

import scala.concurrent.ExecutionContext.Implicits.global

class ValidatePartnershipInformationConnectorISpec extends ComponentSpecHelper with ValidatePartnershipInformationStub {

  lazy val connector: ValidatePartnershipInformationConnector = app.injector.instanceOf[ValidatePartnershipInformationConnector]

  implicit val hc: HeaderCarrier = HeaderCarrier()

  "POST /partnership-information/validate-partnership-information" should {
    "return true" when {
      "the supplied postcode matches what is held downstream" in {
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> true))

        val result = await(connector.validate(testSautr, testPostcode))

        result mustBe true
      }
    }

    "return false" when {
      "the supplied postcode does not match what is held downstream" in {
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj("identifiersMatch" -> false))

        val result = await(connector.validate(testSautr, testPostcode))

        result mustBe false
      }
    }

    "throw an exception" when {
      "any other response is returned" in {
        stubValidate(testSautr, testPostcode)(GATEWAY_TIMEOUT, body = Json.obj())

        intercept[InternalServerException](await(connector.validate(testSautr, testPostcode)))
      }

      "invalid JSON is returned" in {
        stubValidate(testSautr, testPostcode)(OK, body = Json.obj())

        intercept[InternalServerException](await(connector.validate(testSautr, testPostcode)))
      }
    }
  }

}
