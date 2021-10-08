package gluon.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SQL {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Repository {
    }

    /***
     * Query data from database with SELECT.
     * value contains sql expression
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Query {
        String value();
    }

    /***
     * Persist changed to database with UPDATE, DELETE, INSERT
     * value contains sql expression
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Persist {
        String value();
    }

    /***
     * Generates constructor for ResultSet
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Constructable {}

    /**
     * If present, do not include this field in the ResultSet extractor,
     * but provide this field in constructor parameter as is.
     * If source is ResultSet; constructor will shift rs index
     */
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Provided {
        Source value() default Source.ResultSet;
    }

    /**
     * Join Entities (with recursive generation of rs constructor).
     * Valid only for Set<Joined> or List<Joined>
     */
    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.RECORD_COMPONENT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Join {}

}
