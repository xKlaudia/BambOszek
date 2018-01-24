package Shell;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

import java.io.File;
import java.io.FileWriter;
import processesmanagement.Process;
import processesmanagement.ProcessesManagement;
import memoryManagement.VirtualMemory;
import interprocessCommunication.interprocessCommunication;
import fileSystem.FAT;
import Interpreter.Interpreter;
import java.io.BufferedWriter;
import syncMethod.Lock;

	public class Shell {
	
		//TUTAJ MUSZA SIE ZNALEZC OBIEKTY KLAS DO WYWOLYWANIA FUNKCJI
		public	ProcessesManagement processManagement;
	    public  VirtualMemory virtualMemory;
	    public interprocessCommunication interprocessCommunication;
	    public Lock lock;
		public  FAT fat;
        private int id=1;
        public static String currentProcess = "";
        public static int counter = 0; //liczy kwanty wykonywanych rozkazów
        public boolean processKilled = false;
        public String killedProcessName = "";
        public Interpreter interpreter;
        
		public Shell() throws Exception {
            this.virtualMemory = new VirtualMemory();
            this.interprocessCommunication = new interprocessCommunication();
			this.processManagement=new ProcessesManagement(virtualMemory);
			this.fat=new FAT();
            this.interpreter=new Interpreter(this.processManagement, this.fat, this.virtualMemory, this.interprocessCommunication);
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
         
        private static String readLineByLineJava8(String filePath)
    	{
    	    StringBuilder contentBuilder = new StringBuilder();
    	    try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
    	    {
    	        stream.forEach(s -> contentBuilder.append(s).append("\n"));
    	    }
    	    catch (IOException e)
    	    {
    	        e.printStackTrace();
    	    }
    	    return contentBuilder.toString();
    	}
            
            
		public void Dzialaj() throws Exception {
            if(processManagement.processesList.size()==0)
            {
                File file = new File("idle.txt");
                if(!file.isFile())
                {
                    FileWriter writer = new FileWriter(file, true);
                    BufferedWriter in = new BufferedWriter(writer);
                    in.write("JP 0;");
                    in.close();
                    writer.close();
                }
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
		            	
		              if (processManagement.FindProcessWithName(arr[1]) == -1) {
		                try {  
		                    processManagement.NewProcess_XC(arr[1], Integer.parseInt(arr[3]));
		                    processManagement.SetHowManyPagesWithID(id,((Integer.parseInt(arr[4]) - 1) / 16 + 1));
		                    virtualMemory.loadProcess(arr[1], arr[2] + ".txt", Integer.parseInt(arr[4]));
		                    id++;
		                }
		                catch (Exception exception) {
		                    System.out.println(exception.getMessage());
		                    processManagement.DeleteProcessWithID(id);
		                    id++;
		                }
		              }
		              else {
		                  System.out.println("Istnieje proces o podanej nazwie!");
		              }
		              break;
		            }
		            
		            case("go"):{

				 		System.out.println("wykonaj kolejne polecenie z pliku");
		
				 		//	interpreter.RUN(processManagement.);
                        processManagement.CheckStates();
                        /*if(processManagement.GetStateWithName(currentProcess)==3)
                        {
                            lock.addToQueue(processManagement.getProcess(currentProcess));
                        }*/
                        if (currentProcess.equals("") || currentProcess.equals("Idle") || processManagement.processesList.size() < 2) {
                            interpreter.CPU();
                            counter = 0;
                        }
                        if (processManagement.GetLockedWithID(processManagement.GetIDwithName(currentProcess)))
                        {
                            interpreter.CPU();
                            counter = 0;
                        }
                        if (processKilled) {
                            if (killedProcessName.equals(currentProcess)) {
                                interpreter.CPU();
                                counter = 0;
                            }
                            processKilled = false;
                        }
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
		 	
					 case("df"):
					 {
						 fat.PrintDisk();
						 System.out.println("wyswietlanie dysku: ");
						 break;
					 }
		 
		 
					 case("pi"):{if(arr[1]!=null)
					 {
						 int id = Integer.parseInt(arr[1]);processManagement.printProcessInformations(id);
						 System.out.println("dane procesu o id: "+id);}
					 	 break;
					 } //informacje o procesie po id
					 
					 case("pn"):{if(arr[1]!=null)
					 {
						 System.out.println("dane procesu o nazwie: "+arr[1]);}
				         processManagement.getProcess(arr[1]).printInformations();
					 break;
					 } // proces po nazwie
		
		 
					 case("ps"):
                     {
						 System.out.println("wyswietlam liste procesow");
                         processManagement.printProcessListInformations();			 
                         break;
                     } //wyswietla procesy
			 
			
					 case("pwp"):
		             {
		                 System.out.println("Waiting processes:");
		                 for(int i=0;i<processManagement.processesList.size();i++)
		                 {
		                     if(processManagement.processesList.get(i).GetState()==3)
		                     {
		                    	 System.out.println("ID: "+processManagement.processesList.get(i).GetID()+"  "+"Name: " + processManagement.processesList.get(i).GetName());
		                     }
		                 }
		                 break;
		             }//wyswietla liste procesow w stanie waiting
					 
					 case("kill"):
					 {
						 if(arr[1]!=null) {
						 int id = Integer.parseInt(arr[1]);
                         if(id!=0)
                         {
                        	 fat.CheckLocks(processManagement.GetProcessWithID(id));
                             killedProcessName = processManagement.GetNameWithID(id);
                             processManagement.DeleteProcessWithID(id);
                             processKilled = true;
                             System.out.println("usuniecie procesu o id: "+arr[1]);
                         }
                         else System.out.println("You cannot delete Idle process!");
						 }
						 break;
					 } //zabija proces po id
			 
					 case("nice"):
					 {
						 if(arr[1]!=null) {
							 int id = Integer.parseInt(arr[1]);
							 int priorytet = Integer.parseInt(arr[2]);
							 processManagement.SetCurrentPririty(id, priorytet);
							 System.out.println("zmiana priorytetu procesu o id: "+arr[1]);
						 }
						 break;
					 } //zmiana priorytetu procesu
					 	   //shell
                         
                     case("state"):
                     {
                    	 if(arr[1]!=null) {
							 int id = Integer.parseInt(arr[1]);
							 int stan = Integer.parseInt(arr[2]);
							 processManagement.SetState(id, stan);
							 System.out.println("zmiana stanu procesu o id: "+arr[1]);
                    	 }
						 break;
                     }
                     
                     case("current"):
                     {
                          processManagement.getProcess(currentProcess).printInformations();
                          break;
                     }
                     
					 case("echo"):
					 {
						 String content = "";
						 for(int i=1;i<arr.length;i++) { content=content+arr[i]+' ';}
						 System.out.println(content);
						 break;
					 }
                         
                    case("pm"):
                    {
                        if (arr.length < 3) {
                            virtualMemory.printVirtualMemory(0, 128);
                        }
                        else {
                            virtualMemory.printVirtualMemory(Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
                        }
                        break;
                    } //Wyswietla pamiec wirtualna
                        
                    case("ppt"):
                    {
                        if (arr[1] != null) {
                            virtualMemory.printPageTable(arr[1]);
                        }
                        break;
                    } //Wyswietla tablice stronic
                        
                    case("quit"):
                    {
                    	d=false;
                    	break;
                    }
			 
			 
					//-------------------------------------------------PLIKI I KATALOGI
                    case("pd"): { 		
                    	fat.PrintDisk();
						break;
					}
					case("smc"): {
						System.out.println("ZAWARTOSC DYSKU: ");
						fat.ShowMainCatalog();
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
							System.out.println(fat.GetFilesContent(arr[1]));
					System.out.println("wyswietlam zawartosc pliku o nazwie: "+arr[1]);}
					break;
					}
					case("find"):{
						if(arr[1]!=null) {
							Boolean exist = fat.DoesFileExist(arr[1]);
					    	if(exist==true) System.out.println("Plik istnieje");
		                    else System.out.println("Plik o podanej nazwie nie istnieje");
						}
						break;
					} //sprawdz czy plik instnieje
						 
					case("rm"):{
						if(arr[1]!=null) {
							try {
								fat.DeleteFile(arr[1]);
							}
							catch(Exception ex) {
								System.out.println("BLAD USUWANIA PLIKU: " + ex.getMessage());
							}				
						}
						break;
					} //usuniecie pliku
					//case("cp"):{System.out.println(komenda); break;} //kopiowanie pliku
						 //procesy
                    default:
                        System.out.println("Podano nieprawidlowa komende");
                        break;
                    //-------------------------------------------------------------------------
                    case("rf"):{
                    	String bla=readLineByLineJava8(arr[1]);
               			String[] lines = bla.split("\\r?\\n");
               			for(int i =0;i<lines.length;i++) {
               				String run[]=lines[i].split(" ");
               				 
               				switch(run[0]) {
               			 	case("cp"):{
	               	            /*processManagement.NewProcess_XC("p1", 2);
	               	            processManagement.SetHowManyPagesWithID(0,((46 - 1) / 16 + 1));
	               	            virtualMemory.loadProcess("p1", "Silnia.txt", 46);*/
	               	            if (processManagement.FindProcessWithName(run[1]) == -1) {
	               	            	try {  
	               	                    processManagement.NewProcess_XC(run[1], Integer.parseInt(run[3]));
	               	                    processManagement.SetHowManyPagesWithID(id,((Integer.parseInt(run[4]) - 1) / 16 + 1));
	               	                    virtualMemory.loadProcess(run[1], run[2] + ".txt", Integer.parseInt(run[4]));
	               	                    id++;
	               	            	}
	               	            	catch (Exception exception) {
	               	            		System.out.println(exception.getMessage());
	               	            		processManagement.DeleteProcessWithID(id);
	               	            		id++;
	               	            	}
	               	            }
	               	            else {
	               	            	System.out.println("Istnieje proces o podanej nazwie!");
	               	            }
	               	            break;
               			 	}

               			 	case("go"):{

               			 		System.out.println("wykonaj kolejne polecenie z pliku");

               			 		//	interpreter.RUN(processManagement.);
   	                            processManagement.CheckStates();
   	                            /*if(processManagement.GetStateWithName(currentProcess)==3)
   	                            {
   	                                lock.addToQueue(processManagement.getProcess(currentProcess));
   	                            }*/
   	                            if (currentProcess.equals("") || currentProcess.equals("Idle") || processManagement.processesList.size() < 2) {
   	                                interpreter.CPU();
   	                                counter = 0;
   	                            }
   	                            if (processManagement.GetLockedWithID(processManagement.GetIDwithName(currentProcess)))
   	                            {
   	                                interpreter.CPU();
   	                                counter = 0;
   	                            }
   	                            if (processKilled) {
   	                                if (killedProcessName.equals(currentProcess)) {
   	                                    interpreter.CPU();
   	                                    counter = 0;
   	                                }
   	                                processKilled = false;
   	                            }
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
              
               			 	case("df"):{
               			 		fat.PrintDisk();
               			 		System.out.println("wyswietlanie dysku: ");
               			 		break;
               			 	}
               			 
               			 
               			 	case("pi"):{if(run[1]!=null) {
               			 		int id = Integer.parseInt(run[1]);processManagement.printProcessInformations(id);
               			 		System.out.println("dane procesu o id: "+id);}
               			 		break;
               			 	} //informacje o procesie po id
               			 
               			 	case("pn"):{if(run[1]!=null) {
               			 		processManagement.getProcess(run[1]);
               			 		System.out.println("dane procesu o nazwie: "+run[1]);}
               			 		break;
               			 	} // proces po nazwie
               			
               			 	case("ps"):
	                        {
	                           System.out.println("wyswietlam liste procesow");
	                           processManagement.printProcessListInformations();			 
	                           break;
	                        } //wyswietla procesy
               				 
               				case("pwp"):
               	            {
	                            System.out.println("Waiting processes:");
	                            for(int z=0;z<processManagement.processesList.size();z++)
	                            {
	                                if(processManagement.processesList.get(z).GetState()==3)
	                                {
	                                    System.out.println("ID: "+processManagement.processesList.get(z).GetID()+"  "+"Name: " + processManagement.processesList.get(z).GetName());
	                                }
	                            }
	                            break;
               	            }//wyswietla liste procesow w stanie waiting
               				case("kill"):{
               					if(run[1]!=null) {
	               					int id = Integer.parseInt(run[1]);
		                            if(id!=0)
		                            {
		                            	fat.CheckLocks(processManagement.GetProcessWithID(id));
		                                killedProcessName = processManagement.GetNameWithID(id);
		                                processManagement.DeleteProcessWithID(id);
		                                processKilled = true;
		                                System.out.println("usuniecie procesu o id: "+run[1]);
		                            }
		                            else System.out.println("You cannot delete Idle process!");
               					}
               					break;
               				} //zabija proces po id
               				 
               				case("nice"):{if(run[1]!=null) {
               					int id = Integer.parseInt(run[1]);
               					int priorytet = Integer.parseInt(run[2]);
               					processManagement.SetCurrentPririty(id, priorytet);
               					System.out.println("zmiana priorytetu procesu o id: "+run[1]);}
               				break;
               				} //zmiana priorytetu procesu
               				 //shell
               	                         
               	            case("state"):{if(run[1]!=null) {
               	            	int id = Integer.parseInt(run[1]);
               					int stan = Integer.parseInt(run[2]);
               					processManagement.SetState(id, stan);
               					System.out.println("zmiana stanu procesu o id: "+run[1]);}
               					break;
               				}
               	            case("current"):{
               					processManagement.getProcess(currentProcess).printInformations();
               					break;
               				}
               				case("echo"):{
               					System.out.println("echo: "+ run[1]);
               					String content = "";
               					for(int z=1;z<run.length;z++) { content=content+' '+run[z];}
               					System.out.println(content); break;
               				}
               	                         
   	                        case("pm"):{
   	                            if (run.length < 3) {
   	                                virtualMemory.printVirtualMemory(0, 128);
   	                            }
   	                            else {
   	                                virtualMemory.printVirtualMemory(Integer.parseInt(run[1]), Integer.parseInt(run[2]));
   	                            }
   	                            break;
   	                        } //Wyswietla pamiec wirtualna
   	                        
   	                        case("ppt"):{
   	                            if (run[1] != null) {
   	                                virtualMemory.printPageTable(run[1]);
   	                            }
   	                            break;
   	                        } //Wyswietla tablice stronic
   	                        
               				case("quit"):{
               					d=false;
               					break;
               				}
               				 
               				 
               				//-------------------------------------------------PLIKI I KATALOGI
               				case("pd"): { 		
               					fat.PrintDisk();
               					break;
               				}
               				case("smc"): {
               					System.out.println("ZAWARTOSC DYSKU: ");
               					 fat.ShowMainCatalog();
               					 break;
               				}
               				case("sf"): {
               					 try {
               						 fat.ShowFileInfo(run[1]);
               					 }
               					 catch(Exception ex) {
               						 System.out.println("BLAD OTWIERANIA PLIKU " + ex.getMessage());
               					 }
               					 break;
               				}
               				case("cf"):{
               					if(run[1]!=null) {
               					}
               					if(run.length>2) {
               						String content = "";
               						for(int z=2;z<run.length;z++) { content=content+' '+run[z];}
               						fat.CreateNewFile(run[1],content);
               						//System.out.println("utworzono plik o nazwie: "+arr[1]+ "i zawartosci"+ content); 
               					} 
               					if(run.length==2) {
               						if(run[1]!=null) {
               						fat.CreateEmptyFile(run[1]);
               						}
               					//System.out.println("utworzono plik o nazwie: "+arr[1]);
               					} 
               					break;
               				}
               				case("cat"):{
               					if(run[1]!=null) {
               						System.out.println(fat.GetFilesContent(run[1]));
               						System.out.println("wyswietlam zawartosc pliku o nazwie: "+run[1]);}
               						break;
               				}
               				case("find"):{if(run[1]!=null) {
               					Boolean exist = fat.DoesFileExist(run[1]);
               				    	if(exist==true) System.out.println("Plik istnieje");
               	                    else System.out.println("Plik o podanej nazwie nie istnieje");
               					}
               					 
               					// System.out.println("sprawdzam plik o nazwie: "+arr[1]+" istnieje");
               					break;} //sprawdz czy plik instnieje
               					 
               				case("rm"):{if(run[1]!=null) {
               					try {
               						fat.DeleteFile(run[1]);
               					}
               					catch(Exception ex) {
               						System.out.println("BLAD USUWANIA PLIKU: " + ex.getMessage());
               					}
               					break;
               				}
               				break; 
               			}
               					//usuniecie pliku
               					 //case("cp"):{System.out.println(komenda); break;} //kopiowanie pliku
               					 //procesy
               	     default:
               	    	 System.out.println("Podano nieprawidlowa komende");
   	                     break;
               		 }
           		 } 
             }

		 }}
		 catch(Exception e)
                {
                    e.printStackTrace();
                }
		
                        //System.out.println("Zegnam");
                }
		}}
		
