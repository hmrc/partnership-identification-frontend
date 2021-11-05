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

package uk.gov.hmrc.partnershipidentificationfrontend.assets

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.{GeneralPartnership, ScottishLimitedPartnership, ScottishPartnership}
import uk.gov.hmrc.partnershipidentificationfrontend.models.{JourneyConfig, PageConfig, PartnershipInformation, SaInformation, _}

import java.util.UUID

object TestConstants {

  val testJourneyId: String = UUID.randomUUID().toString
  val testScottishLimitedPartnershipJourneyId: String = UUID.randomUUID().toString
  val testSautr: String = "1234567890"
  val testPostcode: String = "AA11AA"
  val testContinueUrl: String = "/test"
  val testCredentialId: String = UUID.randomUUID().toString
  val GGProviderId: String = UUID.randomUUID().toString
  val testGroupId: String = UUID.randomUUID().toString
  val testInternalId: String = UUID.randomUUID().toString
  val testBusinessVerificationJourneyId: String = UUID.randomUUID().toString
  val testSafeId: String = UUID.randomUUID().toString

  val testDeskProServiceId: String = "vrs"
  val testSignOutUrl: String = "Sign out"
  val testDefaultServiceName: String = "Entity Validation Service"
  val testCallingServiceName: String = "Test Service"

  val testJourneyConfig: JourneyConfig = JourneyConfig(testContinueUrl, PageConfig(None, testDeskProServiceId, testSignOutUrl), GeneralPartnership)
  val testScottishPartnershipJourneyConfig: JourneyConfig =
    JourneyConfig(testContinueUrl, PageConfig(None, testDeskProServiceId, testSignOutUrl), ScottishPartnership)
  val testScottishLimitedPartnershipJourneyConfig: JourneyConfig =
    JourneyConfig(testContinueUrl, PageConfig(None, testDeskProServiceId, testSignOutUrl), ScottishLimitedPartnership)

  val testPartnershipInformationJson: JsObject = {
    Json.obj(
      "sautr" -> testSautr,
      "postcode" -> testPostcode
    )
  }

  val testPartnershipFullJourneyDataJson: JsObject = {
    Json.obj(
      "sautr" -> testSautr,
      "postcode" -> testPostcode,
      "identifiersMatch" -> true,
      "businessVerification" -> Json.obj(
        "verificationStatus" -> "PASS"
      ),
      "registration" -> Json.obj(
        "registrationStatus" -> "REGISTERED",
        "registeredBusinessPartnerId" -> testSafeId
      )
    )
  }

  val testPartnershipInformationNoSautrJson: JsObject = Json.obj()

  val testPartnershipInformation: PartnershipInformation = PartnershipInformation(Some(SaInformation(testSautr, testPostcode)))
  val testPartnershipFullJourneyData: PartnershipFullJourneyData = PartnershipFullJourneyData(Some(testPostcode), Some(testSautr), identifiersMatch = true, BusinessVerificationPass, Registered(testSafeId))

}
