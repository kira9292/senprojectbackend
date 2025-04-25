package com.senprojectbackend1.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EngagementTeamSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("jhi_like", table, columnPrefix + "_jhi_like"));
        columns.add(Column.aliased("created_at", table, columnPrefix + "_created_at"));

        columns.add(Column.aliased("team_id", table, columnPrefix + "_team_id"));
        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        return columns;
    }
}
