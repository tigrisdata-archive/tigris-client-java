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
package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.InsertOrReplaceRequestOptions;
import com.tigrisdata.db.client.model.InsertOrReplaceResponse;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.ReadFields;
import com.tigrisdata.db.client.model.ReadRequestOptions;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisFilter;
import com.tigrisdata.db.client.model.UpdateFields;
import com.tigrisdata.db.client.model.UpdateRequestOptions;
import com.tigrisdata.db.client.model.UpdateResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class StandardTigrisAsyncCollection<T extends TigrisCollectionType>
    implements TigrisAsyncCollection<T> {
  private final String collectionName;

  public StandardTigrisAsyncCollection(String collectionName) {
    this.collectionName = collectionName;
  }

  @Override
  public void read(
      TigrisFilter filter,
      ReadFields fields,
      ReadRequestOptions readRequestOptions,
      TigrisDBAsyncReader<T> reader)
      throws TigrisDBException {}

  @Override
  public void read(TigrisFilter filter, ReadFields fields, TigrisDBAsyncReader<T> reader)
      throws TigrisDBException {}

  @Override
  public CompletableFuture<T> readOne(TigrisFilter filter) throws TigrisDBException {
    return null;
  }

  @Override
  public CompletableFuture<InsertResponse> insert(
      List<T> documents, InsertRequestOptions insertRequestOptions) throws TigrisDBException {
    return null;
  }

  @Override
  public CompletableFuture<InsertResponse> insert(List<T> documents) throws TigrisDBException {
    return null;
  }

  @Override
  public CompletableFuture<InsertResponse> insert(T document) throws TigrisDBException {
    return null;
  }

  @Override
  public CompletableFuture<InsertOrReplaceResponse> insertOrReplace(
      List<T> documents, InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisDBException {
    return null;
  }

  @Override
  public CompletableFuture<InsertOrReplaceResponse> insertOrReplace(List<T> documents)
      throws TigrisDBException {
    return null;
  }

  @Override
  public CompletableFuture<UpdateResponse> update(
      TigrisFilter filter, UpdateFields fields, UpdateRequestOptions updateRequestOptions)
      throws TigrisDBException {
    return null;
  }

  @Override
  public CompletableFuture<UpdateResponse> update(TigrisFilter filter, UpdateFields fields)
      throws TigrisDBException {
    return null;
  }

  @Override
  public CompletableFuture<DeleteResponse> delete(
      TigrisFilter filter, DeleteRequestOptions deleteRequestOptions) throws TigrisDBException {
    return null;
  }

  @Override
  public CompletableFuture<DeleteResponse> delete(TigrisFilter filter) throws TigrisDBException {
    return null;
  }

  @Override
  public String name() {
    return collectionName;
  }
}
