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

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.inject.Singleton;

/**
 * Each RegisterAiService represents a LLM provider and model-id.
 * This represents a AiService with default model name.
 * The provider is selected by the key <code>quarkus.langchain4j.chat-model.provider</code> in application.properties.
 */
@RegisterAiService(chatMemoryProviderSupplier = ChatSession.MemorySupplier.class)
@Singleton
public interface Chatter {

    @UserMessage("""
            Answer the question {question}
            in one sentence
            """)
    String ask(String question);

    /**
     * Using a memoryId explicitly to support conversation context.
     */
    String chat(@MemoryId String memoryId, @UserMessage String question);

    /**
     * Analyze the summary of a product, returns a Product Java POJO represents.
     */
    @UserMessage(fromResource = "product_summary.txt")
    Product analyze(String summary);


    @SystemMessage("""
            You are a security administrator of JBoss Enterprise Application Server (EAP or JBEAP).
            Search the internet about the CVE: {cve}, Check if it affects EAP and which version of EAP it affects.
            Check the affected package and in which commit it gets fixed.
            """)
    @UserMessage("""
            Display CVE information of {cve} in a bulletin format, fields are: CVE Identifier, Severity, Summary, If EAP Effected,
            The affected package name, The affected package version, The github repository of the package, Commit to fix the issue.
            """)
    String cveInfo(String cve);

}
