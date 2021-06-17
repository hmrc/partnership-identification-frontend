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

package uk.gov.hmrc.partnershipidentificationfrontend.httpparsers

import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse, InternalServerException}

object ValidatePartnershipInformationHttpParser {

  implicit object RemovePartnershipDetailsHttpReads extends HttpReads[Boolean] {
    override def read(method: String, url: String, response: HttpResponse): Boolean = {
      response.status match {
        case OK =>
          (response.json \ "identifiersMatch").validate[Boolean] match {
            case JsSuccess(identifiersMatch, _) =>
              identifiersMatch
            case JsError(errors) =>
              throw new InternalServerException(s"Validate Partnership Information returned invalid JSON - errors: $errors")
          }
        case status =>
          throw new InternalServerException(s"Partnership Information could not be validated. status: $status, body: ${response.body}")
      }
    }
  }

}
