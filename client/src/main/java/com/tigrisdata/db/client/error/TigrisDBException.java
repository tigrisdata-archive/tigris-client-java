package com.tigrisdata.db.client.error;

/** Base type of TigrisDB exceptions */
public class TigrisDBException extends Exception {

  public TigrisDBException(String message) {
    super(message);
  }

  public TigrisDBException(String message, Throwable cause) {
    super(message, cause);
  }

  public TigrisDBException(Throwable cause) {
    super(cause);
  }
}
