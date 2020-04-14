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
package org.apache.ibatis.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis 为简化配置文件提供了别名机制，该机制是类型转换模块的主要功能之一
 * <p>
 * 类型转换模块的另一个功能是实现 JDBC 类型与 Java 类型之间的转换，该功能在为 SQL 语句绑定实参以及映射查询结果集时都会涉及：
 * <p>
 * 在为 SQL 语句绑定实参时，会将数据由 Java 类型转换成 JDBC 类型。
 * 而在映射结果集时，会将数据由 JDBC 类型转换成 Java 类型。
 * <p>
 * <p>
 * 一共有两类方法，分别是：
 * #setParameter(...) 方法，是 Java Type => JDBC Type 的过程。
 * #getResult(...) 方法，是 JDBC Type => Java Type 的过程。
 *
 * @author Clinton Begin
 */
public interface TypeHandler<T> {

  /**
   * 设置 PreparedStatement 的指定参数
   * <p>
   * Java Type => JDBC Type
   *
   * @param ps        PreparedStatement 对象
   * @param i         参数占位符的位置
   * @param parameter 参数
   * @param jdbcType  JDBC 类型
   * @throws SQLException 当发生 SQL 异常时
   */
  void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

  /**
<<<<<<< HEAD
   * 获得 ResultSet 的指定字段的值
   * <p>
   * JDBC Type => Java Type
   *
   * @param rs         ResultSet 对象
   * @param columnName 字段名
   * @return 值
   * @throws SQLException 当发生 SQL 异常时
=======
   * Gets the result.
   *
   * @param rs
   *          the rs
   * @param columnName
   *          Colunm name, when configuration <code>useColumnLabel</code> is <code>false</code>
   * @return the result
   * @throws SQLException
   *           the SQL exception
>>>>>>> mybatis-3-trunk/master
   */
  T getResult(ResultSet rs, String columnName) throws SQLException;

  /**
   * 获得 ResultSet 的指定字段的值
   * <p>
   * JDBC Type => Java Type
   *
   * @param rs          ResultSet 对象
   * @param columnIndex 字段位置
   * @return 值
   * @throws SQLException 当发生 SQL 异常时
   */
  T getResult(ResultSet rs, int columnIndex) throws SQLException;

  /**
   * 获得 CallableStatement 的指定字段的值
   * <p>
   * JDBC Type => Java Type
   *
   * @param cs          CallableStatement 对象，支持调用存储过程
   * @param columnIndex 字段位置
   * @return 值
   * @throws SQLException
   */
  T getResult(CallableStatement cs, int columnIndex) throws SQLException;

}
