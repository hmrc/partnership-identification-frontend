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

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.PartnershipIdentificationConnector
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.RemovePartnershipDetailsHttpParser.SuccessfullyRemoved
import uk.gov.hmrc.partnershipidentificationfrontend.service.PartnershipInformationService._

import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class PartnershipInformationService @Inject()(connector: PartnershipIdentificationConnector) {

  def storeSautr(journeyId: String,
                 sautr: String
                )(implicit hc: HeaderCarrier): Future[SuccessfullyStored.type] =
    connector.storeData[String](journeyId, SautrKey, sautr)

  def storePostCode(journeyId: String,
                    postCode: String
                   )(implicit hc: HeaderCarrier): Future[SuccessfullyStored.type] =
    connector.storeData[String](journeyId, PostCodeKey, postCode)

  def retrieveSautr(journeyId: String)(implicit hc: HeaderCarrier): Future[Option[String]] =
    connector.retrievePartnershipInformation[String](journeyId, SautrKey)

  def removeSautr(journeyId: String)(implicit hc: HeaderCarrier): Future[SuccessfullyRemoved.type] =
    connector.removePartnershipInformation(journeyId, SautrKey)
}


object PartnershipInformationService {
  val SautrKey: String = "sautr"
  val PostCodeKey: String = "postcode"
}