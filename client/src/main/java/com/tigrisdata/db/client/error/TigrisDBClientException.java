package com.tigrisdata.db.client.error;

/** Represents exceptional situation raised by clients */
public class TigrisDBClientException extends TigrisDBException {
  public TigrisDBClientException(String message) {
    super(message);
  }

  public TigrisDBClientException(String message, Throwable cause) {
    super(message, cause);
  }

  public TigrisDBClientException(Throwable cause) {
    super(cause);
  }
}
