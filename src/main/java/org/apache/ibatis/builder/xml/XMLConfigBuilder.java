/**
 * Copyright 2009-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.builder.xml;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.loader.ProxyFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.JdbcType;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * 继承 BaseBuilder 抽象类，XML 配置构建器，主要负责解析 mybatis-config.xml 配置文件
 *
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public class XMLConfigBuilder extends BaseBuilder {

  /**
   * 是否已解析
   */
  private boolean parsed;
  /**
   * 基于 Java XPath 解析器
   */
  private final XPathParser parser;
  /**
   * 环境
   */
  private String environment;
  /**
   * ReflectorFactory 对象
   */
  private final ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

  public XMLConfigBuilder(Reader reader) {
    this(reader, null, null);
  }

  public XMLConfigBuilder(Reader reader, String environment) {
    this(reader, environment, null);
  }

  public XMLConfigBuilder(Reader reader, String environment, Properties props) {
    this(new XPathParser(reader, true, props, new XMLMapperEntityResolver()), environment, props);
  }

  public XMLConfigBuilder(InputStream inputStream) {
    this(inputStream, null, null);
  }

  public XMLConfigBuilder(InputStream inputStream, String environment) {
    this(inputStream, environment, null);
  }

  public XMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
    this(new XPathParser(inputStream, true, props, new XMLMapperEntityResolver()), environment, props);
  }

  /**
   * 构造器
   *
   * @param parser
   * @param environment
   * @param props
   */
  private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
    // <1> 创建 Configuration 对象
    super(new Configuration());
    ErrorContext.instance().resource("SQL Mapper Configuration");
    // <2> 设置 Configuration 的 variables 属性
    this.configuration.setVariables(props);
    this.parsed = false;
    this.environment = environment;
    this.parser = parser;
  }

  /**
   * 解析 XML 成 Configuration 对象
   *
   * @return
   */
  public Configuration parse() {
    // <1.1> 若已解析，抛出 BuilderException 异常
    if (parsed) {
      throw new BuilderException("Each XMLConfigBuilder can only be used once.");
    }
    // <1.2> 标记已解析
    parsed = true;
    // <2> 解析 XML configuration 节点
    parseConfiguration(parser.evalNode("/configuration"));
    return configuration;
  }

  /**
   * 解析configuration节点的所有配置
   *
   * @param root
   */
  private void parseConfiguration(XNode root) {
    /**
     * 例子如下：对照例子来分析会容易很多
     <configuration>
     <properties resource="jdbc.properties">
     <property name="username" value="root" />
     <property name="password" value="123" />
     </properties>
     <settings>
     <setting name="localCacheScope" value="STATEMENT"/>
     <setting name="cacheEnabled" value="false" />
     <setting name="lazyLoadingEnabled" value="true" />
     <setting name="multipleResultSetsEnabled" value="true" />
     <setting name="useColumnLabel" value="true" />
     <setting name="useGeneratedKeys" value="false" />
     <setting name="defaultExecutorType" value="REUSE" />
     <setting name="defaultStatementTimeout" value="25000" />
     </settings>
     <typeAliases>
     <typeAlias alias="Student" type="com.mybatis3.domain.Student" />
     <typeAlias alias="Teacher" type="com.mybatis3.domain.Teacher" />
     </typeAliases>
     <typeHandlers>
     <typeHandler handler="com.mybatis3.typehandlers.PhoneTypeHandler" />
     </typeHandlers>
     <environments default="development">
     <environment id="development">
     <transactionManager type="JDBC" />
     <dataSource type="POOLED">
     <property name="driver" value="${driver}" />
     <property name="url" value="${url}" />
     <property name="username" value="${username}" />
     <property name="password" value="${password}" />
     </dataSource>
     </environment>
     </environments>
     <mappers>
     <mapper resource="com/mybatis3/mappers/StudentMapper.xml" />
     <mapper resource="com/mybatis3/mappers/TeacherMapper.xml" />
     </mappers>
     </configuration>
     */
    try {
      // issue #117 read properties first
      // <1> 解析configuration配置的 <properties /> 标签
      propertiesElement(root.evalNode("properties"));
      // <2> 解析 <settings /> 标签
      Properties settings = settingsAsProperties(root.evalNode("settings"));
      // <3> 加载自定义 VFS 实现类
      loadCustomVfs(settings);
      // 解析日志实现类
      loadCustomLogImpl(settings);
      // <4> 解析 <typeAliases /> 标签
      typeAliasesElement(root.evalNode("typeAliases"));
      // <5> 解析 <plugins /> 标签
      pluginElement(root.evalNode("plugins"));
      // <6> 解析 <objectFactory /> 标签
      objectFactoryElement(root.evalNode("objectFactory"));
      // <7> 解析 <objectWrapperFactory /> 标签
      objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
      // <8> 解析 <reflectorFactory /> 标签
      reflectorFactoryElement(root.evalNode("reflectorFactory"));
      // <9> 赋值 <settings /> 到 Configuration 属性
      settingsElement(settings);
      // read it after objectFactory and objectWrapperFactory issue #631
      // <10> 解析 <environments /> 标签
      environmentsElement(root.evalNode("environments"));
      // <11> 解析 <databaseIdProvider /> 标签
      databaseIdProviderElement(root.evalNode("databaseIdProvider"));
      // <12> 解析 <typeHandlers /> 标签
      typeHandlerElement(root.evalNode("typeHandlers"));
      // <13> 解析 <mappers /> 标签
      mapperElement(root.evalNode("mappers"));
    } catch (Exception e) {
      throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
    }
  }

  /**
   * 解析配置文件的settings节点，解析后返回Properties对象
   *
   * @param context settings节点
   * @return
   */
  private Properties settingsAsProperties(XNode context) {
    // 将子标签，解析成 Properties 对象
    if (context == null) {
      return new Properties();
    }
    //获取settings下的所有节点
    Properties props = context.getChildrenAsProperties();
    // Check that all settings are known to the configuration class
    // 校验每个属性，在 Configuration 中，有相应的 setting 方法，否则抛出 BuilderException 异常
    MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
    for (Object key : props.keySet()) {
      if (!metaConfig.hasSetter(String.valueOf(key))) {
        throw new BuilderException(
          "The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
      }
    }
    return props;
  }

  /**
   * 加载自定义 VFS 实现类
   *
   * @param props 全局属性，加载出来的实现赋值到此对象
   * @throws ClassNotFoundException
   */
  private void loadCustomVfs(Properties props) throws ClassNotFoundException {
    // 获得 vfsImpl 属性
    String value = props.getProperty("vfsImpl");
    if (value != null) {
      // 使用 , 作为分隔符，拆成 VFS 类名的数组
      String[] clazzes = value.split(",");
      // 遍历 VFS 类名的数组。如果设置了多个实现类，以最后一个为准
      for (String clazz : clazzes) {
        if (!clazz.isEmpty()) {
          // 获得 VFS 类
          @SuppressWarnings("unchecked")
          Class<? extends VFS> vfsImpl = (Class<? extends VFS>) Resources.classForName(clazz);
          // 设置到 Configuration 中
          configuration.setVfsImpl(vfsImpl);
        }
      }
    }
  }

  /**
   * 指定 MyBatis 所用日志的具体实现，未指定时将自动查找。
   *
   * @param props
   */
  private void loadCustomLogImpl(Properties props) {
    Class<? extends Log> logImpl = resolveClass(props.getProperty("logImpl"));
    configuration.setLogImpl(logImpl);
  }

  /**
   * 解析 <typeAliases /> 标签，将配置类注册到 typeAliasRegistry 中。
   *
   * @param parent typeAliases节点
   */
  private void typeAliasesElement(XNode parent) {
    if (parent != null) {
      // 遍历子节点，即<typeAlias/>节点
      for (XNode child : parent.getChildren()) {
        // 指定为包的情况下，注册包下的每个类。如：<package name="cn.com.mybatis.pojo"/>，把包路径下所有类批量写到注册器中
        if ("package".equals(child.getName())) {
          String typeAliasPackage = child.getStringAttribute("name");
          configuration.getTypeAliasRegistry().registerAliases(typeAliasPackage);
        }
        // 指定为类的情况下，直接注册类和别名，如：<typeAlias alias="user" type="cn.com.mybatis.pojo.User"/>
        else {
          String alias = child.getStringAttribute("alias");
          String type = child.getStringAttribute("type");
          try {
            //加载类，如果不存在，则抛出ClassNotFound异常
            Class<?> clazz = Resources.classForName(type);
            //未配置自定义别名，则自动把类名，第一个字母小写作为别名
            if (alias == null) {
              typeAliasRegistry.registerAlias(clazz);
            }
            //有配置自定义别名，则按自定义别名
            else {
              typeAliasRegistry.registerAlias(alias, clazz);
            }
          } catch (ClassNotFoundException e) {
            throw new BuilderException("Error registering typeAlias for '" + alias + "'. Cause: " + e, e);
          }
        }
      }
    }
  }

  /**
   * 解析 <plugins /> 标签，添加到 Configuration#interceptorChain 中
   *
   * @param parent
   * @throws Exception
   */
  private void pluginElement(XNode parent) throws Exception {
    if (parent != null) {
      // 遍历 <plugins /> 标签
      for (XNode child : parent.getChildren()) {
        //获取拦截器的全路径，如例子
        //<plugin interceptor="cn.com.mybatis.test.QueryPlugin">
        //<property name="someProperty" value="100"/>
        //</plugin>
        String interceptor = child.getStringAttribute("interceptor");
        //获取拦截器配置的属性
        Properties properties = child.getChildrenAsProperties();
        //实例化拦截器对象
        Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).getDeclaredConstructor()
          .newInstance();
        //赋值属性
        interceptorInstance.setProperties(properties);
        //添加到configuration中，后续使用
        configuration.addInterceptor(interceptorInstance);
      }
    }
  }

  /**
   * 解析 <objectFactory /> 节点
   *
   * @param context
   * @throws Exception
   */
  private void objectFactoryElement(XNode context) throws Exception {
    if (context != null) {
      // 获取objectFactory全限定类名
      // <objectFactory type="cn.com.mybatis.test.CartObjectFactory">
      // <property name="email" value="xxx"/>
      // <objectFactory/>
      //类名全路径
      String type = context.getStringAttribute("type");
      //objectFactory属性
      Properties properties = context.getChildrenAsProperties();
      //实例化objectFactory对象
      ObjectFactory factory = (ObjectFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      //设置 Properties 属性
      factory.setProperties(properties);
      //设置 Configuration 的 objectFactory 属性
      configuration.setObjectFactory(factory);
    }
  }

  /**
   * 解析 <objectWrapperFactory /> 节点。原理同objectFactory
   *
   * @param context
   * @throws Exception
   */
  private void objectWrapperFactoryElement(XNode context) throws Exception {
    if (context != null) {
      String type = context.getStringAttribute("type");
      ObjectWrapperFactory factory = (ObjectWrapperFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      configuration.setObjectWrapperFactory(factory);
    }
  }

  /**
   * 解析 <reflectorFactory /> 节点
   *
   * @param context
   * @throws Exception
   */
  private void reflectorFactoryElement(XNode context) throws Exception {
    if (context != null) {
      // 获得 ReflectorFactory 的实现类
      String type = context.getStringAttribute("type");
      // 创建 ReflectorFactory 对象
      ReflectorFactory factory = (ReflectorFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      // 设置 Configuration 的 reflectorFactory 属性
      configuration.setReflectorFactory(factory);
    }
  }

  /**
   * 解析 <properties /> 节点
   *
   * @param context
   * @throws Exception
   */
  private void propertiesElement(XNode context) throws Exception {
    if (context != null) {
      // 读取子标签们，为 Properties 对象
      Properties defaults = context.getChildrenAsProperties();
      // 读取 resource 和 url 属性
      String resource = context.getStringAttribute("resource");
      String url = context.getStringAttribute("url");
      // resource 和 url 都存在的情况下，抛出 BuilderException 异常
      //只能设一个
      if (resource != null && url != null) {
        throw new BuilderException(
          "The properties element cannot specify both a URL and a resource based property file reference.  Please specify one or the other.");
      }
      // 读取本地 Properties 配置文件到 defaults 中。
      if (resource != null) {
        defaults.putAll(Resources.getResourceAsProperties(resource));
      }
      // 读取远程 Properties 配置文件到 defaults 中。
      else if (url != null) {
        defaults.putAll(Resources.getUrlAsProperties(url));
      }
      // 覆盖 configuration 中的 Properties 对象到 defaults 中。
      Properties vars = configuration.getVariables();
      if (vars != null) {
        defaults.putAll(vars);
      }
      // 设置 defaults 到 parser 和 configuration 中。
      parser.setVariables(defaults);
      configuration.setVariables(defaults);
    }
  }

  /**
   * 赋值 <settings /> 到 Configuration 属性
   * <p>
   * https://www.cnblogs.com/yunqing/p/8065637.html
   *
   * @param props
   */
  private void settingsElement(Properties props) {
    configuration
      .setAutoMappingBehavior(AutoMappingBehavior.valueOf(props.getProperty("autoMappingBehavior", "PARTIAL")));
    configuration.setAutoMappingUnknownColumnBehavior(
      AutoMappingUnknownColumnBehavior.valueOf(props.getProperty("autoMappingUnknownColumnBehavior", "NONE")));
    //缓一级缓存，默认开启
    configuration.setCacheEnabled(booleanValueOf(props.getProperty("cacheEnabled"), true));
    //指定 Mybatis 创建具有延迟加载能力的对象所用到的代理工具
    configuration.setProxyFactory((ProxyFactory) createInstance(props.getProperty("proxyFactory")));
    //是否启用延迟加载，默认不启用
    configuration.setLazyLoadingEnabled(booleanValueOf(props.getProperty("lazyLoadingEnabled"), false));
    //是否启用按需加载，默认不启用。为false时使用关联查询及时加载，为true时加载对象就加载所有属性
    configuration.setAggressiveLazyLoading(booleanValueOf(props.getProperty("aggressiveLazyLoading"), false));
    //是否开启一条SQL语句可以返回多个结果集。默认开启
    //例如：
    //<select id="findCashItems" parameterType="map" resultMap="adminCashBalance, adminCashMovement, adminCashTrx">
    //  exec RequestActualAdministrativeData #{portfolioId}
    //</select>
    configuration.setMultipleResultSetsEnabled(booleanValueOf(props.getProperty("multipleResultSetsEnabled"), true));
    //允许使用列标签代替列名，默认开启
    configuration.setUseColumnLabel(booleanValueOf(props.getProperty("useColumnLabel"), true));
    //允许使用自定义的主键值(比如由程序生成的UUID 32位编码作为键值)，数据表的PK生成策略将被覆盖。默认不允许自定义
    configuration.setUseGeneratedKeys(booleanValueOf(props.getProperty("useGeneratedKeys"), false));
    //配置默认的执行器。SIMPLE 执行器没 有什么特别之处。REUSE 执行器重用 预处理语句。BATCH 执行器重用语句 和批量更新
    configuration.setDefaultExecutorType(ExecutorType.valueOf(props.getProperty("defaultExecutorType", "SIMPLE")));
    //设置超时时间, 它决定驱动等待一个数 据库响应的时间。未设置默认时间
    configuration.setDefaultStatementTimeout(integerValueOf(props.getProperty("defaultStatementTimeout"), null));
    //结果集获取数量。没有默认值
    configuration.setDefaultFetchSize(integerValueOf(props.getProperty("defaultFetchSize"), null));
    //默认的结果集类型
    configuration.setDefaultResultSetType(resolveResultSetType(props.getProperty("defaultResultSetType")));
    //是否开启自动驼峰命名规则（camel case）映射，即从经典数据库列名 A_COLUMN 到经典 Java 属性名 aColumn 的类似映射
    //默认不开启
    configuration.setMapUnderscoreToCamelCase(booleanValueOf(props.getProperty("mapUnderscoreToCamelCase"), false));
    //允许在嵌套语句中使用分页（RowBounds）。默认不开启
    configuration.setSafeRowBoundsEnabled(booleanValueOf(props.getProperty("safeRowBoundsEnabled"), false));
    //MyBatis 利用本地缓存机制（Local Cache）防止循环引用（circular references）和加速重复嵌套查询。
    //默认值为 SESSION，这种情况下会缓存一个会话中执行的所有查询。
    //若设置值为 STATEMENT，本地会话仅用在语句执行上，对相同 SqlSession 的不同调用将不会共享数据
    configuration.setLocalCacheScope(LocalCacheScope.valueOf(props.getProperty("localCacheScope", "SESSION")));
    //当没有为参数提供特定的 JDBC 类型时，为空值指定 JDBC 类型。
    //某些驱动需要指定列的 JDBC 类型，多数情况直接用一般类型即可，比如 NULL、VARCHAR 或 OTHER。
    configuration.setJdbcTypeForNull(JdbcType.valueOf(props.getProperty("jdbcTypeForNull", "OTHER")));
    //指定哪个对象的方法触发一次延迟加载
    configuration.setLazyLoadTriggerMethods(
      stringSetValueOf(props.getProperty("lazyLoadTriggerMethods"), "equals,clone,hashCode,toString"));
    //允许在嵌套语句中使用分页（ResultHandler）
    configuration.setSafeResultHandlerEnabled(booleanValueOf(props.getProperty("safeResultHandlerEnabled"), true));
    //指定动态 SQL 生成的默认语言
    configuration.setDefaultScriptingLanguage(resolveClass(props.getProperty("defaultScriptingLanguage")));
    //默认的枚举类处理器
    configuration.setDefaultEnumTypeHandler(resolveClass(props.getProperty("defaultEnumTypeHandler")));
    //指定当结果集中值为 null 的时候是否调用映射对象的 setter（map 对象时为 put）方法，
    //这对于有 Map.keySet() 依赖或 null 值初始化的时候是有用的。注意基本类型（int、boolean等）是不能设置成 null 的。
    //默认不启用
    configuration.setCallSettersOnNulls(booleanValueOf(props.getProperty("callSettersOnNulls"), false));
    //允许使用方法签名中的名称作为语句参数名称。默认使用
    configuration.setUseActualParamName(booleanValueOf(props.getProperty("useActualParamName"), true));
    //当返回行的所有列都是空时，MyBatis默认返回null。
    //当开启这个设置时，MyBatis会返回一个空实例。 请注意，它也适用于嵌套的结果集
    configuration.setReturnInstanceForEmptyRow(booleanValueOf(props.getProperty("returnInstanceForEmptyRow"), false));
    //指定 MyBatis 增加到日志名称的前缀
    configuration.setLogPrefix(props.getProperty("logPrefix"));
    //指定一个提供Configuration实例的类. 这个被返回的Configuration实例是用来加载被反序列化对象的懒加载属性值
    configuration.setConfigurationFactory(resolveClass(props.getProperty("configurationFactory")));
  }

  /**
   * 解析 <environments /> 标签
   *
   * @param context
   * @throws Exception
   */
  private void environmentsElement(XNode context) throws Exception {
    if (context != null) {
      // <1> environment 属性非空，从 default 属性获得
      if (environment == null) {
        environment = context.getStringAttribute("default");
      }
      // 遍历 XNode 节点
      for (XNode child : context.getChildren()) {
        // <2> 判断 environment 是否匹配
        String id = child.getStringAttribute("id");
        if (isSpecifiedEnvironment(id)) {
          // <3> 解析 `<transactionManager />` 标签，返回 TransactionFactory 对象
          TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
          // <4> 解析 `<dataSource />` 标签，返回 DataSourceFactory 对象
          DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
          DataSource dataSource = dsFactory.getDataSource();
          // <5> 创建 Environment.Builder 对象
          Environment.Builder environmentBuilder = new Environment.Builder(id).transactionFactory(txFactory)
            .dataSource(dataSource);
          // <6> 构造 Environment 对象，并设置到 configuration 中
          configuration.setEnvironment(environmentBuilder.build());
        }
      }
    }
  }

  /**
   * 解析 <databaseIdProvider /> 标签
   *
   * @param context
   * @throws Exception
   */
  private void databaseIdProviderElement(XNode context) throws Exception {
    DatabaseIdProvider databaseIdProvider = null;
    if (context != null) {
      // <1> 获得 DatabaseIdProvider 的类
      String type = context.getStringAttribute("type");
      // awful patch to keep backward compatibility
      // 保持向后兼容
      if ("VENDOR".equals(type)) {
        type = "DB_VENDOR";
      }
      // <2> 获得 Properties 对象
      Properties properties = context.getChildrenAsProperties();
      // <3> 创建 DatabaseIdProvider 对象，并设置对应的属性
      databaseIdProvider = (DatabaseIdProvider) resolveClass(type).getDeclaredConstructor().newInstance();
      databaseIdProvider.setProperties(properties);
    }
    Environment environment = configuration.getEnvironment();
    if (environment != null && databaseIdProvider != null) {
      // <4> 获得对应的 databaseId 编号
      String databaseId = databaseIdProvider.getDatabaseId(environment.getDataSource());
      configuration.setDatabaseId(databaseId);
    }
  }

  /**
   * 解析 <transactionManager /> 标签，返回 TransactionFactory 对象。
   *
   * @param context
   * @return
   * @throws Exception
   */
  private TransactionFactory transactionManagerElement(XNode context) throws Exception {
    if (context != null) {
      // 获得 TransactionFactory 的类
      String type = context.getStringAttribute("type");
      // 获得 Properties 属性
      Properties props = context.getChildrenAsProperties();
      // 创建 TransactionFactory 对象，并设置属性
      TransactionFactory factory = (TransactionFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a TransactionFactory.");
  }

  /**
   * 解析 <dataSource /> 标签，返回 DataSourceFactory 对象，而后返回 DataSource 对象。
   *
   * @param context
   * @return
   * @throws Exception
   */
  private DataSourceFactory dataSourceElement(XNode context) throws Exception {
    if (context != null) {
      // 获得 DataSourceFactory 的类
      String type = context.getStringAttribute("type");
      // 获得 Properties 属性
      Properties props = context.getChildrenAsProperties();
      // 创建 DataSourceFactory 对象，并设置属性
      DataSourceFactory factory = (DataSourceFactory) resolveClass(type).getDeclaredConstructor().newInstance();
      factory.setProperties(props);
      return factory;
    }
    throw new BuilderException("Environment declaration requires a DataSourceFactory.");
  }

  /**
   * 解析 <typeHandlers /> 标签
   *
   * @param parent
   */
  private void typeHandlerElement(XNode parent) {
    //例子如下：
    //<typeHandlers>
    //    <typeHandler handler="cn.com.mybatis.test.DateTypeHandler" javaType="java.util.Date" jdbcType="TIMESTAMP"/>
    //</typeHandlers>
    if (parent != null) {
      // 遍历子节点。即遍历所有typeHandler
      for (XNode child : parent.getChildren()) {
        // <1> 如果是 package 标签，则扫描该包。批量处理
        if ("package".equals(child.getName())) {
          String typeHandlerPackage = child.getStringAttribute("name");
          typeHandlerRegistry.register(typeHandlerPackage);
        }
        // <2> 如果是 typeHandler 标签，则注册该 typeHandler 信息。单个处理
        else {
          // 获得 javaType、jdbcType、handler
          String javaTypeName = child.getStringAttribute("javaType");
          String jdbcTypeName = child.getStringAttribute("jdbcType");
          String handlerTypeName = child.getStringAttribute("handler");
          Class<?> javaTypeClass = resolveClass(javaTypeName);
          JdbcType jdbcType = resolveJdbcType(jdbcTypeName);
          Class<?> typeHandlerClass = resolveClass(handlerTypeName);
          // 注册 typeHandler
          if (javaTypeClass != null) {
            if (jdbcType == null) {
              typeHandlerRegistry.register(javaTypeClass, typeHandlerClass);
            } else {
              typeHandlerRegistry.register(javaTypeClass, jdbcType, typeHandlerClass);
            }
          } else {
            typeHandlerRegistry.register(typeHandlerClass);
          }
        }
      }
    }
  }

  /**
   * 解析 <mappers /> 标签
   *
   * @param parent
   * @throws Exception
   */
  private void mapperElement(XNode parent) throws Exception {
    //例子如下：
    // 方式一：
    //<mappers>
    //    <mapper resource="org/mybatis/mappers/UserMapper.xml"/>
    //    <mapper resource="org/mybatis/mappers/ProductMapper.xml"/>
    //    <mapper resource="org/mybatis/mappers/ManagerMapper.xml"/>
    //</mappers>
    //方式二：
    //<mappers>
    //    <mapper url="file:///var/mappers/UserMapper.xml"/>
    //    <mapper url="file:///var/mappers/ProductMapper.xml"/>
    //    <mapper url="file:///var/mappers/ManagerMapper.xml"/>
    //</mappers>
    //方式三：
    //<mappers>
    //    <mapper class="org.mybatis.mappers.UserMapper"/>
    //    <mapper class="org.mybatis.mappers.ProductMapper"/>
    //    <mapper class="org.mybatis.mappers.ManagerMapper"/>
    //</mappers>
    //方式四：
    //<mappers>
    //    <package name="org.mybatis.mappers"/>
    //</mappers>
    if (parent != null) {
      // <0> 遍历子节点
      for (XNode child : parent.getChildren()) {
        // <1> 如果是 package 标签，则扫描该包
        if ("package".equals(child.getName())) {
          // 获得包名
          String mapperPackage = child.getStringAttribute("name");
          // 添加到 configuration 中
          configuration.addMappers(mapperPackage);
        }
        // 如果是 mapper 标签，
        else {
          // 获得 resource、url、class 属性
          //使用本地配置文件
          String resource = child.getStringAttribute("resource");
          //使用远程配置文件
          String url = child.getStringAttribute("url");
          //使用本地class文件
          String mapperClass = child.getStringAttribute("class");
          // <2> 使用相对于类路径的资源引用
          if (resource != null && url == null && mapperClass == null) {
            ErrorContext.instance().resource(resource);
            // 获得 resource 的 InputStream 对象
            InputStream inputStream = Resources.getResourceAsStream(resource);
            // 创建 XMLMapperBuilder 对象
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource,
              configuration.getSqlFragments());
            // 执行解析
            mapperParser.parse();
          }
          // <3> 使用url定位到远程资源
          else if (resource == null && url != null && mapperClass == null) {
            ErrorContext.instance().resource(url);
            // 获得 url 的 InputStream 对象
            InputStream inputStream = Resources.getUrlAsStream(url);
            // 创建 XMLMapperBuilder 对象
            XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url,
              configuration.getSqlFragments());
            // 执行解析
            mapperParser.parse();
          }
          // <4> 使用映射器接口实现类的完全限定类名
          else if (resource == null && url == null && mapperClass != null) {
            // 获得 Mapper 接口
            Class<?> mapperInterface = Resources.classForName(mapperClass);
            // 添加到 configuration 中
            configuration.addMapper(mapperInterface);
          } else {
            throw new BuilderException(
              "A mapper element may only specify a url, resource or class, but not more than one.");
          }
        }
      }
    }
  }

  private boolean isSpecifiedEnvironment(String id) {
    if (environment == null) {
      throw new BuilderException("No environment specified.");
    } else if (id == null) {
      throw new BuilderException("Environment requires an id attribute.");
    } else if (environment.equals(id)) {
      return true;
    }
    return false;
  }

}
