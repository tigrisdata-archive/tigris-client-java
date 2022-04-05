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
package com.tigrisdata.db.client.utils;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class UtilitiesTest {
  @Test
  public void testIteratorTransformation() {
    List<String> stringList = new ArrayList<>();
    stringList.add("1");
    stringList.add("2");
    stringList.add("3");

    Iterator<String> stringIterator = stringList.iterator();
    Iterator<Integer> integerIterator =
        Utilities.transformIterator(stringIterator, Integer::parseInt);
    boolean oneSeen = false;
    boolean twoSeen = false;
    boolean threeSeen = false;
    while (integerIterator.hasNext()) {
      int val = integerIterator.next();
      switch (val) {
        case 1:
          {
            if (!oneSeen) oneSeen = true;
            else Assert.fail("one already seen");
            break;
          }
        case 2:
          {
            if (!twoSeen) twoSeen = true;
            else Assert.fail("two already seen");
            break;
          }
        case 3:
          {
            if (!threeSeen) threeSeen = true;
            else Assert.fail("three already seen");
            break;
          }
        default:
          Assert.fail("Unexpected value seen " + val);
      }
    }
    Assert.assertTrue(oneSeen);
    Assert.assertTrue(twoSeen);
    Assert.assertTrue(threeSeen);
  }

  @Test
  public void testSuccessFutureTransformation() {
    SettableFuture<String> listenableFuture = SettableFuture.create();
    CompletableFuture<Integer> completableFuture =
        Utilities.transformFuture(
            listenableFuture, Integer::parseInt, MoreExecutors.directExecutor());

    AtomicBoolean completed = new AtomicBoolean();
    completableFuture.whenComplete(
        (val, ex) -> {
          Assert.assertNull(ex);
          Assert.assertTrue(1 == val);
          completed.set(true);
        });
    listenableFuture.set("1");
    Assert.assertTrue(completed.get());
  }

  @Test
  public void testFailedFutureTransformation() {
    Exception testException = new Exception("testFailedFutureTransformation message");
    SettableFuture<String> listenableFuture = SettableFuture.create();
    CompletableFuture<Integer> completableFuture =
        Utilities.transformFuture(
            listenableFuture, Integer::parseInt, MoreExecutors.directExecutor());

    AtomicBoolean completed = new AtomicBoolean();
    completableFuture.whenComplete(
        (val, ex) -> {
          Assert.assertEquals(testException, ex);
          Assert.assertNull(val);
          completed.set(true);
        });
    listenableFuture.setException(testException);
    Assert.assertTrue(completed.get());
  }
}
