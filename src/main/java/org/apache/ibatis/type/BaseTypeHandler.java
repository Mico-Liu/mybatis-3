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

import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.session.Configuration;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 主要封装统一的异常。
 * <p>
 * TypeHandler 有非常多的子类，当然所有子类都是继承自 BaseTypeHandler 抽象类
 * <p>
 * <p>
 * The base {@link TypeHandler} for references a generic type.
 * <p>
 * Important: Since 3.5.0, This class never call the {@link ResultSet#wasNull()} and {@link CallableStatement#wasNull()}
 * method for handling the SQL {@code NULL} value. In other words, {@code null} value handling should be performed on
 * subclass.
 * </p>
 *
 * @author Clinton Begin
 * @author Simone Tripodi
 * @author Kzuki Shimizu
 */
public abstract class BaseTypeHandler<T> extends TypeReference<T> implements TypeHandler<T> {

  /**
   * @deprecated Since 3.5.0 - See https://github.com/mybatis/mybatis-3/issues/1203. This field will remove future.
   */
  @Deprecated
  protected Configuration configuration;

  /**
   * Sets the configuration.
   *
   * @param c
   *          the new configuration
   * @deprecated Since 3.5.0 - See https://github.com/mybatis/mybatis-3/issues/1203. This property will remove future.
   */
  @Deprecated
  public void setConfiguration(Configuration c) {
    this.configuration = c;
  }

  @Override
  public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
    // <1> 参数为空时，设置为 null 类型
    if (parameter == null) {
      if (jdbcType == null) {
        throw new TypeException("JDBC requires that the JdbcType must be specified for all nullable parameters.");
      }
      try {
        ps.setNull(i, jdbcType.TYPE_CODE);
      } catch (SQLException e) {
        throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . "
          + "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. "
          + "Cause: " + e, e);
      }
    }
    // 参数非空时，设置对应的参数
    else {
      try {
        setNonNullParameter(ps, i, parameter, jdbcType);
      } catch (Exception e) {
        //当发生异常时，统一抛出 TypeException 异常。
        throw new TypeException("Error setting non null for parameter #" + i + " with JdbcType " + jdbcType + " . "
          + "Try setting a different JdbcType for this parameter or a different configuration property. " + "Cause: "
          + e, e);
      }
    }
  }

  @Override
  public T getResult(ResultSet rs, String columnName) throws SQLException {
    try {
      return getNullableResult(rs, columnName);
    } catch (Exception e) {
      //当发生异常时，统一抛出 ResultMapException 异常。
      throw new ResultMapException("Error attempting to get column '" + columnName + "' from result set.  Cause: " + e,
        e);
    }
  }

  @Override
  public T getResult(ResultSet rs, int columnIndex) throws SQLException {
    try {
      return getNullableResult(rs, columnIndex);
    } catch (Exception e) {
      //当发生异常时，统一抛出 ResultMapException 异常。
      throw new ResultMapException("Error attempting to get column #" + columnIndex + " from result set.  Cause: " + e,
        e);
    }
  }

  @Override
  public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
    try {
      return getNullableResult(cs, columnIndex);
    } catch (Exception e) {
      //当发生异常时，统一抛出 ResultMapException 异常。
      throw new ResultMapException(
        "Error attempting to get column #" + columnIndex + " from callable statement.  Cause: " + e, e);
    }
  }

  /**
   * 抽象方法，设置对应的参数。各个子类实现。
   *
   * @param ps
   * @param i
   * @param parameter
   * @param jdbcType
   * @throws SQLException
   */
  public abstract void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType)
    throws SQLException;

  /**
<<<<<<< HEAD
   * 抽象方法，获得指定结果的字段值
   *
   * @param rs
   * @param columnName
   * @return
   * @throws SQLException
=======
   * Gets the nullable result.
   *
   * @param rs
   *          the rs
   * @param columnName
   *          Colunm name, when configuration <code>useColumnLabel</code> is <code>false</code>
   * @return the nullable result
   * @throws SQLException
   *           the SQL exception
>>>>>>> mybatis-3-trunk/master
   */
  public abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;

  /**
   * 抽象方法，获得指定结果的字段值
   *
   * @param rs
   * @param columnIndex
   * @return
   * @throws SQLException
   */
  public abstract T getNullableResult(ResultSet rs, int columnIndex) throws SQLException;

  /**
   * 抽象方法，获得指定结果的字段值
   *
   * @param cs
   * @param columnIndex
   * @return
   * @throws SQLException
   */
  public abstract T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException;

}
