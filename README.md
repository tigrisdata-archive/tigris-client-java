# Tigris Java Client Library

[![java-ci](https://github.com/tigrisdata/tigris-client-java/actions/workflows/java-ci.yml/badge.svg?branch=main)](https://github.com/tigrisdata/tigris-client-java/actions/workflows/java-ci.yml)
[![codecov](https://codecov.io/gh/tigrisdata/tigris-client-java/branch/main/graph/badge.svg)](https://codecov.io/gh/tigrisdata/tigris-client-java)
[![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/tigrisdata/tigris-client-java.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/tigrisdata/tigris-client-java/context:java)
[![javadoc](https://javadoc.io/badge2/com.tigrisdata/tigris-client/javadoc.svg)](https://javadoc.io/doc/com.tigrisdata/tigris-client)
[![Maven Central](https://img.shields.io/maven-central/v/com.tigrisdata/tigris-client-java)](https://mvnrepository.com/artifact/com.tigrisdata/tigris-client)
[![slack](https://img.shields.io/badge/slack-tigrisdata-34D058.svg?logo=slack)](https://tigrisdata.slack.com)
[![GitHub](https://img.shields.io/github/license/tigrisdata/tigris-client-java)](https://github.com/tigrisdata/tigris-client-java/blob/main/LICENSE)

<p align="center">
Tigris is a globally distributed, multi-cloud object storage service with built-in support for the S3 API. It uses Dynamic Data Placement and Access-Based Rebalancing to deliver low-latency access worldwide — without the need to manage replication or caching.
</p>

<p align="center">
This repository is deprecated and contains a prior version of the Tigris Java Client.
</p>

# Documentation
- [Quickstart](https://docs.tigrisdata.com/quickstart)
- [Java Sync Client](https://docs.tigrisdata.com/java/sync-client)
- [Java Async Client](https://docs.tigrisdata.com/java/async-client)
- [Data Modeling Using Java](https://docs.tigrisdata.com/java/datamodel/overview)

# Maven Configuration

```xml
<dependency>
    <groupId>com.tigrisdata</groupId>
    <artifactId>tigris-client</artifactId>
    <version>${tigris.client.java.version}</version>
</dependency>
```

For latest version and for other dependency management or build tool you can 
refer to dependency snippet from
[here](https://mvnrepository.com/artifact/com.tigrisdata/tigris-client).

# Usage
```java
// configuration
TigrisConfiguration config =
    TigrisConfiguration.newBuilder("localhost:8081")
        .withNetwork(
            TigrisConfiguration.NetworkConfig.newBuilder()
                .usePlainText() // for dev env - plaintext communication
                .build())
        .build();

// construct client
TigrisClient client = StandardTigrisClient.getInstance(config);

// create or get db
TigrisDatabase helloDB = client.createDatabaseIfNotExists("hello_db");

// create or update collection(s)
helloDB.createOrUpdateCollections(User.class);

// get collection
TigrisCollection<User> users = helloDB.getCollection(User.class);

// insert
users.insert(new User(1, "Jania McGrory", 6045.7));

// read
User user1 = users.readOne(Filters.eq("id", 1)).get();

// update
users.update(
    Filters.eq("id", 1), 
    UpdateFields.newBuilder().set("name", "Jania McGrover").build()
);

// delete
// delete - delete users with id 1 or 2
users.delete(
    Filters.or(
        Filters.eq("id", 1), 
        Filters.eq("id", 2)
    )
);

// search - search for users with name "Jania"
users.search(
    SearchRequest.newBuilder()
      .withQuery("Jania")
      .withSearchFields("name")
      .build()
    );
```

# License

This software is licensed under the [Apache 2.0](LICENSE).
