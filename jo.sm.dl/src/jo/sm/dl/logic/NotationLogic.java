package jo.sm.dl.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jo.sm.dl.data.MIDINotation;
import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITune;
import jo.util.utils.obj.StringUtils;

public class NotationLogic
{
    private static final String NOTE_WHOLE = StringUtils.uniStr(0x1d15D);
    private static final String NOTE_HALF = StringUtils.uniStr(0x1d15E);
    private static final String NOTE_QUARTER = StringUtils.uniStr(0x1d15F);
    private static final String NOTE_EIGTH = StringUtils.uniStr(0x1d160);
    private static final String NOTE_SIXTEENTH = StringUtils.uniStr(0x1d161);
    private static final String NOTE_THIRTYSECOND = StringUtils.uniStr(0x1d162);
    private static final String NOTE_SIXTYFORTH = StringUtils.uniStr(0x1d163);

    public static List<MIDINotation> makeNotation(MIDITune tune)
    {
        List<MIDINotation> notations = new ArrayList<MIDINotation>();
        double ppq = tune.getPulsesPerQuarter();
        for (MIDINote note : tune.getNotes())
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
        Collections.sort(notations, new Comparator<MIDINotation>() {
            @Override
            public int compare(MIDINotation o1, MIDINotation o2)
            {
                return (int)Math.signum(o1.getMeasure() - o2.getMeasure());
            }
        });
        return notations;
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
}
