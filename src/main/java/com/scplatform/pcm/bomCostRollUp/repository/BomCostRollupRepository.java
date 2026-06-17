/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 */
package com.scplatform.pcm.bomCostRollUp.repository;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.scplatform.pcm.bomCostRollUp.constants.BomCostRollupConstants.*;

@Log4j2
@Repository
public class BomCostRollupRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final BomCostRollupProcedureRepository procedureRepository;
    private final BomCostRollupRepository self;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public BomCostRollupRepository(NamedParameterJdbcTemplate jdbcTemplate,
                                   BomCostRollupProcedureRepository procedureRepository,
                                   @Lazy BomCostRollupRepository self) {
        this.jdbcTemplate        = jdbcTemplate;
        this.procedureRepository = procedureRepository;
        this.self                = self;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true, noRollbackFor = Exception.class)
    public int getBomCostRollupStatus(Long bomKey, Date effectiveFromDate) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(P_BOM_KEY,  bomKey)
                .addValue(P_EFF_DATE, effectiveFromDate);

        try {
            BigDecimal status = jdbcTemplate.queryForObject(SQL_SELECT_ROLLUP_STATUS, params, BigDecimal.class);
            return status != null ? status.intValue() : STATUS_NO_RECORD;
        } catch (EmptyResultDataAccessException e) {
            return STATUS_NO_RECORD;
        } catch (Exception e) {
            log.warn("Could not fetch rollup status for bomKey={}, effDate={}", bomKey, effectiveFromDate, e);
            return STATUS_ERROR;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true, noRollbackFor = Exception.class)
    public String getBomRollupDataFromTempTableAsJson(Long bomKey, Long userKey, Date fromDate) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(P_BOM_KEY,  bomKey)
                .addValue(P_USER_KEY, userKey);

        try {
            List<Map<String, Object>> rows =
                    jdbcTemplate.query(SQL_SELECT_ROLLUP_ROWS, params, (rs, rowNum) -> mapRollupRow(rs));

            ObjectNode root = objectMapper.createObjectNode();
            root.set("DATA", objectMapper.valueToTree(rows));
            String response = objectMapper.writeValueAsString(root);
            log.debug("BOM rollup temp-table response for bomKey={}, userKey={}: {} row(s)",
                    bomKey, userKey, rows.size());
            return response;
        } catch (Exception e) {
            log.error("Error reading BOM rollup data for bomKey={}, userKey={}", bomKey, userKey, e);
            return EMPTY_DATA_JSON;
        }
    }

    private Map<String, Object> mapRollupRow(ResultSet rs) throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>(ROLLUP_ROW_FIELD_COUNT);
        map.put(COL_ITEM_NAME,                rs.getObject(COL_ITEM_NAME));
        map.put(COL_ITEM_ROLLUP_PRICE,        rs.getObject(COL_ITEM_ROLLUP_PRICE));
        map.put(COL_ITEM_PART_NAME,           rs.getObject(COL_ITEM_PART_NAME));
        map.put(COL_ITEM_PART_SELLING_PRICE,  rs.getObject(COL_ITEM_PART_SELLING_PRICE));
        map.put(COL_ITEM_PART_ROLLUP_PRICE,   rs.getObject(COL_ITEM_PART_ROLLUP_PRICE));
        map.put(COL_ITEM_PART_TOTAL_PRICE,    rs.getObject(COL_ITEM_PART_TOTAL_PRICE));
        map.put(COL_ITEM_PART_QTY,            rs.getObject(COL_ITEM_PART_QTY));
        map.put(COL_DIRECT_MATERIAL,          rs.getObject(COL_DIRECT_MATERIAL));
        map.put(COL_SHARING_COST,             rs.getObject(COL_SHARING_COST));
        map.put(COL_DIRECT_LABOR,             rs.getObject(COL_DIRECT_LABOR));
        map.put(COL_ITEM_VA_COST,             rs.getObject(COL_ITEM_VA_COST));
        map.put(COL_DIRECT_LABOR2,            rs.getObject(COL_DIRECT_LABOR2));
        map.put(COL_INDIRECT_LABOR,           rs.getObject(COL_INDIRECT_LABOR));
        map.put(COL_MACHINE_EQUIPMENT,        rs.getObject(COL_MACHINE_EQUIPMENT));
        map.put(COL_MATERIAL_HANDLING,        rs.getObject(COL_MATERIAL_HANDLING));
        map.put(COL_MATERIAL_SCRAP,           rs.getObject(COL_MATERIAL_SCRAP));
        map.put(COL_FREIGHT,                  rs.getObject(COL_FREIGHT));
        map.put(COL_SGA,                      rs.getObject(COL_SGA));
        map.put(COL_FINANCIAL_RECEIVABLES,    rs.getObject(COL_FINANCIAL_RECEIVABLES));
        map.put(COL_PROFIT_MARGIN,            rs.getObject(COL_PROFIT_MARGIN));
        map.put(COL_ADJUSTMENTS_REDUCTIONS,   rs.getObject(COL_ADJUSTMENTS_REDUCTIONS));
        map.put(COL_MISCELLANEOUS,            rs.getObject(COL_MISCELLANEOUS));
        map.put(COL_TARIFF,                   rs.getObject(COL_TARIFF));
        map.put(COL_BOM_KEY,                  rs.getObject(COL_BOM_KEY));
        map.put(COL_CREATED_ON,               rs.getTimestamp(COL_CREATED_ON));
        map.put(COL_CURRENCY_CONVERSION_ERROR_MSG,
                clobToString(rs.getClob(COL_CURRENCY_CONVERSION_ERROR_MSG)));
        return map;
    }

    private String clobToString(Clob clob) {
        if (clob == null) {
            return "";
        }
        try (Reader reader = clob.getCharacterStream()) {
            StringBuilder sb = new StringBuilder();
            char[] buf = new char[4096];
            int read;
            while ((read = reader.read(buf)) != -1) {
                sb.append(buf, 0, read);
            }
            return sb.toString();
        } catch (SQLException | IOException e) {
            log.warn("Failed to read CURRENCY_CONVERSION_ERROR_MSG CLOB", e);
            return "";
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public boolean getBomRollupData(Long bomKey, Long userKey, Date effectiveDate) {
        try {
            procedureRepository.runBomHierarchyWithCost(bomKey, userKey, effectiveDate);
            return true;
        } catch (Exception e) {
            log.error("Failed executing {} for bomKey={}", PROC_NAME, bomKey, e);
            return false;
        }
    }
    public String getRollupData(Long bomKey, Long userKey, Date effectiveDate) {
        if (!self.getBomRollupData(bomKey, userKey, effectiveDate)) {
            return EMPTY_DATA_JSON;
        }
        return self.getBomRollupDataFromTempTableAsJson(bomKey, userKey, effectiveDate);
    }
}
