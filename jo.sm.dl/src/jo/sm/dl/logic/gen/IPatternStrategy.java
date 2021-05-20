package jo.sm.dl.logic.gen;

import jo.sm.dl.data.gen.DanceProfile;
import jo.sm.dl.data.sm.SMProject;

public interface IPatternStrategy
{
    public String getName();
    public void findPatterns(SMProject proj, DanceProfile prof);
}
