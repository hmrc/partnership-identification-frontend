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

package uk.gov.hmrc.partnershipidentificationfrontend.repositories

import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.JsObjectDocumentWriter
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.models.JourneyConfig
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType._
import uk.gov.hmrc.partnershipidentificationfrontend.repositories.JourneyConfigRepository._

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class JourneyConfigRepository @Inject()(reactiveMongoComponent: ReactiveMongoComponent,
                                        appConfig: AppConfig)
                                       (implicit ec: ExecutionContext) extends ReactiveRepository[JourneyConfig, String](
  collectionName = "partnership-identification-frontend",
  mongo = reactiveMongoComponent.mongoConnector.db,
  domainFormat = journeyConfigMongoFormat,
  idFormat = implicitly[Format[String]]
) {

  def insertJourneyConfig(journeyId: String, authInternalId: String, journeyConfig: JourneyConfig): Future[WriteResult] =
    collection.insert(ordered = true).one(
      Json.obj(
        JourneyIdKey -> journeyId,
        AuthInternalIdKey -> authInternalId,
        CreationTimestampKey -> Json.obj("$date" -> Instant.now.toEpochMilli)
      ) ++ Json.toJsObject(journeyConfig)
    )

  def findJourneyConfig(journeyId: String, authInternalId: String): Future[Option[JourneyConfig]] =
    collection.find(
      Json.obj(
        JourneyIdKey -> journeyId,
        AuthInternalIdKey -> authInternalId
      ),
      Some(Json.obj(
        JourneyIdKey -> 0,
        AuthInternalIdKey -> 0
      ))
    ).one[JourneyConfig]

  private lazy val ttlIndex = Index(
    Seq((CreationTimestampKey, IndexType.Ascending)),
    name = Some("PartnershipInformationExpires"),
    options = BSONDocument("expireAfterSeconds" -> appConfig.timeToLiveSeconds)
  )

  private def setIndex(): Unit = {
    collection.indexesManager.drop(ttlIndex.name.get) onComplete {
      _ => collection.indexesManager.ensure(ttlIndex)
    }
  }

  setIndex()

  override def drop(implicit ec: ExecutionContext): Future[Boolean] =
    collection.drop(failIfNotFound = false).map { r =>
      setIndex()
      r
    }

}

object JourneyConfigRepository {
  val JourneyIdKey = "_id"
  val AuthInternalIdKey = "authInternalId"
  val CreationTimestampKey = "creationTimestamp"
  val BusinessEntityKey = "businessEntity"
  val GeneralPartnershipKey = "generalPartnership"
  val ScottishPartnershipKey = "scottishPartnership"
  val ScottishLimitedPartnershipKey = "scottishLimitedPartnership"
  val LimitedPartnershipKey = "limitedPartnership"
  val LimitedLiabilityPartnershipKey = "limitedLiabilityPartnership"

  implicit val partnershipTypeMongoFormat: Format[PartnershipType] = new Format[PartnershipType] {
    override def reads(json: JsValue): JsResult[PartnershipType] = json.validate[String].collect(JsonValidationError("Invalid partnership type")) {
      case GeneralPartnershipKey => GeneralPartnership
      case ScottishPartnershipKey => ScottishPartnership
      case ScottishLimitedPartnershipKey => ScottishLimitedPartnership
      case LimitedPartnershipKey => LimitedPartnership
      case LimitedLiabilityPartnershipKey => LimitedLiabilityPartnership
    }

    override def writes(partnershipType: PartnershipType): JsValue = partnershipType match {
      case GeneralPartnership => JsString(GeneralPartnershipKey)
      case ScottishPartnership => JsString(ScottishPartnershipKey)
      case ScottishLimitedPartnership => JsString(ScottishLimitedPartnershipKey)
      case LimitedPartnership => JsString(LimitedPartnershipKey)
      case LimitedLiabilityPartnership => JsString(LimitedLiabilityPartnershipKey)
    }
  }
  implicit val journeyConfigMongoFormat: OFormat[JourneyConfig] = Json.format[JourneyConfig]
}