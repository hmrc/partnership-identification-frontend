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

package uk.gov.hmrc.partnershipidentificationfrontend.service.mocks

import org.mockito.ArgumentMatchers
import org.mockito.Mockito.{reset, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.service.ValidatePartnershipInformationService

import scala.concurrent.{ExecutionContext, Future}

trait MockValidatePartnershipInformationService extends MockitoSugar with BeforeAndAfterEach {
  self: Suite =>

  val mockValidatePartnershipInformationService: ValidatePartnershipInformationService = mock[ValidatePartnershipInformationService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockValidatePartnershipInformationService)
  }

  def mockValidateIdentifiers(sautr: String,
                              postcode: String
                             )(response: Future[Boolean]): OngoingStubbing[_] = {
    when(mockValidatePartnershipInformationService.validateIdentifiers(
      ArgumentMatchers.eq(sautr),
      ArgumentMatchers.eq(postcode)
    )(ArgumentMatchers.any[HeaderCarrier],
      ArgumentMatchers.any[ExecutionContext]
    )).thenReturn(response)
  }


}
