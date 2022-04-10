# TigrisDB Java Client

Java driver for TigrisDB

__CI__  
[![java-ci](https://github.com/tigrisdata/tigrisdb-client-java/actions/workflows/java-ci.yml/badge.svg?branch=main)](https://github.com/tigrisdata/tigrisdb-client-java/actions/workflows/java-ci.yml)

__Client module JaCoCo__  
[![coverage](.github/badges/jacoco.svg)](https://github.com/tigrisdata/tigrisdb-client-java/blob/main/.github/workflows/java-ci.yml) [![branches coverage](.github/badges/branches.svg)](https://github.com/tigrisdata/tigrisdb-client-java/blob/main/.github/workflows/java-ci.yml)

__License__  
[![GitHub](https://img.shields.io/github/license/tigrisdata/tigrisdb-client-java)](https://github.com/tigrisdata/tigrisdb-client-java/blob/main/LICENSE) 
# Usage
```java
// prepare config
TigrisDBConfiguration tigrisDBConfiguration=
        TigrisDBConfiguration.newBuilder("tigris-data-host:port").build();

// initialize the client
TigrisDBClient tigrisDBClient=StandardTigrisDBClient.getInstance(
        new TigrisAuthorizationToken("your-api-token"),
        tigrisDBConfiguration
        );

// get access to your database
TigrisDatabase myDB=tigrisDBClient.getDatabase("your-db-name");

// get access to your collection
TigrisCollection<Person> peopleCollection=myDB.getCollection(Person.class);

// insert
peopleCollection.insert(new Person(1,"Alice"));

// read
Person alice=peopleCollection.readOne(Filters.eq("id",1));

// update
peopleCollection.update(
    Filters.eq("id",1),
    UpdateFields.newBuilder()
        .set(
            UpdateFields.SetFields.newBuilder()
            .set("name","Dr. Alice")
            .build()
            ).build()
);

// delete
peopleCollection.delete(Filters.eq("id",1));
```

----

# TigrisDB Maven Plugin

TigrisDB maven plugin reads schema files as input and generates the Java models
as output. It also ensures the schema compatibility for `git` managed projects.

# Configuration

```xml

<build>
    <plugins>
        ...
        <plugin>
            <groupId>com.tigrisdata.tools.code-generator</groupId>
            <artifactId>maven-plugin</artifactId>
            <!-- we are still pre-release -->
            <version>1.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>generate-sources</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <!-- directory location of schema files -->
                <!-- default schemaDir=${project.basedir}/src/main/resources/tigrisdb-schema-->
                <schemaDir>src/main/resources/tigrisdb-schema</schemaDir>
                <!-- Java model's package name -->
                <!-- required field -->
                <packageName>com.tigrisdata.store.generated</packageName>
                <!-- Output directory where Java classes will be generated -->
                <!-- default outputDirectory=${project.basedir}/target/generated-sources -->
                <outputDirectory>target/generated-sources</outputDirectory>
                <!-- Disables schema compatibility validation -->
                <!-- default disableValidation=false -->
                <disableValidation>false</disableValidation>
            </configuration>
        </plugin>
        ...
    </plugins>
</build>
```
