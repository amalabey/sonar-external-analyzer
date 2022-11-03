package org.sonarsource.plugins.extanalyzer.rules;

import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RuleDescriptionSection;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonarsource.plugins.extanalyzer.Constants;

import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.HOW_TO_FIX_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.INTRODUCTION_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.ROOT_CAUSE_SECTION_KEY;

public class ExternalAnalyzerRulesDefinition implements RulesDefinition {

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(Constants.REPOSITORY_KEY, Constants.LANGUAGE_KEY).setName("External Analyzer Rules");
    createRule(repository);
    repository.done();
  }

  private NewRule createRule(NewRepository repo) {
    NewRule rule = repo.createRule(Constants.RULE_KEY)
    .setName("The dynamic value passed to the command execution should be validated.")
    .addTags("cwe-78","owasp-a1")
    .setSeverity(Severity.MAJOR)
    .setStatus(RuleStatus.READY)
    .addCwe(78)
    .setHtmlDescription("The dynamic value passed to the command execution should be validated.")
    .addDescriptionSection(descriptionSection(INTRODUCTION_SECTION_KEY, "If a malicious user controls either the FileName or Arguments, he might be able to execute unwanted commands.."))
    .addDescriptionSection(descriptionSection(ROOT_CAUSE_SECTION_KEY, "The root cause of this issue is this and that."))
    .addDescriptionSection(descriptionSection(HOW_TO_FIX_SECTION_KEY,
        "To fix an issue reported by this rule when using Hibernate do this and that."));
    return rule;
  }

  private static RuleDescriptionSection descriptionSection(String sectionKey, String htmlContent) {
    return RuleDescriptionSection.builder()
      .sectionKey(sectionKey)
      .htmlContent(htmlContent)
      .build();
  }
}
