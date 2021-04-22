package util;

/**
 * @author Rex Joush
 * @time 2021.04.21
 */

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Tools for common
 */
public class CommonUtils {

    public static DecimalFormat TimeFormat = new DecimalFormat("00");
    public static DecimalFormat doubleFormat = new DecimalFormat("#.00");

    // Check the file every line whether the format meets the requirements
    public static Pattern pTime = Pattern.compile("(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday) [0-2][0-9]:[0-5][0-9],[A-Za-z]+,[A-Za-z]+,[0-9]+,[0-9]+");

    // Check the file every line whether the format meets the requirements
    public static Pattern pLocation = Pattern.compile("[A-Za-z]+,[-]?\\d+.\\d+,[-]?\\d+.\\d+,[-]?[01](.\\d+)?");

}
