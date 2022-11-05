package org.sonarsource.plugins.extanalyzer.shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import com.contrastsecurity.sarif.Result;
import com.contrastsecurity.sarif.Run;
import com.contrastsecurity.sarif.SarifSchema210;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SarifExternalIssueReader implements IExternalIssueReader {
    private static final Logger LOGGER = Loggers.get(SarifExternalIssueReader.class);
    private final String sarifFilePath;

    public SarifExternalIssueReader(String sarifFilePath) {
        this.sarifFilePath = sarifFilePath;
    }

    @Override
    public Iterable<ExternalIssue> getIssues() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ArrayList<ExternalIssue> issues = new ArrayList<ExternalIssue>();
            ClassLoader classLoader = this.getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(sarifFilePath);
            SarifSchema210  sarif = objectMapper.readValue(inputStream, SarifSchema210.class);
            for(Run run : sarif.getRuns()) {
                for(Result result : run.getResults()) {
                    var ruleId = result.getRuleId();
                    var message = result.getMessage().getText();
                    var filePath = result.getLocations()
                        .get(0)
                        .getPhysicalLocation()
                        .getArtifactLocation()
                        .getUri();
                    var startLine = result.getLocations()
                        .get(0)
                        .getPhysicalLocation()
                        .getRegion()
                        .getStartLine();
                    var startColumn = result.getLocations()
                        .get(0)
                        .getPhysicalLocation()
                        .getRegion()
                        .getStartColumn();
                    issues.add(new ExternalIssue(ruleId, message, filePath, startLine, startColumn));
                }
            }
            return issues;
        } catch (IOException e) {
            LOGGER.error("ERROR: {}", e);
            e.printStackTrace();
        }
        return null;
    }
}
