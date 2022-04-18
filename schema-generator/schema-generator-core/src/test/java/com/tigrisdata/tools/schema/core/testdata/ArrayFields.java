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

@TigrisDBCollection("ArrayFields")
public class ArrayFields {
  private int[] intArray1D;
  private int[][] intArray2D;
  private int[][][] intArray3D;
  private int[][][][] intArray4D;
  private int[][][][][] intArray5D;

  private String[] string1D;
  private String[][] string2D;
  private String[][][] string3D;
  private String[][][][] string4D;
  private String[][][][][] string5D;
}
