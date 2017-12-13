package jo.sm.dl.data;

import java.util.ArrayList;
import java.util.List;

public class SMMeasure
{
    private long            mStartTick;
    private List<SMBeat>    mBeats = new ArrayList<>();

    public List<SMBeat> getBeats()
    {
        return mBeats;
    }

    public void setBeats(List<SMBeat> beats)
    {
        mBeats = beats;
    }

    public long getStartTick()
    {
        return mStartTick;
    }

    public void setStartTick(long startTick)
    {
        mStartTick = startTick;
    }
}
