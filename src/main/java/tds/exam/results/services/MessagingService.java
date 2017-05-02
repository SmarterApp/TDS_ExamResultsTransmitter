package tds.exam.results.services;

import tds.exam.results.tis.TISState;

/**
 * A service used for AMPQ messaging
 */
public interface MessagingService {
    /**
     * Sends a message to the exam report queue acknowledging that a response was received from TIS
     *
     * @param state The TIS response object
     */
    void sendReportAcknowledgement(final TISState state);
}
