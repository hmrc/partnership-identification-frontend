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

package uk.gov.hmrc.partnershipidentificationfrontend.connectors

import play.api.http.Status.OK
import play.api.libs.json.{JsObject, Json, Writes}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse, InternalServerException}
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.RegistrationHttpParser.RegistrationHttpReads
import uk.gov.hmrc.partnershipidentificationfrontend.models.RegistrationStatus

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class RegistrationConnector @Inject()(httpClient: HttpClient,
                                      appConfig: AppConfig
                                     )(implicit ec: ExecutionContext) {

  def register(sautr: String)(implicit hc: HeaderCarrier): Future[RegistrationStatus] = {

    val jsonBody = Json.obj(
      "ordinaryPartnership" -> Json.obj(
        "sautr" -> sautr
      )
    )

    httpClient.POST[JsObject, RegistrationStatus](appConfig.registerUrl, jsonBody)(
      implicitly[Writes[JsObject]],
      RegistrationHttpReads,
      hc,
      ec
    )
  }

}

object RegistrationHttpParser {
  val registrationKey = "registration"

  implicit object RegistrationHttpReads extends HttpReads[RegistrationStatus] {
    override def read(method: String, url: String, response: HttpResponse): RegistrationStatus = {
      response.status match {
        case OK =>
          (response.json \ registrationKey).as[RegistrationStatus]
        case _ =>
          throw new InternalServerException(s"Unexpected response from Register API - status = ${response.status}, body = ${response.body}")
      }
    }
  }

}