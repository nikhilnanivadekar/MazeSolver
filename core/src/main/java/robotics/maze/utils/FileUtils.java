package robotics.maze.utils;

import robotics.maze.exceptions.AmazeProcessingException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileUtils
{
    public static File downloadAndSaveMedia(String mediaUrl, long id) throws IOException
    {
        InputStream in = new BufferedInputStream(new URL(mediaUrl).openStream());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n = 0;
        while (-1 != (n = in.read(buf)))
        {
            out.write(buf, 0, n);
        }
        out.close();
        in.close();
        byte[] response = out.toByteArray();
        String fileName = id + ".jpg";
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(response);
        fos.close();
        return new File(fileName);
    }

    public static File saveImageToFile(BufferedImage bi, String fileName)
    {
        String format = "JPG";
        try
        {
            File output = new File(fileName+"."+format.toLowerCase());
            boolean cache = ImageIO.getUseCache();
            ImageIO.setUseCache(false);
            ImageIO.write(bi, format, output);
            ImageIO.setUseCache(cache);
            return output;
        }
        catch (IOException e)
        {
            throw new AmazeProcessingException("Failed to write to file'" + fileName + "' because of \"" + e.getMessage() + '"', e);
        }
    }
}
