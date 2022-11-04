from common import RulesWriter
from lxml import etree 

class SonarQubeRulesXmlWriter(RulesWriter):
    def write_rules(self, rules, filePath):
        root = etree.Element('rules')
        for rule in rules:
            rule_elem = etree.SubElement(root,'rule')
            key_elem = etree.SubElement(rule_elem, 'key')
            key_elem.text = rule.id
            desc_elem = etree.SubElement(rule_elem, 'description')
            desc_elem.text = etree.CDATA("{0} <br/> <a href='{1}'> More info</a>]]".format(rule.desc, rule.link))
            type_elem = etree.SubElement(rule_elem, 'type')
            type_elem.text = 'VULNERABILITY'

        tree = etree.ElementTree(root)
        with open (filePath, "wb") as file :
            tree.write(file)
