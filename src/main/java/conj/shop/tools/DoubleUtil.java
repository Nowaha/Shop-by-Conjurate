package conj.shop.tools;

import java.text.NumberFormat;

public class DoubleUtil {
    public static String toString(final Double value) {
        return NumberFormat.getInstance().format(value);
    }
}
