package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.*;

import java.util.List;

public interface TigrisDatabase {

  /**
   * Return list of collection names
   *
   * @return list of collection names (TODO: order specification)
   * @throws TigrisDBException in case of an error.
   */
  List<String> listCollections() throws TigrisDBException;

  /**
   * Creates a collection under current database.
   *
   * @param collectionName name of the collection
   * @param schema schema of the collection
   * @param collectionOptions collection option
   * @return the instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of an error.
   */
  TigrisDBResponse createCollection(
      String collectionName, TigrisDBSchema schema, CollectionOptions collectionOptions)
      throws TigrisDBException;

  /**
   * Alters a collection under current database.
   *
   * @param collectionName name of the collection
   * @param schema schema of the collection
   * @param collectionOptions collection option
   * @return the instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of an error.
   */
  TigrisDBResponse alterCollection(
      String collectionName, TigrisDBSchema schema, CollectionOptions collectionOptions)
      throws TigrisDBException;

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
  /**
   * Return an instance of {@link TigrisCollection}
   *
   * @param collectionTypeClass Class type of the collection
   * @param <C> type of the collection that is of type {@link TigrisCollectionType}
   * @return an instance of {@link TigrisCollection}
   * @throws TigrisDBException if collection doesn't exist in the @{@link TigrisDatabase} or any
   *     other error
   */
  <C extends TigrisCollectionType> TigrisCollection<C> getCollection(Class<C> collectionTypeClass)
      throws TigrisDBException;

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
