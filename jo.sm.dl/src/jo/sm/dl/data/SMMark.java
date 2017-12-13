package jo.sm.dl.data;

public class SMMark
{
    private float   mMark;
    private Float   mNumValue;
    private String  mStrValue;
    
    public SMMark()
    {       
    }
    
    public SMMark(float m, float b)
    {
        mMark = m;
        mNumValue = b;
    }
    
    public SMMark(float m, String b)
    {
        mMark = m;
        mStrValue = b;
    }
    
    public float getMark()
    {
        return mMark;
    }
    public void setMark(float mark)
    {
        mMark = mark;
    }
    public Float getNumValue()
    {
        return mNumValue;
    }
    public void setNumValue(Float value)
    {
        mNumValue = value;
    }

    public String getStrValue()
    {
        return mStrValue;
    }

    public void setStrValue(String strValue)
    {
        mStrValue = strValue;
    }
}
