package memoryManagement;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;

public class ExchangeFile {
    /*Tworzenie i czyszczenie pliku wymiany*/
    public void makeExchangeFile() throws IOException {
        PrintWriter printWriter = new PrintWriter("exchange_file.txt", "UTF-8");
        printWriter.close();
    }
    
    /*Otrzymywanie długości pliku wymiany*/
    public long getExchangeFileLength() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("exchange_file.txt", "r");
        long length = randomAccessFile.length();
        randomAccessFile.close();
        return length;
    }
    
    /*Czytanie z pliku wymiany*/
    public char readCharacterFromExchangeFile(long position) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("exchange_file.txt", "r");
        randomAccessFile.seek(position);
        char character = (char) randomAccessFile.readByte();
        randomAccessFile.close();
        return character;
    }
    
    /*Pisanie do pliku wymiany*/
    public void writeCharacterToExchangeFile(long position, char character) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("exchange_file.txt", "rw");
        randomAccessFile.seek(position);
        randomAccessFile.write((byte) character);
        randomAccessFile.close();
    }
    
    /*Usuwanie stronic z pliku wymiany*/
    public void deleteProcessPages(int firstPageNumber, int numberOfPages) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("exchange_file.txt", "rw");
        randomAccessFile.seek((firstPageNumber + numberOfPages) * 16);
        byte backup[] = new byte[(int) (getExchangeFileLength() - (firstPageNumber + numberOfPages) * 16)];
        randomAccessFile.read(backup);
        randomAccessFile.setLength((long) (firstPageNumber * 16 + backup.length));
        randomAccessFile.seek((long) (firstPageNumber * 16));
        randomAccessFile.write(backup);
        randomAccessFile.close();
    }
}