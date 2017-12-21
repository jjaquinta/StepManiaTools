package jo.sm.dl.data;

public class MIDINote implements Comparable<MIDINote>
{
    private int mPitch;
    private int mVelocity;
    private int mExpression;
    private int mVolume;
    private long mTick;
    private long mDuration;
    private int mTrack;
    private int mBank;
    private int mProgram;
    
    // utilities
    public long getVoice()
    {
        long v = ((long)getTrack()<<32) | ((long)getBank()<<16) | ((long)getProgram()<<0);
        return v;
    }
    
    public long getLoud()
    {
        return mVelocity + mExpression + mVolume;
    }
    
    @Override
    public int compareTo(MIDINote o)
    {
        return (int)Math.signum(mTick - o.mTick);
    }
    
    // getters and setters
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

    public int getExpression()
    {
        return mExpression;
    }

    public void setExpression(int expression)
    {
        mExpression = expression;
    }

    public int getVolume()
    {
        return mVolume;
    }

    public void setVolume(int volume)
    {
        mVolume = volume;
    }
}
