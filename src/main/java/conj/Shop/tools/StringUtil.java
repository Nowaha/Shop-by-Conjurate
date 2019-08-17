package conj.Shop.tools;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static String toString(final List<String> list) {
        if (list == null) {
            return null;
        }
        String string = "";
        for (final String s : list) {
            string = string + s + "/;";
        }
        return string;
    }

    public static List<String> fromString(final String string) {
        if (string == null) {
            return null;
        }
        final List<String> list = new ArrayList<String>();
        final String[] split = string.split("/;");
        for (final String s : split) {
            list.add(s);
            Debug.log("Added lore: " + s);
        }
        return list;
    }
}
