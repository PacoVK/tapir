package extensions.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import extensions.docs.report.TerraformDocumentation;
import extensions.security.report.SecurityFinding;
import org.junit.jupiter.api.Test;
import util.TestDataBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReportTest {
    @Test
    void canParse() throws Exception {
        final String json;
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(TestDataBuilder.class
                .getResourceAsStream("/securityReport.json")))) {
            json = new BufferedReader(reader).lines().collect(Collectors.joining());

        }catch (IOException e) {
            e.printStackTrace();
            return;
        }

        var mapper = new ObjectMapper();
        var report = mapper.readValue(json, Report.class);

        // Assertions to verify the parsed object
        assertNotNull(report);
        assertEquals("azure", report.getModuleNamespace());
        assertEquals("foo", report.getModuleName());
        assertEquals("1.0.0", report.getModuleVersion());
        assertEquals("azurerm", report.getProvider());

        var securityReport = report.getSecurityReport();
        assertNotNull(securityReport);
        assertEquals(1, securityReport.get("main.tf").size());
        SecurityFinding finding = securityReport.get("main.tf").get(0);
        assertEquals("AVD-AZU-0014", finding.getId());

        TerraformDocumentation documentation = report.getDocumentation();
        assertNotNull(documentation);
        assertEquals(1, documentation.getProviders().size());
        assertEquals("azurerm", documentation.getProviders().get(0).getName());
        assertEquals(1, documentation.getResources().size());
        assertEquals("mykey", documentation.getResources().get(0).getName());
    }
}
