package jo.sm.dl.data;

import java.util.ArrayList;
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
    private List<SMMark>   mBPMs     = new ArrayList<>();

    // utilities

    public float tickToMinutes(long tick)
    {
        float targetBeat = tickToBeat(tick);
        float minutes = 0;
        float lastMark = 0;
        float bpm = 60;
        for (SMMark mark : mBPMs)
        {
            if (mark.getMark() <= targetBeat)
            {
                float elapsedBeats = mark.getMark() - lastMark; 
                float elapsedMinutes = elapsedBeats/bpm;
                minutes += elapsedMinutes;
                lastMark = mark.getMark();
                bpm = mark.getNumValue();
            }
            else
            {
                float elapsedBeats = targetBeat - lastMark; 
                float elapsedMinutes = elapsedBeats/bpm;
                minutes += elapsedMinutes;
                lastMark = mark.getMark();
                bpm = mark.getNumValue();
                break;
            }
        }
        if (lastMark < targetBeat)
        {
            float elapsedBeats = targetBeat - lastMark; 
            float elapsedMinutes = elapsedBeats/bpm;
            minutes += elapsedMinutes;
        }
        return minutes;
    }

    public float tickToBeat(long tick)
    {
        return tick/(float)mPulsesPerQuarter;
    }

    public int beatToTick(float beat)
    {
        return (int)(beat*mPulsesPerQuarter);
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

    public List<SMMark> getBPMs()
    {
        return mBPMs;
    }

    public void setBPMs(List<SMMark> bPMs)
    {
        mBPMs = bPMs;
    }
}
