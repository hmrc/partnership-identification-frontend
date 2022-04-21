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

package uk.gov.hmrc.partnershipidentificationfrontend.models

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json._

import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._

/**
 * Note, this is a temporary test class checking the deserialization
 * of the ValidationResponse property when represented by String or
 * Boolean value.
 */
class PartnershipFullJourneyDataSpec extends AnyWordSpec with Matchers {

  val currentFullJourneyJson: JsValue = Json.parse(
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
  )

  val previousFullJourneyJson: JsValue = Json.parse(
    s"""{
       |  "sautr" : "$testSautr",
       |  "postcode" : "$testPostcode",
       |  "identifiersMatch" : true,
       |  "businessVerification" : {
       |    "verificationStatus" : "PASS"
       |    },
       |    "registration" : {
       |      "registrationStatus" : "REGISTERED",
       |      "registeredBusinessPartnerId" : "$testSafeId"
       |    }
       |}""".stripMargin
  )

  val erroneousFullJourneyJson: JsValue = Json.parse(
    s"""{
       |  "sautr" : "$testSautr",
       |  "postcode" : "$testPostcode",
       |  "identifiersMatch" : 10,
       |  "businessVerification" : {
       |    "verificationStatus" : "PASS"
       |    },
       |    "registration" : {
       |      "registrationStatus" : "REGISTERED",
       |      "registeredBusinessPartnerId" : "$testSafeId"
       |    }
       |}""".stripMargin
  )

  val expectedCurrentFullJourneyData: PartnershipFullJourneyData =
    PartnershipFullJourneyData(
      Some(testPostcode),
      Some(testSautr),
      None,
      IdentifiersMatched,
      Some(BusinessVerificationPass),
      Registered(testSafeId)
    )

  "reading a valid current representation of a full partnership journey" should {

    "return journey data with a validation response of IdentifiersMatched when the identifiers have been matched" in {

      currentFullJourneyJson.validate[PartnershipFullJourneyData] match {
        case JsSuccess(journeyData, _) => journeyData mustBe expectedCurrentFullJourneyData
        case e: JsError => fail(s"Current full journey data could not be parsed : ${e.toString}")
      }
    }
    
  }

  "reading a valid previous representation of a full partnership journey" should {

    "return journey data with a validation response of IdentifiersMatched when the identifiers have been matched" in {

      previousFullJourneyJson.validate[PartnershipFullJourneyData] match {
        case JsSuccess(journeyData, _) => journeyData mustBe expectedCurrentFullJourneyData
        case e: JsError => fail(s"Previous full journey data could not be parsed : ${e.toString}")
      }

    }
  }

  "reading an invalid representation of a full partnership journey" should {

    "raise a JsError" in {

      erroneousFullJourneyJson.validate[PartnershipFullJourneyData] match {
        case JsSuccess(_,_) => fail(s"Invalid journey data should not have been parsed")
        case e: JsError => e.errors.head._2.head.message mustBe "Invalid validation response"
      }

    }

  }

}
