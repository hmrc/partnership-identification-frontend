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

package uk.gov.hmrc.partnershipidentificationfrontend.config

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

class AppConfigSpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  private val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  "vatRegAccessibilityStatementUrl" should {
    "provide an absolute local VAT Registration accessibility statement URL" in {
      appConfig.vatRegAccessibilityStatementUrl mustBe
        "http://localhost:12346/accessibility-statement/vat-registration?useServiceNavigation"
    }

    "provide a relative VAT Registration accessibility statement URL in deployed environments" in {
      val configuration = Configuration("relative-urls.enabled" -> true).withFallback(app.configuration)
      val deployedAppConfig = new AppConfig(configuration, new ServicesConfig(configuration))

      deployedAppConfig.vatRegAccessibilityStatementUrl mustBe
        "/accessibility-statement/vat-registration?useServiceNavigation"
    }
  }
}
