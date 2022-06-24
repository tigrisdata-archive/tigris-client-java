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

package com.tigrisdata.db.client.search;

import com.tigrisdata.db.api.v1.grpc.Api;
import java.util.Objects;

/**
 * Representation of {@link Api.SearchHitMeta} exposing relevance information for the matched
 * document.
 */
public final class HitMeta {
  // TODO: Implement once API structure is finalized
  private static final HitMeta DEFAULT_INSTANCE = new HitMeta();

  static HitMeta from(Api.SearchHitMeta resp) {
    Objects.requireNonNull(resp);
    return DEFAULT_INSTANCE;
  }
}
