package jo.sm.dl.data;

import java.util.ArrayList;
import java.util.List;

public class PatInst
{
    private List<MIDINote> mNotes = new ArrayList<>();

    public List<MIDINote> getNotes()
    {
        return mNotes;
    }

    public void setNotes(List<MIDINote> notes)
    {
        mNotes = notes;
    }
}
