# Partnership Identification Frontend Test End-Points

## Testing

---

1. [Setting up the Feature Switches](TestREADME.md#get-test-onlyfeature-switches)
2. [Setting up a General Partnership Journey](TestREADME.md#get-test-onlycreate-general-partnership-journey)
3. [Setting up a Scottish Partnership Journey](TestREADME.md#get-test-onlycreate-scottish-partnership-journey)
4. [Setting up a Limited Partnership Journey](TestREADME.md#get-test-onlycreate-limited-partnership-journey)
5. [Setting up a Scottish Limited Partnership Journey](TestREADME.md#get-test-onlycreate-scottish-limited-partnership-journey)
6. [Setting up a Limited Liability Partnership Journey](TestREADME.md#get-test-onlycreate-limited-liability-partnership-journey)
7. [Retrieving Journey Data](TestREADME.md#get-test-onlyretrieve-journeyjourneyid-or-test-onlyretrieve-journey)
8. Business Verification Stub
    1. [Creating a Business Verification journey](TestREADME.md#post-test-onlyverification-questionjourney)
    2. [Retrieving Business Verification result](TestREADME.md#get--test-onlyverification-questionjourneyjourneyidstatus)
9. [Companies House Stub](TestREADME.md#get-test-onlycompanynumberincorporated-company-profile)

#### GET test-only/feature-switches

---
Shows all feature switches:

1. Partnership Identification Frontend

    - Use stub for Business Verification flow
    - Use stub for Companies House API

2. Partnership Identification

    - Use stub for Partnership Known Facts SAUTR call
    - Use stub for register API

#### GET test-only/create-general-partnership-journey

---
This is a test entry point which simulates a service by triggering the initial POST call to set up a journey for General
Partnerships.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

1. Continue URL (Required)

    - Where to redirect the user after the journey has been completed

2. Service Name (Optional)

    - Service Name to use throughout the service
    - Currently, this is empty by default, so the default service name will be used

3. DeskPro Service ID (Required)

    - Used for the `Get help with this page` link
    - This is currently autofilled but can be changed

4. Sign Out URL (Required)

    - Shown in the HMRC header - typically a link to a feedback questionnaire
    - This is currently autofilled but can be changed

5. Business verification checkbox

    - Used for skipping further verification checks carried out currently by Business Verification (SI)
    - This is currently autofilled but can be changed

6. Accessibility Statement URL

    - Shown in the footer - a link to the accessibility statement for the calling service
    - This is currently autofilled but can be changed

7. Regime

    - This is the Tax Regime Identifier
    - It is passed down to the Registration API
    - Accepted values are PPT or VATC

8. Welsh translation for Service Name (Optional)

    - Welsh language translation for service name (item 2)

#### GET test-only/create-scottish-partnership-journey

---
This is a test entry point which simulates a service by triggering the initial POST call to set up a journey for
Scottish Partnerships.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

1. Continue URL (Required)

    - Where to redirect the user after the journey has been completed

2. Service Name (Optional)

    - Service Name to use throughout the service
    - Currently, this is empty by default, so the default service name will be used

3. DeskPro Service ID (Required)

    - Used for the `Get help with this page` link
    - This is currently autofilled but can be changed

4. Sign Out URL (Required)

    - Shown in the HMRC header - typically a link to a feedback questionnaire
    - This is currently autofilled but can be changed

5. Business verification checkbox

    - Used for skipping further verification checks carried out currently by Business Verification (SI)
    - This is currently autofilled but can be changed

6. Accessibility Statement URL

    - Shown in the footer - a link to the accessibility statement for the calling service
    - This is currently autofilled but can be changed

7. Regime

    - This is the Tax Regime Identifier
    - It is passed down to the Registration API
    - Accepted values are PPT or VATC

8. Welsh translation for Service Name (Optional)

    - Welsh language translation for service name (item 2)

#### GET test-only/create-limited-partnership-journey

---
This is a test entry point which simulates a service by triggering the initial POST call to set up a journey for Limited
Partnership.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

1. Continue URL (Required)

    - Where to redirect the user after the journey has been completed

2. Service Name (Optional)

    - Service Name to use throughout the service
    - Currently, this is empty by default, so the default service name will be used

3. DeskPro Service ID (Required)

    - Used for the `Get help with this page` link
    - This is currently autofilled but can be changed

4. Sign Out URL (Required)

    - Shown in the HMRC header - typically a link to a feedback questionnaire
    - This is currently autofilled but can be changed

5. Business verification checkbox

    - Used for skipping further verification checks carried out currently by Business Verification (SI)
    - This is currently autofilled but can be changed

6. Accessibility Statement URL

    - Shown in the footer - a link to the accessibility statement for the calling service
    - This is currently autofilled but can be changed

7. Regime

    - This is the Tax Regime Identifier
    - It is passed down to the Registration API
    - Accepted values are PPT or VATC

8. Welsh translation for Service Name (Optional)

    - Welsh language translation for service name (item 2)

#### GET test-only/create-scottish-limited-partnership-journey

---
This is a test entry point which simulates a service by triggering the initial POST call to set up a journey for
Scottish Limited Partnerships.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

1. Continue URL (Required)

    - Where to redirect the user after the journey has been completed

2. Service Name (Optional)

    - Service Name to use throughout the service
    - Currently, this is empty by default, so the default service name will be used

3. DeskPro Service ID (Required)

    - Used for the `Get help with this page` link
    - This is currently autofilled but can be changed

4. Sign Out URL (Required)

    - Shown in the HMRC header - typically a link to a feedback questionnaire
    - This is currently autofilled but can be changed

5. Business verification checkbox

    - Used for skipping further verification checks carried out currently by Business Verification (SI)
    - This is currently autofilled but can be changed

6. Accessibility Statement URL

    - Shown in the footer - a link to the accessibility statement for the calling service
    - This is currently autofilled but can be changed

7. Regime

    - This is the Tax Regime Identifier
    - It is passed down to the Registration API
    - Accepted values are PPT or VATC

8. Welsh translation for Service Name (Optional)

    - Welsh language translation for service name (item 2)
    -

#### GET test-only/create-limited-liability-partnership-journey

---
This is a test entry point which simulates a service by triggering the initial POST call to set up a journey for Limited
Liability Partnerships.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

1. Continue URL (Required)

    - Where to redirect the user after the journey has been completed

2. Service Name (Optional)

    - Service Name to use throughout the service
    - Currently, this is empty by default, so the default service name will be used

3. DeskPro Service ID (Required)

    - Used for the `Get help with this page` link
    - This is currently autofilled but can be changed

4. Sign Out URL (Required)

    - Shown in the HMRC header - typically a link to a feedback questionnaire
    - This is currently autofilled but can be changed

5. Business verification checkbox

    - Used for skipping further verification checks carried out currently by Business Verification (SI)
    - This is currently autofilled but can be changed

6. Accessibility Statement URL

    - Shown in the footer - a link to the accessibility statement for the calling service
    - This is currently autofilled but can be changed

7. Regime

    - This is the Tax Regime Identifier
    - It is passed down to the Registration API
    - Accepted values are PPT or VATC

8. Welsh translation for Service Name (Optional)

    - Welsh language translation for service name (item 2)

#### GET test-only/retrieve-journey/:journeyId or test-only/retrieve-journey

---
Retrieves all the journey data that is stored against a specific journeyID.

##### Request:

A valid journeyId must be sent in the URI or as a query parameter

##### Response:

Status:

| Expected Response                       | Reason                        |
|-----------------------------------------|-------------------------------|
| ```OK(200)```                           | ```JourneyId exists```        |
| ```NOT_FOUND(404)```                    | ```JourneyId doesn't exist``` |

Example response bodies:

---
General Partnership:

```
{
   "sautr": "1234567890",
   "postcode": "AA11AA"
   "identifiersMatch": true,
   "businessVerification": {
        "verificationStatus":"PASS"
      },
    "registration": {
        "registrationStatus":"REGISTERED",
        "registeredBusinessPartnerId":"X00000123456789"
      }
}
```

---
Scottish Partnership:

```
{
   "sautr": "1234567890",
   "postcode": "AA11AA"
   "identifiersMatch": true,
   "businessVerification": {
        "verificationStatus":"PASS"
      },
    "registration": {
        "registrationStatus":"REGISTERED",
        "registeredBusinessPartnerId":"X00000123456789"
      }
}
```

---
Limited Partnership:

```
{
   "sautr": "1234567890",
   "postcode": "AA11AA"
   "identifiersMatch": true,
   "businessVerification": {
        "verificationStatus":"PASS"
      },
    "registration": {
        "registrationStatus":"REGISTERED",
        "registeredBusinessPartnerId":"X00000123456789"
      }
}
```

---
Scottish Limited Partnership:

```
{
   "sautr": "1234567890",
   "postcode": "AA11AA"
   "identifiersMatch": true,
   "businessVerification": {
        "verificationStatus":"PASS"
      },
    "registration": {
        "registrationStatus":"REGISTERED",
        "registeredBusinessPartnerId":"X00000123456789"
      }
}

```

---
Limited Liability Partnership:

```
{
   "sautr": "1234567890",
   "postcode": "AA11AA"
   "identifiersMatch": true,
   "businessVerification": {
        "verificationStatus":"PASS"
      },
    "registration": {
        "registrationStatus":"REGISTERED",
        "registeredBusinessPartnerId":"X00000123456789"
      }
}
```

#### POST test-only/verification-question/journey

---
Stubs creating a Business Verification journey. The Business Verification Stub Feature Switch will need to be enabled. This bypasses the whole Business Verification journey.

##### Request:

No body is required for this request

##### Response:

Status: **Created(201)**

Example Response body:

```
{“redirectUri” : "/testUrl?journeyId=<businessVerificationJourneyId>"}
```

#### GET  test-only/verification-question/journey/:journeyId/status

---
Stubs retrieving the result from the Business Verification Service. The Business Verification Stub feature switch will
need to be enabled. Always returns BusinessVerification Status as PASS.

##### Request:

A valid Business Verification journeyId must be sent in the URI

##### Response:

Status: **OK(200)**

Response body:

```
{
  "journeyType": "BUSINESS_VERIFICATION",
  "origin": vatc,
  "identifier": {
    "saUtr" -> "1234567890"
  },
  "verificationStatus" -> "PASS"
}
```

### GET test-only/:companyNumber/incorporated-company-profile

---
Stubs retrieving the Company Profile from Companies House. The Companies House API stub feature switch will need to be
enabled.

##### Request:

A valid company Number must be sent in the URI

##### Response:

Status:

| Expected Response                       | Reason                                                      | Example                                |
|-----------------------------------------|-------------------------------------------------------------|----------------------------------------|
| ```NOT_FOUND(404)```                    | ```Company Number doesn't exist```                          | ```00000001```                         |
| ```OK(200)```                           | ```Company postal_code = BB11BB (postCode not maching)```   | ```00000002```                         |
| ```OK(200)```                           | ```company_name longer than 105 chars```                    | ```00000003```                         |
| ```OK(200)```                           | ```Company Number exists```                                 | ```Any other valid Company Number```   |

Example response body:

```
{
  "companyProfile": {
    "companyName":"TestCompanyLtd”,
    “companyNumber":"01234567",
    "dateOfIncorporation":"2020-01-01",
    "unsanitisedCHROAddress": {
      "address_line_1":"testLine1",
      "address_line_2":"test town",
      "care_of":"test name",
      "country":"United Kingdom",
      "locality":"test city",
      "po_box":"123",
      "postal_code":"AA11AA",
      "premises":"1",
      "region":"test region"
    }
  }
}
```

```
{
  "companyProfile": {
    "company_name": "This company name is longer than 105 chars - This company name is longer than 105 chars - This company name is longer than 105 chars",
    "company_number": "00000003",
    "date_of_creation": "2020-01-01",
    "registered_office_address": {
      "address_line_1": "testLine1",
      "address_line_2": "test town",
      "care_of": "test name",
      "country": "United Kingdom",
      "locality": "test city",
      "po_box": "123",
      "postal_code": "AA11AA",
      "premises": "1",
      "region": "test region"
    }
  }
}
```

### License

This code is open source software licensed under
the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
