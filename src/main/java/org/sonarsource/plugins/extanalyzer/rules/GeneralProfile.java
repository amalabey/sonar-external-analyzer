package org.sonarsource.plugins.extanalyzer.rules;

import org.sonar.api.server.profile.BuiltInQualityProfilesDefinition;
import org.sonarsource.plugins.extanalyzer.Constants;

public class GeneralProfile implements BuiltInQualityProfilesDefinition {

    @Override
    public void define(Context context) {
        NewBuiltInQualityProfile dependencyCheckWay = context.createBuiltInQualityProfile("General", Constants.LANGUAGE_KEY);
        dependencyCheckWay.activateRule(Constants.REPOSITORY_KEY, Constants.RULE_KEY);
        dependencyCheckWay.done();
    }
}