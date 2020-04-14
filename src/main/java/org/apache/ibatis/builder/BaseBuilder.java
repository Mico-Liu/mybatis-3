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
package org.apache.ibatis.builder;

import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 基础构造器抽象类，为子类提供通用的工具类
 *
 * @author Clinton Begin
 */
public abstract class BaseBuilder {
  /**
   * MyBatis Configuration 对象
   */
  protected final Configuration configuration;
  /**
   * 类型别名注册器
   */
  protected final TypeAliasRegistry typeAliasRegistry;
  /**
   * 类型处理器注册器
   */
  protected final TypeHandlerRegistry typeHandlerRegistry;

  public BaseBuilder(Configuration configuration) {
    this.configuration = configuration;
    this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
    this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * 创建正则表达式
   *
   * @param regex        指定表达式
   * @param defaultValue 默认表达式
   * @return 正则表达式对象
   */
  protected Pattern parseExpression(String regex, String defaultValue) {
    return Pattern.compile(regex == null ? defaultValue : regex);
  }

  /**
   * 字符串转成Boollean
   *
   * @param value
   * @param defaultValue
   * @return
   */
  protected Boolean booleanValueOf(String value, Boolean defaultValue) {
    return value == null ? defaultValue : Boolean.valueOf(value);
  }

  /**
   * 字符串转成Integer
   *
   * @param value
   * @param defaultValue
   * @return
   */
  protected Integer integerValueOf(String value, Integer defaultValue) {
    return value == null ? defaultValue : Integer.valueOf(value);
  }

  /**
   * 字符串转成Set集合，字符串是以逗号隔开
   *
   * @param value
   * @param defaultValue
   * @return
   */
  protected Set<String> stringSetValueOf(String value, String defaultValue) {
    value = value == null ? defaultValue : value;
    return new HashSet<>(Arrays.asList(value.split(",")));
  }

  /**
   * 解释JdbcType，通过枚举名称解释
   *
   * @param alias 枚举名称
   * @return
   */
  protected JdbcType resolveJdbcType(String alias) {
    if (alias == null) {
      return null;
    }
    try {
      return JdbcType.valueOf(alias);
    } catch (IllegalArgumentException e) {
      throw new BuilderException("Error resolving JdbcType. Cause: " + e, e);
    }
  }

  /**
   * 解析ResultSetType，通过枚举名称解析
   *
   * @param alias 枚举名称
   * @return
   */
  protected ResultSetType resolveResultSetType(String alias) {
    if (alias == null) {
      return null;
    }
    try {
      return ResultSetType.valueOf(alias);
    } catch (IllegalArgumentException e) {
      throw new BuilderException("Error resolving ResultSetType. Cause: " + e, e);
    }
  }

  /**
   * 解析参数类型ParameterMode。通过枚举名称解析
   *
   * @param alias 枚举明名称
   * @return
   */
  protected ParameterMode resolveParameterMode(String alias) {
    if (alias == null) {
      return null;
    }
    try {
      return ParameterMode.valueOf(alias);
    } catch (IllegalArgumentException e) {
      throw new BuilderException("Error resolving ParameterMode. Cause: " + e, e);
    }
  }

  /**
   * 创建对象
   *
   * @param alias
   * @return
   */
  protected Object createInstance(String alias) {
    //查找别名对应的class类
    Class<?> clazz = resolveClass(alias);
    if (clazz == null) {
      return null;
    }
    try {
<<<<<<< HEAD
      // <2> 创建对象
      // 这里重复获得了一次
      return resolveClass(alias).getDeclaredConstructor().newInstance();
=======
      return clazz.getDeclaredConstructor().newInstance();
>>>>>>> mybatis-3-trunk/master
    } catch (Exception e) {
      throw new BuilderException("Error creating instance. Cause: " + e, e);
    }
  }

  /**
   * 从别名注册器中获取已注册的class类
   *
   * @param alias 别名
   * @param <T>
   * @return
   */
  protected <T> Class<? extends T> resolveClass(String alias) {
    if (alias == null) {
      return null;
    }
    try {
      return resolveAlias(alias);
    } catch (Exception e) {
      throw new BuilderException("Error resolving class. Cause: " + e, e);
    }
  }

  /**
   * 从 typeHandlerRegistry 中获得或创建对应的 TypeHandler 对象
   *
   * @param javaType
   * @param typeHandlerAlias
   * @return
   */
  protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, String typeHandlerAlias) {
    if (typeHandlerAlias == null) {
      return null;
    }
    Class<?> type = resolveClass(typeHandlerAlias);
    //如果不是TypeHandler的子类，就抛出异常
    if (type != null && !TypeHandler.class.isAssignableFrom(type)) {
      throw new BuilderException(
        "Type " + type.getName() + " is not a valid TypeHandler because it does not implement TypeHandler interface");
    }
    @SuppressWarnings("unchecked") // already verified it is a TypeHandler
      Class<? extends TypeHandler<?>> typeHandlerType = (Class<? extends TypeHandler<?>>) type;
    return resolveTypeHandler(javaType, typeHandlerType);
  }

  /**
   * 从 typeHandlerRegistry 中获得或创建对应的 TypeHandler 对象
   * <p>
   * 没有就创建一个新的
   *
   * @param javaType
   * @param typeHandlerType
   * @return
   */
  protected TypeHandler<?> resolveTypeHandler(Class<?> javaType, Class<? extends TypeHandler<?>> typeHandlerType) {
    if (typeHandlerType == null) {
      return null;
    }
    // javaType ignored for injected handlers see issue #746 for full detail
    //从TypeHandler注册容器中获取typeHandlerType指定的处理器
    TypeHandler<?> handler = typeHandlerRegistry.getMappingTypeHandler(typeHandlerType);
    if (handler == null) {
      // not in registry, create a new one
      // 如果不存在，进行创建 TypeHandler 对象
      handler = typeHandlerRegistry.getInstance(javaType, typeHandlerType);
    }
    return handler;
  }

  /**
   * 从别名注册器中通过别名名称查找class
   * <p>
   * 通过别名或类全名，获得对应的类
   *
   * @param alias 别名名称
   * @param <T>
   * @return 返回别名类
   */
  protected <T> Class<? extends T> resolveAlias(String alias) {
    return typeAliasRegistry.resolveAlias(alias);
  }
}
