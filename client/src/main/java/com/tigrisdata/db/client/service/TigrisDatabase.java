package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.AlterCollectionResponse;
import com.tigrisdata.db.client.model.CollectionOptions;
import com.tigrisdata.db.client.model.CreateCollectionResponse;
import com.tigrisdata.db.client.model.DropCollectionResponse;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisDBSchema;
import com.tigrisdata.db.client.model.TransactionOptions;
import com.tigrisdata.db.client.model.TruncateCollectionResponse;

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
   * @return the instance of {@link CreateCollectionResponse} from server
   * @throws TigrisDBException in case of an error.
   */
  CreateCollectionResponse createCollection(
      String collectionName, TigrisDBSchema schema, CollectionOptions collectionOptions)
      throws TigrisDBException;

  /**
   * Alters a collection under current database.
   *
   * @param collectionName name of the collection
   * @param schema schema of the collection
   * @param collectionOptions collection option
   * @return the instance of {@link AlterCollectionResponse} from server
   * @throws TigrisDBException in case of an error.
   */
  AlterCollectionResponse alterCollection(
      String collectionName, TigrisDBSchema schema, CollectionOptions collectionOptions)
      throws TigrisDBException;

  /**
   * Truncates the collection.
   *
   * @param collectionName name of the collection
   * @return the instance of {@link TruncateCollectionResponse} from server
   * @throws TigrisDBException in case of an error.
   */
  TruncateCollectionResponse truncateCollection(String collectionName) throws TigrisDBException;

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
