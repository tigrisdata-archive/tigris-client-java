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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import static com.tigrisdata.db.client.Constants.BEGIN_TRANSACTION_FAILED;
import static com.tigrisdata.db.client.Constants.CREATE_OR_UPDATE_COLLECTION_FAILED;
import static com.tigrisdata.db.client.Constants.DESCRIBE_DB_FAILED;
import static com.tigrisdata.db.client.Constants.DROP_COLLECTION_FAILED;
import static com.tigrisdata.db.client.Constants.LIST_COLLECTION_FAILED;
import static com.tigrisdata.db.client.Constants.TRANSACTION_FAILED;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisCollectionType;
import com.tigrisdata.tools.schema.core.ModelToJsonSchema;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** Tigris Database implementation */
class StandardTigrisDatabase extends AbstractTigrisDatabase implements TigrisDatabase {

  private final ManagedChannel managedChannel;
  private final ObjectMapper objectMapper;
  private final ModelToJsonSchema modelToJsonSchema;

  StandardTigrisDatabase(
      String dbName,
      TigrisGrpc.TigrisBlockingStub blockingStub,
      ManagedChannel managedChannel,
      ObjectMapper objectMapper,
      ModelToJsonSchema modelToJsonSchema,
      TigrisConfiguration configuration) {
    super(dbName, blockingStub, configuration);
    this.managedChannel = managedChannel;
    this.objectMapper = objectMapper;
    this.modelToJsonSchema = modelToJsonSchema;
  }

  @Override
  public List<CollectionInfo> listCollections() throws TigrisException {
    try {
      Api.ListCollectionsRequest listCollectionsRequest =
          Api.ListCollectionsRequest.newBuilder().setProject(db).build();
      Api.ListCollectionsResponse listCollectionsResponse =
          blockingStub.listCollections(listCollectionsRequest);
      return listCollectionsResponse.getCollectionsList().stream()
          .map(TypeConverter::toCollectionInfo)
          .collect(Collectors.toList());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          LIST_COLLECTION_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  @Override
  public <T extends TigrisCollectionType> DropCollectionResponse dropCollection(
      Class<T> collectionType) throws TigrisException {
    return this.dropCollection(Utilities.getCollectionName(collectionType));
  }

  @Override
  public <C extends TigrisCollectionType> TigrisCollection<C> getCollection(
      Class<C> documentCollectionTypeClass) {
    return new StandardTigrisCollection<>(
        db, documentCollectionTypeClass, blockingStub, objectMapper, configuration);
  }

  @Override
  public TransactionSession beginTransaction(TransactionOptions transactionOptions)
      throws TigrisException {
    try {
      Api.BeginTransactionRequest beginTransactionRequest =
          Api.BeginTransactionRequest.newBuilder()
              .setProject(db)
              .setOptions(Api.TransactionOptions.newBuilder().build())
              .build();
      AtomicReference<Metadata> headersCapturer = new AtomicReference<>();
      AtomicReference<Metadata> trailersCapturer = new AtomicReference<>();
      ClientInterceptor headerCapturerInterceptor =
          MetadataUtils.newCaptureMetadataInterceptor(headersCapturer, trailersCapturer);
      TigrisGrpc.TigrisBlockingStub interceptedStub =
          blockingStub.withInterceptors(headerCapturerInterceptor);
      Api.BeginTransactionResponse beginTransactionResponse =
          interceptedStub.beginTransaction(beginTransactionRequest);
      Api.TransactionCtx transactionCtx = beginTransactionResponse.getTxCtx();
      return new StandardTransactionSession(
          db,
          transactionCtx,
          managedChannel,
          configuration,
          TypeConverter.getCookie(headersCapturer.get()));
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          BEGIN_TRANSACTION_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  @Override
  public CreateOrUpdateCollectionsResponse createOrUpdateCollections(
      Class<? extends TigrisCollectionType>... collectionModelTypes) throws TigrisException {
    TransactionSession transactionSession = null;
    try {
      transactionSession = beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
      for (Class<? extends TigrisCollectionType> collectionModelType : collectionModelTypes) {
        TigrisSchema schema =
            new TigrisJSONSchema(modelToJsonSchema.toJsonSchema(collectionModelType).toString());
        this.createOrUpdateCollections(
            transactionSession, schema, CollectionOptions.DEFAULT_INSTANCE);
      }
      transactionSession.commit();
      // TODO: revisit the response
      return new CreateOrUpdateCollectionsResponse(
          "Collections created successfully", "Collections created successfully");
    } catch (StatusRuntimeException statusRuntimeException) {
      if (transactionSession != null) {
        transactionSession.rollback();
      }
      throw new TigrisException(
          CREATE_OR_UPDATE_COLLECTION_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    } catch (Exception ex) {
      if (transactionSession != null) {
        transactionSession.rollback();
      }
      throw new TigrisException(CREATE_OR_UPDATE_COLLECTION_FAILED, ex);
    }
  }

  @Override
  public CreateOrUpdateCollectionsResponse createOrUpdateCollections(
      String[] packagesToScan, Optional<Predicate<Class<? extends TigrisCollectionType>>> filter)
      throws TigrisException {
    return this.createOrUpdateCollections(
        Utilities.scanTigrisCollectionModels(packagesToScan, filter));
  }

  @Override
  public DatabaseDescription describe() throws TigrisException {
    try {
      Api.DescribeDatabaseResponse response =
          blockingStub.describeDatabase(
              Api.DescribeDatabaseRequest.newBuilder().setProject(db).build());
      return TypeConverter.toDatabaseDescription(response);
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          DESCRIBE_DB_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }

  @Override
  public String name() {
    return db;
  }

  @Override
  public String toString() {
    return "StandardTigrisDatabase{" + "db='" + db + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StandardTigrisDatabase that = (StandardTigrisDatabase) o;

    return Objects.equals(db, that.db);
  }

  @Override
  public int hashCode() {
    return db != null ? db.hashCode() : 0;
  }

  @Override
  public void transact(Consumer<TransactionSession> sessionConsumer) throws TigrisException {
    TransactionSession session = beginTransaction(TransactionOptions.DEFAULT_INSTANCE);
    try {
      sessionConsumer.accept(session);
      session.commit();
    } catch (StatusRuntimeException statusRuntimeException) {
      session.rollback();
      throw new TigrisException(
          TRANSACTION_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    } catch (Throwable ex) {
      session.rollback();
      throw new TigrisException(TRANSACTION_FAILED, ex);
    }
  }

  @Override
  public void dropAllCollections() throws TigrisException {
    for (CollectionInfo listCollection : listCollections()) {
      dropCollection(listCollection.getCollectionName());
    }
  }

  private DropCollectionResponse dropCollection(String collectionName) throws TigrisException {
    try {
      Api.DropCollectionRequest dropCollectionRequest =
          Api.DropCollectionRequest.newBuilder()
              .setProject(db)
              .setCollection(collectionName)
              .build();
      Api.DropCollectionResponse response = blockingStub.dropCollection(dropCollectionRequest);
      return new DropCollectionResponse(response.getStatus(), response.getMessage());
    } catch (StatusRuntimeException statusRuntimeException) {
      throw new TigrisException(
          DROP_COLLECTION_FAILED,
          TypeConverter.extractTigrisError(statusRuntimeException),
          statusRuntimeException);
    }
  }
}
