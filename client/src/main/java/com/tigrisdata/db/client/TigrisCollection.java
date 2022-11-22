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
import com.tigrisdata.db.client.search.SearchResult;
import com.tigrisdata.db.type.TigrisCollectionType;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Tigris collection
 *
 * @param <T> type of the collection
 */
public interface TigrisCollection<T extends TigrisCollectionType>
    extends TransactionalCollectionOperation<T> {

  /**
   * @param filter filter to narrow down read
   * @param fields optionally specify fields you want to be returned from server
   * @param readRequestOptions read options
   * @return stream of documents
   * @throws TigrisException in case of an error
   */
  Iterator<T> read(TigrisFilter filter, ReadFields fields, ReadRequestOptions readRequestOptions)
      throws TigrisException;

  /**
   * @param filter filter to narrow down read
   * @param readRequestOptions read options
   * @return stream of documents
   * @throws TigrisException in case of an error
   */
  Iterator<T> read(TigrisFilter filter, ReadRequestOptions readRequestOptions)
      throws TigrisException;

  /**
   * Reads matching documents
   *
   * @param filter filter to narrow down read
   * @param fields optionally specify fields you want to be returned from server
   * @return stream of documents
   * @throws TigrisException in case of an error
   */
  Iterator<T> read(TigrisFilter filter, ReadFields fields) throws TigrisException;

  /**
   * Reads matching documents
   *
   * @param filter filter to narrow down read
   * @return stream of documents
   * @throws TigrisException in case of an error
   */
  Iterator<T> read(TigrisFilter filter) throws TigrisException;

  /**
   * Reads all the documents
   *
   * @return stream of documents
   * @throws TigrisException in case of an error
   */
  Iterator<T> readAll() throws TigrisException;

  /**
   * Reads all the documents
   *
   * @param readFields select what fields to read from all the documents
   * @return stream of documents
   * @throws TigrisException in case of an error
   */
  Iterator<T> readAll(ReadFields readFields) throws TigrisException;

  /**
   * Reads a single document. This method is generally recommended for point lookup, if used for
   * non-point lookup any arbitrary matching document will be returned.
   *
   * @param filter filters documents to read
   * @return Optional of document.
   * @throws TigrisException in case of an error
   */
  Optional<T> readOne(TigrisFilter filter) throws TigrisException;

  /**
   * Search for documents in a collection. Easily perform sophisticated queries and refine results
   * using filters with advanced features like faceting and ordering.
   *
   * <p>Note: Searching is expensive. If using as a primary key based lookup, use {@code read()}
   * instead
   *
   * @param request search request to execute
   * @param options search pagination options
   * @return Optional Search result
   * @throws TigrisException in case of error
   * @see #search(SearchRequest)
   */
  Optional<SearchResult<T>> search(SearchRequest request, SearchRequestOptions options)
      throws TigrisException;

  /**
   * Search for documents in a collection.
   *
   * <p>Wrapper around {@link #search(SearchRequest, SearchRequestOptions)} with default pagination
   * options
   *
   * @param request search request to execute
   * @return stream of search results
   * @throws TigrisException in case of error
   */
  Iterator<SearchResult<T>> search(SearchRequest request) throws TigrisException;

  /**
   * Inserts the documents into collection.
   *
   * <p>Note: if your collection model has primary key that is tagged to autoGenerate values. The
   * input list of documents will be modified to set the primary key fields after successful
   * insertion.
   *
   * @param documents list of documents to insert
   * @param insertRequestOptions insert option
   * @return an instance of {@link InsertResponse} from server
   * @throws TigrisException in case of an error
   */
  InsertResponse<T> insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisException;

  /**
   * Inserts the document into collection.
   *
   * <p>Note: if your collection model has primary key that is tagged to autoGenerate values. The
   * input list of documents will be modified to set the primary key fields after successful
   * insertion.
   *
   * @param documents list of documents to insert
   * @return an instance of {@link InsertResponse} from server
   * @throws TigrisException in case of an error
   */
  InsertResponse<T> insert(List<T> documents) throws TigrisException;

  /**
   * Inserts the documents if they don't exist already, replaces them otherwise.
   *
   * <p>Note: if your collection model has primary key that is tagged to autoGenerate values. The
   * input list of documents will be modified to set the primary key fields after successful
   * insertion.
   *
   * @param documents list of documents to replace
   * @param insertOrReplaceRequestOptions option
   * @return an instance of {@link InsertOrReplaceResponse} from server
   * @throws TigrisException in case of an error
   */
  InsertOrReplaceResponse<T> insertOrReplace(
      List<T> documents, InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisException;

  /**
   * Inserts the documents if they don't exist already, replaces them otherwise.
   *
   * <p>Note: if your collection model has primary key that is tagged to autoGenerate values. The
   * input list of documents will be modified to set the primary key fields after successful
   * insertion.
   *
   * @param documents list of documents to replace
   * @return an instance of {@link InsertOrReplaceResponse} from server
   * @throws TigrisException in case of an error
   */
  InsertOrReplaceResponse<T> insertOrReplace(List<T> documents) throws TigrisException;

  /**
   * inserts a single document to the collection
   *
   * <p>Note: if your collection model has primary key that is tagged to autoGenerate values. The
   * input list of documents will be modified to set the primary key fields after successful
   * insertion.
   *
   * @param document document to insert
   * @return an instance of InsertResponse
   * @throws TigrisException in case of an error
   */
  InsertResponse<T> insert(T document) throws TigrisException;

  /**
   * @param filter filters documents to update
   * @param updateFields specifies what and how to update the fields from filtered documents
   * @param updateRequestOptions options
   * @return an instance of UpdateResponse
   * @throws TigrisException in case of an error
   */
  UpdateResponse update(
      TigrisFilter filter, UpdateFields updateFields, UpdateRequestOptions updateRequestOptions)
      throws TigrisException;

  /**
   * @param filter filters documents to update
   * @param updateFields specifies what and how to update the fields from filtered documents
   * @return an instance of UpdateResponse
   * @throws TigrisException in case of an error
   */
  UpdateResponse update(TigrisFilter filter, UpdateFields updateFields) throws TigrisException;

  /**
   * Deletes the matching documents in the collection.
   *
   * @param filter filter to narrow down the documents to delete
   * @param deleteRequestOptions delete option
   * @return an instance of {@link DeleteResponse} from server
   * @throws TigrisException in case of an error
   */
  DeleteResponse delete(TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisException;

  /**
   * Deletes the matching documents in the collection.
   *
   * @param filter filter to narrow down the documents to delete
   * @return an instance of {@link DeleteResponse} from server
   * @throws TigrisException in case of an error
   */
  DeleteResponse delete(TigrisFilter filter) throws TigrisException;

  /**
   * Describes the collection
   *
   * @param collectionOptions options
   * @return description
   * @throws TigrisException in case of an error
   */
  CollectionDescription describe(CollectionOptions collectionOptions) throws TigrisException;

  /**
   * @return stream of events.
   * @throws TigrisException in case of an error.
   */
  Iterator<StreamEvent> events() throws TigrisException;

  /** @return Name of the collection */
  String name();
}
