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

import com.google.common.base.CaseFormat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.tigrisdata.db.annotation.TigrisDBCollection;
import com.tigrisdata.db.client.error.TigrisDBException;
import com.tigrisdata.db.type.TigrisCollectionType;
import org.atteo.evo.inflector.English;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Function;

final class Utilities {
  private Utilities() {}

  // TODO update this once server sends the message back
  static final String INSERT_SUCCESS_RESPONSE = "inserted";
  static final String DELETE_SUCCESS_RESPONSE = "deleted";

  static String getCollectionName(Class<? extends TigrisCollectionType> clazz) {
    TigrisDBCollection tigrisDBCollection = clazz.getAnnotation(TigrisDBCollection.class);
    if (tigrisDBCollection != null) {
      return tigrisDBCollection.value();
    }
    return CaseFormat.UPPER_CAMEL.to(
        CaseFormat.LOWER_UNDERSCORE, English.plural(clazz.getSimpleName()));
  }

  static String extractTigrisDBCollectionName(Class<? extends TigrisCollectionType> clazz) {
    TigrisDBCollection annotation = clazz.getAnnotation(TigrisDBCollection.class);
    if (annotation != null) {
      return annotation.value();
    }
    throw new IllegalArgumentException("No TigrisDBCollection name found");
  }

  /**
   * Converts from {@link Iterator} of Type F to {@link Iterator} of type T
   *
   * @param iterator source iterator
   * @param converter function that converts F to T type
   * @param <F> source type
   * @param <T> destination type
   * @return an instance of {@link Iterator} of type T
   */
  static <F, T> Iterator<T> transformIterator(Iterator<F> iterator, Function<F, T> converter) {
    return new ConvertedIterator<>(iterator, converter);
  }

  /**
   * Converts {@link ListenableFuture} of type F to {@link CompletableFuture} of type T
   *
   * @param listenableFuture source listenable future
   * @param converter function that converts type F to type T
   * @param executor executor to run callback that transforms Future when source Future is complete
   * @param <F> from type
   * @param <T> to type
   * @return an instance of {@link CompletableFuture}
   */
  static <F, T> CompletableFuture<T> transformFuture(
      ListenableFuture<F> listenableFuture,
      Function<F, T> converter,
      Executor executor,
      String errorMessage) {
    return transformFuture(listenableFuture, converter, executor, errorMessage, Optional.empty());
  }
  /**
   * Converts {@link ListenableFuture} of type F to {@link CompletableFuture} of type T
   *
   * @param listenableFuture source listenable future
   * @param converter function that converts type F to type T
   * @param executor executor to run callback that transforms Future when source Future is complete
   * @param exceptionHandler handles exception
   * @param <F> from type
   * @param <T> to type
   * @return an instance of {@link CompletableFuture}
   */
  static <F, T> CompletableFuture<T> transformFuture(
      ListenableFuture<F> listenableFuture,
      Function<F, T> converter,
      Executor executor,
      String errorMessage,
      Optional<BiConsumer<CompletableFuture<T>, Throwable>> exceptionHandler) {
    CompletableFuture<T> result = new CompletableFuture<>();
    Futures.addCallback(
        listenableFuture,
        new FutureCallback<F>() {
          @Override
          public void onSuccess(F f) {
            result.complete(converter.apply(f));
          }

          @Override
          public void onFailure(Throwable throwable) {
            if (exceptionHandler.isPresent()) {
              exceptionHandler.get().accept(result, throwable);
            } else {
              result.completeExceptionally(new TigrisDBException(errorMessage, throwable));
            }
          }
        },
        executor);
    return result;
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
