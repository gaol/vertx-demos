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

import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
@ApplicationScoped
public class CSVFileArgumentor implements Supplier<RetrievalAugmentor> {

    private static final Pattern CVE_PATTERN = Pattern.compile("\\bCVE-\\d{4}-\\d{4,}\\b");

    private volatile RetrievalAugmentor augmentor;

    @ConfigProperty(name = "cve.info.file")
    private String cveInfoFile;

    @Override
    public RetrievalAugmentor get() {
        if (augmentor == null) {
            try {
                final List<String> cveMap = Files.readAllLines(Path.of(cveInfoFile));
                augmentor = DefaultRetrievalAugmentor
                        .builder()
                        .contentRetriever(query -> {
                            String text = query.text();
                            System.out.println("Query augmented content for: " + text);
                            String cve = extractCVENumber(text);
                            System.out.println("CVE to check is: " + cve);
                            return cveMap.stream()
                                    .filter(line -> line.contains(cve) || line.contains("RHSA URL"))
                                    .map(Content::new)
                                    .collect(Collectors.toList());
                        })
                        .build();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return augmentor;
    }

    public static String extractCVENumber(String text) {
        Matcher matcher = CVE_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group();
        }
        throw new IllegalArgumentException("No CVE found !");
    }
}
