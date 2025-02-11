#!/bin/bash
# Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: 0BSD

set -eo pipefail

source /app/utils.sh

LEDGER_API_USER_TOKEN_APP_PROVIDER=$(get_token $LEDGER_API_ADMIN_USER $APP_PROVIDER_USER_ID)
LEDGER_API_USER_TOKEN_APP_USER=$(get_token $LEDGER_API_ADMIN_USER $APP_USER_USER_ID)

if [ ! -f /tmp/onboarding-dars-uploaded ]; then
  upload_dars "$LEDGER_API_USER_TOKEN_APP_PROVIDER" participant-app-provider
  upload_dars "$LEDGER_API_USER_TOKEN_APP_USER" participant-app-user
  touch /tmp/onboarding-dars-uploaded
fi

APP_PROVIDER_PARTY=$(allocate_party_and_create_user "$LEDGER_API_USER_TOKEN_APP_PROVIDER" $APP_PROVIDER_USER_ID participant-app-provider)
APP_USER_PARTY=$(allocate_party_and_create_user "$LEDGER_API_USER_TOKEN_APP_USER" $APP_USER_USER_ID participant-app-user)

# onboard users to wallets
ADMIN_USER_TOKEN_APP_PROVIDER=$(get_token $WALLET_ADMIN_USER $APP_PROVIDER_USER_ID)
ADMIN_USER_TOKEN_APP_USER=$(get_token $WALLET_ADMIN_USER $APP_USER_USER_ID)

onboard_wallet_user "$ADMIN_USER_TOKEN_APP_PROVIDER" $APP_PROVIDER_USER_ID $APP_PROVIDER_PARTY validator-app-provider
onboard_wallet_user "$ADMIN_USER_TOKEN_APP_USER" $APP_USER_USER_ID $APP_USER_PARTY validator-app-user
