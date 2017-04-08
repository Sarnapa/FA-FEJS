package Layout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

class InsertModeInputVerifier extends InputVerifier
{
    private ValueValidator validator;
    private boolean idFlag = false, firstNameFlag = false, lastNameFlag = false, teamFlag = false, integerFlag = true;

    InsertModeInputVerifier(ValueValidator validator)
    {
        this.validator = validator;
    }

   boolean isOK()
   {
       return idFlag && firstNameFlag && lastNameFlag && teamFlag && integerFlag;
   }

    public boolean verify(JComponent input)
    {
        JTextField textField;
        String inputValue;
        String inputName = input.getName();
        switch(inputName)
        {
            case "idField":
            {
                textField = (JTextField)input;
                inputValue = textField.getText();
                if(validator.isEmpty(inputValue)) {
                    input.setBorder(new LineBorder(Color.red, 1));
                    idFlag = false;
                    return false;
                }
                else
                {
                    if(!validator.isInteger(inputValue)) {
                        input.setBorder(new LineBorder(Color.red, 1));
                        idFlag = false;
                        return false;
                    }
                    else
                        idFlag = true;
                }
                break;
            }
            case "firstNameField": case "lastNameField":
                textField = (JTextField)input;
                inputValue = textField.getText();
                if(validator.isEmpty(inputValue)) {
                    input.setBorder(new LineBorder(Color.red, 1));
                    if(inputName.equals("firstNameField"))
                        firstNameFlag = false;
                    else
                        lastNameFlag = false;
                    return false;
                }
                else
                {
                    if(validator.findSpecialCharacter(inputValue)) {
                        input.setBorder(new LineBorder(Color.red, 1));
                        if(inputName.equals("firstNameField"))
                            firstNameFlag = false;
                        else
                            lastNameFlag = false;
                        return false;
                    }
                    else
                    {
                        if(inputName.equals("firstNameField"))
                            firstNameFlag = true;
                        else
                            lastNameFlag = true;
                    }
                }
                break;
            case "teamField":
            {
                textField = (JTextField)input;
                inputValue = textField.getText();
                if (validator.isEmpty(inputValue)) {
                    input.setBorder(new LineBorder(Color.red, 1));
                    teamFlag = false;
                    return false;
                } else {
                    if (validator.findSpecialCharacterForTeam(inputValue)) {
                        input.setBorder(new LineBorder(Color.red, 1));
                        teamFlag = false;
                        return false;
                    } else
                        teamFlag = true;
                }
                break;
            }
            default:
            {
                textField = (JTextField)input;
                inputValue = textField.getText();
                if (!validator.isEmpty(inputValue)) {
                    if (!validator.isInteger(inputValue)) {
                        input.setBorder(new LineBorder(Color.red, 1));
                        integerFlag = false;
                        return false;
                    }
                }
                else
                {
                    textField.setText("0");
                }
                integerFlag = true;
                break;
            }
        }
        input.setBorder(new LineBorder(new Color(0, 153, 0), 1));
        return true;
    }
}
