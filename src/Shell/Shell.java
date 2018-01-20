package Shell;

import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;

import processesmanagement.Process;
import processesmanagement.ProcessesManagement;
import memoryManagement.VirtualMemory;
import fileSystem.FAT;
import Interpreter.Interpreter;
import java.io.BufferedWriter;

	public class Shell {
		//TUTAJ MUSZA SIE ZNALEZC OBIEKTY KLAS DO WYWOLYWANIA FUNKCJI
	public	ProcessesManagement processManagement;
        public  VirtualMemory virtualMemory;
	public  FAT fat;
        private int id=1;
        public static String currentProcess = "";
        public static int counter = 0; //liczy kwanty wykonywanych rozkazów
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
                    if(processManagement.processesList.size()==0)
                    {
                        File file = new File("idle.txt");
                        FileWriter writer = new FileWriter(file, true);
                        BufferedWriter in = new BufferedWriter(writer);
                        in.write("JP 0;");
                        in.close();
                        writer.close();
                        processManagement.NewIdleProcess();
                        processManagement.SetHowManyPagesWithID(0,1);
                        virtualMemory.loadProcess("Idle","idle.txt",6);
                    }
                
			while(d) {
                                for(Process p : processManagement.processesList)
                {
                    if(p.GetState()==2)
                    {
                        currentProcess = p.GetName();
                        break;
                    }
                }
                                processManagement.CheckStates();
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
                
            case("cp"):{
              /*processManagement.NewProcess_XC("p1", 2);
              processManagement.SetHowManyPagesWithID(0,((46 - 1) / 16 + 1));
              virtualMemory.loadProcess("p1", "Silnia.txt", 46);*/
              processManagement.NewProcess_XC(arr[1], Integer.parseInt(arr[3]));
              processManagement.SetHowManyPagesWithID(id,((Integer.parseInt(arr[4]) - 1) / 16 + 1));
              virtualMemory.loadProcess(arr[1], arr[2] + ".txt", Integer.parseInt(arr[4]));
              id++;
              break;
            }
		 	case("go"):{
		 	//	interpreter.RUN(processManagement.);
                            processManagement.CheckStates();
                            if (currentProcess.equals(""))
                                interpreter.CPU();
                            setCurrentProcess();
                            System.out.println("Nazwa aktualnie wykonywanego procesu: " + currentProcess);
                            interpreter.RUN(processManagement.getProcess(currentProcess));
	                    if (counter < 1)
                                counter++;
	                    else
	                    {
	                        counter = 0;
	                        interpreter.CPU();                      
	                    }
                            break;
		 	}
			
			 case("ps"):
                         {
                            System.out.println("wyswietlam liste procesow");
                            processManagement.printProcessListInformations();			 
                            break;
                         } //wyswietla procesy
			 
			 case("pi"):{if(arr[1]!=null) {
				 int id = Integer.parseInt(arr[1]);processManagement.printProcessInformations(id);
			 System.out.println("dane procesu o id: "+id);}
			 break;} //informacje o procesie po id
			 
			 case("pn"):
                         {
                            if(arr[1]!=null) 
                            {
				processManagement.getProcess(arr[1]).printInformations();
                                System.out.println("dane procesu o nazwie: "+arr[1]);
                            }
                            break;
                         } // proces po nazwie
			
			 case("kill"):{
				 if(arr[1]!=null) {
				 int id = Integer.parseInt(arr[1]); 
		                     processManagement.DeleteProcessWithID(id);
                                System.out.println("usuniecie procesu o id: "+arr[1]);
				 }
			 break;} //zabija proces po id
			 
			 case("nice"):{if(arr[1]!=null) {
				 int id = Integer.parseInt(arr[1]);
				 int priorytet = Integer.parseInt(arr[2]);
				 processManagement.SetCurrentPririty(id, priorytet);
				 System.out.println("zmiana priorytetu procesu o id: "+arr[1]);}
				 break;} //zmiana priorytetu procesu
			 //shell
                         
                         case("state"):{if(arr[1]!=null) {
				 int id = Integer.parseInt(arr[1]);
				 int stan = Integer.parseInt(arr[2]);
				 processManagement.SetState(id, stan);
				 System.out.println("zmiana stanu procesu o id: "+arr[1]);}
				 break;}
			 case("current"):{
                                processManagement.getProcess(currentProcess).printInformations();
				 break;}
			 case("echo"):{
				 String content = "";
					for(int i=1;i<arr.length;i++) { content=content+arr[i]+' ';}
					System.out.println(content);
		                            break;
			 }
			 
			 case("quit"):{d=false;break;}
			 
			 
			 //-------------------------------------------------PLIKI I KATALOGI
			 case("pd"): { 		
				 fat.PrintDisk();
				 break;
			 }
			 case("sf"): {
				 try {
					 fat.ShowFileInfo(arr[1]);
				 }
				 catch(Exception ex) {
					 System.out.println("BLAD OTWIERANIA PLIKU " + ex.getMessage());
				 }
				 break;
			 }
			 case("cf"):{
				 if(arr[1]!=null) {
				 }
				 if(arr.length>2) {
					String content = "";
					for(int i=2;i<arr.length;i++) { content=content+' '+arr[i];}
					 fat.CreateNewFile(arr[1],content);
					//System.out.println("utworzono plik o nazwie: "+arr[1]+ "i zawartosci"+ content); 
				 } 
				if(arr.length==2) {
					if(arr[1]!=null) {
					fat.CreateEmptyFile(arr[1]);
					}
				//System.out.println("utworzono plik o nazwie: "+arr[1]);
				} 
				break;
			}
			case("cat"):{
				if(arr[1]!=null) {
					System.out.println(fat.PrintFilesContent(arr[1]));
			System.out.println("wyswietlam zawartosc pliku o nazwie: "+arr[1]);}
			break;
			}
			case("find"):{if(arr[1]!=null) {
				Boolean exist = fat.DoesFileExist(arr[1]);
			    	if(exist==true) System.out.println("Plik istnieje");
                    else System.out.println("Plik o podanej nazwie nie istnieje");
				}
				 
				// System.out.println("sprawdzam plik o nazwie: "+arr[1]+" istnieje");
				break;} //sprawdz czy plik instnieje
				 
			case("rm"):{if(arr[1]!=null) {
				try {
					fat.DeleteFile(arr[1]);
				}
				catch(Exception ex) {
					System.out.println("BLAD USUWANIA PLIKU: " + ex.getMessage());
				}				
			}
			break;} //usuniecie pliku
				 //case("cp"):{System.out.println(komenda); break;} //kopiowanie pliku
				 //procesy
                        default:
                            System.out.println("Podano nieprawidłową komendę!");
                            break;
		 //-------------------------------------------------------------------------
		 }
			
		 }catch(Exception e)
                {
                    e.printStackTrace();
                }
		}
                        System.out.println("Zegnam");
                }
}
