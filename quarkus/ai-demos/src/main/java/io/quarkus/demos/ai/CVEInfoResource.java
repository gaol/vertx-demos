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

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * The REST endpoint which can utilize the a prepared embedding store for RAG.
 * <p/>
 * The backend RAG has been prepared, it is open to use or not.
 */
@Path("/cve")
public class CVEInfoResource {

    @Inject
    private CVEInfoService cveInfoService;

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/prepare")
    public String prepare() {
        System.out.println("\nStart to prepare the embedding store to redis\n");
        cveInfoService.prepare();
        System.out.println("\nredis store prepared !\n");
        return "Done";
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/info")
    public String cveInfo(String cve) {
        System.out.println("\nUser is asking information about CVE: " + cve + "\n");
        return cveInfoService.cveInfo(cve);
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/infoPro")
    public String cveInfoPro(String cve) {
        System.out.println("\nUser is asking information about CVE(Pro): " + cve + "\n");
        return cveInfoService.cveInfoPro(cve);
    }
}
