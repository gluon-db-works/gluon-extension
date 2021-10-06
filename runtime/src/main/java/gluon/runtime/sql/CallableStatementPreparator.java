package gluon.runtime.sql;

import java.sql.CallableStatement;
import java.sql.SQLException;

public interface CallableStatementPreparator {
    void prepare(CallableStatement statement) throws SQLException;
}
