package org.sonarsource.plugins.extanalyzer.settings;

import java.util.List;

import org.sonar.api.config.PropertyDefinition;
import static java.util.Arrays.asList;

public class ExternalAnalyzerProperties {
    public static final String SARIF_FILE_KEY = "sonar.sarif.path";
    public static final String CATEGORY = "External Analyzer";

    private ExternalAnalyzerProperties() {
        // only statics
    }

    public static List<PropertyDefinition> getProperties() {
        return asList(
        PropertyDefinition.builder(SARIF_FILE_KEY)
            .name("SARIF File Path")
            .description("Path to the external analyzer results (SARIF) file")
            .multiValues(true)
            .defaultValue("")
            .category(CATEGORY)
            .build());
    }
}
