from abc import ABC,abstractmethod

class Rule:
    def __init__(self, **kwargs):
        self.__dict__=kwargs

class ExternalRulesProvider(ABC):
    @abstractmethod
    def get_rules(self):
        pass

class RulesWriter(ABC):
    @abstractmethod
    def write_rules(self, rules, filePath):
        pass