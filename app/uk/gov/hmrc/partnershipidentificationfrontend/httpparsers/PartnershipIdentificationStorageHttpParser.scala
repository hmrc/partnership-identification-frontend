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
import uk.gov.hmrc.http.{HttpReads, HttpResponse, InternalServerException}

object PartnershipIdentificationStorageHttpParser {

  case object SuccessfullyStored

  implicit object PartnershipIdentificationStorageHttpReads extends HttpReads[SuccessfullyStored.type] {
    override def read(method: String, url: String, response: HttpResponse): SuccessfullyStored.type = {
      response.status match {
        case OK =>
          SuccessfullyStored
        case status =>
          throw new InternalServerException(s"Storage in Partnership Identification failed with status: $status")
      }
    }
  }

}