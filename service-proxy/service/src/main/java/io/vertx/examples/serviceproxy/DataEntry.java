/*
 *  Copyright (c) 2022 The original author or authors
 *
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of Apache License v2.0 which
 *  accompanies this distribution.
 *
 *       The Apache License v2.0 is available at
 *       http://www.opensource.org/licenses/apache2.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.vertx.examples.serviceproxy;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
@DataObject(generateConverter = true)
public class DataEntry {
    private long id;
    private String name;
    private String message;

    public DataEntry() {
    }

    public DataEntry(JsonObject json) {
        DataEntryConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        DataEntryConverter.toJson(this, json);
        return json;
    }

    public long getId() {
        return id == 0 ? System.currentTimeMillis() : id;
    }

    public DataEntry setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name == null ? "<default-name>" : name;
    }

    public DataEntry setName(String name) {
        this.name = name;
        return this;
    }

    public String getMessage() {
        return message == null ? "<default-message>" : message;
    }

    public DataEntry setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "DataEntry[id=" + id + ", name=" + name + ", message=" + message + "]\n";
    }
}
