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

package uk.gov.hmrc.partnershipidentificationfrontend.connectors

import play.api.http.Status.OK
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse, InternalServerException}
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.RegistrationHttpParser._
import uk.gov.hmrc.partnershipidentificationfrontend.models.RegistrationStatus

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


object RegistrationHttpParser {
  val registrationKey = "registration"
  val sautrKey = "sautr"
  val companyNumberKey = "companyNumber"

  implicit object RegistrationHttpReads extends HttpReads[RegistrationStatus] {
    override def read(method: String, url: String, response: HttpResponse): RegistrationStatus = {
      response.status match {
        case OK =>
          (response.json \ registrationKey).as[RegistrationStatus](RegistrationStatus.format)
        case _ =>
          throw new InternalServerException(s"Unexpected response from Register API - status = ${response.status}, body = ${response.body}")
      }
    }
  }
}

class RegistrationConnector @Inject()(httpClient: HttpClient,
                                      appConfig: AppConfig
                                     )(implicit ec: ExecutionContext) {
  def registerGeneralPartnership(sautr: String, regime: String)(implicit hc: HeaderCarrier): Future[RegistrationStatus] =
    httpClient.POST[JsObject, RegistrationStatus](
      appConfig.registerGeneralPartnershipUrl,
      Json.obj(
        sautrKey -> sautr,
        "regime" -> regime
      )
    )

  def registerScottishPartnership(sautr: String, regime: String)(implicit hc: HeaderCarrier): Future[RegistrationStatus] =
    httpClient.POST[JsObject, RegistrationStatus](
      appConfig.registerScottishPartnershipUrl,
      Json.obj(
        sautrKey -> sautr,
        "regime" -> regime
      )
    )

  def registerLimitedPartnership(sautr: String, companyNumber: String, regime: String)(implicit hc: HeaderCarrier): Future[RegistrationStatus] =
    httpClient.POST[JsObject, RegistrationStatus](
      appConfig.registerLimitedPartnershipUrl,
      Json.obj(
        sautrKey -> sautr,
        companyNumberKey -> companyNumber,
        "regime" -> regime
      )
    )

  def registerLimitedLiabilityPartnership(sautr: String, companyNumber: String, regime: String)(implicit hc: HeaderCarrier): Future[RegistrationStatus] =
    httpClient.POST[JsObject, RegistrationStatus](
      appConfig.registerLimitedLiabilityPartnershipUrl,
      Json.obj(
        sautrKey -> sautr,
        companyNumberKey -> companyNumber,
        "regime" -> regime
      )
    )

  def registerScottishLimitedPartnership(sautr: String, companyNumber: String, regime: String)(implicit hc: HeaderCarrier): Future[RegistrationStatus] =
    httpClient.POST[JsObject, RegistrationStatus](
      appConfig.registerScottishLimitedPartnershipUrl,
      Json.obj(
        sautrKey -> sautr,
        companyNumberKey -> companyNumber,
        "regime" -> regime
      )
    )

}

