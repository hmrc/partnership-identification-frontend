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

package uk.gov.hmrc.partnershipidentificationfrontend.testonly.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.models.JourneyConfig
import uk.gov.hmrc.partnershipidentificationfrontend.testonly.connectors.TestCreateJourneyConnector
import uk.gov.hmrc.partnershipidentificationfrontend.testonly.forms.TestCreateJourneyForm.form
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.partnershipidentificationfrontend.testonly.controllers.routes
import uk.gov.hmrc.partnershipidentificationfrontend.testonly.views.html.test_create_journey

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestCreateJourneyController @Inject()(messagesControllerComponents: MessagesControllerComponents,
                                            testCreateJourneyConnector: TestCreateJourneyConnector,
                                            view: test_create_journey,
                                            val authConnector: AuthConnector
                                           )(implicit ec: ExecutionContext,
                                             appConfig: AppConfig) extends FrontendController(messagesControllerComponents) with AuthorisedFunctions {

  private val defaultJourneyConfig = JourneyConfig(
    continueUrl = s"${appConfig.selfUrl}/identify-your-partnership/test-only/retrieve-journey"
  )

  val show: Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        Future.successful(
          Ok(view(routes.TestCreateJourneyController.submit(), form.fill(defaultJourneyConfig)))
        )
      }
  }

  val submit: Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(
              BadRequest(view(routes.TestCreateJourneyController.submit(), formWithErrors))
            ),
          journeyConfig =>
            testCreateJourneyConnector.createJourney(journeyConfig)
              .map(journeyUrl => SeeOther(journeyUrl))
        )
      }
  }
}
