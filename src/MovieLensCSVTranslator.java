import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/*
 * 
 * 
 * In general:
 * - Files start with column labels first (so skip the first line)
 * - Data can contain commas, but if it does, it always has quotes around it
 * - Data can contain quotes, but if it does, it always has quotes around it and has 2 quotes for each 1 quote that should be in the data
 * - ID numbers can skip around, so we should not use the ID number as a data structure index
 * 
 * Unusual lines of dataset to look out for (and use as test cases):
 * 
 * Movies
 * 56 - Example of commas in the data, escaped using quotes
 * 1007 - Example of a numbers-only title, with some non-alphanumic characters
 * 9126 - Movie without a year
 * 2897 - Movie with many sets of paranthesis before the year
 * 9108 - Movie without any genres
 * 8509 - Movie with a range for the year 
 * 
 * Tags
 * 337 - Example of quotes in the data, escaped using two sets of quotes
 * 
 * Links
 * 912 - Example of a missing piece of link data
 * 
 * Ratings
 * 
 * 
 */
public class MovieLensCSVTranslator
{

    private ArrayList<String> getLinePieces(String line)
    {
        ArrayList<String> pieces = new ArrayList<String>();
        boolean quoted = false;
        int start = 0;

        for (int i = 0; i < line.length(); i++)
        {
            char thisChar = line.charAt(i);
            if (thisChar == '"')
                quoted = !quoted;  
            else if (thisChar == ',' && !quoted)
            {
                pieces.add(line.substring(start, i));
                start = i + 1;
            }
        }

        pieces.add(line.substring(start));

        return pieces;
    }

    public Movie parseMovie(String line)
    {
        ArrayList<String> pieces = getLinePieces(line);
        int id = Integer.parseInt(pieces.get(0));
        String title = pieces.get(1);

        int yearStart = title.lastIndexOf("(");
        int year = -1;

        if (yearStart != -1)
            year = Integer.parseInt(title.substring(yearStart + 1, yearStart + 5));

        String[] genrePieces = pieces.get(2).split("\\|");

        Movie m = new Movie(id, title, year, genrePieces);
        return m;
    }

    public void parseLinks(Movie m, String line)
    {
        ArrayList<String> pieces = getLinePieces(line);
        int movieId = Integer.parseInt(pieces.get(0));

        String imdbId = "";
        if (!pieces.get(1).equals(""))
            imdbId = pieces.get(1);

        String tmdbId = "";
        if (!pieces.get(2).equals(""))
            tmdbId = pieces.get(2);

        if (m.getMovieId() == movieId)
        {
            m.setImdbId(imdbId);
            m.setTmdbId(tmdbId);
        }

    }

    public User parseUser(String line, User recentData)
    {
        ArrayList<String> pieces = getLinePieces(line);
        int userId = Integer.parseInt(pieces.get(0));

        if (recentData == null)
            return new User(userId); 

        if (recentData.getUserId() == userId)
            return null;

        User user = new User(userId); 
        return user;
    }

    public void assignRating(String line, User u, ArrayList<Movie> movieData)
    {

        ArrayList<String> pieces = getLinePieces(line);

        int userId = Integer.parseInt(pieces.get(0));

        if (u.getUserId() != userId)
            return;

        int movieId = Integer.parseInt(pieces.get(1));
        Movie movie = movieData.get(Collections.binarySearch(movieData, new Movie(movieId)));

        double rating = Double.parseDouble(pieces.get(2));
        int timestamp = Integer.parseInt(pieces.get(3));

        
        Rating r = new Rating(timestamp, rating, movie, u);
        
        if (movie != null)
            movie.addRating(r);
        
        u.addRating(r);

    }
    
    public int assignTag(String line, User u, ArrayList<Movie> movieData)
    {

        ArrayList<String> pieces = getLinePieces(line);

        int userId = Integer.parseInt(pieces.get(0));

        if (u.getUserId() != userId)
            return userId;

        int movieId = Integer.parseInt(pieces.get(1));
        Movie movie = movieData.get(Collections.binarySearch(movieData, new Movie(movieId)));

        String tag = pieces.get(2);
        int timestamp = Integer.parseInt(pieces.get(3));
        
        Tag t = new Tag(timestamp, tag, movie, userId);
        
        if (movie != null)
            movie.addTag(t);
        
        u.addTag(t);
        return userId;
    }

}
