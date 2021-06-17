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

import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.ValidatePartnershipInformationHttpParser._
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipInformation

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ValidatePartnershipInformationConnector @Inject()(http: HttpClient,
                                                        appConfig: AppConfig) {

  def validate(partnershipInformation: PartnershipInformation)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] =
    http.POST[PartnershipInformation, Boolean](
      url = appConfig.validatePartnershipInformationUrl,
      body = partnershipInformation
    )

}
