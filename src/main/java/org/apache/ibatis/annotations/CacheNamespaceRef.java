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
 * 指向指定命名空间的注解
 * <p>
 * 对应 XML 标签为 <cache-ref />
 * <p>
 * The annotation that reference a cache.
 *
 * <p>If you use this annotation, should be specified either {@link #value()} or {@link #name()} attribute.
 *
 * <p>
<<<<<<< HEAD
 * If you use this annotation, should be specified either {@link #value()} or {@link #name()} attribute.
 * </p>
=======
 * <b>How to use:</b>
 *
 * <pre>
 * &#064;CacheNamespaceRef(UserMapper.class)
 * public interface AdminUserMapper {
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
@Target(ElementType.TYPE)//标注在类上的注解
public @interface CacheNamespaceRef {

  /**
<<<<<<< HEAD
   * 见 {@link MapperAnnotationBuilder#parseCacheRef()} 方法
   * <p>
   * A namespace type to reference a cache (the namespace name become a FQCN of specified type).
=======
   * Returns the namespace type to reference a cache (the namespace name become a FQCN of specified type).
   *
   * @return the namespace type to reference a cache
>>>>>>> mybatis-3-trunk/master
   */
  Class<?> value() default void.class;

  /**
<<<<<<< HEAD
   * 指向的命名空间
   * <p>
   * A namespace name to reference a cache.
   *
=======
   * Returns the namespace name to reference a cache.
   *
   * @return the namespace name
>>>>>>> mybatis-3-trunk/master
   * @since 3.4.2
   */
  String name() default "";
}
