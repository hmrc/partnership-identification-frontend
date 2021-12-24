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

package uk.gov.hmrc.partnershipidentificationfrontend.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.internalId
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.forms.CapturePostCodeForm.postCodeForm
import uk.gov.hmrc.partnershipidentificationfrontend.service.{JourneyService, PartnershipIdentificationService}
import uk.gov.hmrc.partnershipidentificationfrontend.views.html.capture_post_code_page
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CapturePostCodeController @Inject()(mcc: MessagesControllerComponents,
                                          view: capture_post_code_page,
                                          val authConnector: AuthConnector,
                                          journeyService: JourneyService,
                                          partnershipIdentificationService: PartnershipIdentificationService
                                         )(implicit val config: AppConfig,
                                           ec: ExecutionContext) extends FrontendController(mcc) with AuthorisedFunctions {

  def show(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
          journeyService.getJourneyConfig(journeyId, authInternalId).map {
            journeyConfig =>
              Ok(view(journeyConfig.pageConfig, routes.CapturePostCodeController.submit(journeyId), postCodeForm))
          }
        case _ =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

  def submit(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
          journeyService.getJourneyConfig(journeyId, authInternalId).flatMap {
            journeyConfig =>
              postCodeForm.bindFromRequest.fold(
                formWithErrors =>
                  Future.successful(
                    BadRequest(view(journeyConfig.pageConfig, routes.CapturePostCodeController.submit(journeyId), formWithErrors))
                  ),
                postCode =>
                  partnershipIdentificationService.storePostCode(journeyId, postCode).map {
                    _ => Redirect(routes.CheckYourAnswersController.show(journeyId))
                  }
              )
          }
        case _ =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

}
