# Mybatis结果集自动映射插件

## ResultMap配置现状以及设计初衷

mybatis以其灵活、对sql语句很好的掌控性以及强大的结果集映射能力在众多ORM框架中占据了一席之地。
工作中也在很多地方选择了mybatis来进行数据库的操作，在享受mybatis带来便利的同时，也在反思如何
更高效的使用它。使用mybatis-generator可以帮助我们生成单表的增删改查操作，这真的是非常方便，
但是在处理关联查询时，通常需要我们自己写resultMap，如下：
```xml
  <resultMap id="BaseResultMap" type="example.ibatis.dao.model.StudentDetail" >
    <id column="id" property="id" jdbcType="INTEGER" />
    <result column="create_time" property="createTime" jdbcType="TIMESTAMP" />
    <result column="modified_time" property="modifiedTime" jdbcType="TIMESTAMP" />
    <result column="user_id" property="userId" jdbcType="INTEGER" />
    <result column="class_name" property="className" jdbcType="VARCHAR" />
      <association property="user" javaType="example.ibatis.dao.mysql.model.UserDo">
          <id column="user_id" property="id" jdbcType="INTEGER" />
          <result column="user_name" property="userName" jdbcType="VARCHAR" />
          <result column="user_age" property="userAge" jdbcType="INTEGER" />
      </association>
      <collection property="subjectList" ofType="example.ibatis.dao.mysql.model.StudentSubjectDo">
          <id column="subject_id" property="id" jdbcType="INTEGER" />
          <result column="student_id" property="studentId" jdbcType="INTEGER" />
          <result column="subject_name" property="subjectName" jdbcType="VARCHAR" />
          <result column="subject_teacher" property="subjectTeacher" jdbcType="VARCHAR" />
      </collection>
  </resultMap>
```
可以看到，resultMap中清楚的描述了`数据库字段`与`对象属性`的映射关系，这样mybatis在封装查询结果时，就能将数据库的字段值设置到指定对象的指定属性上。
但是有个问题，resultMap写起来实在是有点麻烦，尤其是当查询字段多，查询关联关系复杂的时候更是如此。当然mybatis自身已经提供了autoMapping功能来解决这个问题，
所以上面的resultMap可以变成如下的形式：
```xml
  <resultMap id="BaseResultMap" type="example.ibatis.dao.model.StudentDetail" autoMapping="true">
    <id column="id" property="id" jdbcType="INTEGER" />
    <result column="class_name" property="className" jdbcType="VARCHAR" />
      <association property="user" javaType="example.ibatis.dao.mysql.model.UserDo">
          <id column="user_id" property="id" jdbcType="INTEGER" />
      </association>
      <collection property="subjectList" ofType="example.ibatis.dao.mysql.model.StudentSubjectDo">
          <id column="subject_id" property="id" jdbcType="INTEGER" />
      </collection>
  </resultMap>
```
我们在`resultMap`中加上了autoMapping属性并设置其为true，表示我们希望mybatis帮我们自动映射。resultMap的autoMapping属性
默认是需要数据库字段名和属性名一样才能匹配上的，当然我们可以设置成数据库字段下划线和属性的驼峰来匹配，就像例子中一样，另外autoMapping默认是只匹配一层
的而不会匹配多层，我们需要通过修改配置来达到让其匹配多个层次结构，例子中对应的mybaits配置如下：
```xml
<configuration>
    <settings>
        <!-- 下划线匹配驼峰 -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <!-- 自动匹配模式为全匹配 -->
        <setting name="autoMappingBehavior" value="FULL"/>
    </settings>
</configuration>
```
另外也是最重要的一点，例子中虽然我们开启了自动匹配，但是对于嵌套对象ID属性的匹配，我们依然配置在了resultMap中，因为在查询时我们对嵌套对象的ID使用了别名，
UserDo的id对应查询结果是user_id，StudentSubjectDo的id对应查询结果是subject_id。若不使用别名，则我们将无法区分包装对象和嵌套对象的ID，因为他们对应的数据库的字段都是ID
关于auto-mapping具体可以参考官方文档：[http://www.mybatis.org/mybatis-3/sqlmap-xml.html#Auto-mapping](http://www.mybatis.org/mybatis-3/sqlmap-xml.html#Auto-mapping)

综上，虽然autoMapping省去了我们很多的配置，但是我们依然要书写部分的ResultMap配置。那我们能否不写ResultMap呢？答案是肯定的，在编写ResultMap时候，我们注意到其有一个type属性，
表示需要封装结果集的具体对象，这个type对应的对象本身不就包含了所有需要从数据库获取的数据信息吗，并且type对应的对象也包含了完整的嵌套信息。但是type对象里面的属性，
对应的是数据库里面的哪一个字段呢？是的，我们可以像JPA一样，通过注解描述每个属性对应的数据库字段，基于此我开发了该插件

## 插件的使用

### 插件配置
1. 将auto-mapper打成jar上传到你本地仓库，然后在项目中申明依赖。或者直接将auto-mapper作为maven模块引入到你的项目中
2. 将插件实现：`ResultSetHandlerInteceptor`配置到`SqlSessionFactoryBean`中，spring-boot示例如下：
```java
    @Bean(name = "sessionFactoryBean")
    public SqlSessionFactoryBean sessionFactoryBean(@Qualifier("dataSource") DataSource dataSource) throws IOException {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/example/ibatis/dao/mysql/**/*.xml"));
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));

        //add auto mapper plugin
        Interceptor[] plugins = new Interceptor[]{new ResultSetHandlerInteceptor()};
        bean.setPlugins(plugins);
        return bean;
    }
```

### 使用示例
> 插件的使用示例在example中

经过上面的步骤，插件已经配置好。如example中的示例对象一样，我们定义了一个StudentDetail对象如下：
```java
public class StudentDetail extends StudentDo {

    @Column(name = "id", jdbcType = JdbcType.INTEGER, isId = true)
    private Integer id;

    @One(idColumn = "user_id", idProperty="id")
    private UserDo user;

    @Many(idColumn = "subject_id", idProperty="id")
    private List<StudentSubjectDo> subjectList;
}
```

说明：
* `StudentDo`、`UserDo`、`StudentSubjectDo`是mybatis-generator自动生成的数据库表对象，原则上我们不对他们做任何改动，因为若对其进行了修改，万一以后表结构有更新，重新生成该对象之后，之前的人为改动就会丢失
* `StudentDetail`表示学生详细信息，
    - 继承自`StudentDo`以便获取所有学生信息，并且覆盖了StudentDo的id属性，并加上了@Column注解
    - 包含了一个一对一关系的`UserDo`对象表示学生对应的用户信息。并通过@One注解表明属性ID与数据库查询结果的对应关系
    - 包含了一个一对多关系的`StudentSubjectDo`对象表示学生的学科列表。并通过@Many注解表明属性ID与数据库查询结果的对应关系

写好封装对象之后，我们可以像如下的形式来完成mybatis经典Mapper接口的编写:
```java
public interface ExStudentMapper {

    @Select("select s.id, s.create_time, s.modified_time, s.class_name," +
            " u.id as user_id, u.user_name, u.user_age, " +
            " ss.id as subject_id, ss.subject_name, ss.subject_teacher " +
            " from ie_student s, ie_user u, ie_student_subject ss " +
            " where s.user_id = u.id and s.id = ss.student_id " +
            " and u.user_name = #{userName}")
    StudentDetail getAutoMapperOne(@Param("userName") String userName);

    List<StudentDetail> getAutoMapperWithXmlSql();
}
```

其中方法`getAutoMapperWithXmlSql()`对应的Sql语句在xml文件`ExStudentMapper.xml`中，如下所示:
```xml
    <select id="getAutoMapperWithXmlSql" resultType="example.ibatis.dao.model.StudentDetail" parameterType="map">
        select s.id, s.create_time, s.modified_time, s.class_name,
         u.id as user_id, u.user_name, u.user_age,
         ss.id as subject_id, ss.subject_name, ss.subject_teacher
         from ie_student s, ie_user u, ie_student_subject ss
         where s.user_id = u.id and s.id = ss.student_id
    </select>
```

说明：
* 我们不必为Mapper中的方法`getAutoMapperOne`单独写ResultMap，也无需通过注解@Results来描述StudentDetail与查询结果的对应关系
* 我们不必为查询方法`getAutoMapperWithXmlSql`单独写ResultMap，直接写resultType即可
* 插件会帮助我们处理数据库查询结果与`StudentDetail`的映射关系，并且能很好的处理内嵌对象

## 插件实现说明

### mybatis对查询语句的解析

mybatis解析SQL语句的来源有两个地方，一是来自xml，一是来自Mapper接口里面的注解（方法上的@Select注解等）
* 在与Spring整合时，xml文件的解析入口是SqlSessionFactoryBean，遍历解析我们在mapperLocations中设置的xml资源，关键代码如下：
```java
  //SqlSessionFactoryBean实现了Spring的InitializingBean
  @Override
  public void afterPropertiesSet() throws Exception {
    ......
    this.sqlSessionFactory = buildSqlSessionFactory();
  }
  
  protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
	......
    if (!isEmpty(this.mapperLocations)) {
      for (Resource mapperLocation : this.mapperLocations) {
        if (mapperLocation == null) {
          continue;
        }

        try {
          XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(),
              configuration, mapperLocation.toString(), configuration.getSqlFragments());
          xmlMapperBuilder.parse();
        } catch (Exception e) {
          throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
        } finally {
          ErrorContext.instance().reset();
        }
        LOGGER.debug(() -> "Parsed mapper file: '" + mapperLocation + "'");
      }
    } else {
      LOGGER.debug(() -> "Property 'mapperLocations' was not specified or no matching resources found");
    }

    return this.sqlSessionFactoryBuilder.build(configuration);
  }
```
* 与Spring整合时，Mapper接口的解析（这里的Mapper接口是指没有关联xml文件的纯Mapper接口），解析代码入口在MapperFactoryBean，关键代码如下：
```java
  /**
   * {@inheritDoc}
   */
  @Override
  protected void checkDaoConfig() {
    super.checkDaoConfig();

    notNull(this.mapperInterface, "Property 'mapperInterface' is required");

    Configuration configuration = getSqlSession().getConfiguration();
    if (this.addToConfig && !configuration.hasMapper(this.mapperInterface)) {
      try {
        //下面的方法最终会执行到MapperRegistry.addMapper，该方法里面通过MapperAnnotationBuilder对Mapper接口提供了解析
        configuration.addMapper(this.mapperInterface);
      } catch (Exception e) {
        logger.error("Error while adding the mapper '" + this.mapperInterface + "' to configuration.", e);
        throw new IllegalArgumentException(e);
      } finally {
        ErrorContext.instance().reset();
      }
    }
  }
```
* 不管SQL语句从哪里来，Mybatis最终都会将每一个Sql语句（SELECT|UPDATE|DELETE|INSERT)解析成一个对应的MappedStatement对象，该对象中有一个属性是我们本次插件开发需要特别关心的，就是ResultMaps。对于我们在xml中配置的ResultMap，最终都会被解析成ResultMap对象，放到对应的MappedStatement中。
mybatis在做查询语句的结果集映射的时候，就会根据MappedStatement中的ResultMaps来封装查询结果。关键源码如下:
```java
public final class MappedStatement {
  ....
  //这里ResultMaps是list，因为jdbc驱动允许我们用一个statement一次执行多条查询语句（分号分隔），每条查询语句会对应一个ResultSet，一个ResultSet需要对应一个ResultMap
  private List<ResultMap> resultMaps;
  ....
}
```

### 插件的工作原理

知道了mybatis查询是通过对应语句的MappedStatement对象中ResultMaps来封装结果的，那么在我们不写ResultMap时，只需要自己去解析封装结果集的java对象，然后生成ResultMap，
再将生成的ResultMap设置到MappedStatement中即可，具体源码可以查看: [ResultSetHandlerInteceptor](https://github.com/andyxuq/mybatis-automapper-plugin/blob/master/auto-mapper/src/main/java/andy/ibatis/plugin/ResultSetHandlerInteceptor.java)

新增注解说明：
* @Column: 标记被注解的属性为数据库的列（若不加Column注解，插件会自动将驼峰属性名转化成下划线的形式表示列名，并添加到ResultMapping中）
    - name: 列名
    - jdbcType: 该列对应的数据类型
    - isId: 是否是主键ID（默认false:不是），对于每一个对象，请务必配置一个主键ID，就像我们用xml配置resultMap一样
    - typeHandler: 同Xml配置中元素ResultMap的typeHandler一样，自定义属性值获取时的类型处理器
* @Many: 表示一对多关系，即ResultMap中的collection
    - idProperty: many对象里，表示id的属性名字是什么，默认"id"
    - idColumn: many对象里，表示主键的列名是什么，若不填写，则必须用@Column注解标注many对象里的主键信息
* @One: 表示一对一关系，即ResultMap中的association
    - idProperty: one对象里，表示id的属性名字是什么，默认"id"
    - idColumn: one对象里，表示主键的列名是什么，若不填写，则必须用@Column注解标注one对象里的主键信息  




