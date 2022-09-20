package com.tigrisdata.db.client;

import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.type.TigrisMessageCollectionType;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Tigris topic, async interface
 *
 * @param <T> type of the topic
 */
public interface TigrisAsyncTopic<T extends TigrisMessageCollectionType> {

  /**
   * Publishes messages into the collection.
   *
   * @param messages list of messages to publish
   * @return an instance of {@link PublishResponse} from server
   * @throws TigrisException in case of an error
   */
  CompletableFuture<PublishResponse<T>> publish(List<T> messages) throws TigrisException;

  /**
   * Publishes a single message to the collection
   *
   * @param message message to publish
   * @return a future to the {@link InsertResponse}
   * @throws TigrisException in case of an error
   */
  CompletableFuture<PublishResponse<T>> publish(T message) throws TigrisException;

  /**
   * Subscribes to published messages of the collection
   *
   * @param reader reader callback
   */
  void subscribe(TigrisAsyncMessageReader<T> reader);
}
