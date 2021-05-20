package jo.sm.dl.logic.gen;

import jo.sm.dl.data.gen.DanceProfile;
import jo.sm.dl.data.sm.SMProject;

public interface IDanceStrategy
{
    public String getName();
    public void assignCharts(SMProject proj, DanceProfile prof);
}
