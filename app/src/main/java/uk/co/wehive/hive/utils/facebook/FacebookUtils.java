package uk.co.wehive.hive.utils.facebook;

import java.util.Collection;

public class FacebookUtils {

    public static boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }
}
