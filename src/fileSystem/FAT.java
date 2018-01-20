package fileSystem;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.*;
import fileSystemExceptions.*;
import processesmanagement.Process;

public class FAT {
	static final int DISK_SIZE = 1024;
	static final int BLOCK_SIZE = 32;
	static final int BLOCKS = DISK_SIZE / BLOCK_SIZE;
	private static final int BLOCK_ERROR = -1, LAST_BLOCK = -1;
	private static final char EMPTY_BYTE = '#';
	private static final File EMPTY_FILE = new File();
	
	private static char[] disk;
	private static int[] FAT;
	private static boolean[] FreeBlocks;
	private static Vector<File> mainCatalog;
	
	public FAT() {
		try {
			disk = new char[DISK_SIZE];
			FAT = new int[BLOCKS];
			FreeBlocks = new boolean[BLOCKS];
			mainCatalog = new Vector<File>();
			
			Arrays.fill(disk, EMPTY_BYTE);
			Arrays.fill(FAT, LAST_BLOCK);
			Arrays.fill(FreeBlocks, true);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public boolean AppendToFile(String fullName, String content) throws Exception {
		if(!DoesFileExist(fullName)) throw new Exception("Nie znaleziono pliku");
		else {
			int contentSize;
			int firstBlock = FilesFirstBlock(fullName);
			int filesFreeSpace;
			
			if(firstBlock == BLOCK_ERROR) throw new Exception("Nie znaleziono pierwszego bloku");
			else { 
				filesFreeSpace = FilesFreeSpace(fullName);
				if (filesFreeSpace == BLOCK_ERROR) throw new Exception("Nie znaleziono pliku.");
				else {
					contentSize = content.length();
					if((FreeBlocksSpace() + filesFreeSpace) < contentSize) throw new OutOfBlocksException("Brak miejsca na dysku");
					else {
						int firstEmptyChar = -1;
						int lastBlock = firstBlock;
						int charToWrite = 0;
						
						while(FAT[lastBlock] != LAST_BLOCK) {
							lastBlock = FAT[lastBlock];
						}
						
						for(int i=0; i<BLOCK_SIZE; i++) {
							if(disk[i+lastBlock*BLOCK_SIZE] == '#') {
								firstEmptyChar = i;
								break;
							}
						}
						if(firstEmptyChar != -1) {
							for(int i=firstEmptyChar; i<BLOCK_SIZE && contentSize != 0; i++) {
								disk[i+BLOCK_SIZE*lastBlock] = content.charAt(charToWrite);
								charToWrite++;
								contentSize--;
							}
						}
						if(contentSize != 0) {
							String unwrittenContent = content.substring(content.length()-contentSize);
							FAT[lastBlock] = NewFileRecord(fullName, unwrittenContent);
						}
					}
				}
			} 				
		}
		RefreshSize(fullName);
		return true;
	}
	
	public void CloseFile(String fullName, Process process) throws Exception {
		if(!DoesFileExist(fullName)) throw new Exception("Brak podanego pliku");
		else {
			for(File file : mainCatalog) {
				if(file.GetFullName().equals(fullName)) {
					file.lock.unlock(process);
					file.SetReadChars(0);
				}
			}
		}
	}
	
	public boolean CreateEmptyFile(String fullName) throws Exception {
		if(!CheckFileName(fullName)) {
			throw new IllegalFileNameException("Podano nieprawidlowa nazwe. Nazwa musi zawierac 1 do 8 znakow (male, duze litery i cyfry) + kropke i nazwe rozszerzenia (txt)");
		}
		else if(DoesFileExist(fullName)) {
			throw new IllegalFileNameException("Istnieje plik o podanej nazwie");
		}
		else {
			int freeBlock = FindFreeBlock();
			if(freeBlock == BLOCK_ERROR) throw new OutOfBlocksException("Brak miejsca na dysku");
			else if(freeBlock < BLOCK_ERROR || freeBlock > (BLOCKS-1)) throw new Exception("Blad algorytmu");
			else {
				File file = new File(fullName, freeBlock, 0);
				FAT[freeBlock] = LAST_BLOCK;
                                FreeBlocks[freeBlock] = false;
				mainCatalog.add(file);			
			}
		}
		return true;
	}
		
	public boolean CreateNewFile(String fullName, String content) throws Exception {
		if(!CheckFileName(fullName)) {
			throw new IllegalFileNameException("Podano nieprawidlowa nazwe. Nazwa musi zawierac 1 do 8 znakow (male, duze litery i cyfry) + kropke i nazwe rozszerzenia (txt)");
		}
		else if(DoesFileExist(fullName)) {
			throw new IllegalFileNameException("Istnieje plik o podanej nazwie");
		}
		else {
			try {
				NewFileRecord(fullName, content);
				RefreshSize(fullName);
			}
			catch(Exception e) {
				throw e;
			}
		}
		return true;
	}
	
	public void DeleteFile(String fullName) throws Exception {
		if(!DoesFileExist(fullName)) throw new Exception("Brak pliku o podanej nazwie");
		else {
			int blockToDelete = FilesFirstBlock(fullName);
			int nextBlockToDelete;
			
			do {
				nextBlockToDelete = FAT[blockToDelete];
				FAT[blockToDelete] = LAST_BLOCK;
				for(int i=0; i<BLOCK_SIZE; i++) disk[i+BLOCK_SIZE*blockToDelete] = EMPTY_BYTE;
				FreeBlocks[blockToDelete] = true;
				blockToDelete = nextBlockToDelete;
			} while(blockToDelete != LAST_BLOCK);
			
			for(int i=0; i<mainCatalog.size(); i++) {
				if(mainCatalog.get(i).GetFullName().equals(fullName)) {
					mainCatalog.remove(i);
					break;
				}
			}
		}
	}
	
	public boolean DoesFileExist(String fullName) {
		for(int i=0; i<mainCatalog.size(); i++) {
			if(mainCatalog.get(i).GetFullName().equals(fullName)) return true;
		}
		return false;
	}
	public int[] GetFAT() {
		return FAT;
	}
	public boolean[] GetFreeBlocks() {
		return FreeBlocks;
	}
	
	public void PrintDisk() {
		int blockNr = 1;
		System.out.println("Ilosc wolnego miejsca na dysku " + CountFreeBlocks()*BLOCK_SIZE);
		for(int i=0; i<disk.length; i++) {
			if(i%BLOCK_SIZE == 0) {
				blockNr = (int) i/BLOCK_SIZE;
				System.out.print("\nBlok " + blockNr + "Nastepny: " + FAT[i/BLOCK_SIZE] + "Plik: " + "<>");
			}
			System.out.print(disk[i]);
		}
		System.out.println(" ");
	}
	
	public void OpenFile(String fullName, Process process) throws Exception {
		if(!DoesFileExist(fullName)) throw new Exception("Brak podanego pliku");
		else {
			for(File file : mainCatalog) {
				if(file.GetFullName().equals(fullName)) file.lock.lock(process);
			}
		}
	}
	
	public String GetFilesContent(String fullName) throws Exception {
		if(DoesFileExist(fullName)) {
			int blockToRead = FilesFirstBlock(fullName);
			String content = "";
			int fileSize = GetFile(fullName).GetSize();
			do {
				for(int i=0; i<BLOCK_SIZE && fileSize!=0; i++) {
					content += disk[i+blockToRead*BLOCK_SIZE];
					fileSize--;
				}
				if(FAT[blockToRead] != LAST_BLOCK && fileSize != 0) {
					blockToRead = FAT[blockToRead];
				}
			} while (fileSize != 0);
			return content;
		}
		else throw new Exception("Plik nie istnieje");
	}
        
        public String GetFilesContent(String fullName, int howManyChars) throws Exception {
            if(DoesFileExist(fullName)) {
                        int currentReadChars = GetFile(fullName).GetReadChars();
			int blockToRead = FilesFirstBlock(fullName);
			String content = "";
			int fileSize = GetFile(fullName).GetSize();
                        
                        if((howManyChars + currentReadChars) > fileSize)
                            throw new Exception("Liczba znakow do przeczytania przekracza rozmiar pliku");
                        while(currentReadChars >= BLOCK_SIZE) {
                            blockToRead = FAT[blockToRead];
                            currentReadChars -= BLOCK_SIZE;
                        }
			do {
				for(int i=0; i<BLOCK_SIZE && fileSize!=0 && howManyChars!=0; i++) {
					content += disk[i+blockToRead*BLOCK_SIZE];
					fileSize--;
                                        howManyChars--;
				}
				if(FAT[blockToRead] != LAST_BLOCK && fileSize != 0 && howManyChars!=0) {
					blockToRead = FAT[blockToRead];
				}
			} while (howManyChars != 0);
			return content;
		}
		else throw new Exception("Plik nie istnieje");
        } 
	
	public void ShowFileInfo(String fullName) throws Exception {
		if(DoesFileExist(fullName)) {
			File file;
			try {
				file = GetFile(fullName);
			} catch (Exception e) {
				throw e;
			}
			try {
				int sizeAtDisk = CountFilesBlocks(file.GetFirstBlock()) * BLOCK_SIZE;
				System.out.println("Nazwa pliku: " + file.GetFullName()
						+ "\nZawartosc: " + GetFilesContent(fullName) 
						+ "\nBlok pierwszy: " + file.GetFirstBlock()
						+ "\nRozmiar: " + file.GetSize()
						+ "\nRozmiar na dysku: " + sizeAtDisk);
			} catch (Exception e) {
				throw e;
			}
		}
		else throw new Exception("Brak pliku");
	}
	/* zwraca czy nazwa jest poprawna */
	private boolean CheckFileName(String input) {
		/* format nazwy: nazwa.txt, gdzie nazwa max. 3 znaki */
		String pattern = "^[a-zA-Z0-9]{1,3}[.]txt";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(input);
		boolean matches = matcher.matches();
		
		if(!matches) {
			return false;
		}
		else return true;
	}
	
	private int CountFilesBlocks(int firstBlock) {
		int counter = 0;
		while(firstBlock != LAST_BLOCK) {
			firstBlock = FAT[firstBlock];
			counter++;
		}
		return counter;
	}
	private int CountFreeBlocks(){
		int countFreeBlocks = 0;
		for(int i=0; i<FreeBlocks.length; i++) {
			if(FreeBlocks[i] == true) countFreeBlocks++;
		}
		return countFreeBlocks;
	}
	
	private int FilesFreeSpace(String fullName) {
		if(DoesFileExist(fullName)) {
			int blockToRead = FilesFirstBlock(fullName);
			int counter = 0;
			for(;;) {
				for(int i=0; i<BLOCK_SIZE; i++) {
					if (disk[i+blockToRead*BLOCK_SIZE] == '#') counter++;
				}
				if(FAT[blockToRead] == LAST_BLOCK) break;
				else blockToRead = FAT[blockToRead];
			}
			return counter;
		}
		else return BLOCK_ERROR;
	}
	private int FindFreeBlock() {
		for(int i=0; i<FreeBlocks.length; i++) {
			if(FreeBlocks[i] == true) return i;
		}
		return BLOCK_ERROR;
	}
	
	private int FilesFirstBlock(String fullName) {
		if(DoesFileExist(fullName)) {
			for(File file : mainCatalog) {
				if(file.GetFullName().equals(fullName)) return file.GetFirstBlock();
			}
		}
		return BLOCK_ERROR;
	}
	private File GetFile(String fullName) throws Exception {
		if(DoesFileExist(fullName)) {
			for(File file : mainCatalog) {
				if(file.GetFullName().equals(fullName)) return file;
			}
		}
		else throw new Exception("Nie znaleziono pliku");
		return EMPTY_FILE;
	}
	
	private int FreeBlocksSpace() {
		return CountFreeBlocks() * BLOCK_SIZE;
	}
	
	private int NewFileRecord(String fullName, String content) throws Exception {
		int freeBlock = FindFreeBlock();
		int firstBlock = freeBlock;
		int fileSize = content.length();
		int numberOfFreeBlocks = CountFreeBlocks();
		
		if(freeBlock == BLOCK_ERROR) throw new OutOfBlocksException("Brak miejsca na dysku. Kod -1");
		else if(freeBlock < BLOCK_ERROR || freeBlock > (BLOCKS-1)) throw new Exception("Blad algorytmu");
		else {
			double d_neededBlocks = (double) fileSize/BLOCK_SIZE; //Casting double to integer rounds down 
			int i_neededBlocks = (int) d_neededBlocks;
			if (d_neededBlocks%1 != 0) i_neededBlocks+=1;
			
			if(i_neededBlocks > numberOfFreeBlocks) throw new OutOfBlocksException("Brak miejsca na dysku. Kod -2");
			else {
				/* nowy wpis katalogowy */
				File file = new File(fullName, freeBlock, fileSize);
				
				int charToWrite = 0;	//Zmienna pomocnicza
				while(fileSize != 0) {
					FAT[freeBlock] = LAST_BLOCK;
					for(int i=0; i<BLOCK_SIZE && fileSize!=0; i++) {
						disk[i+BLOCK_SIZE*freeBlock] = content.charAt(charToWrite);
						charToWrite++;
						fileSize--;
					}
					FreeBlocks[freeBlock] = false;
					if(fileSize > 0) {
						int currentBlock = freeBlock;
						freeBlock = FindFreeBlock();
						FAT[currentBlock] = freeBlock;
					}
				}
				mainCatalog.add(file);
			}
		}
		return firstBlock;
	}
	private void RefreshSize(String fullName) throws Exception {
		if(!DoesFileExist(fullName)) throw new Exception("Brak pliku");
		else {
			int blocksInUse = 1;
			int size = 0;
			int currentBlock = FilesFirstBlock(fullName);
			
			while(FAT[currentBlock] != LAST_BLOCK) {
				blocksInUse++;
				currentBlock = FAT[currentBlock];
			}
			size = blocksInUse * BLOCK_SIZE - FilesFreeSpace(fullName);
			
			GetFile(fullName).SetSize(size);
		}
	}
}