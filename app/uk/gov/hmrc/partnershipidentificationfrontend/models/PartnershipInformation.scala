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

import play.api.libs.json._

case class PartnershipInformation(optSaInformation: Option[SaInformation])

case class SaInformation(sautr: String, postcode: String)

object PartnershipInformation {

  private val SautrKey = "sautr"
  private val PostcodeKey = "postcode"

  implicit val format: OFormat[PartnershipInformation] = new OFormat[PartnershipInformation] {
    override def writes(partnershipInformation: PartnershipInformation): JsObject =
      partnershipInformation.optSaInformation match {
        case Some(SaInformation(sautr, postcode)) =>
          Json.obj(
            SautrKey -> sautr,
            PostcodeKey -> postcode
          )
        case None =>
          Json.obj()
      }

    override def reads(json: JsValue): JsResult[PartnershipInformation] = for {
      optSautr <- (json \ SautrKey).validateOpt[String]
      optPostcode <- (json \ PostcodeKey).validateOpt[String]
    } yield {
      val saInformation = for {
        sautr <- optSautr
        postcode <- optPostcode
      } yield SaInformation(sautr, postcode)

      PartnershipInformation(saInformation)
    }
  }
}