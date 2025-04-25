package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.ExternalLink;
import com.senprojectbackend1.domain.enumeration.LinkType;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ExternalLink}, with proper type conversions.
 */
@Service
public class ExternalLinkRowMapper implements BiFunction<Row, String, ExternalLink> {

    private final ColumnConverter converter;

    public ExternalLinkRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ExternalLink} stored in the database.
     */
    @Override
    public ExternalLink apply(Row row, String prefix) {
        ExternalLink entity = new ExternalLink();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setUrl(converter.fromRow(row, prefix + "_url", String.class));
        entity.setType(converter.fromRow(row, prefix + "_type", LinkType.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
