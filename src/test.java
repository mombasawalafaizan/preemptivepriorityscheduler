import java.util.ArrayList;
public class test {
    public static void main(String[] args){
        ArrayList<ProcessPriority> list = new ArrayList<>();
        int [][] priority = new int[5][4];
        for(int i = 0; i < 5; i++){
            priority[i][0] = (i+1);
            priority[i][1] = (int)(Math.random() * 3);
            priority[i][2] = (int)(Math.random() * 11);
        }
        priority[0][3] = 2;	
        priority[1][3] = 1;
        priority[2][3] = 3;
        priority[3][3] = 1;
        priority[4][3] = 2;
        for(int i = 0; i < priority.length; i++)
            list.add(new ProcessPriority(priority[i][0], priority[i][1], priority[i][3]));
        java.util.Collections.sort(list);
        for(ProcessPriority i: list)
            System.out.println(i);
    }
    
}
