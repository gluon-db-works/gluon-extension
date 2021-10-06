package gluon.runtime.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BatchUpdater implements AutoCloseable {

    protected final PreparedStatement st;
    private final StatementPreparator preparator;

    public BatchUpdater(Connection connection, String sql, StatementPreparator preparator) throws SQLException {
        assert preparator != null : "Preparator must not be null";
        st = connection.prepareStatement(sql);
        this.preparator = preparator;
    }

    @Override
    public void close() throws Exception {
        st.executeBatch();
        st.close();
    }

    public void update() throws SQLException {
        if (preparator != null) preparator.prepare(st);
        st.addBatch();
    }

    public void commit() throws SQLException {
        st.executeBatch();
    }
}
