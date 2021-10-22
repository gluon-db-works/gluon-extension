package gluon.runtime.sql.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class QueryIterator<T>  implements Iterator<T>, AutoCloseable {

    private final ResultSet rs;
    private final ResultSetExtractor<T> resultSetExtractor;

    protected QueryIterator(ResultSet rs, ResultSetExtractor<T> resultSetExtractor) {
        this.rs = rs;
        this.resultSetExtractor = resultSetExtractor;
    }

    @Override
    public boolean hasNext() {
        try {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T next() {
        try {
            return resultSetExtractor.extract(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        rs.close();
    }
}
