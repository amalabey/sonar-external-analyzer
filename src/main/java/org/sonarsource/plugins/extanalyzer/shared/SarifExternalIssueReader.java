package org.sonarsource.plugins.extanalyzer.shared;

import java.io.File;
import java.io.IOException;
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
            ArrayList<ExternalIssue> sarifResults = new ArrayList<ExternalIssue>();
            var sarifFile = new File(sarifFilePath);
            var baseDirectoryPath = sarifFile.getAbsoluteFile().getParent();
            SarifSchema210  sarif = objectMapper.readValue(sarifFile, SarifSchema210.class);
            for(Run run : sarif.getRuns()) {
                for(Result result : run.getResults()) {
                    var ruleId = result.getRuleId();
                    var message = result.getMessage().getText();
                    var filePath = result.getLocations()
                        .get(0)
                        .getPhysicalLocation()
                        .getArtifactLocation()
                        .getUri();
                    var region = result.getLocations()
                        .get(0)
                        .getPhysicalLocation()
                        .getRegion();
                    var startLine =  region != null ? region.getStartLine() : null;
                    var startColumn = region != null ? region.getStartColumn() : null;
                    var absoluteFilePath = filePath.startsWith("/") ? filePath : new File(baseDirectoryPath, filePath).getPath();
                    LOGGER.info("SarifExternalIssueReader: ruleId={}, filePath={}, absoluteFilePath={}, message={}",ruleId, filePath, absoluteFilePath, message);
                    sarifResults.add(new ExternalIssue(ruleId, message, filePath, absoluteFilePath, startLine != null?startLine.intValue():1, startColumn != null?startColumn.intValue():1));
                }
            }
            return sarifResults;
        } catch (IOException e) {
            LOGGER.error("ERROR: {}", e);
            e.printStackTrace();
        }
        return null;
    }
}
