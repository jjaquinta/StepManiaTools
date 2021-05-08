package jo.sm.dle.logic;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITune;
import jo.sm.dle.data.NoteData;
import jo.util.utils.obj.StringUtils;

public class NoteLogic
{
    private static final String NOTE_WHOLE = StringUtils.uniStr(0x1d15D);
    private static final String NOTE_HALF = StringUtils.uniStr(0x1d15E);
    private static final String NOTE_QUARTER = StringUtils.uniStr(0x1d15F);

    public static NoteData getNoteData(MIDITune tune, MIDINote note, int clef)
    {
        NoteData data = new NoteData();
        data.setNote(note);
        long q = note.getDuration()/tune.getPulsesPerQuarter();
        if (q == 1)
        {
            data.setSymbol(NOTE_QUARTER);
            data.setXAdvance(1);
        }
        else if (q == 2)
        {
            data.setSymbol(NOTE_HALF);
            data.setXAdvance(2);
        }
        else if (q == 4)
        {
            data.setSymbol(NOTE_WHOLE);
            data.setXAdvance(4);
        }
        return data;
    }
}
