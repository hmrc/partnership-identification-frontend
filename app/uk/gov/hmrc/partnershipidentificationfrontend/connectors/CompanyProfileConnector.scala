/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.http.Status.{NOT_FOUND, OK}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.CompanyProfileHttpParser._
import uk.gov.hmrc.partnershipidentificationfrontend.models.CompanyProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CompanyProfileConnector @Inject()(http: HttpClientV2,
                                        appConfig: AppConfig
                                       )(implicit ec: ExecutionContext) {

  def getCompanyProfile(companyNumber: String)(implicit hc: HeaderCarrier): Future[Option[CompanyProfile]] =
    http.get(url = url"${appConfig.getCompanyProfileUrl(companyNumber)}")(hc).execute[Option[CompanyProfile]](CompanyProfileHttpReads, ec)

}

object CompanyProfileHttpParser {
  private val companyNameKey = "company_name"
  private val companyNumberKey = "company_number"
  private val dateOfIncorporationKey = "date_of_creation"
  private val registeredOfficeAddressKey = "registered_office_address"

  val companiesHouseReads: Reads[CompanyProfile] = (
    (__ \ companyNameKey).read[String] and
      (__ \ companyNumberKey).read[String] and
      (__ \ dateOfIncorporationKey).read[String] and
      (__ \ registeredOfficeAddressKey).read[JsObject]
    ) (CompanyProfile.apply _)

  implicit object CompanyProfileHttpReads extends HttpReads[Option[CompanyProfile]] {
    override def read(method: String, url: String, response: HttpResponse): Option[CompanyProfile] = {
      response.status match {
        case OK =>
          response.json.validate[CompanyProfile](companiesHouseReads) match {
            case JsSuccess(companyProfile, _) =>
              Some(companyProfile)
            case JsError(errors) =>
              throw new InternalServerException(s"Companies House API returned malformed JSON with errors: $errors")
          }
        case NOT_FOUND =>
          None
        case status =>
          throw new InternalServerException(s"Companies House API failed with status: $status")
      }
    }
  }

}
