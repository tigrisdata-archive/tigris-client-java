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
package com.tigrisdata.db.client.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigrisdata.db.client.model.Field;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class Utilities {
  private Utilities() {}

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static <F, T> Iterator<T> from(Iterator<F> iterator, Function<F, T> converter) {
    return new ConvertedIterator<>(iterator, converter);
  }

  public static String fieldsOperation(String operator, List<Field<?>> fields)
      throws JsonProcessingException {
    Map<String, Object> map = new LinkedHashMap<>();
    fields.forEach(f -> map.put(f.name(), f.value()));
    return OBJECT_MAPPER.writeValueAsString(Collections.singletonMap(operator, map));
  }

  public static String fields(List<Field<?>> fields) throws JsonProcessingException {
    Map<String, Object> map = new LinkedHashMap<>();
    fields.forEach(f -> map.put(f.name(), f.value()));
    return OBJECT_MAPPER.writeValueAsString(map);
  }

  static class ConvertedIterator<F, T> implements Iterator<T> {

    private final Iterator<F> sourceIterator;
    private final Function<F, T> converter;

    public ConvertedIterator(Iterator<F> sourceIterator, Function<F, T> converter) {
      this.sourceIterator = sourceIterator;
      this.converter = converter;
    }

    @Override
    public boolean hasNext() {
      return sourceIterator.hasNext();
    }

    @Override
    public T next() {
      return converter.apply(sourceIterator.next());
    }
  }
}
