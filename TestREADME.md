# Partnership Identification Frontend Test End-Points

#### GET test-only/feature-switches

---
Shows all feature switches:
1. Partnership Identification Frontend

    - Use stub for Business Verification flow

2. Partnership Identification

    - Use stub for Partnership Known Facts SAUTR call

#### GET test-only/create-general-partnership-journey

---
This is a test entry point which simulates a service by triggering the initial POST call to set up a journey for General Partnerships.

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

#### GET test-only/create-scottish-partnership-journey

---
This is a test entry point which simulates a service by triggering the initial POST call to set up a journey for Scottish Partnerships.

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

#### GET test-only/create-scottish-limited-partnership-journey

   ---
   This is a test entry point which simulates a service by triggering the initial POST call to set up a journey for Scottish Limited Partnerships.

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

#### GET test-only/retrieve-journey/:journeyId or test-only/retrieve-journey

---
Retrieves all the journey data that is stored against a specific journeyID.

##### Request:
A valid journeyId must be sent in the URI or as a query parameter

##### Response:
Status:

| Expected Response                       | Reason
|-----------------------------------------|------------------------------
| ```OK(200)```                           |  ```JourneyId exists```
| ```NOT_FOUND(404)```                    | ```JourneyId doesn't exist```

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

#### POST test-only/verification-question/journey

---
Stubs creating a Business Verification journey. The Business Verification Stub Feature Switch will need to be enabled.

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
Stubs retrieving the result from the Business Verification Service. The Business Verification Stub feature switch will need to be enabled.

##### Request:
A valid Business Verification journeyId must be sent in the URI

##### Response:
Status: **OK(200)**

Response body:
```
{
  "journeyType": "BUSINESS_VERIFICATION",
  "origin": vat,
  "identifier": {
    "saUtr" -> "1234567890"
  },
  "verificationStatus" -> "PASS"
}
```
