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

package uk.gov.hmrc.partnershipidentificationfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.partnershipidentificationfrontend.assets.MessageLookup.{Base, BetaBanner, Header, ConfirmPartnershipName => messages}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ViewSpecHelper.ElementExtensions

trait ConfirmPartnershipNameViewTests {
  this: ComponentSpecHelper =>

  def testConfirmPartnershipNameView(result: => WSResponse,
                                     serviceName: String): Unit = {

    lazy val doc: Document = Jsoup.parse(result.body)

    lazy val config = app.injector.instanceOf[AppConfig]

    "have the correct title" in {
      if (serviceName == "")
        doc.title mustBe messages.title
      else if (serviceName.contains("Test"))
        doc.title mustBe messages.pageConfigTestTitle
      else
        doc.title mustBe messages.title
    }

    "have a sign out link in the header" in {
      doc.getSignOutText mustBe Header.signOut
    }

    "have sign out link redirecting to signOutUrl from journey config" in {
      doc.getSignOutLink mustBe testSignOutUrl
    }

    "have the correct beta banner" in {
      doc.getBanner.text mustBe BetaBanner.title
    }

    "have a banner link that redirects to beta feedback" in {
      doc.getBannerLink mustBe config.betaFeedbackUrl("vrs")
    }

    "have the correct heading" in {
      doc.getH1Elements.first.text mustBe messages.heading
    }

    "display the company name" in {
      doc.getParagraphs.eq(1).text mustBe testCompanyName
    }

    "Have the correct link" in {
      doc.getLink("change-company").text mustBe messages.change_company_link
    }

    "have a save and confirm button" in {
      doc.getSubmitButton.first.text mustBe Base.saveAndContinue
    }

    "have a back link" in {
      doc.getBackLinkText mustBe Base.back
    }

    "have the corect technical help link and text" in {
      doc.getTechnicalHelpLinkText mustBe Base.getHelp
      doc.getTechnicalHelpLink mustBe testTechnicalHelpUrl
    }

    "have accessibility statement link redirecting to accessibilityUrl from journey config" in {
      doc.getAccessibilityLink mustBe testAccessibilityUrl
    }
  }


}
