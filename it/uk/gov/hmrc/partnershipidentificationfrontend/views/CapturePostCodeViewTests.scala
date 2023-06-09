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

package uk.gov.hmrc.partnershipidentificationfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.libs.ws.WSResponse
import uk.gov.hmrc.partnershipidentificationfrontend.assets.MessageLookup.CapturePostCode.pageConfigTestTitle
import uk.gov.hmrc.partnershipidentificationfrontend.assets.MessageLookup.{Base, BetaBanner, Header, CapturePostCode => messages}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ViewSpecHelper.ElementExtensions


trait CapturePostCodeViewTests {
  this: ComponentSpecHelper =>

  def testCapturePostCodeView(result: => WSResponse, serviceName: String = testDefaultServiceName, hasErrors: Boolean = false): Unit = {

    lazy val doc: Document = Jsoup.parse(result.body)

    lazy val config = app.injector.instanceOf[AppConfig]

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

    "correctly display the service name" in {
      doc.getServiceName.text mustBe serviceName
    }

    "have the correct title" in {
      if (hasErrors)
        doc.title mustBe Base.Error.error + messages.title
      else if (serviceName == "Test Service")
        doc.title mustBe pageConfigTestTitle
      else
        doc.title mustBe messages.title
    }

    "have the correct heading" in {
      doc.getH1Elements.text mustBe messages.heading
    }

    "have the correct hint text" in {
      doc.getPostCodeHintText mustBe messages.hint
    }

    "have the correct subtitle" in {
      doc.getSubTitle mustBe messages.subTitle
    }

    "have a save and continue button" in {
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

  def testCapturePostCodeViewWithNoPostCodeErrorMessages(result: => WSResponse): Unit = {

    testCapturePostCodeView(result, hasErrors = true)

    lazy val doc: Document = Jsoup.parse(result.body)

    "correctly display the error summary" in {
      doc.getErrorSummaryTitle.text mustBe Base.Error.title
      doc.getErrorSummaryBody.text mustBe messages.Error.noPostCodeEntered
    }

    "correctly display the field errors" in {
      doc.getFieldErrorMessage.text mustBe Base.Error.error + messages.Error.noPostCodeEntered
    }
  }

  def testCapturePostCodeViewWithInvalidPostCodeErrorMessages(result: => WSResponse): Unit = {

    testCapturePostCodeView(result, hasErrors = true)

    lazy val doc: Document = Jsoup.parse(result.body)

    "correctly display the error summary" in {
      doc.getErrorSummaryTitle.text mustBe Base.Error.title
      doc.getErrorSummaryBody.text mustBe messages.Error.invalidPostCodeEntered
    }

    "correctly display the field errors" in {
      doc.getFieldErrorMessage.text mustBe Base.Error.error + messages.Error.invalidPostCodeEntered
    }
  }

}
