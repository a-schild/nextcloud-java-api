# Changelog for nextcloud api

## Version 14.2.0
- 2026-07-26
- Fix connector lifecycle: closing a `NextcloudConnector` now shuts down the
  shared HTTP client only once the last open connector is closed, so closing
  one connector no longer breaks others still in use (issue #87). Use
  `shutdown()` to force an immediate teardown.
- Add system tags support: list, create and delete system tags, and assign or
  remove tags on a file via the new `SystemTags` connector and
  `NextcloudConnector` methods (issue #110)
- Security: harden XML response parsing against XXE by disabling DTDs and
  external entities in the StAX parser
- Security: no longer embed the basic-auth credentials in the request URL
  (they are sent via the request context instead), avoiding password leakage
  through logs, proxies or exceptions
- Security/correctness: the shared HTTP client is now cached per security
  configuration (trust-all / proxy) instead of a single JVM-wide instance, so a
  connector using `trustAllCertificates(true)` can no longer cause a later,
  secure connector to run with certificate validation disabled. Client creation
  is now also thread-safe.
- Security: reject path separators in user/group identifiers used as URL path
  segments (user and group provisioning, group folder group names) to prevent
  URL/path injection
- Security: add `trustCertificate(X509Certificate)` / `trustCertificates(InputStream)`
  to trust a specific self-signed or private-CA certificate while keeping
  certificate-chain and hostname verification enabled (a safe alternative to
  `trustAllCertificates`, which now also logs a warning when enabled)
- WebDAV (file/folder) operations now honour the same TLS trust configuration
  (`trustAllCertificates` / `trustCertificate`) as the OCS calls, so file
  transfers work against servers with a self-signed or private-CA certificate
  (issue #125)
- Add support for the Group Folders app: create, rename, delete and list group
  folders, grant/revoke group access, set group permissions and set the folder
  quota via the new `GroupFolders` connector and `NextcloudConnector` methods
  (issue #109). Thanks to Denis Verkhovsky for the original implementation the
  port is based on.
- Extend OCS Share API coverage (issue #107):
  - Federated/remote shares: list accepted and pending shares, get info, delete,
    and accept/decline pending shares
  - (Re)send the share notification email via `sendShareEmail`
  - New `ShareType` values `CIRCLE` (7) and `TALK` (10)
  - New `ShareData` attributes `NOTE`, `LABEL`, `ATTRIBUTES`, `SENDMAIL` for
    `editShare`, and `Share` now exposes `getNote()` / `getLabel()`

## Version 14.1.6
- 2026-07-24
- Add optional `expireDate` parameter to `doShare` / `doShareAsync`, so an
  expiration date can be set when creating a share (issue #76)
- Fix JSON parsing of empty OCS results: the API serializes empty collections
  as `[]` instead of `{}`, which caused a `MismatchedInputException` when e.g.
  listing users or groups on an empty result (issue #112)
- Fix file uploads (and other WebDAV writes) failing on servers running on a
  non-standard port: preemptive authentication now uses the configured port, so
  the server no longer issues an auth challenge that a streamed upload cannot
  retry (issue #112)
- Testing: integration tests can now auto-provision a throw-away Nextcloud
  server via Testcontainers when Docker is available, and are executed in CI
  (GitHub Actions)

## Version 14.1.5
- 2026-07-24
  - Updated dependencies:
    - jackson-databind 2.20.0 -> 2.22.1
    - commons-codec 1.19.0 -> 1.22.0
    - commons-io 2.20.0 -> 2.22.0
    - commons-lang3 3.19.0 -> 3.20.0
    - jaxb-runtime 4.0.6 -> 4.0.9
    - jaxb-impl (test) 3.0.2 -> 4.0.9 (aligned with jaxb-runtime)
  - Updated build plugin: central-publishing-maven-plugin 0.9.0 -> 0.11.0
  - Fixed Maven coordinates in README usage example (org.aarboard.nextcloud:nextcloud-api) (issue #74)
  - Removed obsolete oss.sonatype.org snapshot badge
  - Added Dependabot configuration for Maven and GitHub Actions

## Version 14.1.4
- 2025-10-28
  - Switch to autopublish

## Version 14.1.0
- 2025-10-23
  - Add HTTP proxy support (via system variables `https.proxyHost` and `https.proxyPort`)
    Thanks to @xylo
  - Add User properties: lastLogin, backend, language, locale, and subAdminGroups
    Thanks to @xylo

## Version 14.0.0
- 2025-10-21
  - Bump required java version from 8 to 11+ (Thanks to @kindlich)
  - Make connection autoclosable (thanks to @raboof)
  - Bump pom.xml dependencies where possible

## Version 13.0.2
- 2024-03-16
  - Typo fixes
  - Javadoc url's
  - Modifiers should be declared in the correct order
  - Try-with resources
  - Use constants where possible
  - Removed redundant Exception throwing
  - Simplified assertions in tests

## Version 13.0.1
- 2023-09-29
  - Release 13.0.1
  - Cleanup closed http client, thanks to lucnygr

## Version 13
- 2023-09-29
  - Release 13.0.0
  - Switch to jakarta xml stuff for simpler Java 11+ compatibility
  - The api remains identical, except the places where javax.xml stuff was exposed
	They now use the jakarta.xml names
  - Bump all dependencies to latest versions, also build environment
  - Added Cyclone DX SBOM

## Version 12
- 2023-09-29
  - Release 12.0.5, added bearer authentication for non-webdav calls (Thanks to Arnout Engelen)
- 2022-11-29
  - Release 12.0.4, upgraded indirect commons-codec dependency
  - Release 12.0.3, upgraded jackson dependencies
- 2022-04-01
  - Release 12.0.2, upgraded jaxb-runtime to 3.0.2 too because of indirect dependencies
- 2022-04-01
  - Release 12.0.1, upgraded various dependencies
  - jackson-databind upgraded to 2.13.2.2 to fix CVE-2020-36518 (Thanks to MrRoubous)
- 2021-11-02
  - Release 12.0.0, thanks for all contributions (See below)
- 2021-10-28
  - Bump version to 12.0.0-SNAPSHOT due to api changes
  - We did remove all xml based provision api, since they don't work
    in many cases, we use JSON now
  - The getQuota() method now returns an Optional<Long>, to handle the case
    when no quota is set at all (Allowing unlimited storage)
	Thanks @kriszman for the patches

## Version 11
- 2021-10-13
  - Prepare 11.7.0-SNAPSHOT
  - Fix downloadFolder() when having special chars in folder name(s)
    Thanks to flelayo (Issue #71)
  - Adding bearer authentication (Constructors got lost in merge)
  - Added support for user quota field, pull request #70, thanks to kriszman
  - Fix downloaded file name if URI encoded values are returned, use request file name
    Thanks to flelayo for the fix to issue #69
- 2021-10-04
  - Release 11.6.0
  - Merged the webdav path resolver from thestomprock with some fixups
- 2021-10-03
  - Added bearer authentication, thanks torshid
  - Base path customization, thanks torshid
- 2021-09-09
  - Release 11.5.1
  - Integrated fix for invalid userlist, pull request #62, Thanks kriszman
- 2021-05-20
  - Added rename/move operation for folders and files
  - Version bump to 11.5.0 because of added api methods
- 2021-04-27
  - Updated various used libraries
- 2020-11-21
  - Added methods to access nextcloud instance installed in subfolders (Thanks to helmut8080)
  - Version bump to 11.4.0 because of added api methods
- 2020-07-15
  - Enanced API to retrieve file and folder meta data (properties)
  - Added Version class so you can get the library version and buils infos at runtime
- 2020-07-14
  - Added API to upload File objects
  - Deprecated the API to upload InputStream, due to some potential server problems
  - Added API to retrieve file meta data
- 2020-07-11
  - Added jakarta xml bind, since xml bind is no longer existing in java 11
- 2020-05-11
  - Added methods to access the application config api (Thanks to col-panic)
  - Version bump to 11.3.0 because of added api methods
- 2020-05-05
  - Added option to return full path to files in folder listings (Thanks to thepivo)
  - Added option to use the continue header in file uploads/puts (Thanks to TobiWineKenobi)
  - Version bump to 11.2.0 because of added api methods
- 2020-02-24
  - Added option to only return files in folder listings (Thanks to SimonIT)
  - Upgraded slf4j to 1.7.30, httpclient to 4.5.11, httpcore to 4.4.13
  - Integrated pull request from col-panic for clean shutdown and directory install support
  - Release 11.1.0
- 2019-09-07
  - Switch to slf4j as logging framework
  - fix to also use port in sardine connector
  - Upgraded to sardine 5.9
  - Added explicit dependencies to http core and http client libraries
- 2018-12-03
  - downloadFile now can return an InputStream
  - Bump version to 11.1.0-SNAPSHOT to match semantic versioning
  - Prepare for next dev cycle 11.0.4-SNAPSHOT
- 2018-12-03
  - Release 11.0.3
  - Upgrade httpasyncclient to 4.1.4
  - Correctly encode URL's in Files and Folder connector
  - Use port specs in sharing connector
  - Added E-Mail share type
- 2018-08-03
  - Release 11.0.2
  - Reworked file/folder handling
- 2018-07-25
  - Release 11.0.1
  - Available via Maven central
- 2018-07-25
  - Added to maven central to simplify usage
- 2018-06-14
  - Method added to download files
- 2017-08-19
  - Added feature to recursive folder handlings
- 2017-06-08
  - XML parsing via JAXB, REST user provisioning and share api added
- 2017-05-29
  - Async method calls
- 2017-05-22
  - Improved Exception generation/handling, user create/delete implemented
- 2017-05-09
  - Implemented fileupload
- 2017-03-30
  - Initial release

(c) André Schild, Aarboard AG www.aarboard.ch

