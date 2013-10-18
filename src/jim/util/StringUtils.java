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
}
