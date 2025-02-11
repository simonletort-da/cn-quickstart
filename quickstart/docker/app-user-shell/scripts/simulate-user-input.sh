#!/bin/bash
# Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: 0BSD

# Participant admin will need to provide AppProviderParty to initiate the app install request.
# This script acquires the party automatically base on the assumption that it operates in QS demo topology.
set -eo pipefail

source /app/utils.sh

get_app_provider_party() {
  LEDGER_API_ADMIN_USER_TOKEN_APP_PROVIDER=$(get_token $LEDGER_API_ADMIN_USER $AUTH_APP_PROVIDER_CLIENT_ID)
  get_user_party "$LEDGER_API_ADMIN_USER_TOKEN_APP_PROVIDER" $AUTH_APP_PROVIDER_CLIENT_ID participant-app-provider
}

APP_PROVIDER_PARTY=$(get_app_provider_party)

