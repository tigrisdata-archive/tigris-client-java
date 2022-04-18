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

import com.tigrisdata.db.type.TigrisCollectionType;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** TigrisDB async database */
public interface TigrisAsyncDatabase {

  /**
   * Return list of {@link CollectionInfo}
   *
   * @return a future to the {@link List} of {@link CollectionInfo} representing collection
   */
  CompletableFuture<List<CollectionInfo>> listCollections();

  /**
   * Creates or updates collection under current database.
   *
   * @param schemaDirectory directory with schema
   * @return future to the {@link ApplySchemasResponse}
   */
  CompletableFuture<ApplySchemasResponse> applySchemas(File schemaDirectory);

  /**
   * Creates or updates collection under current database.
   *
   * @param collectionsSchemas list of {@link URL}
   * @return future to the {@link ApplySchemasResponse}
   */
  CompletableFuture<ApplySchemasResponse> applySchemas(List<URL> collectionsSchemas);

  /**
   * Drops the collection.
   *
   * @param collectionName name of the collection
   * @return the future to the {@link DropCollectionResponse}
   */
  CompletableFuture<DropCollectionResponse> dropCollection(String collectionName);

  /**
   * Return an instance of {@link TigrisCollection}
   *
   * @param collectionTypeClass Class type of the collection
   * @param <C> type of the collection that is of type {@link TigrisCollectionType}
   * @return an instance of {@link TigrisAsyncCollection}
   */
  <C extends TigrisCollectionType> TigrisAsyncCollection<C> getCollection(
      Class<C> collectionTypeClass);

  /**
   * Begins the transaction on current database
   *
   * @param transactionOptions options
   * @return the future to the {@link TransactionSession}
   */
  CompletableFuture<TransactionSession> beginTransaction(TransactionOptions transactionOptions);

  /** @return name of the current database */
  String name();
}
