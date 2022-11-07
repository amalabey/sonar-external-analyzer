package org.sonarsource.plugins.extanalyzer.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RuleDescriptionSection;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.extanalyzer.Constants;
import org.sonarsource.plugins.extanalyzer.shared.ExternalRule;
import org.sonarsource.plugins.extanalyzer.shared.IExternalRulesStore;
import org.sonarsource.plugins.extanalyzer.shared.XmlExternalRulesStore;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.HOW_TO_FIX_SECTION_KEY;

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
          createRule(repo, rule.Key, rule.Name, rule.Description, rule.Help);
      }
  }

  private void createRule(NewRepository repo, String ruleKey, String name, String description, String help) {
    LOGGER.info("ExternalAnalyzerRulesDefinition: Creating rule {}", ruleKey);
    RuleKey key = RuleKey.of(Constants.REPOSITORY_KEY, ruleKey);
    NewRule rule = repo.createRule(key.rule())
    .setName(name)
    .setSeverity(Severity.MAJOR)
    .setStatus(RuleStatus.READY)
    .setType(RuleType.SECURITY_HOTSPOT)
    .setHtmlDescription(description)
    .addDescriptionSection(descriptionSection(HOW_TO_FIX_SECTION_KEY, String.format("%s <hr><br> %s", description, help)));
    rule.setDebtRemediationFunction(rule.debtRemediationFunctions().linearWithOffset("1h", "30min"));
  }

  private static RuleDescriptionSection descriptionSection(String sectionKey, String htmlContent) {
    return RuleDescriptionSection.builder()
      .sectionKey(sectionKey)
      .htmlContent(htmlContent)
      .build();
  }
}
