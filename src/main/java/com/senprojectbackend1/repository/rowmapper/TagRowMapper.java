package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.Tag;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Tag}, with proper type conversions.
 */
@Service
public class TagRowMapper implements BiFunction<Row, String, Tag> {

    private final ColumnConverter converter;

    public TagRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Tag} stored in the database.
     */
    @Override
    public Tag apply(Row row, String prefix) {
        Tag entity = new Tag();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setColor(converter.fromRow(row, prefix + "_color", String.class));
        entity.setIsForbidden(converter.fromRow(row, prefix + "_is_forbidden", Boolean.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        return entity;
    }
}
