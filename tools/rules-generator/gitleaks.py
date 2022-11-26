from common import Rule, ExternalRulesProvider
import requests
import tomli

TOML_FILE_NAME='gitleaks.toml'

class GitLeaksRulesProvider(ExternalRulesProvider):
    def get_rules(self):
        print('Retriving rules from gitleaks config.toml')
        rules = self._get_rules_from_url('https://raw.githubusercontent.com/zricethezav/gitleaks/master/config/gitleaks.toml')
        return rules

    def _get_rules_from_url(self, url):
        # Download the file first
        r = requests.get(url)
        open(TOML_FILE_NAME,'wb').write(r.content)
        
        with open(TOML_FILE_NAME, mode='rb') as f:
            config = tomli.load(f)
            rules = [Rule(id=x['id'], 
                    name='GitLeaks: {} detected'.format(x['description']), 
                    desc='GitLeaks detected {} using the regex: {}'.format(x['description'], x['regex']), 
                    help_link='', 
                    link='https://github.com/zricethezav/gitleaks/blob/master/config/gitleaks.toml') 
                    for x in config['rules']]
                
            return rules

