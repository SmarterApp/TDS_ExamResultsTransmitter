/*******************************************************************************
 * Copyright 2017 Regents of the University of California. Licensed under the Educational
 * Community License, Version 2.0 (the “license”); you may not use this file except in
 * compliance with the License. You may obtain a copy of the license at
 *
 * https://opensource.org/licenses/ECL-2.0
 *
 * Unless required under applicable law or agreed to in writing, software distributed under the
 * License is distributed in an “AS IS” BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for specific language governing permissions
 * and limitations under the license.
 *
 ******************************************************************************/

package tds.exam.results.repositories.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2RestOperations;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.repositories.TestIntegrationSystemRepository;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RemoteTestIntegrationSystemRepositoryTest {
    private TestIntegrationSystemRepository testIntegrationSystemRepository;

    @Mock
    private OAuth2RestOperations mockRestTemplate;

    @Mock
    private ExamResultsTransmitterServiceProperties mockProperties;

    @Before
    public void setUp() {
        testIntegrationSystemRepository = new RemoteTestIntegrationSystemRepository(mockRestTemplate, mockProperties);
    }

    @Test
    public void shouldSendResultsToTisWithNoJobIdOmitsValidate() {
        final UUID examId = UUID.randomUUID();
        final String results = "TestResults";

        when(mockProperties.getTisUrl()).thenReturn("http://localhost:1234");
        when(mockProperties.getTisCallbackUrl()).thenReturn("http://localhost:1235");
        when(mockProperties.isSendToTis()).thenReturn(true);
        testIntegrationSystemRepository.sendResults(examId, results, Optional.empty());

        verify(mockRestTemplate).exchange(
            Matchers.eq(URI.create("http://localhost:1234/api/testresult?statusCallback=http://localhost:1235")),
            isA(HttpMethod.class), isA(HttpEntity.class), isA(Class.class));
    }

    @Test
    public void shouldSendResultsToTisWithJobIdSetsValidate() {
        final UUID examId = UUID.randomUUID();
        final String results = "TestResults";

        when(mockProperties.getTisUrl()).thenReturn("http://localhost:1234");
        when(mockProperties.getTisCallbackUrl()).thenReturn("http://localhost:1235");
        when(mockProperties.isSendToTis()).thenReturn(true);
        testIntegrationSystemRepository.sendResults(examId, results, Optional.of("id"));

        verify(mockRestTemplate).exchange(
            Matchers.eq(URI.create("http://localhost:1234/api/testresult?scoreMode=validate&statusCallback=http://localhost:1235?jobid=id")),
            isA(HttpMethod.class), isA(HttpEntity.class), isA(Class.class));
    }

    @Test
    public void shouldNotSendResultsToTis() {
        final UUID examId = UUID.randomUUID();
        final String results = "TestResults";

        when(mockProperties.getTisUrl()).thenReturn("http://localhost:1234");
        when(mockProperties.isSendToTis()).thenReturn(false);
        testIntegrationSystemRepository.sendResults(examId, results, Optional.empty());

        verify(mockRestTemplate, never()).exchange(isA(URI.class), isA(HttpMethod.class), isA(HttpEntity.class), isA(Class.class));
    }
}
