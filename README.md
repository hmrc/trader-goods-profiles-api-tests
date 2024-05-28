

# trader-goods-profiles-api-tests

Trader goods profile API tests.

## Pre-requisites

### Services

Start Mongo Docker container as follows:

```bash
docker run --rm -d -p 27017:27017 --name mongo percona/percona-server-mongodb:5.0
```

Start `<SERVICE_MANAGER_PROFILE>` services as follows:

```bash
sm2 --start TGP_API
```

## Tests

Run tests as follows:

* Argument `<environment>` must be `local`, `dev`, `qa` or `staging`.

```bash
./run-local-api-tests local
```

## Scalafmt

Check all project files are formatted as expected as follows:

```bash
sbt scalafmtCheckAll scalafmtCheck
```

Format `*.sbt` and `project/*.scala` files as follows:

```bash
sbt scalafmtSbt
```

Format all project files as follows:

```bash
sbt scalafmtAll
```
## Running ZAP specs - on a developer machine

```bash
./run-local-zap-tests.sh
``` 
Results of your ZAP run will be placed in `dast-config-manager/target/dast-reports/index.html` file
## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
