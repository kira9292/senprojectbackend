package com.senprojectbackend1.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class CommentSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("content", table, columnPrefix + "_content"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));
        columns.add(Column.aliased("updated_at", table, columnPrefix + "_updated_at"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("project_id", table, columnPrefix + "_project_id"));
        return columns;
    }
}
