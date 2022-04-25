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

import com.tigrisdata.db.client.error.TigrisException;

import java.io.Closeable;
import java.util.List;

/** TigrisDB client */
public interface TigrisClient extends Closeable {

  /**
   * Retrieves the database instance
   *
   * @param databaseName databaseName
   * @return an instance of {@link TigrisDatabase}
   */
  TigrisDatabase getDatabase(String databaseName);

  /**
   * Lists the available databases for the current Api.
   *
   * @param listDatabaseOptions options
   * @return a list of @{@link TigrisDatabase}
   * @throws TigrisException authentication failure or any other error
   */
  List<TigrisDatabase> listDatabases(DatabaseOptions listDatabaseOptions) throws TigrisException;

  /**
   * Creates the database if the database is not already present
   *
   * @param databaseName name of the database
   * @return an instance of {@link TigrisDatabase} from server
   * @throws TigrisException in case of auth error or any other failure.
   */
  TigrisDatabase createDatabaseIfNotExists(String databaseName) throws TigrisException;

  /**
   * Drops the database
   *
   * @param databaseName name of the database
   * @return an instance of {@link DropDatabaseResponse} from server
   * @throws TigrisException in case of auth error or any other failure.
   */
  DropDatabaseResponse dropDatabase(String databaseName) throws TigrisException;
}
