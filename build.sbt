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

val silencerVersion = "1.7.16"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

Test / Keys.fork := true
Test / javaOptions += "-Dlogger.resource=logback-test.xml"
Test / parallelExecution := true
addTestReportOption(Test, "test-reports")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    resolvers += Resolver.jcenterRepo,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.govukfrontend.views.html.components._"
    ),
    Assets / pipelineStages := Seq(gzip)
  )
  .settings(scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s")
  .settings(scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s")
  .settings(SilencerSettings(silencerVersion))
  .settings(ScoverageSettings.settings *)
  .settings(libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always)
  .disablePlugins(JUnitXmlReportPlugin)

def calculateITTestsGroupingSettings(isADevMachineProperty: String): Seq[sbt.Setting[_]] = {
  Test / testGrouping := {
    if ("true".equals(isADevMachineProperty))
      onlyOneJvmForAllISpecTestsTestGroup.value
    else
      (Test / testGrouping).value
  }
}

lazy val onlyOneJvmForAllISpecTestsTestGroup = taskKey[Seq[Tests.Group]]("Default test group that run all the tests in only one JVM - (much faster!)")

it / onlyOneJvmForAllISpecTestsTestGroup := Seq(new Tests.Group(
  "<default>",
  (Test / definedTests).value,
  Tests.InProcess,
  Seq.empty
))


lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(calculateITTestsGroupingSettings(System.getProperty("isADevMachine")): _*)
  .settings(libraryDependencies ++= AppDependencies.test)

