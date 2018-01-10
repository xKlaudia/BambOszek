package fileSystem;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.*;
import fileSystemExceptions.*;
import processesmanagement.Process;

public class FAT {
	
	private final static int BLOCK_SIZE = 32;
	private final static int BLOCK_ERROR = -1, LAST_BLOCK = -1;
	private final static char EMPTY_BYTE = '#';
	private final static File EMPTY_FILE = new File();
	
	final static int DISK_SIZE = 1024;
	final static int BLOCKS = DISK_SIZE / BLOCK_SIZE;
	
	
	private char[] disk;
	private int[] FAT;
	private boolean[] FreeBlocks;
	private Vector<File> mainCatalog;
	
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
	
	public boolean AppendToFile(String name, String content) throws Exception {
		if(!DoesFileExist(name)) throw new Exception("Nie znaleziono pliku");
		else {
			int contentSize;
			int firstBlock = FilesFirstBlock(name);
			int filesFreeSpace;
			
			if(firstBlock == BLOCK_ERROR) throw new Exception("Nie znaleziono pierwszego bloku");
			else { 
				filesFreeSpace = FilesFreeSpace(name);
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
							FAT[lastBlock] = NewFileRecord(name, unwrittenContent);
						}
					}
				}
			} 				
		}
		RefreshSize(name);
		return true;
	}
	
	public boolean CreateEmptyFile(String name) throws Exception {
		if(!CheckFileName(name)) {
			throw new IllegalFileNameException("Podano nieprawidlowa nazwe. Nazwa musi zawierac 1 do 8 znakow (male, duze litery i cyfry) + kropke i nazwe rozszerzenia (txt)");
		}
		else if(DoesFileExist(name)) {
			throw new IllegalFileNameException("Istnieje plik o podanej nazwie");
		}
		else {
			int freeBlock = FindFreeBlock();
			if(freeBlock == BLOCK_ERROR) throw new OutOfBlocksException("Brak miejsca na dysku");
			else if(freeBlock < BLOCK_ERROR || freeBlock > (BLOCKS-1)) throw new Exception("Blad algorytmu");
			else {
				File file = new File(name, freeBlock, 0);
				FAT[freeBlock] = LAST_BLOCK;
				mainCatalog.add(file);			
			}
		}
		return true;
	}
	
	public void CloseFile(String name, Process process) {
		if(DoesFileExist(name)) {
			for(File file : mainCatalog) {
				if(file.GetFullName().equals(name)) {
					file.lock.unlock(process);
				}
			}
		}
	}
	public boolean CreateNewFile(String name, String content) throws Exception {
		if(!CheckFileName(name)) {
			throw new IllegalFileNameException("Podano nieprawidlowa nazwe. Nazwa musi zawierac 1 do 8 znakow (male, duze litery i cyfry)");
		}
		else if(DoesFileExist(name)) {
			throw new IllegalFileNameException("Istnieje plik o podanej nazwie");
		}
		else {
			try {
				NewFileRecord(name, content);
				RefreshSize(name);
			}
			catch(Exception e) {
				throw e;
			}
		}
		return true;
	}
	
	public void DeleteFile(String name) throws Exception {
		if(!DoesFileExist(name)) throw new Exception("Brak pliku o podanej nazwie");
		else {
			int blockToDelete = FilesFirstBlock(name);
			int nextBlockToDelete;
			
			do {
				nextBlockToDelete = FAT[blockToDelete];
				FAT[blockToDelete] = LAST_BLOCK;
				for(int i=0; i<BLOCK_SIZE; i++) disk[i+BLOCK_SIZE*blockToDelete] = EMPTY_BYTE;
				FreeBlocks[blockToDelete] = true;
				blockToDelete = nextBlockToDelete;
			} while(blockToDelete != LAST_BLOCK);
			
			for(int i=0; i<mainCatalog.size(); i++) {
				if(mainCatalog.get(i).GetFullName().equals(name)) {
					mainCatalog.remove(i);
					break;
				}
			}
		}
	}
	
	public boolean DoesFileExist(String name) {
		for(int i=0; i<mainCatalog.size(); i++) {
			if(mainCatalog.get(i).GetFullName().equals(name)) return true;
		}
		return false;
	}
	
	public boolean[] GetFreeBlocks() {
		return FreeBlocks;
	}
	
	public void OpenFile(String name, Process process) {
		if(DoesFileExist(name)) {
			for(File file : mainCatalog) {
				if(file.GetFullName().equals(name)) {
					file.lock.lock(process);
				}
			}
		}
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
	}
	public String PrintFilesContent(String name) throws Exception {
		if(DoesFileExist(name)) {
			int blockToRead = FilesFirstBlock(name);
			String content = "";
			int fileSize = GetFile(name).GetSize();
			do {
				for(int i=0; i<BLOCK_SIZE && fileSize!=0; i++) {
					content += disk[i+blockToRead*BLOCK_SIZE];
					fileSize--;
				}
				if(FAT[blockToRead] != LAST_BLOCK && fileSize !=0) {
					blockToRead = FAT[blockToRead];
				}
			} while (fileSize != 0);
			return content;
		}
		else throw new Exception("Plik nie istnieje");
	}
	
	/* zwraca czy nazwa jest poprawna */
	private boolean CheckFileName(String input) {
		/* format nazwy: nazwa.txt, gdzie nazwa max. 8 znakow */
		String pattern = "^[a-zA-Z0-9]{1,8}";
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(input);
		boolean matches = matcher.matches();
		
		if(!matches) {
			return false;
		}
		else return true;
	}
	
	private int CountFreeBlocks(){
		int countFreeBlocks = 0;
		for(int i=0; i<FreeBlocks.length; i++) {
			if(FreeBlocks[i] == true) countFreeBlocks++;
		}
		return countFreeBlocks;
	}
	
	private int FilesFreeSpace(String name) {
		if(DoesFileExist(name)) {
			int blockToRead = FilesFirstBlock(name);
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
	
	private int FilesFirstBlock(String name) {
		if(DoesFileExist(name)) {
			for(File file : mainCatalog) {
				if(file.GetFullName().equals(name)) return file.GetFirstBlock();
			}
		}
		return BLOCK_ERROR;
	}
	private File GetFile(String name) throws Exception {
		if(DoesFileExist(name)) {
			for(File file : mainCatalog) {
				if(file.GetFullName().equals(name)) return file;
			}
		}
		else throw new Exception("Nie znaleziono pliku");
		return EMPTY_FILE;
	}
	
	private int FreeBlocksSpace() {
		return CountFreeBlocks() * BLOCK_SIZE;
	}
	
	private int NewFileRecord(String name, String content) throws Exception {
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
				File file = new File(name, freeBlock, fileSize);
				
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
	
	private void RefreshSize(String name) throws Exception {
		if(!DoesFileExist(name)) throw new Exception("Brak pliku");
		else {
			int blocksInUse = 1;
			int size = 0;
			int currentBlock = FilesFirstBlock(name);
			
			while(FAT[currentBlock] != LAST_BLOCK) {
				blocksInUse++;
				currentBlock = FAT[currentBlock];
			}
			size = blocksInUse * BLOCK_SIZE - FilesFreeSpace(name);
			
			GetFile(name).SetSize(size);
		}
	}
	
	protected void ShowFileInfo(String name) throws Exception {
		if(DoesFileExist(name)) {
			File file;
			try {
				file = GetFile(name);
			} catch (Exception e) {
				throw e;
			}
			try {
				System.out.println("Nazwa pliku: " + file.GetFullName()
						+ "\nZawartosc: " + PrintFilesContent(name) 
						+ "\n Blok pierwszy " + file.GetFirstBlock());
			} catch (Exception e) {
				throw e;
			}
		}
		else throw new Exception("Brak pliku");
	}
}