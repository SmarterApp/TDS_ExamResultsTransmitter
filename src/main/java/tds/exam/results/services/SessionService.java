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

package tds.exam.results.services;

import java.util.UUID;

import tds.session.ExternalSessionConfiguration;
import tds.session.Session;

/**
 * Service for interacting with a remote session service
 */
public interface SessionService {
    /**
     * Finds a session for the given sessionId
     *
     * @param sessionId The id of the session to fetch
     * @return The {@link tds.session.Session} with the given id
     */
    Session findSessionById(final UUID sessionId);

    /**
     * Retrieves the extern by client name
     *
     * @param clientName the client name for the exam
     * @return The {@link tds.session.ExternalSessionConfiguration}
     */
    ExternalSessionConfiguration findExternalSessionConfigurationByClientName(final String clientName);
}
