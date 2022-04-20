package com.tigrisdata.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotates that given type is a TigrisDB collection type. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TigrisDBCollection {
  /**
   * User can specify their collection name here. Default the collection name is derived by
   * pluralizing the classname and snake casing the string.
   *
   * @return collection name
   */
  String value() default "";

  /**
   * Optional collection description for documentation purpose.
   *
   * @return description of the collection
   */
  String description() default "";
}
