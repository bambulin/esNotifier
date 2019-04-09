package cz.bambula.esnotifier.trigonometry;

/**
 * Created by tkozel on 10/21/16.
 */

public class DoubleComparator {

    public static int compare(Double a, Double b) {
        if (Double.compare(a, b) == 0) {
            return 0;
        }
        if (Double.compare(Math.abs(a - b), Math.ulp(a)) <= 0) {
            return 0;
        }
        return Double.compare(a, b);
    }
}
