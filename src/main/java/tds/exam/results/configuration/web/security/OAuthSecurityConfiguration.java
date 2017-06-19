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

package tds.exam.results.configuration.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

@EnableOAuth2Client
@Configuration
@EnableConfigurationProperties(OAuthConfigurationProperties.class)
public class OAuthSecurityConfiguration {
    private static final String GRANT_TYPE_PASSWORD = "password";

    @Autowired
    private OAuthConfigurationProperties properties;

    @Bean
    public OAuth2ProtectedResourceDetails resourceDetails() {
        final ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setClientAuthenticationScheme(AuthenticationScheme.form);
        resource.setAccessTokenUri(properties.getAccessUrl());
        resource.setClientId(properties.getTisClientId());
        resource.setClientSecret(properties.getTisClientSecret());
        resource.setGrantType(GRANT_TYPE_PASSWORD);
        resource.setUsername(properties.getTisUsername());
        resource.setPassword(properties.getTisPassword());
        return resource;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(60000);
        requestFactory.setReadTimeout(60000);
        return requestFactory;
    }

    @Bean
    public OAuth2RestOperations oauthRestTemplate() {
        final OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails());
        template.setRequestFactory(clientHttpRequestFactory());
        return template;
    }
}
