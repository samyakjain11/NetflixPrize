
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PImage;

public class DrawingMovie
{

    private Movie movie;
    private PImage coverArt;

    public DrawingMovie(Movie m)
    {
        this.movie = m;
        coverArt = null;
    }

    public void draw(PApplet drawer, float x, float y, float width, float height)
    {
        drawer.pushStyle();
        drawer.pushMatrix();
        if (movie != null)
        {
            if (coverArt != null)
            {
                drawer.image(coverArt, x, y, width, height);
            }
            
            String title = movie.getTitle();
            
            
            if (title.length() > width / 15)
                title = title.substring(0, 15) + "...";
            
            drawer.text(title, x - 2, y);
        }

        drawer.stroke(0);
        drawer.noFill();
        drawer.rect(x, y, width, height);
        drawer.popStyle();
        drawer.popMatrix();
    }

    public void downloadArt(PApplet drawer)
    {

        Thread downloader = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                Scanner scan = null;
                
                try
                {
                    String link = movie.getImdbId();
   
                    
                    String pageUrlString = "http://www.imdb.com/title/tt" + link + "/";
                    
                    URL pageURL = new URL(pageUrlString);
                    InputStream is = pageURL.openStream();
                    scan = new Scanner(is);
                    
                    String fileData = "";
                    
                    while (scan.hasNextLine())
                    {
                        String line = scan.nextLine();
                        fileData += line + FileIO.lineSeparator;
                    }
                    
                    int indexOfDiv = fileData.indexOf("<div class=\"poster\">");
                    int lastIndexOfItemProp = fileData.lastIndexOf("itemprop=\"image\" />");
                    
                    if (indexOfDiv != -1 || lastIndexOfItemProp != -1)
                    {
                        String narrow = fileData.substring(indexOfDiv, lastIndexOfItemProp);
                        
                        int urlStart = narrow.indexOf("https://ia.media-imdb.com/images/");
                        int urlEnd = narrow.indexOf("_.jpg");
                        
                        String url = narrow.substring(urlStart, urlEnd + 5);
                        coverArt = drawer.loadImage(url);
                    }
                    
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally 
                {
                    if (scan != null)
                        scan.close();
                }
                
                
                
            }

        });

        downloader.start();

    }

    public Movie getMovie()
    {
        return movie;
    }

    public void setMovie(Movie movie)
    {
        this.movie = movie;
    }
    
    

}
