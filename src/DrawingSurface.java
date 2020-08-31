
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

import processing.core.PApplet;

public class DrawingSurface extends PApplet
{

    public static final String ratingsFile = "ml-small-dataset" + FileIO.fileSeparator + "ratings.csv";
    public static final String moviesFile = "ml-small-dataset" + FileIO.fileSeparator + "movies.csv";
    public static final String linksFile = "ml-small-dataset" + FileIO.fileSeparator + "links.csv";
    public static final String tagsFile = "ml-small-dataset" + FileIO.fileSeparator + "tags.csv";

    private final float DRAWING_WIDTH = 1280, DRAWING_HEIGHT = 1480;

    private final float movieWidth = DRAWING_WIDTH / 5, movieHeight = DRAWING_HEIGHT / 4;

    private float scaleW, scaleH;

    private int currentMovie;
    private float movieDrawingOffset;
    private float shiftSpeed;

    private NetFlixPredictor predictor;
    private boolean predictorLoaded;

    private ArrayList<DrawingMovie> drawableMovies;
    private DrawingMovie recommendedMovie;

    private int currentUserID;

    public DrawingSurface()
    {

        currentUserID = -1;

        predictorLoaded = false;

        recommendedMovie = new DrawingMovie(null);

        currentMovie = 0;
        shiftSpeed = 0;
        movieDrawingOffset = 0;
        drawableMovies = new ArrayList<DrawingMovie>();
    }

    public void initializePredictor()
    {
        new Thread()
        {
            public void run()
            {
                predictor = new NetFlixPredictor(moviesFile, ratingsFile, tagsFile, linksFile);



                int recommendedID = predictor.recommendMovie(currentUserID);

                int index = Collections.binarySearch(predictor.getMovieData(), new Movie(recommendedID));
                Movie m = predictor.getMovieData().get(index);
                recommendedMovie = new DrawingMovie(m);
                recommendedMovie.downloadArt(DrawingSurface.this);
                
                predictorLoaded = true;
            }
        }.start();

    }

    // The statements in the setup() function
    // execute once when the program begins
    public void setup()
    {
    }

    // The statements in draw() are executed until the
    // program is stopped. Each statement is executed in
    // sequence and after the last line is read, the first
    // line is executed again.
    public void draw()
    {

        background(255); // Clear the screen with a white background

        scaleW = width / DRAWING_WIDTH;
        scaleH = height / DRAWING_HEIGHT;

        pushMatrix();
        scale(scaleW, scaleH);

        if (predictorLoaded)
        {

            fill(0);
            textAlign(LEFT);
            textSize(30);
            text("All Movies", 10, movieHeight / 4 - 50);

            ArrayList<Movie> movieData = predictor.getMovieData();

            for (int i = 0; i < 6 && i + currentMovie < movieData.size(); i++)
            {
                if (i + currentMovie >= drawableMovies.size())
                {
                    DrawingMovie dm = new DrawingMovie(movieData.get(i + currentMovie));
                    dm.downloadArt(this);
                    drawableMovies.add(dm);
                }
                
                drawableMovies.get(i + currentMovie).draw(this,
                        movieDrawingOffset + i * movieWidth + i * movieWidth / 5, movieHeight / 4, movieWidth,
                        movieHeight);
            }

            fill(0);
            textAlign(LEFT);
            textSize(30);
            text("Recommended for user " + currentUserID, 80, DRAWING_HEIGHT / 2.5f - 50);
            
            recommendedMovie.draw(this, 80, DRAWING_HEIGHT / 2.5f, DRAWING_WIDTH / 2,
                    DRAWING_HEIGHT / 2);
            
            if (predictorLoaded)
            {
                text("Title: " + recommendedMovie.getMovie().getTitle(), DRAWING_WIDTH / 2 + 100, DRAWING_HEIGHT / 2.5f);
                text("Release: " + recommendedMovie.getMovie().getReleaseYear(), DRAWING_WIDTH / 2 + 100, DRAWING_HEIGHT / 2.5f + 50);
                text("Average Rating: " +  new BigDecimal(recommendedMovie.getMovie().getAvgRating()).setScale(2, RoundingMode.DOWN)
                        + " (" + recommendedMovie.getMovie().getRating().size() + ")",
                        DRAWING_WIDTH / 2 + 100, DRAWING_HEIGHT / 2.5f + 100);
                
                String genres = "\n";
                
                for (String gen : recommendedMovie.getMovie().getGenres())
                    genres += gen + '\n';
                
                text("Genres: " + genres, DRAWING_WIDTH / 2 + 100, DRAWING_HEIGHT / 2.5f + 150);
                
                String Comments = "\n";
                
                for (Tag t : recommendedMovie.getMovie().getTags())
                    Comments += "\"" + t.getTag() + "\"" + '\n';
                
                if (!Comments.equals("\n"))
                    text("Comments: " + Comments, DRAWING_WIDTH / 2 + 100, DRAWING_HEIGHT / 2.5f + 450);
                
            }
            
            
        }
        else
        {
            fill(0);
            textAlign(CENTER);
            textSize(50);
            text("Press Enter to Load the Predictor", DRAWING_WIDTH / 2, DRAWING_HEIGHT / 2);
        }

        popMatrix();

    }

    public void mousePressed()
    {
        if (mouseButton == LEFT)
        {
            Point2D.Float click = new Point2D.Float(mouseX / scaleW, mouseY / scaleH);

        }

    }

    public void mouseDragged()
    {
        if (mouseButton == LEFT)
        {
            Point2D.Float click = new Point2D.Float(mouseX / scaleW, mouseY / scaleH);

        }
    }

    public void keyReleased()
    {
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT)
        {
            shiftSpeed = 0;
        }
    }

    public void keyPressed()
    {
        if (keyCode == KeyEvent.VK_SPACE)
        {

        }
        else if (keyCode == KeyEvent.VK_LEFT)
        {
            shiftSpeed = (float) Math.min(shiftSpeed + 1, 20);
            movieDrawingOffset -= shiftSpeed;
            if (movieDrawingOffset < -(movieWidth + movieWidth / 5))
            {
                if (currentMovie == predictor.getMovieData().size() - 1)
                {
                    movieDrawingOffset = -(movieWidth + movieWidth / 5);
                }
                else
                {
                    movieDrawingOffset = 0;
                    currentMovie++;
                }
            }

        }
        else if (keyCode == KeyEvent.VK_RIGHT)
        {
            shiftSpeed = (float) Math.min(shiftSpeed + 1, 20);
            movieDrawingOffset += shiftSpeed;
            if (movieDrawingOffset > 0)
            {
                if (currentMovie == 0)
                {
                    movieDrawingOffset = 0;
                }
                else
                {
                    movieDrawingOffset = -(movieWidth + movieWidth / 5);
                    currentMovie--;
                }
            }

        }
        else if (keyCode == KeyEvent.VK_ENTER)
        {
            String id = JOptionPane.showInputDialog("Please enter your user ID:");

            try
            {
                currentUserID = Integer.parseInt(id);
            }
            catch (NumberFormatException ex)
            {
                ex.printStackTrace();
            }

            initializePredictor();

        }
    }

}
