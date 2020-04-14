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
>>>>>>> mybatis-3-trunk/master
 */
package org.apache.ibatis.annotations;

import java.lang.annotation.*;

/**
<<<<<<< HEAD
 * 查询语句提供器
 * XXXProvider 的用途是，指定一个类( type )的指定方法( method )，返回使用的 SQL 。并且，该方法可以使用 Map<String,Object> params 来作为方法参数，传递参数
=======
 * The annotation that specify a method that provide an SQL for retrieving record(s).
 *
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * public interface UserMapper {
 *
 *   &#064;SelectProvider(type = SqlProvider.class, method = "selectById")
 *   User selectById(int id);
 *
 *   public static class SqlProvider {
 *     public static String selectById() {
 *       return "SELECT id, name FROM users WHERE id = #{id}";
 *     }
 *   }
 *
 * }
 * </pre>
>>>>>>> mybatis-3-trunk/master
 *
 * @author Clinton Begin
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) // 方法
public @interface SelectProvider {

  /**
   * 指明语句所在的类。等同type
   * <p>
   * Specify a type that implements an SQL provider method.
   *
   * @return a type that implements an SQL provider method
   * @see #type()
   * @since 3.5.2
   */
  Class<?> value() default void.class;

  /**
   * 指明语句所在的类。等同value
   * <p>
   * Specify a type that implements an SQL provider method.
   * <p>
   * This attribute is alias of {@link #value()}.
   * </p>
   *
   * @return a type that implements an SQL provider method
   * @see #value()
   */
  Class<?> type() default void.class;

  /**
   * 指明语句所在的方法
   * <p>
   * Specify a method for providing an SQL.
   *
   * <p>
   * Since 3.5.1, this attribute can omit. If this attribute omit, the MyBatis will call a method that decide by
   * following rules.
   * <ul>
<<<<<<< HEAD
   * <li>If class that specified the {@link #type()} attribute implements the
   * {@link org.apache.ibatis.builder.annotation.ProviderMethodResolver}, the MyBatis use a method that returned by it
   * </li>
   * <li>If cannot resolve a method by {@link org.apache.ibatis.builder.annotation.ProviderMethodResolver}(= not
   * implement it or it was returned {@code null}), the MyBatis will search and use a fallback method that named
   * {@code provideSql} from specified type</li>
=======
   *   <li>
   *     If class that specified the {@link #type()} attribute implements the
   *     {@link org.apache.ibatis.builder.annotation.ProviderMethodResolver},
   *     the MyBatis use a method that returned by it
   *   </li>
   *   <li>
   *     If cannot resolve a method by {@link org.apache.ibatis.builder.annotation.ProviderMethodResolver}(= not implement it or it was returned {@code null}),
   *     the MyBatis will search and use a fallback method that named {@code provideSql} from specified type
   *   </li>
>>>>>>> mybatis-3-trunk/master
   * </ul>
   *
   * @return a method name of method for providing an SQL
   */
  String method() default "";

}
