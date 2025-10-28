# nextcloud-java-api
Java api library to access nextcloud features from java applications

![Sonatype Nexus (Releases)](https://img.shields.io/maven-central/v/org.aarboard.nextcloud/nextcloud-api)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/org.aarboard.nextcloud/nextcloud-api?server=https%3A%2F%2Foss.sonatype.org)

## What is the nextcloud api library?
> Use nextcloud features from inside your java application

## Currently implemented features
- Management of groups
- Folder management (Without access control)
- List shares and create new file shares (No way to delete/update shares atm.)
- Tested against nextCloud 31.0.0 server version, but should also work with older nextCloud and ownCloud systems

## Usage
- Add this dependency to your pom.xml file
```
	<dependency>
	    <groupId>com.github.a-schild</groupId>
	    <artifactId>nextcloud-java-api</artifactId>
	    <version>14.1.4</version>
	</dependency>
```

- The 14.x versions require Java 11+,as the jakarta.xml binding requires Java 11+
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
