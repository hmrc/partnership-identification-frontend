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

package uk.gov.hmrc.partnershipidentificationfrontend.views.helpers

import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.govukfrontend.views.Aliases.{Actions, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.{ActionItem, Key}
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.routes
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipInformation

import javax.inject.Singleton

@Singleton
class CheckYourAnswersListBuilder {

  def build(journeyId: String,
            partnershipInformation: PartnershipInformation)(implicit messages: Messages): Seq[SummaryListRow] =
    Seq.empty ++
      maybeACompanyNumberRow(partnershipInformation, journeyId) ++
      Seq(sautrRow(partnershipInformation, journeyId)) ++
      maybeAPostcodeRow(partnershipInformation, journeyId)

  private def maybeACompanyNumberRow(partnershipInformation: PartnershipInformation,
                                     journeyId: String)(implicit messages: Messages): Option[SummaryListRow] =
    partnershipInformation.optCompanyProfile.
      map(partnershipInformation =>
        toSummaryListRow(
          translationKeySuffix = "companyNumber",
          valueToShow = partnershipInformation.companyNumber,
          changeItCall = routes.CaptureCompanyNumberController.show(journeyId))
      )

  private def sautrRow(saInfo: PartnershipInformation, journeyId: String)(implicit messages: Messages): SummaryListRow = toSummaryListRow(
    translationKeySuffix = "sautr",
    valueToShow = saInfo.optSaInformation.map(_.sautr).getOrElse(messages("check-your-answers.no-sautr")),
    changeItCall = routes.CaptureSautrController.show(journeyId)
  )

  private def toSummaryListRow(translationKeySuffix: String, valueToShow: String, changeItCall: Call)(implicit messages: Messages): SummaryListRow = {
    val translationMessage = messages(s"check-your-answers.$translationKeySuffix")
    SummaryListRow(
      key = Key(content = Text(translationMessage)),
      value = Value(content = Text(valueToShow)),
      actions = Some(Actions(items = Seq(
        ActionItem(
          href = changeItCall.url,
          content = Text(messages("base.change")),
          visuallyHiddenText = Some(translationMessage)
        )
      )))
    )
  }

  private def maybeAPostcodeRow(partnershipInformation: PartnershipInformation, journeyId: String)(implicit messages: Messages): Option[SummaryListRow] =
    partnershipInformation
      .optSaInformation
      .map(_.postcode)
      .map(thePostCode => toSummaryListRow(
        translationKeySuffix = "postcode",
        valueToShow = thePostCode,
        changeItCall = routes.CapturePostCodeController.show(journeyId))
      )

}
