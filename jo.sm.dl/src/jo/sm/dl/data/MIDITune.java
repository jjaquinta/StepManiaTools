package jo.sm.dl.data;

import java.util.List;

public class MIDITune
{
    private float          mMSPerTick;
    private List<MIDINote> mNotes;
    private long           mGranularity;
    private int            mTracks;
    private int            mPulsesPerQuarter;
    private float          mBeatsPerMinute;
    private long           mLengthInTicks;
    private float          mLengthInSeconds;

    // utilities

    public long tickToMS(long tick)
    {
        return (long)(tick * mMSPerTick);
    }

    public long msToTicks(long ms)
    {
        return (long)(ms / mMSPerTick);
    }

    // getters and setters

    public float getMSPerTick()
    {
        return mMSPerTick;
    }

    public void setMSPerTick(float mSPerTick)
    {
        mMSPerTick = mSPerTick;
    }

    public List<MIDINote> getNotes()
    {
        return mNotes;
    }

    public void setNotes(List<MIDINote> notes)
    {
        mNotes = notes;
    }

    public long getGranularity()
    {
        return mGranularity;
    }

    public void setGranularity(long granularity)
    {
        mGranularity = granularity;
    }

    public int getTracks()
    {
        return mTracks;
    }

    public void setTracks(int tracks)
    {
        mTracks = tracks;
    }

    public int getPulsesPerQuarter()
    {
        return mPulsesPerQuarter;
    }

    public void setPulsesPerQuarter(int pulsesPerQuarter)
    {
        mPulsesPerQuarter = pulsesPerQuarter;
    }

    public float getBeatsPerMinute()
    {
        return mBeatsPerMinute;
    }

    public void setBeatsPerMinute(float beatsPerMinute)
    {
        mBeatsPerMinute = beatsPerMinute;
    }

    public long getLengthInTicks()
    {
        return mLengthInTicks;
    }

    public void setLengthInTicks(long lengthInTicks)
    {
        mLengthInTicks = lengthInTicks;
    }

    public float getLengthInSeconds()
    {
        return mLengthInSeconds;
    }

    public void setLengthInSeconds(float lengthInSeconds)
    {
        mLengthInSeconds = lengthInSeconds;
    }
}
