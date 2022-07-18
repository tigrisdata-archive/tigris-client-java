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
import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.ErrorInfo;
import com.google.rpc.Status;
import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.client.config.TigrisConfiguration;
import com.tigrisdata.db.client.error.TigrisError;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.search.SearchRequest;
import com.tigrisdata.db.client.search.SearchRequestOptions;
import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import org.junit.Assert;
import org.junit.Test;

public class TypeConverterTest {

  private static final String DB_NAME = "db1";
  private static final String COLLECTION_NAME = "coll1";
  private static final ObjectMapper DEFAULT_OBJECT_MAPPER =
      TigrisConfiguration.newBuilder("test").build().getObjectMapper();

  @Test
  public void apiDatabaseInfoToModelTest() {
    DatabaseInfo convertedDatabaseInfo1 =
        TypeConverter.toDatabaseInfo(Api.DatabaseInfo.newBuilder().setDb(DB_NAME).build());
    DatabaseInfo databaseInfo1 = new DatabaseInfo(DB_NAME);
    Assert.assertEquals(convertedDatabaseInfo1, databaseInfo1);
  }

  @Test
  public void apiCollectionInfoToModelTest() {
    CollectionInfo convertedCollectionInfo1 =
        TypeConverter.toCollectionInfo(
            Api.CollectionInfo.newBuilder().setCollection(COLLECTION_NAME).build());
    CollectionInfo collectionInfo1 = new CollectionInfo(COLLECTION_NAME);
    Assert.assertEquals(convertedCollectionInfo1, collectionInfo1);
  }

  @Test
  public void unreadableSchemaCreateCollectionRequestConversionTest() {
    try {
      TypeConverter.toCreateCollectionRequest(
          DB_NAME, new TigrisJSONSchema("invalid-schema"), CollectionOptions.DEFAULT_INSTANCE);
      Assert.fail("This must fail");
    } catch (TigrisException ignore) {
    }
  }

  @Test
  public void extractTigrisErrorTest() {
    Status status =
        Status.newBuilder()
            .setCode(Code.INTERNAL.getNumber())
            .setMessage("Test message")
            .addDetails(
                Any.pack(
                    ErrorInfo.newBuilder().setReason(Api.Code.DEADLINE_EXCEEDED.name()).build()))
            .build();

    StatusRuntimeException statusRuntimeException = StatusProto.toStatusRuntimeException(status);
    TigrisError tigrisError = TypeConverter.extractTigrisError(statusRuntimeException).get();
    Assert.assertEquals(Api.Code.DEADLINE_EXCEEDED, tigrisError.getCode());
  }

  @Test
  public void toSearchRequest() {
    SearchRequest input = SearchRequest.newBuilder().withQuery("search str").build();
    SearchRequestOptions options =
        SearchRequestOptions.newBuilder().withPage(8).withPerPage(30).build();
    Api.SearchRequest apiSearchRequest =
        TypeConverter.toSearchRequest(
            DB_NAME, COLLECTION_NAME, input, options, DEFAULT_OBJECT_MAPPER);

    Assert.assertEquals(input.getQuery().toJSON(DEFAULT_OBJECT_MAPPER), apiSearchRequest.getQ());
    Assert.assertNotNull(apiSearchRequest.getSearchFieldsList());
    Assert.assertNotNull(apiSearchRequest.getFacet());
    Assert.assertNotNull(apiSearchRequest.getFilter());
    Assert.assertNotNull(apiSearchRequest.getSort());
    Assert.assertNotNull(apiSearchRequest.getFields());
    Assert.assertEquals(options.getPage(), apiSearchRequest.getPage());
    Assert.assertEquals(options.getPerPage(), apiSearchRequest.getPageSize());
  }
}
