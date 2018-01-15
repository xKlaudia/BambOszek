/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpudispatcher;

/**
 *
 * @author Osa
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import processesmanagement.Process;

        
public abstract class CPUDispatcher implements Runnable{

    public static PriorityQueues queues = new PriorityQueues();
    
    //metoda dodawania gotowych procesów do kolejek
    public void addProcessToQueues(Process p){
        queues.addProcess(p);
    }
    
                @Override
                public void run() {
        try {
            this.startDispatcher();
        } catch (InterruptedException ex) {
            Logger.getLogger(CPUDispatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
                }
            
    //Metoda startująca działanie algorytmu
    public void startDispatcher() throws InterruptedException{
        System.out.println("---------------------------------------------");
     while (true){
            queues.printPriorities();
            //System.out.println("---------------------------------------------");
            for(int i = 31; i >= 0; i--){
                if (queues.getQueue(i).isEmpty() != true){
                    Process exec = (Process) queues.getQueue(i).remove();
                    System.out.println(exec.GetName() +" Priority " + exec.GetBasePriority());
                    exec.SetState(2);
                    System.out.println("Zmieniono stan " +exec.GetName()+" na "+exec.GetState());
                    //Thread.sleep(1000);
                    exec.SetState(4);
                    System.out.println("Zmieniono stan na " + +exec.GetState());
                    //System.out.println("---------------------------------------------");
                     
                    queues.aging();
                    //System.out.println("---------------------------------------------");
                    queues.printPriorities();
                    
                }
                else{
                    //System.out.println("Nie znaleziono procesu w kolejkach");
                }
            }
        }
    }
    
    
    
    }

