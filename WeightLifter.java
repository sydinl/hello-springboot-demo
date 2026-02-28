public class WeightLifter {
    
    /**
     * Calculates the maximum weight a weight lifter can gain by eating steaks optimally.
     * 
     * @param steaks Array of steak weights
     * @return Maximum total weight gain
     */
    public static int maxWeightGain(int[] steaks) {
        int n = steaks.length;
        int days = n / 4;
        
        // Sort steaks in descending order
        sortDescending(steaks);
        
        int totalWeight = 0;
        
        // The optimal strategy is to maximize the sum of:
        // - Heaviest steaks for odd days (we get full weight)
        // - Second heaviest steaks for even days (we get second heaviest weight)
        
        // We have days/2 odd days and days/2 even days
        // For odd days: use the heaviest steaks
        // For even days: use the second heaviest steaks
        
        // The optimal strategy is to maximize the sum of:
        // - Heaviest steaks for odd days (we get full weight)
        // - Second heaviest steaks for even days (we get second heaviest weight)
        
        // We have days/2 odd days and days/2 even days
        // For odd days: use the heaviest steaks
        // For even days: use the second heaviest steaks
        
        for (int day = 0; day < days; day++) {
            if (day % 2 == 0) {
                // Odd day (1-based): use the heaviest available steak
                totalWeight += steaks[day];
            } else {
                // Even day (1-based): use the second heaviest available steak
                // We need to skip one steak to get the second heaviest
                totalWeight += steaks[day + 1];
            }
        }
        
        return totalWeight;
    }
    
    /**
     * Sorts an array in descending order using selection sort.
     * 
     * @param arr Array to sort
     */
    private static void sortDescending(int[] arr) {
        int n = arr.length;
        
        for (int i = 0; i < n - 1; i++) {
            int maxIndex = i;
            
            // Find the maximum element in remaining array
            for (int j = i + 1; j < n; j++) {
                if (arr[j] > arr[maxIndex]) {
                    maxIndex = j;
                }
            }
            
            // Swap the found maximum element with the first element
            if (maxIndex != i) {
                int temp = arr[i];
                arr[i] = arr[maxIndex];
                arr[maxIndex] = temp;
            }
        }
    }
    
    /**
     * Main method to test the solution
     */
    public static void main(String[] args) {
        // Test case from the problem
        int[] steaks1 = {1, 2, 3, 4, 5, 6, 7, 8};
        System.out.println("Input: [1,2,3,4,5,6,7,8]");
        System.out.println("Output: " + maxWeightGain(steaks1));
        System.out.println("Expected: 14");
        
        // Additional test cases
        int[] steaks2 = {1, 1, 1, 1, 2, 2, 2, 2};
        System.out.println("\nInput: [1,1,1,1,2,2,2,2]");
        System.out.println("Output: " + maxWeightGain(steaks2));
        System.out.println("Expected: 3");
        
        int[] steaks3 = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 1, 1};
        System.out.println("\nInput: [10,9,8,7,6,5,4,3,2,1,1,1]");
        System.out.println("Output: " + maxWeightGain(steaks3));
        System.out.println("Expected: 24");
    }
}
