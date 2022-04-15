package com.tigrisdata.db.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** This is used by tigrisdb-model-generator to tag the collection name in generated models */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TigrisDBCollection {
  String value() default "";
}
