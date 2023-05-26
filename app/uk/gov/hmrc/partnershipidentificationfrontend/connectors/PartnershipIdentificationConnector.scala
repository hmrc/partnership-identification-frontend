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

package uk.gov.hmrc.partnershipidentificationfrontend.connectors

import play.api.libs.json.{Reads, Writes}
import uk.gov.hmrc.http._
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.RemovePartnershipDetailsHttpParser.{RemovePartnershipDetailsHttpReads, SuccessfullyRemoved}
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.RetrievePartnershipFullJourneyDataHttpParser.RetrievePartnershipFullJourneyDataHttpReads
import uk.gov.hmrc.partnershipidentificationfrontend.models.{PartnershipFullJourneyData, PartnershipInformation}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PartnershipIdentificationConnector @Inject()(http: HttpClient,
                                                   appConfig: AppConfig
                                                  )(implicit ec: ExecutionContext) extends HttpReadsInstances {

  def retrievePartnershipInformation[DataType](journeyId: String,
                                               dataKey: String
                                              )(implicit dataTypeReads: Reads[DataType],
                                                manifest: Manifest[DataType],
                                                hc: HeaderCarrier): Future[Option[DataType]] =
    http.GET[Option[DataType]](s"${appConfig.partnershipInformationUrl(journeyId)}/$dataKey")

  def retrievePartnershipInformation(journeyId: String
                                    )(implicit hc: HeaderCarrier): Future[Option[PartnershipInformation]] =
    http.GET[Option[PartnershipInformation]](appConfig.partnershipInformationUrl(journeyId))

  def retrievePartnershipFullJourneyData(journeyId: String
                                        )(implicit hc: HeaderCarrier): Future[Option[PartnershipFullJourneyData]] =
    http.GET[Option[PartnershipFullJourneyData]](appConfig.partnershipInformationUrl(journeyId))(RetrievePartnershipFullJourneyDataHttpReads, hc, ec)

  def storeData[DataType](journeyId: String, dataKey: String, data: DataType
                         )(implicit dataTypeWriter: Writes[DataType], hc: HeaderCarrier): Future[SuccessfullyStored.type] = {
    http.PUT[DataType, SuccessfullyStored.type](s"${appConfig.partnershipInformationUrl(journeyId)}/$dataKey", data)
  }

  def removePartnershipInformation(journeyId: String,
                                   dataKey: String
                                  )(implicit hc: HeaderCarrier): Future[SuccessfullyRemoved.type] =
    http.DELETE[SuccessfullyRemoved.type](s"${appConfig.partnershipInformationUrl(journeyId)}/$dataKey")(RemovePartnershipDetailsHttpReads, hc, ec)

}
