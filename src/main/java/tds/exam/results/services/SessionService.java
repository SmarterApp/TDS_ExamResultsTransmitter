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
