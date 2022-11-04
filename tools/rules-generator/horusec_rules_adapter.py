from common import Rule, ExternalRulesProvider
from bs4 import BeautifulSoup
import requests
import re

class HorusecRulesProvider(ExternalRulesProvider):
    def get_rules(self):
        print('Retriving rules from Horusec doco')
        csharp_rules = self._get_rules_from_url('https://docs.horusec.io/docs/cli/analysis-tools/open-source-horusec-engine/horusec-csharp')
        js_rules = self._get_rules_from_url('https://docs.horusec.io/docs/cli/analysis-tools/open-source-horusec-engine/horusec-nodejs')
        rules = csharp_rules + js_rules
        return rules

    def _get_rules_from_url(self, url):
        r = requests.get(url)
        html_content = r.content
        soup = BeautifulSoup(r.content, 'html.parser')
        rule_headings = soup.select('div.alert-info')
        rules = [Rule(id=x.contents[0], desc=x.find_next('p').contents[0]) for x in rule_headings]
        
        # Sanitise rules using regexes
        for rule in rules:
            id_match = re.search('\“(?P<id>.+)\”', rule.id)
            if id_match != None:
                rule.id = id_match.group('id')
            link_match = re.search("\((?P<url>https?://[^\s]+)\)", rule.desc)
            rule.link = link_match.group("url") if link_match != None else ""

        return rules

