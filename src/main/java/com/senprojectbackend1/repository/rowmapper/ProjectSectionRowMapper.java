package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.ProjectSection;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProjectSection}, with proper type conversions.
 */
@Service
public class ProjectSectionRowMapper implements BiFunction<Row, String, ProjectSection> {

    private final ColumnConverter converter;

    public ProjectSectionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProjectSection} stored in the database.
     */
    @Override
    public ProjectSection apply(Row row, String prefix) {
        ProjectSection entity = new ProjectSection();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setContent(converter.fromRow(row, prefix + "_content", String.class));
        entity.setMediaUrl(converter.fromRow(row, prefix + "_media_url", String.class));
        entity.setOrder(converter.fromRow(row, prefix + "_jhi_order", Integer.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
