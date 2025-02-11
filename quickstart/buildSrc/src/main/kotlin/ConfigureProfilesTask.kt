// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ConfigureProfilesTask : DefaultTask() {

    data class Option(
        val promptText: String,
        val envVarName: String,
        var isSelected: Boolean = false,
        var isFound: Boolean = false
    )

    init {
        // Allow the task to read from standard input
        inputs.property("standardInput", System.`in`)
    }

    @TaskAction
    fun configure() {
        val options = listOf(
            Option(promptText = "Enable LocalNet", envVarName = "LOCALNET_ENABLED"),
            Option(promptText = "Enable Observability", envVarName = "OBSERVABILITY_ENABLED")
        )

        // Ask yes/no questions for each option, defaulting to yes
        options.forEach { option ->
            option.isSelected = promptForBoolean(option.promptText, default = true)
            println("  ${option.envVarName} set to '${option.isSelected}'.")
            println()
            System.out.flush()
        }

        // Update .env file in-place
        val rootProjectDir = project.rootProject.projectDir
        val dotEnvFile = File(rootProjectDir, ".env.local")

        if (!dotEnvFile.exists()) {
            dotEnvFile.createNewFile()
        }

        val envLines = dotEnvFile.readLines().toMutableList()

        // Process each line in the .env file
        for (i in envLines.indices) {
            val line = envLines[i]
            options.forEach { option ->
                if (line.startsWith("${option.envVarName}=")) {
                    envLines[i] = "${option.envVarName}=${option.isSelected}"
                    option.isFound = true
                }
            }
        }

        // Append variables if they were not found
        options.filter { !it.isFound }.forEach { option ->
            envLines.add("${option.envVarName}=${option.isSelected}")
        }

        // Write the updated lines back to the .env file
        dotEnvFile.writeText(envLines.joinToString(System.lineSeparator()))

        println(".env.local updated successfully.")
    }

    private fun promptForBoolean(prompt: String, default: Boolean): Boolean {
        val optionsText = if (default) "Y/n" else "y/N"
        while (true) {
            print("$prompt? ($optionsText): ")
            System.out.flush() // Flush the output to display the prompt immediately
            val input = readLine()
            if (input == null || input.trim().isEmpty()) {
                return default
            }
            val normalized = input.trim().lowercase()
            when (normalized) {
                "y", "yes", "true", "t", "1" -> return true
                "n", "no", "false", "f", "0" -> return false
                else -> {
                    println("Invalid input. Please enter 'yes' or 'no'.")
                    System.out.flush()
                }
            }
        }
    }
}
