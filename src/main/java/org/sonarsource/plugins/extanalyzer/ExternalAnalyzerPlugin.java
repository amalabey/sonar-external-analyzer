package org.sonarsource.plugins.extanalyzer;

import org.sonar.api.Plugin;
import org.sonarsource.plugins.extanalyzer.rules.ExternalAnalyzerResultsSensor;
import org.sonarsource.plugins.extanalyzer.rules.ExternalAnalyzerRulesDefinition;
import org.sonarsource.plugins.extanalyzer.rules.GeneralLanguage;
import org.sonarsource.plugins.extanalyzer.rules.GeneralProfile;

public class ExternalAnalyzerPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtensions(ExternalAnalyzerRulesDefinition.class, 
      ExternalAnalyzerResultsSensor.class,
      GeneralLanguage.class,
      GeneralProfile.class);
  }
}
