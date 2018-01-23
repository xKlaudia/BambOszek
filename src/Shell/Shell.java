package Shell;

import java.util.Scanner;

import processesmanagement.Process;
import processesmanagement.ProcessesManagement;
import memoryManagement.VirtualMemory;
import fileSystem.FAT;
import Interpreter.Interpreter;

	public class Shell {
		//TUTAJ MUSZA SIE ZNALEZC OBIEKTY KLAS DO WYWOLYWANIA FUNKCJI
	public	ProcessesManagement processManagement;
        public  VirtualMemory virtualMemory;
	public  FAT fat;	
		public Interpreter interpreter;
		
		public Shell() throws Exception {
			this.processManagement=new ProcessesManagement(virtualMemory);
			this.fat=new FAT();
		Dzialaj();	
		}
	public static boolean d=true;
		public void Dzialaj() throws Exception {
		
			while(d) {
			String komenda; 
			System.out.print("$>");
		    Scanner odczyt = new Scanner(System.in); //obiekt do odebrania danych od u�ytkownika
		 
		      komenda = odczyt.nextLine();
		      String[] arr = komenda.split(" ");
		     // for ( String ss : arr) {

		       //   System.out.println(ss);
		     //}
		 switch(arr[0]) {
		 //katalogi
		/* case("cd"):{break;} //zmiana katalogu
		 
		 case("mkdir"):{break;} //utworz katalog
		 
		 case("rmdir"):{break;}//usun katalog
		 
		 case("ls"):{ break;} //wyswietla zawartosc katalogu
		*/
		 case("cf"):{if(arr[1]!=null) {
			 if(arr.length>2) {
				String content = "";
				for(int i=2;i<arr.length;i++) { content=content+' '+arr[i];}
				 fat.CreateNewFile(arr[1],content);
				//System.out.println("utworzono plik o nazwie: "+arr[1]+ "i zawartosci"+ content); 
			 } 
			if(arr.length==2) {if(arr[1]!=null) {
				fat.CreateEmptyFile(arr[1]);
			}
			//System.out.println("utworzono plik o nazwie: "+arr[1]);
			} }
				 break;} //utworz plik o nazwie
		 	case("go"):{
		 		
		 	//	interpreter.RUN(processManagement.);
			 
			 break;
		 	}
		 case("find"):{if(arr[1]!=null) {
			 fat.DoesFileExist(arr[1]);
		 }
		 
		// System.out.println("sprawdzam plik o nazwie: "+arr[1]+" istnieje");
		 break;} //sprawdz czy plik instnieje
		
		 case("cat"):{
			 if(arr[1]!=null) {
			 fat.PrintFilesContent(arr[1]);
		 System.out.println("wyswietlam zawartosc pliku o nazwie: "+arr[1]);}
		 break;} //wypisz zawartosc pliku
		 
		 case("rm"):{if(arr[1]!=null) {
			 fat.DeleteFile(arr[1]);
		// System.out.println("usuwam plik o nazwie: "+arr[1]);
			 }
		 break;} //usuniecie pliku
		 //case("cp"):{System.out.println(komenda); break;} //kopiowanie pliku
		 //procesy
		 case("df"):{
			 fat.PrintDisk();
		 //System.out.println("wyswietlanie dysku: ");
		 break;}
		 case("ps"):{processManagement.printProcessListInformations();
		 System.out.println("wyswietlam liste procesow");
		 break;} //wyswietla procesy
		 
		 case("pi"):{if(arr[1]!=null) {
			 int id = Integer.parseInt(arr[1]);processManagement.printProcessInformations(id);
		 System.out.println("dane procesu o id: "+id);}
		 break;} //informacje o procesie po id
		 
		 case("pn"):{if(arr[1]!=null) {
			 processManagement.getProcess(arr[1]);
		 System.out.println("dane procesu o nazwie: "+arr[1]);}
		 break;} // proces po nazwie
		
		 case("kill"):{
			 if(arr[1]!=null) {
			 int id = Integer.parseInt(arr[1]); processManagement.DeleteProcessWithID(id);
		 System.out.println("usuniecie procesu o nazwie: "+arr[1]);
			 }
		 break;} //zabija proces po id
		 
		 case("nice"):{if(arr[1]!=null) {
			 int id = Integer.parseInt(arr[1]);
			 int priorytet = Integer.parseInt(arr[2]);
			 processManagement.SetCurrentPririty(id, priorytet);
			 System.out.println("zmiana priorytetu procesu o nazwie: "+arr[1]);}
			 break;} //zmiana priorytetu procesu
		 //shell
		 
		 case("echo"):{
			 String content = "";
				for(int i=2;i<arr.length;i++) { content=content+' '+arr[i];}
				System.out.println(content);
		 }
		 
		 case("quit"):{d=false;break;}
		 }
		    
		}
<<<<<<< HEAD
			System.out.println("System zakonczyl dzialanie");
<<<<<<< HEAD
=======
			
		 }catch(Exception e)
                {
                    e.printStackTrace();
                }
>>>>>>> f8443c6b9883c497ac3294af62337e47cb36f857
=======
>>>>>>> parent of 1f1f7d6... Merge branch 'master' of https://github.com/xKlaudia/BambOszek
=======
			System.out.println("Zegnam");
>>>>>>> parent of 59f2f1b... lol
		}
}
