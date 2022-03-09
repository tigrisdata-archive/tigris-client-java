package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.ReadRequestOptions;
import com.tigrisdata.db.client.model.ReplaceRequestOptions;
import com.tigrisdata.db.client.model.ReplaceResponse;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisFilter;

import java.util.Iterator;
import java.util.List;

public interface TigrisCollection<T extends TigrisCollectionType> {

  /**
   * Reads a matching document
   *
   * @param filter filter to narrow down a single document
   * @param readRequestOptions read option
   * @return a document of type {@code T}
   * @throws TigrisDBException in case of an error
   */
  Iterator<T> read(TigrisFilter filter, ReadRequestOptions readRequestOptions)
      throws TigrisDBException;

  /**
   * Reads a matching document
   *
   * @param filter filter to narrow down a single document
   * @return a document of type {@code T}
   * @throws TigrisDBException in case of an error
   */
  Iterator<T> read(TigrisFilter filter) throws TigrisDBException;

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
   * @param documents list of documents to replace
   * @param replaceRequestOptions replace option
   * @return an instance of {@link ReplaceResponse} from server
   * @throws TigrisDBException in case of an error
   */
  ReplaceResponse replace(List<T> documents, ReplaceRequestOptions replaceRequestOptions)
      throws TigrisDBException;

  /**
   * @param documents list of documents to replace
   * @return an instance of {@link ReplaceResponse} from server
   * @throws TigrisDBException in case of an error
   */
  ReplaceResponse replace(List<T> documents) throws TigrisDBException;

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
