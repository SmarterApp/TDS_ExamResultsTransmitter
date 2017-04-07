package tds.exam.results.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "exam-results-transmitter-service")
public class ExamResultsTransmitterServiceProperties {
    private String sessionUrl = "";
    private String examUrl = "";
    private String assessmentUrl = "";

    /**
     * Get the URL for the session microservice.
     *
     * @return session microservice URL
     */
    public String getSessionUrl() {
        return sessionUrl;
    }

    public void setSessionUrl(final String sessionUrl) {
        if (sessionUrl == null) throw new IllegalArgumentException("sessionUrl cannot be null");
        this.sessionUrl = removeTrailingSlash(sessionUrl);
    }

    /**
     * Get the URL for the exam microservice.
     *
     * @return exam microservice URL
     */
    public String getExamUrl() {
        return examUrl;
    }

    public void setExamUrl(final String examUrl) {
        if (examUrl == null) throw new IllegalArgumentException("examUrl cannot be null");
        this.examUrl = removeTrailingSlash(examUrl);
    }

    /**
     * Get the URL for the assessment microservice.
     *
     * @return assessment microservice URL
     */
    public String getAssessmentUrl() {
        return assessmentUrl;
    }

    public void setAssessmentUrl(final String assessmentUrl) {
        if (assessmentUrl == null) throw new IllegalArgumentException("asssessmentUrl cannot be null");
        this.assessmentUrl = removeTrailingSlash(assessmentUrl);
    }

    private String removeTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        } else {
            return url;
        }
    }
}
