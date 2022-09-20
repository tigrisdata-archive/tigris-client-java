package com.tigrisdata.db.client;

import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisMessageCollectionType;

import java.util.Iterator;
import java.util.List;

/**
 * Tigris topic
 *
 * @param <T> type of the topic
 */
public interface TigrisTopic<T extends TigrisMessageCollectionType> {

  /**
   * Publishes messages into the collection.
   *
   * @param messages list of messages to publish
   * @return an instance of {@link PublishResponse} from server
   * @throws TigrisException in case of an error
   */
  PublishResponse<T> publish(List<T> messages) throws TigrisException;

  /**
   * Publishes message into the collection.
   *
   * @param message a message to publish
   * @return an instance of {@link PublishResponse} from server
   * @throws TigrisException in case of an error
   */
  PublishResponse<T> publish(T message) throws TigrisException;

  /**
   * @return stream of messages published
   * @throws TigrisException in case of an error
   */
  Iterator<T> subscribe() throws TigrisException;
}
