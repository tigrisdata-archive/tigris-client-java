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

import java.io.IOException;

/** represents the TigrisDBSchema */
public interface TigrisDBSchema {
  /**
   * Reads the schema content
   *
   * @return string form of the JSON schema
   * @throws IOException if the reading of schema failed.
   */
  String getSchemaContent() throws IOException;
  /**
   * Reads the schema name from "name" JSON attribute.
   *
   * @return name of the schema
   * @throws IOException if the reading of schema failed.
   */
  String getName() throws IOException;
}
