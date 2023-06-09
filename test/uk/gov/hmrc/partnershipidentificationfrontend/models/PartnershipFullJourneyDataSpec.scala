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

package uk.gov.hmrc.partnershipidentificationfrontend.models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json._
import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._

class PartnershipFullJourneyDataSpec extends AnyWordSpec with Matchers {

  val rawValidJson: String =
    s"""{
       |  "sautr" : "$testSautr",
       |  "postcode" : "$testPostcode",
       |  "identifiersMatch" : "IdentifiersMatched",
       |  "businessVerification" : {
       |    "verificationStatus" : "PASS"
       |    },
       |    "registration" : {
       |      "registrationStatus" : "REGISTERED",
       |      "registeredBusinessPartnerId" : "$testSafeId"
       |    }
       |}""".stripMargin

  "reading a valid current representation of a full partnership journey" should {

    "return journey data with a validation response of IdentifiersMatched when the identifiers have been matched" in {

      val currentFullJourneyJson: JsValue = Json.parse(rawValidJson)

      val expectedCurrentFullJourneyData: PartnershipFullJourneyData =
        PartnershipFullJourneyData(
          Some(testPostcode),
          Some(testSautr),
          None,
          IdentifiersMatched,
          Some(BusinessVerificationPass),
          Registered(testSafeId)
        )

      currentFullJourneyJson.validate[PartnershipFullJourneyData] match {
        case JsSuccess(journeyData, _) => journeyData mustBe expectedCurrentFullJourneyData
        case e: JsError => fail(s"Current full journey data could not be parsed : ${e.toString}")
      }
    }

  }

  "full partnership journey" should {
    "not support boolean values for identifiersMatch" in {
      Json
        .parse(rawValidJson.replace("\"IdentifiersMatched\"", "true"))
        .validate[PartnershipFullJourneyData] match {
        case JsSuccess(_, _) => fail("should have failed")
        case e: JsError => e.toString must include("true not supported as ValidationResponse")
      }
    }
    "not support all string values for identifiersMatch" in {
      Json
        .parse(rawValidJson.replace("IdentifiersMatched", "crap"))
        .validate[PartnershipFullJourneyData] match {
        case JsSuccess(_, _) => fail("should have failed")
        case e: JsError => e.toString must include("\"crap\" not supported as ValidationResponse")
      }
    }
  }

}
