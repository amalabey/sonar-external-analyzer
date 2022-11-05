package org.sonarsource.plugins.extanalyzer.shared;

public interface IExternalIssueReader {
    Iterable<ExternalIssue> getIssues();
}
