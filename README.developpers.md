# nextcloud-java-api
Java api library to access nextcloud features from java applications

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
