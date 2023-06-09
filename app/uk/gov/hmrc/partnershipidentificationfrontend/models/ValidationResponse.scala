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

package uk.gov.hmrc.partnershipidentificationfrontend.models

import play.api.libs.json._

sealed trait ValidationResponse

case object IdentifiersMatched extends ValidationResponse

case object IdentifiersMismatch extends ValidationResponse

case object UnMatchable extends ValidationResponse

object ValidationResponse {

  val IdentifiersMatchKey: String = "identifiersMatch"
  val IdentifiersMatchedKey: String = "IdentifiersMatched"

  private val IdentifiersMismatchKey: String = "IdentifiersMismatch"
  private val UnMatchableKey: String = "UnMatchable"

  implicit val format: Format[ValidationResponse] = new Format[ValidationResponse] {

    override def reads(jsValue: JsValue): JsResult[ValidationResponse] = jsValue.validate[String] match {
      case JsSuccess(validationResponseAsString, _) => validationResponseAsString match {
        case IdentifiersMatchedKey => JsSuccess(IdentifiersMatched)
        case IdentifiersMismatchKey => JsSuccess(IdentifiersMismatch)
        case UnMatchableKey => JsSuccess(UnMatchable)
        case _ => notSupportedJsError(jsValue)
      }
      case JsError(_) => notSupportedJsError(jsValue)
    }

    override def writes(validationResponse: ValidationResponse): JsValue = {
      val validationResponseAsString: String = validationResponse match {
        case IdentifiersMatched => IdentifiersMatchedKey
        case IdentifiersMismatch => IdentifiersMismatchKey
        case UnMatchable => UnMatchableKey
      }

      JsString(validationResponseAsString)
    }

  }

  private def notSupportedJsError(jsValue: JsValue): JsError = JsError(s"$jsValue not supported as ValidationResponse")
}
