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

package uk.gov.hmrc.partnershipidentificationfrontend.controllers.errorpages

import play.api.i18n.Messages
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.internalId
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.forms.CannotConfirmBusinessErrorForm.cannotConfirmBusinessForm
import uk.gov.hmrc.partnershipidentificationfrontend.service.JourneyService
import uk.gov.hmrc.partnershipidentificationfrontend.views.html.errorpages.cannot_confirm_business_error_page
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.{routes => appRoutes}
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType._
import uk.gov.hmrc.partnershipidentificationfrontend.utils.MessagesHelper

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CannotConfirmBusinessErrorController @Inject()(mcc: MessagesControllerComponents,
                                                     view: cannot_confirm_business_error_page,
                                                     val authConnector: AuthConnector,
                                                     journeyService: JourneyService,
                                                     messagesHelper: MessagesHelper
                                                    )(implicit val config: AppConfig,
                                                      executionContext: ExecutionContext) extends FrontendController(mcc) with AuthorisedFunctions {

  def show(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
        journeyService.getJourneyConfig(journeyId, authInternalId).map {
          journeyConfig =>
            implicit val messages: Messages = messagesHelper.getRemoteMessagesApi(journeyConfig).preferred(request)
            Ok(view(
              pageConfig = journeyConfig.pageConfig,
              formAction = routes.CannotConfirmBusinessErrorController.submit(journeyId),
              form = cannotConfirmBusinessForm
            ))
        }
        case None => throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }


  def submit(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
            cannotConfirmBusinessForm.bindFromRequest().fold(
              formWithErrors =>
                journeyService.getJourneyConfig(journeyId, authInternalId).map {
                  journeyConfig =>
                    implicit val messages: Messages = messagesHelper.getRemoteMessagesApi(journeyConfig).preferred(request)
                    BadRequest(view(
                      pageConfig = journeyConfig.pageConfig,
                      formAction = routes.CannotConfirmBusinessErrorController.submit(journeyId),
                      form = formWithErrors
                    ))
                },
              continue =>
                if (continue) {
                  journeyService.getJourneyConfig(journeyId, authInternalId).map {
                    journeyConfig => Redirect(journeyConfig.continueUrl + s"?journeyId=$journeyId")
                  }
                }
                else {
                  journeyService.getJourneyConfig(journeyId, authInternalId).map {
                    journeyConfig => journeyConfig.partnershipType match {
                      case GeneralPartnership | ScottishPartnership =>
                        Redirect(appRoutes.CaptureSautrController.show(journeyId))
                      case _ =>
                        Redirect(appRoutes.CaptureCompanyNumberController.show(journeyId))
                    }
                  }
                }
            )
        case None => throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

}

