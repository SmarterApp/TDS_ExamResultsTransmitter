/***************************************************************************************************
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
 **************************************************************************************************/

package tds.exam.results.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.services.ScoringValidationStatusService;
import tds.support.job.JobUpdateRequest;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScoringValidationStatusServiceImplTest {
    private ScoringValidationStatusService service;

    @Mock
    private OAuth2RestOperations restTemplate;

    @Mock
    private RestTemplate unauthedRestTemplate;

    @Mock
    private ExamResultsTransmitterServiceProperties properties;

    @Before
    public void setup() {
        service = new ScoringValidationStatusServiceImpl(restTemplate, unauthedRestTemplate, properties);
    }

    @Test
    public void shouldUpateScoringValidationStatus() {
        final String jobId = "jobId";
        final JobUpdateRequest request = random(JobUpdateRequest.class);

        when(properties.getSupportToolUrl()).thenReturn("http://localhost:8080/api");
        service.updateScoringValidationStatus(jobId, request);
        verify(restTemplate).exchange(isA(URI.class), eq(HttpMethod.PUT), isA(HttpEntity.class), isA(ParameterizedTypeReference.class));
        verify(properties).getSupportToolUrl();
    }

    @Test(expected = RuntimeException.class)
    public void shouldRethrowRuntimeExceptionForHttpException() {
        final String jobId = "jobId";
        final JobUpdateRequest request = random(JobUpdateRequest.class);

        when(properties.getSupportToolUrl()).thenReturn("http://localhost:8080/api");
        when(restTemplate.exchange(isA(URI.class), eq(HttpMethod.PUT), isA(HttpEntity.class), isA(ParameterizedTypeReference.class)))
            .thenThrow(RestClientResponseException.class);
        service.updateScoringValidationStatus(jobId, request);
        verify(restTemplate).exchange(isA(URI.class), eq(HttpMethod.PUT), isA(HttpEntity.class), isA(ParameterizedTypeReference.class));
        verify(properties).getSupportToolUrl();
    }

    @Test(expected = RuntimeException.class)
    public void shouldUpateScoringValidationResults() {
        final String jobId = "jobId";
        final String trt = "<TDSReport/>";

        when(properties.getSupportToolUrl()).thenReturn("http://localhost:8080/api");
        when(unauthedRestTemplate.exchange(isA(URI.class), eq(HttpMethod.POST), isA(HttpEntity.class), isA(Class.class)))
            .thenThrow(RestClientResponseException.class);
        service.updateScoringValidationResults(jobId, trt);
        verify(unauthedRestTemplate).exchange(isA(URI.class), eq(HttpMethod.POST), isA(HttpEntity.class), isA(Class.class));
        verify(restTemplate, Mockito.times(0)).exchange(isA(URI.class), eq(HttpMethod.POST), isA(HttpEntity.class), isA(Class.class));
        verify(properties).getSupportToolUrl();
    }
}
