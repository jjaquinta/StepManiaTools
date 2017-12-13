package jo.sm.dl.data;

public class SMBeat
{
    public static final char NOTE_NONE         = '0';
    public static final char NOTE_NORMAL       = '1';
    public static final char NOTE_HOLD_HEAD    = '2';
    public static final char NOTE_HOLD_RELEASE = '3';
    public static final char NOTE_ROLL_HEAD    = '4';
    public static final char NOTE_MINE         = 'M';
    
    private long    mTick;
    private char[]  mNotes = "0000".toCharArray();
    
    public SMBeat()
    {       
    }
    
    public SMBeat(long tick)
    {
        mTick = tick;
    }

    // utilities
    
    public boolean isAnySteps()
    {
        for (char ch : mNotes)
            if (ch != NOTE_NONE)
                return true;
        return false;
    }    

    // getters and setters
    
    public char[] getNotes()
    {
        return mNotes;
    }

    public void setNotes(char[] notes)
    {
        mNotes = notes;
    }

    public long getTick()
    {
        return mTick;
    }

    public void setTick(long tick)
    {
        mTick = tick;
    }
}
