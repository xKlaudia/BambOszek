/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpudispatcher;

import java.util.LinkedList;
import java.util.Queue;
import processesmanagement.Process;

/**
 *
 * @author Osa
 */
public class PriorityQueues {
    private Queue [] queues;
    
    //inicjalizacja kolejek
    public PriorityQueues(){
        queues = new Queue[32];
        for(int i = 0; i < 32; i++) 
            queues[i] = new LinkedList<Process>(); 
    }
    
    public Queue getQueue(int i){
        return queues[i];
    }
    //dodaje proces do kolejki
    public void addProcess(Process p){
        queues[p.GetCurrentPriority()].add(p);
    }
 
    public Process findProcess(){
         for(int i = 31; i >= 0; i--){
             if(queues[i].isEmpty()){}
             else{
                 return (Process) queues[i].remove();
             }
         }
        return null;
    
    }
    //wyswietlanie informacji o aktualnym stanie kolejek
    public void printPriorities(){
        for(int i = 31; i >= 0; i--){
            if (queues[i].isEmpty() == true){
            // System.out.println(i+" xxx");
            }
            else{
                Process buff = (Process) queues[i].peek();
              //  System.out.println("Priorytet procesu na początku kolejki " + buff.GetCurrentPriority() );
                
                
            }
        }
        
    }
            
            //funkcja postarzania
    public void aging(){
        for(int i = 31; i >= 0; i--){
            if (queues[i].isEmpty() != true){
            while(queues[i].isEmpty()!=true){
                Process buff;
                buff  = (Process) queues[i].remove();
                buff.SetHowLongWaiting(buff.GetHowLongWaiting()+1);
            
                if (buff.GetCurrentPriority() >= 31)
                    buff.SetCurrentPriority(31);
                else
                    buff.SetCurrentPriority(buff.GetCurrentPriority()+buff.GetHowLongWaiting());
            
               // System.out.println("Postarzono " + buff.GetName() + " z " + (buff.GetBasePriority()) + " na " + buff.GetCurrentPriority());
                queues[buff.GetCurrentPriority()].add(buff);
            }
            
            
            }
    
        }

    }
}
