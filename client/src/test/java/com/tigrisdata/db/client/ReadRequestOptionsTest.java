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

public class ReadRequestOptionsTest {

  @Test
  public void equalsTest() {
    ReadRequestOptions readRequestOptions1 = new ReadRequestOptions();
    ReadRequestOptions readRequestOptions2 = new ReadRequestOptions();
    Assert.assertEquals(readRequestOptions1, readRequestOptions1);
    Assert.assertEquals(readRequestOptions1, readRequestOptions2);

    Assert.assertNotEquals(readRequestOptions1, null);
    Assert.assertNotEquals(readRequestOptions1, "some-string");

    ReadRequestOptions readRequestOptions3 = new ReadRequestOptions();
    readRequestOptions3.setReadOptions(ReadOptions.DEFAULT_INSTANCE);
    ReadRequestOptions readRequestOptions4 = new ReadRequestOptions();
    readRequestOptions4.setReadOptions(ReadOptions.DEFAULT_INSTANCE);
    Assert.assertEquals(readRequestOptions3, readRequestOptions4);

    ReadRequestOptions readRequestOptions5 = new ReadRequestOptions(1, 2);
    ReadRequestOptions readRequestOptions6 = new ReadRequestOptions(1, 3);
    Assert.assertNotEquals(readRequestOptions5, readRequestOptions6);

    ReadRequestOptions readRequestOptions7 = new ReadRequestOptions(1, 2);
    ReadRequestOptions readRequestOptions8 = new ReadRequestOptions(2, 2);
    Assert.assertNotEquals(readRequestOptions7, readRequestOptions8);
  }

  @Test
  public void testHashCode() {
    ReadRequestOptions readRequestOptions1 = new ReadRequestOptions();
    ReadRequestOptions readRequestOptions2 = new ReadRequestOptions();
    Assert.assertEquals(readRequestOptions1.hashCode(), readRequestOptions1.hashCode());
    Assert.assertEquals(readRequestOptions1.hashCode(), readRequestOptions2.hashCode());

    ReadRequestOptions readRequestOptions3 = new ReadRequestOptions(1, 2);
    ReadRequestOptions readRequestOptions4 = new ReadRequestOptions(1, 2);
    Assert.assertEquals(readRequestOptions3.hashCode(), readRequestOptions4.hashCode());

    ReadRequestOptions readRequestOptions5 = new ReadRequestOptions(1, 2);
    ReadRequestOptions readRequestOptions6 = new ReadRequestOptions(1, 2);
    Assert.assertEquals(readRequestOptions5.hashCode(), readRequestOptions6.hashCode());
  }
}
