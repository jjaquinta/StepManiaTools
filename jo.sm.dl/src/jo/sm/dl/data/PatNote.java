package jo.sm.dl.data;

public class PatNote
{
    private int mDeltaPitch;
    private int mDeltaVelocity;
    private long mDeltaTick;
    private long mDuration;
    
    public int getDeltaPitch()
    {
        return mDeltaPitch;
    }
    public void setDeltaPitch(int deltaPitch)
    {
        mDeltaPitch = deltaPitch;
    }
    public int getDeltaVelocity()
    {
        return mDeltaVelocity;
    }
    public void setDeltaVelocity(int deltaVelocity)
    {
        mDeltaVelocity = deltaVelocity;
    }
    public long getDeltaTick()
    {
        return mDeltaTick;
    }
    public void setDeltaTick(long deltaTick)
    {
        mDeltaTick = deltaTick;
    }
    public long getDuration()
    {
        return mDuration;
    }
    public void setDuration(long duration)
    {
        mDuration = duration;
    }
}
