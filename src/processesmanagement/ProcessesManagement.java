package processesmanagement;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import memoryManagement.VirtualMemory;
import memoryManagement.ExchangeFile;

public class ProcessesManagement extends Process {

	// TO JEST KLASA Z METODAMI DLA WAS
	
	//***ZMIENNE*****************************************************************************************
	
	public List<Process> processesList;        
	private ID_Overseer idoverseer;	
	private ProcessStateOverseer stateOverseer;	
	private List<Integer> finishedProcessList;	
	VirtualMemory virtualMemory;        
        private int processNumber=0;
	//---Konstruktor-------------------------------------------------------------------------------------
	
	public ProcessesManagement(VirtualMemory virtualMemory) throws IOException 
        {
        this.virtualMemory = new VirtualMemory();
		this.virtualMemory = virtualMemory;
		processesList = new LinkedList<Process>();
		idoverseer = new ID_Overseer();
		stateOverseer = new ProcessStateOverseer();
		finishedProcessList = new LinkedList<Integer>();
	}
	
	//---Dodaj/Usun Procesy-------------------------------------------------------------------------------
	
	/*public int NewProcess_XC(String Name, int priority) throws IOException
        {
		//int i = FindProcessWithName(Name);
		  
		  /*if(i != -1) {
		   
		   System.out.println("You can't create two programs with the same name");
		   return -1;
		  }

		  Process process = new Process();
		  int id = idoverseer.PickID();
		  String s = Integer.toString(id);
		  process.CreateProcess(id,Name, priority, processNumber);
		  processesList.add(process); 
                  processNumber++;
		  CheckStates();
		 return 0;
	}*/
        
        public int NewProcess_XC(String Name,int priority) throws IOException
        {
		int i = FindProcessWithName(Name);
		  
		  if(i != -1) {		   
		   System.out.println("You can't create two programs with the same name");
		   return -1;
		  }

		  Process process = new Process();
		  int id = idoverseer.PickID();
		  String s = Integer.toString(id);
		  process.CreateProcess(id,Name, priority, processNumber);
		  processesList.add(process); 
                  processNumber++;
		  CheckStates();
		 return 0;
	}

	public  Process NewIdleProcess() throws IOException 
        {
		Process process = new Process();
                int id = idoverseer.PickID();
		process.CreateProcess(id, "Idle",0,0);
		process.SetBasePriority(0);
		process.SetCurrentPriority(0);
                processesList.add(process); 
		CheckStates();
		return process;
	}

	private void  DeleteProcess() throws IOException 
        {
		for (int i = 0; i < finishedProcessList.size(); i++) {
                        
			int index = FindProcessWithID(finishedProcessList.get(i));
			virtualMemory.deleteProcess(GetNameWithID(finishedProcessList.get(i))); 
                        for(int j=0;j<processesList.size();j++)
                        {
                            if(processesList.get(index).GetFirstPageNumber()<processesList.get(j).GetFirstPageNumber())
                            {
                                processesList.get(j).SetFirstPageNumber(processesList.get(j).GetFirstPageNumber()-processesList.get(index).GetHowManyPages());
                            }
                        }
                        
			processesList.remove(index);
		}
		finishedProcessList.clear();
	}
        public void DeleteProcessWithID(int ID) throws IOException 
        {
                for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                        processesList.get(i).SetState(4);
                        CheckStates();
                    }
                }

	}

	public void CheckStates() throws IOException 
        {
		for (int i = 0; i < processesList.size(); i++) {
			if(processesList.get(i).pcb.ProcessState == stateOverseer.finished) {
				finishedProcessList.add(processesList.get(i).pcb.ProcessID);
			}
		}
		
		DeleteProcess();
	}
	
	//---Szukanie procesow-----------------------------------------------------------------------

	public int FindProcessWithID(int ID) 
        {
		Process proces_kopia;
		
		for(int i = 0; i < processesList.size(); i++) {
			proces_kopia = processesList.get(i);
			if(proces_kopia.GetID() == ID) {
				return i;
			}	
		}
		
		return -1;
	}

	public int FindProcessWithName(String name) 
        {
		for(int i = 0; i < processesList.size(); i++) {
			if(processesList.get(i).GetName().equals(name)) {
				return i;
			}	
		}
		return -1;
	}
        
	//-----Get/Set---------------------------------------------------------

	public int GetIDwithName(String name) 
        {
            int Identyfikator=-1;
            int pom=0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetName()==name)
                    {
                        Identyfikator = processesList.get(i).GetID();
                        pom++;
                    }
                }
            if(pom>1)
                System.out.println("Found more processes with the same name! Only the ID of last one is going to be returned");
            return Identyfikator;
	}

	public String GetNameWithID(int ID)
        {
            String nazwa = "Brak procesu o takim ID";
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                        nazwa = processesList.get(i).GetName();     
                    }
                }
                return nazwa;
	}

	public int GetStateWithID(int ID) 
        {
            int stan=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       stan= processesList.get(i).GetState();     
                    }
                }

		return stan;
	}
        
        public int GetStateWithName(String Name) 
        {
            int stan=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetName()==Name)
                    {
                       stan= processesList.get(i).GetState();     
                    }
                }

		return stan;
	}
		
	public void SetState(int ID, int State) 
        {
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       int pomoc=processesList.get(i).GetState();
                       if(pomoc==0)
                       {
                           if(State==1)
                           {
                               processesList.get(i).SetState(State);
                           }
                           else if(State!=1)
                               System.out.println("You cant set this state!");
                       }
                       else if(pomoc==1)
                       {
                           if(State==2)
                           {
                               processesList.get(i).SetState(State);
                           }
                           else if(State!=2)
                               System.out.println("You cant set this state!");
                       }
                       else if(pomoc==2)
                       {
                           if(State==3)
                           {
                               processesList.get(i).SetState(State);
                           }
                           else if(State==4)
                           {
                               processesList.get(i).SetState(State);
                           }
                           else if(State!=3 || State!=4)
                               System.out.println("You cant set this state!");                               
                       }
                       else if(pomoc==3)
                       {
                           if(State==1)
                            {
                               processesList.get(i).SetState(State);
                            }
                           else if(State!=1)
                               System.out.println("You cant set this state!");
                       }
                    }
                }

	}

	public int GetBasePriorityWithID(int ID)
        {
		  int priorytet=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       priorytet= processesList.get(i).GetBasePriority();     
                    }
                }

		return priorytet;
	}
	
	public void SetBasePriorityWithID(int ID, int priority) 
        {
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetBasePriority(priority);     
                    }
                }

	}

	public int GetCurrentPrirityWithID(int ID)
        {
		  int priorytet=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       priorytet= processesList.get(i).GetCurrentPriority();     
                    }
                }

		return priorytet;
	}
		
	public void SetCurrentPririty(int ID, int priority)
        {
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                        if(processesList.get(i).GetCurrentPriority()<16)
                            processesList.get(i).SetCurrentPriority(priority);
                        else if(processesList.get(i).GetCurrentPriority()>16)
                            System.out.println("You cannot change realt time process priority");
                    }
                }
	}

	public int GetHowLongWaitingWithID(int ID) 
        {
		  int jakDlugo=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       jakDlugo= processesList.get(i).GetHowLongWaiting();     
                    }
                }

		return jakDlugo;	
		
	}
		
	public void SetHowLongWaitingWithID(int ID, int howLong)
        {			
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetHowLongWaiting(howLong);     
                    }
                }
	}
        public int GetHowManyPagesWithID(int ID) 
        {
		  int jakDuzo=-1;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       jakDuzo= processesList.get(i).GetHowManyPages();     
                    }
                }

		return jakDuzo;			
	}
		
	public void SetHowManyPagesWithID(int ID, int howMany) 
        {			
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetHowManyPages(howMany);     
                    }
                }
	}

	public boolean GetLockedWithID(int ID)
        {		
		  boolean zablokowany=false;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       zablokowany= processesList.get(i).GetLocked();     
                    }
                }

		return zablokowany;
	}
	
	public void SetLockedWithID(int ID) 
        {		
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetLocked();     
                    }
                }
	}
        
        public void SetUnlockedWithID(int ID) 
        {		
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetUnlocked();     
                    }
                }
	}

	public PCB GetPCBWithName(String name) 
        {
            int pomoc=0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetName()==name)
                    {
                      pomoc=i;  
                    }
                }
                return processesList.get(pomoc).pcb; 
	}

	public void printProcessListInformations() 
        {		
            System.out.println("------------------------------");
            System.out.println("Processes list:");
            System.out.println("Normal: ");
            for(int i = 0; i < processesList.size(); i++) 
            {
                if(processesList.get(i).GetState()!=3)
                {
                    if(processesList.get(i).GetCurrentPriority()<=15)           
                    {
                        System.out.println( i + ". " + processesList.get(i).GetID() + ", " + processesList.get(i).GetName());
                    }
                }      
            }
            
            System.out.println("Real Time: ");
            for(int i = 0; i < processesList.size(); i++) 
            {
                if(processesList.get(i).GetState()!=3)
                {
                    if(processesList.get(i).GetCurrentPriority()>15)           
                    {
                        System.out.println( i + ". " + processesList.get(i).GetID() + ", " + processesList.get(i).GetName());
                    }
                }                                     
            }
	}
        
        public void printWaitingProcessListInformations() 
        {		
            System.out.println("------------------------------");
            System.out.println("Waiting Processes list:");
            System.out.println("Normal: ");
            for(int i = 0; i < processesList.size(); i++) 
            {
                if(processesList.get(i).GetState()==3)
                {
                    if(processesList.get(i).GetCurrentPriority()<=15)           
                    {
                        System.out.println( i + ". " + processesList.get(i).GetID() + ", " + processesList.get(i).GetName());
                    }
                }
            }
            
            System.out.println("Real Time: ");
            for(int i = 0; i < processesList.size(); i++) 
            {
                if(processesList.get(i).GetState()==3)
                {
                    if(processesList.get(i).GetCurrentPriority()>15)           
                    {
                        System.out.println( i + ". " + processesList.get(i).GetID() + ", " + processesList.get(i).GetName());
                    }
                }
            }
	}

	public void printProcessInformations(int ID) 
        {
                int a=0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                      processesList.get(i).printInformations();
                      a++;
                    }
                    
                }
                if (a==0)
			System.out.println("This process does not exist");
	}

	public Process getProcess(String name) 
        {
		return processesList.get(FindProcessWithName(name));
	}
        
        public Process GetProcessWithID(int ID)
        {
            Process proces_pom = null;
            for(int i=0;i<processesList.size();i++)
            {
                if(processesList.get(i).GetID()==ID)
                {
                    proces_pom=processesList.get(i);
                }
            }
            return proces_pom;
        }
        
        public long GetFirstPageNumberWithID(int ID) throws IOException 
        {		
		  long pierwszastrona=0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       pierwszastrona= processesList.get(i).GetFirstPageNumber();     
                    }
                }

		return pierwszastrona;
	}
        
        public void SetFirstPageNumberWithID(int ID,long firstPageNumber) throws IOException 
        {	
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       processesList.get(i).SetFirstPageNumber(firstPageNumber);     
                    }
                }
	}
        
        public String GetReceivedMsgWithID(int ID)  
        {		
		  String wiadomosc="";
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       wiadomosc= processesList.get(i).GetReceivedMsg();     
                    }
                }

		return wiadomosc;
	}
        
        public void SetReceivedMsgWithID(int ID,String wiadomosc) throws IOException 
        {	
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                      processesList.get(i).SetReceivedMsg(wiadomosc);     
                    }
                }
	}
        
        public int GetRegAwithID(int ID)  
        {		
		int value=0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       value= processesList.get(i).GetRegA();     
                    }
                }

		return value;
	}
        
        public void SetRegAwithID(int ID, int reg)
        {	
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                      processesList.get(i).SetRegA(reg);     
                    }
                }
	}
         
        public int GetRegBwithID(int ID)  
        {		
		int value=0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       value= processesList.get(i).GetRegB();     
                    }
                }

		return value;
	}
        
        public void SetRegBwithID(int ID, int reg)
        {	
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                      processesList.get(i).SetRegB(reg);     
                    }
                }
	}
         
        public int GetRegCwithID(int ID)  
        {		
		int value=0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       value= processesList.get(i).GetRegC();     
                    }
                }

		return value;
	}
        
        public void SetRegCwithID(int ID, int reg)
        {	
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                      processesList.get(i).SetRegC(reg);     
                    }
                }
	}
         
        public int GetRegDwithID(int ID)  
        {		
		int value=0;
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                       value= processesList.get(i).GetRegD();     
                    }
                }

		return value;
	}
        
        public void SetRegDwithID(int ID, int reg)
        {	
		for(int i=0;i<processesList.size();i++)
                {
                    if(processesList.get(i).GetID()==ID)
                    {
                      processesList.get(i).SetRegD(reg);     
                    }
                }
	}
}
