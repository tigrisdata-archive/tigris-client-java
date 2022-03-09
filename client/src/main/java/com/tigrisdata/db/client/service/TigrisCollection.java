package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.*;

import java.util.List;

public interface TigrisCollection<T extends TigrisCollectionType> {

  /**
   * Reads a matching document
   *
   * @param filter filter to narrow down a single document
   * @param readOptions read option
   * @return an document of type {@code T}
   * @throws TigrisDBException in case of an error
   */
  T read(TigrisFilter filter, ReadOptions readOptions) throws TigrisDBException;

  /**
   * @param documents list of documents to insert
   * @param writeOption write option
   * @return an instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of an error
   */
  TigrisDBResponse insert(List<T> documents, WriteOption writeOption) throws TigrisDBException;

  /**
   * @param documents list of documents to replace
   * @param writeOption write option
   * @return an instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of an error
   */
  TigrisDBResponse replace(List<T> documents, WriteOption writeOption) throws TigrisDBException;

  /**
   * Deletes the matching documents in the collection.
   *
   * @param filter filter to narrow down the documents to delete
   * @param writeOption write option
   * @return an instance of {@link TigrisDBResponse} from server
   * @throws TigrisDBException in case of an error
   */
  TigrisDBResponse delete(TigrisFilter filter, WriteOption writeOption) throws TigrisDBException;

  /** @return Name of the collection */
  String name();
}
