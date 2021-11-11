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

package uk.gov.hmrc.partnershipidentificationfrontend.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.internalId
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.forms.CaptureCompanyNumberForm
import uk.gov.hmrc.partnershipidentificationfrontend.service.{CompanyProfileService, JourneyService}
import uk.gov.hmrc.partnershipidentificationfrontend.views.html.capture_company_number_page
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CaptureCompanyNumberController @Inject()(mcc: MessagesControllerComponents,
                                               journeyService: JourneyService,
                                               view: capture_company_number_page,
                                               val authConnector: AuthConnector,
                                               companyProfileService: CompanyProfileService
                                              )(implicit val config: AppConfig,
                                                ec: ExecutionContext) extends FrontendController(mcc) with AuthorisedFunctions {

  def show(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
          journeyService.getJourneyConfig(journeyId, authInternalId).map {
            journeyConfig =>
              Ok(view(journeyConfig.pageConfig, routes.CaptureCompanyNumberController.submit(journeyId), CaptureCompanyNumberForm.form))
          }
        case None =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

  def submit(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
          CaptureCompanyNumberForm.form.bindFromRequest().fold(
            formWithErrors => {
              journeyService.getJourneyConfig(journeyId, authInternalId).map {
                journeyConfig =>
                  BadRequest(view(journeyConfig.pageConfig, routes.CaptureCompanyNumberController.submit(journeyId), formWithErrors))
              }
            },
            companyNumber =>
              companyProfileService.retrieveAndStoreCompanyProfile(journeyId, companyNumber).map {
                case Some(_) =>
                  Redirect(routes.ConfirmPartnershipNameController.show(journeyId))
                case None =>
                  NotImplemented //TODO Redirect to error page
              }
          )
        case None =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

}
