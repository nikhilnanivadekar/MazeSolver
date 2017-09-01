package robotics.maze.integration;

import twitter4j.MediaEntity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileUtils
{
    public static File downloadAndSaveMedia(MediaEntity mediaEntity) throws IOException
    {
        InputStream in = new BufferedInputStream(new URL(mediaEntity.getMediaURL()).openStream());
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
        String fileName = mediaEntity.getId() + ".jpg";
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(response);
        fos.close();
        return new File(fileName);
    }
}
