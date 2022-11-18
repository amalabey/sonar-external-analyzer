from checkov import CheckovRulesProvider
from horusec import HorusecRulesProvider
from sonarqube import SonarQubeRulesXmlWriter
from bpa import TemplateAnalyzerRulesProvider

if __name__ == "__main__":
    print("Generating rules from external tools documentation")
    horusec = HorusecRulesProvider()
    checkov = CheckovRulesProvider()
    template_analyzer = TemplateAnalyzerRulesProvider()
    hsc_rules = horusec.get_rules()
    ckv_rules = checkov.get_rules()
    template_analyzer_rules = template_analyzer.get_rules()
    rules = template_analyzer_rules + hsc_rules + ckv_rules

    writer = SonarQubeRulesXmlWriter()
    writer.write_rules(rules, "../../src/main/resources/sonar-rules.xml")
    print("Done.")