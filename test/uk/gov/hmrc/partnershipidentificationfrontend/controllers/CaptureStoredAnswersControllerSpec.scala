/*
 * Copyright 2025 HM Revenue & Customs
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

import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.partnershipidentificationfrontend.models.PartnershipType.LimitedPartnership
import uk.gov.hmrc.partnershipidentificationfrontend.models._
import uk.gov.hmrc.partnershipidentificationfrontend.service.JourneyService
import uk.gov.hmrc.partnershipidentificationfrontend.service.mocks.MockPartnershipIdentificationService

import scala.concurrent.Future
import play.api.libs.json.Json

class CaptureStoredAnswersControllerSpec
    extends AnyWordSpec
    with Matchers
    with MockitoSugar
    with BeforeAndAfterEach
    with MockPartnershipIdentificationService {

  private val mockAuthConnector: AuthConnector = mock[AuthConnector]
  private val mockJourneyService: JourneyService = mock[JourneyService]

  private val testJourneyId = "journey-123"
  private val testInternalId = "internal-123"
  private val testCompanyName = "Test Partnership LLP"
  private val testCompanyNumber = "AB123456"
  private val testSautr = "1234567890"
  private val testPostCode = "AA1 1AA"

  private val pageConfig = PageConfig(
    optServiceName = Some("Test Service"),
    deskProServiceId = "test-service-id",
    signOutUrl = "/sign-out",
    accessibilityUrl = "/accessibility",
    optLabels = None
  )

  private val journeyConfig = JourneyConfig(
    continueUrl = "/continue",
    businessVerificationCheck = false,
    pageConfig = pageConfig,
    partnershipType = LimitedPartnership,
    regime = "VATC"
  )

  private val companyProfile = CompanyProfile(
    companyName = testCompanyName,
    companyNumber = testCompanyNumber,
    dateOfIncorporation = "2020-01-01",
    unsanitisedCHROAddress = Json.obj("postal_code" -> testPostCode)
  )

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "metrics.enabled" -> false,
        "metrics.jvm" -> false,
        "microservice.metrics.graphite.enabled" -> false
      )
      .overrides(
        bind[AuthConnector].toInstance(mockAuthConnector),
        bind[JourneyService].toInstance(mockJourneyService),
        bind[uk.gov.hmrc.partnershipidentificationfrontend.service.PartnershipIdentificationService].toInstance(mockPartnershipIdentificationService)
      )
      .build()

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuthConnector, mockJourneyService)
    when(
      mockAuthConnector.authorise[Option[String]](any(), any[Retrieval[Option[String]]]())(any(), any())
    ).thenReturn(Future.successful(Some(testInternalId)))
    when(mockJourneyService.getJourneyConfig(testJourneyId, testInternalId)).thenReturn(Future.successful(journeyConfig))
  }

  private def documentOf(result: Future[play.api.mvc.Result]) =
    Jsoup.parse(contentAsString(result))

  "CaptureCompanyNumberController.show" should {
    "pre-populate the stored company number" in {
      mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(companyProfile)))

      val controller = app.injector.instanceOf[CaptureCompanyNumberController]
      val result = controller.show(testJourneyId)(FakeRequest())
      val doc = documentOf(result)

      status(result) mustBe OK
      doc.getElementById("companyNumber").`val`() mustBe testCompanyNumber
    }
  }

  "CaptureSautrController.show" should {
    "pre-populate the stored sautr" in {
      mockRetrieveSautr(testJourneyId)(Future.successful(Some(testSautr)))

      val controller = app.injector.instanceOf[CaptureSautrController]
      val result = controller.show(testJourneyId)(FakeRequest())
      val doc = documentOf(result)

      status(result) mustBe OK
      doc.getElementById("sa-utr").`val`() mustBe testSautr
    }
  }

  "CapturePostCodeController.show" should {
    "pre-populate the stored postcode" in {
      mockRetrievePostCode(testJourneyId)(Future.successful(Some(testPostCode)))

      val controller = app.injector.instanceOf[CapturePostCodeController]
      val result = controller.show(testJourneyId)(FakeRequest())
      val doc = documentOf(result)

      status(result) mustBe OK
      doc.getElementById("postcode").`val`() mustBe testPostCode
    }
  }

  "ConfirmPartnershipNameController.show" should {
    "pre-populate the stored yes/no answer" in {
      mockRetrieveCompanyProfile(testJourneyId)(Future.successful(Some(companyProfile)))
      mockRetrieveConfirmedPartnershipName(testJourneyId)(Future.successful(Some(true)))

      val controller = app.injector.instanceOf[ConfirmPartnershipNameController]
      val result = controller.show(testJourneyId)(FakeRequest())
      val doc = documentOf(result)

      status(result) mustBe OK
      doc.select("input[name=yes_no][value=yes]").hasAttr("checked") mustBe true
    }
  }
}
