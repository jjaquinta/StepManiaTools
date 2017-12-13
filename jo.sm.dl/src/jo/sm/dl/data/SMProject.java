package jo.sm.dl.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SMProject
{
    public static final String MARK_PATTERNS = "mark_patterns";
    public static final String ENERGY_GRAPH = "energy_graph";
    public static final String NOTE_GRAPH = "note_graph";
    
    private Set<String> mFlags = new HashSet<>();
    private File     mInput;
    private File     mOutput;
    private SMTune   mTune;
    private MIDITune mMIDI;
    private List<PatDef> mPatterns = new ArrayList<>();

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
}
