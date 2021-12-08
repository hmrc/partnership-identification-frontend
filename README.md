# Partnership Identification Frontend

This is a Scala/Play frontend to allow Partnerships to provide their information to HMRC.

### How to run the service

1. Make sure any dependent services are running using the following service-manager command
   `sm --start PARTNERSHIP_IDENTIFICATION_ALL -r`

2. Stop the frontend in service manager using
   `sm --stop PARTNERSHIP_IDENTIFICATION_FRONTEND`

3. Run the frontend locally using
   `sbt 'run 9722 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes'`

### Testing
See the TestREADME for more information on how to use our stubs for testing

# End-Points

---
## Creating the General Partnership journey
### POST /general-partnership-journey

---
Creates a new journey for a General Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

All other fields must be provided.

```
{
   "continueUrl" : "/test",
   "optServiceName" : "Service Name",
   "deskProServiceId" : "abc",
   "signOutUrl" : "/sign-out",
}
```

### POST /scottish-partnership-journey

---
Creates a new journey for a Scottish Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

All other fields must be provided.

```
{
   "continueUrl" : "/test",
   "optServiceName" : "Service Name",
   "deskProServiceId" : "abc",
   "signOutUrl" : "/sign-out",
}
```

### POST /limited-partnership-journey

---
Creates a new journey for a Limited Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

All other fields must be provided.

```
{
   "continueUrl" : "/test",
   "optServiceName" : "Service Name",
   "deskProServiceId" : "abc",
   "signOutUrl" : "/sign-out",
}
```

### POST /scottish-limited-partnership-journey

---
Creates a new journey for a Scottish Limited Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

All other fields must be provided.

```
{
   "continueUrl" : "/test",
   "optServiceName" : "Service Name",
   "deskProServiceId" : "abc",
   "signOutUrl" : "/sign-out",
}
```

### POST /limited-liability-partnership-journey

---
Creates a new journey for a Limited Liability Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

All other fields must be provided.

```
{
   "continueUrl" : "/test",
   "optServiceName" : "Service Name",
   "deskProServiceId" : "abc",
   "signOutUrl" : "/sign-out",
}
```

#### Response:
Status: **Created(201)**

Example Response body:

```
{“journeyStartUrl” : "/testUrl"}
```

### GET /journey/:journeyId

---
Retrieves all the journey data that is stored against a specific journeyID.
#### Request:
A valid journeyId must be sent in the URI

#### Response:
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
  "postcode": "AA11AA",
  "identifiersMatch": true,
  "businessVerification": {
    "verificationStatus": "PASS"
  },
  "registration": {
    "registrationStatus": "REGISTERED",
    "registeredBusinessPartnerId": "X00000123456789"
  }
}
```

---
Scottish Partnership:
```
{
  "sautr": "1234567890",
  "postcode": "AA11AA",
  "identifiersMatch": true,
  "businessVerification": {
    "verificationStatus": "PASS"
  },
  "registration": {
    "registrationStatus": "REGISTERED",
    "registeredBusinessPartnerId": "X00000123456789"
  }
}
```

---
Limited Partnership:
```
{
  "sautr": "1234567890",
  "postcode": "AA11AA",
  "identifiersMatch": true,
  "businessVerification": {
    "verificationStatus": "PASS"
  },
  "registration": {
    "registrationStatus": "REGISTERED",
    "registeredBusinessPartnerId": "X00000123456789"
  },
  "companyProfile": {
    "companyName": "TestPartnership",
    "companyNumber": "01234567",
    "dateOfIncorporation": "2020-01-01",
    "unsanitisedCHROAddress": {
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

---
Scottish Limited Partnership:
```
{
  "sautr": "1234567890",
  "postcode": "AA11AA",
  "identifiersMatch": true,
  "businessVerification": {
    "verificationStatus": "PASS"
  },
  "registration": {
    "registrationStatus": "REGISTERED",
    "registeredBusinessPartnerId": "X00000123456789"
  },
  "companyProfile": {
    "companyName": "TestPartnership",
    "companyNumber": "01234567",
    "dateOfIncorporation": "2020-01-01",
    "unsanitisedCHROAddress": {
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

---
Limited Liability Partnerships
```
{
  "sautr": "1234567890",
  "postcode": "AA11AA",
  "identifiersMatch": true,
  "businessVerification": {
    "verificationStatus": "PASS"
  },
  "registration": {
    "registrationStatus": "REGISTERED",
    "registeredBusinessPartnerId": "X00000123456789"
  },
  "companyProfile": {
    "companyName": "TestPartnership",
    "companyNumber": "01234567",
    "dateOfIncorporation": "2020-01-01",
    "unsanitisedCHROAddress": {
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

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
