package org.sonarsource.plugins.extanalyzer.rules;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonarsource.plugins.extanalyzer.Constants;
import org.sonarsource.plugins.extanalyzer.shared.ExternalRule;
import org.sonarsource.plugins.extanalyzer.shared.IExternalRulesStore;
import org.sonarsource.plugins.extanalyzer.shared.XmlExternalRulesStore;

public class GeneralProfile implements BuiltInQualityProfilesDefinition {
    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile dependencyCheckWay = context.createBuiltInQualityProfile("General", Constants.LANGUAGE_KEY);
        IExternalRulesStore store = new XmlExternalRulesStore();
        Iterable<ExternalRule> rules = store.getRules();
        dependencyCheckWay.activateRule(Constants.REPOSITORY_KEY, Constants.DEP_CHECK_RULE_ID);
        dependencyCheckWay.activateRule(Constants.REPOSITORY_KEY, Constants.DEFAULT_RULE_ID);
        for(ExternalRule rule : rules) {
            dependencyCheckWay.activateRule(Constants.REPOSITORY_KEY, rule.Key);
        }
        dependencyCheckWay.done();
    }
}