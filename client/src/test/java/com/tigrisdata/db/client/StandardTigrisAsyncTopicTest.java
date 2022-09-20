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
package com.tigrisdata.db.client;

import com.tigrisdata.db.client.collection.ChatMessage;
import com.tigrisdata.db.client.error.TigrisException;
import com.tigrisdata.db.client.grpc.TestTigrisService;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class StandardTigrisAsyncTopicTest {

  private static String SERVER_NAME;
  private static final TestTigrisService TEST_USER_SERVICE = new TestTigrisService();
  @ClassRule public static final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

  @BeforeClass
  public static void setup() throws Exception {
    SERVER_NAME = InProcessServerBuilder.generateName();
    grpcCleanup
        .register(
            InProcessServerBuilder.forName(SERVER_NAME)
                .directExecutor()
                .addService(TEST_USER_SERVICE)
                .build())
        .start();
  }

  @Test
  public void testPublish() throws TigrisException, ExecutionException, InterruptedException {
    TigrisAsyncClient client = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = client.getDatabase("db1");
    TigrisAsyncTopic<ChatMessage> chatTopic = db1.getTopic(ChatMessage.class);
    CompletableFuture<PublishResponse<ChatMessage>> response =
        chatTopic.publish(new ChatMessage("message-1", "user-1", "user-2"));
    PublishResponse<ChatMessage> resp = response.get();
    Assert.assertEquals("published: 1", resp.getStatus());
  }

  @Test
  public void testPublishMultiple()
      throws TigrisException, ExecutionException, InterruptedException {
    List<ChatMessage> messages = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      messages.add(new ChatMessage("message-1", "user-1", "user-2"));
    }
    TigrisAsyncClient client = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = client.getDatabase("db1");
    TigrisAsyncTopic<ChatMessage> chatTopic = db1.getTopic(ChatMessage.class);
    CompletableFuture<PublishResponse<ChatMessage>> response = chatTopic.publish(messages);
    Assert.assertEquals("published: 5", response.get().getStatus());
  }

  @Test
  public void testSubscribe() throws InterruptedException {
    TigrisAsyncClient client = TestUtils.getTestAsyncClient(SERVER_NAME, grpcCleanup);
    TigrisAsyncDatabase db1 = client.getDatabase("db1");
    TigrisAsyncTopic<ChatMessage> chatTopic = db1.getTopic(ChatMessage.class);

    AtomicInteger count = new AtomicInteger();
    AtomicBoolean completed = new AtomicBoolean();
    TigrisAsyncMessageReader<ChatMessage> reader =
        new TigrisAsyncMessageReader<ChatMessage>() {
          @Override
          public void onNext(ChatMessage message) {
            Assert.assertEquals(1, message.getId());
            Assert.assertEquals("user1", message.getFrom());
            Assert.assertEquals("user2", message.getTo());
            Assert.assertEquals("hello", message.getMessage());
            count.incrementAndGet();
          }

          @Override
          public void onError(Throwable t) {
            Assert.fail("This should not happen");
          }

          @Override
          public void onCompleted() {
            Assert.assertEquals(1, count.get());
            completed.set(true);
          }
        };
    chatTopic.subscribe(reader);
    while (count.get() < 1) {
      //noinspection BusyWait
      Thread.sleep(10);
    }
    Assert.assertTrue(completed.get());
  }
}
