package jo.sm.dl.data.sm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.sm.dl.data.JProperties;
import jo.sm.dl.data.midi.MIDITune;
import jo.sm.dl.data.sm.pat.PatDef;

public class SMProject
{
    public static final String ENERGY_GRAPH = "energyGraph";
    public static final String NOTE_GRAPH = "noteGraph";
    public static final String OGG_OUT = "ogg";
    public static final String MP3_OUT = "mp3";
    public static final String SM_OUT = "sm";
    public static final String SSC_OUT = "scc";
    
    public static final String DIFF_BEGINNER = "Beginner";
    public static final String DIFF_EASY = "Easy";
    public static final String DIFF_MEDIUM = "Medium";
    public static final String DIFF_HARD = "Hard";
    public static final String DIFF_CHALLENGE = "Challenge";

    
    private JProperties mProps;
    private Map<String,Integer> mDifficulties = new HashMap<String, Integer>();
    private File     mInput;
    private File     mOutput;
    private SMTune   mTune;
    private MIDITune mMIDI;
    private List<PatDef> mPatterns = new ArrayList<>();
    
    // utilities
    public boolean isFlag(String flag)
    {
        return mProps.getProperty(flag) != null;
    }
    
    // getters and setters

    public File getInput()
    {
        return mInput;
    }

    public void setInput(File input)
    {
        mInput = input;
    }

    public File getOutput()
    {
        return mOutput;
    }

    public void setOutput(File output)
    {
        mOutput = output;
    }

    public SMTune getTune()
    {
        return mTune;
    }

    public void setTune(SMTune tune)
    {
        mTune = tune;
    }

    public MIDITune getMIDI()
    {
        return mMIDI;
    }

    public void setMIDI(MIDITune mIDI)
    {
        mMIDI = mIDI;
    }

    public List<PatDef> getPatterns()
    {
        return mPatterns;
    }

    public void setPatterns(List<PatDef> patterns)
    {
        mPatterns = patterns;
    }

    public Map<String, Integer> getDifficulties()
    {
        return mDifficulties;
    }

    public void setDifficulties(Map<String, Integer> difficulties)
    {
        mDifficulties = difficulties;
    }

    public JProperties getProps()
    {
        return mProps;
    }

    public void setProps(JProperties props)
    {
        mProps = props;
    }
}
