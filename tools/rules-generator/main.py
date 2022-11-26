from checkov import CheckovRulesProvider
from horusec import HorusecRulesProvider
from sonarqube import SonarQubeRulesXmlWriter
from sonarqube import SonarQubeRulesXmlReader
from bpa import TemplateAnalyzerRulesProvider
from psrules import PSRuleRulesProvider
from gitleaks import GitLeaksRulesProvider

if __name__ == "__main__":
    print("Generating rules from external tools documentation")
    horusec = HorusecRulesProvider()
    checkov = CheckovRulesProvider()
    ta = TemplateAnalyzerRulesProvider()
    psrule = PSRuleRulesProvider()
    gitleaks = GitLeaksRulesProvider()
    rules = list()
    rules += horusec.get_rules()
    rules += checkov.get_rules()
    rules += ta.get_rules()
    rules += psrule.get_rules()
    rules += gitleaks.get_rules()

    rules_xml_path = "../../src/main/resources/sonar-rules.xml"
    writer = SonarQubeRulesXmlWriter()
    writer.write_rules(rules, rules_xml_path)

    reader = SonarQubeRulesXmlReader()
    rule_ids = reader.get_rule_ids(rules_xml_path)
    writer.write_rule_ids(rule_ids, "../../src/main/java/org/sonarsource/plugins/extanalyzer/rules/Rules.java")

    print("Done.")