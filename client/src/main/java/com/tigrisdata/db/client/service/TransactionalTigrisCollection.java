package com.tigrisdata.db.client.service;

import com.tigrisdata.db.api.v1.grpc.Api;
import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.Field;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.ReadOptions;
import com.tigrisdata.db.client.model.ReadRequestOptions;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisFilter;
import com.tigrisdata.db.client.model.WriteOptions;

import java.util.Iterator;
import java.util.List;

public class TransactionalTigrisCollection<T extends TigrisCollectionType>
    extends StandardTigrisCollection<T> implements TransactionTigrisCollection<T> {
  private final Api.TransactionCtx transactionCtx;

  TransactionalTigrisCollection(
      String databaseName,
      Class<T> collectionTypeClass,
      TigrisDBGrpc.TigrisDBBlockingStub stub,
      Api.TransactionCtx transactionCtx) {
    super(databaseName, collectionTypeClass, stub);
    this.transactionCtx = transactionCtx;
  }

  @Override
  public Iterator<T> read(
      TigrisFilter filter, List<Field<?>> fields, ReadRequestOptions readRequestOptions)
      throws TigrisDBException {
    if (readRequestOptions.getReadOptions() != null) {
      readRequestOptions.getReadOptions().setTransactionCtx(transactionCtx);
    } else {
      readRequestOptions.setReadOptions(new ReadOptions(transactionCtx));
    }
    return super.read(filter, fields, readRequestOptions);
  }

  @Override
  public InsertResponse insert(List<T> documents, InsertRequestOptions insertRequestOptions)
      throws TigrisDBException {
    if (insertRequestOptions.getWriteOptions() != null) {
      insertRequestOptions.getWriteOptions().setTransactionCtx(transactionCtx);
    } else {
      insertRequestOptions.setWriteOptions(new WriteOptions(transactionCtx));
    }
    return super.insert(documents, insertRequestOptions);
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter, DeleteRequestOptions deleteRequestOptions)
      throws TigrisDBException {
    if (deleteRequestOptions.getWriteOptions() != null) {
      deleteRequestOptions.getWriteOptions().setTransactionCtx(transactionCtx);
    } else {
      deleteRequestOptions.setWriteOptions(new WriteOptions(transactionCtx));
    }
    return super.delete(filter, deleteRequestOptions);
  }

  @Override
  public Iterator<T> read(TigrisFilter filter, List<Field<?>> fields) throws TigrisDBException {
    return read(filter, fields, new ReadRequestOptions(new ReadOptions(transactionCtx)));
  }

  @Override
  public InsertResponse insert(List<T> documents) throws TigrisDBException {
    return insert(documents, new InsertRequestOptions(new WriteOptions(transactionCtx)));
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter) throws TigrisDBException {
    return delete(filter, new DeleteRequestOptions(new WriteOptions(transactionCtx)));
  }
}
