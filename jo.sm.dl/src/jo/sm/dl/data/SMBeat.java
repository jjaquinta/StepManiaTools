package jo.sm.dl.data;

public class SMBeat implements Comparable<SMBeat>
{
    public static final char NOTE_NONE         = '0';
    public static final char NOTE_NORMAL       = '1';
    public static final char NOTE_HOLD_HEAD    = '2';
    public static final char NOTE_HOLD_RELEASE = '3';
    public static final char NOTE_ROLL_HEAD    = '4';
    public static final char NOTE_MINE         = 'M';
    
    private long    mTick;
    private char[]  mNotes = "0000".toCharArray();
    private SMMeasure mMeasure;
    private MIDINote mNote;
    private int     mAlignment;
    
    public SMBeat(SMMeasure measure, long tick, int alignment)
    {
        mMeasure = measure;
        mTick = tick;
        mAlignment = alignment;
    }

    // utilities
    
    public int compareTo(SMBeat o) 
    {
        return (int)Math.signum(mTick - o.mTick);
    }
    
    public boolean isAnySteps()
    {
        for (char ch : mNotes)
            if (ch != NOTE_NONE)
                return true;
        return false;
    }    
    
    public int getHowManySteps()
    {
        int count = 0;
        for (char ch : mNotes)
            if (ch != NOTE_NONE)
                count++;
        return count;
    }
    
    public boolean overlap(SMBeat b)
    {
        for (int i = 0; i < mNotes.length; i++)
            if ((mNotes[i] != NOTE_NONE) && (b.mNotes[i] != NOTE_NONE))
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

    public SMMeasure getMeasure()
    {
        return mMeasure;
    }

    public void setMeasure(SMMeasure measure)
    {
        mMeasure = measure;
    }

    public MIDINote getNote()
    {
        return mNote;
    }

    public void setNote(MIDINote note)
    {
        mNote = note;
    }

    public int getAlignment()
    {
        return mAlignment;
    }

    public void setAlignment(int alignment)
    {
        mAlignment = alignment;
    }
}
