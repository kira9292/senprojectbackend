package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.ProjectGallery;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProjectGallery}, with proper type conversions.
 */
@Service
public class ProjectGalleryRowMapper implements BiFunction<Row, String, ProjectGallery> {

    private final ColumnConverter converter;

    public ProjectGalleryRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProjectGallery} stored in the database.
     */
    @Override
    public ProjectGallery apply(Row row, String prefix) {
        ProjectGallery entity = new ProjectGallery();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setImageUrl(converter.fromRow(row, prefix + "_image_url", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setOrder(converter.fromRow(row, prefix + "_jhi_order", Integer.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
