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

import static com.tigrisdata.db.client.Constants.DESCRIBE_COLLECTION_FAILED;
import static com.tigrisdata.db.client.Constants.EVENTS_FAILED;
import static com.tigrisdata.db.client.Constants.READ_FAILED;
import static com.tigrisdata.db.client.TypeConverter.toCollectionDescription;
import static com.tigrisdata.db.client.TypeConverter.toCollectionOptions;
import com.tigrisdata.db.client.config.TigrisConfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.search.SearchRequest;
import com.tigrisdata.db.client.search.SearchRequestOptions;
import com.tigrisdata.db.client.search.SearchResult;
import com.tigrisdata.db.type.TigrisCollectionType;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** Tigris collection implementation */
class StandardTigrisCollection<T extends TigrisCollectionType> extends AbstractTigrisCollection<T>
    implements TigrisCollection<T> {

  StandardTigrisCollection(
      String databaseName,
      Class<T> collectionTypeClass,
      TigrisGrpc.TigrisBlockingStub stub,
      ObjectMapper objectMapper,
      TigrisConfiguration configuration) {
    super(databaseName, collectionTypeClass, stub, objectMapper, configuration);
  }

  @Override
  public Iterator<T> read(
      TigrisFilter filter, ReadFields fields, ReadRequestOptions readRequestOptions)
      throws TigrisException {
    return this.readInternal(filter, fields, readRequestOptions, null);
  }

  @Override
  public Iterator<T> read(TigrisFilter filter, ReadRequestOptions readRequestOptions)
      throws TigrisException {
    return this.read(filter, ReadFields.all(), readRequestOptions);
  }

  @Override
  public Iterator<T> read(TigrisFilter filter) throws TigrisException {
    return this.read(filter, ReadFields.all(), new ReadRequestOptions());
  }

  @Override
  public Iterator<T> read(
      TransactionSession session,
      TigrisFilter filter,
      ReadFields fields,
      ReadRequestOptions readRequestOptions)
      throws TigrisException {
    return this.readInternal(filter, fields, readRequestOptions, session);
  }

  @Override
  public Iterator<T> read(TransactionSession tx, TigrisFilter filter) throws TigrisException {
    return this.read(tx, filter, ReadFields.all(), new ReadRequestOptions());
  }

  @Override
  public Iterator<T> read(TigrisFilter filter, ReadFields fields) throws TigrisException {
    return this.read(filter, fields, new ReadRequestOptions());
  }

  @Override
  public Iterator<T> read(TransactionSession session, TigrisFilter filter, ReadFields fields)
      throws TigrisException {
    return this.read(session, filter, fields, new ReadRequestOptions());
  }

  @Override
  public Iterator<T> readAll() throws TigrisException {
    return this.read(Filters.nothing(), ReadFields.all(), new ReadRequestOptions());
  }

  @Override
  public Iterator<T> readAll(ReadFields readFields) throws TigrisException {
    return this.read(Filters.nothing(), readFields, new ReadRequestOptions());
  }

  @Override
  public Optional<T> readOne(TigrisFilter filter) throws TigrisException {
    return this.readOne(null, filter);
  }

  @Override
  public Optional<T> readOne(TransactionSession session, TigrisFilter filter)
      throws TigrisException {
    Iterator<T> iterator = this.read(session, filter);
    try {
      if (iterator.hasNext()) {
        return Optional.of(iterator.next());
      }
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          READ_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
    return Optional.empty();
  }

  @Override
  public Optional<SearchResult<T>> search(SearchRequest request, SearchRequestOptions options)
      throws TigrisException {
    Iterator<SearchResult<T>> resultIterator = this.searchInternal(request, options);
    if (resultIterator.hasNext()) {
      return Optional.of(resultIterator.next());
    }
    return Optional.empty();
  }

  @Override
  public Iterator<SearchResult<T>> search(SearchRequest request) throws TigrisException {
    return this.searchInternal(request, null);
  }

  @Override
  public InsertResponse<T> insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisException {
    return this.insertInternal(documents, insertRequestOptions, null);
  }

  @Override
  public InsertResponse<T> insert(
      TransactionSession session, List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisException {
    return this.insertInternal(documents, insertRequestOptions, session);
  }

  @Override
  public InsertResponse<T> insert(List<T> documents) throws TigrisException {
    return this.insert(documents, new InsertRequestOptions(WriteOptions.DEFAULT_INSTANCE));
  }

  @Override
  public InsertResponse<T> insert(TransactionSession session, List<T> documents)
      throws TigrisException {
    return this.insert(session, documents, new InsertRequestOptions(WriteOptions.DEFAULT_INSTANCE));
  }

  @Override
  public InsertResponse<T> insert(T document) throws TigrisException {
    return this.insert(Collections.singletonList(document));
  }

  @Override
  public InsertResponse<T> insert(TransactionSession session, T document) throws TigrisException {
    return this.insert(session, Collections.singletonList(document));
  }

  @Override
  public InsertOrReplaceResponse<T> insertOrReplace(
      List<T> documents, InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisException {
    return this.insertOrReplaceInternal(documents, insertOrReplaceRequestOptions, null);
  }

  @Override
  public InsertOrReplaceResponse<T> insertOrReplace(
      TransactionSession session,
      List<T> documents,
      InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisException {
    return this.insertOrReplaceInternal(documents, insertOrReplaceRequestOptions, session);
  }

  @Override
  public InsertOrReplaceResponse<T> insertOrReplace(List<T> documents) throws TigrisException {
    return this.insertOrReplace(documents, new InsertOrReplaceRequestOptions());
  }

  @Override
  public InsertOrReplaceResponse<T> insertOrReplace(TransactionSession session, List<T> documents)
      throws TigrisException {
    return this.insertOrReplace(session, documents, new InsertOrReplaceRequestOptions());
  }

  @Override
  public UpdateResponse update(
      TigrisFilter filter, UpdateFields updateFields, UpdateRequestOptions updateRequestOptions)
      throws TigrisException {
    return this.updateInternal(filter, updateFields, updateRequestOptions, null);
  }

  @Override
  public UpdateResponse update(
      TransactionSession session,
      TigrisFilter filter,
      UpdateFields updateFields,
      UpdateRequestOptions updateRequestOptions)
      throws TigrisException {
    return this.updateInternal(filter, updateFields, updateRequestOptions, session);
  }

  @Override
  public UpdateResponse update(TigrisFilter filter, UpdateFields updateFields)
      throws TigrisException {
    return update(filter, updateFields, new UpdateRequestOptions());
  }

  @Override
  public UpdateResponse update(
      TransactionSession session, TigrisFilter filter, UpdateFields updateFields)
      throws TigrisException {
    return this.update(session, filter, updateFields, new UpdateRequestOptions());
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisException {
    return this.deleteInternal(filter, deleteRequestOptions, null);
  }

  @Override
  public DeleteResponse delete(
      TransactionSession session, TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisException {
    return this.deleteInternal(filter, deleteRequestOptions, session);
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter) throws TigrisException {
    return this.delete(filter, new DeleteRequestOptions(WriteOptions.DEFAULT_INSTANCE));
  }

  @Override
  public DeleteResponse delete(TransactionSession session, TigrisFilter filter)
      throws TigrisException {
    return this.delete(session, filter, new DeleteRequestOptions(WriteOptions.DEFAULT_INSTANCE));
  }

  @Override
  public CollectionDescription describe(CollectionOptions options) throws TigrisException {
    try {
      Api.DescribeCollectionResponse response =
          blockingStub.describeCollection(
              Api.DescribeCollectionRequest.newBuilder()
                  .setCollection(collectionName)
                  .setOptions(toCollectionOptions(options))
                  .build());
      return toCollectionDescription(response);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          DESCRIBE_COLLECTION_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  @Override
  public Iterator<StreamEvent> events() throws TigrisException {
    try {
      Api.EventsRequest streamRequest =
          Api.EventsRequest.newBuilder().setDb(databaseName).setCollection(collectionName).build();
      Iterator<Api.EventsResponse> streamResponseIterator = blockingStub.events(streamRequest);
      Function<Api.EventsResponse, StreamEvent> converter =
          streamResponse -> {
            try {
              return StreamEvent.from(streamResponse.getEvent(), objectMapper);
            } catch (IOException e) {
              throw new IllegalArgumentException("Failed to convert event data to JSON", e);
            }
          };
      return Utilities.transformIterator(streamResponseIterator, converter);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          EVENTS_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  @Override
  public String name() {
    return collectionName;
  }
}
