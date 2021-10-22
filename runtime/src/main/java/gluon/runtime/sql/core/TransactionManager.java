package gluon.runtime.sql.core;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager implements AutoCloseable {
    private final Connection connection;
    private boolean commited = false;

    public TransactionManager(Connection connection) {
        this.connection = connection;
    }

    public void commit() throws SQLException {
        connection.commit();
        this.commited = true;
    }

    @Override
    public void close() throws Exception {
        // auto-rollback if exception
        if (!commited) {
            connection.rollback();
        }
    }
}
