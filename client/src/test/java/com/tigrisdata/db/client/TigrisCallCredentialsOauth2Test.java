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

import com.google.common.util.concurrent.MoreExecutors;
import io.grpc.CallCredentials;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.Executor;

public class TigrisCallCredentialsOauth2Test {

  @Test
  public void testRequestMetadataPreparation() {
    TokenService mockedTokenService = Mockito.mock(TokenService.class);
    Mockito.when(mockedTokenService.getAccessToken()).thenReturn("test-token");
    CallCredentials.RequestInfo mockedRequestInfo = Mockito.mock(CallCredentials.RequestInfo.class);
    CallCredentials.MetadataApplier mockedMetadataApplier =
        Mockito.mock(CallCredentials.MetadataApplier.class);
    TigrisCallCredentialOauth2 tigrisCallCredentialOauth2 =
        new TigrisCallCredentialOauth2(mockedTokenService);
    Executor executor = MoreExecutors.directExecutor();
    tigrisCallCredentialOauth2.applyRequestMetadata(
        mockedRequestInfo, executor, mockedMetadataApplier);
    Mockito.verify(mockedTokenService, Mockito.times(1)).getAccessToken();
  }
}
