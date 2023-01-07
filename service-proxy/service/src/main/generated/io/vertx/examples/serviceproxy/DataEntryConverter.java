package io.vertx.examples.serviceproxy;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Converter and mapper for {@link io.vertx.examples.serviceproxy.DataEntry}.
 * NOTE: This class has been automatically generated from the {@link io.vertx.examples.serviceproxy.DataEntry} original class using Vert.x codegen.
 */
public class DataEntryConverter {


  private static final Base64.Decoder BASE64_DECODER = JsonUtil.BASE64_DECODER;
  private static final Base64.Encoder BASE64_ENCODER = JsonUtil.BASE64_ENCODER;

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, DataEntry obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "id":
          if (member.getValue() instanceof Number) {
            obj.setId(((Number)member.getValue()).longValue());
          }
          break;
        case "message":
          if (member.getValue() instanceof String) {
            obj.setMessage((String)member.getValue());
          }
          break;
        case "name":
          if (member.getValue() instanceof String) {
            obj.setName((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(DataEntry obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(DataEntry obj, java.util.Map<String, Object> json) {
    json.put("id", obj.getId());
    if (obj.getMessage() != null) {
      json.put("message", obj.getMessage());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
  }
}
