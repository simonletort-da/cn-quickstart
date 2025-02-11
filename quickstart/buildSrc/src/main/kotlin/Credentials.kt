// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

import org.gradle.api.Action
import org.gradle.api.artifacts.repositories.PasswordCredentials
import java.io.File

object Credentials {
    fun fromNetRc(domain: String): Action<PasswordCredentials> {
        val file = File("${System.getProperty("user.home")}/.netrc")
        if (!file.exists()) {
            throw Exception("~/.netrc file not found")
        }
        val lines = file.readLines(Charsets.UTF_8)
        var isTargetMachine = false
        var username: String? = null
        var password: String? = null

        for (line in lines) {
            val tokens = line.trim().split("\\s+".toRegex(), limit=2)
            if (tokens.isEmpty()) continue
            val value = tokens.getOrNull(1)

            when (tokens[0]) {
                "machine" -> {
                    isTargetMachine = value == domain
                    username = null
                    password = null
                }
                "login" -> if (isTargetMachine) {
                    username = value
                }
                "password" -> if (isTargetMachine) {
                    password = value
                }
            }

            if (isTargetMachine && username != null && password != null) {
                return Action {
                    this.username = username
                    this.password = password
                }
            }
        }
        throw Exception("Can't find complete entry for domain $domain in ~/.netrc")
    }
}
