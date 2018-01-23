
package processesmanagement;

public class ID_Overseer {

    private int FirstID = 0;
    private int currentID;
    private int ID;
    public ID_Overseer() {

	}
    public int PickID() {
        currentID=FirstID;
        ID=currentID;
        FirstID++;
        return ID;
    }
    
}
