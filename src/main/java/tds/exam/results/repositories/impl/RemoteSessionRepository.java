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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

import tds.exam.results.configuration.ExamResultsTransmitterServiceProperties;
import tds.exam.results.repositories.SessionRepository;
import tds.session.ExternalSessionConfiguration;
import tds.session.Session;

import static tds.exam.results.configuration.SupportApplicationConfiguration.SESSION_APP_CONTEXT;

@Repository
public class RemoteSessionRepository implements SessionRepository {
    private final RestTemplate restTemplate;
    private final ExamResultsTransmitterServiceProperties properties;

    @Autowired
    public RemoteSessionRepository(final RestTemplate restTemplate, final ExamResultsTransmitterServiceProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @Override
    public Session findSessionById(final UUID sessionId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/%s/%s",
            properties.getSessionUrl(),
            SESSION_APP_CONTEXT,
            sessionId));

        return restTemplate.getForObject(builder.build().toUri(), Session.class);
    }

    @Override
    public ExternalSessionConfiguration findExternalSessionConfigurationByClientName(final String clientName) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format("%s/%s/external-config/%s",
            properties.getSessionUrl(),
            SESSION_APP_CONTEXT,
            clientName));

        return restTemplate.getForObject(builder.build().toUri(), ExternalSessionConfiguration.class);
    }
}
