from common import Rule, ExternalRulesProvider
from bs4 import BeautifulSoup
import requests
import re

class CheckovRulesProvider(ExternalRulesProvider):
    def get_rules(self):
        print('Retriving rules from Checkov API + doco scraping')
        ruleid_doco_mappings = self._get_rule_links()
        
        rules = list()
        for rule_id, doco_url in ruleid_doco_mappings:
            rule = self._get_rule_from_url(rule_id, doco_url)
            if rule != None:
                rules.append(rule)

        return rules

    def _get_rule_links(self):
        response = requests.get('https://www.bridgecrew.cloud/api/v1/guidelines')
        response.raise_for_status()
        response_json = response.json()
        return response_json['guidelines'].items()


    def _get_rule_from_url(self, rule_id, doco_url):
        print("{0} - {1}".format(rule_id, doco_url))
        try:
            response = requests.get(doco_url)
            if response:
                html_content = response.content
                soup = BeautifulSoup(response.content, 'html.parser')

                name = soup.select('header#content-head h1')[0].contents[0]
                desc = "".join([str(x) for x in soup.select('div#content-container div.markdown-body')[0].contents])
                rule = Rule(id=rule_id, name=name, desc=desc, link=doco_url, help_link="")
                return rule
            else:
                print("WARN: Unable to access {0}".format(doco_url))
        except Exception as e:
            print("ERR: {0}".format(str(e)))
            
        return None
        