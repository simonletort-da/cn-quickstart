// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

def main() {
  val dars = decode[Seq[String]](sys.env.get("SPLICE_APP_DARS").getOrElse("[]")).getOrElse(
    sys.error("Failed to parse dars list")
  )

  logger.info("Waiting for validator to finish init...")
  logger.debug(s"Loaded environment: ${sys.env}")
  validator_backend.waitForInitialization(10.minutes)

  logger.info(s"Uploading DARs: ${dars}")
  dars.foreach(validator_backend.participantClient.upload_dar_unless_exists(_))
}