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
package io.vertx.examples.openshift;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class FakeData {

    private final static HourcePrice NULL = new HourcePrice("<null>", "<Not Found>", 0d);

    private static class HourcePrice {
        private final String id;
        private final String name;
        private final double price;
        HourcePrice(String id, String name, double price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }

        public JsonObject json() {
            return new JsonObject().put("name", name).put("price", price);
        }

        public JsonObject nameJson() {
            return new JsonObject().put("id", id).put("name", name);
        }
    }

    private final static HashMap<String, HourcePrice> prices = new HashMap<>(6);
    static {
        // used house price on 2022-11-09 from https://fangjia.gotohui.com/fjdata-1
        prices.put("bj", new HourcePrice("bj", "Beijing", 65901d));
        prices.put("sh", new HourcePrice("sh", "Shanghai", 68909d));
        prices.put("tj", new HourcePrice("tj", "Tianjin", 23834d));
        prices.put("cq", new HourcePrice("cq", "Chongqing", 13987d));
        prices.put("sz", new HourcePrice("sz", "Shenzhen", 65893d));
        prices.put("qd", new HourcePrice("qd", "Qingdao", 22419d));
        prices.put("cd", new HourcePrice("cd", "Chengdu", 17060d));
        prices.put("xa", new HourcePrice("xa", "Xian", 17294d));
    }

    public JsonObject getPrice(String name) {
        return prices.getOrDefault(name, NULL).json();
    }

    public JsonArray cities() {
        JsonArray ja = new JsonArray();
        prices.values().forEach(hp -> ja.add(hp.nameJson()));
        return ja;
    }

}
