/*
 * Copyright 2022 Tigris Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tigrisdata.spring.autoconfigure;

import com.tigrisdata.db.client.TigrisAsyncClient;
import com.tigrisdata.db.client.TigrisAsyncDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

public class TigrisAsyncInitializer implements CommandLineRunner {

  private final String dbName;
  private final String[] collectionClasses;
  private final TigrisAsyncClient tigrisAsyncClient;
  private static final Logger log = LoggerFactory.getLogger(TigrisAsyncInitializer.class);

  public TigrisAsyncInitializer(
      @Value("${tigris.db.name}") String dbName,
      @Value("${tigris.db.collectionClasses}") String collectionClasses,
      TigrisAsyncClient tigrisAsyncClient) {
    this.dbName = dbName;
    this.tigrisAsyncClient = tigrisAsyncClient;
    this.collectionClasses = collectionClasses.split(",");
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("Creating tigris database");
    TigrisAsyncDatabase db = tigrisAsyncClient.createDatabaseIfNotExists(this.dbName).get();
    log.info("Created tigris database");

    log.info("Resolving collection classes");
    Class[] classes = new Class[collectionClasses.length];
    for (int i = 0; i < collectionClasses.length; i++) {
      log.info("Resolving collection class = {}", collectionClasses[i]);
      try {
        classes[i] = Class.forName(collectionClasses[i]);
        log.info("Resolved collection class = {}", collectionClasses[i]);
      } catch (ClassNotFoundException classNotFoundException) {
        log.error("Failed to resolve collection class = {}", collectionClasses[i]);
        throw new RuntimeException("Failed to resolve collection class " + collectionClasses[i]);
      }
    }

    log.info("Creating tigris collections for db={}", dbName);
    db.createOrUpdateCollections(classes).get();
    log.info("Created tigris collections for db={}", dbName);
    log.info("Tigris initialization completed successfully.");
  }
}
