from checkov import CheckovRulesProvider
from horusec import HorusecRulesProvider
from sonarqube import SonarQubeRulesXmlWriter
from bpa import TemplateAnalyzerRulesProvider
from psrules import PSRuleRulesProvider

if __name__ == "__main__":
    print("Generating rules from external tools documentation")
    horusec = HorusecRulesProvider()
    checkov = CheckovRulesProvider()
    ta = TemplateAnalyzerRulesProvider()
    psrule = PSRuleRulesProvider()
    hsc_rules = horusec.get_rules()
    ckv_rules = checkov.get_rules()
    ta_rules = ta.get_rules()
    psrule_rules = psrule.get_rules()
    rules = psrule_rules + ta_rules + hsc_rules + ckv_rules

    writer = SonarQubeRulesXmlWriter()
    writer.write_rules(rules, "../../src/main/resources/sonar-rules.xml")
    print("Done.")