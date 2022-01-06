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

package uk.gov.hmrc.partnershipidentificationfrontend.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.Json
import uk.gov.hmrc.partnershipidentificationfrontend.models.RegistrationStatus
import uk.gov.hmrc.partnershipidentificationfrontend.utils.{WiremockHelper, WiremockMethods}

trait RegisterStub extends WiremockMethods {

  def stubRegisterGeneralPartnership(sautr: String)(status: Int, body: RegistrationStatus): StubMapping =
    when(method = POST, uri = "/partnership-identification/register-general-partnership", Json.obj("sautr" -> sautr))
      .thenReturn(
        status = status,
        body = Json.obj("registration" -> body)
      )

  def verifyRegisterGeneralPartnership(sautr: String): Unit =
    WiremockHelper.verifyPost(uri = "/partnership-identification/register-general-partnership", optBody = Some(Json.obj("sautr" -> sautr).toString))

  def stubRegisterScottishPartnership(sautr: String)(status: Int, body: RegistrationStatus): StubMapping =
    when(method = POST, uri = "/partnership-identification/register-scottish-partnership", Json.obj("sautr" -> sautr))
      .thenReturn(
        status = status,
        body = Json.obj("registration" -> body)
      )

  def verifyRegisterScottishPartnership(sautr: String): Unit =
    WiremockHelper.verifyPost(uri = "/partnership-identification/register-scottish-partnership", optBody = Some(Json.obj("sautr" -> sautr).toString))

}
