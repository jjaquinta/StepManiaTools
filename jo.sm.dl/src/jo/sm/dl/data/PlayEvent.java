package jo.sm.dl.data;

import jo.sm.dl.data.midi.MIDINote;

public class PlayEvent
{
    public static int   ON = 1;
    public static int   OFF = 2;
    public static int   START = 3;
    public static int   STOP = 4;
    
    private int         mAction;
    private MIDINote    mNote;
    
    public int getAction()
    {
        return mAction;
    }
    public void setAction(int action)
    {
        mAction = action;
    }
    public MIDINote getNote()
    {
        return mNote;
    }
    public void setNote(MIDINote note)
    {
        mNote = note;
    }
}
