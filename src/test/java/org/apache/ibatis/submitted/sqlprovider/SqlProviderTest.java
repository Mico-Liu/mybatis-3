/**
 *    Copyright 2009-2019 the original author or authors.
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
package org.apache.ibatis.submitted.sqlprovider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.BaseDataTest;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.annotation.ProviderContext;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SqlProviderTest {

  private static SqlSessionFactory sqlSessionFactory;
  private static SqlSessionFactory sqlSessionFactoryForDerby;

  @BeforeAll
  static void setUp() throws Exception {
    // create a SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/sqlprovider/mybatis-config.xml")) {
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      sqlSessionFactory.getConfiguration().addMapper(StaticMethodSqlProviderMapper.class);
      sqlSessionFactory.getConfiguration().addMapper(DatabaseIdMapper.class);
    }
    // populate in-memory database
    BaseDataTest.runScript(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(),
        "org/apache/ibatis/submitted/sqlprovider/CreateDB.sql");

    // create a SqlSessionFactory
    try (Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/sqlprovider/mybatis-config.xml")) {
      sqlSessionFactoryForDerby = new SqlSessionFactoryBuilder().build(reader, "development-derby");
      sqlSessionFactoryForDerby.getConfiguration().addMapper(DatabaseIdMapper.class);
    }
  }

  // Test for list
  @Test
  void shouldGetTwoUsers() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<Integer> list = new ArrayList<>();
      list.add(1);
      list.add(3);
      List<User> users = mapper.getUsers(list);
      assertEquals(2, users.size());
      assertEquals("User1", users.get(0).getName());
      assertEquals("User3", users.get(1).getName());
    }
  }

  // Test for simple value without @Param
  @Test
  void shouldGetOneUser() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      {
        User user = mapper.getUser(4);
        assertNotNull(user);
        assertEquals("User4", user.getName());
      }
      {
        User user = mapper.getUser(null);
        assertNull(user);
      }
    }
  }

  // Test for empty
  @Test
  void shouldGetAllUsers() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User> users = mapper.getAllUsers();
      assertEquals(4, users.size());
      assertEquals("User1", users.get(0).getName());
      assertEquals("User2", users.get(1).getName());
      assertEquals("User3", users.get(2).getName());
      assertEquals("User4", users.get(3).getName());
    }
  }

  // Test for single JavaBean
  @Test
  void shouldGetUsersByCriteria() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      {
        User criteria = new User();
        criteria.setId(1);
        List<User> users = mapper.getUsersByCriteria(criteria);
        assertEquals(1, users.size());
        assertEquals("User1", users.get(0).getName());
      }
      {
        User criteria = new User();
        criteria.setName("User");
        List<User> users = mapper.getUsersByCriteria(criteria);
        assertEquals(4, users.size());
        assertEquals("User1", users.get(0).getName());
        assertEquals("User2", users.get(1).getName());
        assertEquals("User3", users.get(2).getName());
        assertEquals("User4", users.get(3).getName());
      }
    }
  }

  // Test for single map
  @Test
  void shouldGetUsersByCriteriaMap() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("id", 1);
        List<User> users = mapper.getUsersByCriteriaMap(criteria);
        assertEquals(1, users.size());
        assertEquals("User1", users.get(0).getName());
      }
      {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("name", "User");
        List<User> users = mapper.getUsersByCriteriaMap(criteria);
        assertEquals(4, users.size());
        assertEquals("User1", users.get(0).getName());
        assertEquals("User2", users.get(1).getName());
        assertEquals("User3", users.get(2).getName());
        assertEquals("User4", users.get(3).getName());
      }
    }
  }

  @Test
  void shouldGetUsersByCriteriaMapWithParam() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("id", 1);
        List<User> users = mapper.getUsersByCriteriaMapWithParam(criteria);
        assertEquals(1, users.size());
        assertEquals("User1", users.get(0).getName());
      }
      {
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("name", "User");
        List<User> users = mapper.getUsersByCriteriaMapWithParam(criteria);
        assertEquals(4, users.size());
        assertEquals("User1", users.get(0).getName());
        assertEquals("User2", users.get(1).getName());
        assertEquals("User3", users.get(2).getName());
        assertEquals("User4", users.get(3).getName());
      }
    }
  }

  // Test for multiple parameter without @Param
  @Test
  void shouldGetUsersByName() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User> users = mapper.getUsersByName("User", "id DESC");
      assertEquals(4, users.size());
      assertEquals("User4", users.get(0).getName());
      assertEquals("User3", users.get(1).getName());
      assertEquals("User2", users.get(2).getName());
      assertEquals("User1", users.get(3).getName());
    }
  }

// Test for map without @Param
@Test
  void shouldGetUsersByNameUsingMap() {
    try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
      Mapper mapper = sqlSession.getMapper(Mapper.class);
      List<User> users = mapper.getUsersByNameUsingMap("User", "id DESC");
      assertEquals(4, users.size()