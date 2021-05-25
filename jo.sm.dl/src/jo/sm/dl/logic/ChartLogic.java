package jo.sm.dl.logic;

import java.util.HashMap;
import java.util.Map;

import jo.sm.dl.data.gen.ChartProfile;
import jo.sm.dl.data.sm.SMProject;
import jo.sm.dl.logic.gen.AlignedChartStrategy;
import jo.sm.dl.logic.gen.DefaultChartStrategy;
import jo.sm.dl.logic.gen.IChartStrategy;

public class ChartLogic
{
    private static final Map<String, IChartStrategy> mStrategies = new HashMap<>();
    private static IChartStrategy mDefault;
    static
    {
        addChartStrategy(new DefaultChartStrategy());
        addChartStrategy(new AlignedChartStrategy());
    }
    
    private static void addChartStrategy(IChartStrategy ps)
    {
        if (mDefault == null)
            mDefault = ps;
        mStrategies.put(ps.getName(), ps);
    }
    
    public static IChartStrategy getStrategy(String name)
    {
        if (mStrategies.containsKey(name))
            return mStrategies.get(name);
        else
            return mDefault;
    }
    
    public static void generateChart(SMProject proj, ChartProfile prof)
    {
        IChartStrategy strat = getStrategy(prof.getStrategy());
        strat.generateChart(proj, prof);
    }
}