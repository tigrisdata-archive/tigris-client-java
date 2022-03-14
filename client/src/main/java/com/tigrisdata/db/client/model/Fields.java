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
