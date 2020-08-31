import java.util.ArrayList;
import java.util.Arrays;

public class User implements Comparable<User>
{
    private int userId;
    private ArrayList<Rating> ratings;
    private ArrayList<Tag> tags;
    private double avgRating;
    
    
    
    public User(int userId, ArrayList<Rating> ratings, ArrayList<Tag> tags)
    {
        this.userId = userId;
        this.ratings = ratings;
        this.tags = tags;

    }
    
    private void setAvgRating()
    {
        for (Rating r : ratings)
            avgRating += r.getRating();

        avgRating /= ratings.size();
    }

    public User()
    {
        this(-1, new ArrayList<Rating>(), new ArrayList<Tag>());
    }

    public User(int userId)
    {
        this(userId, new ArrayList<Rating>(), new ArrayList<Tag>());
    }

    public void addRating(Rating r)
    {
        double temp = avgRating * ratings.size();
        ratings.add(r);
        temp += r.getRating();
        avgRating = (temp / ratings.size());
    }

    public void addTag(Tag t)
    {
        tags.add(t);
    }

    public String toString()
    {
        
        String out = "\n(USER)";
        out += "\nUSERID: " + userId;
        out += "\nUserAvgRating: " + avgRating;
        out += "\nRATINGS:\n";

            for (int i  = 0; i < ratings.size(); i++)
                out += (i + 1) + ") " + ratings.get(i).toString() + "\n";

        out += "\nTAGS:\n";

        for (int i  = 0; i < tags.size(); i++)
            out += (i + 1) + ") " + tags.get(i).toString() + "\n";

        return out;

    }
    
    public int compareTo(User o)
    {
        return userId - o.getUserId();
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public ArrayList<Rating> getRatings()
    {
        return ratings;
    }

    public void setRatings(ArrayList<Rating> ratings)
    {
        this.ratings = ratings;
    }

    public ArrayList<Tag> getTags()
    {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags)
    {
        this.tags = tags;
    }

    public double getAvgRating()
    {
        return avgRating;
    }

    public void setAvgRating(double avgRating)
    {
        this.avgRating = avgRating;
    }
}
