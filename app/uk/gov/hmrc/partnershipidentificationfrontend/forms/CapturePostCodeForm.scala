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

package uk.gov.hmrc.partnershipidentificationfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraint
import uk.gov.hmrc.partnershipidentificationfrontend.forms.utils.ConstraintUtil._
import uk.gov.hmrc.partnershipidentificationfrontend.forms.utils.ValidationHelper._

object CapturePostCodeForm {

  val postCode = "postcode"

  val postCodeRegex = """^[A-Z]{1,2}[0-9][0-9A-Z]?\s?[0-9][A-Z]{2}$"""

  val postCodeNotEntered: Constraint[String] = Constraint("postcode.not-entered")(
    postCode => validate(
      constraint = postCode.isEmpty,
      errMsg = "capture-postcode.error.not_entered"
    )
  )

  val postCodeInvalid: Constraint[String] = Constraint("postcode.invalid-format")(
    postCode => validateNot(
      constraint = postCode matches postCodeRegex,
      errMsg = "capture-postcode.error.invalid_format"
    )
  )

  val postCodeForm: Form[String] = Form(
    single(
      postCode -> text.verifying(postCodeNotEntered andThen postCodeInvalid)
    )
  )

}
