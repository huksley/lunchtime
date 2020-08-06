# Lunchtime

Provides readable schedule information for the restaurant.
Used by Restaurant App v.1.0.5+

See API documentation online: https://huksley.github.io/lunchtime/redoc.html

## Building and running locally

```sh
./gradlew npmInstall npmBuild build
./gradlew run
```

## Testing API:

```sh
curl -XPOST -d @src/main/resources/example-request.json http://localhost:8080/schedule/readable -v --header "Content-Type: application/json"
```

## Next version

We have proposed changes in [LUNCH-219](https://wolt.atlassian.net/browse/LUNCH-219) to the schedule structure:

  - [x] Multilingual support
  - [x] Fractional hour start and stop time (say 9:30am)
  - [x] Support for exceptions (holidays)
  - [x] Better reflects user behaviour in Restaurant App v. 1.2.X+ (current version in use)

Example for schedule (see JIRA task for more information):

```json
{
  "week": {
    "friday": [
      {
        "open": 28800,
        "close": 80000
      }
    ]
  },
  "exceptions": {
    "2020-12-25": [
      {
        "open": 28800,
        "close": 51200
      }
    ]
  }
}
```

Contact #prj-api-lunchtime group on Slack to reach out to us!

## Links

  * https://github.com/JetBrains/kotless/
  * https://ktor.io/servers/configuration/environments.html
  * https://kotlinlang.org/docs/reference/lambdas.html
  * https://github.com/huksley/zeebe-kafka-camunda/blob/master/workers/src/main/kotlin/zeebe/workers/order/KafkaOrderReceiver.kt
  * https://github.com/JetBrains/kotless/tree/master/examples/kotless/shortener
  * https://kotlinlang.org/docs/reference/opt-in-requirements.html
