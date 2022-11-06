package org.sonarsource.plugins.extanalyzer.shared;

public class ExternalRule {
    public final String Key;
    public final String Name;
    public final String Description;
    public final String Help;

    public ExternalRule(String key, String name, String description, String help) {
        this.Key = key;
        this.Name = name;
        this.Description = description;
        this.Help = help;
    } 
}