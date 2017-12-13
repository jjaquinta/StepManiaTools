package jo.sm.dl.data;

import java.util.ArrayList;
import java.util.List;

public class PatDef
{
    private List<PatNote> mNotes = new ArrayList<>();
    private List<PatInst> mInstances = new ArrayList<>();
    private List<String> mSteps = new ArrayList<>();
    private long         mBeat;
    
    public List<PatNote> getNotes()
    {
        return mNotes;
    }
    public void setNotes(List<PatNote> notes)
    {
        mNotes = notes;
    }
    public List<PatInst> getInstances()
    {
        return mInstances;
    }
    public void setInstances(List<PatInst> instances)
    {
        mInstances = instances;
    }
    public List<String> getSteps()
    {
        return mSteps;
    }
    public void setSteps(List<String> steps)
    {
        mSteps = steps;
    }
    public long getBeat()
    {
        return mBeat;
    }
    public void setBeat(long beat)
    {
        mBeat = beat;
    }
}
