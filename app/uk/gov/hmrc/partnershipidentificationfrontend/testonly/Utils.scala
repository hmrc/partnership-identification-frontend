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

package uk.gov.hmrc.partnershipidentificationfrontend.testonly

import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.PartnershipType
import uk.gov.hmrc.partnershipidentificationfrontend.models.{JourneyConfig, PageConfig}

object Utils {

  def defaultJourneyConfigFor(appConfig: AppConfig, pageConfig: PageConfig, partnershipType: PartnershipType, regime: String): JourneyConfig = JourneyConfig(
    continueUrl = s"${appConfig.selfUrl}/identify-your-partnership/test-only/retrieve-journey",
    pageConfig = pageConfig,
    businessVerificationCheck = true,
    partnershipType = partnershipType,
    regime = regime
  )

  def defaultPageConfig(appConfig: AppConfig): PageConfig = PageConfig(
    optServiceName = None,
    deskProServiceId = "vrs",
    signOutUrl = appConfig.vatRegFeedbackUrl,
    "/"
  )

}
