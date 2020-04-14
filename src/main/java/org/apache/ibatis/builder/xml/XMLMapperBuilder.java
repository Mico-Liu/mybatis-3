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
package org.apache.ibatis.builder.xml;

import org.apache.ibatis.builder.*;
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
package org.apache.ibatis.builder.xml;

import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.builder.BaseBuilder;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.CacheRefResolver;
import org.apache.ibatis.builder.IncompleteElementException;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.ResultMapResolver;
>>>>>>> mybatis-3-trunk/master
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.io.InputStream;
import java.io.Reader;
import java.util.*;

/**
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
public class XMLMapperBuilder extends BaseBuilder {

    /**
     * 基于 Java XPath 解析器
     */
    private final XPathParser parser;
    /**
     * Mapper 构造器助手
     */
    private final MapperBuilderAssistant builderAssistant;
    /**
     * 可被其他语句引用的可重用语句块的集合
     * <p>
     * 例如：<sql id="userColumns"> ${alias}.id,${alias}.username,${alias}.password </sql>
     */
    private final Map<String, XNode> sqlFragments;
    /**
     * 资源引用的地址
     */
    private final String resource;

    @Deprecated
    public XMLMapperBuilder(Reader reader, Configuration configuration, String resource, Map<String, XNode> sqlFragments,
                            String namespace) {
        this(reader, configuration, resource, sqlFragments);
        this.builderAssistant.setCurrentNamespace(namespace);
    }

<<<<<<< HEAD
    @Deprecated
    public XMLMapperBuilder(Reader reader, Configuration configuration, String resource,
                            Map<String, XNode> sqlFragments) {
        this(new XPathParser(reader, true, configuration.getVariables(), new XMLMapperEntityResolver()), configuration,
                resource, sqlFragments);
=======
    parsePendingResultMaps();
    parsePendingCacheRefs();
    parsePendingStatements();
  }

  public XNode getSqlFragment(String refid) {
    return sqlFragments.get(refid);
  }

  private void configurationElement(XNode context) {
    try {
      String namespace = context.getStringAttribute("namespace");
      if (namespace == null || namespace.isEmpty()) {
        throw new BuilderException("Mapper's namespace cannot be empty");
      }
      builderAssistant.setCurrentNamespace(namespace);
      cacheRefElement(context.evalNode("cache-ref"));
      cacheElement(context.evalNode("cache"));
      parameterMapElement(context.evalNodes("/mapper/parameterMap"));
      resultMapElements(context.evalNodes("/mapper/resultMap"));
      sqlElement(context.evalNodes("/mapper/sql"));
      buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
    } catch (Exception e) {
      throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
>>>>>>> mybatis-3-trunk/master
    }

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource,
                            Map<String, XNode> sqlFragments, String namespace) {
        this(inputStream, configuration, resource, sqlFragments);
        this.builderAssistant.setCurrentNamespace(namespace);
    }

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource,
                            Map<String, XNode> sqlFragments) {
        this(new XPathParser(inputStream, true, configuration.getVariables(), new XMLMapperEntityResolver()), configuration,
                resource, sqlFragments);
    }

    private XMLMapperBuilder(XPathParser parser, Configuration configuration, String resource,
                             Map<String, XNode> sqlFragments) {
        super(configuration);
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
        this.parser = parser;
        this.sqlFragments = sqlFragments;
        this.resource = resource;
    }

    /**
     * 解析 Mapper XML 配置文件
     */
    public void parse() {
        // <1> 判断当前 Mapper 是否已经加载过
        if (!configuration.isResourceLoaded(resource)) {
            // <2> 解析 `<mapper />` 节点
            configurationElement(parser.evalNode("/mapper"));
            // <3> 标记该 Mapper 已经加载过
            configuration.addLoadedResource(resource);
            // <4> 绑定 Mapper
            bindMapperForNamespace();
        }

        // <5> 解析待定的 <resultMap /> 节点
        parsePendingResultMaps();
        // <6> 解析待定的 <cache-ref /> 节点
        parsePendingCacheRefs();
        // <7> 解析待定的 SQL 语句的节点
        parsePendingStatements();
    }

    public XNode getSqlFragment(String refid) {
        return sqlFragments.get(refid);
    }

    /**
     * 解析 <mapper /> 节点
     *
     * @param context
     */
    private void configurationElement(XNode context) {
        try {
            // <1> 获得 namespace 属性
            String namespace = context.getStringAttribute("namespace");
            if (namespace == null || namespace.equals("")) {
                throw new BuilderException("Mapper's namespace cannot be empty");
            }
            // <1> 设置 namespace 属性。获得 namespace 属性，并设置到 MapperAnnotationBuilder 中。
            builderAssistant.setCurrentNamespace(namespace);
            // <2> 解析 <cache-ref /> 节点
            cacheRefElement(context.evalNode("cache-ref"));
            // <3> 解析 <cache /> 节点
            cacheElement(context.evalNode("cache"));
            // 已废弃！老式风格的参数映射。内联参数是首选,这个元素可能在将来被移除，这里不会记录。
            parameterMapElement(context.evalNodes("/mapper/parameterMap"));
            // <4> 解析 <resultMap /> 节点们
            resultMapElements(context.evalNodes("/mapper/resultMap"));
            // <5> 解析 <sql /> 节点们
            sqlElement(context.evalNodes("/mapper/sql"));
            // <6> 解析 <select /> <insert /> <update /> <delete /> 节点们
            buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
        }
    }

    /**
     * 解析 <select />、<insert />、<update />、<delete /> 节点们。
     *
     * @param list
     */
    private void buildStatementFromContext(List<XNode> list) {
        if (configuration.getDatabaseId() != null) {
            buildStatementFromContext(list, configuration.getDatabaseId());
        }
        buildStatementFromContext(list, null);
    }

    /**
     * 解析 <select />、<insert />、<update />、<delete /> 节点们。
     *
     * @param list
     * @param requiredDatabaseId
     */
    private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
        // <1> 遍历 <select /> <insert /> <update /> <delete /> 节点们
        for (XNode context : list) {
            // <1> 创建 XMLStatementBuilder 对象，执行解析
            final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context,
                    requiredDatabaseId);
            try {
                statementParser.parseStatementNode();
            } catch (IncompleteElementException e) {
                // <2> 解析失败，添加到 configuration 中
                configuration.addIncompleteStatement(statementParser);
            }
        }
    }

    /**
     * 解析待定的 <resultMap /> 节点
     */
    private void parsePendingResultMaps() {
        // 获得 ResultMapResolver 集合，并遍历进行处理
        Collection<ResultMapResolver> incompleteResultMaps = configuration.getIncompleteResultMaps();
        synchronized (incompleteResultMaps) {
            Iterator<ResultMapResolver> iter = incompleteResultMaps.iterator();
            while (iter.hasNext()) {
                try {
                    // 执行解析
                    iter.next().resolve();
                    // 移除
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // ResultMap is still missing a resource...
                    // 解析失败，不抛出异常。不处理
                }
            }
        }
    }

    /**
     * 解析待定的 <cache-ref /> 节点
     */
    private void parsePendingCacheRefs() {
        // 获得 CacheRefResolver 集合，并遍历进行处理
        Collection<CacheRefResolver> incompleteCacheRefs = configuration.getIncompleteCacheRefs();
        synchronized (incompleteCacheRefs) {
            Iterator<CacheRefResolver> iter = incompleteCacheRefs.iterator();
            while (iter.hasNext()) {
                try {
                    // 执行解析
                    iter.next().resolveCacheRef();
                    // 移除
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // Cache ref is still missing a resource...
                }
            }
        }
    }

    /**
     * 解析待定的 SQL 语句的节点
     */
    private void parsePendingStatements() {
        // 获得 XMLStatementBuilder 集合，并遍历进行处理
        Collection<XMLStatementBuilder> incompleteStatements = configuration.getIncompleteStatements();
        synchronized (incompleteStatements) {
            Iterator<XMLStatementBuilder> iter = incompleteStatements.iterator();
            while (iter.hasNext()) {
                try {
                    // 执行解析
                    iter.next().parseStatementNode();
                    // 移除
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // Statement is still missing a resource...
                }
            }
        }
    }
<<<<<<< HEAD

    /**
     * 解析 <cache-ref /> 节点。
     * <p>
     * 例子如：<cache-ref namespace="com.someone.application.data.SomeMapper"/>
     *
     * @param context
     */
    private void cacheRefElement(XNode context) {
        if (context != null) {
            // <1> 获得指向的 namespace 名字，并添加到 configuration 的 cacheRefMap 中
            configuration.addCacheRef(builderAssistant.getCurrentNamespace(), context.getStringAttribute("namespace"));
            // <2> 创建 CacheRefResolver 对象
            CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant,
                    context.getStringAttribute("namespace"));
            try {
                // 执行解析
                cacheRefResolver.resolveCacheRef();
            } catch (IncompleteElementException e) {
                // <3> 解析失败，因为此处指向的 Cache 对象可能未初始化，则先调用 Configuration#addIncompleteCacheRef(CacheRefResolver incompleteCacheRef)
                // 方法，添加到 configuration 的 incompleteCacheRefs 中。
                configuration.addIncompleteCacheRef(cacheRefResolver);
            }
        }
    }


    /**
     * 解析 cache /> 标签
     *
     * @param context
     */
    private void cacheElement(XNode context) {
        /**
         *
         * 示例如下：
         *
         // 使用默认缓存
         <cache eviction="FIFO" flushInterval="60000"  size="512" readOnly="true"/>

         // 使用自定义缓存
         <cache type="com.domain.something.MyCustomCache">
         <property name="cacheFile" value="/tmp/my-custom-cache.tmp"/>
         </cache>
         *
         */
        if (context != null) {
            // <1> 获得负责存储的 Cache 实现类。默认使用PerpetualCache实现
            String type = context.getStringAttribute("type", "PERPETUAL");
            //获取cache的class
            Class<? extends Cache> typeClass = typeAliasRegistry.resolveAlias(type);
            // <2> 获得负责过期的 Cache 实现类。默认使用LRU
            String eviction = context.getStringAttribute("eviction", "LRU");
            //获取evictionClass的class
            Class<? extends Cache> evictionClass = typeAliasRegistry.resolveAlias(eviction);
            // <3> 获得 flushInterval、size、readWrite、blocking 属性
            // 刷新间隔时间
            Long flushInterval = context.getLongAttribute("flushInterval");
            //缓存个数
            Integer size = context.getIntAttribute("size");
            //只读属性，默认非只读
            boolean readWrite = !context.getBooleanAttribute("readOnly", false);
            //阻塞属性，默认非阻塞
            boolean blocking = context.getBooleanAttribute("blocking", false);
            // <4> 获得 Properties 属性
            Properties props = context.getChildrenAsProperties();
            // <5> 创建 Cache 对象
            builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
        }
    }

    private void parameterMapElement(List<XNode> list) {
        for (XNode parameterMapNode : list) {
            String id = parameterMapNode.getStringAttribute("id");
            String type = parameterMapNode.getStringAttribute("type");
            Class<?> parameterClass = resolveClass(type);
            List<XNode> parameterNodes = parameterMapNode.evalNodes("parameter");
            List<ParameterMapping> parameterMappings = new ArrayList<>();
            for (XNode parameterNode : parameterNodes) {
                String property = parameterNode.getStringAttribute("property");
                String javaType = parameterNode.getStringAttribute("javaType");
                String jdbcType = parameterNode.getStringAttribute("jdbcType");
                String resultMap = parameterNode.getStringAttribute("resultMap");
                String mode = parameterNode.getStringAttribute("mode");
                String typeHandler = parameterNode.getStringAttribute("typeHandler");
                Integer numericScale = parameterNode.getIntAttribute("numericScale");
                ParameterMode modeEnum = resolveParameterMode(mode);
                Class<?> javaTypeClass = resolveClass(javaType);
                JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
                Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
                ParameterMapping parameterMapping = builderAssistant.buildParameterMapping(parameterClass, property,
                        javaTypeClass, jdbcTypeEnum, resultMap, modeEnum, typeHandlerClass, numericScale);
                parameterMappings.add(parameterMapping);
            }
            builderAssistant.addParameterMap(id, parameterClass, parameterMappings);
=======
  }

  private void resultMapElements(List<XNode> list) {
    for (XNode resultMapNode : list) {
      try {
        resultMapElement(resultMapNode);
      } catch (IncompleteElementException e) {
        // ignore, it will be retried
      }
    }
  }

  private ResultMap resultMapElement(XNode resultMapNode) {
    return resultMapElement(resultMapNode, Collections.emptyList(), null);
  }

  private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings, Class<?> enclosingType) {
    ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
    String type = resultMapNode.getStringAttribute("type",
        resultMapNode.getStringAttribute("ofType",
            resultMapNode.getStringAttribute("resultType",
                resultMapNode.getStringAttribute("javaType"))));
    Class<?> typeClass = resolveClass(type);
    if (typeClass == null) {
      typeClass = inheritEnclosingType(resultMapNode, enclosingType);
    }
    Discriminator discriminator = null;
    List<ResultMapping> resultMappings = new ArrayList<>(additionalResultMappings);
    List<XNode> resultChildren = resultMapNode.getChildren();
    for (XNode resultChild : resultChildren) {
      if ("constructor".equals(resultChild.getName())) {
        processConstructorElement(resultChild, typeClass, resultMappings);
      } else if ("discriminator".equals(resultChild.getName())) {
        discriminator = processDiscriminatorElement(resultChild, typeClass, resultMappings);
      } else {
        List<ResultFlag> flags = new ArrayList<>();
        if ("id".equals(resultChild.getName())) {
          flags.add(ResultFlag.ID);
>>>>>>> mybatis-3-trunk/master
        }
    }
<<<<<<< HEAD

    /**
     * 解析 <resultMap /> 节点们
     *
     * @param list
     * @throws Exception
     */
    private void resultMapElements(List<XNode> list) throws Exception {
        // 遍历 <resultMap /> 节点们
        for (XNode resultMapNode : list) {
            try {
                // 处理单个 <resultMap /> 节点
                resultMapElement(resultMapNode);
            } catch (IncompleteElementException e) {
                // ignore, it will be retried
            }
        }
=======
    String id = resultMapNode.getStringAttribute("id",
            resultMapNode.getValueBasedIdentifier());
    String extend = resultMapNode.getStringAttribute("extends");
    Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
    ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend, discriminator, resultMappings, autoMapping);
    try {
      return resultMapResolver.resolve();
    } catch (IncompleteElementException e) {
      configuration.addIncompleteResultMap(resultMapResolver);
      throw e;
>>>>>>> mybatis-3-trunk/master
    }

    /**
     * 处理单个resultMap节点
     *
     * @param resultMapNode
     * @return
     * @throws Exception
     */
    private ResultMap resultMapElement(XNode resultMapNode) throws Exception {
        return resultMapElement(resultMapNode, Collections.emptyList(), null);
    }
<<<<<<< HEAD

    /**
     * 处理单个resultMap节点
     *
     * @param resultMapNode
     * @param additionalResultMappings
     * @param enclosingType
     * @return
     * @throws Exception
     */
    private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings,
                                       Class<?> enclosingType) throws Exception {

        /**
         *
         *
         <resultMap type="ClassEntity" id="classResultMap">
         <id property="classID" column="CLASS_ID" />
         <result property="className" column="CLASS_NAME" />
         <result property="classYear" column="CLASS_YEAR" />
         <association property="teacherEntity" column="TEACHER_ID"  resultMap="teacherResultMap"/>
         <collection property="studentList" column="CLASS_ID" javaType="ArrayList" ofType="StudentEntity" resultMap="studentResultMap"/>
         </resultMap>
         *
         */

        ErrorContext.instance().activity("processing " + resultMapNode.getValueBasedIdentifier());
        // <1> 获得 type 属性
        // 优先获取type属性，不存在时再获取ofType属性，不存在再获取resultType，不存在再获取javaType。
        String type = resultMapNode.getStringAttribute("type", resultMapNode.getStringAttribute("ofType",
                resultMapNode.getStringAttribute("resultType", resultMapNode.getStringAttribute("javaType"))));
        // <1> 解析 type 对应的类
        Class<?> typeClass = resolveClass(type);
        if (typeClass == null) {
            typeClass = inheritEnclosingType(resultMapNode, enclosingType);
        }
        Discriminator discriminator = null;
        // <2> 创建 ResultMapping 集合
        List<ResultMapping> resultMappings = new ArrayList<>();
        resultMappings.addAll(additionalResultMappings);
        // <2> 遍历 <resultMap /> 的子节点
        List<XNode> resultChildren = resultMapNode.getChildren();
        for (XNode resultChild : resultChildren) {
            // <2.1> 处理 <constructor /> 节点
            if ("constructor".equals(resultChild.getName())) {
                processConstructorElement(resultChild, typeClass, resultMappings);
            }
            // <2.2> 处理 <discriminator /> 节点
            else if ("discriminator".equals(resultChild.getName())) {
                discriminator = processDiscriminatorElement(resultChild, typeClass, resultMappings);
            }
            // <2.3> 处理其它节点
            else {
                List<ResultFlag> flags = new ArrayList<>();
                if ("id".equals(resultChild.getName())) {
                    flags.add(ResultFlag.ID);
                }
                //将当前子节点构建成 ResultMapping 对象，并添加到 resultMappings 中
                resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
            }
        }
        // <1> 获得 id 属性
        String id = resultMapNode.getStringAttribute("id", resultMapNode.getValueBasedIdentifier());
        // <1> 获得 extends 属性
        String extend = resultMapNode.getStringAttribute("extends");
        // <1> 获得 autoMapping 属性
        Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
        // <3> 创建 ResultMapResolver 对象，执行解析
        ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend, discriminator,
                resultMappings, autoMapping);
        try {
            return resultMapResolver.resolve();
        } catch (IncompleteElementException e) {
            // <4> 解析失败，添加到 configuration 中. 为什么要这样呢？ 是因为解析是按顺序执行，
            // 如果解析的对象在后面，就会跳过解析，等所有依赖的都解析完才可以解析
            configuration.addIncompleteResultMap(resultMapResolver);
            throw e;
        }
    }

    protected Class<?> inheritEnclosingType(XNode resultMapNode, Class<?> enclosingType) {
        if ("association".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
            String property = resultMapNode.getStringAttribute("property");
            if (property != null && enclosingType != null) {
                MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
                return metaResultType.getSetterType(property);
            }
        } else if ("case".equals(resultMapNode.getName()) && resultMapNode.getStringAttribute("resultMap") == null) {
            return enclosingType;
        }
        return null;
=======
    return null;
  }

  private void processConstructorElement(XNode resultChild, Class<?> resultType, List<ResultMapping> resultMappings) {
    List<XNode> argChildren = resultChild.getChildren();
    for (XNode argChild : argChildren) {
      List<ResultFlag> flags = new ArrayList<>();
      flags.add(ResultFlag.CONSTRUCTOR);
      if ("idArg".equals(argChild.getName())) {
        flags.add(ResultFlag.ID);
      }
      resultMappings.add(buildResultMappingFromContext(argChild, resultType, flags));
    }
  }

  private Discriminator processDiscriminatorElement(XNode context, Class<?> resultType, List<ResultMapping> resultMappings) {
    String column = context.getStringAttribute("column");
    String javaType = context.getStringAttribute("javaType");
    String jdbcType = context.getStringAttribute("jdbcType");
    String typeHandler = context.getStringAttribute("typeHandler");
    Class<?> javaTypeClass = resolveClass(javaType);
    Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
    JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
    Map<String, String> discriminatorMap = new HashMap<>();
    for (XNode caseChild : context.getChildren()) {
      String value = caseChild.getStringAttribute("value");
      String resultMap = caseChild.getStringAttribute("resultMap", processNestedResultMappings(caseChild, resultMappings, resultType));
      discriminatorMap.put(value, resultMap);
>>>>>>> mybatis-3-trunk/master
    }

    /**
     * 处理 <constructor /> 节点
     *
     * @param resultChild    constructor节点
     * @param resultType     resultMap对应的实体类型
     * @param resultMappings resultMap解析每个节点后存储的Map
     * @throws Exception
     */
    private void processConstructorElement(XNode resultChild, Class<?> resultType, List<ResultMapping> resultMappings)
            throws Exception {
        // <1> 遍历 <constructor /> 的子节点们
        /**
         <constructor>
         <idArg column="userId" javaType="int"/>
         <arg column="userName" javaType="String"/>
         </constructor>
         */
        //获取构造方法的所有参数
        List<XNode> argChildren = resultChild.getChildren();
        //遍历每个参数
        for (XNode argChild : argChildren) {
            // <2> 获得 ResultFlag 集合
            List<ResultFlag> flags = new ArrayList<>();
            //默认是构造方法的普通参数
            flags.add(ResultFlag.CONSTRUCTOR);
            //标识一下当前参数是主键
            if ("idArg".equals(argChild.getName())) {
                flags.add(ResultFlag.ID);
            }
            // <3> 将当前子节点构建成 ResultMapping 对象，并添加到 resultMappings 中
            resultMappings.add(buildResultMappingFromContext(argChild, resultType, flags));
        }
    }

    /**
     * 处理 <discriminator /> 节点。
     *
     * @param context
     * @param resultType
     * @param resultMappings
     * @return
     * @throws Exception
     */
    private Discriminator processDiscriminatorElement(XNode context, Class<?> resultType,
                                                      List<ResultMapping> resultMappings) throws Exception {
        /**
         *

         <discriminator column="during" javaType="_int">
         // 形式1：通过resultType设置动态映射信息
         <case value="4" resultType="EStudent">
         <result column="juniorHighSchool" property="seniorHighSchool"/>
         </case>

         // 形式2: 通过resultMap设置动态映射信息
         <case value="5" resultMap="dynamicRM"/>
         <case value="6" resultMap="dynamicRM"/>
         </discriminator>
         *
         */


        String column = context.getStringAttribute("column");
        String javaType = context.getStringAttribute("javaType");
        String jdbcType = context.getStringAttribute("jdbcType");
        String typeHandler = context.getStringAttribute("typeHandler");
        // <1> 解析各种属性对应的类
        Class<?> javaTypeClass = resolveClass(javaType);
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        // <2> 遍历 <discriminator /> 的子节点，解析成 discriminatorMap 集合
        // case标签
        Map<String, String> discriminatorMap = new HashMap<>();
        for (XNode caseChild : context.getChildren()) {
            String value = caseChild.getStringAttribute("value");
            String resultMap = caseChild.getStringAttribute("resultMap",
                    //处理内嵌的ResultMap
                    processNestedResultMappings(caseChild, resultMappings, resultType));
            discriminatorMap.put(value, resultMap);
        }
        return builderAssistant.buildDiscriminator(resultType, column, javaTypeClass, jdbcTypeEnum, typeHandlerClass,
                discriminatorMap);
    }

    /**
     * 解析SQL片段
     *
     * @param list 所有SQL片段节点列表
     */
    private void sqlElement(List<XNode> list) {
        if (configuration.getDatabaseId() != null) {
            sqlElement(list, configuration.getDatabaseId());
        }
        sqlElement(list, null);
    }

    /**
     * 解析SQL片段
     *
     * @param list
     * @param requiredDatabaseId
     */
    private void sqlElement(List<XNode> list, String requiredDatabaseId) {
        /**
         *
         <mapper namespace="UserDaoMapper">
         <resultMap id="userMap" type="User" autoMapping="true">
         <id column="id" property="id" />
         <result column="user_name" property="userName" />
         </resultMap>
         <sql id="commonSql">
         id,user_name,password,name,age,sex,birthday,created,updated
         </sql>
         <select id="findAllUsers" resultType="User" resultMap="userMap">
         select <include refid="commonSql"></include>  from tb_user
         </select>

         </mapper>
         */
        // <1> 遍历所有 <sql /> 节点，逐个处理。
        for (XNode context : list) {
            // <2> 获得 databaseId 属性
            String databaseId = context.getStringAttribute("databaseId");
            // <3> 获得完整的 id 属性，格式为 `${namespace}.${id}` 。
            String id = context.getStringAttribute("id");
            id = builderAssistant.applyCurrentNamespace(id, false);
            // <4> 判断 databaseId 是否匹配
            if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) {
                // <5> 添加到 sqlFragments 中
                sqlFragments.put(id, context);
            }
        }
    }

    /**
     * 判断 databaseId 是否匹配。
     *
     * @param id                 SQL片段的ID值
     * @param databaseId         数据库提供商ID
     * @param requiredDatabaseId SQL片段配置的数据库提供商ID
     * @return
     */
    private boolean databaseIdMatchesCurrent(String id, String databaseId, String requiredDatabaseId) {
        // 如果不匹配，则返回 false
        if (requiredDatabaseId != null) {
            return requiredDatabaseId.equals(databaseId);
        }
        // 如果未设置 requiredDatabaseId ，但是 databaseId 存在，说明还是不匹配，则返回 false
        if (databaseId != null) {
            return false;
        }
        // 判断是否已经存在，不存在，返回true
        if (!this.sqlFragments.containsKey(id)) {
            return true;
        }
        // skip this fragment if there is a previous one with a not null databaseId
        XNode context = this.sqlFragments.get(id);
        // 若存在，则判断原有的 sqlFragment 是否 databaseId 为空。因为，当前 databaseId 为空，这样两者才能匹配。
        return context.getStringAttribute("databaseId") == null;
    }
<<<<<<< HEAD

    /**
     * 将当前节点构建成 ResultMapping 对象
     *
     * @param context
     * @param resultType
     * @param flags
     * @return
     * @throws Exception
     */
    private ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags)
            throws Exception {
        // <1> 获得各种属性
        String property;
        if (flags.contains(ResultFlag.CONSTRUCTOR)) {
            property = context.getStringAttribute("name");
        } else {
            property = context.getStringAttribute("property");
        }
        String column = context.getStringAttribute("column");
        String javaType = context.getStringAttribute("javaType");
        String jdbcType = context.getStringAttribute("jdbcType");
        String nestedSelect = context.getStringAttribute("select");
        String nestedResultMap = context.getStringAttribute("resultMap",
                processNestedResultMappings(context, Collections.emptyList(), resultType));
        String notNullColumn = context.getStringAttribute("notNullColumn");
        String columnPrefix = context.getStringAttribute("columnPrefix");
        String typeHandler = context.getStringAttribute("typeHandler");
        String resultSet = context.getStringAttribute("resultSet");
        String foreignColumn = context.getStringAttribute("foreignColumn");
        boolean lazy = "lazy"
                .equals(context.getStringAttribute("fetchType", configuration.isLazyLoadingEnabled() ? "lazy" : "eager"));
        // <1> 获得各种属性对应的类
        Class<?> javaTypeClass = resolveClass(javaType);
        Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
        JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
        // <2> 构建 ResultMapping 对象
        return builderAssistant.buildResultMapping(resultType, property, column, javaTypeClass, jdbcTypeEnum, nestedSelect,
                nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags, resultSet, foreignColumn, lazy);
    }

    /**
     * 处理标签为association、collection、case
     *
     * @param context
     * @param resultMappings
     * @param enclosingType
     * @return
     * @throws Exception
     */
    private String processNestedResultMappings(XNode context, List<ResultMapping> resultMappings, Class<?> enclosingType)
            throws Exception {
        /**
         *
         <case value="4" resultType="EStudent">
         <result column="juniorHighSchool" property="seniorHighSchool"/>
         </case>
         *
         */

        if ("association".equals(context.getName()) || "collection".equals(context.getName())
                || "case".equals(context.getName())) {
            if (context.getStringAttribute("select") == null) {
                validateCollection(context, enclosingType);
                ResultMap resultMap = resultMapElement(context, resultMappings, enclosingType);
                return resultMap.getId();
            }
        }
        return null;
    }

    protected void validateCollection(XNode context, Class<?> enclosingType) {
        if ("collection".equals(context.getName()) && context.getStringAttribute("resultMap") == null
                && context.getStringAttribute("javaType") == null) {
            MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
            String property = context.getStringAttribute("property");
            if (!metaResultType.hasSetter(property)) {
                throw new BuilderException(
                        "Ambiguous collection type for property '" + property + "'. You must specify 'javaType' or 'resultMap'.");
            }
        }
    }

    /**
     * 绑定 Mapper
     */
    private void bindMapperForNamespace() {
        String namespace = builderAssistant.getCurrentNamespace();
        if (namespace != null) {
            // <1> 获得 Mapper 映射配置文件对应的 Mapper 接口，实际上类名就是 namespace 。嘿嘿，这个是常识。
            Class<?> boundType = null;
            try {
                boundType = Resources.classForName(namespace);
            } catch (ClassNotFoundException e) {
                // ignore, bound type is not required
            }
            if (boundType != null) {
                // <2> 不存在该 Mapper 接口，则进行添加
                if (!configuration.hasMapper(boundType)) {
                    // Spring may not know the real resource name so we set a flag
                    // to prevent loading again this resource from the mapper interface
                    // look at MapperAnnotationBuilder#loadXmlResource
                    // <3> 标记 namespace 已经添加，避免 MapperAnnotationBuilder#loadXmlResource(...) 重复加载
                    configuration.addLoadedResource("namespace:" + namespace);
                    // <4> 添加到 configuration 中
                    configuration.addMapper(boundType);
                }
            }
        }
=======
    // skip this fragment if there is a previous one with a not null databaseId
    XNode context = this.sqlFragments.get(id);
    return context.getStringAttribute("databaseId") == null;
  }

  private ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags) {
    String property;
    if (flags.contains(ResultFlag.CONSTRUCTOR)) {
      property = context.getStringAttribute("name");
    } else {
      property = context.getStringAttribute("property");
    }
    String column = context.getStringAttribute("column");
    String javaType = context.getStringAttribute("javaType");
    String jdbcType = context.getStringAttribute("jdbcType");
    String nestedSelect = context.getStringAttribute("select");
    String nestedResultMap = context.getStringAttribute("resultMap", () ->
        processNestedResultMappings(context, Collections.emptyList(), resultType));
    String notNullColumn = context.getStringAttribute("notNullColumn");
    String columnPrefix = context.getStringAttribute("columnPrefix");
    String typeHandler = context.getStringAttribute("typeHandler");
    String resultSet = context.getStringAttribute("resultSet");
    String foreignColumn = context.getStringAttribute("foreignColumn");
    boolean lazy = "lazy".equals(context.getStringAttribute("fetchType", configuration.isLazyLoadingEnabled() ? "lazy" : "eager"));
    Class<?> javaTypeClass = resolveClass(javaType);
    Class<? extends TypeHandler<?>> typeHandlerClass = resolveClass(typeHandler);
    JdbcType jdbcTypeEnum = resolveJdbcType(jdbcType);
    return builderAssistant.buildResultMapping(resultType, property, column, javaTypeClass, jdbcTypeEnum, nestedSelect, nestedResultMap, notNullColumn, columnPrefix, typeHandlerClass, flags, resultSet, foreignColumn, lazy);
  }

  private String processNestedResultMappings(XNode context, List<ResultMapping> resultMappings, Class<?> enclosingType) {
    if (Arrays.asList("association", "collection", "case").contains(context.getName())
        && context.getStringAttribute("select") == null) {
      validateCollection(context, enclosingType);
      ResultMap resultMap = resultMapElement(context, resultMappings, enclosingType);
      return resultMap.getId();
    }
    return null;
  }

  protected void validateCollection(XNode context, Class<?> enclosingType) {
    if ("collection".equals(context.getName()) && context.getStringAttribute("resultMap") == null
        && context.getStringAttribute("javaType") == null) {
      MetaClass metaResultType = MetaClass.forClass(enclosingType, configuration.getReflectorFactory());
      String property = context.getStringAttribute("property");
      if (!metaResultType.hasSetter(property)) {
        throw new BuilderException(
            "Ambiguous collection type for property '" + property + "'. You must specify 'javaType' or 'resultMap'.");
      }
    }
  }

  private void bindMapperForNamespace() {
    String namespace = builderAssistant.getCurrentNamespace();
    if (namespace != null) {
      Class<?> boundType = null;
      try {
        boundType = Resources.classForName(namespace);
      } catch (ClassNotFoundException e) {
        // ignore, bound type is not required
      }
      if (boundType != null && !configuration.hasMapper(boundType)) {
        // Spring may not know the real resource name so we set a flag
        // to prevent loading again this resource from the mapper interface
        // look at MapperAnnotationBuilder#loadXmlResource
        configuration.addLoadedResource("namespace:" + namespace);
        configuration.addMapper(boundType);
      }
>>>>>>> mybatis-3-trunk/master
    }

}
