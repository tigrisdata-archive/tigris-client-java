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

public class CollectionInfoTest {
  @Test
  public void testEquals() {
    CollectionInfo collectionInfo1 = new CollectionInfo("c1");
    CollectionInfo collectionInfo2 = new CollectionInfo("c1");
    Assert.assertEquals(collectionInfo1, collectionInfo1);
    Assert.assertEquals(collectionInfo1, collectionInfo2);
    Assert.assertFalse(collectionInfo1.equals(null));
    Assert.assertFalse(collectionInfo1.equals("some-string"));
    Assert.assertFalse(collectionInfo1.equals(new CollectionInfo("c2")));
  }

  @Test
  public void testHashCode() {
    CollectionInfo collectionInfo1 = new CollectionInfo("c1");
    CollectionInfo collectionInfo2 = new CollectionInfo("c1");
    Assert.assertEquals(collectionInfo1.hashCode(), collectionInfo1.hashCode());
    Assert.assertEquals(collectionInfo1.hashCode(), collectionInfo2.hashCode());

    CollectionInfo collectionInfo3 = new CollectionInfo(null);
    CollectionInfo collectionInfo4 = new CollectionInfo(null);
    Assert.assertEquals(collectionInfo3.hashCode(), collectionInfo4.hashCode());
  }

  @Test
  public void toStringTest() {
    CollectionInfo collectionInfo1 = new CollectionInfo("c1");
    Assert.assertEquals("CollectionInfo{collectionName='c1'}", collectionInfo1.toString());
    CollectionInfo collectionInfo2 = new CollectionInfo("c1");
    CollectionInfo collectionInfo3 = new CollectionInfo("c1");
    Assert.assertEquals(collectionInfo2.toString(), collectionInfo3.toString());
  }

  @Test
  public void testGetCollectionName() {
    CollectionInfo collectionInfo1 = new CollectionInfo("c1");
    Assert.assertEquals("c1", collectionInfo1.getCollectionName());
  }
}
