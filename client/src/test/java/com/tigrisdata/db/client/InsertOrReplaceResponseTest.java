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
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class InsertOrReplaceResponseTest {

  @Test
  public void equalsTest() {
    Timestamp createdAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    Timestamp updatedAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    final String ok = "ok";
    InsertOrReplaceResponse ob1 = new InsertOrReplaceResponse(ok, createdAt, createdAt);
    InsertOrReplaceResponse ob2 = new InsertOrReplaceResponse(ok, createdAt, updatedAt);
    Assert.assertEquals(ob1, ob1);
    Assert.assertEquals(ob1, ob2);
    Assert.assertNotEquals(ob1, null);
    Assert.assertNotEquals(ob1, "some-string");
  }

  @Test
  public void hashCodeTest() {
    Timestamp createdAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    Timestamp updatedAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    final String ok = "ok";
    InsertOrReplaceResponse ob1 = new InsertOrReplaceResponse(ok, createdAt, updatedAt);
    InsertOrReplaceResponse ob2 = new InsertOrReplaceResponse(ok, createdAt, updatedAt);
    Assert.assertEquals(ob1.hashCode(), ob1.hashCode());
    Assert.assertEquals(ob1.hashCode(), ob2.hashCode());

    InsertOrReplaceResponse ob3 = new InsertOrReplaceResponse(null, createdAt, updatedAt);
    InsertOrReplaceResponse ob4 = new InsertOrReplaceResponse(null, createdAt, updatedAt);
    Assert.assertEquals(ob3.hashCode(), ob4.hashCode());
  }

  @Test
  public void accessorTest() {
    final String status = UUID.randomUUID().toString();
    Timestamp createdAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    Timestamp updatedAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    InsertOrReplaceResponse ob = new InsertOrReplaceResponse(status, createdAt, updatedAt);
    Assert.assertEquals(status, ob.getStatus());
    Assert.assertEquals(createdAt, ob.getMetadata().getCreatedAt());
    Assert.assertEquals(updatedAt, ob.getMetadata().getUpdatedAt());
  }
}
