package tds.exam.results.repositories;

import java.util.UUID;

import tds.session.ExternalSessionConfiguration;
import tds.session.Session;

/**
 * A repository used for fetching {@link tds.session.Session} data
 */
public interface SessionRepository {
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
