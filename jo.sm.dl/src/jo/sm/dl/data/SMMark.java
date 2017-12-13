package jo.sm.dl.data;

public class SMMark
{
    private float   mMark;
    private float   mValue;
    
    public SMMark()
    {       
    }
    
    public SMMark(float m, float b)
    {
        mMark = m;
        mValue = b;
    }
    
    public float getMark()
    {
        return mMark;
    }
    public void setMark(float mark)
    {
        mMark = mark;
    }
    public float getValue()
    {
        return mValue;
    }
    public void setValue(float value)
    {
        mValue = value;
    }
}
