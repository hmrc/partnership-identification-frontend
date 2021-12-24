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

package uk.gov.hmrc.partnershipidentificationfrontend.controllers.errorpages

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.internalId
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.{routes => appRoutes}
import uk.gov.hmrc.partnershipidentificationfrontend.service.JourneyService
import uk.gov.hmrc.partnershipidentificationfrontend.views.html.errorpages.company_number_not_found
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CompanyNumberNotFoundController @Inject()(messagesControllerComponents: MessagesControllerComponents,
                                                journeyService: JourneyService,
                                                view: company_number_not_found,
                                                val authConnector: AuthConnector
                                               )(implicit val appConfig: AppConfig, ec: ExecutionContext)
                                               extends FrontendController(messagesControllerComponents) with AuthorisedFunctions {

  def show(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
    authorised().retrieve(internalId) {
      case Some(authInternalId) =>
      journeyService.getJourneyConfig(journeyId, authInternalId).map {
        journeyConfig =>
        Ok(view(journeyConfig.pageConfig, routes.CompanyNumberNotFoundController.submit(journeyId)))
      }
      case None => throw new InternalServerException("Internal ID could not be retrieved from Auth")
    }
  }

  def submit(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
    authorised(){
      Future.successful(Redirect(appRoutes.CaptureCompanyNumberController.show(journeyId)))
    }
  }

}
