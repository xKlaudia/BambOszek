package memoryManagement;

import java.util.Objects;

public class SecondChanceElement {
    private final int frameNumber;
    private boolean secondChance;
    
    public SecondChanceElement(int frameNumber, boolean secondChance) {
        this.frameNumber = frameNumber;
        this.secondChance = secondChance;
    }
    
    int getFrameNumber() {
        return this.frameNumber;
    }
    
    boolean getSecondChance() {
        return this.secondChance;
    }
    
    void setSecondChance(boolean secondChance) {
        this.secondChance = secondChance;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;
        if (!(object instanceof SecondChanceElement))
            return false;
        SecondChanceElement secondChanceElementObject = (SecondChanceElement)object;
        if (this.frameNumber == secondChanceElementObject.getFrameNumber() && this.secondChance == secondChanceElementObject.getSecondChance())
            return true;
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.frameNumber, this.secondChance);
    }
}