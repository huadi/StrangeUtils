package huadi.util.string;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String 工具类, 提供了一些针对 String 的奇怪操作.
 * 
 * @author Huadi
 */
public class StringUtil {
    // ------------------------ RandomString ------------------------ //
    private static final char[] ALPHABETA = "abcdefghijklmnopqrstuvwxyz"
            .toCharArray();
    private static final char ch[] = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '0', '1' };
    private static Random random = new Random();

    private static int rand(int lo, int hi) {
        int n = hi - lo + 1;
        int i = random.nextInt() % n;
        if (i < 0)
            i = -i;
        return lo + i;
    }

    /**
     * Generate a random string.
     *
     * @param length
     *            The random string's length.
     * @return A random <code>String</code> which's length is the specified
     *         value.
     */
    public static String randomStringV2(int length) {
        if (length > 0) {
            char[] result = new char[length];
            int index = 0, rand = random.nextInt();
            for (int i = 0; i < length % 5; i++) {
                result[index++] = ch[(byte) rand & 61];
                rand >>= 6;
            }
            for (int i = length / 5; i > 0; i--) {
                rand = random.nextInt();
                for (int j = 0; j < 5; j++) {
                    result[index++] = ch[(byte) rand & 61];
                    rand >>= 6;
                }
            }
            return new String(result, 0, length);
        } else if (length == 0) {
            return "";
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Generate a random string.
     *
     * @param lo
     *            The lower bound of the string's length.
     * @param hi
     *            The upper bound of the string's length.
     * @return a random <code>String</code> which's length is a random value
     *         between <code>lo</code> and <code>hi</code>.
     */
    public static String randomString(int lo, int hi) {
        int len = rand(lo, hi);
        byte b[] = new byte[len];
        for (int i = 0; i < len; i++) {
            int mux = random.nextInt() % 3;
            if (mux < 0)
                mux = -mux;
            switch (mux) {
            case 0:
                b[i] = (byte) rand('a', 'z');
                break;
            case 1:
                b[i] = (byte) rand('A', 'Z');
                break;
            case 2:
                b[i] = (byte) rand('0', '9');
                break;
            }
        }
        return new String(b, 0, len);
    }

    /**
     * Generate a random string.<br />
     * This method running lower than the V2 method.
     *
     * @param length
     *            The random string's length.
     * @return A random <code>String</code> which's length is the specified
     *         value.
     */
    @Deprecated
    public static String randomString(int length) {
        char[] idChar = new char[length];
        byte[] idByte = new byte[length];
        random.nextBytes(idByte);

        for (int i = 0; i < idByte.length; i++) {
            int index = (idByte[i] % ALPHABETA.length);
            if (index < 0)
                index = -index;
            idChar[i] = ALPHABETA[index];
        }
        StringBuffer sb = new StringBuffer("");
        sb.append(idChar);
        return sb.toString();
    }

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
     * @param str
     *            the original string.
     * @param beginIndex
     *            the beginning index, inclusive.
     * @param endIndex
     *            the ending index, exclusive.
     * @return the specified substring.
     * @exception IndexOutOfBoundsException
     *                if the <code>beginIndex</code> is negative, or
     *                <code>endIndex</code> is larger than the length of this
     *                <code>String</code> object, or <code>beginIndex</code> is
     *                larger than <code>endIndex</code>.
     */
    public static String subString(String str, int beginIndex, int endIndex) {
        if (beginIndex < 0)
            throw new StringIndexOutOfBoundsException(beginIndex);
        if (beginIndex > endIndex)
            throw new StringIndexOutOfBoundsException(endIndex - beginIndex);
        if (beginIndex == endIndex)
            return "";

        int byteLength = 0;
        String returnString = "";
        for (int i = 0; byteLength < endIndex && i < str.length(); i++) {
            char c = str.charAt(i);
            byteLength += (31 < c && c < 128) ? 1 : 2;
            if (byteLength > beginIndex)
                returnString += c;
        }

        return returnString;
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
     * @param beginIndex
     *            the beginning index, inclusive.
     * @return the specified substring.
     * @exception IndexOutOfBoundsException
     *                if <code>beginIndex</code> is negative or larger than the
     *                length of this <code>String</code> object.
     */
    public static String subString(String str, int beginIndex) {
        return subString(str, beginIndex, str.length());
    }

    /**
     * Remove the domain of an URL and the last "/".<br />
     * This method may be used to cut the domain to build a request's namespace.
     *
     * @param url
     *            the URL string needs to remove domain.
     * @return "http://www.google.com/huadi" or "https://www.google.com/huadi/"
     *         returns "/huadi".
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
     * @param idCard
     *            the ID card number needs to be checked.
     * @return <code>true</code> if legal. <code>false</code> if illegal.
     */
    public static boolean isLegalIdCard(String idCard) {
        String pattern = "((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65|71|81|82|91)\\d{4})((((19|20)(([02468][048])|([13579][26]))0229))|((20[0-9][0-9])|(19[0-9][0-9]))((((0[1-9])|(1[0-2]))((0[1-9])|(1\\d)|(2[0-8])))|((((0[1,3-9])|(1[0-2]))(29|30))|(((0[13578])|(1[02]))31))))((\\d{3}(x|X))|(\\d{4}))";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(idCard);
        return m.matches();
    }
}
