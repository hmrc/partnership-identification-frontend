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

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.internalId
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.partnershipidentificationfrontend.service.{AuditService, JourneyService, RegistrationOrchestrationService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class RegistrationController @Inject()(journeyService: JourneyService,
                                       registrationOrchestrationService: RegistrationOrchestrationService,
                                       messagesControllerComponents: MessagesControllerComponents,
                                       auditService: AuditService,
                                       val authConnector: AuthConnector)
                                      (implicit ec: ExecutionContext) extends FrontendController(messagesControllerComponents) with AuthorisedFunctions {

  def register(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) => for {
          journeyConfig <- journeyService.getJourneyConfig(journeyId, authInternalId)
          _ <- registrationOrchestrationService.register(journeyId, journeyConfig.partnershipType, journeyConfig.businessVerificationCheck, journeyConfig.regime)
          _ <- auditService.auditPartnershipInformation(journeyId, journeyConfig)
        } yield Redirect(routes.JourneyRedirectController.redirectToContinueUrl(journeyId))
        case _ =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }
}
