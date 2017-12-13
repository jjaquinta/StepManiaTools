package jo.sm.dl.data;

public class PatNote
{
    private int mIndex;
    private int mDeltaPitch;
    private int mDeltaVelocity;
    private long mDeltaTick;
    private long mDuration;
    private String mSteps;
    
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
    public String getSteps()
    {
        return mSteps;
    }
    public void setSteps(String steps)
    {
        mSteps = steps;
    }
    public int getIndex()
    {
        return mIndex;
    }
    public void setIndex(int index)
    {
        mIndex = index;
    }
}
