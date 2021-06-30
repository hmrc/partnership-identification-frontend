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

import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException}
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.JourneyConnector
import uk.gov.hmrc.partnershipidentificationfrontend.models.JourneyConfig
import uk.gov.hmrc.partnershipidentificationfrontend.repositories.JourneyConfigRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class JourneyService @Inject()(journeyConnector: JourneyConnector,
                               journeyConfigRepository: JourneyConfigRepository
                              )(implicit ec: ExecutionContext) {

  def createJourney(journeyConfig: JourneyConfig, authInternalId: String)(implicit headerCarrier: HeaderCarrier): Future[String] =
    for {
      journeyId <- journeyConnector.createJourney()
      _ <- journeyConfigRepository.insertJourneyConfig(journeyId, authInternalId, journeyConfig)
    } yield journeyId

  def getJourneyConfig(journeyId: String, authInternalId: String): Future[JourneyConfig] =
    journeyConfigRepository.findJourneyConfig(journeyId, authInternalId).map {
      case Some(journeyConfig) =>
        journeyConfig
      case None =>
        throw new NotFoundException(s"Journey config was not found for journey ID $journeyId")
    }

  def storeBusinessEntity(journeyId: String, authInternalId: String, businessEntity: String): Future[Unit] =
    journeyConfigRepository.upsertBusinessEntity(journeyId, authInternalId, businessEntity)

}
