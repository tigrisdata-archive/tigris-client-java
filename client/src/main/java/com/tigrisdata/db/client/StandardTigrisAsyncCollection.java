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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ListenableFuture;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import static com.tigrisdata.db.client.Messages.DELETE_FAILED;
import static com.tigrisdata.db.client.Messages.DESCRIBE_COLLECTION_FAILED;
import static com.tigrisdata.db.client.Messages.INSERT_FAILED;
import static com.tigrisdata.db.client.Messages.INSERT_OR_REPLACE_FAILED;
import static com.tigrisdata.db.client.Messages.READ_FAILED;
import static com.tigrisdata.db.client.Messages.UPDATE_FAILED;
import static com.tigrisdata.db.client.TypeConverter.readOneDefaultReadRequestOptions;
import static com.tigrisdata.db.client.TypeConverter.toCollectionDescription;
import static com.tigrisdata.db.client.TypeConverter.toCollectionOptions;
import static com.tigrisdata.db.client.TypeConverter.toDeleteRequest;
import static com.tigrisdata.db.client.TypeConverter.toInsertRequest;
import static com.tigrisdata.db.client.TypeConverter.toReadRequest;
import static com.tigrisdata.db.client.TypeConverter.toReplaceRequest;
import static com.tigrisdata.db.client.TypeConverter.toUpdateRequest;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisCollectionType;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * An async implementation of TigrisDB Collection
 *
 * @param <T> type of the collection
 */
public class StandardTigrisAsyncCollection<T extends TigrisCollectionType>
    implements TigrisAsyncCollection<T> {
  private final String databaseName;
  private final String collectionName;
  private final Class<T> collectionTypeClass;
  private final Executor executor;
  private final TigrisDBGrpc.TigrisDBStub stub;
  private final TigrisDBGrpc.TigrisDBFutureStub futureStub;
  private final ObjectMapper objectMapper;

  StandardTigrisAsyncCollection(
      String databaseName,
      Class<T> collectionTypeClass,
      ManagedChannel channel,
      Executor executor,
      ObjectMapper objectMapper) {
    this.databaseName = databaseName;
    this.collectionName = Utilities.getCollectionName(collectionTypeClass);
    this.collectionTypeClass = collectionTypeClass;
    this.executor = executor;
    this.stub = TigrisDBGrpc.newStub(channel);
    this.futureStub = TigrisDBGrpc.newFutureStub(channel);
    this.objectMapper = objectMapper;
  }

  @Override
  public void read(
      TigrisFilter filter,
      ReadFields fields,
      ReadRequestOptions readRequestOptions,
      TigrisAsyncReader<T> reader) {
    Api.ReadRequest readRequest =
        toReadRequest(
            databaseName, collectionName, filter, fields, readRequestOptions, objectMapper);
    stub.read(
        readRequest,
        new ReadManyResponseObserverAdapter<>(
            reader, collectionTypeClass, objectMapper, READ_FAILED));
  }

  @Override
  public void read(TigrisFilter filter, ReadFields fields, TigrisAsyncReader<T> reader) {
    this.read(filter, fields, new ReadRequestOptions(), reader);
  }

  @Override
  public CompletableFuture<Optional<T>> readOne(TigrisFilter filter) {
    Api.ReadRequest readRequest =
        toReadRequest(
            databaseName,
            collectionName,
            filter,
            ReadFields.empty(),
            readOneDefaultReadRequestOptions(),
            objectMapper);
    CompletableFuture<Optional<T>> completableFuture = new CompletableFuture<>();
    stub.read(
        readRequest,
        new ReadSingleResponseObserverAdapter<>(
            completableFuture, collectionTypeClass, objectMapper, READ_FAILED));
    return completableFuture;
  }

  @Override
  public CompletableFuture<InsertResponse> insert(
      List<T> documents, InsertRequestOptions insertRequestOptions) throws TigrisException {
    try {
      Api.InsertRequest insertRequest =
          toInsertRequest(
              databaseName, collectionName, documents, insertRequestOptions, objectMapper);
      ListenableFuture<Api.InsertResponse> insertResponseListenableFuture =
          futureStub.insert(insertRequest);
      return Utilities.transformFuture(
          insertResponseListenableFuture,
          input ->
              new InsertResponse(
                  input.getStatus(),
                  input.getMetadata().getCreatedAt(),
                  input.getMetadata().getUpdatedAt()),
          executor,
          INSERT_FAILED);
    } catch (JsonProcessingException jsonProcessingException) {
      throw new TigrisException(
          "Failed to serialize documents to JSON, This should never happen on generated Java "
              + "models, if the Java models are not locally modified please report a bug",
          jsonProcessingException);
    }
  }

  @Override
  public CompletableFuture<InsertResponse> insert(List<T> documents) throws TigrisException {
    return this.insert(documents, new InsertRequestOptions());
  }

  @Override
  public CompletableFuture<InsertResponse> insert(T document) throws TigrisException {
    return this.insert(Collections.singletonList(document));
  }

  @Override
  public CompletableFuture<InsertOrReplaceResponse> insertOrReplace(
      List<T> documents, InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisException {
    try {
      Api.ReplaceRequest replaceRequest =
          toReplaceRequest(
              databaseName, collectionName, documents, insertOrReplaceRequestOptions, objectMapper);
      ListenableFuture<Api.ReplaceResponse> replaceResponseListenableFuture =
          futureStub.replace(replaceRequest);
      return Utilities.transformFuture(
          replaceResponseListenableFuture,
          input ->
              new InsertOrReplaceResponse(
                  input.getStatus(),
                  input.getMetadata().getCreatedAt(),
                  input.getMetadata().getUpdatedAt()),
          executor,
          INSERT_OR_REPLACE_FAILED);
    } catch (JsonProcessingException jsonProcessingException) {
      throw new TigrisException(
          "Failed to serialize documents to JSON, This should never happen on generated Java "
              + "models, if the Java models are not locally modified please report a bug",
          jsonProcessingException);
    }
  }

  @Override
  public CompletableFuture<InsertOrReplaceResponse> insertOrReplace(List<T> documents)
      throws TigrisException {
    return this.insertOrReplace(documents, new InsertOrReplaceRequestOptions());
  }

  @Override
  public CompletableFuture<UpdateResponse> update(
      TigrisFilter filter, UpdateFields fields, UpdateRequestOptions updateRequestOptions)
      throws TigrisException {
    Api.UpdateRequest updateRequest =
        toUpdateRequest(
            databaseName, collectionName, filter, fields, updateRequestOptions, objectMapper);
    ListenableFuture<Api.UpdateResponse> updateResponseListenableFuture =
        futureStub.update(updateRequest);
    return Utilities.transformFuture(
        updateResponseListenableFuture,
        input ->
            new UpdateResponse(
                input.getStatus(),
                input.getMetadata().getCreatedAt(),
                input.getMetadata().getUpdatedAt(),
                input.getModifiedCount()),
        executor,
        UPDATE_FAILED);
  }

  @Override
  public CompletableFuture<UpdateResponse> update(TigrisFilter filter, UpdateFields fields)
      throws TigrisException {
    return this.update(filter, fields, new UpdateRequestOptions());
  }

  @Override
  public CompletableFuture<DeleteResponse> delete(
      TigrisFilter filter, DeleteRequestOptions deleteRequestOptions) {
    Api.DeleteRequest deleteRequest =
        toDeleteRequest(databaseName, collectionName, filter, deleteRequestOptions, objectMapper);
    ListenableFuture<Api.DeleteResponse> deleteResponseListenableFuture =
        futureStub.delete(deleteRequest);
    return Utilities.transformFuture(
        deleteResponseListenableFuture,
        response ->
            new DeleteResponse(
                response.getStatus(),
                response.getMetadata().getCreatedAt(),
                response.getMetadata().getUpdatedAt()),
        executor,
        DELETE_FAILED);
  }

  @Override
  public CompletableFuture<DeleteResponse> delete(TigrisFilter filter) {
    return this.delete(filter, new DeleteRequestOptions());
  }

  @Override
  public CompletableFuture<CollectionDescription> describe(CollectionOptions collectionOptions)
      throws TigrisException {
    ListenableFuture<Api.DescribeCollectionResponse> describeCollectionResponseListenableFuture =
        futureStub.describeCollection(
            Api.DescribeCollectionRequest.newBuilder()
                .setCollection(collectionName)
                .setOptions(toCollectionOptions(collectionOptions, Optional.empty()))
                .build());
    return Utilities.transformFuture(
        describeCollectionResponseListenableFuture,
        response -> {
          try {
            return toCollectionDescription(response);
          } catch (TigrisException e) {
            throw new IllegalArgumentException(e);
          }
        },
        executor,
        DESCRIBE_COLLECTION_FAILED);
  }

  @Override
  public String name() {
    return collectionName;
  }

  static class ReadManyResponseObserverAdapter<T extends TigrisCollectionType>
      implements StreamObserver<Api.ReadResponse> {
    private final TigrisAsyncReader<T> reader;
    private final Class<T> collectionTypeClass;
    private final ObjectMapper objectMapper;
    private final String errorMessage;

    public ReadManyResponseObserverAdapter(
        TigrisAsyncReader<T> reader,
        Class<T> collectionTypeClass,
        ObjectMapper objectMapper,
        String errorMessage) {
      this.reader = reader;
      this.collectionTypeClass = collectionTypeClass;
      this.objectMapper = objectMapper;
      this.errorMessage = errorMessage;
    }

    @Override
    public void onNext(Api.ReadResponse readResponse) {
      try {
        T doc = objectMapper.readValue(readResponse.getData().toStringUtf8(), collectionTypeClass);
        reader.onNext(doc);
      } catch (JsonProcessingException ex) {
        reader.onError(
            new TigrisException(
                "Failed to deserialize the read document to your model of type "
                    + collectionTypeClass.getName()
                    + ", please make sure your local schema generated models are in sync with server schema, please file a bug if your schemas are already in sync",
                ex));
      }
    }

    @Override
    public void onError(Throwable throwable) {
      reader.onError(new TigrisException(errorMessage, throwable));
    }

    @Override
    public void onCompleted() {
      reader.onCompleted();
    }
  }

  static class ReadSingleResponseObserverAdapter<T extends TigrisCollectionType>
      implements StreamObserver<Api.ReadResponse> {
    private final CompletableFuture<Optional<T>> completableFuture;
    private final Class<T> collectionTypeClass;
    private final ObjectMapper objectMapper;
    private final String errorMessage;

    public ReadSingleResponseObserverAdapter(
        CompletableFuture<Optional<T>> completableFuture,
        Class<T> collectionTypeClass,
        ObjectMapper objectMapper,
        String errorMessage) {
      this.completableFuture = completableFuture;
      this.collectionTypeClass = collectionTypeClass;
      this.objectMapper = objectMapper;
      this.errorMessage = errorMessage;
    }

    @Override
    public void onNext(Api.ReadResponse readResponse) {
      try {
        T doc = objectMapper.readValue(readResponse.getData().toStringUtf8(), collectionTypeClass);
        completableFuture.complete(Optional.of(doc));
      } catch (JsonProcessingException ex) {
        completableFuture.completeExceptionally(
            new TigrisException(
                "Failed to deserialize the read document to your model of type "
                    + collectionTypeClass.getName()
                    + ", please make sure your local schema generated models are in sync with server schema, please file a bug if your schemas are already in sync",
                ex));
      }
    }

    @Override
    public void onError(Throwable throwable) {
      completableFuture.completeExceptionally(new TigrisException(errorMessage, throwable));
    }

    @Override
    public void onCompleted() {
      // no op
    }
  }
}
