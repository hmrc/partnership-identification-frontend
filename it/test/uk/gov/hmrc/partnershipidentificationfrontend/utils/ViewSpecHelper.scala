/*
 * Copyright 2024 HM Revenue & Customs
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

package uk.gov.hmrc.partnershipidentificationfrontend.utils

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.scalatest.matchers.{HavePropertyMatchResult, HavePropertyMatcher}

import scala.jdk.CollectionConverters._

object ViewSpecHelper {

  implicit class ElementExtensions(element: Element) {

    lazy val content: Element = element.getElementsByTag("article").iterator().asScala.toList.head

    lazy val getParagraphs: Elements = element.getElementsByTag("p")

    lazy val getBulletPoints: Elements = element.getElementsByTag("li")

    lazy val getH1Elements: Elements = element.getElementsByTag("h1")

    lazy val getH2Elements: Elements = element.getElementsByTag("h2")

    lazy val getFormElements: Elements = element.getElementsByClass("form-field-group")

    lazy val getLabelElement: Elements = element.getElementsByTag("label")

    lazy val getLegendElement: Elements = element.getElementsByTag("legend")

    lazy val getErrorSummaryTitle: Elements = element.getElementsByClass("govuk-error-summary__title")

    lazy val getErrorSummaryBody: Elements = element.getElementsByClass("govuk-error-summary__body")

    lazy val getFieldErrorMessage: Elements = element.getElementsByClass("govuk-error-message")

    lazy val getSubmitButton: Elements = element.getElementsByClass("govuk-button")

    lazy val getHintText: String = element.getElementsByClass("govuk-hint").text()

    lazy val getPostCodeHintText: String = element.select("p[class=govuk-hint]").text()

    lazy val getSubTitle: String = element.select("p[class=govuk-body]").text()

    lazy val getForm: Elements = element.select("form")

    lazy val getSummaryListRows: Elements = element.getElementsByClass("govuk-summary-list__row")

    lazy val getServiceName: Elements = element.getElementsByClass("govuk-header__service-name")

    def getSpan(id: String): Elements = element.select(s"""span[id=$id]""")

    def getLink(id: String): Elements = element.select(s"""a[id=$id]""")

    def getTextFieldInput(id: String): Elements = element.select(s"""input[id=$id]""")

    def getBulletPointList: Elements = element.select("ul[class=list list-bullet]")

    def getSummaryListQuestion: String = element.getElementsByClass("govuk-summary-list__key").text

    def getSummaryListAnswer: String = element.getElementsByClass("govuk-summary-list__value").text

    def getSummaryListChangeLink: String = element.select("dd.govuk-summary-list__actions > a").attr("href")

    def getSummaryListChangeText: String = element.select("dd.govuk-summary-list__actions > a").text

    def getBanner: Elements = element.getElementsByClass("govuk-phase-banner__text")

    def getFooters: Elements = element.select("ul[class=govuk-footer__inline-list] > li > a")

    lazy val getSignOutLink: String = element.select(".hmrc-sign-out-nav__link").attr("href")

    lazy val getSignOutText: String = element.select(".hmrc-sign-out-nav__link").text

    lazy val getBannerLink: String = getBanner.select(".govuk-link").attr("href")

    lazy val getBackLinkText: String = element.select(".govuk-back-link").text

    lazy val getAccessibilityLink: String = element.getFooters.get(1).attr("href")

    lazy val getTechnicalHelpLink: String = element.getElementsByClass("hmrc-report-technical-issue").attr("href")

    lazy val getTechnicalHelpLinkText: String = element.getElementsByClass("hmrc-report-technical-issue").text

    lazy val getSearchCompaniesHouse: Elements = element.select(".govuk-body > a")
  }

  def text(text: String): HavePropertyMatcher[Elements, String] =
    (element: Elements) => HavePropertyMatchResult(
      element.text() == text,
      "text",
      text,
      element.text()
    )

}

