/**
<<<<<<< HEAD
 * Copyright 2009-2016 the original author or authors.
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
=======
 *    Copyright 2009-2019 the original author or authors.
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
>>>>>>> mybatis-3-trunk/master
 */
package org.apache.ibatis.annotations;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
<<<<<<< HEAD
 * 结果字段的注解
 *
=======
 * The annotation that specify a mapping definition for the property.
 *
 * @see Results
>>>>>>> mybatis-3-trunk/master
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Results.class)
public @interface Result {
  /**
<<<<<<< HEAD
   * @return 是否是 ID 字段
=======
   * Returns whether id column or not.
   *
   * @return {@code true} if id column; {@code false} if otherwise
>>>>>>> mybatis-3-trunk/master
   */
  boolean id() default false;

  /**
<<<<<<< HEAD
   * @return 数据库的字段
=======
   * Return the column name(or column label) to map to this argument.
   *
   * @return the column name(or column label)
>>>>>>> mybatis-3-trunk/master
   */
  String column() default "";

  /**
<<<<<<< HEAD
   * @return Java 类中的属性
=======
   * Returns the property name for applying this mapping.
   *
   * @return the property name
>>>>>>> mybatis-3-trunk/master
   */
  String property() default "";

  /**
<<<<<<< HEAD
   * java类型
   *
   * @return
=======
   * Return the java type for this argument.
   *
   * @return the java type
>>>>>>> mybatis-3-trunk/master
   */
  Class<?> javaType() default void.class;

  /**
<<<<<<< HEAD
   * jdbc类型
   *
   * @return
=======
   * Return the jdbc type for column that map to this argument.
   *
   * @return the jdbc type
>>>>>>> mybatis-3-trunk/master
   */
  JdbcType jdbcType() default JdbcType.UNDEFINED;

  /**
<<<<<<< HEAD
   * 使用的typeHandler处理器
   *
   * @return
=======
   * Returns the {@link TypeHandler} type for retrieving a column value from result set.
   *
   * @return the {@link TypeHandler} type
>>>>>>> mybatis-3-trunk/master
   */
  Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

  /**
<<<<<<< HEAD
   * @return {@link One} 注解
=======
   * Returns the mapping definition for single relationship.
   *
   * @return the mapping definition for single relationship
>>>>>>> mybatis-3-trunk/master
   */
  One one() default @One;

  /**
<<<<<<< HEAD
   * @return {@link Many} 注解
=======
   * Returns the mapping definition for collection relationship.
   *
   * @return the mapping definition for collection relationship
>>>>>>> mybatis-3-trunk/master
   */
  Many many() default @Many;
}
