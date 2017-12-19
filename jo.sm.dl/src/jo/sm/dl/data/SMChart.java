package jo.sm.dl.data;

import java.util.ArrayList;
import java.util.List;

public class SMChart
{
    private String          mNotesChartType;
    private String          mNotesDescription;
    private String          mNotesDifficulty;
    private int             mNotesMeter;
    private float[]         mGrooveMeter = new float[5];
    private List<SMMeasure> mMeasures = new ArrayList<>();

    // utilities
    public List<SMBeat> getBeats(long startTick, long endTick)
    {
        List<SMBeat> beats = new ArrayList<>();
        for (SMMeasure m : mMeasures)
        {
            if (m.getStartTick() > endTick)
                break;
            for (SMBeat b : m.getBeats())
                if ((b.getTick() >= startTick) && (b.getTick() <= endTick))
                    beats.add(b);
        }
        return beats;
    }

    
    // getters and setters
    public String getNotesChartType()
    {
        return mNotesChartType;
    }
    public void setNotesChartType(String notesChartType)
    {
        mNotesChartType = notesChartType;
    }
    public String getNotesDescription()
    {
        return mNotesDescription;
    }
    public void setNotesDescription(String notesDescription)
    {
        mNotesDescription = notesDescription;
    }
    public String getNotesDifficulty()
    {
        return mNotesDifficulty;
    }
    public void setNotesDifficulty(String notesDifficulty)
    {
        mNotesDifficulty = notesDifficulty;
    }
    public int getNotesMeter()
    {
        return mNotesMeter;
    }
    public void setNotesMeter(int notesMeter)
    {
        mNotesMeter = notesMeter;
    }
    public float[] getGrooveMeter()
    {
        return mGrooveMeter;
    }
    public void setGrooveMeter(float[] grooveMeter)
    {
        mGrooveMeter = grooveMeter;
    }
    public List<SMMeasure> getMeasures()
    {
        return mMeasures;
    }
    public void setMeasures(List<SMMeasure> measures)
    {
        mMeasures = measures;
    }
}
