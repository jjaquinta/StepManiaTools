package jo.sm.dl.data.gen;

import java.util.ArrayList;
import java.util.List;

public class DanceProfile
{
    private String              mStrategy;
    private List<ChartProfile>  mCharts = new ArrayList<>();
    
    public String getStrategy()
    {
        return mStrategy;
    }
    public void setStrategy(String strategy)
    {
        mStrategy = strategy;
    }
    public List<ChartProfile> getCharts()
    {
        return mCharts;
    }
    public void setCharts(List<ChartProfile> charts)
    {
        mCharts = charts;
    }

}
