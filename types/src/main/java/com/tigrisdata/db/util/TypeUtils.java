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
import com.tigrisdata.db.type.TigrisCollectionType;
import org.atteo.evo.inflector.English;

public final class TypeUtils {

  private TypeUtils() {}

  /**
   * Get collection name from the collection model class. It first looks at the annotation for user
   * specific collection name, if it doesn't find user's input then it will convert the class name
   * to plural and snake case name;
   *
   * @param clazz collection model class
   * @return name of the collection.
   */
  public static String getCollectionName(Class<? extends TigrisCollectionType> clazz) {
    TigrisCollection tigrisCollection = clazz.getAnnotation(TigrisCollection.class);
    if (tigrisCollection != null) {
      return tigrisCollection.value();
    }
    return toSnakeCase(English.plural(clazz.getSimpleName()));
  }

  private static String toSnakeCase(String str) {
    StringBuilder sb = new StringBuilder();

    for (char c : str.toCharArray()) {
      if (Character.isLetter(c) || Character.isDigit(c)) {
        if (Character.isUpperCase(c)) {
          if (sb.length() > 0) {
            sb.append("_");
          }

          sb.append(Character.toLowerCase(c));
        } else {
          sb.append(c);
        }
      } else {
        sb.append("_");
      }
    }

    return sb.toString();
  }
}
