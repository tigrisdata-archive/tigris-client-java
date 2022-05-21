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

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.api.v1.grpc.Api;

public class StreamEvent {
  private byte[] txId;
  private String collection;
  private String op;
  private byte[] key;
  private byte[] lKey;
  private byte[] rKey;
  private JsonNode data;
  private boolean last;

  public static StreamEvent from(Api.StreamEvent apiEvent, ObjectMapper objectMapper)
      throws IOException {
    StreamEvent event = new StreamEvent();
    event.setTxId(apiEvent.getTxId().toByteArray());
    event.setCollection(apiEvent.getCollection());
    event.setOp(apiEvent.getOp());
    event.setKey(apiEvent.getKey().toByteArray());
    event.setLKey(apiEvent.getLkey().toByteArray());
    event.setRKey(apiEvent.getRkey().toByteArray());
    event.setData(objectMapper.readTree(apiEvent.getData().toByteArray()));
    event.setLast(apiEvent.getLast());
    return event;
  }

  public byte[] getTxId() {
    return txId;
  }

  public void setTxId(byte[] txId) {
    this.txId = txId;
  }

  public String getCollection() {
    return collection;
  }

  public void setCollection(String collection) {
    this.collection = collection;
  }

  public String getOp() {
    return op;
  }

  public void setOp(String op) {
    this.op = op;
  }

  public byte[] getKey() {
    return key;
  }

  public void setKey(byte[] key) {
    this.key = key;
  }

  public byte[] getLKey() {
    return lKey;
  }

  public void setLKey(byte[] lKey) {
    this.lKey = lKey;
  }

  public byte[] getRKey() {
    return rKey;
  }

  public void setRKey(byte[] rKey) {
    this.rKey = rKey;
  }

  public JsonNode getData() {
    return data;
  }

  public void setData(JsonNode data) {
    this.data = data;
  }

  public boolean isLast() {
    return last;
  }

  public void setLast(boolean last) {
    this.last = last;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", StreamEvent.class.getSimpleName() + "[", "]")
        .add("txId=" + Arrays.toString(txId))
        .add("collection='" + collection + "'")
        .add("op='" + op + "'")
        .add("key=" + Arrays.toString(key))
        .add("lKey=" + Arrays.toString(lKey))
        .add("rKey=" + Arrays.toString(rKey))
        .add("data=" + data)
        .add("last=" + last)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StreamEvent that = (StreamEvent) o;
    return last == that.last
        && Arrays.equals(txId, that.txId)
        && Objects.equals(collection, that.collection)
        && Objects.equals(op, that.op)
        && Arrays.equals(key, that.key)
        && Arrays.equals(lKey, that.lKey)
        && Arrays.equals(rKey, that.rKey)
        && Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(collection, op, data, last);
    result = 31 * result + Arrays.hashCode(txId);
    result = 31 * result + Arrays.hashCode(key);
    result = 31 * result + Arrays.hashCode(lKey);
    result = 31 * result + Arrays.hashCode(rKey);
    return result;
  }
}
