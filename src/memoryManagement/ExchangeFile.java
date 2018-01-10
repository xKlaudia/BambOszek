package memoryManagement;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;

public class ExchangeFile {
        public long getExchangeFileLength() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("exchange_file.txt", "r");
        long length = randomAccessFile.length();
        randomAccessFile.close();
        return length;
    }
    
    public char readCharacterFromExchangeFile(long position) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("exchange_file.txt", "r");
        randomAccessFile.seek(position);
        char character = (char) randomAccessFile.readByte();
        randomAccessFile.close();
        return character;
    }
    
    public void writeCharacterToExchangeFile(long position, char character) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("exchange_file.txt", "rw");
        randomAccessFile.seek(position);
        randomAccessFile.write((byte) character);
        randomAccessFile.close();
    }
    
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