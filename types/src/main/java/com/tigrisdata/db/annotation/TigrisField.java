package com.tigrisdata.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Specifies a field of a document. Allows user to customize the schema for the field. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface TigrisField {
  /**
   * Description of the field, this is for documentation / readability purpose
   *
   * @return description string
   */
  String description() default "";
}
