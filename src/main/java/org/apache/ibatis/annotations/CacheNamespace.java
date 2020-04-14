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

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.decorators.LruCache;
import org.apache.ibatis.cache.impl.PerpetualCache;

import java.lang.annotation.*;

/**
<<<<<<< HEAD
 * 缓存空间配置的注解
=======
 * The annotation that specify to use cache on namespace(e.g. mapper interface).
 *
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * &#064;acheNamespace(implementation = CustomCache.class, properties = {
 *   &#064;Property(name = "host", value = "${mybatis.cache.host}"),
 *   &#064;Property(name = "port", value = "${mybatis.cache.port}"),
 *   &#064;Property(name = "name", value = "usersCache")
 * })
 * public interface UserMapper {
 *   // ...
 * }
 * </pre>
>>>>>>> mybatis-3-trunk/master
 *
 * @author Clinton Begin
 * @author Kazuki Shimizu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)// Mapper 类上
public @interface CacheNamespace {
<<<<<<< HEAD
  /**
   * @return 负责存储的 Cache 实现类
=======

  /**
   * Returns the cache implementation type to use.
   *
   * @return the cache implementation type
>>>>>>> mybatis-3-trunk/master
   */
  Class<? extends Cache> implementation() default PerpetualCache.class;

  /**
<<<<<<< HEAD
   * @return 负责过期的 Cache 实现类
=======
   * Returns the cache evicting implementation type to use.
   *
   * @return the cache evicting implementation type
>>>>>>> mybatis-3-trunk/master
   */
  Class<? extends Cache> eviction() default LruCache.class;

  /**
<<<<<<< HEAD
   * @return 清空缓存的频率。0 代表不清空
=======
   * Returns the flush interval.
   *
   * @return the flush interval
>>>>>>> mybatis-3-trunk/master
   */
  long flushInterval() default 0;

  /**
<<<<<<< HEAD
   * @return 缓存容器大小
=======
   * Return the cache size.
   *
   * @return the cache size
>>>>>>> mybatis-3-trunk/master
   */
  int size() default 1024;

  /**
<<<<<<< HEAD
   * @return 是否序列化。{@link org.apache.ibatis.cache.decorators.SerializedCache}
=======
   * Returns whether use read/write cache.
   *
   * @return {@code true} if use read/write cache; {@code false} if otherwise
>>>>>>> mybatis-3-trunk/master
   */
  boolean readWrite() default true;

  /**
<<<<<<< HEAD
   * @return 是否阻塞。{@link org.apache.ibatis.cache.decorators.BlockingCache}
=======
   * Returns whether block the cache at request time or not.
   *
   * @return {@code true} if block the cache; {@code false} if otherwise
>>>>>>> mybatis-3-trunk/master
   */
  boolean blocking() default false;

  /**
<<<<<<< HEAD
   * {@link Property} 数组
   * <p>
   * Property values for a implementation object.
   *
=======
   * Returns property values for a implementation object.
   *
   * @return property values
>>>>>>> mybatis-3-trunk/master
   * @since 3.4.2
   */
  Property[] properties() default {};

}
