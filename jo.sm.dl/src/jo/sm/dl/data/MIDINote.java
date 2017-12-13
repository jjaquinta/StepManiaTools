package jo.sm.dl.data;

public class MIDINote
{
    private int mPitch;
    private int mVelocity;
    private long mTick;
    private long mDuration;
    private int mTrack;
    private int mBank;
    private int mProgram;
    
    public int getPitch()
    {
        return mPitch;
    }
    public void setPitch(int pitch)
    {
        mPitch = pitch;
    }
    public long getTick()
    {
        return mTick;
    }
    public void setTick(long Tick)
    {
        mTick = Tick;
    }
    public long getDuration()
    {
        return mDuration;
    }
    public void setDuration(long duration)
    {
        mDuration = duration;
    }
    public int getTrack()
    {
        return mTrack;
    }
    public void setTrack(int track)
    {
        mTrack = track;
    }
    public int getBank()
    {
        return mBank;
    }
    public void setBank(int bank)
    {
        mBank = bank;
    }
    public int getProgram()
    {
        return mProgram;
    }
    public void setProgram(int program)
    {
        mProgram = program;
    }
    public int getVelocity()
    {
        return mVelocity;
    }
    public void setVelocity(int velocity)
    {
        mVelocity = velocity;
    }
}
