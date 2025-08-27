import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.addTestReportOption

val appName = "partnership-identification-frontend"

val silencerVersion = "1.7.19"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.16"

Test / Keys.fork := true
Test / javaOptions += "-Dlogger.resource=logback-test.xml"
Test / parallelExecution := true
addTestReportOption(Test, "test-reports")

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
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

def calculateITTestsGroupingSettings(isADevMachineProperty: String): Seq[sbt.Setting[?]] = {
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
  .settings(calculateITTestsGroupingSettings(System.getProperty("isADevMachine")) *)
  .settings(libraryDependencies ++= AppDependencies.test)

