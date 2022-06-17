package com.tigrisdata.db.client;

import com.tigrisdata.db.client.error.TigrisException;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public interface TransactionalCollectionOperation<T> {

  /**
   * Transaction aware read
   *
   * @param tx transaction session
   * @param filter filter to narrow down read
   * @param fields optionally specify fields you want to be returned from server
   * @param readRequestOptions read options
   * @return stream of documents
   * @throws TigrisException in case of an error
   */
  Iterator<T> read(
      TransactionSession tx,
      TigrisFilter filter,
      ReadFields fields,
      ReadRequestOptions readRequestOptions)
      throws TigrisException;

  /**
   * Transaction aware read
   *
   * @param tx transaction session
   * @param filter filter to narrow down read
   * @param fields optionally specify fields you want to be returned from server
   * @return stream of documents
   * @throws TigrisException in case of an error
   */
  Iterator<T> read(TransactionSession tx, TigrisFilter filter, ReadFields fields)
      throws TigrisException;

  /**
   * Transaction aware read
   *
   * @param tx transaction session
   * @param filter filter to narrow down read
   * @return stream of documents
   * @throws TigrisException in case of an error
   */
  Iterator<T> read(TransactionSession tx, TigrisFilter filter) throws TigrisException;

  /**
   * Transaction aware: Reads a single document. This method is generally recommended for point
   * lookup, if used for non-point lookup any arbitrary matching document will be returned.
   *
   * @param tx transaction session
   * @param filter filters documents to read
   * @return Optional of document.
   * @throws TigrisException in case of an error
   */
  Optional<T> readOne(TransactionSession tx, TigrisFilter filter) throws TigrisException;

  /**
   * Transaction aware insertion
   *
   * @param tx transaction session
   * @param documents list of documents to insert
   * @param insertRequestOptions insert option
   * @return an instance of {@link InsertResponse} from server
   * @throws TigrisException in case of an error
   */
  InsertResponse insert(
      TransactionSession tx, List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisException;

  /**
   * Transaction aware insertion
   *
   * @param tx transaction session
   * @param documents list of documents to insert
   * @return an instance of {@link InsertResponse} from server
   * @throws TigrisException in case of an error
   */
  InsertResponse insert(TransactionSession tx, List<T> documents) throws TigrisException;

  /**
   * Transaction aware: Inserts the documents if they don't exist already, replaces them otherwise.
   *
   * @param tx transaction session
   * @param documents list of documents to replace
   * @param insertOrReplaceRequestOptions option
   * @return an instance of {@link InsertOrReplaceResponse} from server
   * @throws TigrisException in case of an error
   */
  InsertOrReplaceResponse insertOrReplace(
      TransactionSession tx,
      List<T> documents,
      InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisException;

  /**
   * Transaction session: Inserts the documents if they don't exist already, replaces them
   * otherwise.
   *
   * @param tx transaction session
   * @param documents list of documents to replace
   * @return an instance of {@link InsertOrReplaceResponse} from server
   * @throws TigrisException in case of an error
   */
  InsertOrReplaceResponse insertOrReplace(TransactionSession tx, List<T> documents)
      throws TigrisException;

  /**
   * Transaction session: inserts a single document to the collection
   *
   * @param tx transaction session
   * @param document document to insert
   * @return an instance of InsertResponse
   * @throws TigrisException in case of an error
   */
  InsertResponse insert(TransactionSession tx, T document) throws TigrisException;

  /**
   * Transaction aware update
   *
   * @param tx transaction session
   * @param filter filters documents to update
   * @param updateFields specifies what and how to update the fields from filtered documents
   * @param updateRequestOptions options
   * @return an instance of UpdateResponse
   * @throws TigrisException in case of an error
   */
  UpdateResponse update(
      TransactionSession tx,
      TigrisFilter filter,
      UpdateFields updateFields,
      UpdateRequestOptions updateRequestOptions)
      throws TigrisException;

  /**
   * Transaction aware update
   *
   * @param tx transaction session
   * @param filter filters documents to update
   * @param updateFields specifies what and how to update the fields from filtered documents
   * @return an instance of UpdateResponse
   * @throws TigrisException in case of an error
   */
  UpdateResponse update(TransactionSession tx, TigrisFilter filter, UpdateFields updateFields)
      throws TigrisException;

  /**
   * Transaction aware: Deletes the matching documents in the collection.
   *
   * @param tx Transaction session
   * @param filter filter to narrow down the documents to delete
   * @param deleteRequestOptions delete option
   * @return an instance of {@link DeleteResponse} from server
   * @throws TigrisException in case of an error
   */
  DeleteResponse delete(
      TransactionSession tx, TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisException;

  /**
   * Transaction aware: Deletes the matching documents in the collection.
   *
   * @param tx transaction session
   * @param filter filter to narrow down the documents to delete
   * @return an instance of {@link DeleteResponse} from server
   * @throws TigrisException in case of an error
   */
  DeleteResponse delete(TransactionSession tx, TigrisFilter filter) throws TigrisException;
}
