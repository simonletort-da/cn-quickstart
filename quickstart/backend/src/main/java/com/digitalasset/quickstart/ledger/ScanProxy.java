// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

package com.digitalasset.quickstart.ledger;

import com.digitalasset.quickstart.validatorproxy.client.ApiException;
import com.digitalasset.quickstart.validatorproxy.client.api.ScanProxyApi;
import com.digitalasset.quickstart.validatorproxy.client.model.GetAmuletRulesProxyResponse;
import com.digitalasset.quickstart.validatorproxy.client.model.GetDsoPartyIdResponse;
import com.digitalasset.quickstart.validatorproxy.client.model.GetOpenAndIssuingMiningRoundsProxyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class ScanProxy {
    private final ScanProxyApi scanProxyApi;
    private final Logger logger = LoggerFactory.getLogger(ScanProxy.class);

    public ScanProxy(ScanProxyApi scanProxyApi) {
        this.scanProxyApi = scanProxyApi;
    }

    public CompletableFuture<GetDsoPartyIdResponse> getDsoPartyId() {
        try {
            return scanProxyApi.getDsoPartyId()
                    .exceptionally(ex -> {
                        logger.error("Error fetching DSO party id", ex);
                        throw new RuntimeException(ex);
                    });
        } catch (ApiException e) {
            throw new RuntimeException(e); // cannot happen - OpenAPI codegen adds false checked `throws` declaration
        }
    }

    public CompletableFuture<GetAmuletRulesProxyResponse> getAmuletRules() {
        try {
            return scanProxyApi.getAmuletRules()
                    .exceptionally(ex -> {
                        logger.error("Error fetching AmuletRules", ex);
                        throw new RuntimeException(ex);
                    });
        } catch (ApiException e) {
            throw new RuntimeException(e); // cannot happen - OpenAPI codegen adds false checked `throws` declaration
        }
    }

    public CompletableFuture<GetOpenAndIssuingMiningRoundsProxyResponse> getOpenAndIssuingMiningRounds() {
        try {
            return scanProxyApi.getOpenAndIssuingMiningRounds()
                    .exceptionally(ex -> {
                        logger.error("Error fetching Open and Issuing MiningRounds", ex);
                        throw new RuntimeException(ex);
                    });
        } catch (ApiException e) {
            throw new RuntimeException(e); // cannot happen - OpenAPI codegen adds false checked `throws` declaration
        }
    }
}
