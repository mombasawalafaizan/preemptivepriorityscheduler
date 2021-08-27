package org.demo;

import java.util.ArrayList;
import java.util.Scanner;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;

public class PrePrioScheduler extends Application{
    
    @Override
    public void start(Stage primaryStage){
        
    }
    
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        
        System.out.print("Enter number of processes: ");
        int number_of_process = input.nextInt();
        
        /*priority matrix:
            0th column: process id
            1st column: arrival time
            2nd column: service time
            3rd column: priority
        */
        int [][] priority = new int[number_of_process][4];
        System.out.print("Enter process id for each process(should be unique for each process): ");
        for(int i = 0; i < priority.length; i++)
            priority[i][0] = input.nextInt();
        
        System.out.print("Enter arrival time for each process: ");
        for(int i = 0; i < priority.length; i++)
            priority[i][1] = input.nextInt();
        
        System.out.print("Enter service time for each process: ");
        for(int i = 0; i < priority.length; i++)
            priority[i][2] = input.nextInt();
        
        System.out.print("Enter priority for each process: ");
        for(int i = 0; i < priority.length; i++)
            priority[i][3] = input.nextInt();
        
        /*priority matrix:
            0th column: Process ID
            1st column: Arrival Time
            2nd column: Service Time
            3rd column: Priority
            4th column: Finish Time  
            5th column: TAT
            6th column: NTAT
        */
        int [][] computed_values = new int[number_of_process][7];
        sort(priority, 1); //Sort with respect to arrival time
        for(int i = 0; i<priority.length; i++)
            System.arraycopy(priority[i], 0, computed_values[i], 0, priority[i].length);
        
        //Schedule the processes
        PreemptivePriorityScheduler(priority,computed_values);
        
        //compute the wating time and turnaround time
        computeWT_TAT(computed_values);
        
        printTable(computed_values);
       
    }
    
    public static void PreemptivePriorityScheduler(int [][] priority, int [][] computed_values){
        //A list of Process Priority class so that we can add processes dynamically
        //and sort the processes with the help of compareTo method in ProcessPriority class using Collections.sort
        ArrayList<ProcessPriority> process_list = new ArrayList<>();
        
        int cur_highest_priority_process = 0; //Early arrived and unfinished process with highes priority
       
        int process_cnt = 0; //a count variable for arrived processes
        
        /*The next two variable are used in special case when a process has finished and other process
        has not arrived and hence, the scheduler does not have any process to schedule*/
        int ideal_processor = 0; //Count the number of seconds that process is ideal
        boolean no_process = false; //false if no_process is there in main memory
        for(int cur_sec = min(priority,1); cur_sec < (min(priority,1) + (sum(computed_values,2)) + ideal_processor); cur_sec++){
            boolean process_arrives = false;
            //check for every second till the last process arrives
            if(cur_sec <= max(priority,1)){
                //check if 1 or more process arrive at same second
                while(cur_sec == priority[process_cnt][1] && process_cnt < priority.length){
                    process_arrives = true;
                    no_process = false;
                    System.out.println("Process "+priority[process_cnt][0]+" arrives.");
                    ProcessPriority process = new ProcessPriority(priority[process_cnt][0], 
                            priority[process_cnt][1], priority[process_cnt][3]);
                    process_list.add(process);
                    process_cnt++;
                    //Condition if all process have arrived and last process
                    if(process_cnt == priority.length) break;
                }
                if(process_arrives){
                    //sort the arrived process w.r.t priority and arrival time using compareTo method in ProcessPriority class
                    java.util.Collections.sort(process_list);
                    int cur_highest_priority_process_id = process_list.get(0).getProcessID();
                    //Search for the process with the required process id
                    cur_highest_priority_process = indexSearch(priority, cur_highest_priority_process_id, 0);
                    process_arrives = false;
                }
                //Run the process for one second
            }
            if(no_process){ 
                System.out.println("Scheduler is idle because no process is there in main memory at "+cur_sec+" second.");
                ideal_processor++;
            }
            else{
                System.out.println("Process "+priority[cur_highest_priority_process][0]+" runs at "+cur_sec+" second.");
                priority[cur_highest_priority_process][2]--; //decrease the service time
                if(priority[cur_highest_priority_process][2] == 0){
                    System.out.println("Process "+priority[cur_highest_priority_process][0]
                                    +" finishes at "+(cur_sec+1)+" second.");
                    computed_values[cur_highest_priority_process][4] = cur_sec+1; //set the finish time in answer matrix
                    process_list.remove(0); //Remove the process which has finished
                    //if no process is there, then initialize no_process as true
                    if(!process_list.isEmpty())
                        //Get the next process with highest priority
                        cur_highest_priority_process = indexSearch(priority, process_list.get(0).getProcessID(), 0);
                    else
                        no_process = true;
                }
            }
        }
    }
    
    public static void printTable(int [][] computed_values){
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.println("--Process | Arrival | Service | Priority | Finish | TurnAround | Waiting | Normalized--");
        System.out.println("--  ID    |  Time   |  Time   |          |  Time  |     Time   |  Time   |   TAT     --");
        System.out.println("---------------------------------------------------------------------------------------");
        for(int i[]: computed_values){
            System.out.printf("--%6d  | %7d | %7d | %8d | %6d | %10d | %7d |",
                i[0], i[1], i[2], i[3], i[4], i[5], i[6]);
            System.out.printf(" %10.2f--", (float)i[5]/(float)i[2]);
            System.out.println();
        }
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.printf("Average waiting time: %.2f\n",average(computed_values, 6));
        System.out.printf("Average turnaround time: %.2f\n",average(computed_values, 5));
    }
    
    public static void sort(int [][] matrix, int col){
        int pass;
        int min_idx;
        for(int i = 0; i < matrix.length; i++){
            min_idx = i;
            for(pass = i + 1; pass < matrix.length; pass++){
                    if(matrix[pass][col] < matrix[min_idx][col])
                            min_idx = pass;
            } 
            if( min_idx != i){
                    int [] temp = matrix[min_idx];
                    matrix[min_idx] = matrix[i]; 
                    matrix[i] = temp;
            }
	}
    }
    
    public static void computeWT_TAT(int matrix[][]){
        for(int i = 0; i < matrix.length; i++){
            //Calculate TAT = FT - AT
            matrix[i][5]= matrix[i][4] - matrix[i][1];
            //Calculate WT = TAT - ST
            matrix[i][6] = matrix[i][5] - matrix[i][2];
        }
    }
    
    public static int indexSearch(int [][] matrix, int el, int col){
        for(int i = 0; i < matrix.length; i++){
            if(matrix[i][col] == el)
                return i;
        }
        return -1;
    }
    
    public static int min(int [][] matrix, int col){
        int min = matrix[0][col];
        for(int i = 0; i < matrix.length; i++)
                if(min > matrix[i][col])
                        min = matrix[i][col];
        return min;
    }
	
    public static int max(int [][] matrix, int col){
        int max = matrix[0][col];
        for(int i = 0; i < matrix.length; i++)
                if(max < matrix[i][col])
                        max = matrix[i][col];
        return max;
    }

    public static int sum(int [][] matrix, int col){
        int sum = 0;
        for(int i = 0; i < matrix.length; i++)
                sum += matrix[i][col];
        return sum;
    }
    
    public static double average(int [][] matrix, int col){
        return (double)sum(matrix,col)/matrix.length;
    }
}
