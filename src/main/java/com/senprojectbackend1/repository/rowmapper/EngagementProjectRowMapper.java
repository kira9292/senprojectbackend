package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.EngagementProject;
import com.senprojectbackend1.domain.enumeration.EngagementType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link EngagementProject}, with proper type conversions.
 */
@Service
public class EngagementProjectRowMapper implements BiFunction<Row, String, EngagementProject> {

    private final ColumnConverter converter;

    public EngagementProjectRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link EngagementProject} stored in the database.
     */
    @Override
    public EngagementProject apply(Row row, String prefix) {
        EngagementProject entity = new EngagementProject();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setType(converter.fromRow(row, prefix + "_type", EngagementType.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", String.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
