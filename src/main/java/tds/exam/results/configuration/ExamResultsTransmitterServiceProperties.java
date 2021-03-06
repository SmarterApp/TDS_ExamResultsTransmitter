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

package tds.exam.results.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "exam-results-transmitter-service")
public class ExamResultsTransmitterServiceProperties {
    private String sessionUrl = "";
    private String examUrl = "";
    private String supportToolUrl = "";
    private String assessmentUrl = "";
    private String tisUrl = "";
    private String tisCallbackUrl = "";
    private boolean validateTrtXml = false;
    private boolean sendToTis = false;
    private long retryInitialInterval = 1000;
    private double retryIntervalMultiplier = 2;
    private long retryMaxInterval = 5000;
    private int retryAmount = 3;

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

    /**
     * Gets the URL for the Test Integration System
     *
     * @return the TIS url
     */
    public String getTisUrl() {
        return tisUrl;
    }

    public void setTisUrl(final String tisUrl) {
        this.tisUrl = tisUrl;
    }

    /**
     * Gets the callback url for the Test Integration System
     *
     * @return the TIS callback URL
     */
    public String getTisCallbackUrl() {
        return tisCallbackUrl;
    }

    public void setTisCallbackUrl(final String tisCallbackUrl) {
        this.tisCallbackUrl = tisCallbackUrl;
    }

    /**
     * Get the URL for the support tool microservice.
     *
     * @return support tool microservice URL
     */
    public String getSupportToolUrl() {
        return supportToolUrl;
    }

    public void setSupportToolUrl(final String supportToolUrl) {
        if (supportToolUrl == null) throw new IllegalArgumentException("supportToolUrl cannot be null");
        this.supportToolUrl = removeTrailingSlash(supportToolUrl);
    }

    private String removeTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        } else {
            return url;
        }
    }

    /**
     * @return {@code true} to validate XML
     */
    public boolean isValidateTrtXml() {
        return validateTrtXml;
    }

    public void setValidateTrtXml(final boolean validateTrtXml) {
        this.validateTrtXml = validateTrtXml;
    }

    /**
     * @return {@code true} to send to TIS
     */
    public boolean isSendToTis() {
        return sendToTis;
    }

    public void setSendToTis(boolean sendToTis) {
        this.sendToTis = sendToTis;
    }

    /**
     * @return the initial interval retry time that will be used as we graduate to larger numbers between retries
     */
    public long getRetryInitialInterval() {
        return retryInitialInterval;
    }

    public void setRetryInitialInterval(final long retryInitialInterval) {
        this.retryInitialInterval = retryInitialInterval;
    }

    /**
     * @return the interval between retries
     */
    public double getRetryIntervalMultiplier() {
        return retryIntervalMultiplier;
    }

    public void setRetryIntervalMultiplier(final double retryIntervalMultiplier) {
        this.retryIntervalMultiplier = retryIntervalMultiplier;
    }

    /**
     * @return the max interval the retry will scale to
     */
    public long getRetryMaxInterval() {
        return retryMaxInterval;
    }

    public void setRetryMaxInterval(final long retryMaxInterval) {
        this.retryMaxInterval = retryMaxInterval;
    }

    /**
     * @return the number of times a message will be retried
     */
    public int getRetryAmount() {
        return retryAmount;
    }

    public void setRetryAmount(final int retryAmount) {
        this.retryAmount = retryAmount;
    }
}
