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
import org.sonarsource.plugins.extanalyzer.shared.IExternalIssueReader;
import org.sonarsource.plugins.extanalyzer.shared.SarifExternalIssueReader;

import com.google.common.collect.Iterables;

import org.sonar.api.utils.log.*;

public class ExternalAnalyzerResultsSensor implements Sensor {
  private static final Logger LOGGER = Loggers.get(ExternalAnalyzerResultsSensor.class);
  private static final double ARBITRARY_GAP = 2.0;
  private static final int LINE_1 = 1;

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("Create issues based on external analyzer results");
  }

  @Override
  public void execute(SensorContext context) {
    LOGGER.info("ExternalAnalyzerResultsSensor: Starting sensor execution");

    IExternalIssueReader externalIssueReader = new SarifExternalIssueReader("example-issues.satif");
    Iterable<org.sonarsource.plugins.extanalyzer.shared.ExternalIssue> externalIssues = externalIssueReader.getIssues();

    for (ExternalIssue issue : externalIssues) {
      importIssue(context, issue);
    }

    FileSystem fs = context.fileSystem();
    Iterable<InputFile> csprojFiles = fs.inputFiles(fs.predicates().hasExtension("cs"));
    for (InputFile csprojFile : csprojFiles) {
      RuleKey ruleKey = RuleKey.of(Constants.REPOSITORY_KEY, Constants.RULE_KEY);
      LOGGER.info("ExternalAnalyzerResultsSensor: Creating an issue {} - {}", csprojFile.filename(), ruleKey.rule());
      NewIssue newIssue = context.newIssue()
          .forRule(ruleKey)
          .gap(ARBITRARY_GAP);

      NewIssueLocation primaryLocation = newIssue.newLocation()
          .on(csprojFile)
          .at(csprojFile.selectLine(LINE_1))
          .message("You can't do anything. This is first line!");
      newIssue.at(primaryLocation);
      newIssue.save();

      LOGGER.info("ExternalAnalyzerResultsSensor: Sucessfully created the issue");
    }
    LOGGER.info("ExternalAnalyzerResultsSensor: Completed sensor execution");
  }

  private void importIssue(SensorContext context, ExternalIssue issue) {
    FileSystem fs = context.fileSystem();
    Iterable<InputFile> matchedFiles = fs.inputFiles(fs.predicates().hasRelativePath(issue.FilePath));
    if (Iterables.size(matchedFiles) > 0) {
      InputFile inputFile = matchedFiles.iterator().next();
      RuleKey ruleKey = RuleKey.of(Constants.REPOSITORY_KEY, issue.RuleId);
      LOGGER.info("ExternalAnalyzerResultsSensor: Creating an issue {} - {}", inputFile.filename(), ruleKey.rule());
      NewIssue newIssue = context.newIssue()
          .forRule(ruleKey)
          .gap(ARBITRARY_GAP);

      NewIssueLocation primaryLocation = newIssue.newLocation()
          .on(inputFile)
          .at(inputFile.selectLine(issue.StartLine))
          .message(issue.Message);
      newIssue.at(primaryLocation);
      newIssue.save();
    } else {
      LOGGER.warn("ExternalAnalyzerResultsSensor: Unable to find file: {} in the input source files", issue.FilePath);
    }
  }
}
