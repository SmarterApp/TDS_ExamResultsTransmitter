package tds.exam.results.configuration.web.security;

import org.springframework.beans.factory.annotation.Value;
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
public class OAuthSecurityConfiguration {
    private static final String GRANT_TYPE_PASSWORD = "password";

    @Value("${oauth.access.url")
    private String accessUrl;

    @Value("${oauth.tis.client.id}")
    private String clientId;

    @Value("${oauth.tis.client.secret}")
    private String clientSecret;

    @Value("${oauth.tis.username}")
    private String username;

    @Value("${oauth.tis.password}")
    private String password;

    @Bean
    public OAuth2ProtectedResourceDetails resourceDetails() {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setClientAuthenticationScheme(AuthenticationScheme.form);
        resource.setAccessTokenUri(accessUrl);
        resource.setClientId(clientId);
        resource.setClientSecret(clientSecret);
        resource.setGrantType(GRANT_TYPE_PASSWORD);
        resource.setUsername(username);
        resource.setPassword(password);
        return resource;
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectionRequestTimeout(60000);
        requestFactory.setReadTimeout(60000);
        return requestFactory;
    }

    @Bean
    public OAuth2RestOperations oauthRestTemplate(final OAuth2ClientContext context) {
        OAuth2RestTemplate template = new OAuth2RestTemplate(resourceDetails(), context);
        //TODO: Do we need a custom factory? SRP factory has connectionTimeout = 20000 and readTimeout = 60000
        template.setRequestFactory(clientHttpRequestFactory());
        return template;
    }
}
