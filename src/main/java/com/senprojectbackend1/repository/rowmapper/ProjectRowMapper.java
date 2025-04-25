package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.domain.enumeration.ProjectType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Project}, with proper type conversions.
 */
@Service
public class ProjectRowMapper implements BiFunction<Row, String, Project> {

    private final ColumnConverter converter;

    public ProjectRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Project} stored in the database.
     */
    @Override
    public Project apply(Row row, String prefix) {
        Project entity = new Project();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setShowcase(converter.fromRow(row, prefix + "_showcase", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", ProjectStatus.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setUpdatedAt(converter.fromRow(row, prefix + "_updated_at", Instant.class));
        entity.setGithubUrl(converter.fromRow(row, prefix + "_github_url", String.class));
        entity.setWebsiteUrl(converter.fromRow(row, prefix + "_website_url", String.class));
        entity.setDemoUrl(converter.fromRow(row, prefix + "_demo_url", String.class));
        entity.setOpenToCollaboration(converter.fromRow(row, prefix + "_open_to_collaboration", Boolean.class));
        entity.setOpenToFunding(converter.fromRow(row, prefix + "_open_to_funding", Boolean.class));
        entity.setType(converter.fromRow(row, prefix + "_type", ProjectType.class));
        entity.setTotalLikes(converter.fromRow(row, prefix + "_total_likes", Integer.class));
        entity.setTotalShares(converter.fromRow(row, prefix + "_total_shares", Integer.class));
        entity.setTotalViews(converter.fromRow(row, prefix + "_total_views", Integer.class));
        entity.setTotalComments(converter.fromRow(row, prefix + "_total_comments", Integer.class));
        entity.setTotalFavorites(converter.fromRow(row, prefix + "_total_favorites", Integer.class));
        entity.setIsDeleted(converter.fromRow(row, prefix + "_is_deleted", Boolean.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setLastUpdatedBy(converter.fromRow(row, prefix + "_last_updated_by", String.class));
        entity.setTeamId(converter.fromRow(row, prefix + "_team_id", Long.class));
        return entity;
    }
}
