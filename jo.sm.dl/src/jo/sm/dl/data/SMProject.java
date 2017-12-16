package jo.sm.dl.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SMProject
{
    public static final String MARK_PATTERNS = "mark_patterns";
    public static final String ENERGY_GRAPH = "energy_graph";
    public static final String NOTE_GRAPH = "note_graph";
    public static final String OGG_OUT = "ogg_out";
    public static final String MP3_OUT = "mp3_out";
    public static final String SM_OUT = "sm_out";
    public static final String SSC_OUT = "scc_out";
    
    public static final String DIFF_BEGINNER = "Beginner";
    public static final String DIFF_EASY = "Easy";
    public static final String DIFF_MEDIUM = "Medium";
    public static final String DIFF_HARD = "Hard";
    public static final String DIFF_CHALLENGE = "Challenge";

    
    private Set<String> mFlags = new HashSet<>();
    private Map<String,Integer> mDifficulties = new HashMap<String, Integer>();
    private String mArtist = "Unknown artist";
    private File     mInput;
    private File     mOutput;
    private SMTune   mTune;
    private MIDITune mMIDI;
    private List<PatDef> mPatterns = new ArrayList<>();
    
    // utilities
    public boolean isFlag(String flag)
    {
        return mFlags.contains(flag);
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

    public Set<String> getFlags()
    {
        return mFlags;
    }

    public void setFlags(Set<String> flags)
    {
        mFlags = flags;
    }

    public Map<String, Integer> getDifficulties()
    {
        return mDifficulties;
    }

    public void setDifficulties(Map<String, Integer> difficulties)
    {
        mDifficulties = difficulties;
    }

    public String getArtist()
    {
        return mArtist;
    }

    public void setArtist(String artist)
    {
        mArtist = artist;
    }
}
