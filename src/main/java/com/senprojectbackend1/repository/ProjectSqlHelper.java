package com.senprojectbackend1.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProjectSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("title", table, columnPrefix + "_title"));
        columns.add(Column.aliased("description", table, columnPrefix + "_description"));
        columns.add(Column.aliased("showcase", table, columnPrefix + "_showcase"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));
        columns.add(Column.aliased("updated_at", table, columnPrefix + "_updated_at"));
        columns.add(Column.aliased("github_url", table, columnPrefix + "_github_url"));
        columns.add(Column.aliased("website_url", table, columnPrefix + "_website_url"));
        columns.add(Column.aliased("demo_url", table, columnPrefix + "_demo_url"));
        columns.add(Column.aliased("open_to_collaboration", table, columnPrefix + "_open_to_collaboration"));
        columns.add(Column.aliased("open_to_funding", table, columnPrefix + "_open_to_funding"));
        columns.add(Column.aliased("type", table, columnPrefix + "_type"));
        columns.add(Column.aliased("total_likes", table, columnPrefix + "_total_likes"));
        columns.add(Column.aliased("total_shares", table, columnPrefix + "_total_shares"));
        columns.add(Column.aliased("total_views", table, columnPrefix + "_total_views"));
        columns.add(Column.aliased("total_comments", table, columnPrefix + "_total_comments"));
        columns.add(Column.aliased("total_favorites", table, columnPrefix + "_total_favorites"));
        columns.add(Column.aliased("is_deleted", table, columnPrefix + "_is_deleted"));
        columns.add(Column.aliased("created_by", table, columnPrefix + "_created_by"));
        columns.add(Column.aliased("last_updated_by", table, columnPrefix + "_last_updated_by"));

        columns.add(Column.aliased("team_id", table, columnPrefix + "_team_id"));
        return columns;
    }
}
