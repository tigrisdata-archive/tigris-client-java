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
package com.tigrisdata.db.client.model;

import org.junit.Assert;
import org.junit.Test;

public class UpdateResponseTest {
  @Test
  public void equalsTest() {
    UpdateResponse ob1 = new UpdateResponse(1);
    UpdateResponse ob2 = new UpdateResponse(1);
    Assert.assertEquals(ob1, ob1);
    Assert.assertEquals(ob1, ob2);
    Assert.assertNotEquals(ob1, null);
    Assert.assertNotEquals(ob1, "some-string");

    UpdateResponse ob3 = new UpdateResponse(1);
    UpdateResponse ob4 = new UpdateResponse(2);
    Assert.assertNotEquals(ob3, ob4);
  }

  @Test
  public void hashCodeTest() {
    UpdateResponse ob1 = new UpdateResponse(1);
    UpdateResponse ob2 = new UpdateResponse(1);
    Assert.assertEquals(ob1.hashCode(), ob1.hashCode());
    Assert.assertEquals(ob1.hashCode(), ob2.hashCode());
  }

  @Test
  public void testAccessor() {
    UpdateResponse ob1 = new UpdateResponse(1);
    Assert.assertEquals(1L, ob1.getUpdatedRecordCount());
  }
}
