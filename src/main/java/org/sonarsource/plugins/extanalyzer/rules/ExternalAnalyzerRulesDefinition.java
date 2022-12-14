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

      createDependencyCheckRule(repo);
      createDefaultRule(repo);

      for(ExternalRule rule : rules)
      {
          createRule(repo, rule.Key, rule.Name, rule.Description, rule.Help, RuleType.SECURITY_HOTSPOT);
      }
  }

  private void createRule(NewRepository repo, String ruleKey, String name, String description, String help, RuleType type) {
    LOGGER.info("ExternalAnalyzerRulesDefinition: Creating rule {}", ruleKey);
    RuleKey key = RuleKey.of(Constants.REPOSITORY_KEY, ruleKey);
    NewRule rule = repo.createRule(key.rule())
    .setName(name)
    .setSeverity(Severity.MAJOR)
    .setStatus(RuleStatus.READY)
    .setType(type)
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

  private void createDependencyCheckRule(NewRepository repo)
  {
    String key = Constants.DEP_CHECK_RULE_ID;
    String name = "Using Components with Known Vulnerabilities";
    String description = "<p>Components, such as libraries, frameworks, and other software modules, almost always run with full privileges. If a vulnerable component is exploited, such an attack can facilitate serious data loss or server takeover. Applications using components with known vulnerabilities may undermine application defenses and enable a range of possible attacks and impacts. Refer to Dependency-Check tab for more information.</p>";
    String help = "<ul><li>OWASP Top 10 2013-A9: <a href=\"https://www.owasp.org/index.php/Top_10_2013-A9-Using_Components_with_Known_Vulnerabilities\">Using Components with Known Vulnerabilities</a></li><li><a href=\"https://cwe.mitre.org/data/definitions/937.html\">Common Weakness Enumeration CWE-937</a></li><p>This issue was generated by <a href=\"https://www.owasp.org/index.php/OWASP_Dependency_Check\">Dependency-Check</a></p></ul>";

    createRule(repo, key, name, description, help, RuleType.VULNERABILITY);
  }

  private void createDefaultRule(NewRepository repo)
  {
    String key = Constants.DEFAULT_RULE_ID;
    String name = "Issue reported by unknown external analyzer";
    String description = "<p>This issue was reported by an external analyzer</p>";
    String help = "";

    createRule(repo, key, name, description, help, RuleType.SECURITY_HOTSPOT);
  }
}
