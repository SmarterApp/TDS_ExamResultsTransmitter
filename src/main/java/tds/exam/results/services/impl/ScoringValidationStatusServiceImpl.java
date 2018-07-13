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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import tds.common.web.resources.NoContentResponseResource;
import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.services.ScoringValidationStatusService;
import tds.support.job.JobUpdateRequest;

@Service
public class ScoringValidationStatusServiceImpl implements ScoringValidationStatusService {
    private static final Logger log = LoggerFactory.getLogger(ScoringValidationStatusServiceImpl.class);
    private final OAuth2RestOperations restTemplate;
    private final ExamResultsTransmitterServiceProperties properties;

    @Autowired
    public ScoringValidationStatusServiceImpl(final OAuth2RestOperations restTemplate,
                                              final ExamResultsTransmitterServiceProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public void updateScoringValidationStatus(final String jobId, final JobUpdateRequest request) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);

        final HttpEntity<JobUpdateRequest> requestHttpEntity = new HttpEntity<>(request, headers);

        final UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/scoring/%s",
            properties.getSupportToolUrl(), jobId));

        try {
            restTemplate.exchange(
                builder.build().toUri(),
                HttpMethod.PUT,
                requestHttpEntity,
                new ParameterizedTypeReference<NoContentResponseResource>() {
                });
        } catch (final HttpStatusCodeException e) {
            log.error("Unable to update scoring job status for the job {}", jobId, e);
            throw new RuntimeException(String.format("Unable to update support tool job status: %s", e.getResponseBodyAsString()), e);
        }
    }
}
