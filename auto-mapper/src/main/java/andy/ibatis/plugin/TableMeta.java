package andy.ibatis.plugin;

import andy.ibatis.plugin.annation.Column;
import andy.ibatis.plugin.annation.Many;
import andy.ibatis.plugin.annation.One;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: andyxu
 * Date: 2018/11/27
 * Time: 15:44
 */
public class TableMeta {

    private Class<?> source;

    private List<ColumnEntry> entryList;

    private Map<String, NestEntry> nestEntryMap;

    private TableMeta(Class<?> source){
        this.source = source;
    }

    public static TableMeta ofType(Class<?> type) {
        TableMeta meta = new TableMeta(type);
        meta.entryList = new ArrayList<>();
        meta.nestEntryMap = new HashMap<>();

        Field[] fieldArray = getAllFields(type);
        Arrays.stream(fieldArray).forEach(field-> {
            Column column = field.getAnnotation(Column.class);
            One one = field.getAnnotation(One.class);
            Many many = field.getAnnotation(Many.class);

            String propertyName = field.getName();
            Class<?> javaType = field.getType();
            String columnName = null;
            JdbcType jdbcType = null;
            boolean isPrimaryKey = false;

            if (one != null || null != many) {
                Class<?> subMapType = null == many ? javaType : getFieldType(field);
                String idProperty = null == one ? many.idProperty() : one.idProperty();
                String idColumn = null == one ? many.idColumn() : one.idColumn();
                meta.nestEntryMap.put(propertyName, NestEntry.of(subMapType, idProperty, idColumn));
            } else if (null != column) {
                columnName = column.name();
                jdbcType = column.jdbcType();
                if (column.isId()) {
                    isPrimaryKey = true;
                }
            }
            if (null == columnName) {
                columnName = deCamelPropertyName(propertyName);
            }

            ColumnEntry entry = new ColumnEntry(propertyName, columnName, javaType, jdbcType, isPrimaryKey);
            meta.entryList.add(entry);
        });
        return meta;
    }

    private static Field[] getAllFields(Class<?> type) {
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        while (type != Object.class) {
            Field[] fields = type.getDeclaredFields();
            Arrays.stream(fields).filter(field -> !fieldNames.contains(field.getName()))
                    .collect(Collectors.toList())
                    .forEach(field->{
                        fieldNames.add(field.getName());
                        fieldList.add(field);
                    });
            type = type.getSuperclass();
        }
        return fieldList.toArray(new Field[fieldList.size()]);
    }

    private static Class<?> getFieldType(Field field) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            //support one generic type only
            Type genericType = ((ParameterizedType) type).getActualTypeArguments()[0];
            if (genericType instanceof ParameterizedType) {
                return (Class<?>)((ParameterizedType) genericType).getRawType();
            } else if (genericType instanceof Class<?>) {
                return (Class<?>) genericType;
            }
        } else if (type instanceof Class<?>) {
            return (Class<?>)type;
        }
        return field.getType();
    }

    private static String deCamelPropertyName(String propertyName) {
        StringBuilder result = new StringBuilder();
        if (propertyName != null && propertyName.length() > 0) {
            for (int i = 0; i < propertyName.length(); i++) {
                char ch = propertyName.charAt(i);
                if (Character.isUpperCase(ch)) {
                    result.append("_");
                    result.append(Character.toLowerCase(ch));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }

    public Class<?> getSource() {
        return source;
    }

    public List<ColumnEntry> getEntryList() {
        return entryList;
    }

    public Map<String, NestEntry> getNestEntryMap() {
        return nestEntryMap;
    }

    final static class NestEntry {

        private Class<?> source;

        private String idProperty;

        private String idColumn;

        public static NestEntry of(Class<?> source, String idProperty, String idColumn) {
            return new NestEntry(source, idProperty, idColumn);
        }

        private NestEntry(Class<?> source, String idProperty, String idColumn) {
            this.source = source;
            this.idProperty = idProperty;
            this.idColumn = idColumn;
        }

        public Class<?> getSource() {
            return source;
        }

        public String getIdProperty() {
            return idProperty;
        }

        public String getIdColumn() {
            return idColumn;
        }

    }

    final static class ColumnEntry {

        private String propertyName;

        private String columnName;

        private Class<?> javaType;

        private JdbcType jdbcType;

        private boolean isPrimaryKey;

        public ColumnEntry(String propertyName, String columnName, Class<?> javaType, JdbcType jdbcType, boolean isPrimaryKey) {
            this.propertyName = propertyName;
            this.columnName = columnName;
            this.javaType = javaType;
            this.jdbcType = jdbcType;
            this.isPrimaryKey = isPrimaryKey;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public String getColumnName() {
            return columnName;
        }

        public Class<?> getJavaType() {
            return javaType;
        }

        public JdbcType getJdbcType() {
            return jdbcType;
        }

        public boolean isPrimaryKey() {
            return isPrimaryKey;
        }

    }

}
