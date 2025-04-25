package com.senprojectbackend1.repository.rowmapper;

import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.enumeration.Genre;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserProfile}, with proper type conversions.
 */
@Service
public class UserProfileRowMapper implements BiFunction<Row, String, UserProfile> {

    private final ColumnConverter converter;

    public UserProfileRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserProfile} stored in the database.
     */
    @Override
    public UserProfile apply(Row row, String prefix) {
        UserProfile entity = new UserProfile();
        entity.setId(converter.fromRow(row, prefix + "_id", String.class));
        entity.setLogin(converter.fromRow(row, prefix + "_login", String.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setImageUrl(converter.fromRow(row, prefix + "_image_url", String.class));
        entity.setActivated(converter.fromRow(row, prefix + "_activated", Boolean.class));
        entity.setLangKey(converter.fromRow(row, prefix + "_lang_key", String.class));
        entity.setCreatedBy(converter.fromRow(row, prefix + "_created_by", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setLastModifiedBy(converter.fromRow(row, prefix + "_last_modified_by", String.class));
        entity.setLastModifiedDate(converter.fromRow(row, prefix + "_last_modified_date", Instant.class));
        entity.setProfileLink(converter.fromRow(row, prefix + "_profile_link", String.class));
        entity.setBiography(converter.fromRow(row, prefix + "_biography", String.class));
        entity.setBirthDate(converter.fromRow(row, prefix + "_birth_date", Instant.class));
        entity.setJob(converter.fromRow(row, prefix + "_job", String.class));
        entity.setSexe(converter.fromRow(row, prefix + "_sexe", Genre.class));
        return entity;
    }
}
