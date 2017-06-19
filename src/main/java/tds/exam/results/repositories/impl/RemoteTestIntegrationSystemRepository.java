/*******************************************************************************
 * Copyright 2016 Smarter Balance Licensed under the
 *     Educational Community License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may
 *     obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an "AS IS"
 *     BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing
 *     permissions and limitations under the License.
 ******************************************************************************/

package tds.exam.results.repositories.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.repositories.TestIntegrationSystemRepository;

@Repository
public class RemoteTestIntegrationSystemRepository implements TestIntegrationSystemRepository {
    private static final Logger log = LoggerFactory.getLogger(RemoteTestIntegrationSystemRepository.class);
    private final OAuth2RestOperations restTemplate;
    private final ExamResultsTransmitterServiceProperties properties;

    @Autowired
    public RemoteTestIntegrationSystemRepository(final OAuth2RestOperations restTemplate,
                                                 final ExamResultsTransmitterServiceProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public void sendResults(final UUID examId, final String results) {
        if (!properties.isSendToTis()) {
            log.info("SendToTIS configuration property is false resulting in TIS XML not sent: " + results);
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<?> requestHttpEntity = new HttpEntity<>(results, headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/api/testresult", properties.getTisUrl()))
            .queryParam("statusCallback", properties.getTisCallbackUrl());

        try {
            restTemplate.exchange(
                builder.build().toUri(),
                HttpMethod.POST,
                requestHttpEntity,
                String.class);
        } catch (final HttpStatusCodeException e) {
            throw new RuntimeException(String.format("Unable to send test result to TIS: %s", e.getResponseBodyAsString()), e);
        }
    }
}
