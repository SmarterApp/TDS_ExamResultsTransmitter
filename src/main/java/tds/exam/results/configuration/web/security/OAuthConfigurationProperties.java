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

package tds.exam.results.configuration.web.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth")
public class OAuthConfigurationProperties {
    private String accessUrl = "";
    private String tisClientId = "";
    private String tisClientSecret = "";
    private String tisUsername = "";
    private String tisPassword = "";

    /**
     * @return The SSO access url
     */
    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(final String accessUrl) {
        this.accessUrl = accessUrl;
    }

    /**
     * @return The TIS OAuth client id
     */
    public String getTisClientId() {
        return tisClientId;
    }

    public void setTisClientId(final String tisClientId) {
        this.tisClientId = tisClientId;
    }

    /**
     * @return The TIS OAuth client secret
     */
    public String getTisClientSecret() {
        return tisClientSecret;
    }

    public void setTisClientSecret(final String tisClientSecret) {
        this.tisClientSecret = tisClientSecret;
    }

    /**
     * @return The TIS admin username to use with OAuth
     */
    public String getTisUsername() {
        return tisUsername;
    }

    public void setTisUsername(final String tisUsername) {
        this.tisUsername = tisUsername;
    }

    /**
     * @return The TIS admin password to use with OAuth
     */
    public String getTisPassword() {
        return tisPassword;
    }

    public void setTisPassword(final String tisPassword) {
        this.tisPassword = tisPassword;
    }
}
