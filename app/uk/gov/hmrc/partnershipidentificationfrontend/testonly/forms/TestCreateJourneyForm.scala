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

package uk.gov.hmrc.partnershipidentificationfrontend.testonly.forms

import play.api.data.Form
import play.api.data.Forms.{boolean, mapping, text}
import play.api.data.validation.Constraint
import uk.gov.hmrc.partnershipidentificationfrontend.forms.utils.MappingUtil.optText
import uk.gov.hmrc.partnershipidentificationfrontend.forms.utils.ValidationHelper.validate
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.PartnershipType
import uk.gov.hmrc.partnershipidentificationfrontend.models.{JourneyConfig, PageConfig}


object TestCreateJourneyForm {

  val businessVerificationCheck = "businessVerificationCheck"
  val continueUrl = "continueUrl"
  val serviceName = "serviceName"
  val deskProServiceId = "deskProServiceId"
  val alphanumericRegex = "^[A-Z0-9]*$"
  val signOutUrl = "signOutUrl"
  val accessibilityUrl = "accessibilityUrl"

  def form(partnershipType: PartnershipType): Form[JourneyConfig] = {
    Form(mapping(
      continueUrl -> text.verifying(continueUrlEmpty),
      serviceName -> optText,
      businessVerificationCheck -> boolean,
      deskProServiceId -> text.verifying(deskProServiceIdEmpty),
      signOutUrl -> text.verifying(signOutUrlEmpty),
      accessibilityUrl -> text.verifying(accessibilityUrlEmpty)
    )((continueUrl, serviceName, businessVerificationCheck, deskProServiceId, signOutUrl, accessibilityUrl) =>
      JourneyConfig(
        continueUrl,
        businessVerificationCheck,
        pageConfig = PageConfig(serviceName, deskProServiceId, signOutUrl, accessibilityUrl),
        partnershipType = partnershipType
      ))(journeyConfig =>
      Some(
        journeyConfig.continueUrl,
        journeyConfig.pageConfig.optServiceName,
        journeyConfig.businessVerificationCheck,
        journeyConfig.pageConfig.deskProServiceId,
        journeyConfig.pageConfig.signOutUrl,
        journeyConfig.pageConfig.accessibilityUrl
      )))
  }

  def continueUrlEmpty: Constraint[String] = Constraint("continue_url.not_entered")(
    continueUrl => validate(
      constraint = continueUrl.isEmpty,
      errMsg = "Continue URL not entered"
    )
  )

  def deskProServiceIdEmpty: Constraint[String] = Constraint("desk_pro_service_id.not_entered")(
    serviceId => validate(
      constraint = serviceId.isEmpty,
      errMsg = "DeskPro Service Identifier is not entered"
    )
  )

  def signOutUrlEmpty: Constraint[String] = Constraint("sign_out_url.not_entered")(
    signOutUrl => validate(
      constraint = signOutUrl.isEmpty,
      errMsg = "Sign Out Url is not entered"
    )
  )

  def accessibilityUrlEmpty: Constraint[String] = Constraint("accessibility_url.not_entered")(
    accessibilityUrl => validate(
      constraint = accessibilityUrl.isEmpty,
      errMsg = "Accessibility Url is not entered"
    )
  )

}
