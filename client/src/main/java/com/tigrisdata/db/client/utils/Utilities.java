package com.tigrisdata.db.client.utils;

import java.util.Iterator;
import java.util.function.Function;

public final class Utilities {
  private Utilities() {}

  public static <F, T> Iterator<T> from(Iterator<F> iterator, Function<F, T> converter) {
    return new ConvertedIterator<>(iterator, converter);
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
