package jo.sm.dl.logic.gen;

import jo.sm.dl.data.gen.ChartProfile;
import jo.sm.dl.data.gen.DanceProfile;
import jo.sm.dl.data.gen.DiffProfile;
import jo.sm.dl.data.sm.SMProject;
import jo.sm.dl.logic.DifficultyLogic;

public class DefaultDanceStrategy implements IDanceStrategy
{
    private static final String[] DIFFICULTIES = {
            SMProject.DIFF_BEGINNER, SMProject.DIFF_EASY, SMProject.DIFF_MEDIUM, SMProject.DIFF_HARD, SMProject.DIFF_CHALLENGE
    };
    
    @Override
    public String getName()
    {
        return "default";
    }

    @Override
    public void assignCharts(SMProject proj, DanceProfile prof)
    {
        if (prof.getCharts().size() > 0)
            return;
        for (int i = 0; i < DIFFICULTIES.length; i++)
        {
            int idx = proj.getDifficulties().get(DIFFICULTIES[i]);
            DiffProfile diff = DifficultyLogic.getProfile(idx);
            diff.reset();
            ChartProfile cp = new ChartProfile();
            cp.setName(DIFFICULTIES[i]);
            cp.setDifficulty(diff);
            cp.setStrategy(prof.getChartStrategy());
            prof.getCharts().add(cp);
        }
    }

}
