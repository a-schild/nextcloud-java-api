# nextcloud-java-api
Java api library to access nextcloud features from java applications

![Maven Central](https://img.shields.io/maven-central/v/org.aarboard.nextcloud/nextcloud-api)

## What is the nextcloud api library?
> Use nextcloud features from inside your java application

## Currently implemented features
- Management of groups
- Folder management (Without access control)
- List shares and create new file shares (No way to delete/update shares atm.)
- Calendars (CalDAV) and contacts (CardDAV), see below
- Tested against nextCloud 31.0.0 server version, but should also work with older nextCloud and ownCloud systems

## Usage
- Add this dependency to your pom.xml file
```
	<dependency>
	    <groupId>org.aarboard.nextcloud</groupId>
	    <artifactId>nextcloud-api</artifactId>
	    <version>14.2.1</version>
	</dependency>
```

### Trying a pre-release snapshot

Unreleased work is published as a `-SNAPSHOT` to the Central Portal snapshot
repository so it can be tried before a release. Snapshots are overwritten by
later builds and Sonatype removes them after about 90 days, so don't depend on
one from a production build.

```xml
<repositories>
    <repository>
        <id>central-snapshots</id>
        <url>https://central.sonatype.com/repository/maven-snapshots/</url>
        <snapshots><enabled>true</enabled></snapshots>
    </repository>
</repositories>

<dependency>
    <groupId>org.aarboard.nextcloud</groupId>
    <artifactId>nextcloud-api</artifactId>
    <version>14.3.0-SNAPSHOT</version>
</dependency>
```

- The 14.x versions require Java 11+,as the jakarta.xml binding requires Java 11+
- The 13.x versions are now using the jakarta.xml binding stuff, to prevent problems with Java 11+
  No API changes have been made in v13, but at some places the XML stuff is exposed
  Which made it necessary to bump the major version number
- Create a NextcloudConnector instance and provide your server settings and authentification
- Now you can use the methods exposed to access your nextcloud instance

## Calendars and contacts

Calendar entries and contacts are exchanged as raw iCalendar/vCard documents.
The library does not parse them, so it needs no iCalendar or vCard dependency
and you stay free to use the parser of your choice (for example
[ical4j](https://github.com/ical4j/ical4j) or
[ez-vcard](https://github.com/mangstadt/ez-vcard)).

```java
try (NextcloudConnector nc = new NextcloudConnector("cloud.example.org", true, 443, "user", "password")) {
    for (Calendar calendar : nc.listCalendars()) {
        System.out.println(calendar.getName() + " -> " + calendar.getDisplayName());
    }

    // All entries of a calendar, or only the events in a time range
    List<CalendarEntry> all = nc.getCalendarEntries("personal");
    List<CalendarEntry> thisWeek = nc.getCalendarEntriesInRange("personal",
            Instant.now(), Instant.now().plus(7, ChronoUnit.DAYS));

    String ics = thisWeek.get(0).getData();   // the iCalendar document

    // Pass true to have the server expand recurring events into one VEVENT per
    // occurrence in the range, instead of one event carrying its RRULE. The
    // expanded result is a computed view of that range, so don't write it back.
    List<CalendarEntry> occurrences = nc.getCalendarEntriesInRange("personal",
            Instant.now(), Instant.now().plus(7, ChronoUnit.DAYS), true);

    // Store an entry, and update it only while it still carries this etag
    String etag = nc.putCalendarEntry("personal", "my-event.ics", ics);
    nc.putCalendarEntry("personal", "my-event.ics", changedIcs, etag);

    nc.deleteCalendarEntry("personal", "my-event.ics");
}
```

Contacts work the same way via `listAddressBooks()`, `getContacts(book)`,
`getContact(book, name)`, `putContact(...)` and `deleteContact(...)`.
Calendars and address books can also be created and deleted with
`createCalendar(name, displayName, colour)` / `deleteCalendar(name)` and
`createAddressBook(name, displayName, description)` / `deleteAddressBook(name)`.

## When you wish to contribute to the project
[Infos for contributors](./README.developers.md)

## Changelog
[Changelog](Changelog.md)


(c) André Schild, Aarboard AG www.aarboard.ch
