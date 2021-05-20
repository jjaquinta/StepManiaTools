package jo.sm.dl.logic;

import java.util.HashMap;
import java.util.Map;

import jo.sm.dl.data.gen.DanceProfile;
import jo.sm.dl.data.sm.SMProject;
import jo.sm.dl.logic.gen.DefaultPatternStrategy;
import jo.sm.dl.logic.gen.IPatternStrategy;

public class PatternLogic
{
    public static final int MIN_PAT = 4;
    public static final int MIN_INST = 4;
    
    private static final Map<String, IPatternStrategy> mStrategies = new HashMap<>();
    private static IPatternStrategy mDefault;
    static
    {
        addPatternStrategy(new DefaultPatternStrategy());
    }
    
    private static void addPatternStrategy(IPatternStrategy ps)
    {
        if (mDefault == null)
            mDefault = ps;
        mStrategies.put(ps.getName(), ps);
    }
    
    public static IPatternStrategy getStrategy(String name)
    {
        if (mStrategies.containsKey(name))
            return mStrategies.get(name);
        else
            return mDefault;
    }

    public static void findPatterns(SMProject proj, DanceProfile prof)
    {
        IPatternStrategy strat = getStrategy(prof.getPatternStrategy());
        strat.findPatterns(proj, prof);
    }
}
