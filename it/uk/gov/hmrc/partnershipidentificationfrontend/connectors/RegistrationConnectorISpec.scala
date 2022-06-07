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

import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.models.{Registered, RegistrationFailed}
import uk.gov.hmrc.partnershipidentificationfrontend.stubs.RegisterStub
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

class RegistrationConnectorISpec extends ComponentSpecHelper with RegisterStub {

  private val registrationConnector = app.injector.instanceOf[RegistrationConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "registerGeneralPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterGeneralPartnership(testSautr, testRegime)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerGeneralPartnership(testSautr, testRegime))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterGeneralPartnership(testSautr, testRegime)(OK, RegistrationFailed(Some(testRegistrationFailure)))

        val result = await(registrationConnector.registerGeneralPartnership(testSautr, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }
      }
      "multiple failures have been returned" in {
        stubRegisterGeneralPartnership(testSautr, testRegime)(OK, RegistrationFailed(Some(testMultipleRegistrationFailure)))

        val result = await(registrationConnector.registerGeneralPartnership(testSautr, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testMultipleRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }
      }
    }
  }

  "registerScottishPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterScottishPartnership(testSautr, testRegime)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerScottishPartnership(testSautr, testRegime))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterScottishPartnership(testSautr, testRegime)(OK, RegistrationFailed(Some(testRegistrationFailure)))

        val result = await(registrationConnector.registerScottishPartnership(testSautr, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }
      }
      "multiple failures have been returned" in {
        stubRegisterScottishPartnership(testSautr, testRegime)(OK, RegistrationFailed(Some(testMultipleRegistrationFailure)))

        val result = await(registrationConnector.registerScottishPartnership(testSautr, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testMultipleRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }
      }
    }
  }

  "registerLimitedPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterLimitedPartnership(testSautr, testCompanyNumber, testRegime)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerLimitedPartnership(testSautr, testCompanyNumber, testRegime))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterLimitedPartnership(testSautr, testCompanyNumber, testRegime)(OK, RegistrationFailed(Some(testRegistrationFailure)))

        val result = await(registrationConnector.registerLimitedPartnership(testSautr, testCompanyNumber, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }
      }
      "multiple failures have been returned" in {
        stubRegisterLimitedPartnership(testSautr, testCompanyNumber, testRegime)(OK, RegistrationFailed(Some(testMultipleRegistrationFailure)))

        val result = await(registrationConnector.registerLimitedPartnership(testSautr, testCompanyNumber, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testMultipleRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }      }
    }
  }

  "registerLimitedLiabilityPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime)(OK, RegistrationFailed(Some(testRegistrationFailure)))

        val result = await(registrationConnector.registerLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }
      }
      "multiple failures have been returned" in {
        stubRegisterLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime)(OK, RegistrationFailed(Some(testMultipleRegistrationFailure)))

        val result = await(registrationConnector.registerLimitedLiabilityPartnership(testSautr, testCompanyNumber, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testMultipleRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }      }
    }
  }

  "registerScottishLimitedPartnership" should {
    "return Registered" when {
      "the registration has been successful" in {
        stubRegisterScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime)(OK, Registered(testSafeId))

        val result = await(registrationConnector.registerScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime))

        result mustBe Registered(testSafeId)
      }
    }

    "return RegistrationFailed" when {
      "the registration has not been successful" in {
        stubRegisterScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime)(OK, RegistrationFailed(Some(testRegistrationFailure)))

        val result = await(registrationConnector.registerScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }
      }
      "multiple failures have been returned" in {
        stubRegisterScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime)(OK, RegistrationFailed(Some(testMultipleRegistrationFailure)))

        val result = await(registrationConnector.registerScottishLimitedPartnership(testSautr, testCompanyNumber, testRegime))

        result match {
          case RegistrationFailed(Some(failures)) => failures mustBe testMultipleRegistrationFailure
          case _ => fail("Incorrect RegistrationStatus has been returned")
        }      }
    }
  }
}
