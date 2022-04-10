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
package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.CollectionOptions;
import com.tigrisdata.db.client.model.CreateOrUpdateCollectionResponse;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;
import com.tigrisdata.db.client.model.TigrisDBSchema;

public interface TransactionSession {
  /**
   * Return an instance of {@link TransactionTigrisCollection}
   *
   * @param collectionTypeClass Class type of the collection
   * @param <C> type of the collection that is of type {@link TigrisCollectionType}
   * @return an instance of {@link TransactionTigrisCollection}
   * @throws TigrisDBException if collection doesn't exist in the @{@link TigrisDatabase} or any
   *     other error
   */
  <C extends TigrisCollectionType> TransactionTigrisCollection<C> getCollection(
      Class<C> collectionTypeClass) throws TigrisDBException;

  /**
   * Creates a collection under current database in transactional session.
   *
   * @param schema schema of the collection
   * @param collectionOptions collection option
   * @return the instance of {@link CreateOrUpdateCollectionResponse} from server
   * @throws TigrisDBException in case of an error.
   */
  CreateOrUpdateCollectionResponse createOrUpdateCollection(
      TigrisDBSchema schema, CollectionOptions collectionOptions) throws TigrisDBException;

  /**
   * Commits the current ongoing transaction
   *
   * @return an instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of an error
   */
  TigrisDBResponse commit() throws TigrisDBException;

  /**
   * Rolls back the current ongoing transaction
   *
   * @return an instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of an error
   */
  TigrisDBResponse rollback() throws TigrisDBException;
}
