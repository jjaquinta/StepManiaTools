package jo.sm.dl.data;

import java.util.ArrayList;
import java.util.List;

public class PatDef
{
    private List<PatNote> mNotes = new ArrayList<>();
    private List<PatInst> mInstances = new ArrayList<>();
    private long         mBeat;

    // utilties
    public float getQLen(int q)
    {
        float score = 0;
        for (PatNote note : mNotes)
            if (note.getDeltaTick()%q == 0)
                score += 1;
            else if (note.getDeltaTick()%(q/2) == 0)
                score += .5f;
            else if (note.getDeltaTick()%(q/4) == 0)
                score += .25f;
        return score;
    }
    
    public float getNormalizedVolume()
    {
        long length = mNotes.get(mNotes.size() - 1).getDeltaTick();
        float vol = 0;
        for (PatInst inst : mInstances)
            for (MIDINote note : inst.getNotes())
            {
                int loud = note.getVelocity() + note.getExpression() + note.getVolume();
                vol += loud*note.getDuration();
            }
        vol /= mInstances.size();
        vol /= length;
        return vol;
    }
    
    public int getScore(int q)
    {
        //return getInstances().size()*getNotes().size();
        //return getInstances().size();
        //return getNotes().size();
        float score = getQLen(q);
        score *= Math.log10(getInstances().size());
        score *= getNormalizedVolume();
        return (int)score;
    }
    
    // getters and setters
    
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
    public long getBeat()
    {
        return mBeat;
    }
    public void setBeat(long beat)
    {
        mBeat = beat;
    }
}
