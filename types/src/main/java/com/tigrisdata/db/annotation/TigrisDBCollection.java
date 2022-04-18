package com.tigrisdata.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotates that given type is a TigrisDB collection type. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TigrisDBCollection {
  String value() default "";

  String description() default "";
}
