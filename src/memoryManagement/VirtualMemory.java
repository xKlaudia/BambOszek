package memoryManagement;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class VirtualMemory {
    private char virtualMemory[] = new char[128];
    private boolean freeFrames[] = new boolean[8];
    private LinkedList<String> processesNames = new LinkedList<>();
    private LinkedList<PageTable> pageTables = new LinkedList<>();
    private Queue<SecondChanceElement> secondChance = new LinkedList<>();
    private ExchangeFile exchangeFile = new ExchangeFile();
    
    public String currentProcess = "test"; //zmienna testowa
    
    public VirtualMemory() {
        for (int i = 0; i < 128; i++)
            virtualMemory[i] = ' ';
        for (int i = 0; i < 8; i++)
            freeFrames[i] = true;
    }
    
    public char readMemory(int logicalAddress) throws IOException {
        int pageTableIndex = 0;
        int firstPageNumber = 0;
        for (int i = 0; i < processesNames.size(); i++) {
            if (processesNames.get(i).equals(currentProcess)) { //Sprawdź nazwę aktualnie wykonywanego programu.
                pageTableIndex = i;
                break;
            }
            firstPageNumber += pageTables.get(i).getLength();
        }
        if (!(pageTables.get(pageTableIndex).getValid(logicalAddress / 16))) {
            boolean freeFrame = false;
            int freeFrameIndex = 0;
            for (int i = 0; i < freeFrames.length; i++) {
                if (freeFrames[i]) {
                    freeFrame = true;
                    freeFrameIndex = i;
                    break;
                }
            }
            if (freeFrame) {
                for (int i = 0; i < 16; i++)
                    virtualMemory[freeFrameIndex * 16 + i] = exchangeFile.readCharacterFromExchangeFile((long) ((firstPageNumber + logicalAddress / 16) * 16 + i)); //Sprawdź numer pierwszej stronicy.
                pageTables.get(pageTableIndex).setFrameNumber(logicalAddress / 16, freeFrameIndex);
                pageTables.get(pageTableIndex).setValid(logicalAddress / 16, true);
                secondChance.add(new SecondChanceElement(freeFrameIndex, true));
                freeFrames[freeFrameIndex] = false;
            }
            else {
                for (;;) {
                    if (!(secondChance.peek().getSecondChance())) {
                        Integer victimFrameNumber = secondChance.peek().getFrameNumber();
                        int victimPageNumber = 0;
                        boolean victimFrameFound = false;
                        for (int i = 0; i < pageTables.size(); i++) {
                            for (int j = 0; j < pageTables.get(i).getLength(); j++) {
                                if (pageTables.get(i).getValid(j) && victimFrameNumber.equals(pageTables.get(i).getFrameNumber(j))) {
                                    victimFrameFound = true;
                                    victimPageNumber += j; //Sprawdź numer pierwszej stronicy.
                                    pageTables.get(i).setFrameNumber(j, null);
                                    pageTables.get(i).setValid(j, false);
                                    break;
                                }
                            }
                            if (victimFrameFound)
                                break;
                            else
                                victimPageNumber += pageTables.get(i).getLength();
                        }
                        for (int i = 0; i < 16; i++) {
                            exchangeFile.writeCharacterToExchangeFile((long) (victimPageNumber * 16 + i), virtualMemory[victimFrameNumber * 16 + i]);
                            virtualMemory[victimFrameNumber * 16 + i] = exchangeFile.readCharacterFromExchangeFile((long) ((firstPageNumber + logicalAddress / 16) * 16 + i)); //Sprawdż numer pierwszej stronicy.
                        }
                        pageTables.get(pageTableIndex).setFrameNumber(logicalAddress / 16, victimFrameNumber);
                        pageTables.get(pageTableIndex).setValid(logicalAddress / 16, true);
                        secondChance.remove();
                        secondChance.add(new SecondChanceElement(victimFrameNumber, true));
                        break;
                    }
                    else {
                        int backupFrameNumber = secondChance.peek().getFrameNumber();
                        secondChance.remove();
                        secondChance.add(new SecondChanceElement(backupFrameNumber, false));
                    }
                }
            }
        }
        int frameNumber = pageTables.get(pageTableIndex).getFrameNumber(logicalAddress / 16);
        for (SecondChanceElement secondChanceElement : secondChance) {
            if (secondChanceElement.getFrameNumber() == frameNumber) {
                if (!(secondChanceElement.getSecondChance()))
                    secondChanceElement.setSecondChance(true);
                break;
            }
        }
        return virtualMemory[frameNumber * 16 + (logicalAddress % 16)];
    }
    
    public void writeMemory(int logicalAddress, char character) throws IOException {
        int pageTableIndex = 0;
        int firstPageNumber = 0;
        for (int i = 0; i < processesNames.size(); i++) {
            if (processesNames.get(i).equals(currentProcess)) { //Sprawdź nazwę aktualnie wykonywanego programu.
                pageTableIndex = i;
                break;
            }
            firstPageNumber += pageTables.get(i).getLength();
        }
        if (!(pageTables.get(pageTableIndex).getValid(logicalAddress / 16))) {
            boolean freeFrame = false;
            int freeFrameIndex = 0;
            for (int i = 0; i < freeFrames.length; i++) {
                if (freeFrames[i]) {
                    freeFrame = true;
                    freeFrameIndex = i;
                    break;
                }
            }
            if (freeFrame) {
                for (int i = 0; i < 16; i++)
                    virtualMemory[freeFrameIndex * 16 + i] = exchangeFile.readCharacterFromExchangeFile((long) ((firstPageNumber + logicalAddress / 16) * 16 + i)); //Sprawdź numer pierwszej stronicy.
                pageTables.get(pageTableIndex).setFrameNumber(logicalAddress / 16, freeFrameIndex);
                pageTables.get(pageTableIndex).setValid(logicalAddress / 16, true);
                secondChance.add(new SecondChanceElement(freeFrameIndex, true));
                freeFrames[freeFrameIndex] = false;
            }
            else {
                for (;;) {
                    if (!(secondChance.peek().getSecondChance())) {
                        Integer victimFrameNumber = secondChance.peek().getFrameNumber();
                        int victimPageNumber = 0;
                        boolean victimFrameFound = false;
                        for (int i = 0; i < pageTables.size(); i++) {
                            for (int j = 0; j < pageTables.get(i).getLength(); j++) {
                                if (pageTables.get(i).getValid(j) && victimFrameNumber.equals(pageTables.get(i).getFrameNumber(j))) {
                                    victimFrameFound = true;
                                    victimPageNumber += j; //Sprawdź numer pierwszej stronicy.
                                    pageTables.get(i).setFrameNumber(j, null);
                                    pageTables.get(i).setValid(j, false);
                                    break;
                                }
                            }
                            if (victimFrameFound)
                                break;
                            else
                                victimPageNumber += pageTables.get(i).getLength();
                        }
                        for (int i = 0; i < 16; i++) {
                            exchangeFile.writeCharacterToExchangeFile((long) (victimPageNumber * 16 + i), virtualMemory[victimFrameNumber * 16 + i]);
                            virtualMemory[victimFrameNumber * 16 + i] = exchangeFile.readCharacterFromExchangeFile((long) ((firstPageNumber + logicalAddress / 16) * 16 + i)); //Sprawdż numer pierwszej stronicy.
                        }
                        pageTables.get(pageTableIndex).setFrameNumber(logicalAddress / 16, victimFrameNumber);
                        pageTables.get(pageTableIndex).setValid(logicalAddress / 16, true);
                        secondChance.remove();
                        secondChance.add(new SecondChanceElement(victimFrameNumber, true));
                        break;
                    }
                    else {
                        int backupFrameNumber = secondChance.peek().getFrameNumber();
                        secondChance.remove();
                        secondChance.add(new SecondChanceElement(backupFrameNumber, false));
                    }
                }
            }
        }
        int frameNumber = pageTables.get(pageTableIndex).getFrameNumber(logicalAddress / 16);
        for (SecondChanceElement secondChanceElement : secondChance) {
            if (secondChanceElement.getFrameNumber() == frameNumber) {
                if (!(secondChanceElement.getSecondChance()))
                    secondChanceElement.setSecondChance(true);
                break;
            }
        }
        virtualMemory[frameNumber * 16 + (logicalAddress % 16)] = character;
    }
    
    public void printVirtualMemory(int address, int numberOfCharacters) {
        System.out.print("Pamięć wirtualna od adresu " + address + " do adresu " + (address + numberOfCharacters) + ": ");
        for (int i = 0; i < numberOfCharacters; i++)
            System.out.print(virtualMemory[address + i]);
        System.out.println();
    }
    
    public void printFreeFrames() {
        System.out.print("Wolne ramki:");
        for (int i = 0; i < freeFrames.length; i++) {
            System.out.print(" " + i + "(" + (freeFrames[i] ? "1)" : "0)"));
        }
        System.out.println();
    }
    
    public void printPageTable(String processName) {
        for (int i = 0; i < processesNames.size(); i++) {
            if (processesNames.get(i).equals(processName))
                pageTables.get(i).printPageTable();
        }
    }
    
    public void printSecondChance() {
        System.out.print("Kolejka algorytmu drugiej szansy:");
        for (SecondChanceElement secondChanceElement : secondChance)
            System.out.print(" " + secondChanceElement.getFrameNumber() + "(" + (secondChanceElement.getSecondChance() ? "1)" : "0)"));
        System.out.println();
    }
    
    public void loadProcess(String processName, String program, int size) throws IOException {
        processesNames.add(processName);
        pageTables.add(new PageTable(size));
        for (int i = 0; i < (size / 16 + 1) * 16; i++) {
            if (i < program.length())
                exchangeFile.writeCharacterToExchangeFile(exchangeFile.getExchangeFileLength(), program.charAt(i));
            else
                exchangeFile.writeCharacterToExchangeFile(exchangeFile.getExchangeFileLength(), ' ');
        }
    }
    
    public void deleteProcess(String processName) throws IOException {
        int index = 0;
        int frameNumber;
        int firstPageNumber = 0;
        int numberOfPages = 0;
        for (int i = 0; i < processesNames.size(); i++) {
            if (processesNames.get(i).equals(processName)) {
                index = i;
                numberOfPages = pageTables.get(i).getLength();
                break;
            }
            firstPageNumber += pageTables.get(i).getLength();
        }
        for (int i = 0; i < pageTables.get(index).getLength(); i++) {
            if (pageTables.get(index).getValid(i)) {
                frameNumber = pageTables.get(index).getFrameNumber(i);
                freeFrames[frameNumber] = true;
                if (secondChance.contains(new SecondChanceElement(frameNumber, false)))
                    secondChance.remove(new SecondChanceElement(frameNumber, false));
                if (secondChance.contains(new SecondChanceElement(frameNumber, true)))
                    secondChance.remove(new SecondChanceElement(frameNumber, true));
            }
        }
        processesNames.remove(index);
        pageTables.remove(index);
        exchangeFile.deleteProcessPages(firstPageNumber, numberOfPages); //Usuń stronice z pliku wymiany.
    }
}