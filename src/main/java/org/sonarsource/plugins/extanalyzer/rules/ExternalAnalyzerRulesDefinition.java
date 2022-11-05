package org.sonarsource.plugins.extanalyzer.rules;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarsource.plugins.extanalyzer.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ExternalAnalyzerRulesDefinition implements RulesDefinition {
  private static final Logger LOGGER = Loggers.get(ExternalAnalyzerRulesDefinition.class);
  private static final String RULES_FILE_NAME = "sonar-rules.xml";

  @Override
  public void define(Context context) {
    LOGGER.info("ExternalAnalyzerRulesDefinition: Start defining the rule set");
    NewRepository repository = context.createRepository(Constants.REPOSITORY_KEY, Constants.LANGUAGE_KEY).setName("External Analyzer Rules");
    try {
      importRules(RULES_FILE_NAME, repository);
    } catch (ParserConfigurationException | SAXException | IOException | URISyntaxException e) {
      LOGGER.error("ERROR: {}", e);
      e.printStackTrace();
    }
    repository.done();
    LOGGER.info("ExternalAnalyzerRulesDefinition: Completed defininig the ruleset");
  }

  private void importRules(String fileName, NewRepository repo) throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
      ClassLoader classLoader = this.getClass().getClassLoader();
      InputStream inputStream = classLoader.getResourceAsStream(fileName);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(inputStream);

      NodeList rules = doc.getElementsByTagName("rule");
      for(int i=0; i<rules.getLength(); i++)
      {
          Node rule = rules.item(i);
          if(rule.getNodeType() == Node.ELEMENT_NODE)
          {
              Element ruleElement = (Element) rule;
              String ruleKey = ruleElement.getElementsByTagName("key").item(0).getTextContent();
              String ruleName = ruleElement.getElementsByTagName("name").item(0).getTextContent();
              String desc = ruleElement.getElementsByTagName("description").item(0).getTextContent();

              createRule(repo, ruleKey, ruleName, desc);
          }
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
