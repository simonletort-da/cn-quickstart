// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

package com.digitalasset.quickstart.service;

import org.openapitools.model.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("${openapi.asset.base-path:}")
public class UserApiImpl implements com.digitalasset.quickstart.api.UserApi {

    @Override
    public CompletableFuture<ResponseEntity<AuthenticatedUser>> getAuthenticatedUser() {
        OAuth2AuthenticationToken auth = null;
        if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2AuthenticationToken) {
            auth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        }
        if (auth == null || !auth.isAuthenticated())
            return CompletableFuture.completedFuture(ResponseEntity.status(401).build());

        String party = auth.getPrincipal().getName();
        List<String> authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        AuthenticatedUser user = new AuthenticatedUser(
                party.split("::")[0],
                party,
                authorities,
                authorities.contains("ROLE_ADMIN")
        );
        return CompletableFuture.completedFuture(ResponseEntity.ok(user));
    }
}
