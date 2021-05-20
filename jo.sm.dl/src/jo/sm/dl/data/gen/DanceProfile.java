package jo.sm.dl.data.gen;

import java.util.ArrayList;
import java.util.List;

public class DanceProfile
{
    private String              mDanceStrategy;
    private String              mPatternStrategy;
    private List<ChartProfile>  mCharts = new ArrayList<>();
    
    public List<ChartProfile> getCharts()
    {
        return mCharts;
    }
    public void setCharts(List<ChartProfile> charts)
    {
        mCharts = charts;
    }
    public String getDanceStrategy()
    {
        return mDanceStrategy;
    }
    public void setDanceStrategy(String danceStrategy)
    {
        mDanceStrategy = danceStrategy;
    }
    public String getPatternStrategy()
    {
        return mPatternStrategy;
    }
    public void setPatternStrategy(String patternStrategy)
    {
        mPatternStrategy = patternStrategy;
    }

}
