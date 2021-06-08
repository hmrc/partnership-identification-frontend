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

package uk.gov.hmrc.partnershipidentificationfrontend.service

import javax.inject.{Inject, Singleton}
import play.api.libs.json.JsString
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.PartnershipIdentificationConnector
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.RemovePartnershipDetailsHttpParser.SuccessfullyRemoved
import uk.gov.hmrc.partnershipidentificationfrontend.service.IncorporatedEntityInformationService.SautrKey

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PartnershipInformationService @Inject()(connector: PartnershipIdentificationConnector
                                                    )(implicit ec: ExecutionContext) {

  def storeSautr(journeyId: String,
                 sautr: String
                )(implicit hc: HeaderCarrier): Future[SuccessfullyStored.type] =
    connector.storeData[String](journeyId, SautrKey, sautr)

  def retrieveSautr(journeyId: String)(implicit hc: HeaderCarrier): Future[Option[String]] =
    connector.retrievePartnershipInformation[JsString](journeyId, SautrKey).map {
      case Some(jsString) => Some(jsString.value)
      case None => None
    }

  def removeSautr(journeyId: String)(implicit hc: HeaderCarrier): Future[SuccessfullyRemoved.type] =
    connector.removePartnershipInformation(journeyId, SautrKey)
}


object IncorporatedEntityInformationService {
  val SautrKey: String = "sautr"
}