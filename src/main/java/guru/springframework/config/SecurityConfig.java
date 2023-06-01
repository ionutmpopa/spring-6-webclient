package guru.springframework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;

@Configuration
public class SecurityConfig {

    @Bean
    ReactiveOAuth2AuthorizedClientManager auth2AuthorizedClientManager(
        ReactiveClientRegistrationRepository clientRegistrationRepository,
        ReactiveOAuth2AuthorizedClientService auth2AuthorizedClientService) {

        ReactiveOAuth2AuthorizedClientProvider auth2AuthorizedClientProvider =
            ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
            new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository,
                auth2AuthorizedClientService
            );

        authorizedClientManager.setAuthorizedClientProvider(auth2AuthorizedClientProvider);

        return authorizedClientManager;
    }

}
