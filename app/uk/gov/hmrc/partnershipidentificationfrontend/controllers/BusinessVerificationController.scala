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
import uk.gov.hmrc.partnershipidentificationfrontend.service.{BusinessVerificationService, JourneyService, PartnershipIdentificationService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BusinessVerificationController @Inject()(mcc: MessagesControllerComponents,
                                               val authConnector: AuthConnector,
                                               businessVerificationService: BusinessVerificationService,
                                               partnershipIdentificationService: PartnershipIdentificationService,
                                               journeyService: JourneyService
                                              )(implicit val executionContext: ExecutionContext) extends FrontendController(mcc) with AuthorisedFunctions {

  def startBusinessVerificationJourney(journeyId: String): Action[AnyContent] = Action.async {
    implicit request =>
      authorised() {
        partnershipIdentificationService.retrieveSautr(journeyId).flatMap {
          case Some(sautr) =>
            businessVerificationService.createBusinessVerificationJourney(journeyId, sautr).flatMap {
              case Some(redirectUri) =>
                Future.successful(Redirect(redirectUri))
              case None =>
                Future.successful(NotImplemented)
            }
          case None =>
            throw new InternalServerException(s"There is no SAUTR for $journeyId")
        }
      }
  }

  def retrieveBusinessVerificationResult(journeyId: String): Action[AnyContent] = Action.async {
    implicit req =>
      authorised().retrieve(internalId) {
        case Some(authInternalId) =>
          req.getQueryString("journeyId") match {
            case Some(businessVerificationJourneyId) =>
              businessVerificationService.retrieveBusinessVerificationStatus(businessVerificationJourneyId).flatMap {
                verificationStatus =>
                  partnershipIdentificationService.storeBusinessVerificationStatus(journeyId, verificationStatus).flatMap {
                    _ => //Update once integrated with Register API
                      journeyService.getJourneyConfig(journeyId, authInternalId).flatMap {
                        journeyConfig => Future.successful(Redirect(journeyConfig.continueUrl + s"?journeyId=$journeyId"))
                      }
                  }
              }
            case None =>
              throw new InternalServerException("JourneyID is missing from Business Verification callback")
          }
        case _ =>
          throw new InternalServerException("Internal ID could not be retrieved from Auth")
      }
  }

}