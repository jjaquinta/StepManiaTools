package jo.sm.dl.data.gen;

public class ChartProfile
{
    private String      mName;
    private DiffProfile mDifficulty;
    private String      mStrategy;
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public DiffProfile getDifficulty()
    {
        return mDifficulty;
    }
    public void setDifficulty(DiffProfile difficulty)
    {
        mDifficulty = difficulty;
    }
    public String getStrategy()
    {
        return mStrategy;
    }
    public void setStrategy(String strategy)
    {
        mStrategy = strategy;
    }
}
