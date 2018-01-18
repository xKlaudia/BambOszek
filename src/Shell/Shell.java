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
        public static String currentProcess = "";
        public int counter = 0; //liczy kwanty wykonywanych rozkazów
        public Interpreter interpreter;
		
		public Shell() throws Exception {
                        this.virtualMemory = new VirtualMemory();
			this.processManagement=new ProcessesManagement(virtualMemory);
			this.fat=new FAT();
                        this.interpreter=new Interpreter(this.processManagement, this.fat, this.virtualMemory);
		Dzialaj();	
		}
	public static boolean d=true;
        
            public void setCurrentProcess()
            {
                for(Process p : processManagement.processesList)
                {
                    if(p.GetState()==2)
                    {
                        currentProcess = p.GetName();
                        break;
                    }
                }
            }
        
		public void Dzialaj() throws Exception {
		
			while(d) {
                            try
                            {
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
                                 
                        case("cp"):{
                          processManagement.NewProcess_XC("p1", 2);
                          processManagement.SetHowManyPagesWithID(0,((45 - 1) / 16 + 1));
                          virtualMemory.loadProcess("p1", "Procesy.txt", 45);
                          break;
                        }
		 	case("go"):{
		 	//	interpreter.RUN(processManagement.);
			interpreter.RUN(processManagement.getProcess(currentProcess));
                        setCurrentProcess();
                        if(counter<2) counter++;
                        else 
                        {
                            counter = 0;
                            interpreter.CPU();                      
                        }
                        
			 break;
		 	}
		 case("find"):{if(arr[1]!=null) {
			 fat.DoesFileExist(arr[1]);
		 }
		 
		// System.out.println("sprawdzam plik o nazwie: "+arr[1]+" istnieje");
		 break;} //sprawdz czy plik instnieje
		
		 case("cat"):{
			 if(arr[1]!=null) {
			 System.out.println(fat.PrintFilesContent(arr[1]));
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
			 int id = Integer.parseInt(arr[1]); 
                         processManagement.DeleteProcessWithID(id);
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
				for(int i=1;i<arr.length;i++) { content=content+arr[i]+' ';}
				System.out.println(content);
                                break;
		 }
		 
		 case("quit"):{d=false;break;}
		 }
		    
		}catch(Exception e)
                {
                    e.printStackTrace();
                }
		}
                        System.out.println("Zegnam");
                }
}
