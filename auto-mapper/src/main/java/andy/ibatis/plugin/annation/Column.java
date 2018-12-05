package andy.ibatis.plugin.annation;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.*;

/**
 * User: andyxu
 * Date: 2018/11/22
 * Time: 15:24
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {

    /** db column name */
    String name();

    /** column type */
    JdbcType jdbcType() default JdbcType.NULL;

    /** is primary key */
    boolean isId() default false;

    /** type handler */
    Class<? extends TypeHandler<?>> typeHandler() default UnknownTypeHandler.class;
}
