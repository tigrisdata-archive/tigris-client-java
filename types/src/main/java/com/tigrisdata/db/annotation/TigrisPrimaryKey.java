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
  int order();

  /**
   * Primary key can have auto-generate value set to it on server as part of insert operations,
   * server will supply auto-generated value if this field is left empty by user.
   *
   * @return if this should be auto-generated, if left empty during insertion.
   */
  boolean autoGenerate() default false;
}
