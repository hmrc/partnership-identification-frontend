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
import uk.gov.hmrc.partnershipidentificationfrontend.models.{BusinessVerificationUnchallenged, PartnershipInformation, SaInformation}
import uk.gov.hmrc.partnershipidentificationfrontend.service.{JourneyService, PartnershipIdentificationService, ValidatePartnershipInformationService}
import uk.gov.hmrc.partnershipidentificationfrontend.views.html.check_your_answers_page
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CheckYourAnswersController @Inject()(mcc: MessagesControllerComponents,
                                           view: check_your_answers_page,
                                           val authConnector: AuthConnector,
                                           journeyService: JourneyService,
                                           partnershipInformationService: PartnershipIdentificationService,
                                           validatePartnershipInformationService: ValidatePartnershipInformationService
                                          )(implicit val config: AppConfig, executionContext: ExecutionContext)
  extends FrontendController(mcc) with AuthorisedFunctions {

  def show(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
          journeyService.getJourneyConfig(journeyId, authInternalId).flatMap {
            journeyConfig =>
              partnershipInformationService.retrievePartnershipInformation(journeyId).map {
                case Some(partnershipInformation) => Ok(view(
                  journeyId,
                  journeyConfig.pageConfig,
                  routes.CheckYourAnswersController.submit(journeyId),
                  partnershipInformation
                ))
                case _ => throw new InternalServerException(s"No data stored for journeyId: $journeyId")
              }
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
              partnershipInformationService.retrievePartnershipInformation(journeyId).flatMap {
                case Some(PartnershipInformation(Some(SaInformation(sautr, postcode)))) =>
                  validatePartnershipInformationService.validate(sautr, postcode).flatMap {
                    validatePartnershipResponse =>
                      if (validatePartnershipResponse) {
                        partnershipInformationService.storeIdentifiersMatch(journeyId, validatePartnershipResponse).map {
                          _ => Redirect(routes.BusinessVerificationController.startBusinessVerificationJourney(journeyId))
                        }
                      }
                      else {
                        for {
                          _ <- partnershipInformationService.storeIdentifiersMatch(journeyId, validatePartnershipResponse)
                          _ <- partnershipInformationService.storeBusinessVerificationStatus(journeyId, BusinessVerificationUnchallenged)
                        } yield
                          Redirect(journeyConfig.continueUrl + s"?journeyId=$journeyId")
                      }
                  }
                case Some(PartnershipInformation(None)) =>
                  partnershipInformationService.storeIdentifiersMatch(journeyId, identifiersMatch = false).map {
                    _ => Redirect(journeyConfig.continueUrl + s"?journeyId=$journeyId")
                  }
                case _ =>
                  throw new InternalServerException(s"No data stored for journeyId: $journeyId")
              }
          }
        case _ =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

}