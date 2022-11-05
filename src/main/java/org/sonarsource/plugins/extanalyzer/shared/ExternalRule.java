package org.sonarsource.plugins.extanalyzer.shared;

public class ExternalRule {
    public final String Key;
    public final String Name;
    public final String Description;

    public ExternalRule(String key, String name, String description) {
        this.Key = key;
        this.Name = name;
        this.Description = description;
    } 
}