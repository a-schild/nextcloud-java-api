# nextcloud-java-api
Java api library to access nextcloud features from java applications

# What is the nextcloud api library?
> Use nextcloud features from inside your java application

# Currently implemented features
- Management of groups
- Folder management (Without access control)
- List shares and create new file shares (No way to delete/update shares atm.)
- Tested agains nextCloud 14.0.4 server version, but should also work with older nextCloud and ownCloud systems

# Usage
- Add this dependency to your pom.xml file
```
<groupId>org.aarboard.nextcloud</groupId>
    <artifactId>nextcloud-api</artifactId>
<version>11.0.3</version>
```

- Create a NextcloudConnector instance and provide your server settings and authentification
- Now you can use the methods exposed to access your nextcloud instance

# Changelog
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
