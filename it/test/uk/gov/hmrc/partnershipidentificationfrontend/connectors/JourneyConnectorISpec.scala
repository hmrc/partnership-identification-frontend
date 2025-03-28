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
import play.api.test.Helpers.{CREATED, INTERNAL_SERVER_ERROR, await, defaultAwaitTimeout}
import uk.gov.hmrc.http.{HeaderCarrier, InternalServerException}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants.testJourneyId
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.JourneyStub
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

class JourneyConnectorISpec extends ComponentSpecHelper with JourneyStub {

  private val createJourneyConnector = app.injector.instanceOf[JourneyConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "createJourney" should {
    "return the journeyId" in {
      stubCreateJourney(CREATED, Json.obj("journeyId" -> testJourneyId))

      val result = await(createJourneyConnector.createJourney())

      result mustBe testJourneyId
    }
    "raise an exception" when {
      "an unexpected status is returned" in {
        stubCreateJourney(INTERNAL_SERVER_ERROR)

        intercept[InternalServerException](await(createJourneyConnector.createJourney()))
      }
    }
  }

}
