package com.tigrisdata.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a given field as the part of the primary key. The value attribute determines the order
 * of the primary key
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TigrisPrimaryKey {
  /**
   * There can be multiple field composing the primary-key. This accepts the order.
   *
   * @return order of the field in primary_keys
   */
  int value();
}
