package Interpreter;


import java.util.ArrayList;
//import memorymanagement.Memory;
import fileSystem.FAT;
import interproccessCommunication.interprocessCommunication;
import processesmanagment.ProcessesManagement;
import processesmanagment.Process;
import processesmanagment.PCB;
import memoryManagement.VirtualMemory;
import cpudispatcher.CPUDispatcher;
import java.io.IOException;

public class Interpreter {

    private int Reg_A=0, Reg_B=0, Reg_C=0, Reg_D = 0;
    //private bool Flag_E = 0;      //Flaga do bledu wykonywania rozkazu
    private Procesor procesor;
    private CPUDispatcher processor;
    private VirtualMemory memory;
    private interprocessCommunication communication;
    private ProcessesManagement manager;
    private FAT filesystem;
    private PCB PCB;            //Zmienna do kopii PCB procesu
    private int CMDCounter;     //Licznik rozkazu do czytania z pami�ci
    private int CCKCounter;     //licznik do sprawdzania czy program się skończył

//-------------------------------------------------------------------------------------------------------------------

    public Interpreter(CPUDispatcher processor, VirtualMemory memory, interprocessCommunication communication, ProcessesManagement manager, FAT filesystem, PCB PCB, int CMDCounter, int CCKCounter) {
        this.processor = processor;
        this.memory = memory;
        this.communication = communication;
        this.manager = manager;
        this.filesystem = filesystem;
        this.PCB = PCB;
        this.CMDCounter = CMDCounter;
        this.CCKCounter = CCKCounter;
    }



    public Interpreter(ProcessesManagement manager, FAT filesystem, VirtualMemory memory) {                   //Memory memory, bez tego
        this.memory=memory;
        this.manager=manager;
        this.filesystem=filesystem;
        procesor=new Procesor();
    }

//-------------------------------------------------------------------------------------------------------------------

    public int RUN(Process Running) throws Exception {
        this.PCB=Running.GetPCB();
        interprocessCommunication communication = new interprocessCommunication();

        CCKCounter = 0;
        CMDCounter = Running.GetPCB().getCommandCounter(); //Pobieranie licznika rozkar�w

        this.Reg_A = Running.GetPCB().getA(); //Pobieranie stanu rejestru A
        this.Reg_B = Running.GetPCB().getB(); //Pobieranie stanu rejestru B
        this.Reg_C = Running.GetPCB().getC();//Pobieranie stanu rejestru C
        this.Reg_D = Running.GetPCB().getD();//Pobieranie stanu rejestru D 

        
        procesor.Set_A(Reg_A);          //Ustawianie wartosci rejestru A do pami�ci
        procesor.Set_B(Reg_B);          //Ustawianie wartosci rejestru B do pami�ci
        procesor.Set_C(Reg_C);
        procesor.Set_D(Reg_D);  //Ustawianie wartosci rejestru C do pami�ci

        String Instruction = "";

        Instruction = GetInstruction(Running.GetPCB());   //Zmienna pomocnicza do �adowania instrukcji z pami�ci
        Execute(Instruction,Running);

        ReturnToPCB(Running.GetPCB());
        //Running.SetPCB();
        return 0;
    }

//-------------------------------------------------------------------------------------------------------------------

    void Execute(String Instruction, Process Running) throws Exception {
        int x = 0;  //takie co� do sprawdzania czy by�a spacja
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
        case "AD": // Dodawanie warto�ci
            if (What) {
                procesor.SetValue(P1, GetValue(P1) + GetValue(P2));
            } else {
                procesor.SetValue(P1, GetValue(P1) + Integer.parseInt(P2));
            }
            break;

        case "SB": // Odejmowanie warto�ci
            if (What) {
                procesor.SetValue(P1, GetValue(P1) - GetValue(P2));
            } else {
                procesor.SetValue(P1, GetValue(P1) - Integer.parseInt(P2));
            }
            break;

        case "ML": // Mno�enie warto�ci
            if (What) {
                procesor.SetValue(P1, GetValue(P1) * GetValue(P2));
            } else {
                procesor.SetValue(P1, GetValue(P1) * Integer.parseInt(P2));
            }
            break;

        case "MV": // Umieszczenie warto�ci
            if (What) {
                procesor.SetValue(P1, GetValue(P2));
            } else {
                procesor.SetValue(P1, Integer.parseInt(P2));
            }
            break;

//-----------------------------------------------------------------------   PLIKI

        case "CE": // Tworzenie pliku
            if(filesystem.CreateEmptyFile(P1)==true) {
                //filesystem.createEmptyFile(P1);
            }
            else {
                Running.SetState(2);
            }
            break;

        case "CF": // Tworzenie pliku z zawartoscia
            if (What) {
                if(filesystem.CreateNewFile(P1,Integer.toString(GetValue(P2)))==true) {
                    //filesystem.CreateNewFile(P1,Integer.toString(GetValue(P2)));
                }
                else{
                    Running.SetState(2);
                }
            } else {
                if(filesystem.CreateNewFile(P1, P2)==true) {
                    //filesystem.createFile(P1, P2);
                }
                else {
                    Running.SetState(2);
                }
            }
            break;

        /*case "WF": // Dopisanie do pliku
            filesystem.OpenFile(P1);
            if (What) {
                if(filesystem.appendToFile(P1,Integer.toString(GetValue(P2)))==1){
                    filesystem.appendToFile(P1,Integer.toString(GetValue(P2)));
                }
                else {
                    Running.Setstan(2);
                }
            }
            else {
                if(filesystem.appendToFile(P1,P2)==1) {
                    filesystem.appendToFile(P1, P2);
                }
                else {
                    Running.Setstan(2);
                }
            }
            filesystem.closeFile(P1);
            break;

        case "DF": // Usuwanie pliku
            filesystem.openFile(P1);
            if((filesystem.deleteFile(P1))==1) {
                filesystem.deleteFile(P1);
            }
            else {
                Running.Setstan(2);
            }
            break;*/

//-----------------------------------------------------------------------   JUMPY I KONCZENIE

        case "JP": // Skok do rozkazu
            CMDCounter = Integer.parseInt(P1);
            break;

        case "JX": // Skok do rozkazu, je�li rejestr != 0
            if(GetValue(P1)!=0) {
                CMDCounter = Integer.parseInt(P2) + Running.GetPCB().getCommandCounter();
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
            memory.loadProcess(P1, P2, Integer.getInteger(P3));
            manager.NewProcess_XC(P1);
            //}
            //else {
            //    Running.Setstan(2);
            //}
            break;

        case "XZ": // -- wstrzymanie procesu
            Running.SetState(1);
            break;
        }

        }


//-------------------------------------------------------------------------------------------------------------------

    private boolean CheckP2(String P2) {
        if(P2 == "A" || P2 == "B" || P2 == "C") {
            return true;
        }
        else {
            return false;
        }
    }

//-------------------------------------------------------------------------------------------------------------------

    private void ReturnToPCB(PCB Running) {
            Running.setA(procesor.Get_A());
            Running.setB(procesor.Get_B());
            Running.setC(procesor.Get_C());
            Running.setD(procesor.Get_D());

            Running.setCommandCounter(CMDCounter);
    }

//-------------------------------------------------------------------------------------------------------------------

    private String GetInstruction(PCB Running) throws IOException {
        String Instruction = "";
        int Counter=0;

        do{
            //Instruction += Running.PageTable.readFromMemory(CMDCounter); //pobieranie z pami�ci znaku o danym numerze, oraz nale��cego do danego procesu

            Instruction += memory.readMemory(CMDCounter);
            CMDCounter++;
            Counter++;
        }while (Instruction.charAt(Counter)!=',' && Instruction.charAt(Counter)!=';');
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
