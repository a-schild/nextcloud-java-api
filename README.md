# nextcloud-java-api
Java api library to access nextcloud features from java applications

![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/org.aarboard.nextcloud/nextcloud-api?label=release&nexusVersion=2&server=https%3A%2F%2Foss.sonatype.org%2F)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/org.aarboard.nextcloud/nextcloud-api?label=snapshot&server=https%3A%2F%2Foss.sonatype.org%2F)

## What is the nextcloud api library?
> Use nextcloud features from inside your java application

## Currently implemented features
- Management of groups
- Folder management (Without access control)
- List shares and create new file shares (No way to delete/update shares atm.)
- Tested against nextCloud 27.0.1 server version, but should also work with older nextCloud and ownCloud systems

## Usage
- Add this dependency to your pom.xml file
```
	<dependency>
	    <groupId>com.github.a-schild</groupId>
	    <artifactId>nextcloud-java-api</artifactId>
	    <version>13.0.0</version>
	</dependency>
```

- The 13.x versions are now using the jakarta.xml binding stuff, to prevent problems with Java 11+
  No API changes have been made in v13, but at some places the XML stuff is exposed
  Which made it necessary to bump the major version number
- Create a NextcloudConnector instance and provide your server settings and authentification
- Now you can use the methods exposed to access your nextcloud instance

## When you wish to contribute to the project
[Infos for contributors](./README.developers.md)

## Changelog
[Changelog](Changelog.md)


(c) André Schild, Aarboard AG www.aarboard.ch
