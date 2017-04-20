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
    public OAuth2RestOperations oauthRestTemplate(final OAuth2ClientContext context) {
        final OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails(), context);
        template.setRequestFactory(clientHttpRequestFactory());
        return template;
    }
}
