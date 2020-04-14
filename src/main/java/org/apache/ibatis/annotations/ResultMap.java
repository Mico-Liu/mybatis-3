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
 * 使用的结果集的注解
=======
 * The annotation that specify result map names to use.
 *
 * <p>
 * <b>How to use:</b><br>
 * Mapper interface:
 *
 * <pre>
 * public interface UserMapper {
 *   &#064;Select("SELECT id, name FROM users WHERE id = #{id}")
 *   &#064;ResultMap("userMap")
 *   User selectById(int id);
 *
 *   &#064;Select("SELECT u.id, u.name FROM users u INNER JOIN users_email ue ON u.id = ue.id WHERE ue.email = #{email}")
 *   &#064;ResultMap("userMap")
 *   User selectByEmail(String email);
 * }
 * </pre>
 * Mapper XML:
 * <pre>{@code
 * <mapper namespace="com.example.mapper.UserMapper">
 *   <resultMap id="userMap" type="com.example.model.User">
 *     <id property="id" column="id" />
 *     <result property="name" column="name" />
 *     <association property="email" select="selectUserEmailById" column="id" fetchType="lazy"/>
 *   </resultMap>
 * </mapper>
 * }
 * </pre>
>>>>>>> mybatis-3-trunk/master
 *
 * @author Jeff Butler
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)// 方法
public @interface ResultMap {
  /**
<<<<<<< HEAD
   * @return 结果集
=======
   * Returns result map names to use.
   *
   * @return result map names
>>>>>>> mybatis-3-trunk/master
   */
  String[] value();
}
