package com.asianitinc.availtrade.config;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidation
{
    private static Matcher matcher;
    private static Pattern pattern;


    /*
     *** Name POLICY ***

     * Name must be at least 5 chars
     * Does not contains no digit
     * Can contains uppercase and lowercase
     * Contains only [.] this special char
     * Maximum 25 chars
     */
    public static boolean nameValidation(String name)
    {
        String regx = "^[\\p{L} .'-]+$";
        pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(name);

        //with regx char ,name length is greater than 5 and less than 25
        return matcher.find() && (name.length() >= 5 && name.length() <= 25);
    }

    /*
     *** Email POLICY ***

     * Email must be at least 8 chars
     * Does contains any digit or number
     * Can contains lowercase and uppercase
     * Contains only [. _ % @] this special char
     * Maximum 45 chars
     */

    public static boolean emailValidation(String email)
    {
        String regx = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
        matcher = pattern.matcher(email);

        //with regx char ,name length is greater than 5 and less than 25
        return matcher.find() && (email.length() >= 8 && email.length() <= 40);
    }


    /*
     *** PASSWORD POLICY ***

     * password must be at least 8 chars
     * Contains at least one digit
     * Contains at least one lower case and one upper case char
     * Contains at least one char with a set of special chars [@#$%^]
     * Does not contain space or tab
     */
    public static boolean passwordValidation(String pass)
    {

//        String regx = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
//
//        Pattern pattern = Pattern.compile(regx,Pattern.CASE_INSENSITIVE);
//        Matcher matcher = pattern.matcher(pass);

        //with regx char ,name length is greater than 5 and less than 25
        return (pass.length() >= 6 && pass.length() <= 16);
    }


    /*
        *** PHONE NUMBER POLICY ***
        * Phone number must be 11 digits
        * If user use country code,that case phone number must be 14 digits
     */

    public static boolean phoneNumberValidation(String phone) {
        if (TextUtils.isEmpty(phone))
            return false;

        if (phone.length() > 6) {
            if (phone.substring(0, 3).equals("+88")) {
                if (phone.length() != 14) {
                    return false;
                } else {
                    switch (phone.substring(3, 6)) {
                        case "019":
                            break;
                        case "018":
                            break;
                        case "017":
                            break;
                        case "016":
                            break;
                        case "015":
                            break;
                        case "014":
                            break;
                        case "013":
                            break;
                        default: {
                            return false;
                        }
                    }
                }
            } else if (!phone.substring(0, 3).equals("+88")) {
                if (phone.length() != 11) {
                    return false;
                } else {
                    switch (phone.substring(0, 3)) {
                        case "019":
                            break;
                        case "018":
                            break;
                        case "017":
                            break;
                        case "016":
                            break;
                        case "015":
                            break;
                        case "014":
                            break;
                        case "013":
                        default: {
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        }

        return true;
    }
}
