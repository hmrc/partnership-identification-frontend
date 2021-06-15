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

package uk.gov.hmrc.partnershipidentificationfrontend.assets

object MessageLookup {

  object Base {
    val confirmAndContinue = "Confirm and continue"
    val change = "Change"
    val saveAndContinue = "Save and continue"
    val saveAndComeBack = "Save and come back later"
    val getHelp = "Is this page not working properly?"

    object Error {
      val title = "There is a problem"
      val error = "Error: "
    }

  }

  object Header {
    val signOut = "Sign out"
  }

  object BetaBanner {
    val title = "This is a new service - your feedback (opens in new tab) will help us to improve it."
  }

  object CaptureSautr {
    val title = "What is your Unique Taxpayer Reference?"
    val heading = "What is your Unique Taxpayer Reference?"
    val line_1 = "This is 10 numbers, for example 1234567890. It will be on tax returns and other letters about Self Assessment. It may be called ‘reference’, ‘UTR’ or ‘official use’. You can find a lost UTR number."
    val line_2 = "I do not have a Unique Taxpayer Reference"
    val details_line_1 = "Your UTR helps us identify your partnership."
    val details_line_2 = "I cannot find my UTR"
    val details_line_3 = "My partnership does not have a UTR"

    object Error {
      val invalidSautrEntered = "Enter a Unique Taxpayer Reference in the correct format"
    }
  }

  object CapturePostCode {
    val title = "What is the postcode where the partnership is registered for Self Assessment?"
    val heading = "What is the postcode where the partnership is registered for Self Assessment?"
    val hint = "For example, AB1 2YZ"

    object Error {
      val noPostCodeEntered = "Enter the postcode where the partnership is registered for Self Assessment"
      val invalidPostCodeEntered = "Enter a valid postcode"
      val invalidCharactersEntered = "Enter a postcode using only letters and numbers"
    }
  }
}
