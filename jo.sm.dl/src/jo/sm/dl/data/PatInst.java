package jo.sm.dl.data;

import java.util.ArrayList;
import java.util.List;

public class PatInst
{
    private boolean        mUsed;
    private List<MIDINote> mNotes = new ArrayList<>();

    // utilities

    public MIDINote findNote(long tick)
    {
        for (MIDINote n : mNotes)
            if (n.getTick() == tick)
                return n;
        return null;
    }

    // getters and setters
    
    public List<MIDINote> getNotes()
    {
        return mNotes;
    }

    public void setNotes(List<MIDINote> notes)
    {
        mNotes = notes;
    }

    public boolean isUsed()
    {
        return mUsed;
    }

    public void setUsed(boolean used)
    {
        mUsed = used;
    }
}
