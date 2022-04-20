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

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.type.TigrisCollectionType;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/** TigrisDB Database */
public interface TigrisDatabase {

  /**
   * Return list of collection info
   *
   * @return list of {@link CollectionInfo}
   * @throws TigrisDBException in case of an error.
   */
  List<CollectionInfo> listCollections() throws TigrisDBException;

  /**
   * Creates or updates collections
   *
   * @param collectionModelTypes an array of collection model classes
   * @return response
   * @throws TigrisDBException in case of an error.
   */
  CreateOrUpdateCollectionsResponse createOrUpdateCollections(
      Class<? extends TigrisCollectionType>... collectionModelTypes) throws TigrisDBException;

  /**
   * Creates or updates collections
   *
   * @param packagesToScan an array of Java packages to scan for collection model.
   * @param filter optional filter to filter out classes from scanned set of classes
   * @return response
   * @throws TigrisDBException in case of an error.
   */
  CreateOrUpdateCollectionsResponse createOrUpdateCollections(
      String[] packagesToScan, Optional<Predicate<Class<? extends TigrisCollectionType>>> filter)
      throws TigrisDBException;

  /**
   * Drops the collection.
   *
   * @param collectionName name of the collection
   * @return the instance of {@link DropCollectionResponse} from server
   * @throws TigrisDBException in case of an error.
   */
  DropCollectionResponse dropCollection(String collectionName) throws TigrisDBException;

  /**
   * Return an instance of {@link TigrisCollection}
   *
   * @param collectionTypeClass Class type of the collection
   * @param <C> type of the collection that is of type {@link TigrisCollectionType}
   * @return an instance of {@link TigrisCollection}
   */
  <C extends TigrisCollectionType> TigrisCollection<C> getCollection(Class<C> collectionTypeClass);

  /**
   * Begins the transaction on current database
   *
   * @param transactionOptions options
   * @return transaction aware instance of {@link TransactionSession}
   * @throws TigrisDBException in case of an error
   */
  TransactionSession beginTransaction(TransactionOptions transactionOptions)
      throws TigrisDBException;

  /** @return name of the current database */
  String name();
}
