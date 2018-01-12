package memoryManagement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.StringBuilder;
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
    
    /*Konstruktor*/
    public VirtualMemory() throws IOException {
        //Inicjalizacja pamięci wirtualnej
        for (int i = 0; i < 128; i++)
            virtualMemory[i] = ' ';
        //Inicjalizacja tablicy wolnych ramek
        for (int i = 0; i < 8; i++)
            freeFrames[i] = true;
        //Tworzenie i czyszczenie pliku wymiany
        exchangeFile.makeExchangeFile();
    }
    
    /*Czytanie z pamięci wirtualnej*/
    public char readMemory(int logicalAddress) throws Exception, IOException {
        if (logicalAddress < 0)
            throw new Exception("Podano nieprawidłowy adres logiczny!");
        int pageTableIndex = 0;
        int firstPageNumber = 0;
        //Wyszukiwanie indeksu tablicy stronic po nazwie procesu
        for (int i = 0; i < processesNames.size(); i++) {
            if (processesNames.get(i).equals(currentProcess)) {
                if (logicalAddress > pageTables.get(i).getLength() * 16)
                    throw new Exception("Podano nieprawidłowy adres logiczny!");
                pageTableIndex = i;
                break;
            }
            firstPageNumber += pageTables.get(i).getLength();
        }
        if (!(pageTables.get(pageTableIndex).getValid(logicalAddress / 16))) {
            //Wykonywane gdy stronica nie znajduje się w ramce
            boolean freeFrame = false;
            int freeFrameIndex = 0;
            //Sprawdzanie czy któraś z ramek jest wolna
            for (int i = 0; i < freeFrames.length; i++) {
                if (freeFrames[i]) {
                    freeFrame = true;
                    freeFrameIndex = i;
                    break;
                }
            }
            if (freeFrame) {
                //Wykonywane jeśli któraś z ramek jest wolna. Przepisywanie stronicy do wolnej ramki
                for (int i = 0; i < 16; i++)
                    virtualMemory[freeFrameIndex * 16 + i] = exchangeFile.readCharacterFromExchangeFile((long) ((firstPageNumber + logicalAddress / 16) * 16 + i));
                //Ustawianie w tablicy stronic numeru zajętej przez stronicę ramki i bitu valid
                pageTables.get(pageTableIndex).setFrameNumber(logicalAddress / 16, freeFrameIndex);
                pageTables.get(pageTableIndex).setValid(logicalAddress / 16, true);
                secondChance.add(new SecondChanceElement(freeFrameIndex, true));
                //Ustawienie ramki jako zajętej
                freeFrames[freeFrameIndex] = false;
            }
            else {
                //Wykonywane jeśli żadna z ramek nie jest wolna
                for (;;) {
                    if (!(secondChance.peek().getSecondChance())) {
                        //Wykonywane jeśli bit valid jest ustawiony na 0
                        Integer victimFrameNumber = secondChance.peek().getFrameNumber();
                        int victimPageNumber = 0;
                        boolean victimFrameFound = false;
                        //Szukanie indeksu stronicy-ofiary
                        for (int i = 0; i < pageTables.size(); i++) {
                            for (int j = 0; j < pageTables.get(i).getLength(); j++) {
                                if (pageTables.get(i).getValid(j) && victimFrameNumber.equals(pageTables.get(i).getFrameNumber(j))) {
                                    victimFrameFound = true;
                                    victimPageNumber += j;
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
                            //Zapisywanie stronicy-ofiary do pliku wymiany
                            exchangeFile.writeCharacterToExchangeFile((long) (victimPageNumber * 16 + i), virtualMemory[victimFrameNumber * 16 + i]);
                            //Wczytywanie stronicy do pamięci wirtualnej
                            virtualMemory[victimFrameNumber * 16 + i] = exchangeFile.readCharacterFromExchangeFile((long) ((firstPageNumber + logicalAddress / 16) * 16 + i));
                        }
                        //Ustawianie w tablicy stronic numeru zajętej przez stronicę ramki i bitu valid
                        pageTables.get(pageTableIndex).setFrameNumber(logicalAddress / 16, victimFrameNumber);
                        pageTables.get(pageTableIndex).setValid(logicalAddress / 16, true);
                        secondChance.remove();
                        secondChance.add(new SecondChanceElement(victimFrameNumber, true));
                        break;
                    }
                    else {
                        //Wykonywanie algorytmu drugiej szansy
                        int backupFrameNumber = secondChance.peek().getFrameNumber();
                        secondChance.remove();
                        secondChance.add(new SecondChanceElement(backupFrameNumber, false));
                    }
                }
            }
        }
        int frameNumber = pageTables.get(pageTableIndex).getFrameNumber(logicalAddress / 16);
        //Ustawianie bitu odwołaniana na 1
        for (SecondChanceElement secondChanceElement : secondChance) {
            if (secondChanceElement.getFrameNumber() == frameNumber) {
                if (!(secondChanceElement.getSecondChance()))
                    secondChanceElement.setSecondChance(true);
                break;
            }
        }
        return virtualMemory[frameNumber * 16 + (logicalAddress % 16)];
    }
    
    /*Pisanie do pamięci wirtualnej*/
    public void writeMemory(int logicalAddress, char character) throws Exception, IOException {
        if (logicalAddress < 0)
            throw new Exception("Podano nieprawidłowy adres logiczny!");
        int pageTableIndex = 0;
        int firstPageNumber = 0;
        //Wyszukiwanie indeksu tablicy stronic po nazwie procesu
        for (int i = 0; i < processesNames.size(); i++) {
            if (processesNames.get(i).equals(currentProcess)) {
                if (logicalAddress > pageTables.get(i).getLength() * 16)
                    throw new Exception("Podano nieprawidłowy adres logiczny!");
                pageTableIndex = i;
                break;
            }
            firstPageNumber += pageTables.get(i).getLength();
        }
        if (!(pageTables.get(pageTableIndex).getValid(logicalAddress / 16))) {
            //Wykonywane gdy stronica nie znajduje się w ramce
            boolean freeFrame = false;
            int freeFrameIndex = 0;
            //Sprawdzanie czy któraś z ramek jest wolna
            for (int i = 0; i < freeFrames.length; i++) {
                if (freeFrames[i]) {
                    freeFrame = true;
                    freeFrameIndex = i;
                    break;
                }
            }
            if (freeFrame) {
                //Wykonywane jeśli któraś z ramek jest wolna. Przepisywanie stronicy do wolnej ramki
                for (int i = 0; i < 16; i++)
                    virtualMemory[freeFrameIndex * 16 + i] = exchangeFile.readCharacterFromExchangeFile((long) ((firstPageNumber + logicalAddress / 16) * 16 + i));
                //Ustawianie w tablicy stronic numeru zajętej przez stronicę ramki i bitu valid
                pageTables.get(pageTableIndex).setFrameNumber(logicalAddress / 16, freeFrameIndex);
                pageTables.get(pageTableIndex).setValid(logicalAddress / 16, true);
                secondChance.add(new SecondChanceElement(freeFrameIndex, true));
                //Ustawienie ramki jako zajętej
                freeFrames[freeFrameIndex] = false;
            }
            else {
                //Wykonywane jeśli żadna z ramek nie jest wolna
                for (;;) {
                    if (!(secondChance.peek().getSecondChance())) {
                        //Wykonywane jeśli bit valid jest ustawiony na 0
                        Integer victimFrameNumber = secondChance.peek().getFrameNumber();
                        int victimPageNumber = 0;
                        boolean victimFrameFound = false;
                        //Szukanie indeksu stronicy-ofiary
                        for (int i = 0; i < pageTables.size(); i++) {
                            for (int j = 0; j < pageTables.get(i).getLength(); j++) {
                                if (pageTables.get(i).getValid(j) && victimFrameNumber.equals(pageTables.get(i).getFrameNumber(j))) {
                                    victimFrameFound = true;
                                    victimPageNumber += j;
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
                            //Zapisywanie stronicy-ofiary do pliku wymiany
                            exchangeFile.writeCharacterToExchangeFile((long) (victimPageNumber * 16 + i), virtualMemory[victimFrameNumber * 16 + i]);
                            //Wczytywanie stronicy do pamięci wirtualnej
                            virtualMemory[victimFrameNumber * 16 + i] = exchangeFile.readCharacterFromExchangeFile((long) ((firstPageNumber + logicalAddress / 16) * 16 + i));
                        }
                        //Ustawianie w tablicy stronic numeru zajętej przez stronicę ramki i bitu valid
                        pageTables.get(pageTableIndex).setFrameNumber(logicalAddress / 16, victimFrameNumber);
                        pageTables.get(pageTableIndex).setValid(logicalAddress / 16, true);
                        secondChance.remove();
                        secondChance.add(new SecondChanceElement(victimFrameNumber, true));
                        break;
                    }
                    else {
                        //Wykonywanie algorytmu drugiej szansy
                        int backupFrameNumber = secondChance.peek().getFrameNumber();
                        secondChance.remove();
                        secondChance.add(new SecondChanceElement(backupFrameNumber, false));
                    }
                }
            }
        }
        int frameNumber = pageTables.get(pageTableIndex).getFrameNumber(logicalAddress / 16);
        //Ustawianie bitu odwołaniana na 1
        for (SecondChanceElement secondChanceElement : secondChance) {
            if (secondChanceElement.getFrameNumber() == frameNumber) {
                if (!(secondChanceElement.getSecondChance()))
                    secondChanceElement.setSecondChance(true);
                break;
            }
        }
        virtualMemory[frameNumber * 16 + (logicalAddress % 16)] = character;
    }
    
    /*Wyświetlanie pamięci wirtualnej*/
    public void printVirtualMemory(int address, int numberOfCharacters) throws Exception {
        if (address < 0 || address > 127 || numberOfCharacters < 0 || address + numberOfCharacters > 127)
            throw new Exception("Podano nieprawidłowe argumenty!");
        System.out.print("Pamięć wirtualna od adresu " + address + " do adresu " + (address + numberOfCharacters) + ": ");
        for (int i = 0; i < numberOfCharacters; i++)
            System.out.print(virtualMemory[address + i]);
        System.out.println();
    }
    
    /*Wyświetlanie tablicy wolnych ramek*/
    public void printFreeFrames() {
        System.out.print("Wolne ramki:");
        for (int i = 0; i < freeFrames.length; i++) {
            System.out.print(" " + i + "(" + (freeFrames[i] ? "1)" : "0)"));
        }
        System.out.println();
    }
    
    /*Wyświetlanie tablicy stronic*/
    public void printPageTable(String processName) {
        for (int i = 0; i < processesNames.size(); i++) {
            if (processesNames.get(i).equals(processName))
                pageTables.get(i).printPageTable();
        }
    }
    
    /*Wczytywanie procesu do pliku wymiany*/
    public void loadProcess(String processName, String program, int size) throws Exception, IOException {
        if (size < 1)
            throw new Exception("Podano nieprawidłowy rozmiar!");
        processesNames.add(processName);
        pageTables.add(new PageTable(size));
        //Wczytywanie kodu programu
        String programCode;
        String line;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(program));
        StringBuilder stringBuilder = new StringBuilder();
        line = bufferedReader.readLine();
        while (line != null) {
            stringBuilder.append(line);
            stringBuilder.append(" ");
            line = bufferedReader.readLine();
        }
        programCode = stringBuilder.toString();
        bufferedReader.close();
        if (size < programCode.length())
            throw new Exception("Podano za mały rozmiar!");
        for (int i = 0; i < (size / 16 + 1) * 16; i++) {
            if (i < programCode.length())
                exchangeFile.writeCharacterToExchangeFile(exchangeFile.getExchangeFileLength(), programCode.charAt(i));
            else
                exchangeFile.writeCharacterToExchangeFile(exchangeFile.getExchangeFileLength(), ' ');
        }
    }
    
    /*Usuwanie procesu z pliku wymiany*/
    public void deleteProcess(String processName) throws IOException {
        int index = 0;
        int frameNumber;
        int firstPageNumber = 0;
        int numberOfPages = 0;
        //Szukanie tablicy stronic
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
        exchangeFile.deleteProcessPages(firstPageNumber, numberOfPages);
    }
}