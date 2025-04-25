package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.EngagementTeam;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link EngagementTeam}, with proper type conversions.
 */
@Service
public class EngagementTeamRowMapper implements BiFunction<Row, String, EngagementTeam> {

    private final ColumnConverter converter;

    public EngagementTeamRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link EngagementTeam} stored in the database.
     */
    @Override
    public EngagementTeam apply(Row row, String prefix) {
        EngagementTeam entity = new EngagementTeam();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLike(converter.fromRow(row, prefix + "_jhi_like", Integer.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setTeamId(converter.fromRow(row, prefix + "_team_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", String.class));
        return entity;
    }
}
