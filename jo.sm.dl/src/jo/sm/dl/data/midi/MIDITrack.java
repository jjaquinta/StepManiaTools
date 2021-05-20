package jo.sm.dl.data.midi;

import java.util.ArrayList;

import java.util.List;

import jo.sm.dl.logic.MIDILogic;

public class MIDITrack
{
    public static final int    TREBLE_CLEF = 1;
    public static final int    BASS_CLEF   = 2;

    public static final int    IGNORE      = -1;
    public static final int    UNKNOWN     = 0;
    public static final int    MELODY      = 1;
    public static final int    RHYTHYM     = 2;
    public static final int    BASS        = 3;
    public static final int    HARMONY     = 4;
    public static final int    INCIDENTAL  = 5;

    private int                mTrack;
    private int                mBank;
    private int                mProgram;
    private int                mLowPitch;
    private int                mHighPitch;
    private List<MIDINote>     mNotes      = new ArrayList<>();
    private List<MIDINotation> mNotation   = new ArrayList<>();
    private int                mType;

    // utilities

    @Override
    public String toString()
    {
        return "Track " + (mTrack + 1) + " ("
                + MIDILogic.getInstrumentName(mBank, mProgram) + ")" + " "
                + MIDINote.NOTES[mLowPitch] + "->" + MIDINote.NOTES[mHighPitch]
                + " x" + mNotes.size();
    }

    public long getVoice()
    {
        long v = ((long)getTrack() << 32) | ((long)getBank() << 16)
                | ((long)getProgram() << 0);
        return v;
    }

    public void add(MIDINote note)
    {
        if (mNotes.size() == 0)
        {
            mBank = note.getBank();
            mProgram = note.getProgram();
            mLowPitch = note.getPitch();
            mHighPitch = note.getPitch();
        }
        else
        {
            mLowPitch = Math.min(mLowPitch, note.getPitch());
            mHighPitch = Math.max(mLowPitch, note.getPitch());
        }
        mNotes.add(note);
    }

    public int getClef()
    {
        if (getLowPitch() > 51)
            return TREBLE_CLEF;
        else if (getHighPitch() < 69)
            return BASS_CLEF;
        else
            return BASS_CLEF | TREBLE_CLEF;
    }

    // getters and setters
    public int getTrack()
    {
        return mTrack;
    }

    public void setTrack(int track)
    {
        mTrack = track;
    }

    public int getBank()
    {
        return mBank;
    }

    public void setBank(int bank)
    {
        mBank = bank;
    }

    public int getProgram()
    {
        return mProgram;
    }

    public void setProgram(int program)
    {
        mProgram = program;
    }

    public List<MIDINote> getNotes()
    {
        return mNotes;
    }

    public void setNotes(List<MIDINote> notes)
    {
        mNotes = notes;
    }

    public int getLowPitch()
    {
        return mLowPitch;
    }

    public void setLowPitch(int lowPitch)
    {
        mLowPitch = lowPitch;
    }

    public int getHighPitch()
    {
        return mHighPitch;
    }

    public void setHighPitch(int highPitch)
    {
        mHighPitch = highPitch;
    }

    public List<MIDINotation> getNotation()
    {
        return mNotation;
    }

    public void setNotation(List<MIDINotation> notation)
    {
        mNotation = notation;
    }

    public int getType()
    {
        return mType;
    }

    public void setType(int type)
    {
        mType = type;
    }
}
