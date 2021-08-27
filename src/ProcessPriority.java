package org.demo;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author FAIZAN MOMBASAWALA
 */
public class ProcessPriority implements Comparable<ProcessPriority>{
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
    
}
