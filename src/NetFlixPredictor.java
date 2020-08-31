import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class NetFlixPredictor
{

    // Add fields to represent your database.
    private ArrayList<Movie> movieData;
    private ArrayList<User> userData;

    private double globalMovieAvg;
    private double globalUserAvg;

    // TODO more than one prediction per user

    /**
     * 
     * Use the file names to read all data into some local structures.
     * 
     * @param movieFilePath
     *            The full path to the movies database.
     * @param ratingFilePath
     *            The full path to the ratings database.
     * @param tagFilePath
     *            The full path to the tags database.
     * @param linkFilePath
     *            The full path to the links database.
     */
    public NetFlixPredictor(String movieFilePath, String ratingsFilePath, String tagFilePath, String linkFilePath)
    {
        // Testing movies

        globalMovieAvg = 0;
        globalUserAvg = 0;
        movieData = new ArrayList<Movie>();
        userData = new ArrayList<User>();

        ArrayList<String> movieStringData = FileIO.readFile(movieFilePath);
        ArrayList<String> linkStringData = FileIO.readFile(linkFilePath);
        ArrayList<String> ratingStringData = FileIO.readFile(ratingsFilePath);
        ArrayList<String> tagStringData = FileIO.readFile(tagFilePath);

        MovieLensCSVTranslator translator = new MovieLensCSVTranslator();

        for (int i = 1; i < movieStringData.size(); i++)
        {
            Movie m = translator.parseMovie(movieStringData.get(i));
            translator.parseLinks(m, linkStringData.get(i));
            movieData.add(m);

        }

        Collections.sort(movieData);

        int queuedId = Integer.parseInt(tagStringData.get(1).substring(0, tagStringData.get(1).indexOf(","))); // 15
        int queuedIndex = 1;

        for (int i = 1; i < ratingStringData.size(); i++)
        {
            User user = null;
            if (userData.size() == 0)
                user = translator.parseUser(ratingStringData.get(i), null);
            else
                user = translator.parseUser(ratingStringData.get(i), userData.get(userData.size() - 1));

            if (user != null)
                translator.assignRating(ratingStringData.get(i), user, movieData);
            else
                translator.assignRating(ratingStringData.get(i), userData.get(userData.size() - 1), movieData);

            if (user != null && user.getUserId() == queuedId)
            {

                for (int j = queuedIndex; j < tagStringData.size(); j++)
                {
                    int assignedId = translator.assignTag(tagStringData.get(j), user, movieData);

                    if (assignedId != queuedId)
                    {
                        queuedId = assignedId;
                        queuedIndex = j;
                        break;
                    }

                }

            }

            if (user != null)
                userData.add(user);

        }

        // int count = 0;
        //
        // for (Movie m : movieData)
        // {
        // globalMovieAvg += (m.getAvgRating()) * (m.getRating().size() / 4);
        // count += (m.getRating().size() / 4);
        // }
        //
        // globalMovieAvg /= count;

        int count = 0;

        for (User u : userData)
        {
            globalUserAvg += (u.getAvgRating()) * (u.getRatings().size() / 4);
            count += (u.getRatings().size() / 4);
        }

        globalUserAvg /= count;

        Collections.sort(userData);

    }

    /**
     * If userNumber has rated movieNumber, return the rating. Otherwise, return -1.
     * 
     * @param userNumber
     *            The ID of the user.
     * @param movieNumber
     *            The ID of the movie.
     * @return The rating that userNumber gave movieNumber, or -1 if the user does
     *         not exist in the database, the movie does not exist, or the movie has
     *         not been rated by this user.
     */
    public double getRating(int userID, int movieID)
    {
        int userIndex = Collections.binarySearch(userData, new User(userID));
        if (userIndex < 0)
            return -1;
        
        int movieIndex = Collections.binarySearch(movieData, new Movie(movieID));
        if (movieIndex < 0)
            return -1;
        

        User user = userData.get(userIndex);
        ArrayList<Rating> search = new ArrayList<Rating>(user.getRatings());
        Collections.sort(search);

        int ratingIndex = Collections.binarySearch(search, new Rating(new User(userID), new Movie(movieID)));

        if (ratingIndex < 0)
            return -1;

        return user.getRatings().get(ratingIndex).getRating();
    }

    /**
     * If userNumber has rated movieNumber, return the rating. Otherwise, use other
     * available data to guess what this user would rate the movie.
     * 
     * @param userNumber
     *            The ID of the user.
     * @param movieNumber
     *            The ID of the movie.
     * @return The rating that userNumber gave movieNumber, or the best guess if the
     *         movie has not been rated by this user.
     * @pre A user with id userID and a movie with id movieID exist in the database.
     */
    public double guessRating(int userID, int movieID)
    {
        double r = getRating(userID, movieID);

        if (r != -1)
            return r;

        Movie movie = movieData.get(Collections.binarySearch(movieData, new Movie(movieID)));
        User user = userData.get(Collections.binarySearch(userData, new User(userID)));



        // for (User u : userData)
        // {
        // double simil = similiarity(user, u);
        // if (simil > 0)
        // {
        // U.add(u);
        // sim.add(simil);
        // }
        // }
        // if (match == 0)
        // return movie.getAvgRating();
        //
        // // Avg user bias?
        // double userBias = (baseline - user.getAvgRating());
        // System.out.println(userBias);
        //
        // return (ratingGuess / match) - userBias;

        double rating = 0;
        double index = 0;
        double stdDev = 0;

        HashMap<String, Double> genreAvg = new HashMap<String, Double>();
        HashMap<String, Integer> occurrence = new HashMap<String, Integer>();
        
        for (Rating k : movie.getRating())
        {
            double sim = (similarity(k.getUser(), user));

            // System.out.println(movieSimilarity(movie, k.getMovie()));
            double movieSim = (movieSimilarity(movie, movie));
            // int common = commonItems(movie, k.getMovie());

            rating += sim * (k.getRating() - k.getUser().getAvgRating()) 
                    * (movieSim * 1.01);
            index += Math.abs(sim) * Math.abs(movieSim);

            for (String genre : k.getMovie().getGenres())
            {
                genreAvg.put(genre, 0.0);
                occurrence.put(genre, 0);
            }

        }
        
        double avgTime = 0;
        
        for (Rating k : user.getRatings())
        {
            stdDev += (k.getRating() - k.getUser().getAvgRating()) * (k.getRating() - k.getUser().getAvgRating());
            avgTime += k.getMovie().getReleaseYear();
            
            String[] keys = genreAvg.keySet().toArray(new String[genreAvg.keySet().size()]);
            Arrays.sort(keys);

            int indexFound = -1;

            for (String genre : k.getMovie().getGenres())
            {
                indexFound = Arrays.binarySearch(keys, genre);

                if (indexFound >= 0)
                {
                    genreAvg.put(genre, genreAvg.get(genre) + k.getRating());
                    occurrence.put(genre, (int) (occurrence.get(genre) + 1));
                }

            }

        }

        double avgGenre = 0;
        int count = 0;

        for (String genre : genreAvg.keySet())
        {
            if (occurrence.get(genre) != 0 && genreAvg.get(genre) != 0.0)
            {
                avgGenre += (occurrence.get(genre) / 3) * (genreAvg.get(genre) / occurrence.get(genre));
                count += (occurrence.get(genre) / 3);
            }

        }

        if (count == 0)
        {
            avgGenre = user.getAvgRating();
            count = 1;
        }

        stdDev /= (user.getRatings().size() - 1);
        stdDev = Math.sqrt(stdDev);

        if (index != 0)
            index = 1 / index;

        return (((stdDev * (index * rating)) + (avgGenre / (count))) * 0.80)  
                + ((0.10 * movie.getAvgRating()) + (0.10 * user.getAvgRating()));
    }

    private double movieSimilarity(Movie i, Movie j)
    {
        double sim = 0;
        double iSim = 0;
        double jSim = 0;

        // Cosine similarity
        // for (Rating a : i.getRatings())
        // {
        // for (Rating b : j.getRatings())
        // {
        // if (a.getMovie().getMovieId() == b.getMovie().getMovieId())
        // sim += (a.getRating()) * (b.getRating());
        //
        // jSim += (b.getRating()) * b.getRating();
        // }
        //
        // iSim += (a.getRating()) * a.getRating();
        // }

        ArrayList<Rating> smaller = new ArrayList<Rating>(j.getRating());
        ArrayList<Rating> bigger = new ArrayList<Rating>(i.getRating());

        Movie smallerMovie = j;
        Movie biggerMovie = i;

        if (j.getRating().size() > i.getRating().size())
        {
            smaller = i.getRating();
            bigger = j.getRating();
            smallerMovie = i;
            biggerMovie = j;
        }

        Collections.sort(bigger, new UserComparator());

        for (Rating a : smaller)
        {
            int indexFound = Collections.binarySearch(bigger, a, new UserComparator());

            if (indexFound >= 0)
            {
                Rating b = bigger.get(indexFound);

                // int commonGenres = commonItems(a.getMovie(), b.getMovie());

                jSim += (b.getRating() - biggerMovie.getAvgRating()) * (b.getRating() - biggerMovie.getAvgRating());
                iSim += (a.getRating() - smallerMovie.getAvgRating()) * (a.getRating() - smallerMovie.getAvgRating());
                sim += ((a.getRating() - smallerMovie.getAvgRating()) * (b.getRating() - biggerMovie.getAvgRating()));
            }

        }

        if (iSim == 0 || jSim == 0)
            return 0;

        return (sim) / ((Math.sqrt(iSim)) * (Math.sqrt(jSim)));
    }

    private double similarity(User i, User j)
    {
        double sim = 0;
        double iSim = 0;
        double jSim = 0;

        ArrayList<Rating> smaller = new ArrayList<Rating>(j.getRatings());
        ArrayList<Rating> bigger = new ArrayList<Rating>(i.getRatings());
        User smallerUser = j;
        User biggerUser = i;

        if (j.getRatings().size() > i.getRatings().size())
        {
            smaller = i.getRatings();
            bigger = j.getRatings();
            smallerUser = i;
            biggerUser = j;
        }

        Collections.sort(bigger, new MovieComparator());

        for (Rating a : smaller)
        {
            int indexFound = Collections.binarySearch(bigger, a, new MovieComparator());

            if (indexFound >= 0)
            {
                Rating b = bigger.get(indexFound);

                // int commonGenres = commonItems(a.getMovie(), b.getMovie());

                jSim += (b.getRating() - biggerUser.getAvgRating()) * (b.getRating() - biggerUser.getAvgRating());
                iSim += (a.getRating() - smallerUser.getAvgRating()) * (a.getRating() - smallerUser.getAvgRating());
                sim += ((a.getRating() - smallerUser.getAvgRating()) * (b.getRating() - biggerUser.getAvgRating()));
            }

        }

        if (iSim == 0 || jSim == 0)
            return 0;

        return (sim) / ((Math.sqrt(iSim)) * (Math.sqrt(jSim)));
    }

    private int commonItems(Movie movie, Movie movie2)
    {
        int result = 0;

        Arrays.sort(movie2.getGenres());

        Arrays.sort(movie.getGenres());

        for (String genre : movie.getGenres())
        {
            int index = Arrays.binarySearch(movie2.getGenres(), genre);

            if (index >= 0)
                result++;

        }

        return result;
    }

    /**
     * Recommend a movie that you think this user would enjoy (but they have not
     * currently rated it).
     * 
     * @param userNumber
     *            The ID of the user.
     * @return The ID of a movie that data suggests this user would rate highly (but
     *         they haven't rated it currently).
     * @pre A user with id userID exists in the database.
     */
    public int recommendMovie(int userID)
    {
        User user = userData.get(Collections.binarySearch(userData, new User(userID)));
        
        double mostSimilar = 0;
        User simUser = null;
       
        
        for (User u : userData)
        {
            double sim = similarity(u, user);
            
            if (sim > mostSimilar)
            {
                mostSimilar = sim;
                simUser = u;
            }
        }
        
        double maxRating = 0;
        int movieID = 0;
        Movie m = null;

        for (Rating r : simUser.getRatings())
        {
            Movie movie = r.getMovie();
            
            double guess = guessRating(userID, movie.getMovieId());
            
            int index = Collections.binarySearch(user.getRatings(), new Rating(new User(userID), new Movie(movie.getMovieId())));

            if (index >= 0)
                continue;
            
            int commonGenres = 0;
            
            for (Rating rat : user.getRatings())
                commonGenres += commonItems(movie, rat.getMovie());
            
            if (movie.getAvgRating() > 3.5 && movie.getRating().size() > 10 && commonGenres > 20)
            {
                if (guess > maxRating && guess > user.getAvgRating())
                {
                    maxRating = guess;
                    movieID = movie.getMovieId();
                    m = movie;
                }
            }
            System.out.println("trap");
            
        }
        
        if (m == null)
        {
            for (Movie movie : movieData)
            {
                int index = Collections.binarySearch(user.getRatings(), new Rating(new User(userID), new Movie(movie.getMovieId())));

                if (index >= 0)
                    continue;
                
                double guess = guessRating(userID, movie.getMovieId());
                int commonGenres = 0;
                
                for (Rating r : user.getRatings())
                    commonGenres += commonItems(movie, r.getMovie());
                    
                if (movie.getAvgRating() > 3.7 && movie.getRating().size() > 15 && commonGenres > 20)
                {
                    if (guess > maxRating && guess > user.getAvgRating())
                    {
                        maxRating = guess;
                        movieID = movie.getMovieId();
                        m = movie;
                    }
                }
                
            }
        }
        
        
        System.out.println(user);
        
        return movieID;

    }

    public ArrayList<Movie> getMovieData()
    {
        return movieData;
    }

    public void setMovieData(ArrayList<Movie> movieData)
    {
        
        this.movieData = movieData;
    }

    public ArrayList<User> getUserData()
    {
        return userData;
    }

    public void setUserData(ArrayList<User> userData)
    {
        this.userData = userData;
    }
}

class MovieComparator implements Comparator<Rating>
{
    public int compare(Rating o1, Rating o2)
    {
        return o1.getMovie().compareTo(o2.getMovie());
    }

}

class UserComparator implements Comparator<Rating>
{
    public int compare(Rating o1, Rating o2)
    {
        return o1.getUser().compareTo(o2.getUser());
    }

}
