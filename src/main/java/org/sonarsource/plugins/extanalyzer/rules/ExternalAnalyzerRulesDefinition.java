package org.sonarsource.plugins.extanalyzer.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.extanalyzer.Constants;
import org.sonarsource.plugins.extanalyzer.shared.ExternalRule;
import org.sonarsource.plugins.extanalyzer.shared.IExternalRulesStore;
import org.sonarsource.plugins.extanalyzer.shared.XmlExternalRulesStore;

public class ExternalAnalyzerRulesDefinition implements RulesDefinition {
  private static final Logger LOGGER = Loggers.get(ExternalAnalyzerRulesDefinition.class);

  @Override
  public void define(Context context) {
    LOGGER.info("ExternalAnalyzerRulesDefinition: Start defining the rule set");
    NewRepository repository = context.createRepository(Constants.REPOSITORY_KEY, Constants.LANGUAGE_KEY).setName("External Analyzer Rules");
    importRules(repository);
    repository.done();
    LOGGER.info("ExternalAnalyzerRulesDefinition: Completed defininig the ruleset");
  }

  private void importRules(NewRepository repo) {
      IExternalRulesStore store = new XmlExternalRulesStore();
      Iterable<ExternalRule> rules = store.getRules();

      for(ExternalRule rule : rules)
      {
          createRule(repo, rule.Key, rule.Name, rule.Description);
      }
  }

  private void createRule(NewRepository repo, String ruleKey, String name, String description) {
    LOGGER.info("ExternalAnalyzerRulesDefinition: Creating rule {}", ruleKey);
    RuleKey key = RuleKey.of(Constants.REPOSITORY_KEY, ruleKey);
    NewRule rule = repo.createRule(key.rule())
    .setName(name)
    .setSeverity(Severity.MAJOR)
    .setStatus(RuleStatus.READY)
    .setType(RuleType.VULNERABILITY)
    .setHtmlDescription(description);
    rule.setDebtRemediationFunction(rule.debtRemediationFunctions().linearWithOffset("1h", "30min"));
  }
}
