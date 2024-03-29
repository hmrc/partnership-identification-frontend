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

package uk.gov.hmrc.partnershipidentificationfrontend.controllers

import play.api.i18n.Messages
import play.api.mvc._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.internalId
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.errorpages.{routes => errorRoutes}
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.{GeneralPartnership, ScottishPartnership}
import uk.gov.hmrc.partnershipidentificationfrontend.models.{IdentifiersMatched, IdentifiersMismatch, JourneyConfig, UnMatchable, ValidationResponse}
import uk.gov.hmrc.partnershipidentificationfrontend.service.{AuditService, JourneyService, PartnershipIdentificationService, ValidationOrchestrationService}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.MessagesHelper
import uk.gov.hmrc.partnershipidentificationfrontend.views.helpers.CheckYourAnswersListBuilder
import uk.gov.hmrc.partnershipidentificationfrontend.views.html.check_your_answers_page
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject()(mcc: MessagesControllerComponents,
                                           view: check_your_answers_page,
                                           val authConnector: AuthConnector,
                                           journeyService: JourneyService,
                                           partnershipIdentificationService: PartnershipIdentificationService,
                                           validationOrchestrationService: ValidationOrchestrationService,
                                           checkYourAnswersListBuilder: CheckYourAnswersListBuilder,
                                           auditService: AuditService,
                                           messagesHelper: MessagesHelper
                                          )(implicit val config: AppConfig,
                                            executionContext: ExecutionContext) extends FrontendController(mcc) with AuthorisedFunctions {

  def show(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
          journeyService.getJourneyConfig(journeyId, authInternalId).flatMap {
            journeyConfig =>
              partnershipIdentificationService.retrievePartnershipInformation(journeyId).map {
                case Some(partnershipInformation) =>
                  implicit val messages: Messages = messagesHelper.getRemoteMessagesApi(journeyConfig).preferred(request)
                  Ok(view(
                    journeyId,
                    journeyConfig.pageConfig,
                    routes.CheckYourAnswersController.submit(journeyId),
                    checkYourAnswersListBuilder.build(journeyId, partnershipInformation)
                  ))
                case _ =>
                  throw new InternalServerException(s"No data stored for journeyId: $journeyId")
              }
          }
        case _ =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

  def submit(journeyId: String): Action[AnyContent] = Action.async { implicit request =>
    def validationToPages(journeyConfig: JourneyConfig, validation: ValidationResponse): Future[Result] = {
      val isPartnershipGeneralOrScottish = journeyConfig.partnershipType == GeneralPartnership || journeyConfig.partnershipType == ScottishPartnership
      val isRegimeOtherThanVATC = journeyConfig.regime != "VATC"

      validation match {
        case IdentifiersMatched if journeyConfig.businessVerificationCheck =>
          Future.successful(Redirect(routes.BusinessVerificationController.startBusinessVerificationJourney(journeyId)))
        case IdentifiersMatched =>
          Future.successful(Redirect(routes.RegistrationController.register(journeyId)))
        case UnMatchable if isPartnershipGeneralOrScottish =>
          auditService.auditPartnershipInformation(journeyId, journeyConfig).map {
            _ => Redirect(routes.JourneyRedirectController.redirectToContinueUrl(journeyId))
          }
        case UnMatchable | IdentifiersMismatch if isRegimeOtherThanVATC =>
          auditService.auditPartnershipInformation(journeyId, journeyConfig).map {
            _ => Redirect(routes.JourneyRedirectController.redirectToContinueUrl(journeyId))
          }
        case UnMatchable | IdentifiersMismatch =>
          auditService.auditPartnershipInformation(journeyId, journeyConfig).map {
            _ => Redirect(errorRoutes.CannotConfirmBusinessErrorController.show(journeyId))
          }
      }
    }

    authorised().retrieve(internalId) {
      case Some(authInternalId) =>
        for {
          journeyConfig <- journeyService.getJourneyConfig(journeyId, authInternalId)
          validation    <- validationOrchestrationService.orchestrate(journeyId, journeyConfig.businessVerificationCheck)
          response      <- validationToPages(journeyConfig, validation)
        } yield response
      case _ =>
        throw new InternalServerException("Internal ID could not be retrieved from Auth")
    }
  }
}
