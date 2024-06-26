# Partnership Identification Frontend

This is a Scala/Play frontend to allow Partnerships to provide their information to HMRC.

### How to run the service

1. Make sure any dependent services are running using the following service-manager command
```bash
sm2 --start PARTNERSHIP_IDENTIFICATION_ALL
```
2. Stop the frontend in service manager using
```bash
sm2 --stop PARTNERSHIP_IDENTIFICATION_FRONTEND
```
3. Run the frontend locally using
```bash
sbt 'run 9722 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes'
```

### Unit Testing
```bash
sbt clean coverage test coverageReport
``` 

### Integration Testing
```bash
sbt clean coverage it/test coverageReport
``` 

By default, for each ISpec test a new JVM will run and all tests will run sequentially.  
To run all ISpec tests in a single JVM start sbt with the -DisADevMachine=true property and then run `Test` task:
```bash
sbt -DisADevMachine=true clean coverage it/test coverageReport
``` 

### Testing Endpoints

See the [TestREADME](TestREADME.md) for more information on how to use our stubs for testing

# End-Points

---
### POST /general-partnership-journey

---
Creates a new journey for a General Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

If the user provides a SA UTR during the journey and the UTR passes the matching check, by default a
business verification check will be performed before registration. This check can be disabled by submitting
the field businessVerificationCheck with a value of false. If not provided, a default value of true is set
for the field.

Labels "cy" and "en" enable welsh and english translations for the service name to be provided by the calling service respectively.
If the labels property is omitted or present, but the "cy" property is not fully defined, the service's default values will be
used for the undefined properties.

All other fields must be provided.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

The deskProServiceId is used to set the service name in the beta feedback url.

```
{
  "continueUrl": "/test",
  "businessVerificationCheck": false,
  "optServiceName": "Service Name", // deprecated, use labels.en.optServiceName
  "deskProServiceId": "abc",
  "signOutUrl": "/sign-out",
  "accessibilityUrl": "/accessibility-statement/my-service",
  "regime": "VATC",
  "labels": {
    "cy": {
      "optServiceName": "Service name translated into welsh"
    },
    "en": {
      "optServiceName": "Service name in english"
    }
  }
}
```

#### Response:
Status: **Created(201)**

Example Response body:

```
{“journeyStartUrl” : "<protocol>://<host>:<port number>/identify-your-partnership/<journey id>/sa-utr"}
```

where protocol, host and port number are set to the values for the appropriate environment and journey id is used to identify the specific user journey.

### POST /scottish-partnership-journey

---
Creates a new journey for a Scottish Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

If the user provides a SA UTR during the journey and the UTR passes the matching check, by default a
business verification check will be performed before registration. This check can be disabled by submitting
the field businessVerificationCheck with a value of false. If not provided, a default value of true is set
for the field.

Labels "cy" and "en" enable welsh and english translations for the service name to be provided by the calling service respectively.
If the labels property is omitted or present, but the "cy" property is not fully defined, the service's default values will be
used for the undefined properties.

All other fields must be provided.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

The deskProServiceId is used to set the service name in the beta feedback url.

```
{
  "continueUrl": "/test",
  "businessVerificationCheck": false,
  "optServiceName": "Service Name", // deprecated, use labels.en.optServiceName
  "deskProServiceId": "abc",
  "signOutUrl": "/sign-out",
  "accessibilityUrl": "/accessibility-statement/my-service",
  "regime": "VATC",
  "labels": {
    "cy": {
      "optServiceName": "Service name translated into welsh"
    },
    "en": {
      "optServiceName": "Service name in english"
    }
  }
}
```

#### Response:
Status: **Created(201)**

Example Response body:

```
{“journeyStartUrl” : "<protocol>://<host>:<port number>/identify-your-partnership/<journey id>/sa-utr"}
```

where protocol, host and port number are set to the values for the appropriate environment and journey id is used to identify the specific user journey.

### POST /limited-partnership-journey

---
Creates a new journey for a Limited Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

Labels "cy" and "en" enable welsh and english translations for the service name to be provided by the calling service respectively.
If the labels property is omitted or present, but the "cy" property is not fully defined, the service's default values will be
used for the undefined properties..

All other fields must be provided.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

The deskProServiceId is used to set the service name in the beta feedback url.

```
{
  "continueUrl": "/test",
  "optServiceName": "Service Name", // deprecated, use labels.en.optServiceName
  "deskProServiceId": "abc",
  "signOutUrl": "/sign-out",
  "accessibilityUrl": "/accessibility-statement/my-service",
  "regime": "VATC",
  "labels": {
    "cy": {
      "optServiceName": "Service name translated into welsh"
    },
    "en": {
      "optServiceName": "Service name in english"
    }
  }
}
```

#### Response:
Status: **Created(201)**

Example Response body:

```
{“journeyStartUrl” : "<protocol>://<host>:<port number>/identify-your-partnership/<journey id>/company-registration-number"}
```

### POST /scottish-limited-partnership-journey

---
Creates a new journey for a Scottish Limited Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

Labels "cy" and "en" enable welsh and english translations for the service name to be provided by the calling service respectively.
If the labels property is omitted or present, but the "cy" property is not fully defined, the service's default values will be
used for the undefined properties.

All other fields must be provided.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

The deskProServiceId is used to set the service name in the beta feedback url.

```
{
  "continueUrl": "/test",
  "optServiceName": "Service Name", // deprecated, use labels.en.optServiceName
  "deskProServiceId": "abc",
  "signOutUrl": "/sign-out",
  "accessibilityUrl": "/accessibility-statement/my-service",
  "regime": "VATC",
  "labels": {
    "cy": {
      "optServiceName": "Service name translated into welsh"
    },
    "en": {
      "optServiceName": "Service name in english"
    }
  }
}
```

#### Response:
Status: **Created(201)**

Example Response body:

```
{“journeyStartUrl” : "<protocol>://<host>:<port number>/identify-your-partnership/<journey id>/company-registration-number"}
```

where protocol, host and port number are set to the values for the appropriate environment and journey id is used to identify the specific user journey.

### POST /limited-liability-partnership-journey

---
Creates a new journey for a Limited Liability Partnership, storing the journeyConfig against the journeyId.
#### Request:
optServiceName will default to `Entity Validation Service` if the field is not provided.

Labels "cy" and "en" enable welsh and english translations for the service name to be provided by the calling service respectively.
If the labels property is omitted or present, but the "cy" property is not fully defined, the service's default values will be
used for the undefined properties.

All other fields must be provided.

All URLs provided must be relative, apart from locally where localhost is allowed. If you need to call out to Business
Verification (rather than stub it) all non-relative urls will cause the handover to Business Verification to fail.

The deskProServiceId is used to set the service name in the beta feedback url.

```
{
  "continueUrl": "/test",
  "optServiceName": "Service Name", // deprecated, use labels.en.optServiceName
  "deskProServiceId": "abc",
  "signOutUrl": "/sign-out",
  "accessibilityUrl": "/accessibility-statement/my-service",
  "regime": "VATC",
  "labels": {
    "cy": {
      "optServiceName": "Service name translated into welsh"
    },
    "en": {
      "optServiceName": "Service name in english"
    }
  }
}
```

#### Response:
Status: **Created(201)**

Example Response body:

```
{“journeyStartUrl” : "<protocol>://<host>:<port number>/identify-your-partnership/<journey id>/company-registration-number"}
```

### GET /journey/:journeyId

---
Retrieves all the journey data that is stored against a specific journeyID.
#### Request:
A valid journeyId must be sent in the URI

#### Response:
Status:

| Expected Response    | Reason                        |
|----------------------|-------------------------------|
| ```OK(200)```        | ```JourneyId exists```        |
| ```NOT_FOUND(404)``` | ```JourneyId doesn't exist``` |

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
