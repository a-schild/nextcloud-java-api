# nextcloud-java-api
Java api library to access nextcloud features from java applications

![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/org.aarboard.nextcloud/nextcloud-api?label=release&nexusVersion=2&server=https%3A%2F%2Foss.sonatype.org%2F)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/org.aarboard.nextcloud/nextcloud-api?label=snapshot&server=https%3A%2F%2Foss.sonatype.org%2F)

# What is the nextcloud api library?
> Use nextcloud features from inside your java application

# Currently implemented features
- Management of groups
- Folder management (Without access control)
- List shares and create new file shares (No way to delete/update shares atm.)
- Tested against nextCloud 16.0.4 server version, but should also work with older nextCloud and ownCloud systems

# Usage
- Add this dependency to your pom.xml file
```
<groupId>org.aarboard.nextcloud</groupId>
<artifactId>nextcloud-api</artifactId>
<version>11.2.0</version>
```

- Create a NextcloudConnector instance and provide your server settings and authentification
- Now you can use the methods exposed to access your nextcloud instance

# Changelog
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
