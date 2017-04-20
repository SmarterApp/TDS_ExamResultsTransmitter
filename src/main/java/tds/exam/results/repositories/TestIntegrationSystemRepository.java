package tds.exam.results.repositories;

import java.util.UUID;

/**
 * A repository for sending TRT reports to the remote Test Integration System
 */
public interface TestIntegrationSystemRepository {
    /**
     * Sends the TRT results XML to the Test Integration System
     *
     * @param examId  The exam id of the TRT
     * @param results The marshalled TRT XML blob
     */
    void sendResults(final UUID examId, final String results);
}
