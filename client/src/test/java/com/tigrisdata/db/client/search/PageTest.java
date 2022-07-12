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

package com.tigrisdata.db.client.search;

import com.tigrisdata.db.api.v1.grpc.Api;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class PageTest {

  @Test
  public void convert() {
    Api.Page page = Api.Page.newBuilder().setCurrent(12).setSize(19).build();
    Page actual = Page.from(page);
    Assert.assertEquals(12, actual.getCurrent());
    Assert.assertEquals(19, actual.getSize());
  }

  @Test
  public void convertFromDefault() {
    Api.Page page = Api.Page.newBuilder().build();
    Page actual = Page.from(page);
    Assert.assertEquals(0, actual.getCurrent());
    Assert.assertEquals(0, actual.getSize());
  }

  @Test
  public void convertFromNull() {
    Assert.assertThrows(NullPointerException.class, () -> Page.from(null));
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(Page.class).verify();
  }
}
