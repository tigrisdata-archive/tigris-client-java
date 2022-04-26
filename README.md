# Tigris Java Client

Java driver for Tigris

[![java-ci](https://github.com/tigrisdata/tigris-client-java/actions/workflows/java-ci.yml/badge.svg?branch=main)](https://github.com/tigrisdata/tigrisdb-client-java/actions/workflows/java-ci.yml)
[![codecov](https://codecov.io/gh/tigrisdata/tigris-client-java/branch/main/graph/badge.svg)](https://codecov.io/gh/tigrisdata/tigrisdb-client-java)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/tigrisdata/tigris-client-java.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/tigrisdata/tigrisdb-client-java/context:java)
![Snyk Vulnerabilities for GitHub Repo](https://img.shields.io/snyk/vulnerabilities/github/tigrisdata/tigris-client-java)
[![javadoc](https://javadoc.io/badge2/com.tigrisdata/tigrisdb-client/javadoc.svg)](https://javadoc.io/doc/com.tigrisdata/tigrisdb-client)
[![Maven Central](https://img.shields.io/maven-central/v/com.tigrisdata/tigrisdb-client-java)](https://mvnrepository.com/artifact/com.tigrisdata/tigrisdb-client)
[![slack](https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white)](https://join.slack.com/t/tigrisdatacommunity/shared_invite/zt-16fn5ogio-OjxJlgttJIV0ZDywcBItJQ)
[![GitHub](https://img.shields.io/github/license/tigrisdata/tigris-client-java)](https://github.com/tigrisdata/tigrisdb-client-java/blob/main/LICENSE)

# Maven

```xml
<dependency>
    <groupId>com.tigrisdata</groupId>
    <artifactId>tigrisdb-client</artifactId>
    <version>1.0.0-alpha.7</version>
</dependency>
```

# Usage
```java
// prepare config
TigrisConfiguration tigrisConfiguration = 
        TigrisConfiguration.newBuilder("tigris-data-host:port").build();

// initialize the client
TigrisClient tigrisClient = StandardTigrisClient.getInstance
        (tigrisConfiguration);

// get access to your database
TigrisDatabase myDB = tigrisClient.getDatabase("your-db-name");

// get access to your collection
TigrisCollection<Person> peopleCollection = myDB.getCollection(Person.class);

// insert
peopleCollection.insert(new Person(1, "Alice"));

// read
Person alice = peopleCollection.readOne(Filters.eq("id", 1)).get();

// update
peopleCollection.update(
    Filters.eq("id", 1),
    UpdateFields.newBuilder()
        .set("name", "Dr. Alice")
        .build()
);

// delete
peopleCollection.delete(Filters.eq("id", 1));
```
