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

public final class Fields {

  public static Field<String> stringField(String key, String value) {
    return new StandardField<>(key, value);
  }

  public static Field<Integer> integerField(String key, Integer value) {
    return new StandardField<>(key, value);
  }

  public static Field<Double> doubleField(String key, Double value) {
    return new StandardField<>(key, value);
  }

  public static Field<Boolean> booleanField(String key, Boolean value) {
    return new StandardField<>(key, value);
  }

  public static Field<byte[]> byteArrayField(String key, byte[] value) {
    return new StandardField<>(key, value);
  }
}
