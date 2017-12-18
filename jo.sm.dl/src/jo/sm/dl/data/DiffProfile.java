package jo.sm.dl.data;

public class DiffProfile
{
    private int mMeter;
    private int mNPM; // notes per minute
    private float mDoublePC; // percentage of double arrows
    private float mHoldPC; // percentage of hold arrows
    private float mRollPC; // percentage of rolls
    private float mMinesPC; // percentage of mines
    private float mNote4ths; // percentage
    private float mNote8ths; // percentage
    private float mNote12ths; // percentage
    private float mNote16ths; // percentage
    private float mNote24ths; // percentage
    private float mNote32nds; // percentage
    private float mNote48ths; // percentage
    private float mNote64ths; // percentage
    private float mNote192nds; // percentage
    private boolean mNoBackArrow;
    private float mDoubleRoundOff;
    private float mHoldRoundOff;
    private float mMinesRoundOff;
    
    // utilities
    public void reset()
    {
        mDoubleRoundOff = 0;
        mHoldRoundOff = 0;
        mMinesRoundOff = 0;
    }
    
    // getters and setters
    
    public int getNPM()
    {
        return mNPM;
    }
    public void setNPM(int nPM)
    {
        mNPM = nPM;
    }
    public float getDoublePC()
    {
        return mDoublePC;
    }
    public void setDoublePC(float doublePC)
    {
        mDoublePC = doublePC;
    }
    public float getHoldPC()
    {
        return mHoldPC;
    }
    public void setHoldPC(float holdPC)
    {
        mHoldPC = holdPC;
    }
    public float getRollPC()
    {
        return mRollPC;
    }
    public void setRollPC(float rollPC)
    {
        mRollPC = rollPC;
    }
    public float getMinesPC()
    {
        return mMinesPC;
    }
    public void setMinesPC(float minesPC)
    {
        mMinesPC = minesPC;
    }
    public float getNote4ths()
    {
        return mNote4ths;
    }
    public void setNote4ths(float note4ths)
    {
        mNote4ths = note4ths;
    }
    public float getNote8ths()
    {
        return mNote8ths;
    }
    public void setNote8ths(float note8ths)
    {
        mNote8ths = note8ths;
    }
    public float getNote12ths()
    {
        return mNote12ths;
    }
    public void setNote12ths(float note12ths)
    {
        mNote12ths = note12ths;
    }
    public float getNote16ths()
    {
        return mNote16ths;
    }
    public void setNote16ths(float note16ths)
    {
        mNote16ths = note16ths;
    }
    public float getNote24ths()
    {
        return mNote24ths;
    }
    public void setNote24ths(float note24ths)
    {
        mNote24ths = note24ths;
    }
    public float getNote32nds()
    {
        return mNote32nds;
    }
    public void setNote32nds(float note32nds)
    {
        mNote32nds = note32nds;
    }
    public float getNote48ths()
    {
        return mNote48ths;
    }
    public void setNote48ths(float note48ths)
    {
        mNote48ths = note48ths;
    }
    public float getNote64ths()
    {
        return mNote64ths;
    }
    public void setNote64ths(float note64ths)
    {
        mNote64ths = note64ths;
    }
    public float getNote192nds()
    {
        return mNote192nds;
    }
    public void setNote192nds(float note192nds)
    {
        mNote192nds = note192nds;
    }
    public int getMeter()
    {
        return mMeter;
    }
    public void setMeter(int meter)
    {
        mMeter = meter;
    }
    public boolean isNoBackArrow()
    {
        return mNoBackArrow;
    }
    public void setNoBackArrow(boolean noBackArrow)
    {
        mNoBackArrow = noBackArrow;
    }
    public float getDoubleRoundOff()
    {
        return mDoubleRoundOff;
    }
    public void setDoubleRoundOff(float doubleRoundOff)
    {
        mDoubleRoundOff = doubleRoundOff;
    }
    public float getHoldRoundOff()
    {
        return mHoldRoundOff;
    }
    public void setHoldRoundOff(float holdRoundOff)
    {
        mHoldRoundOff = holdRoundOff;
    }
    public float getMinesRoundOff()
    {
        return mMinesRoundOff;
    }
    public void setMinesRoundOff(float minesRoundOff)
    {
        mMinesRoundOff = minesRoundOff;
    }
}
