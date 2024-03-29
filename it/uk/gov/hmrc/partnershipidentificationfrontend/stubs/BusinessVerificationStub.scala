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

package uk.gov.hmrc.partnershipidentificationfrontend.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.routes
import uk.gov.hmrc.partnershipidentificationfrontend.models.JourneyConfig
import uk.gov.hmrc.partnershipidentificationfrontend.utils.WiremockMethods

trait BusinessVerificationStub extends WiremockMethods {

  def stubCreateBusinessVerificationJourney(sautr: String,
                                            journeyId: String,
                                            appConfig: AppConfig,
                                            journeyConfig: JourneyConfig
                                           )(status: Int,
                                             body: JsObject = Json.obj()): StubMapping =

    internalStubCreateBusinessVerificationJourney(
      sautr,
      journeyId,
      appConfig,
      journeyConfig,
      "/business-verification/journey")(status, body)

  def stubRetrieveBusinessVerificationResult(journeyId: String)
                                            (status: Int,
                                             body: JsObject = Json.obj()): StubMapping =
    when(method = GET, uri = s"/business-verification/journey/$journeyId/status")
      .thenReturn(
        status = status,
        body = body
      )

  def stubCreateBusinessVerificationJourneyFromStub(sautr: String,
                                                    journeyId: String,
                                                    appConfig: AppConfig,
                                                    journeyConfig: JourneyConfig
                                                   )(status: Int,
                                                     body: JsObject = Json.obj()): StubMapping =

    internalStubCreateBusinessVerificationJourney(
      sautr,
      journeyId,
      appConfig,
      journeyConfig, "/identify-your-partnership/test-only/business-verification/journey")(status, body)

  def stubRetrieveBusinessVerificationResultFromStub(journeyId: String)
                                                    (status: Int,
                                                     body: JsObject = Json.obj()): StubMapping =
    when(method = GET, uri = s"/identify-your-partnership/test-only/business-verification/journey/$journeyId/status")
      .thenReturn(
        status = status,
        body = body
      )

  private def internalStubCreateBusinessVerificationJourney(sautr: String,
                                                            journeyId: String,
                                                            appConfig: AppConfig,
                                                            journeyConfig: JourneyConfig,
                                                            uri: String
                                                           )(status: Int,
                                                             body: JsObject): StubMapping = {

    val callingService: String = journeyConfig.pageConfig.optServiceName.getOrElse(appConfig.defaultServiceName)

    val postBody = Json.obj("journeyType" -> "BUSINESS_VERIFICATION",
      "origin" -> journeyConfig.regime.toLowerCase,
      "identifiers" -> Json.arr(
        Json.obj(
          "saUtr" -> sautr
        )
      ),
      "entityType" -> "PARTNERSHIP",
      "continueUrl" -> routes.BusinessVerificationController.retrieveBusinessVerificationResult(journeyId).url,
      "accessibilityStatementUrl" -> journeyConfig.pageConfig.accessibilityUrl,
      "pageTitle" -> callingService,
      "deskproServiceName" -> journeyConfig.pageConfig.deskProServiceId
    )

    when(method = POST, uri = uri, postBody)
      .thenReturn(
        status = status,
        body = body
      )
  }

}

