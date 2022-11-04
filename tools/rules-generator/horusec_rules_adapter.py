from common import Rule, ExternalRulesProvider

class HorusecRulesProvider(ExternalRulesProvider):
    def get_rules(self):
        print("Retriving rules from Horusec doco")