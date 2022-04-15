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

final class Messages {

  private Messages() {}
  // error
  // client
  public static final String LIST_DBS_FAILED = "Failed to list database(s)";
  public static final String CREATE_DB_FAILED = "Failed to create database";
  public static final String DROP_DB_FAILED = "Failed to drop database";
  public static final String DB_ALREADY_EXISTS = "Database already exists";

  // database
  public static final String LIST_COLLECTION_FAILED = "Failed to list collection(s)";
  public static final String CREATE_OR_UPDATE_COLLECTION_FAILED =
      "Failed to create collections in transaction";
  public static final String DROP_COLLECTION_FAILED = "Failed to drop collection";
  public static final String BEGIN_TRANSACTION_FAILED = "Failed to begin transaction";

  // collections
  public static final String INSERT_FAILED = "Failed to insert";
  public static final String INSERT_OR_REPLACE_FAILED = "Failed to insertOrReplace";
  public static final String UPDATE_FAILED = "Failed to update";
  public static final String DELETE_FAILED = "Failed to delete";
  public static final String READ_FAILED = "Failed to read";

  // success
  public static final String COLLECTIONS_APPLIED = "Collections created or changes applied";
}
