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

package uk.gov.hmrc.partnershipidentificationfrontend.api.controllers

import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.internalId
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.InternalServerException
import uk.gov.hmrc.partnershipidentificationfrontend.api.controllers.JourneyController._
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.{routes => controllerRoutes}
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipFullJourneyData.jsonWriterForCallingServices
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType._
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.partnershipidentificationfrontend.service.{JourneyService, PartnershipIdentificationService}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class JourneyController @Inject()(controllerComponents: ControllerComponents,
                                  journeyService: JourneyService,
                                  val authConnector: AuthConnector,
                                  appConfig: AppConfig,
                                  partnershipIdentificationService: PartnershipIdentificationService
                                 )(implicit ec: ExecutionContext) extends BackendController(controllerComponents) with AuthorisedFunctions {

  def createGeneralPartnershipJourney: Action[JourneyConfig] = createJourney(GeneralPartnership)

  def createScottishPartnershipJourney: Action[JourneyConfig] = createJourney(ScottishPartnership)

  def createScottishLimitedPartnershipJourney: Action[JourneyConfig] = createJourney(ScottishLimitedPartnership)

  def createLimitedPartnershipJourney: Action[JourneyConfig] = createJourney(LimitedPartnership)

  def createLimitedLiabilityPartnershipJourney: Action[JourneyConfig] = createJourney(LimitedLiabilityPartnership)

  private def createJourney(partnershipType: PartnershipType): Action[JourneyConfig] =
    Action.async(parse.json[JourneyConfig] { json =>
      for {
        businessVerificationCheck <- (json \ businessVerificationCheckKey).validateOpt[Boolean]
        continueUrl <- (json \ continueUrlKey).validate[String]
        optServiceName <- (json \ optServiceNameKey).validateOpt[String]
        deskProServiceId <- (json \ deskProServiceIdKey).validate[String]
        signOutUrl <- (json \ signOutUrlKey).validate[String]
        accessibilityUrl <- (json \ accessibilityUrlKey).validate[String]
        regime <- (json \ regimeKey).validate[String]
      } yield JourneyConfig(
        continueUrl,
        businessVerificationCheck.getOrElse(true),
        PageConfig(optServiceName, deskProServiceId, signOutUrl, accessibilityUrl),
        partnershipType,
        regime
      )
    }) {
      implicit req =>
        authorised().retrieve(internalId) {
          case Some(authInternalId) =>
            journeyService.createJourney(req.body, authInternalId).map {
              journeyId =>
                partnershipType match {
                  case GeneralPartnership | ScottishPartnership =>
                    Created(Json.obj(
                      "journeyStartUrl" -> s"${appConfig.selfUrl}${controllerRoutes.CaptureSautrController.show(journeyId).url}"
                    ))
                  case _ =>
                    Created(Json.obj(
                      "journeyStartUrl" -> s"${appConfig.selfUrl}${controllerRoutes.CaptureCompanyNumberController.show(journeyId).url}"
                    ))
                }
            }
          case _ =>
            throw new InternalServerException("Internal ID could not be retrieved from Auth")
        }
    }

  def retrieveJourneyData(journeyId: String): Action[AnyContent] = Action.async {
    implicit req =>
      authorised() {
        partnershipIdentificationService.retrievePartnershipFullJourneyData(journeyId).map {
          case Some(journeyData) =>
            Ok(Json.toJson(journeyData)(jsonWriterForCallingServices))
          case None =>
            NotFound
        }
      }
  }

}

object JourneyController {
  val businessVerificationCheckKey = "businessVerificationCheck"
  val continueUrlKey = "continueUrl"
  val optServiceNameKey = "optServiceName"
  val deskProServiceIdKey = "deskProServiceId"
  val signOutUrlKey = "signOutUrl"
  val accessibilityUrlKey = "accessibilityUrl"
  val regimeKey = "regime"
}
