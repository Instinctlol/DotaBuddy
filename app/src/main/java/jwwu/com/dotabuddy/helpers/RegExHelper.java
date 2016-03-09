package jwwu.com.dotabuddy.helpers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by Instinctlol on 23.10.2015.
 */
public final class RegExHelper {

    /**
     * Returns an ArrayList, where each index contains all the text between a prefix and a suffic inside a source String.
     * If nothing is found at all, then return an ArrayList, where the first index contains an 'empty' String : ""
     * @param prefix What stands before a successCount we search for
     * @param suffix What stands behind a successCount we search for
     * @param source Search inside this String
     * @return ArrayList containing all the values between prefix and suffix, or if nothing is found, an ArrayList with only one element, which is an empty String: ""
     */
    public static ArrayList<String> searchForValuesBetweenPrefixAndSuffix(String prefix, String suffix, String source) {
        return searchPatternInSource(prefix + "(.*?)" + suffix, source);
    }

    public static ArrayList<String> greedySearchForValuesBetweenPrefixAndSuffix(String prefix, String suffix, String source) {
        return searchPatternInSource(prefix + "(.*)" + suffix, source);
    }

    public static ArrayList<String> searchPatternInSourceRemoveWhitespace(String pattern, String source) {
        ArrayList<String> findings = searchPatternInSource(pattern,source);

        formatArrayListWhitespace(findings);

        return findings;
    }

    public static ArrayList<String> searchPatternInSource(String pattern, String source) {
        // the pattern we want to search for
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(source);
        ArrayList<String> findings = new ArrayList<>();

        while(m.find())
            findings.add(m.group(1));

        if(findings.isEmpty())
        //we dont want to return empty arraylists, because we need to save these values/findings.
        // if no successCount is found, then it is simply an empty string, which we are going to save
            findings.add("");

        return findings;
    }

    public static void formatArrayListWhitespace(ArrayList<String> list) {
        if(!list.isEmpty()) {
            for(String t : list) {
                list.set(list.indexOf(t),t.replace(" ","_"));
            }
        }
    }
}
