package jo.sm.dle.data;

import jo.sm.dl.data.midi.MIDINote;

public class NoteData
{
    private MIDINote    mNote;
    private int         mYAdjust;
    private boolean     mSharp;
    private boolean     mFlat;
    private int         mXAdvance;
    private String      mSymbol;
    
    public MIDINote getNote()
    {
        return mNote;
    }
    public void setNote(MIDINote note)
    {
        mNote = note;
    }
    public int getYAdjust()
    {
        return mYAdjust;
    }
    public void setYAdjust(int yAdjust)
    {
        mYAdjust = yAdjust;
    }
    public boolean isSharp()
    {
        return mSharp;
    }
    public void setSharp(boolean sharp)
    {
        mSharp = sharp;
    }
    public boolean isFlat()
    {
        return mFlat;
    }
    public void setFlat(boolean flat)
    {
        mFlat = flat;
    }
    public int getXAdvance()
    {
        return mXAdvance;
    }
    public void setXAdvance(int xAdvance)
    {
        mXAdvance = xAdvance;
    }
    public String getSymbol()
    {
        return mSymbol;
    }
    public void setSymbol(String symbol)
    {
        mSymbol = symbol;
    }
}
