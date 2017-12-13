package jo.sm.dl.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SMProject
{
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
}
