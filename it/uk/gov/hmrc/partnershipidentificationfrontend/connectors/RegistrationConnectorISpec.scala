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

package uk.gov.hmrc.partnershipidentificationfrontend.connectors

import play.api.test.Helpers.{OK, await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants.{testCompanyNumber, testSafeId, testSautr}
import uk.gov.hmrc.partnershipidentificationfrontend.models.{Registered, RegistrationFailed}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.RegisterStub
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

class RegistrationConnectorISpec extends ComponentSpecHelper with RegisterStub {

  private val registrationConnector = app.injector.instanceOf[RegistrationConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "registerGeneralPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterGeneralPartnership(testSautr)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerGeneralPartnership(testSautr))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterGeneralPartnership(testSautr)(OK, RegistrationFailed)

        val result = await(registrationConnector.registerGeneralPartnership(testSautr))

        result mustBe RegistrationFailed
      }
    }
  }

  "registerScottishPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterScottishPartnership(testSautr)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerScottishPartnership(testSautr))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterScottishPartnership(testSautr)(OK, RegistrationFailed)

        val result = await(registrationConnector.registerScottishPartnership(testSautr))

        result mustBe RegistrationFailed
      }
    }
  }

  "registerLimitedPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterLimitedPartnership(testSautr, testCompanyNumber)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerLimitedPartnership(testSautr, testCompanyNumber))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterLimitedPartnership(testSautr, testCompanyNumber)(OK, RegistrationFailed)

        val result = await(registrationConnector.registerLimitedPartnership(testSautr, testCompanyNumber))

        result mustBe RegistrationFailed
      }
    }
  }

  "registerLimitedLiabilityPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerLimitedLiabilityPartnership(testSautr, testCompanyNumber))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber)(OK, RegistrationFailed)

        val result = await(registrationConnector.registerLimitedLiabilityPartnership(testSautr, testCompanyNumber))

        result mustBe RegistrationFailed
      }
    }
  }

  "registerScottishLimitedPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterScottishLimitedPartnership(testSautr, testCompanyNumber)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerScottishLimitedPartnership(testSautr, testCompanyNumber))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterScottishLimitedPartnership(testSautr, testCompanyNumber)(OK, RegistrationFailed)

        val result = await(registrationConnector.registerScottishLimitedPartnership(testSautr, testCompanyNumber))

        result mustBe RegistrationFailed
      }
    }
  }
}
