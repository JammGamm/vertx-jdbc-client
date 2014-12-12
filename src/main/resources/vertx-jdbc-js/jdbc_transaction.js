/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/** @module vertx-jdbc-js/jdbc_transaction */
var utils = require('vertx-js/util/utils');
var JdbcActions = require('vertx-jdbc-js/jdbc_actions');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JJdbcTransaction = io.vertx.ext.jdbc.JdbcTransaction;

/**

 @class
*/
var JdbcTransaction = function(j_val) {

  var j_jdbcTransaction = j_val;
  var that = this;
  JdbcActions.call(this, j_val);

  /**
   Executes the given SQL statement

   @public
   @param sql {string} 
   @param resultHandler {function} 
   */
  this.execute = function(sql, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_jdbcTransaction.execute(sql, function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**
   Executes the given SQL <code>SELECT</code> statement which returns the results of the query.

   @public
   @param sql {string} 
   @param params {todo} 
   @param resultHandler {function} 
   */
  this.query = function(sql, params, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && __args[1] instanceof Array && typeof __args[2] === 'function') {
      j_jdbcTransaction.query(sql, utils.convParamJsonArray(params), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnListSetJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**
   Executes the given SQL statement which may be an <code>INSERT</code>, <code>UPDATE</code>, or <code>DELETE</code>
   statement.

   @public
   @param sql {string} 
   @param params {todo} 
   @param resultHandler {function} 
   */
  this.update = function(sql, params, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] === 'object' && __args[1] instanceof Array && typeof __args[2] === 'function') {
      j_jdbcTransaction.update(sql, utils.convParamJsonArray(params), function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnJson(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**
   Commits the given transaction.

   @public
   @param handler {function} 
   */
  this.commit = function(handler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_jdbcTransaction.commit(function(ar) {
      if (ar.succeeded()) {
        handler(null, null);
      } else {
        handler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  /**
   Rolls back the given transaction.

   @public
   @param handler {function} 
   */
  this.rollback = function(handler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_jdbcTransaction.rollback(function(ar) {
      if (ar.succeeded()) {
        handler(null, null);
      } else {
        handler(null, ar.cause());
      }
    });
    } else utils.invalidArgs();
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_jdbcTransaction;
};

// We export the Constructor function
module.exports = JdbcTransaction;