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

import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl.idFunctor
import uk.gov.hmrc.play.bootstrap.binders.{AbsoluteWithHostnameFromAllowlist, OnlyRelative, RedirectUrl}

import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.models.JourneyConfig

import javax.inject.{Inject, Singleton}

@Singleton
class UrlHelper @Inject()(appConfig: AppConfig) {

  def withUseServiceNavigation(url: String): String = {
    val (urlWithoutFragment, fragment) = url.span(_ != '#')
    val queryStartIndex = urlWithoutFragment.indexOf('?')
    val queryParameters =
      if (queryStartIndex < 0) Seq.empty
      else urlWithoutFragment.substring(queryStartIndex + 1).split('&').toSeq

    if (queryParameters.exists(_.takeWhile(_ != '=') == "useServiceNavigation")) {
      url
    } else {
      val separator =
        if (queryStartIndex < 0) "?"
        else if (urlWithoutFragment.endsWith("?") || urlWithoutFragment.endsWith("&")) ""
        else "&"

      s"$urlWithoutFragment${separator}useServiceNavigation$fragment"
    }
  }

  def containsRelativeOrAcceptedUrlsOnly(journeyConfig: JourneyConfig): Boolean = {
    areRelativeOrAcceptedUrls(
      journeyConfig.continueUrl, journeyConfig.pageConfig.signOutUrl, journeyConfig.pageConfig.accessibilityUrl
    )
  }

  private def areRelativeOrAcceptedUrls(urls: String*): Boolean = {
    val allowedUrls = urls.map(url => isRelativeOrAcceptedUrl(url))

    !allowedUrls.contains(false)
  }

  private def isRelativeOrAcceptedUrl(url: String): Boolean = {
    try {
      RedirectUrl(url).getEither(OnlyRelative | AbsoluteWithHostnameFromAllowlist(appConfig.allowedHosts)) match {
          case Right(_) => true
          case Left(_) => false
        }
      } catch {
        case _: IllegalArgumentException => false
      }
  }

}
