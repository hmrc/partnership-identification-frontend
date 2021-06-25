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

package uk.gov.hmrc.partnershipidentificationfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.partnershipidentificationfrontend.assets.MessageLookup.{Base, BetaBanner, Header, CheckYourAnswers => messages}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.routes
import uk.gov.hmrc.partnershipidentificationfrontend.models.SaInformation
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ViewSpecHelper.ElementExtensions

import scala.collection.JavaConverters._


trait CheckYourAnswersViewTests {
  this: ComponentSpecHelper =>

  def testCheckYourAnswersView(result: => WSResponse, journeyId: String, optSaInformation: Option[SaInformation]): Unit = {
    lazy val doc: Document = Jsoup.parse(result.body)
    lazy val config = app.injector.instanceOf[AppConfig]

    "have a sign out link in the header" in {
      doc.getSignOutText mustBe Header.signOut
    }

    "have sign out link redirecting to signOutUrl from journey config" in {
      doc.getSignOutText mustBe testSignOutUrl
    }

    "have the correct beta banner" in {
      doc.getBanner.text mustBe BetaBanner.title
    }

    "have a banner link that redirects to beta feedback" in {
      doc.getBannerLink mustBe config.betaFeedbackUrl("vrs")
    }

    "have the correct title" in {
      doc.title mustBe messages.title
    }

    "have the correct heading" in {
      doc.getH1Elements.text mustBe messages.heading
    }

    "have a summary list which" should {
      lazy val summaryListRows = doc.getSummaryListRows.iterator().asScala.toList


      optSaInformation match {
        case Some(saInformation) =>
          "have an sautr row" in {
            val sautrRow = summaryListRows.head

            sautrRow.getSummaryListQuestion mustBe messages.sautr
            sautrRow.getSummaryListAnswer mustBe saInformation.sautr
            sautrRow.getSummaryListChangeLink mustBe routes.CaptureSautrController.show(journeyId).url
            sautrRow.getSummaryListChangeText mustBe s"${Base.change} ${messages.sautr}"
          }

          "have a postcode row" in {
            val postcodeRow = summaryListRows.last

            postcodeRow.getSummaryListQuestion mustBe messages.postCode
            postcodeRow.getSummaryListAnswer mustBe saInformation.postcode
            postcodeRow.getSummaryListChangeLink mustBe routes.CapturePostCodeController.show(journeyId).url
            postcodeRow.getSummaryListChangeText mustBe s"${Base.change} ${messages.postCode}"
          }

          "have 2 rows" in {
            summaryListRows.size mustBe 2
          }
        case None =>
          "have an sautr row" in {
            val sautrRow = summaryListRows.head

            sautrRow.getSummaryListQuestion mustBe messages.sautr
            sautrRow.getSummaryListAnswer mustBe messages.noSautr
            sautrRow.getSummaryListChangeLink mustBe routes.CaptureSautrController.show(journeyId).url
            sautrRow.getSummaryListChangeText mustBe s"${Base.change} ${messages.sautr}"
          }

          "have 1 row" in {
            summaryListRows.size mustBe 1
          }
      }

      "have a continue and confirm button" in {
        doc.getSubmitButton.first.text mustBe Base.confirmAndContinue
      }
    }

  }
}
