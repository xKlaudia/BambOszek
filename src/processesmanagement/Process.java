
package processesmanagement;

import java.io.IOException;
import memoryManagement.ExchangeFile;

public class Process {

		public PCB pcb = new PCB();
		private PriorityOverseer priorityOverseer = new PriorityOverseer();		
		private ProcessStateOverseer stateOverseer = new ProcessStateOverseer();		
		private int basePriority;

	protected void CreateProcess(int ID,String name, int priority, int processNumber) throws IOException
        {		
            if(priority>31 || priority<0)
            {
                pcb.BaseProcessPriority = priorityOverseer.RollPriority();		
		pcb.CurrentProcessPriority = pcb.BaseProcessPriority;	
            }
            else
            {				
		pcb.BaseProcessPriority = priority;		
		pcb.CurrentProcessPriority = priority;                
            }
            pcb.ProcessState = stateOverseer.ready;		
            pcb.ProcessID = ID;		
            pcb.ProcessName = name;	
            pcb.locked = false;
            pcb.receivedMsg="";
            pcb.A = 0;		
            pcb.B = 0;		
            pcb.C = 0;		
            pcb.D = 0;			
            pcb.commandCounter = 0;	
            pcb.howLongWaiting = 0;
            pcb.whenCameToList = processNumber;
            ExchangeFile E = new ExchangeFile();
            pcb.firstPageNumber = E.getExchangeFileLength()/16;                
            pcb.howManyPages = 0;
	}


	public void printInformations() 
        {
		System.out.println("------------------------------");
		System.out.println("ID - " + pcb.ProcessID);
		System.out.println("name - " + pcb.ProcessName);
		System.out.println("state - " + pcb.ProcessState);
		System.out.println("base priority - " + pcb.BaseProcessPriority);
		System.out.println("current priority - " + pcb.CurrentProcessPriority);
		System.out.println("waiting time - " + pcb.howLongWaiting);
                System.out.println("when came to list - " + pcb.whenCameToList);
		System.out.println("lock state - " + pcb.locked);
                System.out.println("first page number - " + pcb.firstPageNumber);
		System.out.println("number of pages - " + pcb.howManyPages);
                System.out.println("received message - " + pcb.receivedMsg);
		System.out.println("Register A - " + pcb.A);
		System.out.println("Register B - " + pcb.B);
		System.out.println("Register C - " + pcb.C);
		System.out.println("Register D - " + pcb.D);
		System.out.println("done command counter - " + pcb.commandCounter);
	}

	public int GetID() 
        {		
		return pcb.ProcessID;
	}

	public String GetName()
        {		
		return pcb.ProcessName;
	}

	public int GetState() 
        {			
		return pcb.ProcessState;
	}
		
	public void SetState(int State) 
        {			
		pcb.ProcessState = State;
	}

	public int GetBasePriority() 
        {
		return pcb.BaseProcessPriority;
	}
	
	public void SetBasePriority(int priority) 
        {
		pcb.BaseProcessPriority = priority;
	}

	public int GetCurrentPriority() 
        {	
		return pcb.CurrentProcessPriority;
	}
        
        public void SetCommandCounter(int commandCounter) 
        {
		pcb.commandCounter = commandCounter;
	}

	public int GetCommandCounter() 
        {	
		return pcb.commandCounter;
	}
		
	public void SetCurrentPriority(int Priority) 
        {		
		pcb.CurrentProcessPriority = Priority;
	}
	
	public int GetHowLongWaiting() 
        {		
		return pcb.howLongWaiting;
	}
		
	public void SetHowLongWaiting(int howLong) 
        {		
		pcb.howLongWaiting = howLong;
	}

	public boolean GetLocked() 
        {	
		return pcb.locked;
	}
	
	public void SetLocked() 
        {	
		pcb.locked = true;
	}
        
        public void SetUnlocked()
        {
            pcb.locked=false;
        }
        
        public String GetReceivedMsg() 
        {	
		return pcb.receivedMsg;
	}
	
	public void SetReceivedMsg(String wiadomosc) 
        {	
		pcb.receivedMsg = wiadomosc;
	}

	public PCB GetPCB() 
        {	
		return pcb;
	}
	
	public void SetPCB(PCB yourPCB) 
        {
		pcb = yourPCB;
	}
        
        public  long GetFirstPageNumber() throws IOException
        {
                return pcb.firstPageNumber; 
        }
        
        public void SetFirstPageNumber(long exchange) throws IOException 
        {
            pcb.firstPageNumber=exchange;
        }
        
        public int GetHowManyPages() 
        {			
                return pcb.howManyPages;
	}
		
	public void SetHowManyPages(int howMany) 
        {			
		pcb.howManyPages = howMany;
	}
        
        public int GetRegA() 
        {		
                return pcb.A;
	}
		
	public void SetRegA(int reg) 
        {		
		pcb.A = reg;
	}
         
        public int GetRegB() 
        {		
                return pcb.B;
	}
		
	public void SetRegB(int reg) 
        {		
		pcb.B = reg;
	}
         
        public int GetRegC() 
        {		
                return pcb.C;
	}
		
	public void SetRegC(int reg) 
        {		
		pcb.C = reg;
	}
          
        public int GetRegD() 
        {		
                return pcb.D;
	}
		
	public void SetRegD(int reg) 
        {		
		pcb.D = reg;
	}

}