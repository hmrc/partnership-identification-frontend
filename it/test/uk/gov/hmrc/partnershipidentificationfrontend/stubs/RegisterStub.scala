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

package test.uk.gov.hmrc.partnershipidentificationfrontend.stubs

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.partnershipidentificationfrontend.models.RegistrationStatus
import uk.gov.hmrc.partnershipidentificationfrontend.utils.{WiremockHelper, WiremockMethods}

trait RegisterStub extends WiremockMethods {
  implicit private val RegistrationStatusFormat: OFormat[RegistrationStatus] = RegistrationStatus.format

  def stubRegisterGeneralPartnership(sautr: String, regime: String)(status: Int, body: RegistrationStatus): StubMapping =
    when(method = POST, uri = "/partnership-identification/register-general-partnership", Json.obj(
      "sautr" -> sautr,
      "regime" -> regime
    ))
      .thenReturn(
        status = status,
        body = Json.obj("registration" -> body)
      )

  def verifyRegisterGeneralPartnership(sautr: String, regime: String): Unit =
    WiremockHelper.verifyPost(uri = "/partnership-identification/register-general-partnership", optBody = Some(Json.obj(
      "sautr" -> sautr,
      "regime" -> regime
    ).toString))

  def stubRegisterScottishPartnership(sautr: String, regime: String)(status: Int, body: RegistrationStatus): StubMapping =
    when(method = POST, uri = "/partnership-identification/register-scottish-partnership", Json.obj(
      "sautr" -> sautr,
      "regime" -> regime
    ))
      .thenReturn(
        status = status,
        body = Json.obj("registration" -> body)
      )

  def verifyRegisterScottishPartnership(sautr: String, regime: String): Unit =
    WiremockHelper.verifyPost(uri = "/partnership-identification/register-scottish-partnership", optBody = Some(Json.obj(
      "sautr" -> sautr,
      "regime" -> regime
    ).toString))

  def stubRegisterLimitedPartnership(sautr: String, companyNumber: String, regime: String)(status: Int, body: RegistrationStatus): StubMapping =
    when(method = POST,
      uri = "/partnership-identification/register-limited-partnership",
      Json.obj(
        "sautr" -> sautr,
        "companyNumber" -> companyNumber,
        "regime" -> regime
      )
    ).thenReturn(
      status = status,
      body = Json.obj("registration" -> body)
    )

  def verifyRegisterLimitedPartnership(sautr: String, companyNumber: String, regime: String): Unit =
    WiremockHelper.verifyPost(
      uri = "/partnership-identification/register-limited-partnership",
      optBody = Some(Json.obj(
        "sautr" -> sautr,
        "companyNumber" -> companyNumber,
        "regime" -> regime
      ).toString)
    )

  def stubRegisterLimitedLiabilityPartnership(sautr: String, companyNumber: String, regime: String)(status: Int, body: RegistrationStatus): StubMapping =
    when(method = POST,
      uri = "/partnership-identification/register-limited-liability-partnership",
      Json.obj(
        "sautr" -> sautr,
        "companyNumber" -> companyNumber,
        "regime" -> regime
      )
    ).thenReturn(
      status = status,
      body = Json.obj("registration" -> body)
    )

  def verifyRegisterLimitedLiabilityPartnership(sautr: String, companyNumber: String, regime: String): Unit =
    WiremockHelper.verifyPost(
      uri = "/partnership-identification/register-limited-liability-partnership",
      optBody = Some(Json.obj(
        "sautr" -> sautr,
        "companyNumber" -> companyNumber,
        "regime" -> regime
      ).toString)
    )


  def stubRegisterScottishLimitedPartnership(sautr: String, companyNumber: String, regime: String)(status: Int, body: RegistrationStatus): StubMapping =
    when(
      method = POST,
      uri = "/partnership-identification/register-scottish-limited-partnership",
      Json.obj(
        "sautr" -> sautr,
        "companyNumber" -> companyNumber,
        "regime" -> regime
      )
    ).thenReturn(
      status = status,
      body = Json.obj("registration" -> body)
    )

  def verifyRegisterScottishLimitedPartnership(sautr: String, companyNumber: String, regime: String): Unit =
    WiremockHelper.verifyPost(
      uri = "/partnership-identification/register-scottish-limited-partnership",
      optBody = Some(Json.obj(
        "sautr" -> sautr,
        "companyNumber" -> companyNumber,
        "regime" -> regime
      ).toString)
    )
}
