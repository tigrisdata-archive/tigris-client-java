package com.tigrisdata.db.client.model;

public class StandardField<T> implements Field<T> {

  private final String name;
  private final T value;

  public StandardField(String name, T value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public T value() {
    return value;
  }

  @Override
  public String toString() {
    return "\"" + name + "\":" + value + "";
  }
}
