package huadi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String 工具类, 提供了一些针对 String 的奇怪操作.
 *
 * @author Huadi
 */
public class StringUtils {
    // ------------------------ SubString ------------------------ //

    /**
     * substring for Unicode character.<br />
     * Returns a new string that is a substring of <code>str</code>.<br />
     * For ASCII characters, it do the same thing like
     * {@link String#substring(int beginIndex, int endIndex)} method. The
     * substring begins at the byte presented by the specified
     * <code>beginIndex</code> and extends to the character at index
     * <code>endIndex - 1</code>. If the specified index is exactly in the half
     * of the character, the method will retain the character in the returned
     * string. So the length of the substring measured by Byte may between
     * (endIndex - beginIndex) to (endIndex - beginIndex + 2).
     * <p>
     * Examples: <blockquote>
     *
     * <pre>
     * subString("hamburger", 4, 8) returns "urge"
     * subString("smiles", 1, 5) returns "mile"
     * subString("1贰肆67捌0", 0, 7) returns "1贰肆67"
     * subString("1贰肆67捌0", 1, 7) returns "贰肆67"
     * subString("1贰肆67捌0", 2, 7) returns "贰肆67"
     * subString("1贰肆67捌0", 3, 8) returns "肆67捌"
     * subString("1贰肆67捌0", 3, 9) returns "肆67捌"
     * subString("1贰肆67捌0", 3, 10) returns "肆67捌0"
     * </pre>
     *
     * </blockquote>
     *
     * @param str        the original string.
     * @param beginIndex the beginning index, inclusive.
     * @param endIndex   the ending index, exclusive.
     * @return the specified substring.
     * @throws IndexOutOfBoundsException if the <code>beginIndex</code> is negative, or
     *                                   <code>endIndex</code> is larger than the length of this
     *                                   <code>String</code> object, or <code>beginIndex</code> is
     *                                   larger than <code>endIndex</code>.
     */
    public static String unicodeSubString(String str, int beginIndex, int endIndex) {
        if (beginIndex < 0)
            throw new StringIndexOutOfBoundsException(beginIndex);
        if (beginIndex > endIndex)
            throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
        if (beginIndex == endIndex)
            return "";

        int byteLength = 0;
        StringBuilder returnString = new StringBuilder();
        for (int i = 0; byteLength < endIndex && i < str.length(); i++) {
            char c = str.charAt(i);
            byteLength += (31 < c && c < 128) ? 1 : 2;
            if (byteLength > beginIndex)
                returnString.append(c);
        }

        return returnString.toString();
    }

    /**
     * substring for Unicode character.<br />
     * Returns a new string that is a substring of <code>str</code>.<br />
     * For ASCII characters, it do the same thing like
     * {@link String#substring(int beginIndex)} method. The substring begins at
     * the byte presented by the specified <code>beginIndex</code> and extends
     * to the end of this string. If the specified index is exactly in the half
     * of the character, the method will retain the character in the returned
     * string.
     * <p>
     * Examples: <blockquote>
     *
     * <pre>
     * subString("unhappy", 2) returns "happy"
     * subString("Harbison", 3) returns "bison"
     * subString("1贰肆67捌0", 0) returns "1贰肆67捌0"
     * subString("1贰肆67捌0", 1) returns "贰肆67捌0"
     * subString("1贰肆67捌0", 2) returns "贰肆67捌0"
     * </pre>
     *
     * </blockquote>
     *
     * @param beginIndex the beginning index, inclusive.
     * @return the specified substring.
     * @throws IndexOutOfBoundsException if <code>beginIndex</code> is negative or larger than the
     *                                   length of this <code>String</code> object.
     */
    public static String unicodeSubString(String str, int beginIndex) {
        return unicodeSubString(str, beginIndex, str.length());
    }

    /**
     * Remove the domain of an URL and the last "/".<br />
     * This method may be used to cut the domain to build a request's namespace.
     *
     * @param url the URL string needs to remove domain.
     * @return "http://www.google.com/huadi" or "https://www.google.com/huadi/"
     * returns "/huadi".
     */
    public static String removeDomain(String url) {
        Pattern p = Pattern.compile("^(https?://)[^/]*");
        Matcher m = p.matcher(url);
        url = m.replaceFirst("");
        return (url.endsWith("/")) ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Check if an 18-digit China mainland ID card number is legal.
     *
     * @param idCard the ID card number needs to be checked.
     * @return <code>true</code> if legal. <code>false</code> if illegal.
     */
    public static boolean isLegalIdCard(String idCard) {
        String pattern = "((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65|71|81|82|91)\\d{4})((((19|20)(([02468][048])|([13579][26]))0229))|((20[0-9][0-9])|(19[0-9][0-9]))((((0[1-9])|(1[0-2]))((0[1-9])|(1\\d)|(2[0-8])))|((((0[1,3-9])|(1[0-2]))(29|30))|(((0[13578])|(1[02]))31))))((\\d{3}(x|X))|(\\d{4}))";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(idCard);
        return m.matches();
    }
}
