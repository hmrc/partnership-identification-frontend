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

import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse, InternalServerException}
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipFullJourneyData

object RetrievePartnershipFullJourneyDataHttpParser {

  implicit object RetrievePartnershipFullJourneyDataHttpReads extends HttpReads[Option[PartnershipFullJourneyData]] {
    override def read(method: String, url: String, response: HttpResponse): Option[PartnershipFullJourneyData] = {
      response.status match {
        case OK =>
          response.json.validate[PartnershipFullJourneyData] match {
            case JsSuccess(partnershipDetails, _) => Some(partnershipDetails)
            case JsError(errors) =>
              throw new InternalServerException(s"`Failed to read Partnerhsip Journey Data with the following error/s: $errors")
          }
        case NOT_FOUND =>
          None
        case status =>
          throw new InternalServerException(s"Unexpected status from Partnership Journey Data retrieval. Status returned - $status")
      }
    }
  }
}
