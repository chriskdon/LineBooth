package linebooth;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Chris Kellendonk
 * Student #: 4810800
 * Date: 2014-04-16.
 */
public class Utils {
    /**
     * Flatten 2D array to 1D
     * @param arr
     * @param <T>
     * @return
     */
    public static float[] flatten(float[][] arr) {
        float[] flat = new float[arr.length * arr.length];

        int x = 0;
        for(int r = 0; r < arr.length; r++) {
            for(int c = 0; c < arr[r].length; c++) {
                  flat[x++] = arr[r][c];
            }
        }

        return flat;
    }

    public static void print(float[][] arr) {
        for(int r = 0; r < arr.length; r++) {
            for(int c = 0; c < arr[r].length; c++) {
               System.out.print(arr[r][c] + ", ");
            }
            System.out.println();
        }

        System.out.println(Arrays.toString(flatten(arr)));
    }
}
