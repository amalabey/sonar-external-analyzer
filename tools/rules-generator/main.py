from horusec_rules_adapter import HorusecRulesProvider

if __name__ == "__main__":
    print("Generating rules from external tools documentation")
    horusec = HorusecRulesProvider()
    rules = horusec.get_rules()
    print("Done.")