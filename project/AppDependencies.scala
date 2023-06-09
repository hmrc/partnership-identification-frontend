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

import play.core.PlayVersion
import sbt.*

object AppDependencies {

  private val bootstrapPlayVersion = "7.15.0"
  private val hmrcMongoVersion = "0.74.0"

  val compile = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-28"  % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "play-frontend-hmrc"         % "4.1.0-play-28", // later versions up to 7.7.0 break the footer in the html as of now 2023.06
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-28"         % hmrcMongoVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % "2.14.2"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % bootstrapPlayVersion % Test,
    "org.scalatest"          %% "scalatest"               % "3.1.1"              % Test,
    "org.jsoup"               % "jsoup"                   % "1.13.1"             % Test,
    "com.typesafe.play"      %% "play-test"               % PlayVersion.current  % Test,
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.36.8"             % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"      % "5.1.0"              % "test, it",
    "com.github.tomakehurst"  % "wiremock-jre8"           % "2.31.0"             % IntegrationTest,
    "org.mockito"            %% "mockito-scala-scalatest" % "1.16.55"            % Test,
    "org.scalatestplus"      %% "mockito-3-4"             % "3.2.10.0"           % Test,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % hmrcMongoVersion     % Test
  )
}
