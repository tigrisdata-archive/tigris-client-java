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
package com.tigrisdata.db.client.grpc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TestUserService extends TigrisDBGrpc.TigrisDBImplBase {

  private List<String> dbs;
  private Map<String, List<String>> dbToCollectionsMap;
  private Map<String, List<JsonObject>> collectionToDocumentsMap;
  private String txId;
  private String txOrigin;

  public TestUserService() {
    this.reset();
  }

  public void reset() {
    // default dbs
    this.dbs =
        new ArrayList<String>() {
          {
            add("db1");
            add("db2");
            add("db3");
          }
        };

    this.dbToCollectionsMap = new HashMap<>();
    this.collectionToDocumentsMap = new HashMap<>();
    for (String db : dbs) {
      List<String> collections = new ArrayList<>();
      for (int i = 0; i < 5; i++) {
        collections.add(db + "_c" + i);
      }
      dbToCollectionsMap.put(db, collections);
    }

    for (String db : dbs) {
      for (String collection : dbToCollectionsMap.get(db)) {
        List<JsonObject> documents = collectionToDocumentsMap.get(collection);
        if (documents == null) {
          documents = new ArrayList<>();
          collectionToDocumentsMap.put(collection, documents);
        }
        for (int i = 0; i < 5; i++) {
          documents.add(getDocument(i, collection));
        }
      }
    }
  }

  @Override
  public void beginTransaction(
      Api.BeginTransactionRequest request,
      StreamObserver<Api.BeginTransactionResponse> responseObserver) {
    this.txId = UUID.randomUUID().toString();
    this.txOrigin = txId + "_origin";
    responseObserver.onNext(
        Api.BeginTransactionResponse.newBuilder()
            .setTxCtx(Api.TransactionCtx.newBuilder().setId(txId).setOrigin(txOrigin).build())
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void commitTransaction(
      Api.CommitTransactionRequest request,
      StreamObserver<Api.CommitTransactionResponse> responseObserver) {
    if (!request.getTxCtx().getId().equals(txId)
        || !request.getTxCtx().getOrigin().equals(txOrigin)) {
      responseObserver.onError(new IllegalArgumentException("Unexpected transaction"));
      responseObserver.onCompleted();
    }
    resetTx();
    responseObserver.onNext(Api.CommitTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void rollbackTransaction(
      Api.RollbackTransactionRequest request,
      StreamObserver<Api.RollbackTransactionResponse> responseObserver) {
    if (!request.getTxCtx().getId().equals(txId)
        || !request.getTxCtx().getOrigin().equals(txOrigin)) {
      responseObserver.onError(new IllegalArgumentException("Unexpected transaction"));
      responseObserver.onCompleted();
    }
    resetTx();
    responseObserver.onNext(Api.RollbackTransactionResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void insert(
      Api.InsertRequest request, StreamObserver<Api.InsertResponse> responseObserver) {
    if (dbToCollectionsMap.get(request.getDb()).contains(request.getCollection())) {
      for (ByteString bytes : request.getDocumentsList()) {
        collectionToDocumentsMap
            .get(request.getCollection())
            .add(JsonParser.parseString(bytes.toStringUtf8()).getAsJsonObject());
      }
    }
    responseObserver.onNext(Api.InsertResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void replace(
      Api.ReplaceRequest request, StreamObserver<Api.ReplaceResponse> responseObserver) {
    if (dbToCollectionsMap.get(request.getDb()).contains(request.getCollection())) {
      for (ByteString docBytes : request.getDocumentsList()) {
        JsonObject doc = JsonParser.parseString(docBytes.toStringUtf8()).getAsJsonObject();
        boolean matched = false;
        for (JsonObject jsonObject : collectionToDocumentsMap.get(request.getCollection())) {
          if (jsonObject.get("id").getAsLong() == doc.get("id").getAsLong()) {
            // update name
            matched = true;
            jsonObject.remove("name");
            jsonObject.addProperty("name", doc.get("name").getAsString());
            break;
          }
        }
        if (!matched) {
          // if not exist add it
          collectionToDocumentsMap.get(request.getCollection()).add(doc);
        }
      }
    }
    responseObserver.onNext(Api.ReplaceResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void delete(
      Api.DeleteRequest request, StreamObserver<Api.DeleteResponse> responseObserver) {
    JsonObject filterJsonObject =
        JsonParser.parseString(request.getFilter().toStringUtf8()).getAsJsonObject();
    /*
     Filter shape
     {"id":1}
    */
    if (dbToCollectionsMap.get(request.getDb()).contains(request.getCollection())) {
      List<JsonObject> newDocs = new ArrayList<>();
      for (JsonObject document : collectionToDocumentsMap.get(request.getCollection())) {
        // delete doc matching the ID, by adding all but the matching
        if (document.get("id").getAsLong() != filterJsonObject.get("id").getAsLong()) {
          newDocs.add(document);
        }
      }
      collectionToDocumentsMap.put(request.getCollection(), newDocs);
    }
    responseObserver.onNext(Api.DeleteResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void update(
      Api.UpdateRequest request, StreamObserver<Api.UpdateResponse> responseObserver) {
    /*
     Filter shape
     {"id":1}
    */

    JsonObject filterJsonObject =
        JsonParser.parseString(request.getFilter().toStringUtf8()).getAsJsonObject();
    /*
    Field shape
    {
      "$set":{
        "name":"new_name"
       }
     }
     */
    JsonObject fieldJsonObject =
        JsonParser.parseString(request.getFields().toStringUtf8()).getAsJsonObject();

    if (dbToCollectionsMap.get(request.getDb()).contains(request.getCollection())) {
      for (JsonObject document : collectionToDocumentsMap.get(request.getCollection())) {
        // delete doc matching the ID, by adding all but the matching
        if (document.get("id").getAsLong() == filterJsonObject.get("id").getAsLong()) {
          document.remove("name");
          document.addProperty(
              "name",
              fieldJsonObject.getAsJsonObject("$set").getAsJsonPrimitive("name").getAsString());
        }
      }
    }
    responseObserver.onNext(Api.UpdateResponse.newBuilder().build());
    responseObserver.onCompleted();
  }

  @Override
  public void read(Api.ReadRequest request, StreamObserver<Api.ReadResponse> responseObserver) {
    /*
     Filter shape
     {"id":1}
    */

    JsonObject filterJsonObject =
        JsonParser.parseString(request.getFilter().toStringUtf8()).getAsJsonObject();
    // for test assume there is only one key
    String filterKey = filterJsonObject.keySet().stream().findAny().get();
    if (dbToCollectionsMap.get(request.getDb()).contains(request.getCollection())) {
      for (JsonObject jsonObject : collectionToDocumentsMap.get(request.getCollection())) {
        // if field exists in the doc, then filter
        if (jsonObject.keySet().contains(filterKey)) {
          if (filterJsonObject.get(filterKey).equals(jsonObject.get(filterKey))) {
            responseObserver.onNext(
                Api.ReadResponse.newBuilder()
                    .setDoc(ByteString.copyFromUtf8(jsonObject.toString()))
                    .build());
          }
        } else {
          // if the key is not present then allow for "test" purpose.
          responseObserver.onNext(
              Api.ReadResponse.newBuilder()
                  .setDoc(ByteString.copyFromUtf8(jsonObject.toString()))
                  .build());
        }
      }
    }
    responseObserver.onCompleted();
  }

  @Override
  public void createOrUpdateCollection(
      Api.CreateOrUpdateCollectionRequest request,
      StreamObserver<Api.CreateOrUpdateCollectionResponse> responseObserver) {
    dbToCollectionsMap.get(request.getDb()).add(request.getCollection());
    responseObserver.onNext(
        Api.CreateOrUpdateCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " created")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void dropCollection(
      Api.DropCollectionRequest request,
      StreamObserver<Api.DropCollectionResponse> responseObserver) {
    dbToCollectionsMap.get(request.getDb()).remove(request.getCollection());
    responseObserver.onNext(
        Api.DropCollectionResponse.newBuilder()
            .setMsg(request.getCollection() + " dropped")
            .build());
    responseObserver.onCompleted();
  }

  @Override
  public void listDatabases(
      Api.ListDatabasesRequest request,
      StreamObserver<Api.ListDatabasesResponse> responseObserver) {
    Api.ListDatabasesResponse.Builder listDatabasesResponseBuilder =
        Api.ListDatabasesResponse.newBuilder();
    for (String db : dbs) {
      listDatabasesResponseBuilder.addDatabases(Api.DatabaseInfo.newBuilder().setName(db).build());
    }
    responseObserver.onNext(listDatabasesResponseBuilder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void listCollections(
      Api.ListCollectionsRequest request,
      StreamObserver<Api.ListCollectionsResponse> responseObserver) {
    Api.ListCollectionsResponse.Builder builder = Api.ListCollectionsResponse.newBuilder();
    for (String collectionName : dbToCollectionsMap.get(request.getDb())) {
      builder.addCollections(Api.CollectionInfo.newBuilder().setName(collectionName).build());
    }
    responseObserver.onNext(builder.build());
    responseObserver.onCompleted();
  }

  @Override
  public void createDatabase(
      Api.CreateDatabaseRequest request,
      StreamObserver<Api.CreateDatabaseResponse> responseObserver) {
    this.dbs.add(request.getDb());
    responseObserver.onNext(
        Api.CreateDatabaseResponse.newBuilder().setMsg(request.getDb() + " created").build());
    responseObserver.onCompleted();
  }

  @Override
  public void dropDatabase(
      Api.DropDatabaseRequest request, StreamObserver<Api.DropDatabaseResponse> responseObserver) {
    this.dbs.remove(request.getDb());
    responseObserver.onNext(
        Api.DropDatabaseResponse.newBuilder().setMsg(request.getDb() + " dropped").build());
    responseObserver.onCompleted();
  }

  private void resetTx() {
    this.txOrigin = "";
    this.txId = "";
  }

  private static JsonObject getDocument(int index, String collectionName) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("id", index);
    jsonObject.addProperty("name", collectionName + "_d" + index);
    return jsonObject;
  }
}
