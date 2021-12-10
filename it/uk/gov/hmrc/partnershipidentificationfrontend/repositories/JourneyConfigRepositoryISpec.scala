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

package uk.gov.hmrc.partnershipidentificationfrontend.repositories

import org.scalatest.Assertion
import org.scalatest.concurrent.{AbstractPatienceConfiguration, Eventually}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.partnershipidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.partnershipidentificationfrontend.utils.ComponentSpecHelper

import scala.concurrent.ExecutionContext.Implicits.global

class JourneyConfigRepositoryISpec extends ComponentSpecHelper with AbstractPatienceConfiguration with Eventually {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(config)
    .configure("application.router" -> "testOnlyDoNotUseInAppConf.Routes")
    .configure("mongodb.timeToLiveSeconds" -> "10")
    .build

  override def beforeEach(): Unit = {
    super.beforeEach()
    await(repo.drop)
  }

  val repo: JourneyConfigRepository = app.injector.instanceOf[JourneyConfigRepository]

  "documents" should {
    "successfully insert a new document" in {
      await(repo.insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig))
      await(repo.count) mustBe 1
    }

    "successfully insert journeyConfig" in {
      await(repo.insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig))
      await(repo.findById(testJourneyId)) must contain(testGeneralPartnershipJourneyConfig)
    }

    "successfully insert a journeyConfig for a scottish limited partnership" in {
      await(repo.insertJourneyConfig(testJourneyId, testInternalId, testScottishLimitedPartnershipJourneyConfig))
      await(repo.findById(testJourneyId)) must contain(testScottishLimitedPartnershipJourneyConfig)
    }

    "successfully insert a journeyConfig for a limited liability partnership" in {
      await(repo.insertJourneyConfig(
        testJourneyId,
        testInternalId,
        testLimitedLiabilityPartnershipJourneyConfig
      ))
      await(repo.findById(testJourneyId)) must contain(testLimitedLiabilityPartnershipJourneyConfig)
    }

    "successfully delete all documents" in {
      await(repo.insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig))
      await(repo.drop)
      await(repo.count) mustBe 0
    }

    "successfully delete one document" in {
      await(repo.insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig))
      await(repo.insertJourneyConfig(testJourneyId + 1, testInternalId, testGeneralPartnershipJourneyConfig))
      await(repo.removeById(testJourneyId + 1))
      await(repo.count) mustBe 1
    }

    "successfully insert a journeyConfig with businessVerificationCheck field true" in {
      successfulInsertAJourneyConfigWith(businessVerificationCheck = true)
    }

    "successfully insert a journeyConfig with businessVerificationCheck field false" in {
      successfulInsertAJourneyConfigWith(businessVerificationCheck = false)
    }

    def successfulInsertAJourneyConfigWith(businessVerificationCheck: Boolean): Assertion = {
      await(repo.insertJourneyConfig(testJourneyId, testInternalId, testGeneralPartnershipJourneyConfig.copy(businessVerificationCheck = businessVerificationCheck)))
      await(repo.findById(testJourneyId)) must contain(testGeneralPartnershipJourneyConfig.copy(businessVerificationCheck = businessVerificationCheck))
    }

  }
}
