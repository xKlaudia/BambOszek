package processesmanagement;

import java.util.Random;

public class PriorityOverseer {

	private int priorytet;
	private int max_priorytet = 15;
	private int min_priorytet = 1;
	private int priorytet2;
        private int max_priorytet2 = 31;
        private int min_priorytet2 = 16;
	private Random wylosowana_liczba = new Random();
        private Random wylosowana_liczba2 = new Random();

	public int RollPriority() {
		
		priorytet = wylosowana_liczba.nextInt(max_priorytet)+min_priorytet;
		
		return priorytet;
	}
        public int RollRealTimePriority()
        {
           
            priorytet2 = wylosowana_liczba2.nextInt(max_priorytet2)+min_priorytet2;
		
            return priorytet2;
        }
}