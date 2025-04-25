package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.Notification;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Notification}, with proper type conversions.
 */
@Service
public class NotificationRowMapper implements BiFunction<Row, String, Notification> {

    private final ColumnConverter converter;

    public NotificationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Notification} stored in the database.
     */
    @Override
    public Notification apply(Row row, String prefix) {
        Notification entity = new Notification();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setContent(converter.fromRow(row, prefix + "_content", String.class));
        entity.setCreatedAt(converter.fromRow(row, prefix + "_created_at", Instant.class));
        entity.setReadAt(converter.fromRow(row, prefix + "_read_at", Instant.class));
        entity.setType(converter.fromRow(row, prefix + "_type", NotificationType.class));
        entity.setEntityId(converter.fromRow(row, prefix + "_entity_id", String.class));
        entity.setAction(converter.fromRow(row, prefix + "_action", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", String.class));
        return entity;
    }
}
