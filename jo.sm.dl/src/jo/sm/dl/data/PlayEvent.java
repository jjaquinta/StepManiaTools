package jo.sm.dl.data;

public class PlayEvent
{
    public static int   ON = 1;
    public static int   OFF = 2;
    
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
