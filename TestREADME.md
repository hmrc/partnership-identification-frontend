# Partnership Identification Frontend Test End-Points

#### GET test-only/feature-switches

---
Shows all feature switches:
1. Partnership Identification Frontend

    - Use stub for Business Verification flow
    
2. Partnership Identification

    - Use stub for Partnership Known Facts SAUTR call

#### GET test-only/create-journey

---
This is a test entry point which simulates a service by triggering the initial POST call to set up a journey.

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
