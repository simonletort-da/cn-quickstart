// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

package com.digitalasset.quickstart.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final String appProviderTokenUri;

    public OAuth2AuthenticationSuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
        appProviderTokenUri = getVariable("AUTH_APP_PROVIDER_TOKEN_URI");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken auth))
            throw new IllegalArgumentException("Authentication must be an instance of OAuth2AuthenticationToken");

        ClientRegistration clientReg = authorizedClientService.loadAuthorizedClient(auth.getAuthorizedClientRegistrationId(), auth.getName()).getClientRegistration();

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (appProviderTokenUri.equals(clientReg.getProviderDetails().getTokenUri())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                new DefaultOAuth2User(authorities, Map.of("principal", clientReg.getClientName()), "principal"),
                authorities,
                auth.getAuthorizedClientRegistrationId()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        // workaround spring security bug that doesn't set csrf token on oauth2 success
        // see https://github.com/spring-projects/spring-security/issues/12141#issuecomment-1321215874
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        csrfToken.getToken();
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.onAuthenticationSuccess(request, response, newAuth);
    }

    private String getVariable(String name) {
        String value = System.getenv(name);
        if (value == null) {
            throw new IllegalStateException("Environment variable " + name + " was not set");
        }
        return value;
    }
}
