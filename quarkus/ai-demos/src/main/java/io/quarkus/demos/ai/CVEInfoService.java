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

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkiverse.langchain4j.redis.RedisEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.concurrent.atomic.AtomicBoolean;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
@ApplicationScoped
public class CVEInfoService {

    @ConfigProperty(name = "cve.info.file", defaultValue = "/home/lgao/workspace/eap7.1/eap7_3_cols_jbeaps.csv")
    private String cveInfoFile;

    @Inject
    RedisEmbeddingStore store;

    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    Chatter chatter;

    String cveInfo(String cve) {
        return chatter.cveInfo(cve);
    }

    String cveInfoPro(String cve) {
        return chatter.cveInfo(cve);
    }


    public synchronized void prepare() {
        Document cveDoc = FileSystemDocumentLoader.loadDocument(cveInfoFile, new TextDocumentParser());
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(500, 0))
                .build();
        // Warning - this can take a long time...
        ingestor.ingest(cveDoc);
    }
}
