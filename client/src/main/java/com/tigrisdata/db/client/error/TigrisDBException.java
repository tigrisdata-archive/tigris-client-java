package com.tigrisdata.db.client.error;

/** Base type of TigrisDB exceptions */
public class TigrisDBException extends Exception {

  TigrisDBException(String message) {
    super(message);
  }

  TigrisDBException(String message, Throwable cause) {
    super(message, cause);
  }

  TigrisDBException(Throwable cause) {
    super(cause);
  }
}
