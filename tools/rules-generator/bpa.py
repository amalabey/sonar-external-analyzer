from common import Rule, ExternalRulesProvider
from bs4 import BeautifulSoup
import requests
import re

class TemplateAnalyzerRulesProvider(ExternalRulesProvider):
    def get_rules(self):
        print('Retriving rules from Template analyzer')
        rules = self._get_rules_from_url('https://github.com/Azure/template-analyzer/blob/development/docs/built-in-bpa-rules.md')
        return rules

    def _get_rules_from_url(self, url):
        r = requests.get(url)
        html_content = r.content
        soup = BeautifulSoup(r.content, 'html.parser')

        rules = list()
        rule_headings = soup.select('article.markdown-body h3')
        for rule_heading in rule_headings:
            heading_text = rule_heading.contents[1]
            heading_tokens = heading_text.split(":")
            rule_id = heading_tokens[0]
            rule_name = heading_tokens[1]
            rule_link = "{0}?#{1}".format(url, rule_heading.find("a")["id"])
            rule_desc = str(rule_heading.find_next_sibling("p").contents)
            rule_desc += str(rule_heading.find_next_sibling("p").find_next_sibling("p").contents)
            rule = Rule(id=rule_id, name=rule_name, desc=rule_desc, link=rule_link, help_link=rule_link)
            rules.append(rule)
        return rules