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
import play.api.test.Helpers._
import uk.gov.hmrc.http.{InternalServerException, HeaderCarrier}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants.testBusinessVerificationJourneyId
import uk.gov.hmrc.partnershipidentificationfrontend.featureswitch.core.config.{BusinessVerificationStub, FeatureSwitching}
import uk.gov.hmrc.partnershipidentificationfrontend.models.{BusinessVerificationFail, BusinessVerificationPass}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.BusinessVerificationStub
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

class RetrieveBusinessVerificationStatusConnectorISpec extends ComponentSpecHelper with BusinessVerificationStub with FeatureSwitching {

  private val retrieveBusinessVerificationStatusConnector = app.injector.instanceOf[RetrieveBusinessVerificationStatusConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "retrieveBusinessVerificationStatus" when {
    s"the $BusinessVerificationStub feature switch is enabled" should {
      "return BvPass" in {
        enable(BusinessVerificationStub)
        stubRetrieveBusinessVerificationResultFromStub(testBusinessVerificationJourneyId)(OK, Json.obj("verificationStatus" -> "PASS"))

        val result = await(retrieveBusinessVerificationStatusConnector.retrieveBusinessVerificationStatus(testBusinessVerificationJourneyId))

        result mustBe BusinessVerificationPass
      }
      "return BvFail" in {
        enable(BusinessVerificationStub)
        stubRetrieveBusinessVerificationResultFromStub(testBusinessVerificationJourneyId)(OK, Json.obj("verificationStatus" -> "FAIL"))

        val result = await(retrieveBusinessVerificationStatusConnector.retrieveBusinessVerificationStatus(testBusinessVerificationJourneyId))

        result mustBe BusinessVerificationFail
      }
      "raise an internal server exception" when {
        "an unexpected status is returned" in {

          enable(BusinessVerificationStub)
          stubRetrieveBusinessVerificationResultFromStub(testBusinessVerificationJourneyId)(INTERNAL_SERVER_ERROR)

          intercept[InternalServerException](
            await(retrieveBusinessVerificationStatusConnector.retrieveBusinessVerificationStatus(testBusinessVerificationJourneyId))
          )
        }
      }
    }

    s"the $BusinessVerificationStub feature switch is disabled" should {
      "return BvPass" in {
        disable(BusinessVerificationStub)
        stubRetrieveBusinessVerificationResult(testBusinessVerificationJourneyId)(OK, Json.obj("verificationStatus" -> "PASS"))

        val result = await(retrieveBusinessVerificationStatusConnector.retrieveBusinessVerificationStatus(testBusinessVerificationJourneyId))

        result mustBe BusinessVerificationPass
      }
      "return BvFail" in {
        disable(BusinessVerificationStub)
        stubRetrieveBusinessVerificationResult(testBusinessVerificationJourneyId)(OK, Json.obj("verificationStatus" -> "FAIL"))

        val result = await(retrieveBusinessVerificationStatusConnector.retrieveBusinessVerificationStatus(testBusinessVerificationJourneyId))

        result mustBe BusinessVerificationFail
      }
      "raise an internal server exception" when {
        "an unexpected status is returned" in {

          disable(BusinessVerificationStub)
          stubRetrieveBusinessVerificationResult(testBusinessVerificationJourneyId)(INTERNAL_SERVER_ERROR)

          intercept[InternalServerException](
            await(retrieveBusinessVerificationStatusConnector.retrieveBusinessVerificationStatus(testBusinessVerificationJourneyId))
          )
        }
      }
    }
  }
}
