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
package org.apache.ibatis.binding;

import org.apache.ibatis.annotations.Flush;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ParamNameResolver;
import org.apache.ibatis.reflection.TypeParameterResolver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Mapper 方法。在 Mapper 接口中，每个定义的方法，对应一个 MapperMethod 对象
 * <p>
 * 我们自定义的Mapper接口，定义了n多方法，每个方法都对应一个MapperMethod对象
 * <p>
 * 此类作用：
 * 解析Mapper接口的方法，并封装成MapperMethod对象
 * 将Sql命令，正确路由到恰当的SqlSession的方法上
 *
 * @author Clinton Begin
 * @author Eduardo Macarron
 * @author Lasse Voss
 * @author Kazuki Shimizu
 */
public class MapperMethod {

  /**
   * SqlCommand 对象。 保存了Sql命令的类型和键id
   */
  private final SqlCommand command;
  /**
   * MethodSignature 对象。保存了Mapper接口方法的解析信息
   */
  private final MethodSignature method;

  /**
   * @param mapperInterface Mapper 接口
   * @param method          Mapper中的方法
   * @param config          整个应用的配置信息
   */
  public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
    this.command = new SqlCommand(config, mapperInterface, method);
    this.method = new MethodSignature(config, mapperInterface, method);
  }

  /**
   * 根据解析结果，路由到恰当的SqlSession方法上
   *
   * @param sqlSession
   * @param args
   * @return
   */
  public Object execute(SqlSession sqlSession, Object[] args) {
    Object result;
    switch (command.getType()) {
      case INSERT: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.insert(command.getName(), param));
        break;
      }
      case UPDATE: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.update(command.getName(), param));
        break;
      }
      case DELETE: {
        Object param = method.convertArgsToSqlCommandParam(args);
        result = rowCountResult(sqlSession.delete(command.getName(), param));
        break;
      }
      case SELECT:
        if (method.returnsVoid() && method.hasResultHandler()) {
          executeWithResultHandler(sqlSession, args);
          result = null;
        } else if (method.returnsMany()) {
          result = executeForMany(sqlSession, args);
        } else if (method.returnsMap()) {
          result = executeForMap(sqlSession, args);
        } else if (method.returnsCursor()) {
          result = executeForCursor(sqlSession, args);
        } else {
          Object param = method.convertArgsToSqlCommandParam(args);
          result = sqlSession.selectOne(command.getName(), param);
          if (method.returnsOptional() && (result == null || !method.getReturnType().equals(result.getClass()))) {
            result = Optional.ofNullable(result);
          }
        }
        break;
      case FLUSH:
        result = sqlSession.flushStatements();
        break;
      default:
        throw new BindingException("Unknown execution method for: " + command.getName());
    }
    if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
      throw new BindingException("Mapper method '" + command.getName()
        + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
    }
    return result;
  }

  private Object rowCountResult(int rowCount) {
    final Object result;
    if (method.returnsVoid()) {
      result = null;
    } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
      result = rowCount;
    } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
      result = (long) rowCount;
    } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
      result = rowCount > 0;
    } else {
      throw new BindingException(
        "Mapper method '" + command.getName() + "' has an unsupported return type: " + method.getReturnType());
    }
    return result;
  }

  private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {
    MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(command.getName());
    if (!StatementType.CALLABLE.equals(ms.getStatementType())
      && void.class.equals(ms.getResultMaps().get(0).getType())) {
      throw new BindingException(
        "method " + command.getName() + " needs either a @ResultMap annotation, a @ResultType annotation,"
          + " or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
    }
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      sqlSession.select(command.getName(), param, rowBounds, method.extractResultHandler(args));
    } else {
      sqlSession.select(command.getName(), param, method.extractResultHandler(args));
    }
  }

  private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
    List<E> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      result = sqlSession.selectList(command.getName(), param, rowBounds);
    } else {
      result = sqlSession.selectList(command.getName(), param);
    }
    // issue #510 Collections & arrays support
    if (!method.getReturnType().isAssignableFrom(result.getClass())) {
      if (method.getReturnType().isArray()) {
        return convertToArray(result);
      } else {
        return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
      }
    }
    return result;
  }

  private <T> Cursor<T> executeForCursor(SqlSession sqlSession, Object[] args) {
    Cursor<T> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      result = sqlSession.selectCursor(command.getName(), param, rowBounds);
    } else {
      result = sqlSession.selectCursor(command.getName(), param);
    }
    return result;
  }

  private <E> Object convertToDeclaredCollection(Configuration config, List<E> list) {
    Object collection = config.getObjectFactory().create(method.getReturnType());
    MetaObject metaObject = config.newMetaObject(collection);
    metaObject.addAll(list);
    return collection;
  }

  @SuppressWarnings("unchecked")
  private <E> Object convertToArray(List<E> list) {
    Class<?> arrayComponentType = method.getReturnType().getComponentType();
    Object array = Array.newInstance(arrayComponentType, list.size());
    if (arrayComponentType.isPrimitive()) {
      for (int i = 0; i < list.size(); i++) {
        Array.set(array, i, list.get(i));
      }
      return array;
    } else {
      return list.toArray((E[]) array);
    }
  }

  private <K, V> Map<K, V> executeForMap(SqlSession sqlSession, Object[] args) {
    Map<K, V> result;
    Object param = method.convertArgsToSqlCommandParam(args);
    if (method.hasRowBounds()) {
      RowBounds rowBounds = method.extractRowBounds(args);
      result = sqlSession.selectMap(command.getName(), param, method.getMapKey(), rowBounds);
    } else {
      result = sqlSession.selectMap(command.getName(), param, method.getMapKey());
    }
    return result;
  }

  public static class ParamMap<V> extends HashMap<String, V> {

    private static final long serialVersionUID = -2212268410512043556L;

    @Override
    public V get(Object key) {
      if (!super.containsKey(key)) {
        throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + keySet());
      }
      return super.get(key);
    }

  }

  public static class SqlCommand {
    /**
     * {@link MappedStatement#getId()}。通过它可以找到MappedStatement。 name有可能为空，当type={@link SqlCommandType#FLUSH}
     * <p>
     * 对应 MappedStatement#getId() 方法获得的标识。实际上，就是 ${NAMESPACE_NAME}.${语句_ID}，
     * 例如："org.apache.ibatis.autoconstructor.AutoConstructorMapper.getSubject2"
     */
    private final String name;
    /**
     * SQL 命令类型。CRUD
     */
    private final SqlCommandType type;

    public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
      final String methodName = method.getName();
      final Class<?> declaringClass = method.getDeclaringClass();
      // <1> 获得 MappedStatement 对象
      MappedStatement ms = resolveMappedStatement(mapperInterface, methodName, declaringClass, configuration);
      // <2> 找不到 MappedStatement
      if (ms == null) {
        // 如果有 @Flush 注解，则标记为 FLUSH 类型
        if (method.getAnnotation(Flush.class) != null) {
          name = null;
          type = SqlCommandType.FLUSH;
        }
        // ，如果再找不到 MappedStatement就抛出 BindingException 异常
        else {
          throw new BindingException(
            "Invalid bound statement (not found): " + mapperInterface.getName() + "." + methodName);
        }
      }
      // <3> 找到 MappedStatement
      else {
        // 获得 name
        name = ms.getId();
        // 获得 type
        type = ms.getSqlCommandType();
        // 如果是 UNKNOWN 类型，抛出 BindingException 异常
        if (type == SqlCommandType.UNKNOWN) {
          throw new BindingException("Unknown execution method for: " + name);
        }
      }
    }

    public String getName() {
      return name;
    }

    public SqlCommandType getType() {
      return type;
    }

    /**
     * 获得 MappedStatement 对象
     *
     * @param mapperInterface Mapper接口
     * @param methodName      Mapper的方法名称
     * @param declaringClass  Mapper中的方法methodName所在的类
     * @param configuration   mybatis 配置
     * @return
     */
    private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName, Class<?> declaringClass,
                                                   Configuration configuration) {
      // <1> 获得编号。 接口全名称加上方法名。即包路径加接口名称加方法名
      String statementId = mapperInterface.getName() + "." + methodName;
      //判断配置文件里里有 MappedStatement 对象，并返回
      if (configuration.hasStatement(statementId)) {
        return configuration.getMappedStatement(statementId);
      }
      // 如果当前方法是在当前接口里面，而配置文件又没配置语句，则返回null，需要在编译期间就抛出异常，告知用户
      else if (mapperInterface.equals(declaringClass)) {
        return null;
      }

      // 遍历父接口，继续获得 MappedStatement 对象
      for (Class<?> superInterface : mapperInterface.getInterfaces()) {
        //如果declaringClass是superInterface的接口或declaringClass和superInterface的类型是否相同。 如果方法定义在父类里，此时放回true
        if (declaringClass.isAssignableFrom(superInterface)) {
          MappedStatement ms = resolveMappedStatement(superInterface, methodName, declaringClass, configuration);
          if (ms != null) {
            return ms;
          }
        }
      }

      //找不到，返回null。会抛出异常
      return null;
    }
  }

  /**
   * 是 MapperMethod 的内部静态类，方法签名
   */
  public static class MethodSignature {

    /**
     * 返回类型是否为集合
     */
    private final boolean returnsMany;
    /**
     * 返回类型是否为 Map
     */
    private final boolean returnsMap;
    /**
     * 返回类型是否为 void
     */
    private final boolean returnsVoid;
    /**
     * 返回类型是否为 {@link org.apache.ibatis.cursor.Cursor}
     */
    private final boolean returnsCursor;
    /**
     * 返回类型是否为 {@link java.util.Optional}
     */
    private final boolean returnsOptional;
    /**
     * 返回类型
     */
    private final Class<?> returnType;
    /**
     * 返回方法上的 {@link MapKey#value()} ，前提是返回类型为 Map
     */
    private final String mapKey;
    /**
     * 获得 {@link ResultHandler} 在方法参数中的位置。
     * <p>
     * 如果为 null ，说明不存在这个类型
     */
    private final Integer resultHandlerIndex;
    /**
     * 获得 {@link RowBounds} 在方法参数中的位置。
     * <p>
     * 如果为 null ，说明不存在这个类型
     */
    private final Integer rowBoundsIndex;
    /**
     * ParamNameResolver 对象
     */
    private final ParamNameResolver paramNameResolver;

    /**
     * @param configuration   mybatis 配置对象
     * @param mapperInterface Mapper接口
     * @param method          Mapper方法
     */
    public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
      // 初始化 returnType 属性
      Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
      // 普通类
      if (resolvedReturnType instanceof Class<?>) {
        this.returnType = (Class<?>) resolvedReturnType;
      }
      //泛型
      else if (resolvedReturnType instanceof ParameterizedType) {
        this.returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
      }
      //其它情况，比如内部类
      else {
        this.returnType = method.getReturnType();
      }

      //赋值returnsVoid，判断是否是Void返回类型
      this.returnsVoid = void.class.equals(this.returnType);
      //赋值returnsMany，判断是否是集合类型，Collection或数组
      this.returnsMany = configuration.getObjectFactory().isCollection(this.returnType) || this.returnType.isArray();
      //赋值returnsCursor，判断是否是游标类型
      this.returnsCursor = Cursor.class.equals(this.returnType);
      //赋值returnsOptional，判断是否是Optional类型
      this.returnsOptional = Optional.class.equals(this.returnType);
      //赋值mapKey，在方法上MapKey注解的value值
      this.mapKey = getMapKey(method);
      //返回值是Map
      this.returnsMap = this.mapKey != null;
      //分页参数RowBounds在参数中的位置，就是rowBoundsIndex
      this.rowBoundsIndex = getUniqueParamIndex(method, RowBounds.class);
      //resultHandlerIndex在参数中的位置，就是resultHandlerIndex
      this.resultHandlerIndex = getUniqueParamIndex(method, ResultHandler.class);
      //解析自定义Mapper接口的方法参数，赋值到ParamNameResolver的names中
      this.paramNameResolver = new ParamNameResolver(configuration, method);
    }

    /**
     * @param args
     * @return
     */
    public Object convertArgsToSqlCommandParam(Object[] args) {
      return paramNameResolver.getNamedParams(args);
    }

    /**
     * 自定义Mapper接口中的方法参数是否有RowBounds参数
     *
     * @return
     */
    public boolean hasRowBounds() {
      return rowBoundsIndex != null;
    }

    /**
     * 获取自定义Mapper接口中方法的RowBounds参数
     *
     * @param args 自定义Mapper接口中方法的所有参数
     * @return
     */
    public RowBounds extractRowBounds(Object[] args) {
      return hasRowBounds() ? (RowBounds) args[rowBoundsIndex] : null;
    }

    /**
     * 自定义Mapper接口中的方法参数是否有ResultHandler参数
     *
     * @return
     */
    public boolean hasResultHandler() {
      return resultHandlerIndex != null;
    }

    /**
     * 获取自定义Mapper接口方法的ResultHandler
     *
     * @param args
     * @return
     */
    public ResultHandler extractResultHandler(Object[] args) {
      return hasResultHandler() ? (ResultHandler) args[resultHandlerIndex] : null;
    }

<<<<<<< HEAD
    /**
     *
     * @return
     */
    public String getMapKey() {
      return mapKey;
    }

=======
>>>>>>> mybatis-3-trunk/master
    public Class<?> getReturnType() {
      return returnType;
    }

    public boolean returnsMany() {
      return returnsMany;
    }

    public boolean returnsMap() {
      return returnsMap;
    }

    public boolean returnsVoid() {
      return returnsVoid;
    }

    public boolean returnsCursor() {
      return returnsCursor;
    }

    /**
     * return whether return type is {@code java.util.Optional}.
     *
     * @return return {@code true}, if return type is {@code java.util.Optional}
     * @paramType 指定的参数类型
     * @since 3.5.0
     */
    public boolean returnsOptional() {
      return returnsOptional;
    }

    private Integer getUniqueParamIndex(Method method, Class<?> paramType) {
      Integer index = null;
      //获取方法的所有参数
      final Class<?>[] argTypes = method.getParameterTypes();
      //遍历方法所有参数
      for (int i = 0; i < argTypes.length; i++) {
        //如果paramType和argTypes[i]类型相同，或paramType是argTypes[i]的父类，就返回true
        if (paramType.isAssignableFrom(argTypes[i])) {
          //还未匹配到
          if (index == null) {
            index = i;
          }
          //如果匹配到多个参数，就抛异常
          else {
            throw new BindingException(
              method.getName() + " cannot have multiple " + paramType.getSimpleName() + " parameters");
          }
        }
      }
      return index;
    }

<<<<<<< HEAD
    /**
     * 获取MapKey注解上的名称。
     * 两个条件
     * 1、返回类型必须的父类或父接口，一定是Map
     * 2、在方法上MapKey注解的value就是这个key值
     *
     * @param method
     * @return
     */
=======
    public String getMapKey() {
      return mapKey;
    }

>>>>>>> mybatis-3-trunk/master
    private String getMapKey(Method method) {
      String mapKey = null;
      //如果返回的类型是Map或者是Map的子类
      if (Map.class.isAssignableFrom(method.getReturnType())) {
        //获取方法上的MapKey注解
        final MapKey mapKeyAnnotation = method.getAnnotation(MapKey.class);
        //如果配置了MapKey注解，就返回注解对应的值
        if (mapKeyAnnotation != null) {
          mapKey = mapKeyAnnotation.value();
        }
        //没有配置注解，就返回null
      }
      return mapKey;
    }
  }

}
