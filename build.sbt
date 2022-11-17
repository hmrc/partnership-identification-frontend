import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.addTestReportOption
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "partnership-identification-frontend"

val silencerVersion = "1.7.3"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.12.13",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.govukfrontend.views.html.components._"
    ),
    Assets / pipelineStages := Seq(gzip),
    // ***************
    // Use the silencer plugin to suppress warnings
    scalacOptions += "-P:silencer:pathFilters=views;routes",
    libraryDependencies ++= Seq(
      compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
      "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
    )
    // ***************
  )
  .settings(publishingSettings: _*)
  .configs(IntegrationTest)
  .settings(DefaultBuildSettings.integrationTestSettings())
  .settings(calculateITTestsGroupingSettings(System.getProperty("isADevMachine")): _*)
  .settings(resolvers += Resolver.jcenterRepo)
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

