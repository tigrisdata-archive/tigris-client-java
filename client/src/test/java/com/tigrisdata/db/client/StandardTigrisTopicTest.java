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
import java.util.Iterator;
import java.util.List;

public class StandardTigrisTopicTest {

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
  public void testPublish() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TigrisTopic<ChatMessage> chatTopic = db1.getTopic(ChatMessage.class);
    PublishResponse<ChatMessage> response =
        chatTopic.publish(new ChatMessage("message-1", "user-1", "user-2"));
    Assert.assertEquals("published: 1", response.getStatus());
  }

  @Test
  public void testPublishMultiple() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TigrisTopic<ChatMessage> chatTopic = db1.getTopic(ChatMessage.class);
    List<ChatMessage> messages = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      messages.add(new ChatMessage("message-1", "user-1", "user-2"));
    }
    PublishResponse<ChatMessage> response = chatTopic.publish(messages);
    Assert.assertEquals("published: 5", response.getStatus());
  }

  @Test
  public void testSubscribe() throws TigrisException {
    TigrisClient client = TestUtils.getTestClient(SERVER_NAME, grpcCleanup);
    TigrisDatabase db1 = client.getDatabase("db1");
    TigrisTopic<ChatMessage> chatTopic = db1.getTopic(ChatMessage.class);
    Iterator<ChatMessage> itr = chatTopic.subscribe();
    int count = 0;
    while (itr.hasNext()) {
      ChatMessage msg = itr.next();
      Assert.assertEquals("hello", msg.getMessage());
      Assert.assertEquals("user1", msg.getFrom());
      Assert.assertEquals("user2", msg.getTo());
      Assert.assertEquals(1, msg.getId());
      count++;
    }
    Assert.assertEquals(1, count);
  }
}
