#!/bin/bash
# Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: 0BSD

set -eo pipefail

source /app/utils.sh

LEDGER_API_USER_TOKEN_APP_PROVIDER=$(get_token $LEDGER_API_ADMIN_USER $AUTH_APP_PROVIDER_CLIENT_ID)
LEDGER_API_USER_TOKEN_APP_USER=$(get_token $LEDGER_API_ADMIN_USER $AUTH_APP_USER_CLIENT_ID)
export AUTH_APP_PROVIDER_PARTY=$(get_user_party "$LEDGER_API_USER_TOKEN_APP_PROVIDER" $AUTH_APP_PROVIDER_CLIENT_ID participant-app-provider)
export AUTH_APP_USER_PARTY=$(get_user_party "$LEDGER_API_USER_TOKEN_APP_USER" $AUTH_APP_USER_CLIENT_ID participant-app-user)

tar -xf /backend.tar -C /opt
/opt/backend/bin/backend