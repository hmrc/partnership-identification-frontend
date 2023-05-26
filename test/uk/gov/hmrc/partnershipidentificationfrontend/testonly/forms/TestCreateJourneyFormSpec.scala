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

package uk.gov.hmrc.partnershipidentificationfrontend.testonly.forms

import org.scalatest.OptionValues._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers._
import play.api.data.Form
import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.models.JourneyConfig
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.LimitedLiabilityPartnership
import uk.gov.hmrc.partnershipidentificationfrontend.testonly.forms.TestCreateJourneyForm._

class TestCreateJourneyFormSpec extends AnyFlatSpec {

  val incomingFormData: Map[String, String] = Map(
    continueUrl -> testContinueUrl,
    TestCreateJourneyForm.businessVerificationCheck -> "true",
    deskProServiceId -> testDeskProServiceId,
    signOutUrl -> testSignOutUrl,
    accessibilityUrl -> accessibilityUrl,
    regime -> testRegime
  )

  private val formUnderTest: Form[JourneyConfig] = TestCreateJourneyForm.form(LimitedLiabilityPartnership)

  "bind" should "support businessVerificationCheck true" in {

    val actualJourneyConfig: JourneyConfig = formUnderTest
      .bind(incomingFormData)
      .value
      .value

    actualJourneyConfig.businessVerificationCheck should be(true)
  }

  "bind" should "support businessVerificationCheck false" in {
    val actualJourneyConfig: JourneyConfig = formUnderTest
      .bind(incomingFormData.updated(TestCreateJourneyForm.businessVerificationCheck, "false"))
      .value
      .value

    actualJourneyConfig.businessVerificationCheck should be(false)
  }

  "fill" should "support businessVerificationCheck true" in {
    val actualJourneyConfig: JourneyConfig = formUnderTest
      .fill(testDefaultGeneralPartnershipJourneyConfig)
      .value
      .value

    actualJourneyConfig.businessVerificationCheck should be(true)
  }

  "fill" should "support businessVerificationCheck false" in {
    val actualJourneyConfig: JourneyConfig = formUnderTest
      .fill(testDefaultGeneralPartnershipJourneyConfig.copy(businessVerificationCheck = false))
      .value
      .value

    actualJourneyConfig.businessVerificationCheck should be(false)
  }



}
