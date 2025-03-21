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

  private val bootstrapPlayVersion = "9.11.0"
  private val hmrcMongoVersion = "2.6.0"

  val compile = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-frontend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-30"  % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "play-frontend-hmrc-play-30" % "11.12.0", // later versions up to 7.7.0 break the footer in the html as of now 2023.06
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"         % hmrcMongoVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala"       % "2.18.3"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-30"  % bootstrapPlayVersion % Test,
    "org.scalatest"          %% "scalatest"               % "3.2.19"              % Test,
    "org.jsoup"               % "jsoup"                   % "1.19.1"             % Test,
    "org.playframework"      %% "play-test"               % PlayVersion.current  % Test,
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.64.8"             % Test,
    "org.scalatestplus.play" %% "scalatestplus-play"      % "7.0.1"              % Test,
    "com.github.tomakehurst"  % "wiremock"                % "3.0.1"              % Test,
    "org.mockito"            %% "mockito-scala-scalatest" % "1.17.37"            % Test,
    "org.scalatestplus"      %% "mockito-5-10"            % "3.2.18.0"           % Test,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-30" % hmrcMongoVersion     % Test
  )
}
