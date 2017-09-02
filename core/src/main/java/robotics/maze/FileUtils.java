package robotics.maze;

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

    public static void saveImageToFile(BufferedImage bi, String fileName)
    {
        try
        {
            ImageIO.write(bi, "PNG", new File(fileName));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Ay, carrumba! Couldn't write '" + fileName + "'", e);
        }
    }
}
