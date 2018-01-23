
package processesmanagement;

public class PCB {
	
	protected int ProcessID;
	protected String ProcessName;
	protected int ProcessState;
	protected int BaseProcessPriority;
	protected int CurrentProcessPriority;
	protected boolean locked;
	protected int howLongWaiting;
        protected int whenCameToList;
	protected int commandCounter;
	protected String receivedMsg;
        protected long firstPageNumber;
	protected int howManyPages;
	protected int A;
	protected int B;
	protected int C;
	protected int D;

}
