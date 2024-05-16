/*
 *  Copyright (c) 2024 The original author or authors
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
package io.quarkus.demos.ai;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.lang.reflect.Type;
import java.util.function.BiFunction;


/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
public class OpenAIImageSerialization implements BiFunction<ObjectMapper, Type, ObjectWriter> {

    @Override
    public ObjectWriter apply(ObjectMapper objectMapper, Type type) {
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return objectMapper.writer();
    }
}
