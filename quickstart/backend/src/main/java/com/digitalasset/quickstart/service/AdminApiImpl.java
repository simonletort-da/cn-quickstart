// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

package com.digitalasset.quickstart.service;

import com.digitalasset.quickstart.oauth.OAuth2ClientRegistrationRepository;
import org.openapitools.model.OAuth2ClientRegistration;
import org.openapitools.model.OAuth2ClientRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Controller
@RequestMapping("${openapi.asset.base-path:}")
public class AdminApiImpl implements com.digitalasset.quickstart.api.Oauth2Api {
    private final OAuth2ClientRegistrationRepository clientRegistrationRepository;


    @Autowired
    public AdminApiImpl(OAuth2ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Override
    public CompletableFuture<ResponseEntity<OAuth2ClientRegistration>> createClientRegistration(OAuth2ClientRegistrationRequest request) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(request.getClientId())
                .clientId(request.getClientId())
                .clientSecret(request.getClientSecret())
                .authorizationUri(request.getAuthorizationUri())
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .tokenUri(request.getTokenUri())
                .jwkSetUri(request.getJwkSetUri())
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope(request.getScope())
                .clientName(request.getParty())
                .build();

        clientRegistrationRepository.addRegistration(clientRegistration);
        OAuth2ClientRegistration result = new OAuth2ClientRegistration(
                clientRegistration.getClientId(),
                clientRegistration.getProviderDetails().getAuthorizationUri(),
                clientRegistration.getProviderDetails().getTokenUri(),
                clientRegistration.getProviderDetails().getJwkSetUri(),
                String.join(" ", clientRegistration.getScopes()),
                clientRegistration.getClientName());

        return CompletableFuture.completedFuture(ResponseEntity.ok(result));
    }

    @Override
    public CompletableFuture<ResponseEntity<Void>> deleteClientRegistration(String clientId) {
        clientRegistrationRepository.removeRegistration(clientId);
        return CompletableFuture.completedFuture(ResponseEntity.ok().build());
    }

    @Override
    public CompletableFuture<ResponseEntity<List<OAuth2ClientRegistration>>> listClientRegistrations() {
        List<OAuth2ClientRegistration> registrations = clientRegistrationRepository.getRegistrations().stream()
                .filter(registration -> AuthorizationGrantType.AUTHORIZATION_CODE.equals(registration.getAuthorizationGrantType()))
                .map(registration -> {
                    OAuth2ClientRegistration oauth2ClientRegistration = new OAuth2ClientRegistration();
                    oauth2ClientRegistration.setClientId(registration.getClientId());
                    oauth2ClientRegistration.setAuthorizationUri(registration.getProviderDetails().getAuthorizationUri());
                    oauth2ClientRegistration.setTokenUri(registration.getProviderDetails().getTokenUri());
                    oauth2ClientRegistration.setJwkSetUri(registration.getProviderDetails().getJwkSetUri());
                    oauth2ClientRegistration.setScope(String.join(" ", registration.getScopes()));
                    oauth2ClientRegistration.setParty(registration.getClientName());
                    oauth2ClientRegistration.setPreconfigured(registration.getProviderDetails().getConfigurationMetadata().containsKey("preconfigured"));
                    return oauth2ClientRegistration;
                })
                .collect(Collectors.toList());
        return CompletableFuture.completedFuture(ResponseEntity.ok(registrations));
    }
}
