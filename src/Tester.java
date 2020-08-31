import java.util.ArrayList;

import javax.lang.model.util.SimpleAnnotationValueVisitor6;

public class Tester
{
    public static final String movieFilePath = "ml-small-dataset" + FileIO.fileSeparator + "movies.csv";
    public static final String linkFilePath = "ml-small-dataset" + FileIO.fileSeparator + "links.csv";
    public static final String tagFilePath = "ml-small-dataset" + FileIO.fileSeparator + "tags.csv";
    public static final String ratingsFilePath = "ml-small-dataset" + FileIO.fileSeparator + "ratings.csv";

    public static void main(String[] args)
    {

        ArrayList<Movie> movieData = new ArrayList<Movie>();
        ArrayList<User> userData = new ArrayList<User>();

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

        int queueId = Integer.parseInt(tagStringData.get(1).substring(0, tagStringData.get(1).indexOf(","))); // 15
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

            if (user != null && user.getUserId() == queueId)
            {
                
                for (int j = queuedIndex; j < tagStringData.size(); j++)
                {
                    int assignedId = translator.assignTag(tagStringData.get(j), user, movieData);
                    
                    if (assignedId != queueId)
                    {
                        queueId = assignedId;
                        queuedIndex = j;
                        break;
                    }
                        
                }
                    
            }   
            
            if (user != null)
                userData.add(user);
        }
        
        User user = userData.get(0);
        Movie movie = movieData.get(30);
        double userBias = (user.getAvgRating() - movie.getAvgRating());
        
        System.out.println(movie.getRating());
        
        System.out.println(similiarity(userData.get(0), userData.get(6)));
        

    }
    
    private static double similiarity(User i, User j)
    {
        double sim = 0;
        double iSim = 0;
        double jSim = 0;
//        for (Rating a : i.getRatings())
//        {
//            for (Rating b : j.getRatings())
//            {
//                if (a.getMovie().getMovieId() == b.getMovie().getMovieId())
//                    sim += (a.getRating()) * (b.getRating());
//                
//                jSim += (b.getRating()) * b.getRating();
//            }
//            
//            iSim += (a.getRating()) * a.getRating();
//        }
        
        for (Rating a : i.getRatings())
        {
            for (Rating b : j.getRatings())
            {
                if (a.getMovie().getMovieId() == b.getMovie().getMovieId())
                {
                    jSim += (b.getRating() - j.getAvgRating()) * (b.getRating() - j.getAvgRating());
                    iSim += (a.getRating() - i.getAvgRating()) * (a.getRating() - i.getAvgRating());
                    sim += (a.getRating() - i.getAvgRating()) * (b.getRating() - j.getAvgRating());
                }

            }
            
            
        }
       
        
        return (sim) / ((Math.sqrt(iSim)) * (Math.sqrt(jSim)));
    }

}
