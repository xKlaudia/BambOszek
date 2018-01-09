package interprocessCommunication;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import processesmanagement.ProcessesManagement;
import syncMethod.Lock;
import processesmanagement.Process;


public class interprocessCommunication
{
    //-------------------------ZMIENNE STATYCZNE-------------------------------

    private static int FIFO_LINES = -1;
    private static String FILE_NAME = "communcation.txt";

    //-------------------------ZMIENNE-----------------------------------------

    //private ProcessesManagement processesmanagment;
    //private Lock lock;

    //-------------------------KONSTRUKTORY------------------------------------

    public interprocessCommunication()
    {
        //this.lock = lock;
        //processesmanagment = new ProcessesManagement();
        //this.lock = new Lock(process.GetName())
    }

    //-------------------------FUNKCJE-----------------------------------------

    //Zapisuje wiadomość do pliku
    public void write(String message, Process process)
    {

       lock.lock(process);
        try
        {
            File file = new File(FILE_NAME);
            if(!file.isFile()) FIFO_LINES = 0;
            else
            {
                ArrayList<String> messages_array = new ArrayList<>();
                FileReader reader = new FileReader(file);
                Scanner out = new Scanner(reader);

                while(out.hasNextLine())
                {
                    messages_array.add(out.nextLine());
                }
                FIFO_LINES = messages_array.size();
                reader.close();
                out.close();
            }
            FileWriter writer = new FileWriter(file, true);
            BufferedWriter in = new BufferedWriter(writer);
            in.write(message + "\n");
            FIFO_LINES++;
            in.close();
            writer.close();
        } catch (IOException ex)
        {
            System.out.println("Error: " + ex.getMessage());
        }
        lock.unlock(process);
    }

    //zczytuje wiadomość z pliku
    public String read(Process process)
    {

        lock.lock(process);
        String message = "";
        try
        {
            File file = new File(FILE_NAME);
            ArrayList<String> messages_array = new ArrayList<>();
            FileReader reader = new FileReader(file);
            Scanner out = new Scanner(reader);

            while(out.hasNextLine())
            {
                messages_array.add(out.nextLine());
            }
            reader.close();
            out.close();
            lock.unlock(process);

            if(messages_array.isEmpty())
            {
                System.out.println("Can't read from empty file");
            }
            else
            {
                message = messages_array.get(0);

                messages_array.remove(0);
                FIFO_LINES--;
                FileWriter writer = new FileWriter(file);
                BufferedWriter in = new BufferedWriter(writer);

                for(int i=0; i<messages_array.size(); i++) in.write(messages_array.get(i) + "\n");

                in.close();
                writer.close();
            }

            return message;

        } catch (FileNotFoundException ex1)
        {
            System.out.println("Error: " + ex1.getMessage());
            return message;
        } catch (IOException ex)
        {
            System.out.println("Error: " + ex.getMessage());
            return message;
        }
    }

    //wyświetla zawartość pliku
    public void show()
    {
        File file = new File(FILE_NAME);
        if(FIFO_LINES < 0) System.out.println("File didn't exist");
        else if(FIFO_LINES == 0) System.out.println("File is empty");
        else
        {
            try
            {
                System.out.println("Number of communicates: " + FIFO_LINES);
                FileReader reader = new FileReader(file);
                Scanner out = new Scanner(reader);
                for(int i=0; i<FIFO_LINES; i++)
                {
                    System.out.println(out.nextLine());
                }

            } catch (FileNotFoundException ex)
            {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    //usuwa plik
    private void delete()
    {
        File file = new File(FILE_NAME);
        file.delete();
        FIFO_LINES = -1;
    }
}
