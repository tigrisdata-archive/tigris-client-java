package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;

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
