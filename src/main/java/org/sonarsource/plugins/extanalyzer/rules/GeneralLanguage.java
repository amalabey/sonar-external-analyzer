package org.sonarsource.plugins.extanalyzer.rules;

import org.sonar.api.resources.AbstractLanguage;
import org.sonarsource.plugins.extanalyzer.Constants;

/**
 * In order for a rule repository to work properly, the rules created in the repository
 * must be associated with a language. This is a workaround so that rules that apply to
 * components where the language is not known (or irrelevant).
 *
 * This class simply creates a new 'language' called general.
 */
public class GeneralLanguage extends AbstractLanguage {

    public GeneralLanguage() {
        super(Constants.LANGUAGE_KEY, "General");
    }

    @Override
    public String[] getFileSuffixes() {
        return new String[0];
    }
}
