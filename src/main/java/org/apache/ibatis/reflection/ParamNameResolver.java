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
package org.apache.ibatis.reflection;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 参数名解析器
 */
public class ParamNameResolver {

  public static final String GENERIC_NAME_PREFIX = "param";

  private final boolean useActualParamName;

  /**
   * <p>
   * The key is the index and the value is the name of the parameter.<br />
   * The name is obtained from {@link Param} if specified. When {@link Param} is not specified, the parameter index is
   * used. Note that this index could be different from the actual index when the method has special parameters (i.e.
   * {@link RowBounds} or {@link ResultHandler}).
   * </p>
   * <ul>
   * <li>aMethod(@Param("M") int a, @Param("N") int b) -&gt; {{0, "M"}, {1, "N"}}</li>
   * <li>aMethod(int a, int b) -&gt; {{0, "0"}, {1, "1"}}</li>
   * <li>aMethod(int a, RowBounds rb, int b) -&gt; {{0, "0"}, {2, "1"}}</li>
   * </ul>
   * <p>
   * 参数名映射
   * <p>
   * KEY：参数顺序
   * VALUE：参数名
   */
  private final SortedMap<Integer, String> names;
  /**
   * 是否有 {@link Param} 注解的参数
   */
  private boolean hasParamAnnotation;

  /**
   * @param config mybatis 配置
   * @param method 方法
   */
  public ParamNameResolver(Configuration config, Method method) {
<<<<<<< HEAD
    //获取所有方法参数类型
=======
    this.useActualParamName = config.isUseActualParamName();
>>>>>>> mybatis-3-trunk/master
    final Class<?>[] paramTypes = method.getParameterTypes();
    //获取方法的所有注解
    final Annotation[][] paramAnnotations = method.getParameterAnnotations();
    final SortedMap<Integer, String> map = new TreeMap<>();
    int paramCount = paramAnnotations.length;
    // get names from @Param annotations
    for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
      // 忽略，如果是特殊参数。 RowBounds和ResultHandler
      if (isSpecialParameter(paramTypes[paramIndex])) {
        // skip special parameters
        continue;
      }
      String name = null;
      // 遍历当前参数的注解，因为一个参数可能配置了好几个注解
      for (Annotation annotation : paramAnnotations[paramIndex]) {
        //只查Param注解，过滤其它注解
        if (annotation instanceof Param) {
          hasParamAnnotation = true;
          //获取注解的名称。
          name = ((Param) annotation).value();
          break;
        }
      }
      //如果没有配置Param注解
      if (name == null) {
        // @Param was not specified.
<<<<<<< HEAD
        // 其次，获取真实的参数名
        // 默认是开启的
        if (config.isUseActualParamName()) {
          //获取指定的参数名
=======
        if (useActualParamName) {
>>>>>>> mybatis-3-trunk/master
          name = getActualParamName(method, paramIndex);
        }
        // 最差，使用 map 的顺序，作为编号
        if (name == null) {
          // use the parameter index as the name ("0", "1", ...)
          // gcode issue #71
          /**
           * 为什么这里没有使用name = GENERIC_NAME_PREFIX + String.valueOf(map.size()),
           * {@link ParamNameResolver#getNamedParams} 方法有使用GENERIC_NAME_PREFIX
           */
          name = String.valueOf(map.size());
        }
      }
      map.put(paramIndex, name);
    }
    // 构建不可变集合
    names = Collections.unmodifiableSortedMap(map);
  }

  /**
   * 获取方法的指定参数名
   *
   * @param method     方法
   * @param paramIndex 参数序号
   * @return 参数名
   */
  private String getActualParamName(Method method, int paramIndex) {
    return ParamNameUtil.getParamNames(method).get(paramIndex);
  }

  /**
   * 是否是指定参数
   *
   * @param clazz 是否RowBounds参数或RowBounds子类参数。或者ResultHandler参数或ResultHandler子类参数
   * @return
   */
  private static boolean isSpecialParameter(Class<?> clazz) {
    return RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz);
  }

  /**
   * 参数名的集合
   * <p>
   * Returns parameter names referenced by SQL providers.
   *
   * @return the names
   */
  public String[] getNames() {
    return names.values().toArray(new String[0]);
  }

  /**
   * 获得参数名与值的映射
   *
   * <p>
   * A single non-special parameter is returned without a name. Multiple parameters are named using the naming rule. In
   * addition to the default names, this method also adds the generic names (param1, param2, ...).
   * </p>
<<<<<<< HEAD
   * 获得参数名与值的映射
=======
   *
   * @param args
   *          the args
   * @return the named params
>>>>>>> mybatis-3-trunk/master
   */
  public Object getNamedParams(Object[] args) {
    final int paramCount = names.size();
    // 无参数，则返回 null
    if (args == null || paramCount == 0) {
      return null;
<<<<<<< HEAD
    }
    // 只有一个非注解的参数，直接返回首元素
    else if (!hasParamAnnotation && paramCount == 1) {
      return args[names.firstKey()];
=======
    } else if (!hasParamAnnotation && paramCount == 1) {
      Object value = args[names.firstKey()];
      return wrapToMapIfCollection(value, useActualParamName ? names.get(0) : null);
>>>>>>> mybatis-3-trunk/master
    } else {
      // 集合。
      // 组合 1 ：KEY：参数名，VALUE：参数值
      // 组合 2 ：KEY：GENERIC_NAME_PREFIX + 参数顺序，VALUE ：参数值
      final Map<String, Object> param = new ParamMap<>();
      int i = 0;
      // 遍历 names 集合
      for (Map.Entry<Integer, String> entry : names.entrySet()) {
        // 组合 1 ：添加到 param 中
        // names的key是从0开始到参数size-1。value是参数名
        // param的key是参数名。value是args对应序号的值
        param.put(entry.getValue(), args[entry.getKey()]);

        // add generic param names (param1, param2, ...)
<<<<<<< HEAD
        // 组合 2 ：添加到 param 中
        final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
=======
        final String genericParamName = GENERIC_NAME_PREFIX + (i + 1);
>>>>>>> mybatis-3-trunk/master
        // ensure not to overwrite parameter named with @Param
        if (!names.containsValue(genericParamName)) {
          param.put(genericParamName, args[entry.getKey()]);
        }
        i++;
      }
      return param;
    }
  }

  /**
   * Wrap to a {@link ParamMap} if object is {@link Collection} or array.
   *
   * @param object a parameter object
   * @param actualParamName an actual parameter name
   *                        (If specify a name, set an object to {@link ParamMap} with specified name)
   * @return a {@link ParamMap}
   * @since 3.5.5
   */
  public static Object wrapToMapIfCollection(Object object, String actualParamName) {
    if (object instanceof Collection) {
      ParamMap<Object> map = new ParamMap<>();
      map.put("collection", object);
      if (object instanceof List) {
        map.put("list", object);
      }
      Optional.ofNullable(actualParamName).ifPresent(name -> map.put(name, object));
      return map;
    } else if (object != null && object.getClass().isArray()) {
      ParamMap<Object> map = new ParamMap<>();
      map.put("array", object);
      Optional.ofNullable(actualParamName).ifPresent(name -> map.put(name, object));
      return map;
    }
    return object;
  }

}
