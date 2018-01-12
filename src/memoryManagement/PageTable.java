package memoryManagement;

public class PageTable {
    private Integer frameNumber[];
    private boolean valid[];
    
    /*Konstruktor*/
    public PageTable(int size) {
        if (size < 1) {
            frameNumber = new Integer[0];
            valid = new boolean[0];
        }
        else {
            frameNumber = new Integer[(size - 1) / 16 + 1];
            valid = new boolean[(size - 1) / 16 + 1];
            for (int i = 0; i < (size - 1) / 16 + 1; i++) {
                frameNumber[i] = null;
                valid[i] = false;
            }
        }
    }
    
    public Integer getFrameNumber(int index) {
        return frameNumber[index];
    }
    
    public boolean getValid(int index) {
        return valid[index];
    }
    
    public int getLength() {
        return frameNumber.length;
    }
    
    public void setFrameNumber(int index, Integer frameNumber) {
        this.frameNumber[index] = frameNumber;
    }
    
    public void setValid(int index, boolean valid) {
        this.valid[index] = valid;
    }
    
    /*Wyświetlanie tablicy stronic*/
    public void printPageTable() {
        System.out.println("Tablica stronic:");
        for (int i = 0; i < frameNumber.length; i++) {
            System.out.print("Stronica " + i + ": ");
            if (valid[i])
                System.out.println(frameNumber[i] + " 1");
            else
                System.out.println("- 0");
        }
    }
}