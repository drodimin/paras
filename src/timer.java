public class timer
{
	int duration;
	long last;
	
	public timer()
	{
	}
	
	public void setDur(int dur)
	{
		duration = dur;
	}
	
	public void setTime(long time)
	{
		last = time;
	}
	
	public long elapsed(long curr)
	{
		return curr - last;
	}
		
	public boolean event(long curr)
	{
		if(curr - last >= duration)
		{
			last = curr;
			return true;
		}
		return false;
	}
}