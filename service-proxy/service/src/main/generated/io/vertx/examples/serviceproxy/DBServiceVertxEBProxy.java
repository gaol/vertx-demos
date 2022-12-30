/*
* Copyright 2014 Red Hat, Inc.
*
* Red Hat licenses this file to you under the Apache License, version 2.0
* (the "License"); you may not use this file except in compliance with the
* License. You may obtain a copy of the License at:
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/

package io.vertx.examples.serviceproxy;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.Vertx;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;
import io.vertx.serviceproxy.ProxyUtils;

import java.util.List;
import io.vertx.examples.serviceproxy.DBService;
import io.vertx.core.Vertx;
import io.vertx.examples.serviceproxy.DataEntry;
import io.vertx.core.Future;
/*
  Generated Proxy code - DO NOT EDIT
  @author Roger the Robot
*/

@SuppressWarnings({"unchecked", "rawtypes"})
public class DBServiceVertxEBProxy implements DBService {
  private Vertx _vertx;
  private String _address;
  private DeliveryOptions _options;
  private boolean closed;

  public DBServiceVertxEBProxy(Vertx vertx, String address) {
    this(vertx, address, null);
  }

  public DBServiceVertxEBProxy(Vertx vertx, String address, DeliveryOptions options) {
    this._vertx = vertx;
    this._address = address;
    this._options = options;
    try {
      this._vertx.eventBus().registerDefaultCodec(ServiceException.class, new ServiceExceptionMessageCodec());
    } catch (IllegalStateException ex) {
    }
  }

  @Override
  public Future<Void> save(DataEntry data){
    if (closed) return io.vertx.core.Future.failedFuture("Proxy is closed");
    JsonObject _json = new JsonObject();
    _json.put("data", data != null ? data.toJson() : null);

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "save");
    _deliveryOptions.getHeaders().set("action", "save");
    return _vertx.eventBus().<Void>request(_address, _json, _deliveryOptions).map(msg -> {
      return msg.body();
    });
  }
  @Override
  public Future<List<DataEntry>> load(){
    if (closed) return io.vertx.core.Future.failedFuture("Proxy is closed");
    JsonObject _json = new JsonObject();

    DeliveryOptions _deliveryOptions = (_options != null) ? new DeliveryOptions(_options) : new DeliveryOptions();
    _deliveryOptions.addHeader("action", "load");
    _deliveryOptions.getHeaders().set("action", "load");
    return _vertx.eventBus().<JsonArray>request(_address, _json, _deliveryOptions).map(msg -> {
      return msg.body().stream()
        .map(v -> v != null ? new io.vertx.examples.serviceproxy.DataEntry((JsonObject)v) : null)
        .collect(Collectors.toList());
    });
  }
}
