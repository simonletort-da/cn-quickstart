// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

package com.digitalasset.quickstart.repository;

import com.digitalasset.quickstart.pqs.Contract;
import com.digitalasset.quickstart.pqs.Pqs;
import quickstart_licensing.licensing.appinstall.AppInstall;
import quickstart_licensing.licensing.appinstall.AppInstallRequest;
import quickstart_licensing.licensing.license.License;
import quickstart_licensing.licensing.license.LicenseRenewalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import splice_wallet_payments.splice.wallet.payment.AcceptedAppPayment;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A single repository class that wraps PQS queries with typed methods.
 */
@Repository
public class DamlRepository {

    private final Pqs pqs;

    @Autowired
    public DamlRepository(Pqs pqs) {
        this.pqs = pqs;
    }

    // ------------------------------------------------------------------------
    // LicenseRenewalRequest queries
    // ------------------------------------------------------------------------

    /**
     * Find all active LicenseRenewalRequest contracts.
     */
    public CompletableFuture<List<Contract<LicenseRenewalRequest>>> findActiveLicenseRenewalRequests() {
        return pqs.active(LicenseRenewalRequest.class);
    }

    /**
     * Fetch a LicenseRenewalRequest contract by contract ID.
     */
    public CompletableFuture<Contract<LicenseRenewalRequest>> findLicenseRenewalRequestById(String contractId) {
        return pqs.byContractId(LicenseRenewalRequest.class, contractId);
    }

    // ------------------------------------------------------------------------
    // License queries
    // ------------------------------------------------------------------------

    /**
     * Find all active License contracts.
     */
    public CompletableFuture<List<Contract<License>>> findActiveLicenses() {
        return pqs.active(License.class);
    }

    /**
     * Fetch a License contract by contract ID.
     */
    public CompletableFuture<Contract<License>> findLicenseById(String contractId) {
        return pqs.byContractId(License.class, contractId);
    }

    /**
     * Fetch a single active License that matches user, provider, licenseNum, and dso.
     */
    public CompletableFuture<Optional<Contract<License>>> findSingleActiveLicense(
            String user,
            String provider,
            Long licenseNum,
            String dso
    ) {
        final String whereClause =
                "payload->>'user' = ? "
                        + "AND payload->>'provider' = ? "
                        + "AND (payload->>'licenseNum')::int = ? "
                        + "AND payload->>'dso' = ?";
        return pqs.singleActiveWhere(
                License.class,
                whereClause,
                user,
                provider,
                licenseNum,
                dso
        );
    }

    // ------------------------------------------------------------------------
    // AcceptedAppPayment queries
    // ------------------------------------------------------------------------

    /**
     * Fetch a single active AcceptedAppPayment matching a given reference, user, and provider.
     */
    public CompletableFuture<Optional<Contract<AcceptedAppPayment>>> findSingleActiveAcceptedAppPayment(
            String referenceCid,
            String user,
            String provider
    ) {
        final String whereClause =
                "payload->>'reference' = ? "
                        + "AND payload->>'sender' = ? "
                        + "AND payload->>'provider' = ?";
        return pqs.singleActiveWhere(
                AcceptedAppPayment.class,
                whereClause,
                referenceCid,
                user,
                provider
        );
    }

    // ------------------------------------------------------------------------
    // AppInstall queries
    // ------------------------------------------------------------------------

    /**
     * Fetch all active AppInstall contracts.
     */
    public CompletableFuture<List<Contract<AppInstall>>> findActiveAppInstalls() {
        return pqs.active(AppInstall.class);
    }

    /**
     * Fetch an AppInstall contract by contract ID.
     */
    public CompletableFuture<Contract<AppInstall>> findAppInstallById(String contractId) {
        return pqs.byContractId(AppInstall.class, contractId);
    }

    // ------------------------------------------------------------------------
    // AppInstallRequest queries
    // ------------------------------------------------------------------------

    /**
     * Fetch all active AppInstallRequest contracts.
     */
    public CompletableFuture<List<Contract<AppInstallRequest>>> findActiveAppInstallRequests() {
        return pqs.active(AppInstallRequest.class);
    }

    /**
     * Fetch an AppInstallRequest contract by contract ID.
     */
    public CompletableFuture<Contract<AppInstallRequest>> findAppInstallRequestById(String contractId) {
        return pqs.byContractId(AppInstallRequest.class, contractId);
    }
}
