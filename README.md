
# partnership-identification-frontend

This is a Scala/Play frontend to allow Partnerships to provide their information to HMRC.

### How to run the service

1. Make sure any dependent services are running using the following service-manager command
   `sm --start PARTNERSHIP_IDENTIFICATION_ALL -r`

2. Stop the frontend in service manager using
   `sm --stop PARTNERSHIP_IDENTIFICATION_FRONTEND`

3. Run the frontend locally using
   `sbt 'run 9722 -Dapplication.router=testOnlyDoNotUseInAppConf.Routes'`

## End-Points
### POST /journey

---
Creates a new journey, storing the journeyConfig against the journeyId.
#### Request:
Request body must contain the continueUrl.

```
{
  "continueUrl" : "/testUrl"
}
```

#### Response:
Status: **Created(201)**

Example Response body:

```
{“journeyStartUrl” : "/testUrl"}
```
### Test End-Points

#### GET/POST test-only/create-journey

---
This is a test entry point which simulates a service making the initial call to setup a journey.

1. ContinueURL(Required)

   - Where to redirect the user after the journey has been completed
   
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
