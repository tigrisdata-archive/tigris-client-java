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

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Assert;
import org.junit.Test;

public class SearchRequestOptionsTest {

  @Test
  public void build() {
    SearchRequestOptions options =
        SearchRequestOptions.newBuilder().withPage(5).withPerPage(20).build();
    Assert.assertEquals(5, options.getPage());
    Assert.assertEquals(20, options.getPerPage());
  }

  @Test
  public void getDefault() {
    SearchRequestOptions options = SearchRequestOptions.getDefault();
    Assert.assertEquals(1, options.getPage());
    Assert.assertEquals(10, options.getPerPage());
  }

  @Test
  public void equalsContract() {
    EqualsVerifier.forClass(SearchRequestOptions.class).verify();
  }
}
