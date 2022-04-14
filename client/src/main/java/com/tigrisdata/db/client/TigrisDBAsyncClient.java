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
package com.tigrisdata.db.client;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** TigrisDB async client */
public interface TigrisDBAsyncClient extends Closeable {

  /**
   * Retrieves the database instance
   *
   * @param databaseName databaseName
   * @return an instance of {@link TigrisAsyncDatabase}
   */
  TigrisAsyncDatabase getDatabase(String databaseName);

  /**
   * Lists the available databases for the current Api.
   *
   * @param listDatabaseOptions options
   * @return a future to the list of @{@link TigrisAsyncDatabase}
   */
  CompletableFuture<List<TigrisAsyncDatabase>> listDatabases(DatabaseOptions listDatabaseOptions);

  /**
   * Creates the database
   *
   * @param databaseName name of the database
   * @param databaseOptions options
   * @return a future to the {@link TigrisDBResponse}
   */
  CompletableFuture<TigrisDBResponse> createDatabase(
      String databaseName, DatabaseOptions databaseOptions);

  /**
   * Drops the database
   *
   * @param databaseName name of the database
   * @param databaseOptions options
   * @return a future to the {@link TigrisDBResponse}
   */
  CompletableFuture<TigrisDBResponse> dropDatabase(
      String databaseName, DatabaseOptions databaseOptions);
}
