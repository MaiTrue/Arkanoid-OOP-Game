public class bai2 {
    public static int minArray(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int min = arr[0];
        for (int value : arr) {
            if (value < min) {
                min = value;
            }
        }
            return min;
    }
}
6
