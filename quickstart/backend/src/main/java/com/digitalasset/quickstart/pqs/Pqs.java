// Copyright (c) 2025, Digital Asset (Switzerland) GmbH and/or its affiliates. All rights reserved.
// SPDX-License-Identifier: 0BSD

package com.digitalasset.quickstart.pqs;

import com.digitalasset.transcode.Converter;
import com.digitalasset.transcode.codec.json.JsonStringCodec;
import com.digitalasset.transcode.java.ContractId;
import com.digitalasset.transcode.java.Template;
import com.digitalasset.transcode.java.Utils;
import com.digitalasset.transcode.schema.Dictionary;
import com.digitalasset.transcode.schema.Identifier;
import daml.Daml;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class Pqs {
    private final JdbcTemplate jdbcTemplate;
    private final Dictionary<Converter<String, Object>> json2Dto;

    @Autowired
    public Pqs(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.json2Dto = Utils.getConverters(new JsonStringCodec(true, true), Daml.ENTITIES);
    }

    @WithSpan
    public <T extends Template> CompletableFuture<List<com.digitalasset.quickstart.pqs.Contract<T>>> active(Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "select contract_id, payload from active(?)";
            Identifier identifier = Utils.getTemplateIdByClass(clazz);
            List<com.digitalasset.quickstart.pqs.Contract<T>> results = jdbcTemplate.query(
                    sql,
                    new PqsContractRowMapper<>(identifier),
                    identifier.qualifiedName()
            );
            Span.current().setAttribute("backend.get.active.result.count", results.size());
            return results;
        });
    }

    /**
     * Fetch an active contract with a given WHERE clause.
     */
    @WithSpan
    public <T extends Template> CompletableFuture<Optional<Contract<T>>> singleActiveWhere(
            Class<T> clazz,
            String whereClause,
            Object... params
    ) {
        return CompletableFuture.supplyAsync(() -> {
            Identifier identifier = Utils.getTemplateIdByClass(clazz);
            String sql = "select contract_id, payload from active(?) where " + whereClause;
            List<com.digitalasset.quickstart.pqs.Contract<T>> results = jdbcTemplate.query(
                    sql,
                    new PqsContractRowMapper<>(identifier),
                    combineParams(identifier.qualifiedName(), params)
            );
            if (results.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(results.get(0));
            }
        });
    }

    private Object[] combineParams(String qname, Object... params) {
        Object[] combined = new Object[params.length + 1];
        combined[0] = qname;
        System.arraycopy(params, 0, combined, 1, params.length);
        return combined;
    }

    @WithSpan
    public <T extends Template> CompletableFuture<com.digitalasset.quickstart.pqs.Contract<T>> byContractId(Class<T> clazz, @SpanAttribute("backend.get.contract.id") String id) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "select contract_id, payload from lookup_contract(?)";
            return jdbcTemplate.<com.digitalasset.quickstart.pqs.Contract<T>>queryForObject(sql, new PqsContractRowMapper(Utils.getTemplateIdByClass(clazz)), id);
        });
    }

    private class PqsContractRowMapper<T extends Template> implements RowMapper<com.digitalasset.quickstart.pqs.Contract<T>> {
        private final Identifier templateId;

        public PqsContractRowMapper(Identifier templateId) {
            this.templateId = templateId;
        }

        @WithSpan
        @Override
        public com.digitalasset.quickstart.pqs.Contract<T> mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new com.digitalasset.quickstart.pqs.Contract<>(
                    new ContractId<>(rs.getString("contract_id")),
                    (T) json2Dto.template(templateId).convert(rs.getString("payload"))
            );
        }
    }
}