from common import Rule, ExternalRulesProvider
from bs4 import BeautifulSoup
import requests
import re

class PSRuleRulesProvider(ExternalRulesProvider):
    def get_rules(self):
        print('Retriving rules from PSRule')
        rules = self._get_rules_from_url('https://azure.github.io/PSRule.Rules.Azure/en/rules/')
        return rules

    def _get_rules_from_url(self, url):
        r = requests.get(url)
        html_content = r.content
        soup = BeautifulSoup(r.content, 'html.parser')

        rules = list()
        rule_rows = soup.select('table tbody tr')
        for rule_row in rule_rows:
            columns = rule_row.find_all("td")
            rule_id = columns[0].contents[0]
            rule_link = "{0}{1}".format(url, columns[1].find("a")["href"])
            rule_name = columns[2].contents[0]
            rule_desc = self._get_desc(rule_link)
            rule = Rule(id=rule_id, name=rule_name, desc=rule_desc, link=rule_link, help_link=rule_link)
            rules.append(rule)
        return rules

    def _get_desc(self, rule_link):
        r = requests.get(rule_link)
        html_content = r.content
        soup = BeautifulSoup(r.content, 'html.parser')
        desc = ""

        desc_tag = soup.select("h2#description")[0]
        for tag in desc_tag.next_siblings:
            if tag.name == "hr":
                break
            else:
                desc += str(tag)
        return desc