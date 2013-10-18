package jim.util;

public class StringUtils {



    /**
     * Inverse of String.split(). Joins an array of Strings to one string. e.g.
     * {"abc", "def"} joinwith ' ' -> "abc def".
     */
    public static String join(String arrayOfStrings[], char joinChar) {
        return join(arrayOfStrings, joinChar, 0);
    }



    /**
     * Inverse of String.split(). Joins an array of Strings to one string. e.g.
     * {"abc", "def"} joinwith ' ' -> "abc def".
     */
    public static String join(String arrayOfStrings[],
                               char joinChar,
                               int startIndex) {
        return join(arrayOfStrings, joinChar, startIndex, arrayOfStrings.length);
    }



    /**
     * Inverse of String.split(). Joins an array of Strings to one string. e.g.
     * {"abc", "def"} joinwith ' ' -> "abc def".
     */
    public static String join(String arrayOfStrings[],
                               char joinChar,
                               int startIndex,
                               int endIndex) {
        StringBuilder result = new StringBuilder();

        for (int i = startIndex; i < endIndex - 1; i++) {
            result.append(arrayOfStrings[i]);
            result.append(joinChar);
        }

        result.append(arrayOfStrings[endIndex - 1]);

        return result.toString();
    }



    public static boolean isStringSurroundedBy(String str, char begin, char end) {
        return str.charAt(0) == begin && str.charAt(str.length() - 1) == end;
    }



    public static String stripStringPrefixSuffix(String str, int n){
        // Not an efficient solution. <3 Recursion, though
        if (n <= 0) {
            return str;
        } else {
            // May not make sense if we just strip by n?
            String inner = str.substring(1, str.length() - 1);
            return stripStringPrefixSuffix(inner, n - 1);
        }
    }
    
    
    
    public static String removeAllSymbols(String tellDateOrTime) {
        String findDate = tellDateOrTime.replaceAll("[^\\p{L}\\p{Nd}]", "");
        return findDate;
    }



    public static int[] splitDate(String date_in_string) {
        final int LENGTH_OF_DATE = 3; // [DD][MM][YY]
        final int INDICATE_YEAR = 2;
        final int INDICATE_MONTH = 1;
        
        // we will accept date format of 090913 - DDMMYY
        int[] dates = new int[LENGTH_OF_DATE];
        String[] temp = date_in_string.split("");
        int counter = 1; // temp[0] is a spacing
        for (int i = 0; i < LENGTH_OF_DATE; i++) {
            if (i == INDICATE_YEAR) {
                dates[i] = Integer.parseInt("20" +
                                            temp[counter++] +
                                            temp[counter++]);
            } else if (i == INDICATE_MONTH) {
                dates[i] = Integer.parseInt(temp[counter++] + temp[counter++]);
                dates[i] = dates[i];
            } else {
                dates[i] = Integer.parseInt(temp[counter++] + temp[counter++]);
            }
        }
        return dates;
    }
}
