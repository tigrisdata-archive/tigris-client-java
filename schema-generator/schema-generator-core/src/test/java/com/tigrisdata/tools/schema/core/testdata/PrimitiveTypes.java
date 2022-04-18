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
package com.tigrisdata.tools.schema.core.testdata;

import com.tigrisdata.db.annotation.TigrisDBCollection;
import com.tigrisdata.db.annotation.TigrisDBCollectionField;
import com.tigrisdata.db.annotation.TigrisDBCollectionPrimaryKey;
import com.tigrisdata.db.type.TigrisCollectionType;

@TigrisDBCollection("PrimitiveTypesCollection")
public class PrimitiveTypes implements TigrisCollectionType {
  @TigrisDBCollectionField(description = "This is the id field")
  @TigrisDBCollectionPrimaryKey(1)
  private int id;

  private String name;
  private short shortNum;
  private byte byteNum;
  private long longNum;
  private float floatNum;
  private double doubleNum;
  private char aChar;
  private boolean aBoolean;
}
