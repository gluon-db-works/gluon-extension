package gluon.runtime.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Updater implements AutoCloseable {

    protected final PreparedStatement st;
    private final StatementPreparator preparator;

    public Updater(Connection connection, String sql, StatementPreparator preparator) throws SQLException {
        assert preparator != null : "Preparator must not be null";
        st = connection.prepareStatement(sql);
        this.preparator = preparator;
    }

    @Override
    public void close() throws Exception {
        if (st != null) st.close();
    }

    public void update() throws SQLException {
        if (this.preparator != null) this.preparator.prepare(st);
        st.executeUpdate();
    }

}

