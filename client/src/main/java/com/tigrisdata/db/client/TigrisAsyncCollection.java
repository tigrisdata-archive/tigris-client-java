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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * An async TigrisDB collection
 *
 * @param <T> type of collection
 */
public interface TigrisAsyncCollection<T extends TigrisCollectionType> {

  /**
   * @param filter filter to narrow down read
   * @param fields optionally specify fields you want to be returned from server
   * @param readRequestOptions read options
   * @param reader reader callback
   */
  void read(
      TigrisFilter filter,
      ReadFields fields,
      ReadRequestOptions readRequestOptions,
      TigrisDBAsyncReader<T> reader);

  /**
   * Reads matching documents
   *
   * @param filter filter to narrow down read
   * @param fields optionally specify fields you want to be returned from server
   * @param reader reader callback
   */
  void read(TigrisFilter filter, ReadFields fields, TigrisDBAsyncReader<T> reader);

  /**
   * Reads a single document. This method is generally recommended for point lookup, if used for
   * non-point lookup any arbitrary matching document will be returned.
   *
   * @param filter filter to read one document
   * @return a future to the document
   */
  CompletableFuture<Optional<T>> readOne(TigrisFilter filter);

  /**
   * @param documents list of documents to insert
   * @param insertRequestOptions insert option
   * @return a future to the {@link InsertResponse}
   * @throws TigrisDBException in case of an error
   */
  CompletableFuture<InsertResponse> insert(
      List<T> documents, InsertRequestOptions insertRequestOptions) throws TigrisDBException;

  /**
   * @param documents list of documents to insert
   * @return a future to the {@link InsertResponse}
   * @throws TigrisDBException in case of an error
   */
  CompletableFuture<InsertResponse> insert(List<T> documents) throws TigrisDBException;

  /**
   * inserts a single document to the collection
   *
   * @param document document to insert
   * @return a future to the {@link InsertResponse}
   * @throws TigrisDBException in case of an error
   */
  CompletableFuture<InsertResponse> insert(T document) throws TigrisDBException;

  /**
   * Inserts the documents if they don't exist already, replaces them otherwise.
   *
   * @param documents list of documents to replace
   * @param insertOrReplaceRequestOptions option
   * @return a future to the {@link InsertOrReplaceResponse}
   * @throws TigrisDBException in case of an error
   */
  CompletableFuture<InsertOrReplaceResponse> insertOrReplace(
      List<T> documents, InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisDBException;

  /**
   * Inserts the documents if they don't exist already, replaces them otherwise.
   *
   * @param documents list of documents to replace
   * @return a future to the {@link InsertOrReplaceResponse}
   * @throws TigrisDBException in case of an error
   */
  CompletableFuture<InsertOrReplaceResponse> insertOrReplace(List<T> documents)
      throws TigrisDBException;

  /**
   * @param filter filters documents to update
   * @param fields specifies what and how to update the fields from filtered documents
   * @param updateRequestOptions options
   * @return a future to the {@link UpdateResponse}
   * @throws TigrisDBException in case of an error
   */
  CompletableFuture<UpdateResponse> update(
      TigrisFilter filter, UpdateFields fields, UpdateRequestOptions updateRequestOptions)
      throws TigrisDBException;

  /**
   * @param filter filters documents to update
   * @param fields specifies what and how to update the fields from filtered documents
   * @return a future to the {@link UpdateResponse}
   * @throws TigrisDBException in case of an error
   */
  CompletableFuture<UpdateResponse> update(TigrisFilter filter, UpdateFields fields)
      throws TigrisDBException;

  /**
   * Deletes the matching documents in the collection.
   *
   * @param filter filter to narrow down the documents to delete
   * @param deleteRequestOptions delete option
   * @return a future to the {@link DeleteResponse}
   */
  CompletableFuture<DeleteResponse> delete(
      TigrisFilter filter, DeleteRequestOptions deleteRequestOptions);

  /**
   * Deletes the matching documents in the collection.
   *
   * @param filter filter to narrow down the documents to delete
   * @return a future to the {@link DeleteResponse}
   */
  CompletableFuture<DeleteResponse> delete(TigrisFilter filter);

  /** @return Name of the collection */
  String name();
}
