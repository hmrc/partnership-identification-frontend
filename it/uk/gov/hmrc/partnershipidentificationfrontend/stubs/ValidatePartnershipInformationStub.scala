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
import uk.gov.hmrc.partnershipidentificationfrontend.utils.WiremockMethods

trait ValidatePartnershipInformationStub extends WiremockMethods {

  def stubValidate(sautr: String, postcode: String)(status: Int, body: JsObject): StubMapping =
    when(
      method = POST,
      uri = s"/partnership-identification/validate-partnership-information",
      body = Json.obj(
        "sautr" -> sautr,
        "postcode" -> postcode
      )
    ).thenReturn(
      status = status,
      body = body
    )
}
