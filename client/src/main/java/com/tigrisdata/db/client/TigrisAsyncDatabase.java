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
import com.tigrisdata.db.type.TigrisCollectionType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/** Tigris async database */
public interface TigrisAsyncDatabase {

  /**
   * Return list of {@link CollectionInfo}
   *
   * @return a future to the {@link List} of {@link CollectionInfo} representing collection
   */
  CompletableFuture<List<CollectionInfo>> listCollections();

  /**
   * Creates or updates collections
   *
   * @param collectionModelTypes an array of collection model classes
   * @return future to the {@link CreateOrUpdateCollectionsResponse}
   */
  CompletableFuture<CreateOrUpdateCollectionsResponse> createOrUpdateCollections(
      Class<? extends TigrisCollectionType>... collectionModelTypes);

  /**
   * Creates or updates collections by scanning classpath packages and applying user's filter *
   * (optionally) The alternate method {@link TigrisDatabase#createOrUpdateCollections(Class[])} is
   * recommended where user specifies fixed list of classes to avoid classpath scan at runtime.
   *
   * @param packagesToScan an array of Java packages to scan for collection model.
   * @param filter optional filter to filter out classes from scanned set of classes
   * @return future to the {@link CreateOrUpdateCollectionsResponse}
   */
  CompletableFuture<CreateOrUpdateCollectionsResponse> createOrUpdateCollections(
      String[] packagesToScan, Optional<Predicate<Class<? extends TigrisCollectionType>>> filter);

  /**
   * Drops the collection.
   *
   * @param collectionTypeClass Class type of the collection
   * @param <T> type of the collection that is of type {@link TigrisCollectionType}
   * @return the future to the {@link DropCollectionResponse}
   */
  <T extends TigrisCollectionType> CompletableFuture<DropCollectionResponse> dropCollection(
      Class<T> collectionTypeClass);

  /**
   * Return an instance of {@link TigrisCollection}
   *
   * @param collectionTypeClass Class type of the collection
   * @param <C> type of the collection that is of type {@link TigrisCollectionType}
   * @return an instance of {@link TigrisAsyncCollection}
   */
  <C extends TigrisCollectionType> TigrisAsyncCollection<C> getCollection(
      Class<C> collectionTypeClass);

  /** @param streamer streamer callback */
  void stream(TigrisAsyncStreamer streamer);

  /**
   * Begins the transaction on current database
   *
   * @param transactionOptions options
   * @return the future to the {@link TransactionSession}
   */
  CompletableFuture<TransactionSession> beginTransaction(TransactionOptions transactionOptions);

  /**
   * @return the future to the {@link DatabaseDescription} containing description of database.
   * @throws TigrisException in case of an error.
   */
  CompletableFuture<DatabaseDescription> describe() throws TigrisException;

  /** @return name of the current database */
  String name();
}
