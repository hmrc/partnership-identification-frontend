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

package uk.gov.hmrc.partnershipidentificationfrontend.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.partnershipidentificationfrontend.controllers.routes
import uk.gov.hmrc.partnershipidentificationfrontend.utils.WiremockMethods

trait BusinessVerificationStub extends WiremockMethods {

  def stubCreateBusinessVerificationJourney(sautr: String,
                                            journeyId: String
                                           )(status: Int,
                                             body: JsObject = Json.obj()): StubMapping = {

    val postBody = Json.obj("journeyType" -> "BUSINESS_VERIFICATION",
      "origin" -> "vat",
      "identifiers" -> Json.arr(
        Json.obj(
          "saUtr" -> sautr
        )
      ),
      "continueUrl" -> routes.BusinessVerificationController.retrieveBusinessVerificationResult(journeyId).url
    )

    when(method = POST, uri = "/business-verification/journey", postBody)
      .thenReturn(
        status = status,
        body = body
      )
  }

  def stubRetrieveBusinessVerificationResult(journeyId: String)
                                            (status: Int,
                                             body: JsObject = Json.obj()): StubMapping =
    when(method = GET, uri = s"/business-verification/journey/$journeyId/status")
      .thenReturn(
        status = status,
        body = body
      )

  def stubCreateBusinessVerificationJourneyFromStub(sautr: String,
                                                    journeyId: String
                                                   )(status: Int,
                                                     body: JsObject = Json.obj()): StubMapping = {

    val postBody = Json.obj("journeyType" -> "BUSINESS_VERIFICATION",
      "origin" -> "vat",
      "identifiers" -> Json.arr(
        Json.obj(
          "saUtr" -> sautr
        )
      ),
      "continueUrl" -> routes.BusinessVerificationController.retrieveBusinessVerificationResult(journeyId).url
    )

    when(method = POST, uri = "/identify-your-partnership/test-only/business-verification/journey", postBody)
      .thenReturn(
        status = status,
        body = body
      )
  }

  def stubRetrieveBusinessVerificationResultFromStub(journeyId: String)
                                                    (status: Int,
                                                     body: JsObject = Json.obj()): StubMapping =
    when(method = GET, uri = s"/identify-your-partnership/test-only/business-verification/journey/$journeyId/status")
      .thenReturn(
        status = status,
        body = body
      )

}
