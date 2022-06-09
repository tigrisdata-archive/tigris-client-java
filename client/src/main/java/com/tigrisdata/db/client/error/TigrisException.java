/*
 * Copyright 2022 Tigris Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tigrisdata.db.client.error;

import java.util.Optional;

/** Base type of Tigris exceptions */
public class TigrisException extends Exception {
  private final Optional<TigrisError> tigrisErrorOptional;

  public TigrisException(
      String message, Optional<TigrisError> tigrisErrorOptional, Throwable cause) {
    super(message, cause);
    this.tigrisErrorOptional = tigrisErrorOptional;
  }

  public TigrisException(String message, Throwable cause) {
    super(message, cause);
    this.tigrisErrorOptional = Optional.empty();
  }

  /**
   * When Tigris Server reports an error, it will provide additional detail about the error. If this
   * exception is raised from server side, those details will be made available here.
   *
   * @return optionally {@link TigrisError}
   */
  public Optional<TigrisError> getTigrisErrorOptional() {
    return tigrisErrorOptional;
  }

  @Override
  public String getMessage() {
    String message = super.getMessage();
    if (getCause() != null && getCause().getMessage() != null) {
      message += " Cause: " + getCause().getMessage();
    }
    return message;
  }
}
