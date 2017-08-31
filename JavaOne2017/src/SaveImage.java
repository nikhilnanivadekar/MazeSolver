import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SaveImage {
    public static void main(String[] args) throws IOException {
        int xAxisSize = 5;
        int yAxisSize = 5;
        int[][] graph = Utils.getGraph5By5();

        BufferedImage bufferedImage = new BufferedImage(xAxisSize * Constants.IMAGE_SCALE, yAxisSize * Constants.IMAGE_SCALE, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //background
        graphics.setBackground(Color.WHITE);
        graphics.fillRect(0, 0, xAxisSize * Constants.IMAGE_SCALE, yAxisSize * Constants.IMAGE_SCALE);

        for (int x = 0; x < xAxisSize; x++) {
            for (int y = 0; y < yAxisSize; y++) {
                int value = graph[x][y];
                graphics.setPaint(Constants.COLOR_MAP.get(value));
                //graphics.fillOval(x * Constants.IMAGE_SCALE, y * Constants.IMAGE_SCALE, Constants.IMAGE_SCALE, Constants.IMAGE_SCALE);
                int x1 = x * Constants.IMAGE_SCALE;
                int y1 = y * Constants.IMAGE_SCALE;

                if (value == Constants.START) {
                    graphics.fillRoundRect(x1, y1, Constants.IMAGE_SCALE, Constants.IMAGE_SCALE, Constants.IMAGE_SCALE / 2, Constants.IMAGE_SCALE / 2);
                    graphics.setPaint(Color.BLACK);
                    graphics.drawString("S", x1 + (Constants.IMAGE_SCALE / 2), y1 + (Constants.IMAGE_SCALE / 2));
                } else if (value == Constants.END) {
                    graphics.fillRoundRect(x1, y1, Constants.IMAGE_SCALE, Constants.IMAGE_SCALE, Constants.IMAGE_SCALE / 2, Constants.IMAGE_SCALE / 2);
                    graphics.setPaint(Color.BLACK);
                    graphics.drawString("E", x1 + (Constants.IMAGE_SCALE / 2), y1 + (Constants.IMAGE_SCALE / 2));
                } else {
                    graphics.fillRect(x1, y1, Constants.IMAGE_SCALE, Constants.IMAGE_SCALE);
                }
            }
        }
        graphics.dispose();

        ImageIO.write(bufferedImage, "jpg", new File("image.jpg"));
        System.out.println("Image Created");
    }
}
