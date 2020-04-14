/**
<<<<<<< HEAD
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
package org.apache.ibatis.builder.annotation;

import org.apache.ibatis.annotations.*;
=======
 *    Copyright 2009-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.builder.annotation;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.CacheNamespaceRef;
import org.apache.ibatis.annotations.Case;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.Property;
import org.apache.ibatis.annotations.Result;
>>>>>>> mybatis-3-trunk/master
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public class MapperAnnotationBuilder {

<<<<<<< HEAD
    /**
     * SQL 操作注解集合
     */
    private static final Set<Class<? extends Annotation>> SQL_ANNOTATION_TYPES = new HashSet<>();
    /**
     * SQL 操作提供者注解集合
     */
    private static final Set<Class<? extends Annotation>> SQL_PROVIDER_ANNOTATION_TYPES = new HashSet<>();

    private final Configuration configuration;
    private final MapperBuilderAssistant assistant;
    /**
     * Mapper 接口类
     */
    private final Class<?> type;

    static {
        SQL_ANNOTATION_TYPES.add(Select.class);
        SQL_ANNOTATION_TYPES.add(Insert.class);
        SQL_ANNOTATION_TYPES.add(Update.class);
        SQL_ANNOTATION_TYPES.add(Delete.class);

        SQL_PROVIDER_ANNOTATION_TYPES.add(SelectProvider.class);
        SQL_PROVIDER_ANNOTATION_TYPES.add(InsertProvider.class);
        SQL_PROVIDER_ANNOTATION_TYPES.add(UpdateProvider.class);
        SQL_PROVIDER_ANNOTATION_TYPES.add(DeleteProvider.class);
    }

    public MapperAnnotationBuilder(Configuration configuration, Class<?> type) {
        // 创建 MapperBuilderAssistant 对象
        String resource = type.getName().replace('.', '/') + ".java (best guess)";
        this.assistant = new MapperBuilderAssistant(configuration, resource);
        this.configuration = configuration;
        this.type = type;
    }

    public void parse() {
        // <1> 判断当前 Mapper 接口是否应加载过。
        String resource = type.toString();
        if (!configuration.isResourceLoaded(resource)) {
            // <2> 加载对应的 XML Mapper
            loadXmlResource();
            // <3> 标记该 Mapper 接口已经加载过
            configuration.addLoadedResource(resource);
            // <4> 设置 namespace 属性
            assistant.setCurrentNamespace(type.getName());
            // <5> 解析 @CacheNamespace 注解
            parseCache();
            // <6> 解析 @CacheNamespaceRef 注解
            parseCacheRef();
            // <7> 遍历每个方法，解析其上的注解
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                try {
                    // issue #237
                    if (!method.isBridge()) {
                        // <7.1> 执行解析
                        parseStatement(method);
                    }
                } catch (IncompleteElementException e) {
                    // <7.2> 解析失败，添加到 configuration 中
                    configuration.addIncompleteMethod(new MethodResolver(this, method));
                }
            }
=======
  private static final Set<Class<? extends Annotation>> SQL_ANNOTATION_TYPES = new HashSet<>();
  private static final Set<Class<? extends Annotation>> SQL_PROVIDER_ANNOTATION_TYPES = new HashSet<>();

  private final Configuration configuration;
  private final MapperBuilderAssistant assistant;
  private final Class<?> type;

  static {
    SQL_ANNOTATION_TYPES.add(Select.class);
    SQL_ANNOTATION_TYPES.add(Insert.class);
    SQL_ANNOTATION_TYPES.add(Update.class);
    SQL_ANNOTATION_TYPES.add(Delete.class);

    SQL_PROVIDER_ANNOTATION_TYPES.add(SelectProvider.class);
    SQL_PROVIDER_ANNOTATION_TYPES.add(InsertProvider.class);
    SQL_PROVIDER_ANNOTATION_TYPES.add(UpdateProvider.class);
    SQL_PROVIDER_ANNOTATION_TYPES.add(DeleteProvider.class);
  }

  public MapperAnnotationBuilder(Configuration configuration, Class<?> type) {
    String resource = type.getName().replace('.', '/') + ".java (best guess)";
    this.assistant = new MapperBuilderAssistant(configuration, resource);
    this.configuration = configuration;
    this.type = type;
  }

  public void parse() {
    String resource = type.toString();
    if (!configuration.isResourceLoaded(resource)) {
      loadXmlResource();
      configuration.addLoadedResource(resource);
      assistant.setCurrentNamespace(type.getName());
      parseCache();
      parseCacheRef();
      for (Method method : type.getMethods()) {
        if (!canHaveStatement(method)) {
          continue;
        }
        if (getSqlCommandType(method) == SqlCommandType.SELECT && method.getAnnotation(ResultMap.class) == null) {
          parseResultMap(method);
        }
        try {
          parseStatement(method);
        } catch (IncompleteElementException e) {
          configuration.addIncompleteMethod(new MethodResolver(this, method));
>>>>>>> mybatis-3-trunk/master
        }
        // <8> 解析待定的方法
        parsePendingMethods();
    }

<<<<<<< HEAD
    /**
     *
     */
    private void parsePendingMethods() {
        // 获得 MethodResolver 集合，并遍历进行处理
        Collection<MethodResolver> incompleteMethods = configuration.getIncompleteMethods();
        synchronized (incompleteMethods) {
            Iterator<MethodResolver> iter = incompleteMethods.iterator();
            while (iter.hasNext()) {
                try {
                    // 执行解析
                    iter.next().resolve();
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // This method is still missing a resource
                }
            }
=======
  private boolean canHaveStatement(Method method) {
    // issue #237
    return !method.isBridge() && !method.isDefault();
  }

  private void parsePendingMethods() {
    Collection<MethodResolver> incompleteMethods = configuration.getIncompleteMethods();
    synchronized (incompleteMethods) {
      Iterator<MethodResolver> iter = incompleteMethods.iterator();
      while (iter.hasNext()) {
        try {
          iter.next().resolve();
          iter.remove();
        } catch (IncompleteElementException e) {
          // This method is still missing a resource
>>>>>>> mybatis-3-trunk/master
        }
    }

    /**
     * 加载对应的 XML Mapper 。
     */
    private void loadXmlResource() {
        // Spring may not know the real resource name so we check a flag
        // to prevent loading again a resource twice
        // this flag is set at XMLMapperBuilder#bindMapperForNamespace
        // <1> 判断 Mapper XML 是否已经加载过，如果加载过，就不加载了。
        // 此处，是为了避免和 XMLMapperBuilder#parse() 方法冲突，重复解析
        if (!configuration.isResourceLoaded("namespace:" + type.getName())) {
            // <2> 获得 InputStream 对象
            String xmlResource = type.getName().replace('.', '/') + ".xml";
            // #1347
            InputStream inputStream = type.getResourceAsStream("/" + xmlResource);
            if (inputStream == null) {
                // Search XML mapper that is not in the module but in the classpath.
                try {
                    inputStream = Resources.getResourceAsStream(type.getClassLoader(), xmlResource);
                } catch (IOException e2) {
                    // ignore, resource is not required
                }
            }
            // <2> 创建 XMLMapperBuilder 对象，执行解析
            if (inputStream != null) {
                XMLMapperBuilder xmlParser = new XMLMapperBuilder(inputStream, assistant.getConfiguration(), xmlResource,
                        configuration.getSqlFragments(), type.getName());
                xmlParser.parse();
            }
        }
    }

    /**
     * 解析 @CacheNamespace 注解
     */
    private void parseCache() {
        // <1> 获得类上的 @CacheNamespace 注解
        CacheNamespace cacheDomain = type.getAnnotation(CacheNamespace.class);
        if (cacheDomain != null) {
            // <2> 获得各种属性
            Integer size = cacheDomain.size() == 0 ? null : cacheDomain.size();
            Long flushInterval = cacheDomain.flushInterval() == 0 ? null : cacheDomain.flushInterval();
            // <3> 获得 Properties 属性.将 @Property 注解数组，转换成 Properties 对象
            Properties props = convertToProperties(cacheDomain.properties());
            // <4> 创建 Cache 对象
            assistant.useNewCache(cacheDomain.implementation(), cacheDomain.eviction(), flushInterval, size,
                    cacheDomain.readWrite(), cacheDomain.blocking(), props);
        }
    }

    /**
     * 将 @Property 注解数组，转换成 Properties 对象
     *
     * @param properties
     * @return
     */
    private Properties convertToProperties(Property[] properties) {
        if (properties.length == 0) {
            return null;
        }
        Properties props = new Properties();
        for (Property property : properties) {
            props.setProperty(property.name(), PropertyParser.parse(property.value(), configuration.getVariables()));
        }
        return props;
    }

    /**
     * 解析 @CacheNamespaceRef 注解
     */
    private void parseCacheRef() {
        // 获得类上的 @CacheNamespaceRef 注解
        CacheNamespaceRef cacheDomainRef = type.getAnnotation(CacheNamespaceRef.class);
        if (cacheDomainRef != null) {
            // <2> 获得各种属性
            Class<?> refType = cacheDomainRef.value();
            String refName = cacheDomainRef.name();
            // <2> 校验，如果 refType 和 refName 都为空，则抛出 BuilderException 异常
            if (refType == void.class && refName.isEmpty()) {
                throw new BuilderException("Should be specified either value() or name() attribute in the @CacheNamespaceRef");
            }
            // <2> 校验，如果 refType 和 refName 都不为空，则抛出 BuilderException 异常
            if (refType != void.class && !refName.isEmpty()) {
                throw new BuilderException("Cannot use both value() and name() attribute in the @CacheNamespaceRef");
            }
            // <2> 获得最终的 namespace 属性
            String namespace = (refType != void.class) ? refType.getName() : refName;
            try {
                // <3> 获得指向的 Cache 对象
                assistant.useCacheRef(namespace);
            } catch (IncompleteElementException e) {
                configuration.addIncompleteCacheRef(new CacheRefResolver(assistant, namespace));
            }
        }
<<<<<<< HEAD
    }

    /**
     * 生成 resultMapId
     *
     * @param method
     * @return
     */
    private String parseResultMap(Method method) {
        // <1> 获得返回类型
        Class<?> returnType = getReturnType(method);
        // <2> 获得 @ConstructorArgs、@Results、@TypeDiscriminator 注解
        ConstructorArgs args = method.getAnnotation(ConstructorArgs.class);
        Results results = method.getAnnotation(Results.class);
        TypeDiscriminator typeDiscriminator = method.getAnnotation(TypeDiscriminator.class);
        // <3> 生成 resultMapId
        String resultMapId = generateResultMapName(method);
        // <4> 生成 ResultMap 对象
        applyResultMap(resultMapId, returnType, argsIf(args), resultsIf(results), typeDiscriminator);
        return resultMapId;
    }

    /**
     * 生成 resultMapId
     * <p>
     * mapper全限定名 + '.' + results配置的iD
     * <p>
     * 或
     * <p>
     * mapper全限定名 + '.' + 方法名 + '-' +参数1类型+'-'+参数2类型+...
     * 或
     * <p>
     * mapper全限定名 + '.' + 方法名 + '-' +void
     *
     * @param method
     * @return
     */
    private String generateResultMapName(Method method) {
        /**
         *
         @Select({"select id, name, class_id from my_student"})
         @Results({
         @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
         @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
         @Result(column="class_id", property="classId", jdbcType=JdbcType.INTEGER)
         })
         List<Student> selectAll();

         Results：
         String id() default "";

         Result[] value() default {};
         *
         */

        // 第一种情况，已经声明
        // 如果有 @Results 注解，并且有设置 id 属性，则直接返回。格式为：`${type.name}.${Results.id}` 。
        Results results = method.getAnnotation(Results.class);
        if (results != null && !results.id().isEmpty()) {
            //mapper类的全限定名加点加自定义的ID
            return type.getName() + "." + results.id();
        }
        // 第二种情况，自动生成
        // 获得 suffix 前缀，相当于方法参数构成的签名
        StringBuilder suffix = new StringBuilder();
        for (Class<?> c : method.getParameterTypes()) {
            suffix.append("-");
            suffix.append(c.getSimpleName());
        }
        //如果没有方法参数，则使用void拼接
        if (suffix.length() < 1) {
            suffix.append("-void");
        }
        //mapper类的全限定名加点加方法名称加方法参数名称或void
        return type.getName() + "." + method.getName() + suffix;
    }

    /**
     * 创建 ResultMap 对象
     *
     * @param resultMapId
     * @param returnType
     * @param args
     * @param results
     * @param discriminator
     */
    private void applyResultMap(String resultMapId, Class<?> returnType, Arg[] args, Result[] results,
                                TypeDiscriminator discriminator) {
        // <1> 创建 ResultMapping 数组
=======
      }
      if (inputStream != null) {
        XMLMapperBuilder xmlParser = new XMLMapperBuilder(inputStream, assistant.getConfiguration(), xmlResource, configuration.getSqlFragments(), type.getName());
        xmlParser.parse();
      }
    }
  }

  private void parseCache() {
    CacheNamespace cacheDomain = type.getAnnotation(CacheNamespace.class);
    if (cacheDomain != null) {
      Integer size = cacheDomain.size() == 0 ? null : cacheDomain.size();
      Long flushInterval = cacheDomain.flushInterval() == 0 ? null : cacheDomain.flushInterval();
      Properties props = convertToProperties(cacheDomain.properties());
      assistant.useNewCache(cacheDomain.implementation(), cacheDomain.eviction(), flushInterval, size, cacheDomain.readWrite(), cacheDomain.blocking(), props);
    }
  }

  private Properties convertToProperties(Property[] properties) {
    if (properties.length == 0) {
      return null;
    }
    Properties props = new Properties();
    for (Property property : properties) {
      props.setProperty(property.name(),
          PropertyParser.parse(property.value(), configuration.getVariables()));
    }
    return props;
  }

  private void parseCacheRef() {
    CacheNamespaceRef cacheDomainRef = type.getAnnotation(CacheNamespaceRef.class);
    if (cacheDomainRef != null) {
      Class<?> refType = cacheDomainRef.value();
      String refName = cacheDomainRef.name();
      if (refType == void.class && refName.isEmpty()) {
        throw new BuilderException("Should be specified either value() or name() attribute in the @CacheNamespaceRef");
      }
      if (refType != void.class && !refName.isEmpty()) {
        throw new BuilderException("Cannot use both value() and name() attribute in the @CacheNamespaceRef");
      }
      String namespace = (refType != void.class) ? refType.getName() : refName;
      try {
        assistant.useCacheRef(namespace);
      } catch (IncompleteElementException e) {
        configuration.addIncompleteCacheRef(new CacheRefResolver(assistant, namespace));
      }
    }
  }

  private String parseResultMap(Method method) {
    Class<?> returnType = getReturnType(method);
    Arg[] args = method.getAnnotationsByType(Arg.class);
    Result[] results = method.getAnnotationsByType(Result.class);
    TypeDiscriminator typeDiscriminator = method.getAnnotation(TypeDiscriminator.class);
    String resultMapId = generateResultMapName(method);
    applyResultMap(resultMapId, returnType, args, results, typeDiscriminator);
    return resultMapId;
  }

  private String generateResultMapName(Method method) {
    Results results = method.getAnnotation(Results.class);
    if (results != null && !results.id().isEmpty()) {
      return type.getName() + "." + results.id();
    }
    StringBuilder suffix = new StringBuilder();
    for (Class<?> c : method.getParameterTypes()) {
      suffix.append("-");
      suffix.append(c.getSimpleName());
    }
    if (suffix.length() < 1) {
      suffix.append("-void");
    }
    return type.getName() + "." + method.getName() + suffix;
  }

  private void applyResultMap(String resultMapId, Class<?> returnType, Arg[] args, Result[] results, TypeDiscriminator discriminator) {
    List<ResultMapping> resultMappings = new ArrayList<>();
    applyConstructorArgs(args, returnType, resultMappings);
    applyResults(results, returnType, resultMappings);
    Discriminator disc = applyDiscriminator(resultMapId, returnType, discriminator);
    // TODO add AutoMappingBehaviour
    assistant.addResultMap(resultMapId, returnType, null, disc, resultMappings, null);
    createDiscriminatorResultMaps(resultMapId, returnType, discriminator);
  }

  private void createDiscriminatorResultMaps(String resultMapId, Class<?> resultType, TypeDiscriminator discriminator) {
    if (discriminator != null) {
      for (Case c : discriminator.cases()) {
        String caseResultMapId = resultMapId + "-" + c.value();
>>>>>>> mybatis-3-trunk/master
        List<ResultMapping> resultMappings = new ArrayList<>();
        // <2> 将 @Arg[] 注解数组，解析成对应的 ResultMapping 对象们，并添加到 resultMappings 中。
        applyConstructorArgs(args, returnType, resultMappings);
        // <3> 将 @Result[] 注解数组，解析成对应的 ResultMapping 对象们，并添加到 resultMappings 中。
        applyResults(results, returnType, resultMappings);
        // <4> 创建 Discriminator 对象
        Discriminator disc = applyDiscriminator(resultMapId, returnType, discriminator);
        // TODO add AutoMappingBehaviour
        // <5> ResultMap 对象
        assistant.addResultMap(resultMapId, returnType, null, disc, resultMappings, null);
        // <6> 创建 Discriminator 的 ResultMap 对象们
        createDiscriminatorResultMaps(resultMapId, returnType, discriminator);
    }

    /**
     * 创建 Discriminator 的 ResultMap 对象们
     * @param resultMapId
     * @param resultType
     * @param discriminator
     */
    private void createDiscriminatorResultMaps(String resultMapId, Class<?> resultType, TypeDiscriminator discriminator) {
        if (discriminator != null) {
            // 遍历 @Case 注解
            for (Case c : discriminator.cases()) {
                // 创建 @Case 注解的 ResultMap 的编号
                String caseResultMapId = resultMapId + "-" + c.value();
                // 创建 ResultMapping 数组
                List<ResultMapping> resultMappings = new ArrayList<>();
                // issue #136
                // 将 @Arg[] 注解数组，解析成对应的 ResultMapping 对象们，并添加到 resultMappings 中。
                applyConstructorArgs(c.constructArgs(), resultType, resultMappings);
                // 将 @Result[] 注解数组，解析成对应的 ResultMapping 对象们，并添加到 resultMappings 中。
                applyResults(c.results(), resultType, resultMappings);
                // TODO add AutoMappingBehaviour
                // 创建 ResultMap 对象
                assistant.addResultMap(caseResultMapId, c.type(), resultMapId, null, resultMappings, null);
            }
        }
    }

    /**
     * 创建 Discriminator 对象
     * @param resultMapId
     * @param resultType
     * @param discriminator
     * @return
     */
    private Discriminator applyDiscriminator(String resultMapId, Class<?> resultType, TypeDiscriminator discriminator) {
        if (discriminator != null) {
            // 解析各种属性
            String column = discriminator.column();
            Class<?> javaType = discriminator.javaType() == void.class ? String.class : discriminator.javaType();
            JdbcType jdbcType = discriminator.jdbcType() == JdbcType.UNDEFINED ? null : discriminator.jdbcType();
            @SuppressWarnings("unchecked")
            // 获得 TypeHandler 类
            Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>) (discriminator
                    .typeHandler() == UnknownTypeHandler.class ? null : discriminator.typeHandler());
            // 遍历 @Case[] 注解数组，解析成 discriminatorMap 集合
            Case[] cases = discriminator.cases();
            Map<String, String> discriminatorMap = new HashMap<>();
            for (Case c : cases) {
                String value = c.value();
                String caseResultMapId = resultMapId + "-" + value;
                discriminatorMap.put(value, caseResultMapId);
            }
            // 创建 Discriminator 对象
            return assistant.buildDiscriminator(resultType, column, javaType, jdbcType, typeHandler, discriminatorMap);
        }
        return null;
    }

    /**
     * 解析方法上的 SQL 操作相关的注解
     *
     * @param method
     */
    void parseStatement(Method method) {
        // <1> 获得参数的类型
        Class<?> parameterTypeClass = getParameterType(method);
        // <2> 获得 LanguageDriver 对象
        LanguageDriver languageDriver = getLanguageDriver(method);
        // <3> 获得 SqlSource 对象
        SqlSource sqlSource = getSqlSourceFromAnnotations(method, parameterTypeClass, languageDriver);
        if (sqlSource != null) {
            // <4> 获得各种属性
            Options options = method.getAnnotation(Options.class);
            //mappedStatementId 由类名加点加方法名组成
            final String mappedStatementId = type.getName() + "." + method.getName();
            Integer fetchSize = null;
            Integer timeout = null;
            StatementType statementType = StatementType.PREPARED;
            ResultSetType resultSetType = configuration.getDefaultResultSetType();
            SqlCommandType sqlCommandType = getSqlCommandType(method);
            //是否是select语句
            boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
            //是否需要刷新缓存，非查询语句，则需要刷新缓存
            boolean flushCache = !isSelect;
            //是否使用缓存，查询语句则使用缓存
            boolean useCache = isSelect;

            // <5> 获得 KeyGenerator 对象
            KeyGenerator keyGenerator;
            String keyProperty = null;
            String keyColumn = null;
            //处理Insert、Update、InsertProvider、UpdateProvider注解的逻辑
            if (SqlCommandType.INSERT.equals(sqlCommandType) || SqlCommandType.UPDATE.equals(sqlCommandType)) {
                // first check for SelectKey annotation - that overrides everything else
                // <5.1> 如果有 @SelectKey 注解，则进行处理
                SelectKey selectKey = method.getAnnotation(SelectKey.class);
                //有Select注解
                /**
                 *
                 @Insert("insert into user(name,age) value(#{user.name},#{user.age})")
                 @SelectKey(statement = "select last_insert_id()", keyProperty = "user.id", before = false, resultType = int.class)
                 int insert_selectKeyAnotation(@Param("user") User user);
                 *
                 */
                if (selectKey != null) {
                    //处理SelectKey注解
                    keyGenerator = handleSelectKeyAnnotation(selectKey, mappedStatementId, getParameterType(method),
                            languageDriver);
                    keyProperty = selectKey.keyProperty();
                }
                // <5.2> 如果无 @Options 注解，则根据全局配置处理
                else if (options == null) {
                    keyGenerator = configuration.isUseGeneratedKeys() ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
                }
                // <5.3> 如果有 @Options 注解，则使用该注解的配置处理
                else {
                    keyGenerator = options.useGeneratedKeys() ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE;
                    keyProperty = options.keyProperty();
                    keyColumn = options.keyColumn();
                }
            } else {
                keyGenerator = NoKeyGenerator.INSTANCE;
            }

            // <6> 初始化各种属性
            // 配置了Option注解
            /**
             *
             @Options(useGeneratedKeys = true, keyProperty = "instanceId", keyColumn = "instance_id")


             boolean useCache() default true;  表示本次查询结果被缓存以提高下次查询速度

             boolean flushCache() default false;    表示下次查询时不刷新缓存

             ResultSetType resultSetType() default ResultSetType.FORWARD_ONLY;

             StatementType statementType() default StatementType.PREPARED;

             int fetchSize() default -1;

             int timeout() default -1;

             boolean useGeneratedKeys() default false;

             String keyProperty() default "id";

             String keyColumn() default "";
             *
             */
            if (options != null) {
                //刷新策略
                if (FlushCachePolicy.TRUE.equals(options.flushCache())) {
                    flushCache = true;
                } else if (FlushCachePolicy.FALSE.equals(options.flushCache())) {
                    flushCache = false;
                }
                useCache = options.useCache();
                fetchSize = options.fetchSize() > -1 || options.fetchSize() == Integer.MIN_VALUE ? options.fetchSize() : null; // issue
                // #348
                timeout = options.timeout() > -1 ? options.timeout() : null;
                statementType = options.statementType();
                if (options.resultSetType() != ResultSetType.DEFAULT) {
                    resultSetType = options.resultSetType();
                }
            }

            // <7> 获得 resultMapId 编号字符串
            String resultMapId = null;
            // <7.1> 如果有 @ResultMap 注解，使用该注解为 resultMapId 属性
            ResultMap resultMapAnnotation = method.getAnnotation(ResultMap.class);
            if (resultMapAnnotation != null) {
                resultMapId = String.join(",", resultMapAnnotation.value());
            }
            // <7.2> 如果无 @ResultMap 注解，解析其它注解，作为 resultMapId 属性
            else if (isSelect) {
                resultMapId = parseResultMap(method);
            }

            // 构建 MappedStatement 对象
            assistant.addMappedStatement(mappedStatementId, sqlSource, statementType, sqlCommandType, fetchSize, timeout,
                    // ParameterMapID
                    null, parameterTypeClass, resultMapId, getReturnType(method), resultSetType, flushCache, useCache,
                    // TODO gcode issue #577
                    false, keyGenerator, keyProperty, keyColumn,
                    // DatabaseID
                    null, languageDriver,
                    // ResultSets
                    options != null ? nullOrEmpty(options.resultSets()) : null);
        }
<<<<<<< HEAD
    }

    /**
     * 获得 LanguageDriver 对象
     *
     * @param method
     * @return
     */
    private LanguageDriver getLanguageDriver(Method method) {
        // 解析 @Lang 注解，获得对应的类型
        Lang lang = method.getAnnotation(Lang.class);
        Class<? extends LanguageDriver> langClass = null;
        if (lang != null) {
            langClass = lang.value();
=======
      }

      String resultMapId = null;
      if (isSelect) {
        ResultMap resultMapAnnotation = method.getAnnotation(ResultMap.class);
        if (resultMapAnnotation != null) {
          resultMapId = String.join(",", resultMapAnnotation.value());
        } else {
          resultMapId = generateResultMapName(method);
        }
      }

      assistant.addMappedStatement(
          mappedStatementId,
          sqlSource,
          statementType,
          sqlCommandType,
          fetchSize,
          timeout,
          // ParameterMapID
          null,
          parameterTypeClass,
          resultMapId,
          getReturnType(method),
          resultSetType,
          flushCache,
          useCache,
          // TODO gcode issue #577
          false,
          keyGenerator,
          keyProperty,
          keyColumn,
          // DatabaseID
          null,
          languageDriver,
          // ResultSets
          options != null ? nullOrEmpty(options.resultSets()) : null);
    }
  }

  private LanguageDriver getLanguageDriver(Method method) {
    Lang lang = method.getAnnotation(Lang.class);
    Class<? extends LanguageDriver> langClass = null;
    if (lang != null) {
      langClass = lang.value();
    }
    return configuration.getLanguageDriver(langClass);
  }

  private Class<?> getParameterType(Method method) {
    Class<?> parameterType = null;
    Class<?>[] parameterTypes = method.getParameterTypes();
    for (Class<?> currentParameterType : parameterTypes) {
      if (!RowBounds.class.isAssignableFrom(currentParameterType) && !ResultHandler.class.isAssignableFrom(currentParameterType)) {
        if (parameterType == null) {
          parameterType = currentParameterType;
        } else {
          // issue #135
          parameterType = ParamMap.class;
>>>>>>> mybatis-3-trunk/master
        }
        // 获得 LanguageDriver 对象
        // 如果 langClass 为空，即无 @Lang 注解，则会使用默认 LanguageDriver 类型
        return configuration.getLanguageDriver(langClass);
    }

    /**
     * 获得参数的类型
     *
     * @param method
     * @return
     */
    private Class<?> getParameterType(Method method) {
        Class<?> parameterType = null;
        // 遍历参数类型数组
        // 排除 RowBounds 和 ResultHandler 两种参数
        // 1. 如果是多参数，则是 ParamMap 类型
        // 2. 如果是单参数，则是该参数的类型
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> currentParameterType : parameterTypes) {
            if (!RowBounds.class.isAssignableFrom(currentParameterType)
                    && !ResultHandler.class.isAssignableFrom(currentParameterType)) {
                if (parameterType == null) {
                    parameterType = currentParameterType;
                } else {
                    // issue #135
                    parameterType = ParamMap.class;
                }
            }
        }
        return parameterType;
    }

    /**
     * // <1> 获得返回类型
     *
     * @param method
     * @return
     */
    private Class<?> getReturnType(Method method) {
        // 获得方法的返回类型
        Class<?> returnType = method.getReturnType();
        // 解析成对应的 Type
        Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, type);
        // 如果 Type 是 Class ，普通类
        if (resolvedReturnType instanceof Class) {
            returnType = (Class<?>) resolvedReturnType;
            // 如果是数组类型，则使用 componentType
            if (returnType.isArray()) {
                returnType = returnType.getComponentType();
            }
            // gcode issue #508
            // 如果返回类型是 void ，则尝试使用 @ResultType 注解
            if (void.class.equals(returnType)) {
                ResultType rt = method.getAnnotation(ResultType.class);
                if (rt != null) {
                    returnType = rt.value();
                }
            }
        }
        // 如果 Type 是 ParameterizedType ，泛型
        else if (resolvedReturnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) resolvedReturnType;
            // 获得泛型 rawType
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            // 如果是 Collection 或者 Cursor 类型时
            if (Collection.class.isAssignableFrom(rawType) || Cursor.class.isAssignableFrom(rawType)) {
                // 获得 <> 中实际类型
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                // 如果 actualTypeArguments 的大小为 1 ，进一步处理
                if (actualTypeArguments != null && actualTypeArguments.length == 1) {
                    Type returnTypeParameter = actualTypeArguments[0];
                    // 如果是 Class ，则直接使用 Class
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    }
                    // 如果是 ParameterizedType ，则获取 <> 中实际类型
                    else if (returnTypeParameter instanceof ParameterizedType) {
                        // (gcode issue #443) actual type can be a also a parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    }
                    // 如果是泛型数组类型，则获得 genericComponentType 对应的类
                    else if (returnTypeParameter instanceof GenericArrayType) {
                        Class<?> componentType = (Class<?>) ((GenericArrayType) returnTypeParameter).getGenericComponentType();
                        // (gcode issue #525) support List<byte[]>
                        returnType = Array.newInstance(componentType, 0).getClass();
                    }
                }
            }
            // 如果有 @MapKey 注解，并且是 Map 类型
            else if (method.isAnnotationPresent(MapKey.class) && Map.class.isAssignableFrom(rawType)) {
                // (gcode issue 504) Do not look into Maps if there is not MapKey annotation
                // 获得 <> 中实际类型
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                // 如果 actualTypeArguments 的大小为 2 ，进一步处理。
                // 为什么是 2 ，因为 Map<K, V> 呀，有 K、V 两个泛型
                if (actualTypeArguments != null && actualTypeArguments.length == 2) {
                    // 处理 V 泛型
                    Type returnTypeParameter = actualTypeArguments[1];
                    // 如果 V 泛型为 Class ，则直接使用 Class
                    if (returnTypeParameter instanceof Class<?>) {
                        returnType = (Class<?>) returnTypeParameter;
                    }
                    // 如果 V 泛型为 ParameterizedType ，则获取 <> 中实际类型
                    else if (returnTypeParameter instanceof ParameterizedType) {
                        // (gcode issue 443) actual type can be a also a parameterized type
                        returnType = (Class<?>) ((ParameterizedType) returnTypeParameter).getRawType();
                    }
                }
            }
            // 如果是 Optional 类型时
            else if (Optional.class.equals(rawType)) {
                // 获得 <> 中实际类型
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                // 因为是 Optional<T> 类型，所以 actualTypeArguments 数组大小是一
                Type returnTypeParameter = actualTypeArguments[0];
                // 如果 <T> 泛型为 Class ，则直接使用 Class
                if (returnTypeParameter instanceof Class<?>) {
                    returnType = (Class<?>) returnTypeParameter;
                }
            }
        }

        return returnType;
    }

    /**
     * 从注解中，获得 SqlSource 对象
     *
     * @param method
     * @param parameterType
     * @param languageDriver
     * @return
     */
    private SqlSource getSqlSourceFromAnnotations(Method method, Class<?> parameterType, LanguageDriver languageDriver) {
        try {
            // <1.1> <1.2> 获得方法上的 SQL_ANNOTATION_TYPES 和 SQL_PROVIDER_ANNOTATION_TYPES 对应的类型
            Class<? extends Annotation> sqlAnnotationType = getSqlAnnotationType(method);
            Class<? extends Annotation> sqlProviderAnnotationType = getSqlProviderAnnotationType(method);
            // <2> 如果 SQL_ANNOTATION_TYPES 对应的类型非空
            // 诸如@Insert、@Update、@Delete、@Select注解等
            if (sqlAnnotationType != null) {
                // 如果 SQL_PROVIDER_ANNOTATION_TYPES 对应的类型非空，则抛出 BindingException 异常，因为冲突了。
                if (sqlProviderAnnotationType != null) {
                    throw new BindingException(
                            "You cannot supply both a static SQL and SqlProvider to method named " + method.getName());
                }
                Annotation sqlAnnotation = method.getAnnotation(sqlAnnotationType);
                // <2.2> 获得 value 属性
                /**
                 *
                 @Select("<script>"
                 + "SELECT "
                 + "a.id as 'id',a.create_date as 'createDate',a.content as 'content',"
                 + "a.parent_id as 'parentId',a.first_comment_id as 'firstCommentId',"
                 + "b.id as 'fromUser.id',b.realname as 'fromUser.realname',b.avatar as 'fromUser.avatar',"
                 + "c.id as 'toUser.id',c.realname as 'toUser.realname',c.avatar as 'toUser.avatar' "
                 + "FROM t_demand_comment a "
                 + "LEFT JOIN t_user b ON b.id = a.from_uid "
                 + "LEFT JOIN t_user c ON c.id = a.to_uid "
                 + "WHERE a.demand_id = #{demandId} "
                 + "ORDER BY a.create_date ASC "
                 + "<if test='startNo!=null and pageSize != null '>"
                 + "LIMIT #{startNo},#{pageSize}"
                 + "</if>"
                 + "</script>")


                 @Select({ "SELECT "
                 + " a.id, "
                 + " a.role_name roleName, "
                 + " a.enabled, "
                 + " a.create_by createBy, "
                 + " a.create_time createTime "
                 + " FROM "
                 + " sys_role a "
                 + " WHERE "
                 + " a.id = #{roleId}" })
                 *
                 */
                final String[] strings = (String[]) sqlAnnotation.getClass().getMethod("value").invoke(sqlAnnotation);
                // <2.3> 创建 SqlSource 对象
                return buildSqlSourceFromStrings(strings, parameterType, languageDriver);
            }
            // <3> 如果 SQL_PROVIDER_ANNOTATION_TYPES 对应的类型非空
            // 诸如@InsertProvider、@UpdateProvider、@DeleteProvider、@SelectProvider注解等
            else if (sqlProviderAnnotationType != null) {
                // <3.1> 获得 SQL_PROVIDER_ANNOTATION_TYPES 对应的注解
                Annotation sqlProviderAnnotation = method.getAnnotation(sqlProviderAnnotationType);
                // <3.2> 创建 ProviderSqlSource 对象
                return new ProviderSqlSource(assistant.getConfiguration(), sqlProviderAnnotation, type, method);
            }
            // <4> 返回空
            return null;
        } catch (Exception e) {
            throw new BuilderException("Could not find value method on SQL annotation.  Cause: " + e, e);
        }
    }

    /**
     * 创建 SqlSource 对象
     *
     * @param strings
     * @param parameterTypeClass
     * @param languageDriver
     * @return
     */
    private SqlSource buildSqlSourceFromStrings(String[] strings, Class<?> parameterTypeClass,
                                                LanguageDriver languageDriver) {
        // <1> 拼接 SQL，把多个value值拼凑在一起
        final StringBuilder sql = new StringBuilder();
        for (String fragment : strings) {
            sql.append(fragment);
            sql.append(" ");
        }
        // <2> 创建 SqlSource 对象
        return languageDriver.createSqlSource(configuration, sql.toString().trim(), parameterTypeClass);
    }

    /**
     * 获得方法对应的 SQL 命令类型
     *
     * @param method
     * @return
     */
    private SqlCommandType getSqlCommandType(Method method) {
        //先获取是否是Select、Insert、Delete、Update语句
        Class<? extends Annotation> type = getSqlAnnotationType(method);

        //如果type为空，则说明是SelectProvider、InsertProvider、DeleteProvider、UpdateProvider
        if (type == null) {
            type = getSqlProviderAnnotationType(method);

            //如果两者都不是，则说明没有配置注解
            if (type == null) {
                return SqlCommandType.UNKNOWN;
            }

            //统一转成SqlCommandType的值
            if (type == SelectProvider.class) {
                type = Select.class;
            } else if (type == InsertProvider.class) {
                type = Insert.class;
            } else if (type == UpdateProvider.class) {
                type = Update.class;
            } else if (type == DeleteProvider.class) {
                type = Delete.class;
            }
        }
        return SqlCommandType.valueOf(type.getSimpleName().toUpperCase(Locale.ENGLISH));
    }

    /**
     * 获得方法上的 SQL_ANNOTATION_TYPES 类型的注解类型
     *
     * @param method
     * @return
     */
    private Class<? extends Annotation> getSqlAnnotationType(Method method) {
        return chooseAnnotationType(method, SQL_ANNOTATION_TYPES);
    }

    /**
     * 获得方法上的 SQL_ANNOTATION_TYPES 类型的注解类型
     *
     * @param method
     * @return
     */
    private Class<? extends Annotation> getSqlProviderAnnotationType(Method method) {
        return chooseAnnotationType(method, SQL_PROVIDER_ANNOTATION_TYPES);
    }

    /**
     * 获得符合指定类型的注解类型
     *
     * @param method 方法
     * @param types  指定类型
     * @return 查到的注解类型
     */
    private Class<? extends Annotation> chooseAnnotationType(Method method, Set<Class<? extends Annotation>> types) {
        for (Class<? extends Annotation> type : types) {
            Annotation annotation = method.getAnnotation(type);
            if (annotation != null) {
                return type;
            }
        }
        return null;
    }

    /**
     * 将 @Result[] 注解数组，解析成对应的 ResultMapping 对象们，并添加到 resultMappings 中
     *
     * @param results
     * @param resultType
     * @param resultMappings
     */
    private void applyResults(Result[] results, Class<?> resultType, List<ResultMapping> resultMappings) {
        // 遍历 @Result[] 数组
        for (Result result : results) {
            // 创建 ResultFlag 数组
            List<ResultFlag> flags = new ArrayList<>();
            if (result.id()) {
                flags.add(ResultFlag.ID);
            }
            @SuppressWarnings("unchecked")
            Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>) ((result
                    .typeHandler() == UnknownTypeHandler.class) ? null : result.typeHandler());
            // 构建 ResultMapping 对象
            ResultMapping resultMapping = assistant.buildResultMapping(resultType, nullOrEmpty(result.property()),
                    nullOrEmpty(result.column()), result.javaType() == void.class ? null : result.javaType(),
                    result.jdbcType() == JdbcType.UNDEFINED ? null : result.jdbcType(),
                    //判断是否有内嵌的查询
                    hasNestedSelect(result) ?
                            //调用 #nestedSelectId(Result result) 方法，获得内嵌的查询编号
                            nestedSelectId(result) : null, null, null, null, typeHandler, flags, null, null,
                    //判断是否懒加载
                    isLazy(result));
            // 添加到 resultMappings 中
            resultMappings.add(resultMapping);
        }
<<<<<<< HEAD
    }

    /**
     * 调用 #nestedSelectId(Result result) 方法，获得内嵌的查询编号
     *
     * @param result
     * @return
     */
    private String nestedSelectId(Result result) {
        // 先获得 @One 注解
        String nestedSelect = result.one().select();
        // 获得不到，则再获得 @Many
        if (nestedSelect.length() < 1) {
            nestedSelect = result.many().select();
        }
        // 获得内嵌查询编号，格式为 `{type.name}.${select}`
        if (!nestedSelect.contains(".")) {
            nestedSelect = type.getName() + "." + nestedSelect;
        }
        return nestedSelect;
    }

    /**
     * 判断是否懒加载
     * 根据全局是否懒加载 + @One 或 @Many 注解。
     *
     * @param result
     * @return
     */
    private boolean isLazy(Result result) {
        /**
         *
         已映射语句（也就是映射器方法）的全限定名
         String select() default "";

         加载类型
         FetchType fetchType() default FetchType.DEFAULT;
         *
         */
        // 判断是否开启懒加载
        boolean isLazy = configuration.isLazyLoadingEnabled();
        // 如果有 @One 注解，并且加载方式不是默认值
        if (result.one().select().length() > 0 && FetchType.DEFAULT != result.one().fetchType()) {
            //判断是否懒加载
            isLazy = result.one().fetchType() == FetchType.LAZY;
        }
        // 如果有 @Many 注解，并且加载方式不是默认值
        else if (result.many().select().length() > 0 && FetchType.DEFAULT != result.many().fetchType()) {
            //判断是否懒加载
            isLazy = result.many().fetchType() == FetchType.LAZY;
        }
        return isLazy;
    }

    /**
     * 判断是否有内嵌的查询. 如果result注解里面配置了One注解或者Many注解，则说明有嵌套查询，相反则无
     *
     * @param result
     * @return
     */
    private boolean hasNestedSelect(Result result) {
        //不能再Result里配置了One又配置了Many注解。最多只能配置一个
        if (result.one().select().length() > 0 && result.many().select().length() > 0) {
            throw new BuilderException("Cannot use both @One and @Many annotations in the same @Result");
        }
        // 判断有 @One 或 @Many 注解
        return result.one().select().length() > 0 || result.many().select().length() > 0;
    }

    /**
     * 将 @Arg[] 注解数组，解析成对应的 ResultMapping 对象们，并添加到 resultMappings 中
     *
     * @param args
     * @param resultType
     * @param resultMappings
     */
    private void applyConstructorArgs(Arg[] args, Class<?> resultType, List<ResultMapping> resultMappings) {
        // 遍历 @Arg[] 数组
        for (Arg arg : args) {
            // 创建 ResultFlag 数组
            List<ResultFlag> flags = new ArrayList<>();
            flags.add(ResultFlag.CONSTRUCTOR);
            if (arg.id()) {
                flags.add(ResultFlag.ID);
            }
            @SuppressWarnings("unchecked")
            // 获得 TypeHandler 乐
                    Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>) (arg
                    .typeHandler() == UnknownTypeHandler.class ? null : arg.typeHandler());
            // 将当前 @Arg 注解构建成 ResultMapping 对象
            ResultMapping resultMapping = assistant.buildResultMapping(resultType, nullOrEmpty(arg.name()),
                    nullOrEmpty(arg.column()), arg.javaType() == void.class ? null : arg.javaType(),
                    arg.jdbcType() == JdbcType.UNDEFINED ? null : arg.jdbcType(), nullOrEmpty(arg.select()),
                    nullOrEmpty(arg.resultMap()), null, nullOrEmpty(arg.columnPrefix()), typeHandler, flags, null, null, false);
            // 添加到 resultMappings 中
            resultMappings.add(resultMapping);
        }
    }

    private String nullOrEmpty(String value) {
        return value == null || value.trim().length() == 0 ? null : value;
    }

    private Result[] resultsIf(Results results) {
        return results == null ? new Result[0] : results.value();
    }

    private Arg[] argsIf(ConstructorArgs args) {
        return args == null ? new Arg[0] : args.value();
    }

    /**
     * 处理 @@SelectKey 注解，生成对应的 SelectKey 对象
     *
     * @param selectKeyAnnotation
     * @param baseStatementId
     * @param parameterTypeClass
     * @param languageDriver
     * @return
     */
    private KeyGenerator handleSelectKeyAnnotation(SelectKey selectKeyAnnotation, String baseStatementId,
                                                   Class<?> parameterTypeClass, LanguageDriver languageDriver) {
        // id有类的权限定名加点加方法名加!selectKey组成
        String id = baseStatementId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
        /**
         * @SelectKey(statement = "select last_insert_id()", keyProperty = "user.id", before = false, resultType = int.class)
         *
         */
        //获取各种属性
        //返回类型，比如int
        Class<?> resultTypeClass = selectKeyAnnotation.resultType();
        //语句类型，比如statement
        StatementType statementType = selectKeyAnnotation.statementType();
        //主键属性
        String keyProperty = selectKeyAnnotation.keyProperty();
        //主键在数据库的列名
        String keyColumn = selectKeyAnnotation.keyColumn();
        //是执行insert之前还是执行后再执行当前语句。如果before配置成true，则先执行selectKey的语句，否则先执行Insert的语句
        boolean executeBefore = selectKeyAnnotation.before();

        // defaults
        // 创建 MappedStatement 需要用到的默认值
        boolean useCache = false;
        KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
        //查询数据条数，null则不限制
        Integer fetchSize = null;
        Integer timeout = null;
        boolean flushCache = false;
        String parameterMap = null;
        String resultMap = null;
        ResultSetType resultSetTypeEnum = null;

        // 创建 SqlSource 对象
        SqlSource sqlSource = buildSqlSourceFromStrings(selectKeyAnnotation.statement(), parameterTypeClass,
                languageDriver);
        //查询命令
        SqlCommandType sqlCommandType = SqlCommandType.SELECT;

        // 创建 MappedStatement 对象
        assistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap,
                parameterTypeClass, resultMap, resultTypeClass, resultSetTypeEnum, flushCache, useCache, false, keyGenerator,
                keyProperty, keyColumn, null, languageDriver, null);

        // 获得 SelectKeyGenerator 的编号，格式为 `${namespace}.${id}`
        id = assistant.applyCurrentNamespace(id, false);

        // 获得 MappedStatement 对象
        MappedStatement keyStatement = configuration.getMappedStatement(id, false);
        // 创建 SelectKeyGenerator 对象，并添加到 configuration 中
        SelectKeyGenerator answer = new SelectKeyGenerator(keyStatement, executeBefore);
        configuration.addKeyGenerator(id, answer);
        return answer;
    }
=======
        Annotation sqlAnnotation = method.getAnnotation(sqlAnnotationType);
        final String[] strings = (String[]) sqlAnnotation.getClass().getMethod("value").invoke(sqlAnnotation);
        return buildSqlSourceFromStrings(strings, parameterType, languageDriver);
      } else if (sqlProviderAnnotationType != null) {
        Annotation sqlProviderAnnotation = method.getAnnotation(sqlProviderAnnotationType);
        return new ProviderSqlSource(assistant.getConfiguration(), sqlProviderAnnotation, type, method);
      }
      return null;
    } catch (Exception e) {
      throw new BuilderException("Could not find value method on SQL annotation.  Cause: " + e, e);
    }
  }

  private SqlSource buildSqlSourceFromStrings(String[] strings, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
    return languageDriver.createSqlSource(configuration, String.join(" ", strings).trim(), parameterTypeClass);
  }

  private SqlCommandType getSqlCommandType(Method method) {
    Class<? extends Annotation> type = getSqlAnnotationType(method);

    if (type == null) {
      type = getSqlProviderAnnotationType(method);

      if (type == null) {
        return SqlCommandType.UNKNOWN;
      }

      if (type == SelectProvider.class) {
        type = Select.class;
      } else if (type == InsertProvider.class) {
        type = Insert.class;
      } else if (type == UpdateProvider.class) {
        type = Update.class;
      } else if (type == DeleteProvider.class) {
        type = Delete.class;
      }
    }

    return SqlCommandType.valueOf(type.getSimpleName().toUpperCase(Locale.ENGLISH));
  }

  private Class<? extends Annotation> getSqlAnnotationType(Method method) {
    return chooseAnnotationType(method, SQL_ANNOTATION_TYPES);
  }

  private Class<? extends Annotation> getSqlProviderAnnotationType(Method method) {
    return chooseAnnotationType(method, SQL_PROVIDER_ANNOTATION_TYPES);
  }

  private Class<? extends Annotation> chooseAnnotationType(Method method, Set<Class<? extends Annotation>> types) {
    for (Class<? extends Annotation> type : types) {
      Annotation annotation = method.getAnnotation(type);
      if (annotation != null) {
        return type;
      }
    }
    return null;
  }

  private void applyResults(Result[] results, Class<?> resultType, List<ResultMapping> resultMappings) {
    for (Result result : results) {
      List<ResultFlag> flags = new ArrayList<>();
      if (result.id()) {
        flags.add(ResultFlag.ID);
      }
      @SuppressWarnings("unchecked")
      Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>)
              ((result.typeHandler() == UnknownTypeHandler.class) ? null : result.typeHandler());
      boolean hasNestedResultMap = hasNestedResultMap(result);
      ResultMapping resultMapping = assistant.buildResultMapping(
          resultType,
          nullOrEmpty(result.property()),
          nullOrEmpty(result.column()),
          result.javaType() == void.class ? null : result.javaType(),
          result.jdbcType() == JdbcType.UNDEFINED ? null : result.jdbcType(),
          hasNestedSelect(result) ? nestedSelectId(result) : null,
          hasNestedResultMap ? nestedResultMapId(result) : null,
          null,
          hasNestedResultMap ? findColumnPrefix(result) : null,
          typeHandler,
          flags,
          null,
          null,
          isLazy(result));
      resultMappings.add(resultMapping);
    }
  }

  private String findColumnPrefix(Result result) {
    String columnPrefix = result.one().columnPrefix();
    if (columnPrefix.length() < 1) {
      columnPrefix = result.many().columnPrefix();
    }
    return columnPrefix;
  }

  private String nestedResultMapId(Result result) {
    String resultMapId = result.one().resultMap();
    if (resultMapId.length() < 1) {
      resultMapId = result.many().resultMap();
    }
    if (!resultMapId.contains(".")) {
      resultMapId = type.getName() + "." + resultMapId;
    }
    return resultMapId;
  }

  private boolean hasNestedResultMap(Result result) {
    if (result.one().resultMap().length() > 0 && result.many().resultMap().length() > 0) {
      throw new BuilderException("Cannot use both @One and @Many annotations in the same @Result");
    }
    return result.one().resultMap().length() > 0 || result.many().resultMap().length() > 0;
  }

  private String nestedSelectId(Result result) {
    String nestedSelect = result.one().select();
    if (nestedSelect.length() < 1) {
      nestedSelect = result.many().select();
    }
    if (!nestedSelect.contains(".")) {
      nestedSelect = type.getName() + "." + nestedSelect;
    }
    return nestedSelect;
  }

  private boolean isLazy(Result result) {
    boolean isLazy = configuration.isLazyLoadingEnabled();
    if (result.one().select().length() > 0 && FetchType.DEFAULT != result.one().fetchType()) {
      isLazy = result.one().fetchType() == FetchType.LAZY;
    } else if (result.many().select().length() > 0 && FetchType.DEFAULT != result.many().fetchType()) {
      isLazy = result.many().fetchType() == FetchType.LAZY;
    }
    return isLazy;
  }

  private boolean hasNestedSelect(Result result) {
    if (result.one().select().length() > 0 && result.many().select().length() > 0) {
      throw new BuilderException("Cannot use both @One and @Many annotations in the same @Result");
    }
    return result.one().select().length() > 0 || result.many().select().length() > 0;
  }

  private void applyConstructorArgs(Arg[] args, Class<?> resultType, List<ResultMapping> resultMappings) {
    for (Arg arg : args) {
      List<ResultFlag> flags = new ArrayList<>();
      flags.add(ResultFlag.CONSTRUCTOR);
      if (arg.id()) {
        flags.add(ResultFlag.ID);
      }
      @SuppressWarnings("unchecked")
      Class<? extends TypeHandler<?>> typeHandler = (Class<? extends TypeHandler<?>>)
              (arg.typeHandler() == UnknownTypeHandler.class ? null : arg.typeHandler());
      ResultMapping resultMapping = assistant.buildResultMapping(
          resultType,
          nullOrEmpty(arg.name()),
          nullOrEmpty(arg.column()),
          arg.javaType() == void.class ? null : arg.javaType(),
          arg.jdbcType() == JdbcType.UNDEFINED ? null : arg.jdbcType(),
          nullOrEmpty(arg.select()),
          nullOrEmpty(arg.resultMap()),
          null,
          nullOrEmpty(arg.columnPrefix()),
          typeHandler,
          flags,
          null,
          null,
          false);
      resultMappings.add(resultMapping);
    }
  }

  private String nullOrEmpty(String value) {
    return value == null || value.trim().length() == 0 ? null : value;
  }

  private KeyGenerator handleSelectKeyAnnotation(SelectKey selectKeyAnnotation, String baseStatementId, Class<?> parameterTypeClass, LanguageDriver languageDriver) {
    String id = baseStatementId + SelectKeyGenerator.SELECT_KEY_SUFFIX;
    Class<?> resultTypeClass = selectKeyAnnotation.resultType();
    StatementType statementType = selectKeyAnnotation.statementType();
    String keyProperty = selectKeyAnnotation.keyProperty();
    String keyColumn = selectKeyAnnotation.keyColumn();
    boolean executeBefore = selectKeyAnnotation.before();

    // defaults
    boolean useCache = false;
    KeyGenerator keyGenerator = NoKeyGenerator.INSTANCE;
    Integer fetchSize = null;
    Integer timeout = null;
    boolean flushCache = false;
    String parameterMap = null;
    String resultMap = null;
    ResultSetType resultSetTypeEnum = null;

    SqlSource sqlSource = buildSqlSourceFromStrings(selectKeyAnnotation.statement(), parameterTypeClass, languageDriver);
    SqlCommandType sqlCommandType = SqlCommandType.SELECT;

    assistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType, fetchSize, timeout, parameterMap, parameterTypeClass, resultMap, resultTypeClass, resultSetTypeEnum,
        flushCache, useCache, false,
        keyGenerator, keyProperty, keyColumn, null, languageDriver, null);

    id = assistant.applyCurrentNamespace(id, false);

    MappedStatement keyStatement = configuration.getMappedStatement(id, false);
    SelectKeyGenerator answer = new SelectKeyGenerator(keyStatement, executeBefore);
    configuration.addKeyGenerator(id, answer);
    return answer;
  }
>>>>>>> mybatis-3-trunk/master

}
