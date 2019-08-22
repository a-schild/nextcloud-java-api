# nextcloud-java-api
Java api library to access nextcloud features from java applications

# Versioning
Starting with 11.1.0 we use semanic versioning according to
https://semver.org/spec/v2.0.0.html

In short:
Given a version number MAJOR.MINOR.PATCH, increment the:

    MAJOR version when you make incompatible API changes,
    MINOR version when you add functionality in a backwards compatible manner, and
    PATCH version when you make backwards compatible bug fixes.

Additional labels for pre-release and build metadata are available as extensions to the MAJOR.MINOR.PATCH format.


# How to run unit tests
For the unit test to be executed, you need to specify valid
next cloud server name and login admin credentials

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
