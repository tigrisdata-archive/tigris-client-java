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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.rpc.ErrorInfo;
import com.google.rpc.RetryInfo;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.ObservabilityOuterClass;
import com.tigrisdata.db.api.v1.grpc.TigrisGrpc;
import com.tigrisdata.db.client.error.TigrisError;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.search.SearchRequest;
import com.tigrisdata.db.client.search.SearchRequestOptions;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

final class TypeConverter {

  private TypeConverter() {}

  static final Metadata.Key<String> INBOUND_COOKIE_KEY =
      Metadata.Key.of("Set-Cookie", Metadata.ASCII_STRING_MARSHALLER);

  static final Metadata.Key<String> OUTBOUND_COOKIE_KEY =
      Metadata.Key.of("Cookie", Metadata.ASCII_STRING_MARSHALLER);

  public static Api.ListDatabasesRequest toListDatabasesRequest(DatabaseOptions databaseOptions) {
    return Api.ListDatabasesRequest.newBuilder().build();
  }

  public static Api.CreateDatabaseRequest toCreateDatabaseRequest(
      String databaseName, DatabaseOptions databaseOptions) {
    return Api.CreateDatabaseRequest.newBuilder()
        .setDb(databaseName)
        .setOptions(Api.DatabaseOptions.newBuilder().build())
        .build();
  }

  public static Api.DropDatabaseRequest toDropDatabaseRequest(
      String databaseName, DatabaseOptions databaseOptions) {
    return Api.DropDatabaseRequest.newBuilder()
        .setDb(databaseName)
        .setOptions(Api.DatabaseOptions.newBuilder().build())
        .build();
  }

  public static ServerMetadata toServerMetadata(
      ObservabilityOuterClass.GetInfoResponse apiGetInfoResponse) {
    return new ServerMetadata(apiGetInfoResponse.getServerVersion());
  }

  public static CollectionInfo toCollectionInfo(Api.CollectionInfo collectionInfo) {
    return new CollectionInfo(collectionInfo.getCollection());
  }

  public static DatabaseInfo toDatabaseInfo(Api.DatabaseInfo databaseInfo) {
    return new DatabaseInfo(databaseInfo.getDb());
  }

  public static Api.CreateOrUpdateCollectionRequest toCreateCollectionRequest(
      String databaseName, TigrisSchema schema, CollectionOptions collectionOptions)
      throws TigrisException {
    try {
      return Api.CreateOrUpdateCollectionRequest.newBuilder()
          .setDb(databaseName)
          .setCollection(schema.getName())
          .setSchema(ByteString.copyFromUtf8(schema.getSchemaContent()))
          .setOptions(toCollectionOptions(collectionOptions))
          .build();
    } catch (IOException ioException) {
      throw new TigrisException("Failed to read schema content", ioException);
    }
  }

  static String getCookie(Metadata metadata) {
    final StringBuilder cookie = new StringBuilder();
    if (metadata != null) {
      Iterable<String> inboundCookies = metadata.getAll(TypeConverter.INBOUND_COOKIE_KEY);
      if (inboundCookies != null) {
        inboundCookies.forEach(inboundCookie -> cookie.append(";").append(inboundCookie));
      }
    }
    return cookie.toString();
  }

  static TigrisGrpc.TigrisBlockingStub transactionAwareStub(
      TigrisGrpc.TigrisBlockingStub blockingStub,
      StandardTransactionSession standardTransactionSession) {
    // prepare headers
    Metadata transactionHeaders = new Metadata();
    transactionHeaders.put(
        Metadata.Key.of(Constants.TRANSACTION_HEADER_ORIGIN_KEY, Metadata.ASCII_STRING_MARSHALLER),
        standardTransactionSession.getTransactionCtx().getOrigin());
    transactionHeaders.put(
        Metadata.Key.of(Constants.TRANSACTION_HEADER_ID_KEY, Metadata.ASCII_STRING_MARSHALLER),
        standardTransactionSession.getTransactionCtx().getId());
    if (standardTransactionSession.getCookie() != null
        || !standardTransactionSession.getCookie().isEmpty()) {
      transactionHeaders.put(OUTBOUND_COOKIE_KEY, standardTransactionSession.getCookie());
    }
    // attach headers
    return blockingStub.withInterceptors(
        MetadataUtils.newAttachHeadersInterceptor(transactionHeaders));
  }

  public static ReadRequestOptions readOneDefaultReadRequestOptions() {
    ReadRequestOptions readRequestOptions = new ReadRequestOptions();
    readRequestOptions.setLimit(1L);
    readRequestOptions.setSkip(0L);
    return readRequestOptions;
  }

  public static Api.DropCollectionRequest toDropCollectionRequest(
      String databaseName, String collectionName, CollectionOptions collectionOptions) {
    return Api.DropCollectionRequest.newBuilder()
        .setDb(databaseName)
        .setCollection(collectionName)
        .setOptions(toCollectionOptions(collectionOptions))
        .build();
  }

  public static Api.BeginTransactionRequest toBeginTransactionRequest(
      String databaseName, TransactionOptions transactionOptions) {
    return Api.BeginTransactionRequest.newBuilder()
        .setDb(databaseName)
        .setOptions(Api.TransactionOptions.newBuilder().build())
        .build();
  }

  public static Api.ReadRequest toReadRequest(
      String databaseName,
      String collectionName,
      TigrisFilter filter,
      ReadFields fields,
      ReadRequestOptions readRequestOptions,
      ObjectMapper objectMapper) {
    Api.ReadRequestOptions readRequestOptionsAPI =
        Api.ReadRequestOptions.newBuilder()
            .setLimit(readRequestOptions.getLimit())
            .setSkip(readRequestOptions.getSkip())
            .build();

    Api.ReadRequest.Builder readRequestBuilder =
        Api.ReadRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setFilter(ByteString.copyFromUtf8(filter.toJSON(objectMapper)))
            .setOptions(readRequestOptionsAPI);
    if (!fields.isEmpty()) {
      readRequestBuilder.setFields(ByteString.copyFromUtf8(fields.toJSON(objectMapper)));
    }
    return readRequestBuilder.build();
  }

  public static Api.SearchRequest toSearchRequest(
      String databaseName,
      String collectionName,
      SearchRequest req,
      SearchRequestOptions options,
      ObjectMapper objectMapper) {
    Api.SearchRequest.Builder builder =
        Api.SearchRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setQ(req.getQuery().toJSON(objectMapper))
            .addAllIncludeFields(req.getIncludeFields())
            .addAllExcludeFields(req.getExcludeFields());
    if (Objects.nonNull(req.getSearchFields())) {
      builder.addAllSearchFields(req.getSearchFields().getFields());
    }
    if (Objects.nonNull(req.getFilter())) {
      builder.setFilter(ByteString.copyFromUtf8(req.getFilter().toJSON(objectMapper)));
    }
    if (Objects.nonNull(req.getFacetQuery())) {
      builder.setFacet(ByteString.copyFromUtf8(req.getFacetQuery().toJSON(objectMapper)));
    }
    if (Objects.nonNull(req.getSortingOrder())) {
      builder.setSort(ByteString.copyFromUtf8(req.getSortingOrder().toJSON(objectMapper)));
    }
    if (Objects.nonNull(options)) {
      builder.setPage(options.getPage());
      builder.setPageSize(options.getPerPage());
    }
    return builder.build();
  }

  public static <T> Api.InsertRequest toInsertRequest(
      String databaseName,
      String collectionName,
      List<T> documents,
      InsertRequestOptions insertRequestOptions,
      ObjectMapper objectMapper)
      throws JsonProcessingException {
    Api.InsertRequest.Builder insertRequestBuilder =
        Api.InsertRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(
                Api.InsertRequestOptions.newBuilder()
                    .setWriteOptions(toWriteOptions(insertRequestOptions.getWriteOptions()))
                    .build());
    for (T document : documents) {
      insertRequestBuilder.addDocuments(
          ByteString.copyFromUtf8(objectMapper.writeValueAsString(document)));
    }
    return insertRequestBuilder.build();
  }

  public static Map<String, Object>[] toArrayOfMap(
      List<ByteString> keys, ObjectMapper objectMapper) {
    try {
      Map<String, Object>[] result = new TreeMap[keys.size()];
      int i = 0;
      for (ByteString key : keys) {
        JsonNode node = objectMapper.readTree(key.toStringUtf8());
        Iterator<Map.Entry<String, JsonNode>> itr = node.fields();
        result[i] = new TreeMap<>();
        while (itr.hasNext()) {
          Map.Entry<String, JsonNode> entry = itr.next();
          if (entry.getValue().isInt()) {
            result[i].put(entry.getKey(), entry.getValue().intValue());
          } else if (entry.getValue().isLong()) {
            result[i].put(entry.getKey(), entry.getValue().longValue());
          } else if (entry.getValue().isTextual()) {
            try {
              result[i].put(entry.getKey(), UUID.fromString(entry.getValue().asText()));
            } catch (IllegalArgumentException ignore) {
              result[i].put(entry.getKey(), entry.getValue().asText());
            }
          }
        }
        i++;
      }
      return result;
    } catch (JsonProcessingException jex) {
      throw new IllegalArgumentException(jex);
    }
  }

  public static <T> Api.ReplaceRequest toReplaceRequest(
      String databaseName,
      String collectionName,
      List<T> documents,
      InsertOrReplaceRequestOptions insertOrReplaceRequestOptions,
      ObjectMapper objectMapper)
      throws JsonProcessingException {
    Api.ReplaceRequest.Builder replaceRequestBuilder =
        Api.ReplaceRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(
                Api.ReplaceRequestOptions.newBuilder()
                    .setWriteOptions(
                        toWriteOptions(insertOrReplaceRequestOptions.getWriteOptions()))
                    .build());
    for (T document : documents) {
      replaceRequestBuilder.addDocuments(
          ByteString.copyFromUtf8(objectMapper.writeValueAsString(document)));
    }
    return replaceRequestBuilder.build();
  }

  public static Api.UpdateRequest toUpdateRequest(
      String databaseName,
      String collectionName,
      TigrisFilter filter,
      UpdateFields updateFields,
      UpdateRequestOptions updateRequestOptions,
      ObjectMapper objectMapper) {

    return Api.UpdateRequest.newBuilder()
        .setDb(databaseName)
        .setCollection(collectionName)
        .setFilter(ByteString.copyFromUtf8(filter.toJSON(objectMapper)))
        .setFields(ByteString.copyFromUtf8(updateFields.toJSON(objectMapper)))
        .setOptions(
            Api.UpdateRequestOptions.newBuilder()
                .setWriteOptions(toWriteOptions(updateRequestOptions.getWriteOptions()))
                .build())
        .build();
  }

  public static Api.DeleteRequest toDeleteRequest(
      String databaseName,
      String collectionName,
      TigrisFilter filter,
      DeleteRequestOptions deleteRequestOptions,
      ObjectMapper objectMapper) {
    return Api.DeleteRequest.newBuilder()
        .setDb(databaseName)
        .setCollection(collectionName)
        .setFilter(ByteString.copyFromUtf8(filter.toJSON(objectMapper)))
        .setOptions(
            Api.DeleteRequestOptions.newBuilder()
                .setWriteOptions(toWriteOptions(deleteRequestOptions.getWriteOptions()))
                .build())
        .build();
  }

  public static DatabaseDescription toDatabaseDescription(Api.DescribeDatabaseResponse response)
      throws TigrisException {
    List<CollectionDescription> collectionsDescription = new ArrayList<>();
    for (Api.CollectionDescription collectionDescription : response.getCollectionsList()) {
      collectionsDescription.add(toCollectionDescription(collectionDescription));
    }

    DatabaseMetadata metadata = toDatabaseMetadata(response.getMetadata());
    return new DatabaseDescription(response.getDb(), metadata, collectionsDescription);
  }

  public static Api.CollectionOptions toCollectionOptions(CollectionOptions collectionOptions) {
    Api.CollectionOptions.Builder collectionsOptionBuilder = Api.CollectionOptions.newBuilder();
    return collectionsOptionBuilder.build();
  }

  private static Api.WriteOptions toWriteOptions(WriteOptions writeOptions) {
    Api.WriteOptions.Builder writeOptionsBuilder = Api.WriteOptions.newBuilder();
    return writeOptionsBuilder.build();
  }

  public static CollectionDescription toCollectionDescription(
      Api.DescribeCollectionResponse response) throws TigrisException {
    CollectionMetadata collectionMetadata = toCollectionMetadata(response.getMetadata());
    return new CollectionDescription(
        response.getCollection(),
        collectionMetadata,
        new TigrisJSONSchema(response.getSchema().toStringUtf8()));
  }

  public static CollectionDescription toCollectionDescription(
      Api.CollectionDescription collectionDescription) throws TigrisException {
    CollectionMetadata collectionMetadata =
        toCollectionMetadata(collectionDescription.getMetadata());
    return new CollectionDescription(
        collectionDescription.getCollection(),
        collectionMetadata,
        new TigrisJSONSchema(collectionDescription.getSchema().toStringUtf8()));
  }

  static Optional<TigrisError> extractTigrisError(StatusRuntimeException statusRuntimeException) {
    Optional<ErrorInfo> errorInfo = extract(statusRuntimeException, ErrorInfo.class);
    return errorInfo.map(
        info -> new TigrisError(ObservabilityOuterClass.Code.valueOf(info.getReason())));
  }

  static Optional<RetryInfo> extractRetryInfo(StatusRuntimeException statusRuntimeException) {
    return extract(statusRuntimeException, RetryInfo.class);
  }

  private static <T> Optional<T> extract(
      StatusRuntimeException statusRuntimeException, Class clazz) {
    com.google.rpc.Status status =
        io.grpc.protobuf.StatusProto.fromThrowable(statusRuntimeException);
    for (Any any : status.getDetailsList()) {
      if (any.is(clazz)) {
        try {
          return (Optional<T>) Optional.of(any.unpack(clazz));
        } catch (InvalidProtocolBufferException ignore) {
        }
      }
    }
    return Optional.empty();
  }

  private static DatabaseMetadata toDatabaseMetadata(Api.DatabaseMetadata databaseMetadata) {
    // empty metadata for now
    return new DatabaseMetadata();
  }

  private static CollectionMetadata toCollectionMetadata(
      Api.CollectionMetadata collectionMetadata) {
    // empty metadata for now
    return new CollectionMetadata();
  }

  public static <T> Api.PublishRequest toPublishRequest(
      String databaseName, String collectionName, List<T> messages, ObjectMapper objectMapper)
      throws JsonProcessingException {
    Api.PublishRequest.Builder publishRequestBuilder =
        Api.PublishRequest.newBuilder().setDb(databaseName).setCollection(collectionName);
    for (T message : messages) {
      publishRequestBuilder.addMessages(
          ByteString.copyFromUtf8(objectMapper.writeValueAsString(message)));
    }
    return publishRequestBuilder.build();
  }

  public static Api.SubscribeRequest toSubscribeRequest(
      String databaseName, String collectionName) {
    Api.SubscribeRequestOptions subscribeRequestOptionsAPI =
        Api.SubscribeRequestOptions.newBuilder().build();
    Api.SubscribeRequest.Builder subscribeRequestBuilder =
        Api.SubscribeRequest.newBuilder()
            .setDb(databaseName)
            .setCollection(collectionName)
            .setOptions(subscribeRequestOptionsAPI);
    return subscribeRequestBuilder.build();
  }
}
