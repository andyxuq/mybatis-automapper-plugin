package andy.ibatis.plugin;

import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.ResultMapResolver;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: andyxu
 * Date: 2018/11/20
 * Time: 14:45
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class ResultSetHandlerInteceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(ResultSetHandlerInteceptor.class);

    private static final AtomicInteger NUMBER_COUNTER = new AtomicInteger(0);

    private static final Set<String> MAPPED_STATEMENT_CACHE = new ConcurrentSkipListSet<>();

    private static final TypeAliasRegistry ALIAS_REGISTRY = new TypeAliasRegistry();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        DefaultResultSetHandler handler = (DefaultResultSetHandler)invocation.getTarget();
        MappedStatement statement = getFieldValueByType(handler, MappedStatement.class);
        if (!needAutoMapper(statement)) {
            return invocation.proceed();
        }

        Configuration configuration = getFieldValueByType(handler, Configuration.class);
        String resource = statement.getResource();
        String nameSpace = statement.getId().substring(0, statement.getId().lastIndexOf("."));
        MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(configuration, resource);
        builderAssistant.setCurrentNamespace(nameSpace);

        boolean replaceResultMap = Boolean.FALSE;
        List<ResultMap> rebuildMapList = new ArrayList<>();
        for (ResultMap resultMap : statement.getResultMaps()) {
            if (!needAutoMapper(resultMap)) {
                rebuildMapList.add(resultMap);
                continue;
            }
            replaceResultMap = true;
            ResultMap rebuildResultMap = rebuildResultMap(TableMeta.NestEntry.of(resultMap.getType(), "", ""), builderAssistant);
            rebuildMapList.add(rebuildResultMap);
        }

        if (replaceResultMap) {
            replaceResultMapValue(statement, rebuildMapList);
        }
        MAPPED_STATEMENT_CACHE.add(statement.getId());
        return invocation.proceed();
    }

    private boolean needAutoMapper(MappedStatement statement) {
        if (MAPPED_STATEMENT_CACHE.contains(statement.getId())
                || statement.getSqlCommandType() != SqlCommandType.SELECT) {
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private boolean needAutoMapper(ResultMap resultMap) {
        Collection<Class<?>> simpleTypes = ALIAS_REGISTRY.getTypeAliases().values();
        if (simpleTypes.contains(resultMap.getType())) {
            return Boolean.FALSE;
        } else if (resultMap.getResultMappings() == null || resultMap.getResultMappings().isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {}

    private <T> T getFieldValueByType(Object object, Class<T> fieldType) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == fieldType) {
                return getValue(field, object);
            }
        }
        return null;
    }

    private ResultMap rebuildResultMap(TableMeta.NestEntry primaryEntryInfo, MapperBuilderAssistant builderAssistant) {
        Class<?> type = primaryEntryInfo.getSource();
        try {
            List<ResultMapping> mappingList = rebuildResultMapping(primaryEntryInfo, builderAssistant);
            String id = getCustomIdentifier(type);

            //register result map to configuration
            ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, type,
                    null, null, mappingList, null);
            return resultMapResolver.resolve();
        } catch (Exception e) {
            logger.error("rebuild result map for type:{} fail", type.getName(), e);
            throw e;
        }
    }

    private List<ResultMapping> rebuildResultMapping(TableMeta.NestEntry primaryEntryInfo, MapperBuilderAssistant builderAssistant) {
        TableMeta tableMeta = TableMeta.ofType(primaryEntryInfo.getSource());
        List<ResultMapping> mappingList = new ArrayList<>();
        tableMeta.getEntryList().forEach(entry->{
            List<ResultFlag> flags = new ArrayList<>();
            String columnName = entry.getColumnName();
            String propertyName = entry.getPropertyName();
            String nestResultMapId = null;

            TableMeta.NestEntry nestEntry = tableMeta.getNestEntryMap().get(propertyName);
            if (null != nestEntry) {
                nestResultMapId = rebuildResultMap(nestEntry, builderAssistant).getId();
            }
            if (primaryEntryInfo.getIdProperty().equals(propertyName)) {
                flags.add(ResultFlag.ID);
                columnName = primaryEntryInfo.getIdColumn();
            }

            if (entry.isPrimaryKey()) {
                flags.add(ResultFlag.ID);
            }
            ResultMapping resultMapping = new ResultMapping.Builder(builderAssistant.getConfiguration(), propertyName, columnName, entry.getJavaType())
                    .flags(flags)
                    .jdbcType(entry.getJdbcType())
                    .nestedResultMapId(nestResultMapId)
                    .build();
            mappingList.add(resultMapping);
        });
        return mappingList;
    }

    private String getCustomIdentifier(Class<?> type) {
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append("_[");
        idBuilder.append(type.getSimpleName());
        idBuilder.append("]_");
        idBuilder.append(NUMBER_COUNTER.getAndIncrement());
        return idBuilder.toString();
    }

    private <T> T getValue(Field field, Object obj) throws IllegalAccessException {
        field.setAccessible(Boolean.TRUE);
        try {
            return (T) field.get(obj);
        } catch (IllegalAccessException e) {
            logger.error("get field:{} value from:{} fail", field.getName(), obj.getClass().getName(), e);
            throw e;
        }
    }

    private void replaceResultMapValue(MappedStatement statement, List<ResultMap> rebuildMapList) throws NoSuchFieldException, IllegalAccessException {
        try {
            Field resultMapField = statement.getClass().getDeclaredField("resultMaps");
            resultMapField.setAccessible(Boolean.TRUE);
            resultMapField.set(statement, rebuildMapList);
        } catch (NoSuchFieldException|IllegalAccessException e) {
            logger.error("replace resultMap value for statement fail", e);
            throw e;
        }
    }
}
