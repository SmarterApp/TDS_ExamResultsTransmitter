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

package tds.exam.results.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import tds.common.cache.CacheType;
import tds.exam.results.repositories.SessionRepository;
import tds.exam.results.services.SessionService;
import tds.session.ExternalSessionConfiguration;
import tds.session.Session;

@Service
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    @Autowired
    public SessionServiceImpl(final SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Session findSessionById(final UUID sessionId) {
        return sessionRepository.findSessionById(sessionId);
    }

    @Override
    @Cacheable(CacheType.LONG_TERM)
    public ExternalSessionConfiguration findExternalSessionConfigurationByClientName(final String clientName) {
        return sessionRepository.findExternalSessionConfigurationByClientName(clientName);
    }
}
