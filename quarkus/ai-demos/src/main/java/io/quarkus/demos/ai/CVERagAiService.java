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

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.inject.Singleton;

/**
 * @author <a href="mailto:aoingl@gmail.com">Lin Gao</a>
 */
@RegisterAiService(retrievalAugmentor = CSVFileArgumentor.class)
@Singleton
public interface CVERagAiService {
    @SystemMessage("""
            You are a security administrator of JBoss Enterprise Application Server (EAP or JBEAP).
            Search the internet about the CVE, according to current context, check if it affects EAP and which version of EAP it affects.
            Check the affected package and in which commit it gets fixed.
            If trackers or RHSA URLs are provided, you can access them for more information.
            """)
    @UserMessage("""
            Display CVE information of {cve} in a bulletin format, fields are: CVE Identifier, Severity, Summary, If EAP Effected,
            The affected package name, The affected package version, The github repository of the package, Commit to fix the issue
            You need to analyze by accessing https://access.redhat.com/api/v2/security/cve/{cve}
            """)
    String cveInfoPro(String cve);
}
