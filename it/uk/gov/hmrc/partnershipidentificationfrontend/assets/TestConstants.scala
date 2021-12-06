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
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType._
import uk.gov.hmrc.partnershipidentificationfrontend.models.{PartnershipType => _, _}

import java.time.LocalDate
import java.util.UUID

object TestConstants {

  val testJourneyId: String = UUID.randomUUID().toString
  val testSautr: String = "1234567890"
  val testPostcode: String = "AA11AA"
  val testContinueUrl: String = "/test"
  val testCredentialId: String = UUID.randomUUID().toString
  val GGProviderId: String = UUID.randomUUID().toString
  val testGroupId: String = UUID.randomUUID().toString
  val testInternalId: String = UUID.randomUUID().toString
  val testBusinessVerificationJourneyId: String = UUID.randomUUID().toString
  val testSafeId: String = UUID.randomUUID().toString
  val testCompanyNumber: String = "12345678"
  val testCompanyName: String = "Test Company Ltd"
  val testCtutr: String = "1234567890"
  val testDateOfIncorporation: String = LocalDate.of(2000, 1, 1).toString
  val testAddress: JsObject = Json.obj(
    "address_line_1" -> "testLine1",
    "address_line_2" -> "test town",
    "care_of" -> "test name",
    "country" -> "United Kingdom",
    "locality" -> "test city",
    "po_box" -> "123",
    "postal_code" -> "AA11AA",
    "premises" -> "1",
    "region" -> "test region"
  )
  val testCompanyProfile: CompanyProfile = CompanyProfile(testCompanyName, testCompanyNumber, testDateOfIncorporation, testAddress)

  val testDeskProServiceId: String = "vrs"
  val testSignOutUrl: String = "Sign out"
  val testDefaultServiceName: String = "Entity Validation Service"
  val testCallingServiceName: String = "Test Service"
  val testGeneralPartnershipJourneyConfig: JourneyConfig = testJourneyConfig(GeneralPartnership)
  val testScottishPartnershipJourneyConfig: JourneyConfig = testJourneyConfig(ScottishPartnership)
  val testScottishLimitedPartnershipJourneyConfig: JourneyConfig = testJourneyConfig(ScottishLimitedPartnership)
  val testLimitedPartnershipJourneyConfig: JourneyConfig = testJourneyConfig(LimitedPartnership)
  val testLimitedLiabilityPartnershipJourneyConfig: JourneyConfig = testJourneyConfig(LimitedLiabilityPartnership)

  val testPartnershipInformationJson: JsObject = {
    Json.obj(
      "sautr" -> testSautr,
      "postcode" -> testPostcode
    )
  }

  val testPartnershipInformationNoSautrJson: JsObject = Json.obj()

  val testPartnershipInformationWithCompanyProfile: JsObject =
    Json.obj(
      "sautr" -> testSautr,
      "postcode" -> testPostcode,
      "companyProfile" -> Json.obj(
        "companyName" -> "Test Company Ltd",
        "companyNumber" -> testCompanyNumber,
        "dateOfIncorporation" -> "2020-01-01",
        "unsanitisedCHROAddress" -> Json.obj(
          "address_line_1" -> "testLine1",
          "address_line_2" -> "test town",
          "care_of" -> "test name",
          "country" -> "United Kingdom",
          "locality" -> "test city",
          "po_box" -> "123",
          "postal_code" -> "AA11AA",
          "premises" -> "1",
          "region" -> "test region"
        )
      )
    )

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

  val testPartnershipFullJourneyDataJsonRegistrationFailed: JsObject = {
    Json.obj(
      "sautr" -> testSautr,
      "postcode" -> testPostcode,
      "identifiersMatch" -> true,
      "businessVerification" -> Json.obj(
        "verificationStatus" -> "PASS"
      ),
      "registration" -> Json.obj(
        "registrationStatus" -> "REGISTRATION_FAILED"
      )
    )
  }

  val testPartnershipFullJourneyDataJsonWithCompanyProfile: JsObject = {
    Json.obj(
      "sautr" -> testSautr,
      "postcode" -> testPostcode,
      "identifiersMatch" -> false,
      "businessVerification" -> Json.obj(
        "verificationStatus" -> "UNCHALLENGED"
      ),
      "registration" -> Json.obj(
        "registrationStatus" -> "REGISTRATION_NOT_CALLED"
      ),
      "companyProfile" -> Json.obj(
        "companyName" -> testCompanyName,
        "companyNumber" -> testCompanyNumber,
        "dateOfIncorporation" -> testDateOfIncorporation,
        "unsanitisedCHROAddress" -> Json.obj(
          "address_line_1" -> "testLine1",
          "address_line_2" -> "test town",
          "care_of" -> "test name",
          "country" -> "United Kingdom",
          "locality" -> "test city",
          "po_box" -> "123",
          "postal_code" -> "AA11AA",
          "premises" -> "1",
          "region" -> "test region"
        )
      )
    )
  }

  val testPartnershipInformation: PartnershipInformation = PartnershipInformation(Some(SaInformation(testSautr, testPostcode)), None)

  val testPartnershipFullJourneyData: PartnershipFullJourneyData =
    PartnershipFullJourneyData(
      Some(testPostcode),
      Some(testSautr),
      None,
      identifiersMatch = true,
      BusinessVerificationPass,
      Registered(testSafeId))

  def testJourneyConfig(partnershipType: PartnershipType, serviceName: Option[String] = None): JourneyConfig =
    JourneyConfig(testContinueUrl, PageConfig(serviceName, testDeskProServiceId, testSignOutUrl), partnershipType)

  def testPartnershipFullJourneyDataWithCompanyProfile(companyProfile: Option[CompanyProfile] = None,
                                                       identifiersMatch: Boolean = true): PartnershipFullJourneyData =
    PartnershipFullJourneyData(
      Some(testPostcode),
      Some(testSautr),
      companyProfile,
      identifiersMatch,
      BusinessVerificationUnchallenged,
      RegistrationNotCalled
    )

}
