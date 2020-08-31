import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileIO
{
    public static final String fileSeparator = System.getProperty("file.separator");
    public static final String lineSeparator = System.getProperty("line.separator");

    // Checked Exception
    public static ArrayList<String> readFile(String filename)
    {
        Scanner scan = null;
        try
        {
            ArrayList<String> fileData = new ArrayList<String>();
            FileReader reader = new FileReader(filename);
            scan = new Scanner(reader);

            while (scan.hasNextLine())
            {
                String data = scan.nextLine();
                fileData.add(data);
            }

            return fileData;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (scan != null)
                scan.close();
        }

        return null;
    }

    public static void writeFile(String filename, ArrayList<String> fileData)
    {
        FileWriter write = null;
        try
        {
            write = new FileWriter(filename);

            for (String line : fileData)
            {
                write.write(line);
                write.write(lineSeparator);
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (write != null)
                    write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }

    }
}
