package org.sonarsource.plugins.extanalyzer.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonarsource.plugins.extanalyzer.shared.ExternalIssue;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonarsource.plugins.extanalyzer.Constants;
import org.sonarsource.plugins.extanalyzer.settings.ExternalAnalyzerProperties;
import org.sonarsource.plugins.extanalyzer.shared.IExternalIssueReader;
import org.sonarsource.plugins.extanalyzer.shared.SarifExternalIssueReader;

import com.google.common.collect.Iterables;

import org.sonar.api.utils.log.*;

public class ExternalAnalyzerResultsSensor implements Sensor {
  private static final Logger LOGGER = Loggers.get(ExternalAnalyzerResultsSensor.class);
  private static final double ARBITRARY_GAP = 2.0;

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("Create issues based on external analyzer results");
  }

  @Override
  public void execute(SensorContext context) {
    LOGGER.info("ExternalAnalyzerResultsSensor: Starting sensor execution");
    String[] sarifFilePaths = context.config().getStringArray(ExternalAnalyzerProperties.SARIF_FILE_KEY);
    if (sarifFilePaths == null || sarifFilePaths.length == 0) {
      LOGGER.info("ExternalAnalyzerResultsSensor: sonar.sarif.path not provided. Ignoring external issues.");
      return;
    }

    for (String sarifFilePath : sarifFilePaths) {
      LOGGER.info("ExternalAnalyzerResultsSensor: Importing external issues from: {}", sarifFilePath);
      IExternalIssueReader externalIssueReader = new SarifExternalIssueReader(sarifFilePath);
      Iterable<org.sonarsource.plugins.extanalyzer.shared.ExternalIssue> externalIssues = externalIssueReader
          .getIssues();
      if (externalIssues != null) {
        for (ExternalIssue issue : externalIssues) {
          importIssue(context, issue);
        }
      }
    }

    LOGGER.info("ExternalAnalyzerResultsSensor: Completed sensor execution");
  }

  private void importIssue(SensorContext context, ExternalIssue issue) {
    try {
      LOGGER.info("ExternalAnalyzerResultsSensor: Importing issue: file={}, issueRuleId={}", issue.FilePath,
          issue.RuleId);
      var ruleFinder = new RuleFinder();
      String matchedRuleKey = ruleFinder.getMatchingRuleId(issue.RuleId);
      LOGGER.info("ExternalAnalyzerResultsSensor: Importing issue: file={}, issueRuleId={}, matchedKey={}",
          issue.FilePath, issue.RuleId, matchedRuleKey);
      FileSystem fs = context.fileSystem();
      Iterable<InputFile> matchedFiles = fs.inputFiles(fs.predicates().hasAbsolutePath(issue.AbsoluteFilePath));
      if (Iterables.size(matchedFiles) > 0) {
        InputFile inputFile = matchedFiles.iterator().next();
        RuleKey ruleKey = RuleKey.of(Constants.REPOSITORY_KEY, matchedRuleKey);
        LOGGER.info("ExternalAnalyzerResultsSensor: Creating an issue {} - {}", inputFile.filename(), ruleKey.rule());
        NewIssue newIssue = context.newIssue()
            .forRule(ruleKey)
            .gap(ARBITRARY_GAP);

        NewIssueLocation primaryLocation = newIssue.newLocation()
            .on(inputFile)
            .at(inputFile.selectLine(issue.StartLine));
        if (issue.Message != null)
          primaryLocation.message(issue.Message);
        newIssue.at(primaryLocation);
        newIssue.save();
      } else {
        LOGGER.warn("ExternalAnalyzerResultsSensor: Unable to find file: {} in the input source files", issue.FilePath);
      }
    } catch (Exception exception) {
      LOGGER.error("error occurred while creating the issue: {}",exception.getMessage());
      LOGGER.error(exception.getStackTrace().toString());
    }
  }
}
