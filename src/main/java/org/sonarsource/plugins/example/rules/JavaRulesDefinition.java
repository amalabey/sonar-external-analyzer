package org.sonarsource.plugins.example.rules;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RuleDescriptionSection;
import org.sonar.api.server.rule.RulesDefinition;

import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.HOW_TO_FIX_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.INTRODUCTION_SECTION_KEY;
import static org.sonar.api.server.rule.RuleDescriptionSection.RuleDescriptionSectionKeys.ROOT_CAUSE_SECTION_KEY;

public class JavaRulesDefinition implements RulesDefinition {

  public static final String REPOSITORY = "java-example";
  public static final String JAVA_LANGUAGE = "java";
  public static final RuleKey RULE_ON_LINE_1 = RuleKey.of(REPOSITORY, "line1");

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(REPOSITORY, JAVA_LANGUAGE).setName("My Custom Java Analyzer");

    var hibernate = new org.sonar.api.server.rule.Context("hibernate", "Hibernate");
    var myBatis = new org.sonar.api.server.rule.Context("mybatis", "MyBatis");

    NewRule x1Rule = repository.createRule(RULE_ON_LINE_1.rule())
      .setName("Stupid rule")
      .setHtmlDescription("Generates an issue on every line 1 of Java files")
      .addDescriptionSection(descriptionSection(INTRODUCTION_SECTION_KEY, "This rule is not that stupid", null))
      .addDescriptionSection(descriptionSection(ROOT_CAUSE_SECTION_KEY, "The root cause of this issue is this and that.", null))
      .addDescriptionSection(descriptionSection(HOW_TO_FIX_SECTION_KEY,
        "To fix an issue reported by this rule when using Hibernate do this and that.", hibernate))
      .addDescriptionSection(descriptionSection(HOW_TO_FIX_SECTION_KEY,
        "To fix an issue reported by this rule when using MyBatis do this and that.", myBatis))
      // optional tags
      .setTags("style", "stupid")

      // optional status. Default value is READY.
      .setStatus(RuleStatus.BETA)

      // default severity when the rule is activated on a Quality profile. Default value is MAJOR.
      .setSeverity(Severity.MINOR);

    x1Rule.setDebtRemediationFunction(x1Rule.debtRemediationFunctions().linearWithOffset("1h", "30min"));

    // don't forget to call done() to finalize the definition
    repository.done();
  }

  private static RuleDescriptionSection descriptionSection(String sectionKey, String htmlContent, org.sonar.api.server.rule.Context context) {
    return RuleDescriptionSection.builder()
      .sectionKey(sectionKey)
      .htmlContent(htmlContent)
      //Optional context - can be any framework or component for which you want to create detailed description
      .context(context)
      .build();
  }
}
