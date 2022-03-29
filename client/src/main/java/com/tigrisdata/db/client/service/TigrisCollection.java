package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.Field;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.ReadRequestOptions;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisFilter;
import com.tigrisdata.db.client.model.UpdateRequestOptions;
import com.tigrisdata.db.client.model.UpdateResponse;

import java.util.Iterator;
import java.util.List;

public interface TigrisCollection<T extends TigrisCollectionType> {

  /**
   * @param filter filter to narrow down read
   * @param fields optionally specify fields you want to be returned from server
   * @param readRequestOptions read options
   * @return stream of documents
   * @throws TigrisDBException
   */
  Iterator<T> read(
      TigrisFilter filter, List<Field<?>> fields, ReadRequestOptions readRequestOptions)
      throws TigrisDBException;

  /**
   * Reads matching documents
   *
   * @param filter filter to narrow down read
   * @param fields optionally specify fields you want to be returned from server
   * @return stream of documents
   * @throws TigrisDBException in case of an error
   */
  Iterator<T> read(TigrisFilter filter, List<Field<?>> fields) throws TigrisDBException;

  /**
   * Reads a single document. This method is generally recommended for point lookup, if used for
   * non-point lookup any arbitrary matching document will be returned.
   *
   * @param filter
   * @return
   * @throws TigrisDBException
   */
  T readOne(TigrisFilter filter) throws TigrisDBException;

  /**
   * @param documents list of documents to insert
   * @param insertRequestOptions insert option
   * @return an instance of {@link InsertResponse} from server
   * @throws TigrisDBException in case of an error
   */
  InsertResponse insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisDBException;

  /**
   * @param documents list of documents to insert
   * @return an instance of {@link InsertResponse} from server
   * @throws TigrisDBException in case of an error
   */
  InsertResponse insert(List<T> documents) throws TigrisDBException;

  /**
   * inserts a single document to the collection
   *
   * @param document
   * @return
   * @throws TigrisDBException
   */
  InsertResponse insert(T document) throws TigrisDBException;

  /**
   * @param filter
   * @param fields
   * @param updateRequestOptions
   * @return
   * @throws TigrisDBException
   */
  UpdateResponse update(
      TigrisFilter filter, List<Field<?>> fields, UpdateRequestOptions updateRequestOptions)
      throws TigrisDBException;

  /**
   * @param filter
   * @param fields
   * @return
   * @throws TigrisDBException
   */
  UpdateResponse update(TigrisFilter filter, List<Field<?>> fields) throws TigrisDBException;

  /**
   * Deletes the matching documents in the collection.
   *
   * @param filter filter to narrow down the documents to delete
   * @param deleteRequestOptions delete option
   * @return an instance of {@link DeleteResponse} from server
   * @throws TigrisDBException in case of an error
   */
  DeleteResponse delete(TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisDBException;

  /**
   * Deletes the matching documents in the collection.
   *
   * @param filter filter to narrow down the documents to delete
   * @return an instance of {@link DeleteResponse} from server
   * @throws TigrisDBException in case of an error
   */
  DeleteResponse delete(TigrisFilter filter) throws TigrisDBException;

  /** @return Name of the collection */
  String name();
}
