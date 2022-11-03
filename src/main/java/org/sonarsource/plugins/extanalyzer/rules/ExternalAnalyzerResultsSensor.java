package org.sonarsource.plugins.extanalyzer.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonarsource.plugins.extanalyzer.Constants;
import org.sonar.api.utils.log.*;

public class ExternalAnalyzerResultsSensor implements Sensor {
  private static final Logger LOGGER = Loggers.get(ExternalAnalyzerResultsSensor.class);
  private static final double ARBITRARY_GAP = 2.0;
  private static final int LINE_1 = 1;

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("Add issues on line 1 of all Java files");

    // optimisation to disable execution of sensor if project does
    // not contain Java files or if the example rule is not activated
    // in the Quality profile
    descriptor.onlyOnLanguage("java");
    descriptor.createIssuesForRuleRepositories(Constants.REPOSITORY_KEY);
  }

  @Override
  public void execute(SensorContext context) {
    FileSystem fs = context.fileSystem();
    Iterable<InputFile> javaFiles = fs.inputFiles(fs.predicates().hasLanguage("csharp"));
    for (InputFile javaFile : javaFiles) {
      LOGGER.info("ExternalAnalyzerResultsSensor: Creating an issue");
      RuleKey ruleKey = RuleKey.of(Constants.REPOSITORY_KEY, Constants.RULE_KEY);
      NewIssue newIssue = context.newIssue()
        .forRule(ruleKey)
        .gap(ARBITRARY_GAP);

      NewIssueLocation primaryLocation = newIssue.newLocation()
        .on(javaFile)
        .at(javaFile.selectLine(LINE_1))
        .message("You can't do anything. This is first line!");
      newIssue.at(primaryLocation);
      newIssue.save();
      LOGGER.info("ExternalAnalyzerResultsSensor: Sucessfully created the issue");
    }
  }
}
