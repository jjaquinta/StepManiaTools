package jo.sm.dl.data;

import java.util.ArrayList;
import java.util.List;

public class PatInst
{
    private boolean        mUsed;
    private List<MIDINote> mNotes = new ArrayList<>();

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
