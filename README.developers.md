# nextcloud-java-api contributors infos
Java api library to access nextcloud features from java applications

## Versioning
Starting with 11.1.0 we use semanic versioning according to
https://semver.org/spec/v2.0.0.html

In short:
Given a version number MAJOR.MINOR.PATCH, increment the:

    MAJOR version when you make incompatible API changes,
    MINOR version when you add functionality in a backwards compatible manner, and
    PATCH version when you make backwards compatible bug fixes.

Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.

## Changelog
If enhancing the code base, please also update the [Changelog](Changelog.md)

## Unit tests
For all new functionality, please provide a unit test.

The integration tests need a running Nextcloud server. There are two ways to
provide one:

### Option A: automatic throw-away server (recommended)
If [Docker](https://www.docker.com/) is running and you do **not** configure an
external server, the test suite automatically starts a throw-away Nextcloud
container (via [Testcontainers](https://java.testcontainers.org/)), runs the
tests against it, and removes it afterwards. Just run:

```
mvn test -DskipTests=false
```

Tests are skipped by default (`skipTests=true`). When Docker is not available
and no external server is configured, the tests skip silently. This same
mechanism runs the tests in CI (see `.github/workflows/ci.yml`).

### Option B: your own server
Alternatively, point the tests at an existing Nextcloud server by specifying a
valid server name and admin credentials. When these properties are set, the
container is **not** started and your server is used instead.

You can specify them in your settings.xml file in this way:
``` XML
<settings>
    <profiles>
        <profile>
            <id>nextcloud.api.test</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <nextcloud.api.test.servername>test.nextcloud.org</nextcloud.api.test.servername>
                <nextcloud.api.test.serverport>444</nextcloud.api.test.serverport>
                <nextcloud.api.test.username>admin</nextcloud.api.test.username>
                <nextcloud.api.test.password>adminp@ssw0rd</nextcloud.api.test.password>
            </properties>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>nextcloud.api.test</activeProfile>
    </activeProfiles>
</settings>
```


(c) André Schild, Aarboard AG www.aarboard.ch
