package jo.sm.dl.data.midi;

import java.util.ArrayList;
import java.util.List;

import jo.sm.dl.data.sm.SMMark;
import jo.util.utils.MathUtils;

public class MIDITune
{
    private float              mMSPerTick;
    private List<MIDINote>     mNotes;
    private List<MIDINotation> mNotation = new ArrayList<>();
    private List<MIDITrack>    mTrackInfos = new ArrayList<>();
    private long               mGranularity;
    private int                mTracks;
    private int                mPulsesPerQuarter;
    private float              mBeatsPerMinute;
    private long               mLengthInTicks;
    private float              mLengthInSeconds;
    private List<SMMark>       mBPMs = new ArrayList<>();

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
                float elapsedMinutes = elapsedBeats / bpm;
                minutes += elapsedMinutes;
                lastMark = mark.getMark();
                bpm = mark.getNumValue();
            }
            else
            {
                float elapsedBeats = targetBeat - lastMark;
                float elapsedMinutes = elapsedBeats / bpm;
                minutes += elapsedMinutes;
                lastMark = mark.getMark();
                bpm = mark.getNumValue();
                break;
            }
        }
        if (lastMark < targetBeat)
        {
            float elapsedBeats = targetBeat - lastMark;
            float elapsedMinutes = elapsedBeats / bpm;
            minutes += elapsedMinutes;
        }
        return minutes;
    }

    public MIDINotation findNotation(MIDINote n)
    {
        for (MIDINotation nn : mNotation)
            if (nn.getNote() == n)
                return nn;
        return null;
    }

    public long minutesToTick(float targetMinutes)
    {
        float minutes = 0;
        float lastMark = 0;
        float bpm = 60;
        float beats = 0;
        for (SMMark mark : mBPMs)
        {
            float elapsedBeats = mark.getMark() - lastMark;
            float elapsedMinutes = elapsedBeats / bpm;
            if (minutes + elapsedMinutes < targetMinutes)
            {
                minutes += elapsedMinutes;
                beats += elapsedBeats;
                lastMark = mark.getMark();
                bpm = mark.getNumValue();
            }
            else
            {
                float incrBeats = MathUtils.interpolate(targetMinutes, minutes,
                        minutes + elapsedMinutes, 0, elapsedBeats);
                beats += incrBeats;
                return beatToTick(beats);
            }
        }
        float elapsedBeats = tickToBeat(mLengthInTicks) - lastMark;
        float elapsedMinutes = elapsedBeats / bpm;
        float incrBeats = MathUtils.interpolate(targetMinutes, minutes,
                minutes + elapsedMinutes, 0, elapsedBeats);
        beats += incrBeats;
        return beatToTick(beats);
    }

    public float tickToBeat(long tick)
    {
        return tick / (float)mPulsesPerQuarter;
    }

    public long beatToTick(float beat)
    {
        return (long)(beat * mPulsesPerQuarter);
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

    public List<MIDINotation> getNotation()
    {
        return mNotation;
    }

    public void setNotation(List<MIDINotation> notation)
    {
        mNotation = notation;
    }

    public List<MIDITrack> getTrackInfos()
    {
        return mTrackInfos;
    }

    public void setTrackInfos(List<MIDITrack> trackInfos)
    {
        mTrackInfos = trackInfos;
    }
}
