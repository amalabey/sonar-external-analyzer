package org.sonarsource.plugins.extanalyzer.shared;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlExternalRulesStore implements IExternalRulesStore {
    private static final String RULES_FILE_NAME = "sonar-rules.xml";
    private static final Logger LOGGER = Loggers.get(XmlExternalRulesStore.class);

    @Override
    public Iterable<ExternalRule> getRules() {
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(RULES_FILE_NAME);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);

            ArrayList<ExternalRule> rulesList = new ArrayList<ExternalRule>();
            NodeList rules = doc.getElementsByTagName("rule");
            for (int i = 0; i < rules.getLength(); i++) {
                Node rule = rules.item(i);
                if (rule.getNodeType() == Node.ELEMENT_NODE) {
                    Element ruleElement = (Element) rule;
                    String ruleKey = ruleElement.getElementsByTagName("key").item(0).getTextContent();
                    String ruleName = ruleElement.getElementsByTagName("name").item(0).getTextContent();
                    String desc = ruleElement.getElementsByTagName("description").item(0).getTextContent();

                    rulesList.add(new ExternalRule(ruleKey, ruleName, desc));
                }
            }

            return rulesList;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOGGER.error("ERROR: {}", e);
            e.printStackTrace();
        }
        return null;
    }

}
