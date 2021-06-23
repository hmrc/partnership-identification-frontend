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

package uk.gov.hmrc.partnershipidentificationfrontend.models

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, OFormat, OWrites, Reads}

case class ValidatePartnershipInformationModel(postcode: String, sautr: String)

object ValidatePartnershipInformationModel {

  private val SautrKey = "sautr"
  private val PostcodeKey = "postcode"

  val reads: Reads[ValidatePartnershipInformationModel] = (
    (JsPath \ PostcodeKey).read[String] and
      (JsPath \ SautrKey).read[String]
    ) (ValidatePartnershipInformationModel.apply _)

  val writes: OWrites[ValidatePartnershipInformationModel] = (
    (JsPath \ PostcodeKey).write[String] and
      (JsPath \ SautrKey).write[String]
    ) (unlift(ValidatePartnershipInformationModel.unapply))

  implicit val format: OFormat[ValidatePartnershipInformationModel] = OFormat(reads, writes)

}