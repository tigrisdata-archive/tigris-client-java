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
package com.tigrisdata.db.client.collection;

import com.tigrisdata.db.type.TigrisMessageCollectionType;

import java.util.Objects;

public class ChatMessage implements TigrisMessageCollectionType {
  private int id;
  private final String message;
  private final String from;
  private final String to;

  public ChatMessage(String message, String from, String to) {
    this.message = message;
    this.from = from;
    this.to = to;
  }

  public String getMessage() {
    return message;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChatMessage chatTopic = (ChatMessage) o;
    return Objects.equals(message, chatTopic.message)
        && Objects.equals(from, chatTopic.from)
        && Objects.equals(to, chatTopic.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(message, from, to);
  }
}
