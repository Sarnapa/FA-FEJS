package Layout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class containing methods for verifying values inserting by user.
 */

class ValueValidator {

    boolean isEmpty(Object newValue)
    {
        return (newValue == null || newValue.toString().equals(""));
    }

    boolean findSpecialCharacter(String text)
    {
        Pattern p = Pattern.compile("[^\\p{L}-' ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.find();
    }

    boolean findSpecialCharacterForTeam(String text)
    {
        Pattern p = Pattern.compile("[^\\p{L}0-9-' ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.find();
    }

    boolean isInteger(String text)
    {
        try
        {
            Integer.parseInt(text);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    boolean isValidDate(String dateText)
    {
        Pattern p  = Pattern.compile("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])$");
        Matcher m = p.matcher(dateText);
        return m.find();
    }

}
