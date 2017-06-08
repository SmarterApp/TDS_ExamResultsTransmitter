package tds.exam.results.validation;

import tds.exam.results.trt.TDSReport;

/**
 * A validator responsible for JAXB validation
 */
public interface TDSReportValidator {
    /**
     * Validates the {@link tds.exam.results.trt.TDSReport} against the TRT xsd
     *
     * @param results
     */
    void validateReport(TDSReport results);
}
