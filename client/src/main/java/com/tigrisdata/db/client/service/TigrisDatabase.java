package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBResponse;

import java.util.List;

public interface TigrisDatabase {

  /**
   * Return an instance of {@link TigrisCollection}
   *
   * @param collectionTypeClass Class type of the collection
   * @param <C> type of the collection that is of type {@link TigrisCollectionType}
   * @return
   * @throws TigrisDBException if collection doesn't exist in the @{@link TigrisDatabase} or any
   *     other error
   */
  <C extends TigrisCollectionType> TigrisCollection<C> getCollection(Class<C> collectionTypeClass)
      throws TigrisDBException;

  /**
   * Return list of collection names
   *
   * @return list of collection names (TODO: order specification)
   * @throws TigrisDBException in case of an error.
   */
  List<String> listCollections() throws TigrisDBException;

  /**
   * Truncates the collection.
   *
   * @param collectionName name of the collection
   * @return the instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of an error.
   */
  TigrisDBResponse truncateCollection(String collectionName) throws TigrisDBException;

  /**
   * Drops the collection.
   *
   * @param collectionName name of the collection
   * @return the instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of an error.
   */
  TigrisDBResponse dropCollection(String collectionName) throws TigrisDBException;

  /** @return name of the current database */
  String name();
}
