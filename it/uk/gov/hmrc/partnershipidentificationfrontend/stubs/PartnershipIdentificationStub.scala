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
import play.api.libs.json.{JsString, JsValue, Json}
import uk.gov.hmrc.partnershipidentificationfrontend.models.BusinessVerificationStatus
import uk.gov.hmrc.partnershipidentificationfrontend.utils.{WiremockHelper, WiremockMethods}

trait PartnershipIdentificationStub extends WiremockMethods {


  def stubStoreSautr(journeyId: String, sautr: String)(status: Int): StubMapping =
    when(method = PUT,
      uri = s"/partnership-identification/journey/$journeyId/sautr", body = JsString(sautr)
    ).thenReturn(
      status = status
    )

  def stubStorePostCode(journeyId: String, postCode: String)(status: Int): StubMapping =
    when(method = PUT,
      uri = s"/partnership-identification/journey/$journeyId/postcode", body = JsString(postCode)
    ).thenReturn(
      status = status
    )

  def stubStoreBusinessVerificationStatus(journeyId: String,
                                          businessVerificationStatus: BusinessVerificationStatus
                                         )(status: Int): StubMapping =
    when(method = PUT,
      uri = s"/partnership-identification/journey/$journeyId/businessVerification",
      body = Json.toJson(businessVerificationStatus)
    ).thenReturn(
      status = status
    )

  def stubStoreIdentifiersMatch(journeyId: String, identifiersMatch: Boolean)(status: Int): StubMapping =
    when(method = PUT,
      uri = s"/partnership-identification/journey/$journeyId/identifiersMatch", body = identifiersMatch
    ).thenReturn(
      status = status
    )

  def verifyStoreBusinessVerificationStatus(journeyId: String, businessVerificationStatus: BusinessVerificationStatus): Unit =
    WiremockHelper.verifyPut(
      uri = s"/partnership-identification/journey/$journeyId/businessVerification",
      optBody = Some(Json.toJson(businessVerificationStatus).toString())
    )

  def stubRetrieveSautr(journeyId: String)(status: Int, body: String = ""): StubMapping = {
    when(method = GET,
      uri = s"/partnership-identification/journey/$journeyId/sautr"
    ).thenReturn(
      status = status,
      body = JsString(body)
    )
  }

  def stubRetrievePartnershipDetails(journeyId: String)(status: Int, body: JsValue = Json.obj()): StubMapping =
    when(method = GET,
      uri = s"/partnership-identification/journey/$journeyId"
    ).thenReturn(
      status = status,
      body = body
    )

}
