package org.sonarsource.plugins.extanalyzer.shared;

public class ExternalIssue {
    public final String RuleId;
    public final String Message;
    public final String FilePath;
    public final int StartLine;
    public final int StartColumn;

    public ExternalIssue(String ruleId, String message, String filePath, int startLine, int startColumn) {
        this.RuleId = ruleId;
        this.Message = message;
        this.FilePath = filePath;
        this.StartLine = startLine;
        this.StartColumn = startColumn;
    } 
}