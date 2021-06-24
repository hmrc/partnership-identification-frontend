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
import play.api.libs.json._

case class PartnershipFullJourneyData(postcode: String,
                                      optSautr: Option[String],
                                      identifiersMatch: Boolean,
                                      businessVerification: BusinessVerificationStatus)

object PartnershipFullJourneyData {

  private val SautrKey = "sautr"
  private val PostcodeKey = "postcode"
  private val IdentifiersMatchKey = "identifiersMatch"
  private val BusinessVerificationKey = "businessVerification"

  val reads: Reads[PartnershipFullJourneyData] = (
    (JsPath \ PostcodeKey).read[String] and
      (JsPath \ SautrKey).readNullable[String] and
      (JsPath \ IdentifiersMatchKey).read[Boolean] and
      (JsPath \ BusinessVerificationKey).read[BusinessVerificationStatus]
    ) (PartnershipFullJourneyData.apply _)

  val writes: OWrites[PartnershipFullJourneyData] = (
    (JsPath \ PostcodeKey).write[String] and
      (JsPath \ SautrKey).writeNullable[String] and
      (JsPath \ IdentifiersMatchKey).write[Boolean] and
      (JsPath \ BusinessVerificationKey).write[BusinessVerificationStatus]
    ) (unlift(PartnershipFullJourneyData.unapply))

  implicit val format: OFormat[PartnershipFullJourneyData] = OFormat(reads, writes)

}