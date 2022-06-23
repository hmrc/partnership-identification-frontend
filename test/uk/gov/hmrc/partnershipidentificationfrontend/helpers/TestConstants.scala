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

package uk.gov.hmrc.partnershipidentificationfrontend.helpers

import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.GeneralPartnership
import uk.gov.hmrc.partnershipidentificationfrontend.models._

import java.time.LocalDate
import java.util.UUID

object TestConstants {

  val testContinueUrl: String = "/test"
  val testJourneyId: String = UUID.randomUUID().toString
  val testInternalId: String = UUID.randomUUID().toString
  val testSafeId: String = UUID.randomUUID().toString
  val testPostcode: String = "AA11AA"
  val testRegisteredOfficePostcode: String = "BB11BB"
  val testSignOutUrl: String = "/signOutUrl"
  val testAccessibilityUrl: String = "/accessibility"
  val testRegime: String = "VATC"
  val testSautr: String = "1234567890"
  val testCompanyNumber: String = "12345678"
  val testDateOfIncorporation: String = LocalDate.of(2000, 1, 1).toString
  val testCompanyName: String = "ABC Limited"
  val testIdentifiersMatch: Boolean = true
  val testAddress: JsObject = Json.obj(
    "address_line_1" -> "testLine1",
    "address_line_2" -> "test town",
    "care_of" -> "test name",
    "country" -> "United Kingdom",
    "locality" -> "test city",
    "po_box" -> "123",
    "postal_code" -> testRegisteredOfficePostcode,
    "premises" -> "1",
    "region" -> "test region"
  )
  val testCompanyProfile: CompanyProfile = CompanyProfile(testCompanyName, testCompanyNumber, testDateOfIncorporation, testAddress)

  val testPartnershipInformation: PartnershipInformation = PartnershipInformation(Some(SaInformation(testSautr, testPostcode)), None)
  val testPartnershipInformationWithCompanyProfile: PartnershipInformation =
    PartnershipInformation(Some(SaInformation(testSautr, testPostcode)), Some(testCompanyProfile))

  val testServiceName = "testServiceName"
  val testDeskProServiceId = "testDeskProServiceId"
  val testBusinessPartnerId = "testBusinessPartnerId"
  val testDefaultPageConfig: PageConfig = PageConfig(Some(testServiceName), testDeskProServiceId, testSignOutUrl, testAccessibilityUrl, optLabels = None)
  val testDefaultGeneralPartnershipJourneyConfig: JourneyConfig = JourneyConfig(
    testContinueUrl,
    businessVerificationCheck = true,
    pageConfig = testDefaultPageConfig,
    partnershipType = GeneralPartnership,
    testRegime
  )

  val testRegistrationFailedWithSingleFailure: RegistrationFailed = RegistrationFailed(registrationFailures = Array(
    Failure("PARTY_TYPE_MISMATCH", "The remote endpoint has indicated there is Party Type mismatch"))
  )

}
