public class Tag
{
    private int userId; 
    private int timestamp; //Seconds from midnight (could be converted to Time class)
    private String tag; // User generated comments
    private Movie movie; //Movie the rating is related to
    // TODO movieId?
    
    public Tag(int timestamp, String tag, Movie movie, int userId)
    {
        this.timestamp = timestamp;
        this.userId = userId;
        this.tag = tag;
        this.movie = movie;
    }
    
    public Tag()
    {
        this(-1, null, null, -1);
    }
    
    public String toString()
    {
        String out = "\nMOVIE: " + movie.toString();
        out += "\nTIMESTAMP: " + timestamp;
        out += "\nTAG: " + tag;
        out += "\nUSERID: " + userId;
        
        return out;
        
    }

    public int getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(int timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public Movie getMovie()
    {
        return movie;
    }

    public void setMovie(Movie movie)
    {
        this.movie = movie;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }
    
    
   
}
