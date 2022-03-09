package com.tigrisdata.db.client.service;

import com.tigrisdata.db.api.v1.grpc.TigrisDBGrpc;
import com.tigrisdata.db.api.v1.grpc.User;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.client.model.DeleteRequestOptions;
import com.tigrisdata.db.client.model.DeleteResponse;
import com.tigrisdata.db.client.model.InsertRequestOptions;
import com.tigrisdata.db.client.model.InsertResponse;
import com.tigrisdata.db.client.model.ReadOptions;
import com.tigrisdata.db.client.model.ReadRequestOptions;
import com.tigrisdata.db.client.model.ReplaceRequestOptions;
import com.tigrisdata.db.client.model.ReplaceResponse;
import com.tigrisdata.db.client.model.TigrisCollectionType;
import com.tigrisdata.db.client.model.TigrisFilter;
import com.tigrisdata.db.client.model.WriteOptions;

import java.util.Iterator;
import java.util.List;

public class StandardTransactionTigrisCollection<T extends TigrisCollectionType>
    extends StandardTigrisCollection<T> implements TransactionTigrisCollection<T> {
  private final User.TransactionCtx transactionCtx;

  public StandardTransactionTigrisCollection(
      String databaseName,
      Class<T> collectionTypeClass,
      TigrisDBGrpc.TigrisDBBlockingStub stub,
      User.TransactionCtx transactionCtx) {
    super(databaseName, collectionTypeClass, stub);
    this.transactionCtx = transactionCtx;
  }

  @Override
  public Iterator<T> read(TigrisFilter filter, ReadRequestOptions readRequestOptions)
      throws TigrisDBException {
    if (readRequestOptions.getReadOptions() != null) {
      readRequestOptions.getReadOptions().setTransactionCtx(transactionCtx);
    } else {
      readRequestOptions.setReadOptions(new ReadOptions(transactionCtx));
    }
    return super.read(filter, readRequestOptions);
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
  public ReplaceResponse replace(List<T> documents, ReplaceRequestOptions replaceRequestOptions)
      throws TigrisDBException {
    if (replaceRequestOptions.getWriteOptions() != null) {
      replaceRequestOptions.getWriteOptions().setTransactionCtx(transactionCtx);
    } else {
      replaceRequestOptions.setWriteOptions(new WriteOptions(transactionCtx));
    }
    return super.replace(documents, replaceRequestOptions);
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
  public Iterator<T> read(TigrisFilter filter) throws TigrisDBException {
    return this.read(filter, new ReadRequestOptions(new ReadOptions(transactionCtx)));
  }

  @Override
  public InsertResponse insert(List<T> documents) throws TigrisDBException {
    return this.insert(documents, new InsertRequestOptions(new WriteOptions(transactionCtx)));
  }

  @Override
  public ReplaceResponse replace(List<T> documents) throws TigrisDBException {
    return this.replace(documents, new ReplaceRequestOptions(new WriteOptions(transactionCtx)));
  }

  @Override
  public DeleteResponse delete(TigrisFilter filter) throws TigrisDBException {
    return this.delete(filter, new DeleteRequestOptions(new WriteOptions(transactionCtx)));
  }
}
