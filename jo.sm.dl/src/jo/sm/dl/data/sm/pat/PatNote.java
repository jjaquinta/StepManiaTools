package jo.sm.dl.data.sm.pat;

public class PatNote
{
    private int mIndex;
    private int mDeltaPitch;
    private int mDeltaVelocity;
    private long mDeltaTick;
    private long mDuration;
    private String mSteps;
    
    public PatNote()
    {        
    }
    
    public PatNote(PatNote c, String steps)
    {
        mIndex = c.mIndex;
        mDeltaPitch = c.mDeltaPitch;
        mDeltaVelocity = c.mDeltaVelocity;
        mDeltaTick = c.mDeltaTick;
        mDuration = c.mDuration;
        mSteps = steps;
    }
    
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
    public int getIndex()
    {
        return mIndex;
    }
    public void setIndex(int index)
    {
        mIndex = index;
    }
    public String getSteps()
    {
        return mSteps;
    }
    public void setSteps(String steps)
    {
        mSteps = steps;
    }
}
