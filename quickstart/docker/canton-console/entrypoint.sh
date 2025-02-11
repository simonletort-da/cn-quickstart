#!/bin/bash
# Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
# SPDX-License-Identifier: 0BSD

set -eo pipefail

export ACCESS_TOKEN=$(curl -X POST "${AUTH_APP_PROVIDER_TOKEN_URI}" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=${LEDGER_API_ADMIN_USER}" \
  -d "client_secret=${LEDGER_API_ADMIN_SECRET}" \
  -d "grant_type=client_credentials" \
  -d "scope=daml_ledger_api" | tr -d '\n' | grep -o -E '"access_token"[[:space:]]*:[[:space:]]*"[^"]+' | grep -o -E '[^"]+$')

  /app/bin/canton --no-tty -c /app/app.conf
