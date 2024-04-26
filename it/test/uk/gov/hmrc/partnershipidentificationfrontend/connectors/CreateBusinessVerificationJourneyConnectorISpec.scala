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

package test.uk.gov.hmrc.partnershipidentificationfrontend.connectors

import play.api.http.Status.FORBIDDEN
import play.api.libs.json.Json
import play.api.test.Helpers.{CREATED, NOT_FOUND, await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.config.AppConfig
import uk.gov.hmrc.partnershipidentificationfrontend.featureswitch.core.config.{BusinessVerificationStub, FeatureSwitching}
import uk.gov.hmrc.partnershipidentificationfrontend.models.{JourneyCreated, NotEnoughEvidence, UserLockedOut}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.BusinessVerificationStub
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

class CreateBusinessVerificationJourneyConnectorISpec extends ComponentSpecHelper with BusinessVerificationStub with FeatureSwitching {

  private lazy val createBusinessVerificationJourneyConnector = app.injector.instanceOf[CreateBusinessVerificationJourneyConnector]

  private lazy val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "createBusinessVerificationJourneyConnector" when {
    s"the $BusinessVerificationStub feature switch is enabled" should {
      "return the redirectUri and therefore no BV status" when {
        "the journey creation has been successful" when {
          "the calling service has been defined" in {
            enable(BusinessVerificationStub)
            stubCreateBusinessVerificationJourneyFromStub(
              testSautr,
              testJourneyId,
              appConfig,
              testGeneralPartnershipJourneyConfigWithCallingService(true))(CREATED, Json.obj("redirectUri" -> testContinueUrl))

            val result = await(createBusinessVerificationJourneyConnector.createBusinessVerificationJourney(
                testJourneyId,
                testSautr,
                testGeneralPartnershipJourneyConfigWithCallingService(true)))

            result mustBe Right(JourneyCreated(testContinueUrl))
          }
          "the calling service has not been defined" in {
            enable(BusinessVerificationStub)
            stubCreateBusinessVerificationJourneyFromStub(
              testSautr,
              testJourneyId,
              appConfig,
              testGeneralPartnershipJourneyConfig(true))(CREATED, Json.obj("redirectUri" -> testContinueUrl))

            val result = await(createBusinessVerificationJourneyConnector.createBusinessVerificationJourney(
              testJourneyId,
              testSautr,
              testGeneralPartnershipJourneyConfig(true)))

            result mustBe Right(JourneyCreated(testContinueUrl))
          }
        }
      }

      "return no redirect URL and an appropriate BV status" when {
        "the journey creation has been unsuccessful because BV cannot find the record" in {
          enable(BusinessVerificationStub)
          stubCreateBusinessVerificationJourneyFromStub(testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(NOT_FOUND)

          val result = await(createBusinessVerificationJourneyConnector.createBusinessVerificationJourney(testJourneyId, testSautr, testGeneralPartnershipJourneyConfig(true)))

          result mustBe Left(NotEnoughEvidence)
        }

        "the journey creation has been unsuccessful because the user has had too many attempts and is logged out" in {
          enable(BusinessVerificationStub)
          stubCreateBusinessVerificationJourneyFromStub(testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(FORBIDDEN)

          val result = await(createBusinessVerificationJourneyConnector.createBusinessVerificationJourney(testJourneyId, testSautr, testGeneralPartnershipJourneyConfig(true)))

          result mustBe Left(UserLockedOut)
        }
      }
    }

    s"the $BusinessVerificationStub feature switch is disabled" should {
      "return the redirectUri and therefore no BV status" when {
        "the journey creation has been successful" when {
          "the calling service has not been defined" in {
            disable(BusinessVerificationStub)
            stubCreateBusinessVerificationJourney(
              testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(CREATED, Json.obj("redirectUri" -> testContinueUrl))

            val result = await(createBusinessVerificationJourneyConnector.createBusinessVerificationJourney(
              testJourneyId,
              testSautr,
              testGeneralPartnershipJourneyConfig(true)))

            result mustBe Right(JourneyCreated(testContinueUrl))
          }
          "the calling service has been defined" in {
            disable(BusinessVerificationStub)
            stubCreateBusinessVerificationJourney(
              testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfigWithCallingService(true))(CREATED, Json.obj("redirectUri" -> testContinueUrl))

            val result = await(createBusinessVerificationJourneyConnector.createBusinessVerificationJourney(
              testJourneyId,
              testSautr,
              testGeneralPartnershipJourneyConfigWithCallingService(true)))

            result mustBe Right(JourneyCreated(testContinueUrl))
          }
        }
      }

      "return no redirect URL and an appropriate BV status" when {
        "the journey creation has been unsuccessful because BV cannot find the record" in {
          disable(BusinessVerificationStub)
          stubCreateBusinessVerificationJourney(testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(NOT_FOUND)

          val result = await(createBusinessVerificationJourneyConnector.createBusinessVerificationJourney(testJourneyId, testSautr, testGeneralPartnershipJourneyConfig(true)))

          result mustBe Left(NotEnoughEvidence)
        }

        "the journey creation has been unsuccessful because the user has had too many attempts and is logged out" in {
          disable(BusinessVerificationStub)
          stubCreateBusinessVerificationJourney(testSautr, testJourneyId, appConfig, testGeneralPartnershipJourneyConfig(true))(FORBIDDEN)

          val result = await(createBusinessVerificationJourneyConnector.createBusinessVerificationJourney(testJourneyId, testSautr, testGeneralPartnershipJourneyConfig(true)))

          result mustBe Left(UserLockedOut)
        }
      }
    }
  }

}

