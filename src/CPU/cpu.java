/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CPU;

import Shell.Shell;
import fileSystem.FAT;
import interprocessCommunication.interprocessCommunication;
import java.util.LinkedList;
import memoryManagement.VirtualMemory;
import processesmanagement.PCB;
import processesmanagement.ProcessesManagement;

/**
 *
 * @author Szczepan
 */
public class cpu {
      private VirtualMemory memory;
    private interprocessCommunication communication;
    private ProcessesManagement manager;
    private FAT filesystem;
    private PCB PCB;            //Zmienna do kopii PCB procesu
    private processesmanagement.Process process;
    public void CPU() throws Exception
    {
        if(manager.processesList.size()!=0)
        {
            int max = 0; //manager.processesList.get(0).GetCurrentPriority();
            int highestProcessNumber = 0;
            LinkedList<String> kolejka =  new LinkedList<>();
            LinkedList<Integer> kolejka2 = new LinkedList<>();
            
            for(int i=0; i<manager.processesList.size(); i++)
            {
                if(manager.processesList.get(i).GetCurrentPriority()>max && manager.processesList.get(i).GetState() != 4 && !manager.processesList.get(i).GetLocked())
                {
                    max = manager.processesList.get(i).GetCurrentPriority();
                    highestProcessNumber = i;
                }
            }
            for (int i = 0; i < manager.processesList.size(); i++)
                if (manager.processesList.get(i).GetCurrentPriority()==max && manager.processesList.get(i).GetState() != 4 && !manager.processesList.get(i).GetLocked()) {
                    kolejka.add(manager.processesList.get(i).GetName());
                    kolejka2.add(i);
            }
            for (int i = 0; i < kolejka.size(); i++) {
                if (Shell.currentProcess.equals(kolejka.get(i))) {
                    if (i < kolejka.size() - 1) {
                        highestProcessNumber = kolejka2.get(i + 1);
                    }
                    else
                        highestProcessNumber = kolejka2.get(0);
                    break;
                }
            }
            manager.processesList.get(highestProcessNumber).SetState(2);
        
            for(int i=0; i<manager.processesList.size(); i++)
            {
                if(i!=highestProcessNumber)
                {
                    if(manager.processesList.get(i).GetCurrentPriority()<15 && !manager.processesList.get(i).GetName().equals("Idle"))
                        manager.processesList.get(i).SetCurrentPriority(manager.processesList.get(i).GetCurrentPriority()+1);
                    if(manager.processesList.get(i).GetState()==2) 
                        manager.processesList.get(i).SetState(1);
                }
            }
        }
        else throw new Exception("Processes list is empty");
    }
}
