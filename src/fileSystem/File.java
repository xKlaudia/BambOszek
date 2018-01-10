package fileSystem;

import syncMethod.Lock;

public class File {
	private final int BLOCK_ERROR = -1;
	
	private String name;
	private int firstBlock, size;
	
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
	
	protected int GetFirstBlock() {
		return this.firstBlock;
	}
	
	protected int SetFirstBlock(int firstBlock) {
		if(firstBlock < 0 || firstBlock > (FAT.BLOCKS-1)) return BLOCK_ERROR;
		else {
			this.firstBlock = firstBlock;
			return firstBlock;
		}
	}
	
	protected void SetSize(int size) {
		this.size = size;
	}
	
	public String GetFullName(){
		return this.name;
	}
	
	public int GetSize() {
		return this.size;
	}
}