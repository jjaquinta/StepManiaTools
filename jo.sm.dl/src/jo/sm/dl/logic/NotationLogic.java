package jo.sm.dl.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jo.sm.dl.data.midi.MIDINotation;
import jo.sm.dl.data.midi.MIDINote;
import jo.sm.dl.data.midi.MIDITrack;
import jo.sm.dl.data.midi.MIDITune;
import jo.util.utils.obj.StringUtils;

public class NotationLogic
{
    public static final String NOTE_WHOLE = StringUtils.uniStr(0x1d15D);
    public static final String NOTE_HALF = StringUtils.uniStr(0x1d15E);
    public static final String NOTE_QUARTER = StringUtils.uniStr(0x1d15F);
    public static final String NOTE_EIGTH = StringUtils.uniStr(0x1d160);
    public static final String NOTE_SIXTEENTH = StringUtils.uniStr(0x1d161);
    public static final String NOTE_THIRTYSECOND = StringUtils.uniStr(0x1d162);
    public static final String NOTE_SIXTYFORTH = StringUtils.uniStr(0x1d163);

    public static List<MIDINotation> makeNotation(MIDITune tune)
    {
        if (tune.getNotation().size() == tune.getNotes().size())
            return tune.getNotation();
        for (MIDITrack track : tune.getTrackInfos())
        {
            List<MIDINotation> ret = makeNotation(tune, track.getNotes());
            track.setNotation(ret);
            tune.getNotation().addAll(ret);
        }
        sortNotations(tune.getNotation());
        return tune.getNotation();
    }

    public static List<MIDINotation> makeNotation(MIDITune tune, Collection<MIDINote> notes)
    {
        List<MIDINotation> notations = new ArrayList<MIDINotation>();
        double ppq = tune.getPulsesPerQuarter();
        for (MIDINote note : notes)
        {
            if (note.getDuration() == 0L)
                continue;
            MIDINotation notation = new MIDINotation(note);
            int[] data = NOTE_DATA[note.getPitch()];
            notation.setYAdjust(data[1]);
            notation.setSharps(data[2]);
            notation.setFlats(data[3]);
            notation.setMeasure(note.getTick()/ppq/4);
            double len = note.getDuration()/ppq;
            Object[] ldata = findLData(len);
            notation.setSymbol((String)ldata[1]);
            notation.setDots((int)ldata[2]);
            notations.add(notation);
        }
        sortNotations(notations);
        return notations;
    }

    public static void sortNotations(List<MIDINotation> notations)
    {
        Collections.sort(notations, new Comparator<MIDINotation>() {
            @Override
            public int compare(MIDINotation o1, MIDINotation o2)
            {
                return (int)Math.signum(o1.getMeasure() - o2.getMeasure());
            }
        });
    }
    
    private static Object[] findLData(double len)
    {
        Object[] best = NOTE_LEN[0];
        double bestv = Math.abs(len - (double)best[0]);
        for (int i = 1; i < NOTE_LEN.length; i++)
        {
            double v = Math.abs(len - (double)NOTE_LEN[i][0]);
            if (v < bestv)
            {
                bestv = v;
                best = NOTE_LEN[i];
            }
        }
        return best;
    }
    
    private static final Object[][] NOTE_LEN = {
            // length, symbol, dots
            { 0.0625, NOTE_SIXTYFORTH, 0 },
            { 0.125, NOTE_THIRTYSECOND, 0 },
            { 0.25, NOTE_SIXTEENTH, 0 },
            { 0.5, NOTE_EIGTH, 0 },
            { 1.0, NOTE_QUARTER, 0 },
            { 1.5, NOTE_QUARTER, 1 },
            { 2.0, NOTE_HALF, 0 },
            { 3.0, NOTE_HALF, 1 },
            { 4.0, NOTE_WHOLE, 0 },
            { 6.0, NOTE_WHOLE, 1 },
    };
    
    private static final int[][] NOTE_DATA = {
            // NOTE, Y adjust, sharps, flats
            { 0, 0, 0, 0 },
            { 1, 0, 0, 0 },
            { 2, 0, 0, 0 },
            { 3, 0, 0, 0 },
            { 4, 0, 0, 0 },
            { 5, 0, 0, 0 },
            { 6, 0, 0, 0 },
            { 7, 0, 0, 0 },
            { 8, 0, 0, 0 },
            { 9, 0, 0, 0 },
            { 10, 0, 0, 0 },
            { 11, 0, 0, 0 },
            { 12, -28, 0, 0 },    // C0
            { 13, -28, 1, 0 },
            { 14, -27, 0, 0 },    // D0        
            { 15, -26, 0, 1 },
            { 16, -26, 0, 0 },    // E0
            { 17, -25, 0, 0 },    // F0
            { 18, -25, 1, 0 },
            { 19, -24, 0, 0 },    // G0
            { 20, -23, 0, 1 },
            { 21, -23, 0, 0 },    // A0
            { 22, -22, 0, 1 },
            { 23, -22, 0, 0 },    // B0
            { 24, -21, 0, 0 },    // C1
            { 25, -21, 1, 0 },
            { 26, -20, 0, 0 },    // D1
            { 27, -19, 0, 1 },
            { 28, -19, 0, 0 },    // E1
            { 29, -18, 0, 0 },    // F1
            { 30, -18, 1, 0 },    
            { 31, -17, 0, 0 },    // G1
            { 32, -16, 0, 1 },
            { 33, -16, 0, 0 },    // A1
            { 34, -15, 0, 1 },
            { 35, -15, 0, 0 },    // B1
            { 36, -14, 0, 0 },    // C2
            { 37, -14, 1, 0 },    
            { 38, -13, 0, 0 },    // D2
            { 39, -12, 0, 1 },
            { 40, -12, 0, 0 },    // E2
            { 41, -11, 0, 0 },    // F2
            { 42, -11, 1, 0 },
            { 43, -10, 0, 0 },    // G2
            { 44, -9, 0, 1 },
            { 45, -9, 0, 0 },    // A2
            { 46, -8, 0, 1 },
            { 47, -8, 0, 0 },    // B2
            { 48, -7, 0, 0 },    // C3
            { 49, -7, 1, 0 },
            { 50, -6, 0, 0 },    // D3
            { 51, -5, 0, 1 },
            { 52, -5, 0, 0 },    // E3
            { 53, -4, 0, 0 },    // F3
            { 54, -4, 1, 0 },
            { 55, -3, 0, 0 },    // G3
            { 56, -2, 0, 1 },
            { 57, -2, 0, 0 },    // A3
            { 58, -1, 0, 1 },
            { 59, -1, 0, 0 },    // B3
            { 60, 0, 0, 0 },    // C4
            { 61, 0, 1, 0 },
            { 62, 1, 0, 0 },    // D4
            { 63, 2, 0, 1 },
            { 64, 2, 0, 0 },    // E4
            { 65, 3, 0, 0 },    // F4
            { 66, 3, 1, 0 },
            { 67, 4, 0, 0 },    // G4
            { 68, 5, 0, 1 },
            { 69, 5, 0, 0 },    // A4
            { 70, 6, 0, 1 },
            { 71, 6, 0, 0 },    // B4
            { 72, 7, 0, 0 },    // C5
            { 73, 7, 1, 0 },
            { 74, 8, 0, 0 },    // D5
            { 75, 9, 0, 1 },
            { 76, 9, 0, 0 },    // E5
            { 77, 10, 0, 0 },    // F5
            { 78, 10, 1, 0 },
            { 79, 11, 0, 0 },    // G5
            { 80, 12, 0, 1 },
            { 81, 12, 0, 0 },    // A5
            { 82, 13, 0, 1 },
            { 83, 13, 0, 0 },    // B5
            { 84, 14, 0, 0 },    // C6
            { 85, 14, 1, 0 },
            { 86, 15, 0, 0 },    // D6
            { 87, 16, 0, 1 },
            { 88, 16, 0, 0 },    // E6
            { 89, 17, 0, 0 },    // F6
            { 90, 17, 1, 0 },
            { 91, 18, 0, 0 },    // G6
            { 92, 19, 0, 1 },
            { 93, 19, 0, 0 },    // A6
            { 94, 20, 0, 1 },
            { 95, 20, 0, 0 },    // B6
            { 96, 21, 0, 0 },    // C7
            { 97, 21, 1, 0 },
            { 98, 22, 0, 0 },    // D7
            { 99, 23, 0, 1 },
            { 100, 23, 0, 0 },   // E7
            { 101, 24, 0, 0 },   // F7
            { 102, 24, 1, 0 },   
            { 103, 25, 0, 0 },   // G7
            { 104, 26, 0, 1 },   
            { 105, 26, 0, 0 },   // A7
            { 106, 27, 0, 1 },
            { 107, 27, 0, 0 },   // B7
            { 108, 28, 0, 0 },   // C8
            { 109, 28, 1, 0 },
            
    };

    public static void autoTrack(MIDITune midi)
    {
        int ppq = midi.getPulsesPerQuarter();
        for (MIDITrack track : midi.getTrackInfos())
        {
            double simNotes = autoTrack(track, ppq);
            System.out.println("Track "+track.getTrack()+", simNotes="+simNotes+" clef="+track.getClef()+" ->"+track.getType());
        }
    }
    
    //private static final long GRAN = 32;

    public static double autoTrack(MIDITrack track, int ppq)
    {
        if (track.getType() != MIDITrack.UNKNOWN)
            return 0;
        if (track.getNotes().size() == 0)
        {
            track.setType(MIDITrack.IGNORE);
            return 0;
        }
        else if (track.getNotes().size() < 10)
        {
            track.setType(MIDITrack.INCIDENTAL);
            return 0;
        }
        double simNotes = calcSimNotes(track, ppq);
        if (simNotes >= 3)
            track.setType(MIDITrack.RHYTHYM);
        else if (track.getClef() == MIDITrack.BASS_CLEF)
            track.setType(MIDITrack.BASS);
        else if (track.getClef() == MIDITrack.TREBLE_CLEF)
            track.setType(MIDITrack.MELODY);
        else
            track.setType(MIDITrack.HARMONY);
        return simNotes;
    }

    public static double calcSimNotes(MIDITrack track, int pp4)
    {
        int pp32 = pp4/8;
        int numChords = 0;
        int nonChords = 0;
        int sizeChords = 0;
        for (int i = 0; i < track.getNotes().size();)
        {
            MIDINote n = track.getNotes().get(i);
            int size = 1;
            while (i + size < track.getNotes().size())
            {
                MIDINote n2 = track.getNotes().get(i + size);
                if (n2.getTick() > n.getTick() + pp32)
                    break;
                size++;
            }
            if (size == 1)
                nonChords++;
            else
            {
                numChords++;
                sizeChords += size;
            }
            i += size;
        }
        double avgChord = sizeChords/(double)numChords;
        //System.out.println("non-chords: "+nonChords+", chords="+numChords+", sizeChords="+sizeChords+", avgChord="+avgChord);
        if (numChords < nonChords)
            return 1;
        else if (avgChord < 2)
            return 1;
        else
            return avgChord;
    }

    /*
    public static double calcSimNotesQuarters(MIDITrack track, int ppq)
    {
        MIDINote first = track.getNotes().get(0);
        MIDINote last = track.getNotes().get(track.getNotes().size()-1);
        long firstTick = first.getTick();
        long lastTick = last.getTick() + last.getDuration();
        int[] counts = new int[(int)((lastTick - firstTick)/ppq)+2];
        for (MIDINote n : track.getNotes())
        {
            long startTick = n.getTick();
            long endTick = n.getTick() + n.getDuration();
            int startq = (int)Math.floor(((double)startTick - firstTick)/ppq);
            int endq = (int)Math.ceil(((double)endTick - firstTick)/ppq);
            if (track.getTrack() == 1)
                System.out.println("Note: "+MIDINote.NOTES[n.getPitch()]+" from "+startTick+" to "+endTick+" or "+startq+" to "+endq);
            for (int q = startq; q <= endq; q++)
                try
                {
                    counts[q]++;
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    e.printStackTrace();
                }
        }
        int totalq = 0;
        int qs = 0;
        for (int q = 0; q < counts.length; q++)
            if (counts[q] > 1)
            {
                totalq += counts[q];
                qs++;
                if (track.getTrack() == 1)
                    System.out.println("Q#"+q+" total "+counts[q]);
            }
        return totalq/(double)qs;
    }

    public static double calcSimNotesOverlaps(MIDITrack track, int ppq)
    {
        int notes = 0;
        int overlaps = 0;
        for (int i = 0; i < track.getNotes().size(); i++)
        {
            int o = getOverlaps(track.getNotes(), i, ppq);
            overlaps += o;
            notes++;
        }
        double simNotes = ((double)overlaps)/notes;
        return simNotes;
    }

    private static int getOverlaps(List<MIDINote> notes, int idx, int ppq)
    {
        MIDINote note = notes.get(idx);
        int count = 0;
        // go backwards
        for (int i = idx - 1; i >= 0; i--)
        {
            MIDINote n = notes.get(i);
            if (n.getTick() + n.getDuration() < note.getTick())
                break;
            count++;
        }
        // go forwards
        for (int i = idx + 1; i < notes.size(); i++)
        {
            MIDINote n = notes.get(i);
            if (note.getTick() + note.getDuration() < n.getTick())
                break;
            count++;
        }
        return count;
    }
    */
}
