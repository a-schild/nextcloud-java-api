# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

`nextcloud-java-api` is a Java client library (Maven artifact `org.aarboard.nextcloud:nextcloud-api`) for accessing Nextcloud/ownCloud server features from Java applications. It is a library, not an application — there is no `main` method or runnable entry point.

- Targets Java 11+ (uses the Jakarta XML binding). `maven.compiler.source`/`target` are `11`.
- Published to Maven Central via the Sonatype central-publishing plugin.
- Tested against Nextcloud 31.0.0, but intended to work against older Nextcloud/ownCloud servers too.

## Build & Test Commands

Maven is at `c:\Program Files\Apache Software Foundation\apache-maven-3.8.6`. Use a Java 11+ JDK, e.g. `c:\Program Files\Zulu\zulu-17` or `zulu-21`.

```sh
mvn clean package        # build the jar (tests are skipped by default: skipTests=true)
mvn clean install        # build and install to local repo
mvn test                 # runs tests, but see note below
```

**Tests require a live Nextcloud server and are skipped by default** (`<skipTests>true</skipTests>` in `pom.xml`). The integration tests hit a real server using these system properties, normally supplied via a `settings.xml` profile (see `README.developers.md`):

- `nextcloud.api.test.servername`, `nextcloud.api.test.serverport`, `nextcloud.api.test.username`, `nextcloud.api.test.password`

To actually run tests against a server, override the skip flag:

```sh
mvn test -DskipTests=false
mvn test -DskipTests=false -Dtest=WebDavPathResolverBuilderTest        # single test class
mvn test -DskipTests=false -Dtest=TestFolders#someTestMethod           # single method
```

Note: `WebDavPathResolverBuilderTest` is a pure unit test (no server needed); most other `Test*` classes under `src/test` extend `ATestClass` and require a reachable server.

## Versioning Workflow

- **When touching the repository** (any code/dependency/config change), make sure the `<version>` in `pom.xml` is a `-SNAPSHOT` of the *next* version. If the pom currently holds a released (non-SNAPSHOT) version, bump it by one patch and append `-SNAPSHOT` — e.g. released `14.1.4` becomes `14.1.5-SNAPSHOT`. Ongoing work then stays on that SNAPSHOT.
- **When releasing**, drop the `-SNAPSHOT` suffix so the published version is a clean release number — e.g. `14.1.5-SNAPSHOT` is released as `14.1.5`. After the release, bump again to the next `-SNAPSHOT` per the rule above.
- Follow semantic versioning (see `README.developers.md`): MAJOR = incompatible API changes, MINOR = backwards-compatible features, PATCH = backwards-compatible fixes.

## Release Process

Releasing publishes to Maven Central and requires GPG signing. Use the `release` profile / `maven-release-plugin`; GPG signing is auto-activated when `performRelease=true`. The release plugin is configured with `pushChanges=false` and `localCheckout=true`.

When changing behavior, update `Changelog.md`. Keep the `version` in `pom.xml` and the usage example in `README.md` consistent.

## Architecture

The public entry point is **`NextcloudConnector`** (`api/NextcloudConnector.java`). It is a facade constructed with server settings + credentials (basic auth or bearer token, via `AuthenticationConfig`) and delegates every operation to one of five feature connectors, which it owns:

- **`ProvisionConnector`** (`api/provisioning/`) — users, groups, quotas, user details. OCS provisioning API.
- **`FilesharingConnector`** (`api/filesharing/`) — listing and creating file/folder shares, share permissions, share types.
- **`ConfigConnector`** (`api/config/`) — app config key/value access.
- **`Folders`** and **`Files`** (`api/webdav/`) — WebDAV-based folder/file operations (upload, download, list, delete, create). Built on the Sardine WebDAV client.

`NextcloudConnector` implements `AutoCloseable`; callers should use try-with-resources. Many methods have both synchronous and `CompletableFuture`-returning async variants.

### Request/response plumbing

- **`ServerConfig`** holds host/port/https/auth and an optional sub-path prefix (parsed from a service URL).
- **`ConnectorCommon`** (`api/utils/`) is the shared HTTP engine. It uses Apache HttpAsyncClient to issue GET/POST/PUT/DELETE against the OCS API and returns `CompletableFuture<R>`. All OCS connectors funnel through it.
- Two response-parsing families implement `ConnectorCommon.ResponseParser<R>`:
  - **XML** (`XMLAnswerParser` + `XMLAnswer` subclasses) — OCS API responses use XML by default. Parsers are cached per answer class; parsing uses **Jakarta JAXB** (`jakarta.xml.bind`, with `org.glassfish.jaxb:jaxb-runtime`). This is why the library requires Java 11+.
  - **JSON** (`JsonAnswerParser` + `JsonAnswer`/`JsonListAnswer`/`JsonVoidAnswer`) — used where the API returns JSON, via Jackson. `buildUrl` appends `?format=json` when the parser is a `JsonAnswerParser`.
- WebDAV operations bypass `ConnectorCommon` and go through Sardine directly (`AWebdavHandler` is the shared base for `Files`/`Folders`).
- **`WebDavPathResolver`** (`api/webdav/pathresolver/`) builds the correct WebDAV URL path for a given Nextcloud version. Use `WebDavPathResolverBuilder`; `NextcloudVersion` parses server version strings.

### Exceptions

Custom unchecked exceptions live in `api/exception/`: `NextcloudApiException` (general wrapper), `NextcloudOperationFailedException`, `MoreThanOneShareFoundException`.

### Generated version class

`src/main/java-templates/Version.java` is a template filtered at build time by the `templating-maven-plugin` (populated from Maven/buildnumber properties). Do not edit the generated output under `target/`; edit the template instead.

## Conventions

- Source files carry a GPLv3 license header (`netbeans.hint.license=gpl30`), though the Maven POM declares Apache 2.0 for distribution — match the existing header style in the file you edit.
- Package root is `org.aarboard.nextcloud.api`.
- For new functionality, add a unit test (integration tests need a server, as noted above) and update `Changelog.md`.
