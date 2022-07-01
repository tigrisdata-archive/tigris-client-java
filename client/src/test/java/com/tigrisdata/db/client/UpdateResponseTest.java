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

import com.google.protobuf.Timestamp;
import java.time.Instant;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class UpdateResponseTest {
  @Test
  public void equalsTest() {
    final String ok = "ok";
    Timestamp createdAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    Timestamp updatedAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    UpdateResponse ob1 = new UpdateResponse(ok, createdAt, updatedAt, 1);
    UpdateResponse ob2 = new UpdateResponse(ok, createdAt, updatedAt, 1);
    Assert.assertEquals(ob1, ob1);
    Assert.assertEquals(ob1, ob2);
    Assert.assertNotEquals(ob1, null);
    Assert.assertNotEquals(ob1, "some-string");

    UpdateResponse ob3 = new UpdateResponse(ok, createdAt, updatedAt, 1);
    UpdateResponse ob4 = new UpdateResponse(ok, createdAt, updatedAt, 2);
    Assert.assertNotEquals(ob3, ob4);
  }

  @Test
  public void hashCodeTest() {
    final String ok = "ok";
    Timestamp createdAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    Timestamp updatedAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    UpdateResponse ob1 = new UpdateResponse(ok, createdAt, updatedAt, 1);
    UpdateResponse ob2 = new UpdateResponse(ok, createdAt, updatedAt, 1);
    Assert.assertEquals(ob1.hashCode(), ob1.hashCode());
    Assert.assertEquals(ob1.hashCode(), ob2.hashCode());
  }

  @Test
  public void testAccessor() {
    final String status = UUID.randomUUID().toString();
    final Instant now = Instant.now();
    Timestamp createdAt =
        Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build();
    Timestamp updatedAt =
        Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build();
    UpdateResponse ob = new UpdateResponse(status, createdAt, updatedAt, 1);
    Assert.assertEquals(1L, ob.getModifiedCount());
    Assert.assertEquals(status, ob.getStatus());
    Assert.assertEquals(now, ob.getMetadata().getCreatedAt());
    Assert.assertEquals(now, ob.getMetadata().getUpdatedAt());
  }
}
