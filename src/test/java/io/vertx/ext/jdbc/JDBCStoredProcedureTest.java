/*
 * Copyright (c) 2011-2014 The original author or authors
 * ------------------------------------------------------
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 *     The Eclipse Public License is available at
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 *     The Apache License v2.0 is available at
 *     http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */

package io.vertx.ext.jdbc;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.test.core.VertxTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:pmlopes@gmail.com">Paulo Lopes</a>
 */
public class JDBCStoredProcedureTest extends VertxTestBase {

  protected SQLClient client;

  private static final List<String> SQL = new ArrayList<>();

  static {
    System.setProperty("textdb.allow_full_path", "true");
    System.setProperty("statement.separator", ";;");

    SQL.add("drop table if exists customers");
    SQL.add("create table customers(id integer generated by default as identity, firstname varchar(50), lastname varchar(50), added timestamp)");
    SQL.add("create procedure new_customer(firstname varchar(50), lastname varchar(50))\n" +
        "  modifies sql data\n" +
        "  insert into customers values (default, firstname, lastname, current_timestamp)");
    SQL.add("create procedure customer_lastname(IN firstname varchar(50), OUT lastname varchar(50))\n" +
        "  modifies sql data\n" +
        "  select lastname into lastname from customers where firstname = firstname");
    SQL.add("create function an_hour_before()\n" +
        "  returns timestamp\n" +
        "  return now() - 1 hour");
    SQL.add("create procedure times2(INOUT param INT)\n" +
        "  modifies sql data\n" +
        "  SET param = param * 2");
  }

  @BeforeClass
  public static void createDb() throws Exception {
    Connection conn = DriverManager.getConnection(config().getString("url"));
    for (String sql : SQL) {
      conn.createStatement().execute(sql);
    }
  }

  @Before
  public void setUp() throws Exception {
    super.setUp();
    client = JDBCClient.createNonShared(vertx, config());
  }

  @After
  public void after() throws Exception {
    client.close();
    super.after();
  }

  protected static JsonObject config() {
    return new JsonObject()
        .put("url", "jdbc:hsqldb:mem:test2?shutdown=true")
        .put("driver_class", "org.hsqldb.jdbcDriver");
  }

  @Test
  public void testStoredProcedure0() {
    connection().callWithParams("{call new_customer(?, ?)}", new JsonArray().add("Paulo").add("Lopes"), null, onSuccess(resultSet -> {
      testComplete();
    }));

    await();
  }

  @Test
  public void testStoredProcedure1() {
    connection().callWithParams("{call customer_lastname(?, ?)}", new JsonArray().add("Paulo"), new JsonArray().addNull().add("VARCHAR"), onSuccess(resultSet -> {
      assertNotNull(resultSet);
      assertEquals(0, resultSet.getResults().size());
      assertEquals("Lopes", resultSet.getOutput().getString(1));
      testComplete();
    }));

    await();
  }

  @Test
  public void testStoredProcedure2() {
    connection().callWithParams("{call an_hour_before()}", null, null, onSuccess(resultSet -> {
      assertNotNull(resultSet);
      assertEquals(1, resultSet.getResults().size());
      testComplete();
    }));

    await();
  }

  @Test
  public void testStoredProcedure3() {
    connection().callWithParams("{call times2(?)}", new JsonArray().add(2), new JsonArray().add("INTEGER"), onSuccess(resultSet -> {
      assertNotNull(resultSet);
      assertEquals(0, resultSet.getResults().size());
      assertEquals(new Integer(4), resultSet.getOutput().getInteger(0));
      testComplete();
    }));

    await();
  }

  private SQLConnection connection() {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<SQLConnection> ref = new AtomicReference<>();
    client.getConnection(onSuccess(conn -> {
      ref.set(conn);
      latch.countDown();
    }));

    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    return ref.get();
  }
}
