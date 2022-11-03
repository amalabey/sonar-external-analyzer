package org.sonarsource.plugins.extanalyzer;

import org.sonar.api.Plugin;
import org.sonarsource.plugins.extanalyzer.rules.ExternalAnalyzerResultsSensor;
import org.sonarsource.plugins.extanalyzer.rules.ExternalAnalyzerRulesDefinition;

public class ExternalAnalyzerPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context.addExtensions(ExternalAnalyzerRulesDefinition.class, ExternalAnalyzerResultsSensor.class);
  }
}