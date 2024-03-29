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

package uk.gov.hmrc.partnershipidentificationfrontend.testonly.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipFullJourneyData.jsonWriterForCallingServices
import uk.gov.hmrc.partnershipidentificationfrontend.connectors.PartnershipIdentificationConnector
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class TestRetrieveJourneyDataController @Inject()(messagesControllerComponents: MessagesControllerComponents,
                                                  partnershipIdentificationConnector: PartnershipIdentificationConnector,
                                                  val authConnector: AuthConnector
                                                 )(implicit ec: ExecutionContext) extends FrontendController(messagesControllerComponents) with AuthorisedFunctions {

  def retrievePartnershipDetails(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        partnershipIdentificationConnector.retrievePartnershipFullJourneyData(journeyId).map {
          case Some(journeyData) =>
            Ok(Json.toJson(journeyData)(jsonWriterForCallingServices))
          case None =>
            NotFound
        }
      }
  }

}

