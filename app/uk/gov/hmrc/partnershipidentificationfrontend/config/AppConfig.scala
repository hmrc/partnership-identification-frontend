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

package uk.gov.hmrc.partnershipidentificationfrontend.config

import play.api.Configuration
import uk.gov.hmrc.partnershipidentificationfrontend.featureswitch.core.config.{BusinessVerificationStub, CompaniesHouseStub, FeatureSwitching}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class AppConfig @Inject()(config: Configuration,
                          servicesConfig: ServicesConfig) extends FeatureSwitching {

  lazy val selfBaseUrl: String = servicesConfig.baseUrl("self")
  lazy val selfUrl: String = servicesConfig.getString("microservice.services.self.url")

  lazy val welshLanguageSupportEnabled: Boolean = config.getOptional[Boolean]("features.welsh-language-support").getOrElse(false)

  lazy val timeToLiveSeconds: Long = servicesConfig.getString("mongodb.timeToLiveSeconds").toLong

  private lazy val backendUrl: String = servicesConfig.baseUrl("partnership-identification")
  private lazy val contactHost: String = servicesConfig.getString("contact-frontend.host")

  def createJourneyUrl: String = s"$backendUrl/partnership-identification/journey"

  def partnershipInformationUrl(journeyId: String): String = s"$backendUrl/partnership-identification/journey/$journeyId"

  def validatePartnershipInformationUrl: String = s"$backendUrl/partnership-identification/validate-partnership-information"

  lazy val cookies: String = servicesConfig.getString("urls.footer.cookies")
  lazy val privacy: String = servicesConfig.getString("urls.footer.privacy")
  lazy val termsConditions: String = servicesConfig.getString("urls.footer.termsConditions")
  lazy val govukHelp: String = servicesConfig.getString("urls.footer.govukHelp")
  lazy val companiesHouse: String = servicesConfig.getString("companies-house.url")

  lazy val defaultServiceName: String = servicesConfig.getString("defaultServiceName")

  def betaFeedbackUrl(serviceIdentifier: String): String = s"$contactHost/contact/beta-feedback?service=$serviceIdentifier"

  def reportAProblemPartialUrl(serviceIdentifier: String): String =
    s"$contactHost/contact/problem_reports_ajax?service=$serviceIdentifier"

  def reportAProblemNonJSUrl(serviceIdentifier: String): String =
    s"$contactHost/contact/problem_reports_nonjs?service=$serviceIdentifier"

  private lazy val feedbackUrl: String = servicesConfig.getString("feedback.host")
  lazy val vatRegExitSurveyOrigin = "vat-registration"
  lazy val vatRegFeedbackUrl = s"$feedbackUrl/feedback/$vatRegExitSurveyOrigin"

  lazy val timeout: Int = servicesConfig.getInt("timeout.timeout")
  lazy val countdown: Int = servicesConfig.getInt("timeout.countdown")

  private lazy val businessVerificationUrl = servicesConfig.getString("microservice.services.business-verification.url")

  def createBusinessVerificationJourneyUrl: String = {
    val baseUri = if (isEnabled(BusinessVerificationStub)) {
      s"$selfBaseUrl/identify-your-partnership/test-only/business-verification"
    } else
      businessVerificationUrl

    s"$baseUri/journey"
  }

  def getBusinessVerificationResultUrl(journeyId: String): String = {
    val baseUri = if (isEnabled(BusinessVerificationStub)) {
      s"$selfBaseUrl/identify-your-partnership/test-only/business-verification"
    } else
      businessVerificationUrl

    s"$baseUri/journey/$journeyId/status"
  }

  lazy val registerGeneralPartnershipUrl: String = s"$backendUrl/partnership-identification/register-general-partnership"
  lazy val registerScottishPartnershipUrl: String = s"$backendUrl/partnership-identification/register-scottish-partnership"
  lazy val registerLimitedPartnershipUrl: String = s"$backendUrl/partnership-identification/register-limited-partnership"
  lazy val registerLimitedLiabilityPartnershipUrl: String = s"$backendUrl/partnership-identification/register-limited-liability-partnership"
  lazy val registerScottishLimitedPartnershipUrl: String = s"$backendUrl/partnership-identification/register-scottish-limited-partnership"

  private lazy val incorporationInformationUrl = servicesConfig.baseUrl("incorporation-information")

  def getCompanyProfileUrl(companyNumber: String): String = {
    if (isEnabled(CompaniesHouseStub))
      s"$selfBaseUrl/identify-your-partnership/test-only/$companyNumber/incorporated-company-profile"
    else
      s"$incorporationInformationUrl/incorporation-information/$companyNumber/incorporated-company-profile"
  }

}
