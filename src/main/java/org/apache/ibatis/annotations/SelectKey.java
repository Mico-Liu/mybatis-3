/**
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
 */
package org.apache.ibatis.annotations;

import org.apache.ibatis.mapping.StatementType;

import java.lang.annotation.*;

/**
 * 通过 SQL 语句获得主键的注解
 *
 * @author Clinton Begin
 * <p>
 * 例子：
 * @Insert({"INSERT INTO sys_user (id,user_name, user_password,user_email,user_info, head_img, create_time)VALUES(#{id},#{userName},#{userPassword},#{userEmail},#{userInfo},#{headImg, jdbcType=BLOB},#{createTime,jdbcType=TIMESTAMP})"})
 * @SelectKey(statement="SELECT LAST_INSERT_ID()",keyProperty="id",resultType=Long.class,before=false)
 * <p>
 * 注解方式和XML方式配置的属性基本相同，其中before为false时功能等同于order="AFTER"，before=true时功能等同于order="BEFORE"。
 * <p>
 * 注意：在不同的数据库中，order的配置不同
 * order属性的设置和使用的数据库有关。在MySQL数据库中，order属性设置的值是AFTER，因为当前记录的主键是在insert语句执行成功后才获取到的。
 * 而在Oracel数据库中，order属性的值要设置为BEFORE，这是因为Oracle数据库中需要先从序列获取值，然后将值作为主键插入到数据库中。
 * <p>
 * 我们在使用MyBatis注解方式时，可以不使用Mapper.xml配置文件，也可以和Mapper.xml配合使用。不管我们采用何种方式，我们都必须在MyBatis的配置文件中添加
 *
 * <mappers>
 * <package name="mybatis.simple.mapper"/>
 * </mappers>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)//方法级别
public @interface SelectKey {

  /**
   * @return 语句。 比如：SELECT LAST_INSERT_ID()
   */
  String[] statement();

  /**
   * @return Java 对象的属性。  比如：id
   */
  String keyProperty();

  /**
   * @return 数据库的字段
   */
  String keyColumn() default "";

  /**
   * @return 在插入语句执行前，还是执行后。 true=BEFORE即插入语句执行前执行，false=AFTER
   */
  boolean before();

  /**
   * @return 返回类型。 比如：Long.class
   */
  Class<?> resultType();

  StatementType statementType() default StatementType.PREPARED;
}
