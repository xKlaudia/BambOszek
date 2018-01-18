package Interpreter;


import java.util.ArrayList;
import fileSystem.FAT;
import fileSystemExceptions.*;
import interprocessCommunication.interprocessCommunication;
import processesmanagement.ProcessesManagement;
import processesmanagement.Process;
import processesmanagement.ProcessStateOverseer;
import processesmanagement.PCB;
import memoryManagement.VirtualMemory;
import cpudispatcher.CPUDispatcher;
import java.io.IOException;

public class Interpreter {

    private int Reg_A=0, Reg_B=0, Reg_C=0, Reg_D = 0;
    /*public static final int NEWBIE = ProcessStateOverseer.newbie,
    						READY = ProcessStateOverseer.ready,
    						ACTIVE = ProcessStateOverseer.active,
    						WAITING = ProcessStateOverseer.waiting,
    						FINISHED = ProcessStateOverseer.finished;*/
    						
    //private bool Flag_E = 0;      //Flaga do bledu wykonywania rozkazu
    private Procesor procesor;
    private CPUDispatcher processor;
    private VirtualMemory memory;
    private interprocessCommunication communication;
    private ProcessesManagement manager;
    private FAT filesystem;
    private PCB PCB;            //Zmienna do kopii PCB procesu
    private Process process;
    private int CMDCounter;     //Licznik rozkazu do czytania z pamieci
    private int CCKCounter;     //licznik do sprawdzania czy program sie skonczyl

//-------------------------------------------------------------------------------------------------------------------

    public Interpreter(CPUDispatcher processor, VirtualMemory memory, interprocessCommunication communication, ProcessesManagement manager, FAT filesystem, Process process, int CMDCounter, int CCKCounter) {
        this.processor = processor;
        this.memory = memory;
        this.communication = communication;
        this.manager = manager;
        this.filesystem = filesystem;
        this.process = process;
        this.CMDCounter = CMDCounter;
        this.CCKCounter = CCKCounter;
    }



    public Interpreter(ProcessesManagement manager, FAT filesystem, VirtualMemory memory) {                   //Memory memory, bez tego
        this.memory=memory;
        this.manager=manager;
        this.filesystem=filesystem;
        procesor=new Procesor();
    }

    public void CPU() throws Exception
    {
        if(manager.processesList.size()!=0)
        {
            int max = manager.processesList.get(0).GetCurrentPriority();
            int highestProcessNumber = 0;
            
            for(int i=0; i<manager.processesList.size(); i++)
            {
                if(manager.processesList.get(i).GetCurrentPriority()>max)
                {
                    max = manager.processesList.get(i).GetCurrentPriority();
                    highestProcessNumber = i;
                }
            }
            manager.processesList.get(highestProcessNumber).SetState(2);
        
            for(int i=0; i<manager.processesList.size(); i++)
            {
                if(i!=highestProcessNumber)
                {
                    if(manager.processesList.get(i).GetCurrentPriority()<15) manager.processesList.get(i).SetCurrentPriority(manager.processesList.get(i).GetCurrentPriority()+1);
                    if(manager.processesList.get(i).GetState()==2) manager.processesList.get(i).SetState(3);
                }
            }
        }
        else throw new Exception("Processes list is empty");
    }
    
//-------------------------------------------------------------------------------------------------------------------

    public int RUN(Process Running) throws Exception {
        this.process=Running;
        interprocessCommunication communication = new interprocessCommunication();

        CCKCounter = 0;
        CMDCounter = Running.GetCommandCounter(); //Pobieranie licznika rozkar�w

        this.Reg_A = Running.GetRegA(); //Pobieranie stanu rejestru A
        this.Reg_B = Running.GetRegB(); //Pobieranie stanu rejestru B
        this.Reg_C = Running.GetRegC(); //Pobieranie stanu rejestru C
        this.Reg_D = Running.GetRegD(); //Pobieranie stanu rejestru D 

        
        procesor.Set_A(Reg_A);          //Ustawianie wartosci rejestru A do pamieci
        procesor.Set_B(Reg_B);          //Ustawianie wartosci rejestru B do pamieci
        procesor.Set_C(Reg_C);			
        procesor.Set_D(Reg_D); 		    //Ustawianie wartosci rejestru D do pamieci

        String Instruction = "";

        //Instruction = GetInstruction(Running.GetPCB());   //Zmienna pomocnicza do ladowania instrukcji z pamieci
        for (;;) {
            Instruction += memory.readMemory(CMDCounter);
            CMDCounter++;
            if (Instruction.charAt(Instruction.length() - 1) == ';')
                break;
        }
        Execute(Instruction,Running);

        ReturnToPCB(Running);
        //Running.SetPCB();
        return 0;
    }

//-------------------------------------------------------------------------------------------------------------------

    void Execute(String Instruction, Process Running) throws Exception {
        int x = 0;  //takie cos do sprawdzania czy byla spacja
        int i = 1;  //licznik do podzialu rozkazu na segmenty
        String CMD = "";
        String P1 = "";
        String P2 = "";
        String P3 = "";

//-----------------------------------------------------------------------

        while(i < 5) {
            if(i == 1) {
                while(Instruction.charAt(x)!=' ' && Instruction.charAt(x)!=',' && Instruction.charAt(x)!=';') {
                    CMD += Instruction.charAt(x);
                    CCKCounter++;
                    x++;
                }
                if(Instruction.charAt(x)==' '){
                    i++;
                    x++;
                }
                else if(Instruction.charAt(x)==','){
                    break;
                }
                else if(Instruction.charAt(x)==';'){
                    break;
                }
            }
            else if(i == 2) {
                while(Instruction.charAt(x)!=' ' && Instruction.charAt(x)!=',' && Instruction.charAt(x)!=';') {
                    P1 += Instruction.charAt(x);
                    CCKCounter++;
                    x++;
                }
                if(Instruction.charAt(x)==' '){
                    i++;
                    x++;
                }
                else if(Instruction.charAt(x)==','){
                    break;
                }
                else if(Instruction.charAt(x)==';'){
                    break;
                }
            }
            else if(i == 3) {
                while(Instruction.charAt(x)!=' ' && Instruction.charAt(x)!=',' && Instruction.charAt(x)!=';') {
                    P2 += Instruction.charAt(x);
                    CCKCounter++;
                    x++;
                }
                if(Instruction.charAt(x)==' '){
                    i++;
                    x++;
                }
                else if(Instruction.charAt(x)==','){
                    break;
                }
                else if(Instruction.charAt(x)==';'){
                    break;
                }
            }
            else if(i == 4) {
                while(Instruction.charAt(x)!=' ' && Instruction.charAt(x)!=',' && Instruction.charAt(x)!=';') {
                    P3 += Instruction.charAt(x);
                    CCKCounter++;
                    x++;
                }
                CCKCounter++;
                i++;
            }
            else {
                break;
                }

        }
        CCKCounter++;

        Boolean What = CheckP2(P2);

//-----------------------------------------------------------------------   ARYTMETYKA

        switch (CMD) {
            case "AD": // Dodawanie wartosci
                if (What) {
                    procesor.SetValue(P1, GetValue(P1) + GetValue(P2));
                } else {
                    procesor.SetValue(P1, GetValue(P1) + Integer.parseInt(P2));
                }
                break;

            case "SB": // Odejmowanie wartosci
                if (What) {
                    procesor.SetValue(P1, GetValue(P1) - GetValue(P2));
                } else {
                    procesor.SetValue(P1, GetValue(P1) - Integer.parseInt(P2));
                }
                break;

            case "ML": // Mnozenie wartosci
                if (What) {
                    procesor.SetValue(P1, GetValue(P1) * GetValue(P2));
                } else {
                    procesor.SetValue(P1, GetValue(P1) * Integer.parseInt(P2));
                }
                break;

            case "MV": // Umieszczenie wartosci
                if (What) {
                    procesor.SetValue(P1, GetValue(P2));
                } else {
                    procesor.SetValue(P1, Integer.parseInt(P2));
                }
                break;

    //-----------------------------------------------------------------------   PLIKI

            case "CE": // Tworzenie pliku pustego
                if(What) {
                	try  {
                		filesystem.CreateEmptyFile(P1);
                	}
                	catch(IllegalFileNameException ex) {
                        System.out.println("BLAD NAZWY PLIKU: " + ex.getMessage());
                        Running.SetState(2);
                	}
                	catch(OutOfBlocksException ex2) {
                		System.out.println("BLAD PAMIECI: " + ex2.getMessage());
                        Running.SetState(2);
                	}
                	catch(Exception ex3) {
                        System.out.println("BLAD TYPU NIEOKRESLONEGO: " + ex3.getMessage());
                        Running.SetState(2);
                	}
                } 
                break;


           case "CF": // Tworzenie pliku z zawartoscia
        	   	filesystem.OpenFile(P1, Running);
    	   		if (What) {
            		try {
            			filesystem.CreateNewFile(P1,Integer.toString(GetValue(P2)));
            		}
            		catch (Exception ex) {
            			System.out.println("Blad: " + ex.getMessage());
            			Running.SetState(2);
            		}
                } 
            	else {
                	try {
            			filesystem.CreateNewFile(P1,P2);
            		}
            		catch (Exception ex) {
            			System.out.println("Blad: " + ex.getMessage());
            			Running.SetState(2);
            		}
                }
    	   		filesystem.CloseFile(P1, Running);
                break;   


            case "WF": // Dopisanie do pliku
            	try {
            		filesystem.OpenFile(P1, process);
            	}
            	catch(Exception ex) {
            		System.out.println("BLAD OTWIERANIA: " + ex.getMessage());
            		Running.SetState(2);
            		break;
            	}
            	if(What) {
            		try {
            			filesystem.AppendToFile(P1, P2);
            		}
            		catch(Exception ex2) {
            			System.out.println("BLAD DOPISYWANIA: " + ex2.getMessage());
            			Running.SetState(2);
            			break;
            		}            		
            	}
            	else {
            		Running.SetState(2);
            	}
            	try {
            		filesystem.CloseFile(P1, process);
            	}
            	catch(Exception ex2) {
            		System.out.println("BLAD ZAMYKANIA PLIKU: " + ex2.getMessage());
            		Running.SetState(2);
            	}
            	
            	break;
            	
            case "DF": // Usuwanie pliku
                filesystem.OpenFile(P1, Running);
                try {
                    filesystem.DeleteFile(P1);
                }
                catch(Exception ex) {
                	System.out.println("BLAD USUWANIA PLIKU: " + ex.getMessage());
                    Running.SetState(2);
                }
                break;
                

    //-----------------------------------------------------------------------   JUMPY I KONCZENIE

            case "JP": // Skok do rozkazu
                CMDCounter = Integer.parseInt(P1);
                break;

            case "JX": // Skok do rozkazu, je�li rejestr != 0
                if(GetValue(P1)!=0) {
                    CMDCounter = Integer.parseInt(P2) + Running.GetCommandCounter();
                }
                break;

            case "EX": // Koniec programu
                Running.SetState(4);
                break;

    //-----------------------------------------------------------------------   PROCESY

            case "XR": // czytanie komunikatu;
                //if(communication.read(manager.getProcess(P1)==1){
                    communication.read(manager.getProcess(P1));
                    String pom = "";
                    procesor.SetValue("B", Integer.parseInt(pom));
                //}
                //else {
                  //  Running.Setstan(2);
                    //}
                break;

            case "XS": // -- Wys�anie komunikatu;
                //if(communication.writePipe(P1, P2)==1) {
                    communication.write(P1,manager.getProcess(P2));
                //}
                //else {
                  //  Running.Setstan(2);
               // }
                break;
        /*
            case "XF": // -- znalezienie ID procesu (P1);
                processor.Set_A(manager.GetIDwithName(P1));
                break;
        */
    //        case "XP": // -- Stworzenie potoku
    //            if(communication.createPipe(P1)==1) {
    //                communication.createPipe(P1);
    //            }
    //            else {
    //                Running.Setstan(2);
    //            }
    //            break;
    //
    //        case "XE": // -- Usuwanie potoku
    //            if(communication.deletePipe(P1)==1) {
    //                communication.deletePipe(P1);
    //            }
    //            else {
    //                Running.Setstan(2);
    //            }
    //            break;

            case "XC": // -- tworzenie procesu (P1,P2);
                //if(manager.createprocess(P1,P2)==1) {
                //memory.loadProcess(P1, P2, Integer.getInteger(P3));
                manager.NewProcess_XC(P1, Integer.getInteger(P2));
                //}
                //else {
                //    Running.Setstan(2);
                //}
                break;

            case "XZ": // -- wstrzymanie procesu
                Running.SetState(1);
                break;

    //-----------------------------------------------------------------------   PAMIĘĆ WIRTUALNA

            case "XA": // -- wczytywanie procesu do pliku wymiany
                memory.loadProcess(P1, P2, Integer.parseInt(P3));
                break;

            case "XF": // -- usuwanie procesu z pamięci
                memory.deleteProcess(P1);
                break;

            case "WM": // -- pisanie do pamięci
                for (int j = 0; j < P2.length(); j++) {
                    memory.writeMemory(Integer.parseInt(P1) + j, P2.charAt(j));
                }
                break;
                
            case "RM": // -- czytanie z pamięci
                for (int j = 0; j < Integer.parseInt(P2); j++) {
                    System.out.print(memory.readMemory(Integer.parseInt(P1) + j));
                    System.out.println();
                }
                break;
            }
        }

//-------------------------------------------------------------------------------------------------------------------

    private boolean CheckP2(String P2) {
        if(P2 == "A" || P2 == "B" || P2 == "C" || P2 == "D") return true;
        else return false;
    }

//-------------------------------------------------------------------------------------------------------------------

    private void ReturnToPCB(Process Running) {
            Running.SetRegA(procesor.Get_A());
            Running.SetRegB(procesor.Get_B());
            Running.SetRegC(procesor.Get_C());
            Running.SetRegD(procesor.Get_D());

            Running.SetCommandCounter(CMDCounter);
    }

//-------------------------------------------------------------------------------------------------------------------

    private String GetInstruction(PCB Running) throws Exception, IOException {
        String Instruction = "";
        int Counter=0;

        do{
            //Instruction += Running.PageTable.readFromMemory(CMDCounter); //pobieranie z pamieci znaku o danym numerze, oraz nalezacego do danego procesu

            Instruction += memory.readMemory(CMDCounter);
            CMDCounter++;
            Counter++;
        }while (Instruction.charAt(Counter) != ',' && Instruction.charAt(Counter) != ';');
        return Instruction;
    }

//-------------------------------------------------------------------------------------------------------------------

    private int GetValue(String P1) {
        switch (P1) {
        case "A":
            return procesor.Get_A();
        case "B":
            return procesor.Get_B();
        case "C":
            return procesor.Get_C();
        case "D":
            return procesor.Get_D();
        }
        return 0;
    }

//-------------------------------------------------------------------------------------------------------------------

    public void Show_Regs() {
        System.out.println("Register A: " + procesor.Get_A());
        System.out.println("Register B: " + procesor.Get_B());
        System.out.println("Register C: " + procesor.Get_C());
        System.out.println("Register D: " + procesor.Get_D());
        System.out.println("Command counter: " + this.CMDCounter);
    }

}
