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

import com.tigrisdata.db.annotation.TigrisCollection;
import com.tigrisdata.db.annotation.TigrisPrimaryKey;
import com.tigrisdata.db.type.TigrisCollectionType;

import java.util.Date;

@TigrisCollection(value = "BoxedTypes", description = "Test description for boxed type")
public class BoxedTypes implements TigrisCollectionType {

  @TigrisPrimaryKey(1)
  private Integer aInteger;

  private Long aLong;
  private Float aFloat;
  private Double aDouble;
  private Short aShort;
  private Byte aByte;
  private Character aCharacter;
  private Boolean aBoolean;

  private Date date;
}
