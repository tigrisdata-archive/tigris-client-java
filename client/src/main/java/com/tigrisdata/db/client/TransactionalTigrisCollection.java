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
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.type.TigrisCollectionType;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Transaction aware TigrisDB collection instance
 *
 * @param <T> type of the collection
 */
public class TransactionalTigrisCollection<T extends TigrisCollectionType>
    extends StandardTigrisCollection<T> implements TransactionTigrisCollection<T> {
  private final Api.TransactionCtx transactionCtx;

  TransactionalTigrisCollection(
      String databaseName,
      Class<T> collectionTypeClass,
      TigrisDBGrpc.TigrisDBBlockingStub stub,
      Api.TransactionCtx transactionCtx,
      ObjectMapper objectMapper) {
    super(databaseName, collectionTypeClass, stub, objectMapper);
    this.transactionCtx = transactionCtx;
  }

  @Override
  public Iterator<T> read(
      TigrisFilter filter, ReadFields fields, ReadRequestOptions readRequestOptions)
      throws TigrisDBException {
    if (readRequestOptions.getReadOptions() != null) {
      readRequestOptions
          .getReadOptions()
          .setTransactionCtx(TypeConverter.toTransactionCtx(transactionCtx));
    } else {
      readRequestOptions.setReadOptions(
          new ReadOptions(TypeConverter.toTransactionCtx(transactionCtx)));
    }
    return super.read(filter, ReadFields.empty(), readRequestOptions);
  }

  @Override
  public InsertResponse insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisDBException {
    if (insertRequestOptions.getWriteOptions() != null) {
      insertRequestOptions
          .getWriteOptions()
          .setTransactionCtx(TypeConverter.toTransactionCtx(transactionCtx));
    } else {
      insertRequestOptions.setWriteOptions(
          new WriteOptions(TypeConverter.toTransactionCtx(transactionCtx)));
    }
    return super.insert(documents, insertRequestOptions);
  }

  @Override
  public InsertOrReplaceResponse insertOrReplace(
      List<T> documents, InsertOrReplaceRequestOptions insertOrReplaceRequestOptions)
      throws TigrisDBException {
    if (insertOrReplaceRequestOptions.getWriteOptions() != null) {
      insertOrReplaceRequestOptions
          .getWriteOptions()
          .setTransactionCtx(TypeConverter.toTransactionCtx(transactionCtx));
    } else {
      insertOrReplaceRequestOptions.setWriteOptions(
          new WriteOptions(TypeConverter.toTransactionCtx(transactionCtx)));
    }
    return super.insertOrReplace(documents, insertOrReplaceRequestOptions);
  }

  @Override
  public InsertOrReplaceResponse insertOrReplace(List<T> documents) throws TigrisDBException {
    return insertOrReplace(documents, new InsertOrReplaceRequestOptions());
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisDBException {
    if (deleteRequestOptions.getWriteOptions() != null) {
      deleteRequestOptions
          .getWriteOptions()
          .setTransactionCtx(TypeConverter.toTransactionCtx(transactionCtx));
    } else {
      deleteRequestOptions.setWriteOptions(
          new WriteOptions(TypeConverter.toTransactionCtx(transactionCtx)));
    }
    return super.delete(filter, deleteRequestOptions);
  }

  @Override
  public Iterator<T> read(TigrisFilter filter, ReadFields fields) throws TigrisDBException {
    return read(
        filter,
        fields,
        new ReadRequestOptions(new ReadOptions(TypeConverter.toTransactionCtx(transactionCtx))));
  }

  @Override
  public Optional<T> readOne(TigrisFilter filter) throws TigrisDBException {
    Iterator<T> iterator = this.read(filter, ReadFields.empty());
    if (iterator.hasNext()) {
      return Optional.of(iterator.next());
    }
    return Optional.empty();
  }

  @Override
  public InsertResponse insert(List<T> documents) throws TigrisDBException {
    return insert(
        documents,
        new InsertRequestOptions(new WriteOptions(TypeConverter.toTransactionCtx(transactionCtx))));
  }

  @Override
  public InsertResponse insert(T document) throws TigrisDBException {
    return this.insert(Collections.singletonList(document));
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter) throws TigrisDBException {
    return delete(
        filter,
        new DeleteRequestOptions(new WriteOptions(TypeConverter.toTransactionCtx(transactionCtx))));
  }
}
