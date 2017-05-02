package tds.exam.results.services;

import java.util.UUID;

import tds.exam.results.tis.TISState;

/**
 * A service used for AMPQ messaging
 */
public interface MessagingService {
    /**
     * Sends a message to the exam report queue acknowledging that a response was received from TIS
     *
     * @param examId The id of the exam to report
     * @param state  The TIS response object
     */
    void sendReportAcknowledgement(final UUID examId, final TISState state);
}
