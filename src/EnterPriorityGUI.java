package org.demo;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TextArea;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;

public class EnterPriorityGUI extends Application{
    
    //The GUI code for JavaFX
    @Override
    public void start(Stage primaryStage){
        //First Page
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        
        VBox upper_container = new VBox(20);
        upper_container.setAlignment(Pos.CENTER);
        upper_container.setPadding(new Insets(10));
        Label intro_message = new Label("Welcome to Preemptive Priority Scheduler");
        intro_message.setFont(Font.font("Sans Serif", FontWeight.BOLD, 30));
        upper_container.getChildren().add(intro_message);
        HBox starting = new HBox(10);
        starting.setAlignment(Pos.CENTER);
        starting.setPadding(new Insets(20));
        
        Text number_of_process = new Text("Enter number of processes: ");
        number_of_process.setFont(Font.font("Times New Roman", 20));
        starting.getChildren().add(number_of_process);
        TextField get_processes = new TextField();
        starting.getChildren().add(get_processes);
        upper_container.getChildren().add(starting);
        pane.setTop(upper_container);
        
        Button proceed = new Button("Proceed");
        pane.setCenter(proceed);
        
        proceed.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try{
                    VBox page2 = new VBox(20);
                    page2.setPadding(new Insets(20));
                    
                    Label L0 = new Label("Enter the respective values in the following boxes: ");
                    L0.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
                    page2.getChildren().add(L0);
                    
                    String temp = get_processes.getText();
                    int processes = Integer.parseInt(temp);
                    if(processes <= 0)
                        throw new NumberFormatException();
                    GridPane enter_processes = new GridPane();
                    enter_processes.setPadding(new Insets(20));
                    enter_processes.setAlignment(Pos.CENTER);
                    enter_processes.setHgap(10);  enter_processes.setVgap(5);
                    TextField[][] priorities = new TextField[processes][4];
                    
                    Label L1 = new Label("Process ID");
                    L1.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
                    enter_processes.add(L1, 0, 0);
                    
                    Label L2 = new Label("Arrival Time");
                    L2.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
                    enter_processes.add(L2, 1, 0);
                    
                    Label L3 = new Label("Service Time");
                    L3.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
                    enter_processes.add(L3, 2, 0);
                    
                    Label L4 = new Label("Priority");
                    L4.setFont(Font.font("Times New Roman", FontWeight.BOLD, 15));
                    enter_processes.add(L4, 3, 0);
                    
                    for(int i = 0; i < processes; i++){
                        for(int j = 0; j < 4; j++){
                            priorities[i][j] = new TextField();
                            priorities[i][j].setPrefColumnCount(1);
                            enter_processes.add(priorities[i][j], j, i+1);
                        }
                    }
                    
                    page2.getChildren().add(enter_processes);
                    StackPane temp2 = new StackPane();
                    Button proceed2 = new Button("Schedule");
                    temp2.getChildren().add(proceed2);
                    page2.getChildren().add(temp2);
                    
                    proceed2.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent e2) {
                            try{
                                int[][] priority = new int[processes][4];
                                for(int i = 0; i < processes; i++){
                                    for(int j = 0; j < 4; j++){
                                        priority[i][j] = Integer.parseInt(priorities[i][j].getText());
                                        if(priority[i][j] < 0)
                                            throw new IllegalArgumentException();
                                    }
                                }
                                
                                //check for distinct priority ID
                                ArrayList<Integer> DistinctValues = new ArrayList<>();
                                for(int i = 0; i < priority.length; i++){
                                    if(DistinctValues.contains(priority[i][0]))
                                        throw new IllegalArgumentException();
                                    else
                                        DistinctValues.add(priority[i][0]);
                                }
                                
                                int [][] computed_values = new int[processes][7];
                                sort(priority, 1); //Sort with respect to arrival time
                                for(int i = 0; i<priority.length; i++)
                                    System.arraycopy(priority[i], 0, computed_values[i], 0, priority[i].length);
                                
                                //Schedule the processes and get the scheduling logs
                                ArrayList<String> schedule_log;
                                schedule_log = PreemptivePriorityScheduler(priority,computed_values);
                                
                                //compute the wating time and turnaround time
                                computeWT_TAT(computed_values);
                                
                                
                                TableView table = new TableView();
                                Label label = new Label("Results: ");
                                label.setFont(new Font("Arial", 20));
                                
                                TableColumn col1 = new TableColumn("Process ID");
                                TableColumn col2 = new TableColumn("Arrival Time");
                                TableColumn col3 = new TableColumn("Service Time");
                                TableColumn col4 = new TableColumn("Priority");
                                TableColumn col5 = new TableColumn("Finish Time");
                                TableColumn col6 = new TableColumn("Turnaround Time");
                                TableColumn col7 = new TableColumn("Waiting Time");
                                TableColumn col8 = new TableColumn("Normalized TAT");
                                
                                col1.setCellValueFactory(new PropertyValueFactory<>("process_id"));
                                col2.setCellValueFactory(new PropertyValueFactory<>("arrival_time"));
                                col3.setCellValueFactory(new PropertyValueFactory<>("service_time"));
                                col4.setCellValueFactory(new PropertyValueFactory<>("priority"));
                                col5.setCellValueFactory(new PropertyValueFactory<>("finish_time"));
                                col6.setCellValueFactory(new PropertyValueFactory<>("tat"));
                                col7.setCellValueFactory(new PropertyValueFactory<>("wt"));
                                col8.setCellValueFactory(new PropertyValueFactory<>("ntat"));
                                
                                table.getColumns().addAll(col1, col2, col3, col4, col5, col6, col7, col8);
                                
                                for(int i = 0; i < processes; i++){
                                    double ntat = (int)((computed_values[i][5]/(double)computed_values[i][2]) * 100) / 100.00;
                                    PriorityTable row = new PriorityTable(computed_values[i][0], 
                                        computed_values[i][1], computed_values[i][2], computed_values[i][3], 
                                        computed_values[i][4], computed_values[i][5], computed_values[i][6],
                                        ntat);
                                    table.getItems().add(row);
                                }
                                
                                Button logs_button = new Button("View scheduling logs");
                                
                                VBox page3 = new VBox(20);
                                page3.setPadding(new Insets(30));
                                page3.getChildren().addAll(label, table, logs_button);
                                logs_button.setOnAction(e ->{
                                    TextArea logs = new TextArea();
                                    for(String str: schedule_log){
                                        logs.appendText(str+"\n");
                                    }
                                    page3.getChildren().add(logs);
                                });
                                    
                                Scene scene2 = new Scene(page3, 800, 400);
                                primaryStage.setScene(scene2);
                            }
                            catch(NumberFormatException ex2){
                                StackPane p4 = new StackPane();
                                Label warning2 = new Label("Please fill all the boxes correctly with integer values.");
                                p4.getChildren().add(warning2);
                                page2.getChildren().add(warning2);
                            }
                            catch(IllegalArgumentException ex1){
                                StackPane p4 = new StackPane();
                                Label warning2 = new Label("The process id should be distinct and all inputs must be positive.");
                                p4.getChildren().add(warning2);
                                page2.getChildren().add(warning2);
                            }
                        }
                    });
                    
                    Scene scene1 = new Scene(page2, 800, 400);
                    primaryStage.setScene(scene1);
                }
                catch(NumberFormatException ex){
                    StackPane p3 = new StackPane();
                    Label warning1 = new Label("Please enter a non-zero integer value in the box.");
                    p3.getChildren().add(warning1);
                    pane.setBottom(p3);
                }
                
            }
        });
        
        Scene scene = new Scene(pane,800,400);
        scene.setFill(Color.GREEN);
        primaryStage.setTitle("Preemptive Priority Scheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    //The scheduler which schedules the processes
    public static ArrayList<String> PreemptivePriorityScheduler(int [][] priority, int [][] computed_values){
        //A list of Process Priority class so that we can add processes dynamically
        //and sort the processes with the help of compareTo method in ProcessPriority class using Collections.sort
        ArrayList<ProcessPriority> process_list = new ArrayList<>();
        
        //Log of scheduling of processes
        ArrayList<String> schedule_log = new ArrayList<>();
        
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
                    schedule_log.add("Process "+priority[process_cnt][0]+" arrives.");
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
                schedule_log.add("Scheduler is idle because no process is there in main memory at "+cur_sec+" second.");
                ideal_processor++;
            }
            else{
                schedule_log.add("Process "+priority[cur_highest_priority_process][0]+" runs at "+cur_sec+" second.");
                priority[cur_highest_priority_process][2]--; //decrease the service time
                if(priority[cur_highest_priority_process][2] == 0){
                    schedule_log.add("Process "+priority[cur_highest_priority_process][0]
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
        return schedule_log;
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
    
    public static void main(String[] args){
        launch(args);
    }
    
    public static class PriorityTable{
        private final int process_id;
        private final int arrival_time;
        private final int service_time;
        private final int priority;
        private final int tat;
        private final double ntat;
        private final int wt;
        private final int finish_time;
        
        public PriorityTable(int p_id, int at, int st, int pri, int ft, int tat, int wt, double ntat){
            this.process_id = p_id;
            this.arrival_time = at;
            this.service_time = st;
            this.priority = pri;
            this.tat = tat;
            this.ntat = ntat;
            this.wt = wt;
            this.finish_time = ft;
        }
        
        public int getProcess_id(){ return process_id; }
        public int getArrival_time() { return arrival_time; }
        public int getService_time(){ return service_time; }
        public int getTat(){ return tat; }
        public double getNtat(){ return ntat; }
        public int getFinish_time(){ return finish_time; }
        public int getPriority(){ return priority; }
        public int getWt(){ return wt; }
        
        
    }
}

/*class ProcessPriority implements Comparable<ProcessPriority>{
    private int process_id;
    private int arrival_time;
    private int priority;
    
    public ProcessPriority(int pid, int at, int pri){
        process_id = pid;
        arrival_time = at;
        priority = pri;
    }
    
    public int getProcessID(){ return process_id; }
    public int getArrivalTime(){ return arrival_time; }
    public int getPriority(){ return priority; }
    
    @Override
    public int compareTo(ProcessPriority obj){
        if(this.priority < obj.getPriority())
            return -1;
        else if(this.priority > obj.getPriority())
            return 1;
        else{
            if(this.arrival_time > obj.getArrivalTime())
                return 1;
            else if(this.arrival_time < obj.getArrivalTime())
                return -1;
            else 
                return 0;
        }   
    }
    
    @Override
    public String toString(){
        return "\nProcess ID: "+process_id+
                "\nArrival Time: "+arrival_time
                +"\nPriority: "+priority;
    }
    
}*/