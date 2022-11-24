package org.sonarsource.plugins.extanalyzer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.sonarsource.plugins.extanalyzer.Constants;

public class RuleFinder {
    public String getMatchingRuleId(String key) 
    {
        if(Rules.RULE_IDS.contains(key))
        {
            return key;
        }

        Pattern pattern = Pattern.compile(Constants.DEP_CHECK_RULE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(key);
        if(matcher.find())
        {
            return Constants.DEP_CHECK_RULE_ID;
        }

        return key;
    }
}
