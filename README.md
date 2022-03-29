# TigrisDB Java Client
Java driver for TigrisDB

# Usage


    // prepare config
    TigrisDBConfiguration tigrisDBConfiguration =
        TigrisDBConfiguration.newBuilder("tigris-data-host:port").build();
    
    // initialize the client
    TigrisDBClient tigrisDBClient = StandardTigrisDBClient.getInstance(new TigrisAuthorizationToken("your-api-token"),
            tigrisDBConfiguration);
    
    // get access to your database
    TigrisDatabase myDB = tigrisDBClient.getDatabase("your-db-name");
    
    // get access to your collection
    TigrisCollection<Person> peopleCollection = myDB.getCollection(Person.class);

    // insert
    peopleCollection.insert(new Person(1, "Bob"));

    // read
    Person bob = peopleCollection.readOne(Filters.eq("id", 1));
    
    // update
    peopleCollection.update(
        Filters.eq("id", 1),
        Collections.singletonList(
            Fields.stringField("name", "Mr. Bob")
        )
    );
    
    // delete
    peopleCollection.delete(Filters.eq("id", 1));