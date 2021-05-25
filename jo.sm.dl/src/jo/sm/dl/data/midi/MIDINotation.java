package jo.sm.dl.data.midi;

public class MIDINotation
{
    private MIDITrack   mTrack;
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
    
    // utilities
    public int getAlignedStart()
    {
        int a = (int)(mMeasure*512 + .5);
        int m = a%8;
        if (m <= 5)
            a -= m;
        else if (m >= 6)
            a += (8 - m);
        return a;
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

    public MIDITrack getTrack()
    {
        return mTrack;
    }

    public void setTrack(MIDITrack track)
    {
        mTrack = track;
    }
    }
