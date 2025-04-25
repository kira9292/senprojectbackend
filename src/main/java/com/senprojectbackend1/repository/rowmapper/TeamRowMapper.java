package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.enumeration.TeamVisibility;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Team}, with proper type conversions.
 */
@Service
public class TeamRowMapper implements BiFunction<Row, String, Team> {

    private final ColumnConverter converter;

    public TeamRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Team} stored in the database.
     */
    @Override
    public Team apply(Row row, String prefix) {
        Team entity = new Team();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setLogo(converter.fromRow(row, prefix + "_logo", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        entity.setVisibility(converter.fromRow(row, prefix + "_visibility", TeamVisibility.class));
        entity.setTotalLikes(converter.fromRow(row, prefix + "_total_likes", Integer.class));
        entity.setIsDeleted(converter.fromRow(row, prefix + "_is_deleted", Boolean.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastUpdatedBy(converter.fromRow(row, prefix + "_last_updated_by", String.class));
        return entity;
    }
}
