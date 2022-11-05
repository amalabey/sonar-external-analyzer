package org.sonarsource.plugins.extanalyzer.shared;

public interface IExternalRulesStore {
    Iterable<ExternalRule> getRules();
}
