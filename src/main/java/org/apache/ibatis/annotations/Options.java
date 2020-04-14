/**
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
package org.apache.ibatis.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ibatis.mapping.ResultSetType;
import org.apache.ibatis.mapping.StatementType;

/**
<<<<<<< HEAD
 * 操作可选项
=======
 * The annotation that specify options for customizing default behaviors.
 *
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * public interface UserMapper {
 *   &#064;Option(useGeneratedKeys = true, keyProperty = "id")
 *   &#064;Insert("INSERT INTO users (name) VALUES(#{name})")
 *   boolean insert(User user);
 * }
 * </pre>
>>>>>>> mybatis-3-trunk/master
 *
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Options {
  /**
   * The options for the {@link Options#flushCache()}. The default is {@link FlushCachePolicy#DEFAULT}
   */
  enum FlushCachePolicy {
    /** <code>false</code> for select statement; <code>true</code> for insert/update/delete statement. */
    DEFAULT,
    /** Flushes cache regardless of the statement type. */
    TRUE,
    /** Does not flush cache regardless of the statement type. */
    FALSE
  }

  /**
<<<<<<< HEAD
   * @return 是否使用缓存
=======
   * Returns whether use the 2nd cache feature if assigned the cache.
   *
   * @return {@code true} if use; {@code false} if otherwise
>>>>>>> mybatis-3-trunk/master
   */
  boolean useCache() default true;

  /**
<<<<<<< HEAD
   * @return 刷新缓存的策略
=======
   * Returns the 2nd cache flush strategy.
   *
   * @return the 2nd cache flush strategy
>>>>>>> mybatis-3-trunk/master
   */
  FlushCachePolicy flushCache() default FlushCachePolicy.DEFAULT;

  /**
<<<<<<< HEAD
   * @return 结果类型
=======
   * Returns the result set type.
   *
   * @return the result set type
>>>>>>> mybatis-3-trunk/master
   */
  ResultSetType resultSetType() default ResultSetType.DEFAULT;

  /**
<<<<<<< HEAD
   * @return 语句类型
=======
   * Return the statement type.
   *
   * @return the statement type
>>>>>>> mybatis-3-trunk/master
   */
  StatementType statementType() default StatementType.PREPARED;

  /**
<<<<<<< HEAD
   * @return 加载数量
=======
   * Returns the fetch size.
   *
   * @return the fetch size
>>>>>>> mybatis-3-trunk/master
   */
  int fetchSize() default -1;

  /**
<<<<<<< HEAD
   * @return 超时时间
=======
   * Returns the statement timeout.
   *
   * @return the statement timeout
>>>>>>> mybatis-3-trunk/master
   */
  int timeout() default -1;

  /**
<<<<<<< HEAD
   * @return 是否生成主键
=======
   * Returns whether use the generated keys feature supported by JDBC 3.0
   *
   * @return {@code true} if use; {@code false} if otherwise
>>>>>>> mybatis-3-trunk/master
   */
  boolean useGeneratedKeys() default false;

  /**
<<<<<<< HEAD
   * @return 主键在 Java 类中的属性
=======
   * Returns property names that holds a key value.
   * <p>
   * If you specify multiple property, please separate using comma(',').
   * </p>
   *
   * @return property names that separate with comma(',')
>>>>>>> mybatis-3-trunk/master
   */
  String keyProperty() default "";

  /**
<<<<<<< HEAD
   * @return 主键在数据库中的字段
=======
   * Returns column names that retrieves a key value.
   * <p>
   * If you specify multiple column, please separate using comma(',').
   * </p>
   *
   * @return column names that separate with comma(',')
>>>>>>> mybatis-3-trunk/master
   */
  String keyColumn() default "";

  /**
<<<<<<< HEAD
   * @return 结果集
=======
   * Returns result set names.
   * <p>
   * If you specify multiple result set, please separate using comma(',').
   * </p>
   *
   * @return result set names that separate with comma(',')
>>>>>>> mybatis-3-trunk/master
   */
  String resultSets() default "";
}
