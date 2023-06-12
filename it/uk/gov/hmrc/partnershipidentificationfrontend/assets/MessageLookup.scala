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

package uk.gov.hmrc.partnershipidentificationfrontend.assets

object MessageLookup {

  object Base {
    val confirmAndContinue = "Confirm and continue"
    val change = "Change"
    val saveAndContinue = "Save and continue"
    val saveAndComeBack = "Save and come back later"
    val getHelp = "Is this page not working properly? (opens in new tab)"
    val back = "Back"
    val tryAgain = "Try again"
    val yes = "Yes"
    val no = "No"
    val continue = "Continue"

    object Error {
      val title = "There is a problem"
      val error = "Error: "
    }

  }

  object Header {
    val signOut = "Sign out"
  }

  object BetaBanner {
    val title = "This is a new service – your feedback will help us to improve it."
  }

  object CaptureCompanyNumber {
    val title = "What is the company registration number? - Entity Validation Service - GOV.UK"
    val pageConfigTestTitle = "What is the company registration number? - Test Service - GOV.UK"
    val heading = "What is the company registration number?"
    val line_1 = "You can search Companies House for the company registration number (opens in a new tab)"
    val link = "https://beta.companieshouse.gov.uk/"
    val hint = "It is 8 characters. For example, 01234567 or AC012345."

    object Error {
      val noCompanyNumber = "Enter the company registration number"
      val invalidLengthCompanyNumber = "The company registration number must be 8 characters or fewer"
      val invalidCompanyNumber = "Enter the company registration number in the correct format"
    }
  }

  object ConfirmPartnershipName {
    val title = "Confirm the company name - Entity Validation Service - GOV.UK"
    val pageConfigTestTitle = "Confirm the company name - Test Service - GOV.UK"
    val heading = "Confirm the company name"
    val change_company_link = "Change company"
  }

  object CompanyNumberNotFound {
    val title = "We could not confirm the partnership’s company registration number - Entity Validation Service - GOV.UK"
    val pageConfigTestTitle = "We could not confirm the partnership’s company registration number - Test Service - GOV.UK"
    val heading = "We could not confirm the partnership’s company registration number"
    val paragraph = "The company number is not in our system."
  }

  object CaptureSautr {
    val title = "What is the partnership’s Unique Taxpayer Reference? - Entity Validation Service - GOV.UK"
    val pageConfigTestTitle = "What is the partnership’s Unique Taxpayer Reference? - Test Service - GOV.UK"
    val heading = "What is the partnership’s Unique Taxpayer Reference?"
    val line_1 = "This is 10 numbers, for example 1234567890. It will be on tax returns and other letters about Self Assessment. It may be called ‘reference’, ‘UTR’ or ‘official use’."
    val line_2 = "I do not have a Unique Taxpayer Reference"
    val details_line_1 = "Your UTR helps us identify your partnership."
    val details_line_2 = "I cannot find my UTR"
    val details_line_3 = "My partnership does not have a UTR"
    val link_1 = "I cannot find my UTR"

    object Error {
      val saUtrNotEntered = "Enter the partnership’s Unique Taxpayer Reference"
      val invalidSautrEntered = "Enter the partnership’s Unique Taxpayer Reference in the correct format"
    }

  }

  object CapturePostCode {
    val title = "Enter the postcode the partnership used to register for Self Assessment - Entity Validation Service - GOV.UK"
    val pageConfigTestTitle = "Enter the postcode the partnership used to register for Self Assessment - Test Service - GOV.UK"
    val heading = "Enter the postcode the partnership used to register for Self Assessment"
    val subTitle = "You can find this information in box 2 of your partnership for Self Assessment form (SA400), under the heading About the partnership."
    val hint = "For example, AB1 2YZ"

    object Error {
      val noPostCodeEntered = "Enter the postcode used to register the partnership for Self Assessment"
      val invalidPostCodeEntered = "Enter the postcode in the correct format"
    }

  }

  object CannotConfirmBusiness {
    val title = "The details you provided do not match records held by HMRC - Entity Validation Service - GOV.UK"
    val heading = "The details you provided do not match records held by HMRC"
    val line_1 = "If these details are correct, you can still register. If you entered the wrong details, go back and make changes."
    val radio = "Do you want to continue registering with the details you provided?"

    object Error {
      val no_selection = "Select yes if you want to continue registering with the details you provided"
    }

  }

  object CheckYourAnswers {
    val title = "Check your answers - Entity Validation Service - GOV.UK"
    val heading = "Check your answers"
    val sautr = "Unique Taxpayer Reference"
    val postCode = "Self Assessment postcode"
    val noSautr = "The business does not have a UTR"
    val companyNumber = "Company registration number"
  }

}
