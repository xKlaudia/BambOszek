
package processesmanagement;

public class PCB {
	
	protected int ProcessID;
	protected String ProcessName;
	protected int ProcessState;
	protected int BaseProcessPriority;
	protected int CurrentProcessPriority;
	protected boolean blocked;
	protected int whenCameToList;
	protected int howLongWaiting;	
	public int commandCounter;
	public String receivedMsg;
        public long firstPageNumber;
	public int howManyPages;
	public int A;
	public int B;
	public int C;
	public int D;

}
