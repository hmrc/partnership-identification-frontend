/*
 * Copyright 2023 HM Revenue & Customs
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

import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.addTestReportOption

val appName = "partnership-identification-frontend"

val silencerVersion = "1.7.12"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    resolvers += Resolver.jcenterRepo,
    majorVersion := 0,
    scalaVersion := "2.13.8",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.govukfrontend.views.html.components._"
    ),
    Assets / pipelineStages := Seq(gzip)
  )
  .settings(SilencerSettings(silencerVersion))
  .settings(ScoverageSettings.settings *)
  .configs(IntegrationTest)
  .settings(DefaultBuildSettings.integrationTestSettings())
  .settings(calculateITTestsGroupingSettings(System.getProperty("isADevMachine")): _*)
  .disablePlugins(JUnitXmlReportPlugin)

Test / Keys.fork := true
Test / javaOptions += "-Dlogger.resource=logback-test.xml"
Test / parallelExecution := true
addTestReportOption(Test, "test-reports")

IntegrationTest / Keys.fork := true
IntegrationTest / javaOptions += "-Dlogger.resource=logback-test.xml"

def calculateITTestsGroupingSettings(isADevMachineProperty: String): Seq[sbt.Setting[_]] = {
  IntegrationTest / testGrouping := {
    if ("true".equals(isADevMachineProperty))
      onlyOneJvmForAllISpecTestsTestGroup.value
    else
      (IntegrationTest / testGrouping).value
  }
}

lazy val onlyOneJvmForAllISpecTestsTestGroup = taskKey[Seq[Tests.Group]]("Default test group that run all the tests in only one JVM - (much faster!)")

onlyOneJvmForAllISpecTestsTestGroup := Seq(new Tests.Group(
  "<default>",
  (IntegrationTest / definedTests).value,
  Tests.InProcess,
  Seq.empty
))

