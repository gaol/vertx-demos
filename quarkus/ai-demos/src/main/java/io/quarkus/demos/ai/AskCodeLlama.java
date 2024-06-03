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

import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.inject.Singleton;

/**
 * This represents to talk with a codellama LLM provided by a Ollama service.
 * The configurations start with 'quarkus.langchain4j.ollama.codellama'
 * The provide configuration is: 'quarkus.langchain4j.codellama.chat-model.provider=ollama'
 */
@RegisterAiService(modelName = "codellama")
@Singleton
public interface AskCodeLlama {

    String codeAssistant(String code);
}
