package jo.sm.dl.logic.gen;

import jo.sm.dl.data.gen.ChartProfile;
import jo.sm.dl.data.sm.SMProject;

public interface IChartStrategy
{
    public String getName();
    public void generateChart(SMProject proj, ChartProfile prof);
}
