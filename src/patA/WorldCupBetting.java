package patA;

import java.util.Scanner;

public class WorldCupBetting {
    
    private static char[] level = {'W','T','L'};
    
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        double sum = 1;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i=0 ;i<3;i++){
            double max = 0;
            int index = 0;
            for (int j=0 ;j<3;j++){
                double tmp = scanner.nextDouble();
                if (tmp > max ){
                    max = tmp;
                    index = j;
                }
            }
            sum*=max;
            stringBuilder.append(level[index]).append(" ");
        }
        stringBuilder.append(String.format("%.2f", (sum*0.65-1)*2));
        System.out.println(stringBuilder.toString());
    }
}
