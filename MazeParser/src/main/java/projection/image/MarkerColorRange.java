package projection.image;

import static projection.image.ColorConstants.*;

public enum MarkerColorRange
{
    CORNER_MARKER (340,  15, (h, s, l) -> canTellWhatColor(s, l)), // mostly red
    EMPTY_MARKER  (  0, 360, (h, s, l) -> l > 0.93), // mostly white
    WALL_MARKER   (  0, 360, (h, s, l) -> l < 0.07 || s + l < .5), // mostly black
    START_MARKER  ( 75, 150, (h, s, l) -> canTellWhatColor(s, l)), // mostly green
    STOP_MARKER   ( 190,252, (h, s, l) -> canTellWhatColor(s, l)); // mostly blue

    private static boolean canTellWhatColor(double saturation, double luminance)
    {
        if (saturation < S_MIN || saturation > S_MAX) { return false; }
        if (luminance < L_MIN || luminance > L_MAX) { return false; }

        double sCentered = S_RANGE_MID - saturation;
        double lCentered = L_RANGE_MID - luminance;

        return sCentered*sCentered + lCentered*lCentered <= SL_LIMIT_SQ;
    }

    public interface HslConstraint
    {
        boolean matches(double hue, double saturation, double luminance);
    }

    private int hueFrom, hueTo;
    private HslConstraint constraint;

    MarkerColorRange(int newHueFrom, int newHueTo, HslConstraint newHslContraint)
    {
        this.hueFrom = newHueFrom;
        this.hueTo = newHueTo;

        this.constraint = newHslContraint;
    }

    public boolean checkRGB(int red, int green, int blue)
    {
        double[] hsl = this.rgbToHsl(red, green, blue);

        if (this.hueFrom > this.hueTo)
        {
             // looping around 360
            if (hsl[0] < this.hueFrom && hsl[0] > this.hueTo)
            {
                return false;
            }
        }
        else if (hsl[0] < this.hueFrom || hsl[0] > this.hueTo)
        {
            return false;
        }

        return this.constraint.matches(hsl[0], hsl[1], hsl[2]);
    }

    public static double[] rgbToHsl(double red, double green, double blue)
    {
        double redNorm = red/255.0;
        double greenNorm = green/255.0;
        double blueNorm = blue/255.0;

        double minC = Math.min(redNorm,  Math.min(greenNorm, blueNorm));
        double maxC = Math.max(redNorm,  Math.max(greenNorm, blueNorm));

        double delta = maxC - minC;

        double hue = 0;
        if (delta != 0)
        {
//            If Red is max, then Hue = (G-B)/(max-min)
//            If Green is max, then Hue = 2.0 + (B-R)/(max-min)
//            If Blue is max, then Hue = 4.0 + (R-G)/(max-min)
            if (redNorm == maxC)
            {
                hue = (greenNorm - blueNorm)/delta;
            }
            else if (greenNorm == maxC)
            {
                hue = 2.0 + ((blueNorm - redNorm)/delta);
            }
            else if (blueNorm == maxC)
            {
                hue = 4.0 + ((redNorm - greenNorm)/delta);
            }

            hue *= 60;

            if (hue < 0)
            {
                hue += 360;
            }
        }

        double lightness = (maxC + minC) / 2;
        double saturation = delta == 0 ? 0 : delta / (1 - Math.abs(2*lightness - 1));

        if (saturation > 1.0)
        {
            saturation = 1.0;
        }

        return new double[] {hue, saturation, lightness};
    }
}
