package jo.sm.dl.logic;

import java.util.HashMap;
import java.util.Map;

import jo.sm.dl.data.gen.ChartProfile;
import jo.sm.dl.data.gen.DanceProfile;
import jo.sm.dl.data.midi.MIDITune;
import jo.sm.dl.data.sm.SMProject;
import jo.sm.dl.data.sm.SMTune;
import jo.sm.dl.logic.gen.DefaultDanceStrategy;
import jo.sm.dl.logic.gen.IDanceStrategy;

public class DanceLogic
{
    private static final Map<String, IDanceStrategy> mStrategies = new HashMap<>();
    private static IDanceStrategy mDefault;
    static
    {
        addDanceStrategy(new DefaultDanceStrategy());
    }
    
    private static void addDanceStrategy(IDanceStrategy ps)
    {
        if (mDefault == null)
            mDefault = ps;
        mStrategies.put(ps.getName(), ps);
    }
    
    public static IDanceStrategy getStrategy(String name)
    {
        if (mStrategies.containsKey(name))
            return mStrategies.get(name);
        else
            return mDefault;
    }

    public static void dance(SMProject proj, DanceProfile prof)
    {
        MIDITune midi = proj.getMIDI();
        SMTune steps = proj.getTune();        
        steps.setDisplayBPM(midi.getBeatsPerMinute());
        steps.getBPMs().addAll(midi.getBPMs());
        IDanceStrategy strat = getStrategy(prof.getDanceStrategy());
        strat.assignCharts(proj, prof);
        for (ChartProfile cp : prof.getCharts())
            ChartLogic.generateChart(proj, cp);
    }

}