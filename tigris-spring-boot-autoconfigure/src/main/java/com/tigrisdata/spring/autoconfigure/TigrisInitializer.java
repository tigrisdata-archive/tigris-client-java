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

import com.tigrisdata.db.client.TigrisClient;
import com.tigrisdata.db.client.TigrisDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

public class TigrisInitializer implements CommandLineRunner {

  private final String dbName;
  private final String[] collectionClasses;
  private final TigrisClient tigrisClient;
  private static final Logger log = LoggerFactory.getLogger(TigrisInitializer.class);

  public TigrisInitializer(
      @Value("${tigris.db.name}") String dbName,
      @Value("${tigris.db.collectionClasses}") String collectionClasses,
      TigrisClient tigrisClient) {
    this.dbName = dbName;
    this.tigrisClient = tigrisClient;
    this.collectionClasses = collectionClasses.split(",");
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("Creating tigris database");
    TigrisDatabase db = tigrisClient.createDatabaseIfNotExists(this.dbName);
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
    db.createOrUpdateCollections(classes);
    log.info("Created tigris collections for db={}", dbName);
    log.info("Tigris initialization completed successfully.");
  }
}
