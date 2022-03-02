package com.tigrisdata.db.client.error;

/** Represents exceptional situation raised by server */
public class TigrisDBServerException extends TigrisDBException {
  public TigrisDBServerException(String message) {
    super(message);
  }

  public TigrisDBServerException(String message, Throwable cause) {
    super(message, cause);
  }

  public TigrisDBServerException(Throwable cause) {
    super(cause);
  }
}
