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

import org.junit.Assert;
import org.junit.Test;

public class InsertResponseTest {
  @Test
  public void equalsTest() {
    TigrisDBResponse tigrisDBResponse = new TigrisDBResponse("ok");
    InsertResponse ob1 = new InsertResponse(tigrisDBResponse);
    InsertResponse ob2 = new InsertResponse(tigrisDBResponse);
    Assert.assertEquals(ob1, ob1);
    Assert.assertEquals(ob1, ob2);
    Assert.assertNotEquals(ob1, null);
    Assert.assertNotEquals(ob1, "some-string");
  }

  @Test
  public void hashCodeTest() {
    TigrisDBResponse tigrisDBResponse = new TigrisDBResponse("ok");
    InsertResponse ob1 = new InsertResponse(tigrisDBResponse);
    InsertResponse ob2 = new InsertResponse(tigrisDBResponse);
    Assert.assertEquals(ob1.hashCode(), ob1.hashCode());
    Assert.assertEquals(ob1.hashCode(), ob2.hashCode());

    InsertResponse ob3 = new InsertResponse(null);
    InsertResponse ob4 = new InsertResponse(null);
    Assert.assertEquals(ob3.hashCode(), ob4.hashCode());
  }

  @Test
  public void accessorTest() {
    TigrisDBResponse tigrisDBResponse = new TigrisDBResponse("ok");
    InsertResponse ob = new InsertResponse(tigrisDBResponse);
    Assert.assertEquals(ob.getTigrisDBResponse(), tigrisDBResponse);
  }
}
