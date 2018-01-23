package fileSystem;

import syncMethod.Lock;

public class File {
	//private final int BLOCK_ERROR = -1;
	
	private String name;
	private int firstBlock, size, readChars;
	
	protected Lock lock;
	
	File(String name, int firstBlock, int size) throws Exception {
		this.name = name;
		if(firstBlock < 0 || firstBlock > (FAT.BLOCKS-1)) {
			throw new Exception("Niepoprawny nr bloku");
		}
		else {
			this.firstBlock = firstBlock;
		}
		if(size < 0 || size > FAT.DISK_SIZE) {
			throw new Exception("Niepoprawny rozmiar");
		}
		else {
			this.size = size;
		}
		lock = new Lock("");
		readChars = 0;
	}
	
	protected File() {
		this.name = "#";
		this.firstBlock = -1;
		this.size = -1;
	}
	
	protected File(File file) {
		this.name = file.name;
		this.size = file.size;
		this.firstBlock = file.firstBlock;
	}
	
	protected int GetFirstBlock() { return this.firstBlock; }
	
	protected int GetReadChars() { return readChars; }
	
	protected void SetFirstBlock(int firstBlock) throws Exception {
		if(firstBlock < 0 || firstBlock > (FAT.BLOCKS-1)) throw new Exception("BLAD PRZYDZIALU BLOKU");
		else {
			this.firstBlock = firstBlock;
		}
	}
	
	protected void SetReadChars(int x) throws Exception {
		if(x < 0 || (readChars+x) > size) throw new Exception("Blad wskaznika odczytu pliku");
		else readChars += x;
	}
	
	protected void SetSize(int size) { this.size = size; }
	
	public String GetFullName() { return this.name; }
	
	public int GetSize() { return this.size; }
}