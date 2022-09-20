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
package com.tigrisdata.db.util;

import com.tigrisdata.db.annotation.TigrisCollection;
import com.tigrisdata.db.type.TigrisDocumentCollectionType;
import org.junit.Assert;
import org.junit.Test;

public class TypeUtilsTest {

  @Test
  public void testGetCollectionName() {
    Assert.assertEquals("users", TypeUtils.getCollectionName(User.class));
    Assert.assertEquals("user_types", TypeUtils.getCollectionName(UserType.class));
    Assert.assertEquals(
        "user_shopping_preferences", TypeUtils.getCollectionName(UserShoppingPreference.class));
    Assert.assertEquals("ab_cd_ef_ghs", TypeUtils.getCollectionName(AbCdEfGh.class));
    Assert.assertEquals("customized_name", TypeUtils.getCollectionName(CustomCollectionName.class));
  }

  class User implements TigrisDocumentCollectionType {}

  @TigrisCollection("customized_name")
  class CustomCollectionName implements TigrisDocumentCollectionType {}

  class UserType implements TigrisDocumentCollectionType {}

  class AbCdEfGh implements TigrisDocumentCollectionType {}

  class UserShoppingPreference implements TigrisDocumentCollectionType {}
}
