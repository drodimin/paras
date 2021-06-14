public class fo
{
    public float X = 0, Y = 0, oX = 0, oY = 0, dX = 0, dY = 0;
	public int Type;
	public int dropped = 0;
	public int frame = -1;
    
    public fo(float x, float y, float dx, float dy, int type)
    {
        oX = X = x;
        oY = Y = y;
        dX = dx;
        dY = dy;
        Type = type;
    }
}
