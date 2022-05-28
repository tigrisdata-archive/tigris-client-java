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

import java.util.Collections;
import java.util.TreeMap;
import java.util.UUID;

public class InsertResponseTest {
  @Test
  public void equalsTest() {
    Timestamp createdAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    Timestamp updatedAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    String ok = "ok";
    InsertResponse ob1 =
        new InsertResponse(ok, createdAt, updatedAt, new TreeMap[0], Collections.emptyList());
    InsertResponse ob2 =
        new InsertResponse(ok, createdAt, updatedAt, new TreeMap[0], Collections.emptyList());
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
    String ok = "ok";
    InsertResponse ob1 =
        new InsertResponse(ok, createdAt, updatedAt, new TreeMap[0], Collections.emptyList());
    InsertResponse ob2 =
        new InsertResponse(ok, createdAt, updatedAt, new TreeMap[0], Collections.emptyList());
    Assert.assertEquals(ob1.hashCode(), ob1.hashCode());
    Assert.assertEquals(ob1.hashCode(), ob2.hashCode());

    InsertResponse ob3 =
        new InsertResponse(null, createdAt, updatedAt, new TreeMap[0], Collections.emptyList());
    InsertResponse ob4 =
        new InsertResponse(null, createdAt, updatedAt, new TreeMap[0], Collections.emptyList());
    Assert.assertEquals(ob3.hashCode(), ob4.hashCode());
  }

  @Test
  public void accessorTest() {
    final String status = UUID.randomUUID().toString();

    Timestamp createdAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    Timestamp updatedAt =
        Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
    InsertResponse ob =
        new InsertResponse(status, createdAt, updatedAt, new TreeMap[0], Collections.emptyList());
    Assert.assertEquals(status, ob.getStatus());
    Assert.assertEquals(ob.getMetadata().getCreatedAt(), createdAt);
    Assert.assertEquals(ob.getMetadata().getCreatedAt(), updatedAt);
  }
}
