package Shell;

	import java.util.Scanner;

	public class Shell {
		//TUTAJ MUSZA SIE ZNALEZC OBIEKTY KLAS DO WYWOLYWANIA FUNKCJI
	public	ProcessesManagement processManagement;
	public FAT fat;	
		
		
		public Shell() {
			this.processManagement=new ProcessesManagement();
			this.fat=new FAT();
		Dzialaj();	
		}
	public static boolean d=true;
		public void Dzialaj() {
		
			while(d) {
			String komenda; 
			System.out.print("$>");
		    Scanner odczyt = new Scanner(System.in); //obiekt do odebrania danych od u¿ytkownika
		 
		      komenda = odczyt.nextLine();
		      String[] arr = komenda.split(" ");
		     // for ( String ss : arr) {

		       //   System.out.println(ss);
		     //}
		 switch(arr[0]) {
		 //katalogi
		 case("cd"):{break;} //zmiana katalogu
		 
		 case("mkdir"):{break;} //utworz katalog
		 
		 case("rmdir"):{break;}//usun katalog
		 
		 case("ls"):{ break;} //wyswietla zawartosc katalogu
		
		 case("cf"):{
			 if(arr.length>2) {
				String content;
				for(int i=2;i<arr.length;i++) { content=content+' '+arr[i];}
				 fat.CreateNewFile(arr[1],content);} 
			if(arr.length==2) {fat.CreateEmptyFile(arr[1]);} 
				 break;} //utworz plik o nazwie
		
		 case("find"):{fat.DoesFileExist(arr[1]);break;} //sprawdz czy plik instnieje
		
		 case("cat"):{fat.PrintFilesContent(arr[1]);break;} //wypisz zawartosc pliku
		 
		 case("rm"):{fat.DeleteFile(arr[2]);break;} //usuniecie pliku
		 //case("cp"):{System.out.println(komenda); break;} //kopiowanie pliku
		 //procesy
		
		 case("ps"):{processManagement.printProcessListInformations();break;} //wyswietla procesy
		 
		 case("pi"):{int id = Integer.parseInt(arr[1]);processManagement.printProcessInformations(id);break;} //informacje o procesie po id
		 
		 case("pn"):{processManagement.getProcess(arr[1]);break;} // proces po nazwie
		
		 case("kill"):{int id = Integer.parseInt(arr[1]); processManagement.DeleteProcessWithID(arr[1]);break;} //zabija proces po id
		 
		 case("nice"):{break;} //zmiana priorytetu procesu
		 //shell
		 
		 case("echo"):{
			 String content;
				for(int i=2;i<arr.length;i++) { content=content+' '+arr[i];}
				System.out.println(content);
		 }
		 
		 case("quit"):{d=false;break;}
		 }
		    
		}
			System.out.println("Zegnam");
		}
}
