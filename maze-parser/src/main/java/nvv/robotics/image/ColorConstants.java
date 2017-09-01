package nvv.robotics.image;

public class ColorConstants
{
    public static final double S_MAX  = 1.00;
    public static final double S_MIN  = 0.318;

    public static final double L_MAX  = 0.80;
    public static final double L_MIN  = 0.10;

    public static final double S_RANGE_MID = (S_MAX + S_MIN)/2.0;
    public static final double L_RANGE_MID = (L_MAX + L_MIN)/2.0;

    public static final double S_RANGE_LEN = (S_MAX - S_MIN)/2.0;
    public static final double L_RANGE_LEN = (L_MAX - L_MIN)/2.0;

    public static final double SL_LIMIT_SQ = S_RANGE_LEN*S_RANGE_LEN + L_RANGE_LEN*L_RANGE_LEN;
}
