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
package com.tigrisdata.db.client.collection;

import com.tigrisdata.db.annotation.TigrisPrimaryKey;
import com.tigrisdata.db.type.TigrisDocumentCollectionType;

import java.util.UUID;

public class AutoGeneratingPKeysModel implements TigrisDocumentCollectionType {
  @TigrisPrimaryKey(order = 1, autoGenerate = true)
  int intPKey;

  @TigrisPrimaryKey(order = 2, autoGenerate = true)
  long longPKey;

  @TigrisPrimaryKey(order = 3, autoGenerate = true)
  UUID uuidPKey;

  @TigrisPrimaryKey(order = 4, autoGenerate = true)
  String strPKey;

  String name;

  public AutoGeneratingPKeysModel(String name) {
    this.name = name;
  }

  public int getIntPKey() {
    return intPKey;
  }

  public void setIntPKey(int intPKey) {
    this.intPKey = intPKey;
  }

  public long getLongPKey() {
    return longPKey;
  }

  public void setLongPKey(long longPKey) {
    this.longPKey = longPKey;
  }

  public UUID getUuidPKey() {
    return uuidPKey;
  }

  public void setUuidPKey(UUID uuidPKey) {
    this.uuidPKey = uuidPKey;
  }

  public String getStrPKey() {
    return strPKey;
  }

  public void setStrPKey(String strPKey) {
    this.strPKey = strPKey;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
