package io.quarkus.demos.ai;

import org.junit.jupiter.api.Test;

class SimpleTest {
    @Test
    void testHello() {
        String line = "Display CVE information of CVE-2015-0254 in a bulletin format, fields are: CVE Identifier, Severity, Summary, If EAP Effected,\n" +
                "The affected package name, The affected package version, The github repository of the package, Commit to fix the issue\n" +
                "You may need to analyze by accessing https://cveawg.mitre.org/api/cve/CVE-2015-0254 and https://bugzilla.redhat.com/show_bug.cgi?id=CVE-2015-0254.\n" +
                "Keep the answer brief.";
        String cve = CSVFileArgumentor.extractCVENumber(line);
        System.out.println("cve: " + cve);
    }

}