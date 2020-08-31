import java.util.ArrayList;

public class TesterBugs
{

    public static void main(String[] args)
    {

        // Testing movies
        String movieFilePath = "ml-small-dataset" + FileIO.fileSeparator + "movies.csv";

        ArrayList<String> movieStringData = FileIO.readFile(movieFilePath);

        ArrayList<Movie> movieData = new ArrayList<Movie>();
        MovieLensCSVTranslator translator = new MovieLensCSVTranslator();
        for (int i = 1; i < movieStringData.size(); i++)
        {
            Movie m = translator.parseMovie(movieStringData.get(i));
            movieData.add(m);
        }

        for (Movie m : movieData)
        {
            System.out.println(m);
        }

    }

}
