# Changelog for nextcloud api
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

