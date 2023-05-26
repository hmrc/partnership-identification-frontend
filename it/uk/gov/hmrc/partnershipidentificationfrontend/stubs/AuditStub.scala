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

package uk.gov.hmrc.partnershipidentificationfrontend.stubs

import com.github.tomakehurst.wiremock.client.WireMock.{postRequestedFor, urlEqualTo, verify}
import com.github.tomakehurst.wiremock.http.Request
import com.github.tomakehurst.wiremock.matching.MatchResult.{exactMatch, noMatch}
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsDefined, JsObject, Json}
import uk.gov.hmrc.partnershipidentificationfrontend.utils.WiremockHelper.stubPost

import scala.util.{Success, Try}

trait AuditStub {
  private val httpStatusRecognisedByFrontEnd = 204

  def stubAudit(): StubMapping = {
    stubPost("/write/audit", status = httpStatusRecognisedByFrontEnd, responseBody = "{}")
    stubPost("/write/audit/merged", status = httpStatusRecognisedByFrontEnd, responseBody = "{}")
  }

  def verifyAuditDetail(expectedAudit: JsObject): Unit = {
    val uriMapping = postRequestedFor(urlEqualTo("/write/audit"))
    val postRequest = uriMapping.andMatching {
      (request: Request) =>
        Try(Json.parse(request.getBodyAsString)) match {
          case Success(auditJson) => auditJson \ "detail" match {
            case JsDefined(auditDetail) if auditDetail == expectedAudit => exactMatch()
            case fail => {
              noMatch()
            }
          }
          case _ => noMatch()
        }
    }
    verify(postRequest)
  }
}
