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
import org.mockito.Mockito.{reset, verify, when}
import org.mockito.stubbing.OngoingStubbing
import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.httpparsers.PartnershipIdentificationStorageHttpParser.SuccessfullyStored
import uk.gov.hmrc.partnershipidentificationfrontend.models.{BusinessVerificationStatus, PartnershipInformation, RegistrationStatus}
import uk.gov.hmrc.partnershipidentificationfrontend.service.PartnershipIdentificationService

import scala.concurrent.Future

trait MockPartnershipIdentificationService extends MockitoSugar with BeforeAndAfterEach {
  self: Suite =>

  val mockPartnershipIdentificationService: PartnershipIdentificationService = mock[PartnershipIdentificationService]

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockPartnershipIdentificationService)
  }

  def mockRetrieveSautr(journeyId: String)
                       (response: Future[Option[String]]): OngoingStubbing[_] =
    when(mockPartnershipIdentificationService.retrieveSautr(
      ArgumentMatchers.eq(journeyId)
    )(ArgumentMatchers.any[HeaderCarrier])
    ).thenReturn(response)

  def mockRetrieveBusinessVerificationResponse(journeyId: String)
                                              (response: Future[Option[BusinessVerificationStatus]]): OngoingStubbing[_] =
    when(mockPartnershipIdentificationService.retrieveBusinessVerificationStatus(
      ArgumentMatchers.eq(journeyId)
    )(ArgumentMatchers.any[HeaderCarrier])
    ).thenReturn(response)

  def mockStoreRegistrationResponse(journeyId: String, registrationStatus: RegistrationStatus)
                                   (response: Future[SuccessfullyStored.type]): OngoingStubbing[_] =
    when(mockPartnershipIdentificationService.storeRegistrationStatus(
      ArgumentMatchers.eq(journeyId),
      ArgumentMatchers.eq(registrationStatus)
    )(ArgumentMatchers.any[HeaderCarrier])
    ).thenReturn(response)

  def mockStoreIdentifiersMatch(journeyId: String, identifiersMatch: Boolean)(response: Future[SuccessfullyStored.type]): OngoingStubbing[_] =
    when(mockPartnershipIdentificationService.storeIdentifiersMatch(
      ArgumentMatchers.eq(journeyId),
      ArgumentMatchers.eq(identifiersMatch)
    )(ArgumentMatchers.any[HeaderCarrier])
    ).thenReturn(response)


  def mockStoreBusinessVerificationResponse(journeyId: String, businessVerificationStatus: BusinessVerificationStatus)
                                           (response: Future[SuccessfullyStored.type]): OngoingStubbing[_] =
    when(mockPartnershipIdentificationService.storeBusinessVerificationStatus(
      ArgumentMatchers.eq(journeyId),
      ArgumentMatchers.eq(businessVerificationStatus)
    )(ArgumentMatchers.any[HeaderCarrier])
    ).thenReturn(response)

  def mockRetrievePartnershipInformation(journeyId: String)(response: Future[Option[PartnershipInformation]]): OngoingStubbing[_] =
    when(mockPartnershipIdentificationService.retrievePartnershipInformation(
      ArgumentMatchers.eq(journeyId)
    )(ArgumentMatchers.any[HeaderCarrier])
    ).thenReturn(response)

  def verifyStoreRegistrationResponse(journeyId: String, registrationStatus: RegistrationStatus): Unit =
    verify(mockPartnershipIdentificationService).storeRegistrationStatus(
      ArgumentMatchers.eq(journeyId),
      ArgumentMatchers.eq(registrationStatus)
    )(ArgumentMatchers.any[HeaderCarrier])

}
