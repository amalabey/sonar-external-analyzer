from horusec import HorusecRulesProvider
from sonarqube import SonarQubeRulesXmlWriter

if __name__ == "__main__":
    print("Generating rules from external tools documentation")
    horusec = HorusecRulesProvider()
    rules = horusec.get_rules()

    writer = SonarQubeRulesXmlWriter()
    writer.write_rules(rules, "../../src/main/resources/sonar-rules.xml")
    print("Done.")