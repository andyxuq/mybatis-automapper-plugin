package andy.ibatis.plugin.annation;

import java.lang.annotation.*;

/**
 * User: andyxu
 * Date: 2018/11/22
 * Time: 16:36
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Many {

    /** id property name **/
    String idProperty() default "id";

    /** id column name */
    String idColumn() default "";
}
