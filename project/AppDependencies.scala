import play.core.PlayVersion
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-28" % "5.3.0",
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-28"  % "5.3.0",
    "uk.gov.hmrc"                  %% "play-frontend-hmrc"         % "0.64.0-play-28",
    "uk.gov.hmrc"                  %% "play-frontend-govuk"        % "0.71.0-play-28",
    "uk.gov.hmrc"                  %% "simple-reactivemongo"       % "8.0.0-play-28",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % "2.12.3"

  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % "5.3.0"             % Test,

    "org.scalatest"           %% "scalatest"                  % "3.2.5"             % Test,
    "org.jsoup"               %  "jsoup"                      % "1.13.1"            % Test,
    "com.typesafe.play"       %% "play-test"                  % PlayVersion.current % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.36.8"            % "test, it",
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "5.1.0"             % "test, it",
    "com.github.tomakehurst"  %  "wiremock-jre8"              % "2.27.2"            % IntegrationTest,
    "org.mockito"             %  "mockito-core"               % "3.10.0"            % Test,
    "org.scalatestplus"       %% "mockito-3-4"                % "3.2.9.0"           % Test

  )
}
