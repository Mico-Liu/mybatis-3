/**
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
 */
package org.apache.ibatis.submitted.member_access;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests for member access of Java Object.
 */
class MemberAccessTest {

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void setUp() throws Exception {
    try (
        Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/member_access/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSessionFactory.getConfiguration().addMapper(Mapper.class);
    }
  }

  @Test
  void parameterMappingAndResultAutoMapping() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);

      Params params = new Params();
      Bean bean = mapper.resultAutoMapping(params);

      assertEquals(params.privateField, bean.privateField);
      assertEquals(params.packagePrivateField, bean.packagePrivateField);
      assertEquals(params.protectedField, bean.protectedField);
      assertEquals(params.publicField, bean.publicField);
      assertEquals(params.getPrivateProperty(), bean.properties.get("privateProperty"));
      assertEquals(params.getPackagePrivateProperty(), bean.properties.get("packagePrivateProperty"));
      assertEquals(params.getProtectedProperty(), bean.properties.get("protectedProperty"));
      assertEquals(params.getPublicProperty(), bean.properties.get("publicProperty"));
    }
  }

  @Test // gh-1258
  void parameterMappingAndResultAutoMappingUsingOgnl() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);

      Params params = new Params();
      Bean bean = mapper.resultAutoMappingUsingOgnl(params);

      assertEquals(params.privateField + "%", bean.privateField);
      assertEquals(params.packagePrivateField + "%", bean.packagePrivateField);
      assertEquals(params.protectedField + "%", bean.protectedField);
      assertEquals(params.publicField + "%", bean.publicField);
      assertEquals(params.getPrivateProperty() + "%", bean.properties.get("privateProperty"));
      assertEquals(params.getPackagePrivateProperty() + "%", bean.properties.get("packagePrivateProperty"));
      assertEquals(params.getProtectedProperty() + "%", bean.properties.get("protectedProperty"));
      assertEquals(params.getPublicProperty() + "%", bean.properties.get("publicProperty"));
    }
  }

  @Test
  void parameterMappingAndResultMapping() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);

      Params params = new Params();
      Bean bean = mapper.resultMapping(params);

      assertEquals(params.privateField, bean.privateField);
      assertEquals(params.packagePrivateField, bean.packagePrivateField);
      assertEquals(params.protectedField, bean.protectedField);
      assertEquals(params.publicField, bean.publicField);
      assertEquals(params.getPrivateProperty(), bean.properties.get("privateProperty"));
      assertEquals(params.getPackagePrivateProperty(), bean.properties.get("packagePrivateProperty"));
      assertEquals(params.getProtectedProperty(), bean.properties.get("protectedProperty"));
      assertEquals(params.getPublicProperty(), bean.properties.get("publicProperty"));
    }
  }

  @Test
  void constructorAutoMapping() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);

      {
        Immutable immutable = mapper.privateConstructorAutoMapping();
        assertEquals(1, immutable.properties.size());
        assertEquals("1", immutable.properties.get("arg1"));
      }

      {
        Immutable immutable = mapper.packagePrivateConstructorAutoMapping();
        assertEquals(2, immutable.properties.size());
        assertEquals("1", immutable.properties.get("arg1"));
        assertEquals("2", immutable.properties.get("arg2"));
      }

      {
        Immutable immutable = mapper.protectedConstructorAutoMapping();
        assertEquals(3, immutable.properties.size());
        assertEquals("1", immutable.properties.get("arg1"));
        assertEquals("2", immutable.properties.get("arg2"));
        assertEquals("3", immutable.properties.get("arg3"));
      }

      {
        Immutable immutable = mapper.publicConstructorAutoMapping();
        assertEquals(4, immutable.properties.size());
        assertEquals("1", immutable.properties.get("arg1"));
        assertEquals("2", immutable.properties.get("arg2"));
        assertEquals("3", immutable.properties.get("arg3"));
        assertEquals("4", immutable.properties.get("arg4"));
      }
    }

  }

  @Test
  void constructorMapping() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);

      {
        Immutable immutable = mapper.privateConstructorMapping();
        assertEquals(1, immutable.properties.size());
        assertEquals("1", immutable.properties.get("arg1"));
      }

      {
        Immutable immutable = mapper.packagePrivateConstructorMapping();
        assertEquals(2, immutable.properties.size());
        assertEquals("1", immutable.properties.get("arg1"));
        assertEquals("2", immutable.properties.get("arg2"));
      }

      {
        Immutable immutable = mapper.protectedConstructorMapping();
        assertEquals(3, immutable.properties.size());
        assertEquals("1", immutable.properties.get("arg1"));
        assertEquals("2", immutable.properties.get("arg2"));
        assertEquals("3", immutable.properties.get("arg3"));
      }

      {
        Immutable immutable = mapper.publicConstructorMapping();
        assertEquals(4, immutable.properties.size());
        assertEquals("1", immutable.properties.get("arg1"));
        assertEquals("2", immutable.properties.get("arg2"));
        assertEquals("3", immutable.properties.get("arg3"));
        assertEquals("4", immutable.properties.get("arg4"));
      }
    }

  }

  interface Mapper {
    @Select({
        // @formatter:off
        "SELECT"
          ,"#{privateField} as privateField"
          ,",#{packagePrivateField} as packagePrivateField"
          ,",#{protectedField} as protectedField"
          ,",#{publicField} as publicField"
          ,",#{privateProperty} as privateProperty"
          ,",#{packagePrivateProperty} as packagePrivateProperty"
          ,",#{protectedProperty} as protectedProperty"
          ,",#{publicProperty} as publicProperty"
        ,"FROM"
          ,"INFORMATION_SCHEMA.SYSTEM_USERS"
        // @formatter:on
    })
    Bean resultAutoMapping(Params params);

    @Select({
        // @formatter:off
