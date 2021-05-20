package jo.sm.dl.data.sm.pat;

import java.util.ArrayList;
import java.util.List;

import jo.sm.dl.data.midi.MIDINote;

public class PatDef
{
    private List<PatNote> mNotes = new ArrayList<>();
    private List<PatInst> mInstances = new ArrayList<>();
    private long         mBeat;
    private int          mUsed;
    private boolean      mMelody;

    // utilties
    @Override
    public String toString()
    {
        return "Pattern "+hashCode();
    }
    
    public void used()
    {
        mUsed++;
    }
    
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
        if (mMelody)
            score *= 5;
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

    public int getUsed()
    {
        return mUsed;
    }

    public void setUsed(int used)
    {
        mUsed = used;
    }

    public boolean isMelody()
    {
        return mMelody;
    }

    public void setMelody(boolean melody)
    {
        mMelody = melody;
    }
}
