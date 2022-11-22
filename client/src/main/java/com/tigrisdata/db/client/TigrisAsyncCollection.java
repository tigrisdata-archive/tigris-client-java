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

import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.search.SearchRequest;
import com.tigrisdata.db.client.search.SearchRequestOptions;
import com.tigrisdata.db.type.TigrisCollectionType;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * An async Tigris collection
 *
 * @param <T> type of collection
 */
public interface TigrisAsyncCollection<T extends TigrisCollectionType>
    extends TransactionalCollectionOperation<T> {

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
      TigrisAsyncReader<T> reader);

  /**
   * Reads matching documents
   *
   * @param filter filter to narrow down read
   * @param fields optionally specify fields you want to be returned from server
   * @param reader reader callback
   */
  void read(TigrisFilter filter, ReadFields fields, TigrisAsyncReader<T> reader);

  /**
   * Reads matching documents
   *
   * @param filter filter to narrow down read
   * @param reader reader callback
   */
  void read(TigrisFilter filter, TigrisAsyncReader<T> reader);

  /**
   * Reads all the documents
   *
   * @param reader reader callback
   */
  void readAll(TigrisAsyncReader<T> reader);

  /**
   * Reads all the documents
   *
   * @param readFields select what fields to read from all the documents
   * @param reader reader callback
   */
  void readAll(ReadFields readFields, TigrisAsyncReader<T> reader);

  /**
   * Reads a single document. This method is generally recommended for point lookup, if used for
   * non-point lookup any arbitrary matching document will be returned.
   *
   * @param filter filter to read one document
   * @return a future to the document
   */
  CompletableFuture<Optional<T>> readOne(TigrisFilter filter);

  /**
   * Search for documents in a collection. Easily perform sophisticated queries and refine results
   * using filters with advanced features like faceting and ordering.
   *
   * <p>Note: Searching is expensive. If using as a primary key based lookup, use {@code read()}
   * instead
   *
   * @param request search request to execute
   * @param options search pagination options
   * @param reader reader callback
   * @see #search(SearchRequest, TigrisAsyncSearchReader)
   */
  void search(
      SearchRequest request, SearchRequestOptions options, TigrisAsyncSearchReader<T> reader);

  /**
   * Search for documents in a collection.
   *
   * <p>Wrapper around {@link #search(SearchRequest, SearchRequestOptions, TigrisAsyncSearchReader)}
   * with default pagination options
   *
   * @param request search request to execute
   * @param reader reader callback
   */
  void search(SearchRequest request, TigrisAsyncSearchReader<T> reader);

  /**
   * Inserts documents into collection
   *
   * <p>Note: if your collection model has primary key that is tagged to autoGenerate values. The
   * input list of documents will be modified to set the primary key fields after successful
   * insertion.
   *
   * @param documents list of documents to insert
   * @param insertRequestOptions insert option
   * @return a future to the {@link InsertResponse}
   * @throws TigrisException in case of an error
   */
  CompletableFuture<InsertResponse<T>> insert(
      List<T> documents, InsertRequestOptions insertRequestOptions) throws TigrisException;

  /**
   * Inserts documents into collection
   *
   * <p>Note: if your collection model has primary key that is tagged to autoGenerate values. The
   * input list of documents will be modified to set the primary key fields after successful
   * insertion.
   *
   * @param documents list of documents to insert
   * @return a future to the {@link InsertResponse}
   * @throws TigrisException in case of an error
   */
  CompletableFuture<InsertResponse<T>> insert(List<T> documents) throws TigrisException;

  /**
   * inserts a single document to the collection
   *
   * <p>Note: if your collection model has primary key that is tagged to autoGenerate values. The
   * input list of documents will be modified to set the primary key fields after successful
   * insertion.
   *
   * @param document document to insert
   * @return a future to the {@link InsertResponse}
   * @throws TigrisException in case of an error
   */
  CompletableFuture<InsertResponse<T>> insert(T document) throws TigrisException;

  /**
   * Inserts the documents if they don't exist already, replaces them otherwise.
   *
   * <p>Note: if your collection model has primary key that is tagged to autoGenerate values. The
   * input list of documents will be modified to set the primary key fields after successful
   * insertion.
   *
   * @param documents list of documents to replace
   * @param insertOrReplaceRequestOptions option
   * @return a future to the {@link InsertOrReplaceResponse}
   * @throws TigrisException in case of an error
   */
  CompletableFuture<InsertOrReplaceResponse<T>> insertOrReplace(
      List<T> documents, InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisException;

  /**
   * Inserts the documents if they don't exist already, replaces them otherwise.
   *
   * @param documents list of documents to replace
   * @return a future to the {@link InsertOrReplaceResponse}
   * @throws TigrisException in case of an error
   */
  CompletableFuture<InsertOrReplaceResponse<T>> insertOrReplace(List<T> documents)
      throws TigrisException;

  /**
   * @param filter filters documents to update
   * @param fields specifies what and how to update the fields from filtered documents
   * @param updateRequestOptions options
   * @return a future to the {@link UpdateResponse}
   * @throws TigrisException in case of an error
   */
  CompletableFuture<UpdateResponse> update(
      TigrisFilter filter, UpdateFields fields, UpdateRequestOptions updateRequestOptions)
      throws TigrisException;

  /**
   * @param filter filters documents to update
   * @param fields specifies what and how to update the fields from filtered documents
   * @return a future to the {@link UpdateResponse}
   * @throws TigrisException in case of an error
   */
  CompletableFuture<UpdateResponse> update(TigrisFilter filter, UpdateFields fields)
      throws TigrisException;

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

  /**
   * Describes the collection
   *
   * @param collectionOptions options
   * @return future to the collection description
   * @throws TigrisException in case of an error
   */
  CompletableFuture<CollectionDescription> describe(CollectionOptions collectionOptions)
      throws TigrisException;

  /** @param eventer streamer callback */
  void events(TigrisAsyncEventer eventer);

  /** @return Name of the collection */
  String name();
}
