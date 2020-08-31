import java.util.ArrayList;
import java.util.Arrays;

public class Movie implements Comparable<Movie>
{
    private int movieId;
    private String imdbId, tmdbId; // IDs
    private String title;
    private int releaseYear;
    private String[] genres;

    private ArrayList<Rating> ratings;
    private ArrayList<Tag> tags;
    private double avgRating;
    
    public Movie(int movieId)
    {
        this.movieId = movieId;
    }

    public Movie(int movieId, String imdbId, String tmdbId, String title, int releaseYear, String[] genres,
            ArrayList<Rating> rating, ArrayList<Tag> tags)
    {
        this.movieId = movieId;
        this.imdbId = imdbId;
        this.tmdbId = tmdbId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.setRating(rating);
        this.avgRating = 0;
        this.setTags(tags);
    }

    private void setAvgRating()
    {
        for (Rating r : ratings)
            avgRating += r.getRating();

        avgRating /= ratings.size();
    }

    public Movie(int movieId, String title, int releaseYear, String[] genres)
    {
        this(movieId, "", "", title, releaseYear, genres, new ArrayList<Rating>(), new ArrayList<Tag>());
    }

    // TODO unecessary?
    public Movie()
    {
        this(-1, "", -1, null);
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
        String out = "\nTITLE: " + title;
        out += "\nYEAR: " + releaseYear;
        out += "\nMOVIEID: " + movieId;
        out += "\nIMDBID: " + imdbId;
        out += "\nTMDBID: " + tmdbId;
        out += "\nAvgRating: " + avgRating;
        out += "\nGENRES: " + Arrays.toString(genres);

        return out;
    }
    
    public int compareTo(Movie o)
    {
        return movieId - o.getMovieId(); // 0 is same, pos if >
    }

    public int getMovieId()
    {
        return movieId;
    }

    public void setMovieId(int movieId)
    {
        this.movieId = movieId;
    }

    public String getImdbId()
    {
        return imdbId;
    }

    public void setImdbId(String imdbId)
    {
        this.imdbId = imdbId;
    }

    public String getTmdbId()
    {
        return tmdbId;
    }

    public void setTmdbId(String tmdbId)
    {
        this.tmdbId = tmdbId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getReleaseYear()
    {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear)
    {
        this.releaseYear = releaseYear;
    }

    public String[] getGenres()
    {
        return genres;
    }

    public void setGenres(String[] genres)
    {
        this.genres = genres;
    }

    public ArrayList<Rating> getRating()
    {
        return ratings;
    }

    public void setRating(ArrayList<Rating> rating)
    {
        this.ratings = rating;
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
