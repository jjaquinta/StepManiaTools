package jo.sm.dl.data.midi;

public class MIDINotation
{
    private MIDINote    mNote;
    private int         mYAdjust; // how far to draw +/- from middle c
    private int         mSharps;
    private int         mFlats;
    private int         mDots;
    private String      mSymbol;
    private double      mMeasure;
    
    // constructors
    public MIDINotation()
    {        
    }
    
    public MIDINotation(MIDINote note)
    {
        mNote = note;
    }
    
    // getters and setters
    
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
    public int getSharps()
    {
        return mSharps;
    }
    public void setSharps(int sharps)
    {
        mSharps = sharps;
    }
    public int getFlats()
    {
        return mFlats;
    }
    public void setFlats(int flats)
    {
        mFlats = flats;
    }
    public int getDots()
    {
        return mDots;
    }
    public void setDots(int dots)
    {
        mDots = dots;
    }
    public String getSymbol()
    {
        return mSymbol;
    }
    public void setSymbol(String symbol)
    {
        mSymbol = symbol;
    }
    public double getMeasure()
    {
        return mMeasure;
    }
    public void setMeasure(double measure)
    {
        mMeasure = measure;
    }
    }
