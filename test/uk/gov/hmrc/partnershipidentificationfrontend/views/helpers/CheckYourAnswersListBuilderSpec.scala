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

package uk.gov.hmrc.partnershipidentificationfrontend.views.helpers

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.{Messages, MessagesApi}
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{Actions, Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.ActionItem
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.routes
import uk.gov.hmrc.partnershipidentificationfrontend.helpers.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.models.{PartnershipInformation, SaInformation}

class CheckYourAnswersListBuilderSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val builderUnderTest = new CheckYourAnswersListBuilder()

  object Expected {

    val companyNumberRow = SummaryListRow(
      key = Key(content = Text(TranslationLabel.companyNumber)),
      value = Value(content = Text(testCompanyProfile.companyNumber)),
      actions = Some(Actions(items = Seq(
        ActionItem(
          href = routes.CaptureCompanyNumberController.show(testJourneyId).url,
          content = Text(TranslationLabel.change),
          visuallyHiddenText = Some(TranslationLabel.companyNumber)
        )
      ))))

    val postcodeRow = SummaryListRow(
      key = Key(content = Text(TranslationLabel.postcode)),
      value = Value(content = Text(testPostcode)),
      actions = Some(Actions(items = Seq(
        ActionItem(
          href = routes.CapturePostCodeController.show(testJourneyId).url,
          content = Text(TranslationLabel.change),
          visuallyHiddenText = Some(TranslationLabel.postcode)
        )
      ))))

    val sautrRow = SummaryListRow(
      key = Key(content = Text(TranslationLabel.utr)),
      value = Value(content = Text(testSautr)),
      actions = Some(Actions(items = Seq(
        ActionItem(
          href = routes.CaptureSautrController.show(testJourneyId).url,
          content = Text(TranslationLabel.change),
          visuallyHiddenText = Some(TranslationLabel.utr)
        )
      ))))

    val noSautrRow = SummaryListRow(
      key = Key(content = Text(TranslationLabel.utr)),
      value = Value(content = Text("The business does not have a UTR")),
      actions = Some(Actions(items = Seq(
        ActionItem(
          href = routes.CaptureSautrController.show(testJourneyId).url,
          content = Text(TranslationLabel.change),
          visuallyHiddenText = Some(TranslationLabel.utr)
        )
      ))))

    object TranslationLabel {
      val change = "Change"
      val companyNumber = "Company registration number"
      val postcode = "Self Assessment postcode"
      val utr = "Unique Taxpayer Reference"
    }

  }

  private val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  "build" should {
    "build a summary list sequence" when {
      "the user is on the individual journey" when {
        "there is a company number, a SA-UTR and a postcode" in {

          val actualSummaryList: Seq[SummaryListRow] = builderUnderTest.build(
            testJourneyId,
            PartnershipInformation(Some(SaInformation(testSautr, testPostcode)), Some(testCompanyProfile)),

          )(messages)

          actualSummaryList mustBe Seq(Expected.companyNumberRow, Expected.sautrRow, Expected.postcodeRow)

        }

        "there is no Self Assessment Information and no company number" in {

          val actualSummaryList: Seq[SummaryListRow] = builderUnderTest.build(
            testJourneyId,
            PartnershipInformation(optSaInformation = None, optCompanyProfile = None)
          )(messages)

          actualSummaryList mustBe Seq(Expected.noSautrRow)

        }

        "there is no Self Assessment Information but there is a company number" in {

          val actualSummaryList: Seq[SummaryListRow] = builderUnderTest.build(
            testJourneyId,
            PartnershipInformation(optSaInformation = None, optCompanyProfile = Some(testCompanyProfile))
          )(messages)

          actualSummaryList mustBe Seq(Expected.companyNumberRow, Expected.noSautrRow)

        }
      }

    }
  }

}
