package jo.sm.dl.data;

import java.util.HashMap;
import java.util.Map;

public class MIDINote implements Comparable<MIDINote>
{
    private int mPitch;
    private int mVelocity;
    private int mExpression;
    private int mVolume;
    private long mTick;
    private long mDuration;
    private int mTrack;
    private int mBank;
    private int mProgram;
    
    public static final String[] NOTES = {
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",      
      "10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
      "20",
      "A0", "Bb0", "B0",
      "C1", "Db1", "D1", "Eb1", "E1", "F1", "Gb1", "G1", "Ab1", "A1", "Bb1", "B1", 
      "C2", "Db2", "D2", "Eb2", "E2", "F2", "Gb2", "G2", "Ab2", "A2", "Bb2", "B2", 
      "C3", "Db3", "D3", "Eb3", "E3", "F3", "Gb3", "G3", "Ab3", "A3", "Bb3", "B3", 
      "C4", "Db4", "D4", "Eb4", "E4", "F4", "Gb4", "G4", "Ab4", "A4", "Bb4", "B4", 
      "C5", "Db5", "D5", "Eb5", "E5", "F5", "Gb5", "G5", "Ab5", "A5", "Bb5", "B5", 
      "C6", "Db6", "D6", "Eb6", "E6", "F6", "Gb6", "G6", "Ab6", "A6", "Bb6", "B6", 
      "C7", "Db7", "D7", "Eb7", "E7", "F7", "Gb7", "G7", "Ab7", "A7", "Bb7", "B7", 
      "C8", "Db8", "D8", "Eb8", "E8", "F8", "Gb8", "G8", "Ab8", "A8", "Bb8", "B8", 
      "C9", "Db9", "D9", "Eb9", "E9", "F9", "Gb9", "G9", "Ab9", "A9", "Bb9", "B9", 
    };
    
    public final static Map<Integer, String> NUMBER_TO_INSTRUMENT = new HashMap<Integer,String>();
    static
    {
        NUMBER_TO_INSTRUMENT.put(1, "acoustic grand piano");
        NUMBER_TO_INSTRUMENT.put(2, "bright acoustic piano");
        NUMBER_TO_INSTRUMENT.put(3, "electric grand piano");
        NUMBER_TO_INSTRUMENT.put(4, "honky-tonk piano");
        NUMBER_TO_INSTRUMENT.put(5, "electric piano 1");
        NUMBER_TO_INSTRUMENT.put(6, "electric piano 2");
        NUMBER_TO_INSTRUMENT.put(7, "harpsichord");
        NUMBER_TO_INSTRUMENT.put(8, "clavi");
        NUMBER_TO_INSTRUMENT.put(9, "celesta");
        NUMBER_TO_INSTRUMENT.put(10, "glockenspiel");
        NUMBER_TO_INSTRUMENT.put(11, "music box");
        NUMBER_TO_INSTRUMENT.put(12, "vibraphone");
        NUMBER_TO_INSTRUMENT.put(13, "marimba");
        NUMBER_TO_INSTRUMENT.put(14, "xylophone");
        NUMBER_TO_INSTRUMENT.put(15, "tubular bells");
        NUMBER_TO_INSTRUMENT.put(16, "dulcimer");
        NUMBER_TO_INSTRUMENT.put(17, "drawbar organ");
        NUMBER_TO_INSTRUMENT.put(18, "percussive organ");
        NUMBER_TO_INSTRUMENT.put(19, "rock organ");
        NUMBER_TO_INSTRUMENT.put(20, "church organ");
        NUMBER_TO_INSTRUMENT.put(21, "reed organ");
        NUMBER_TO_INSTRUMENT.put(22, "accordion");
        NUMBER_TO_INSTRUMENT.put(23, "harmonica");
        NUMBER_TO_INSTRUMENT.put(24, "tango accordion");
        NUMBER_TO_INSTRUMENT.put(25, "acoustic guitar (nylon)");
        NUMBER_TO_INSTRUMENT.put(26, "acoustic guitar (steel)");
        NUMBER_TO_INSTRUMENT.put(27, "electric guitar (jazz)");
        NUMBER_TO_INSTRUMENT.put(28, "electric guitar (clean)");
        NUMBER_TO_INSTRUMENT.put(29, "electric guitar (muted)");
        NUMBER_TO_INSTRUMENT.put(30, "overdriven guitar");
        NUMBER_TO_INSTRUMENT.put(31, "distortion guitar");
        NUMBER_TO_INSTRUMENT.put(32, "guitar harmonics");
        NUMBER_TO_INSTRUMENT.put(33, "acoustic bass");
        NUMBER_TO_INSTRUMENT.put(34, "electric bass (finger)");
        NUMBER_TO_INSTRUMENT.put(35, "electric bass (pick)");
        NUMBER_TO_INSTRUMENT.put(36, "fretless bass");
        NUMBER_TO_INSTRUMENT.put(37, "slap bass 1");
        NUMBER_TO_INSTRUMENT.put(38, "slap bass 2");
        NUMBER_TO_INSTRUMENT.put(39, "synth bass 1");
        NUMBER_TO_INSTRUMENT.put(40, "synth bass 2");
        NUMBER_TO_INSTRUMENT.put(41, "violin");
        NUMBER_TO_INSTRUMENT.put(42, "viola");
        NUMBER_TO_INSTRUMENT.put(43, "cello");
        NUMBER_TO_INSTRUMENT.put(44, "contrabass");
        NUMBER_TO_INSTRUMENT.put(45, "tremolo strings");
        NUMBER_TO_INSTRUMENT.put(46, "pizzicato strings");
        NUMBER_TO_INSTRUMENT.put(47, "orchestral harp");
        NUMBER_TO_INSTRUMENT.put(48, "timpani");
        NUMBER_TO_INSTRUMENT.put(49, "string ensemble 1");
        NUMBER_TO_INSTRUMENT.put(50, "string ensemble 2");
        NUMBER_TO_INSTRUMENT.put(51, "synthstrings 1");
        NUMBER_TO_INSTRUMENT.put(52, "synthstrings 2");
        NUMBER_TO_INSTRUMENT.put(53, "choir aahs");
        NUMBER_TO_INSTRUMENT.put(54, "voice oohs");
        NUMBER_TO_INSTRUMENT.put(55, "synth voice");
        NUMBER_TO_INSTRUMENT.put(56, "orchestra hit");
        NUMBER_TO_INSTRUMENT.put(57, "trumpet");
        NUMBER_TO_INSTRUMENT.put(58, "trombone");
        NUMBER_TO_INSTRUMENT.put(59, "tuba");
        NUMBER_TO_INSTRUMENT.put(60, "muted trumpet");
        NUMBER_TO_INSTRUMENT.put(61, "french horn");
        NUMBER_TO_INSTRUMENT.put(62, "brass section");
        NUMBER_TO_INSTRUMENT.put(63, "synthbrass 1");
        NUMBER_TO_INSTRUMENT.put(64, "synthbrass 2");
        NUMBER_TO_INSTRUMENT.put(65, "soprano sax");
        NUMBER_TO_INSTRUMENT.put(66, "alto sax");
        NUMBER_TO_INSTRUMENT.put(67, "tenor sax");
        NUMBER_TO_INSTRUMENT.put(68, "baritone sax");
        NUMBER_TO_INSTRUMENT.put(69, "oboe");
        NUMBER_TO_INSTRUMENT.put(70, "english horn");
        NUMBER_TO_INSTRUMENT.put(71, "bassoon");
        NUMBER_TO_INSTRUMENT.put(72, "clarinet");
        NUMBER_TO_INSTRUMENT.put(73, "piccolo");
        NUMBER_TO_INSTRUMENT.put(74, "flute");
        NUMBER_TO_INSTRUMENT.put(75, "recorder");
        NUMBER_TO_INSTRUMENT.put(76, "pan flute");
        NUMBER_TO_INSTRUMENT.put(77, "blown bottle");
        NUMBER_TO_INSTRUMENT.put(78, "shakuhachi");
        NUMBER_TO_INSTRUMENT.put(79, "whistle");
        NUMBER_TO_INSTRUMENT.put(80, "ocarina");
        NUMBER_TO_INSTRUMENT.put(81, "lead 1 (square)");
        NUMBER_TO_INSTRUMENT.put(82, "lead 2 (sawtooth)");
        NUMBER_TO_INSTRUMENT.put(83, "lead 3 (calliope)");
        NUMBER_TO_INSTRUMENT.put(84, "lead 4 (chiff)");
        NUMBER_TO_INSTRUMENT.put(85, "lead 5 (charang)");
        NUMBER_TO_INSTRUMENT.put(86, "lead 6 (voice)");
        NUMBER_TO_INSTRUMENT.put(87, "lead 7 (fifths)");
        NUMBER_TO_INSTRUMENT.put(88, "lead 8 (bass + lead)");
        NUMBER_TO_INSTRUMENT.put(89, "pad 1 (new age)");
        NUMBER_TO_INSTRUMENT.put(90, "pad 2 (warm)");
        NUMBER_TO_INSTRUMENT.put(91, "pad 3 (polysynth)");
        NUMBER_TO_INSTRUMENT.put(92, "pad 4 (choir)");
        NUMBER_TO_INSTRUMENT.put(93, "pad 5 (bowed)");
        NUMBER_TO_INSTRUMENT.put(94, "pad 6 (metallic)");
        NUMBER_TO_INSTRUMENT.put(95, "pad 7 (halo)");
        NUMBER_TO_INSTRUMENT.put(96, "pad 8 (sweep)");
        NUMBER_TO_INSTRUMENT.put(97, "fx 1 (rain)");
        NUMBER_TO_INSTRUMENT.put(98, "fx 2 (soundtrack)");
        NUMBER_TO_INSTRUMENT.put(99, "fx 3 (crystal)");
        NUMBER_TO_INSTRUMENT.put(100, "fx 4 (atmosphere)");
        NUMBER_TO_INSTRUMENT.put(101, "fx 5 (brightness)");
        NUMBER_TO_INSTRUMENT.put(102, "fx 6 (goblins)");
        NUMBER_TO_INSTRUMENT.put(103, "fx 7 (echoes)");
        NUMBER_TO_INSTRUMENT.put(104, "fx 8 (sci-fi)");
        NUMBER_TO_INSTRUMENT.put(105, "sitar");
        NUMBER_TO_INSTRUMENT.put(106, "banjo");
        NUMBER_TO_INSTRUMENT.put(107, "shamisen");
        NUMBER_TO_INSTRUMENT.put(108, "koto");
        NUMBER_TO_INSTRUMENT.put(109, "kalimba");
        NUMBER_TO_INSTRUMENT.put(110, "bag pipe");
        NUMBER_TO_INSTRUMENT.put(111, "fiddle");
        NUMBER_TO_INSTRUMENT.put(112, "shanai");
        NUMBER_TO_INSTRUMENT.put(113, "tinkle bell");
        NUMBER_TO_INSTRUMENT.put(114, "agogo");
        NUMBER_TO_INSTRUMENT.put(115, "steel drums");
        NUMBER_TO_INSTRUMENT.put(116, "woodblock");
        NUMBER_TO_INSTRUMENT.put(117, "taiko drum");
        NUMBER_TO_INSTRUMENT.put(118, "melodic tom");
        NUMBER_TO_INSTRUMENT.put(119, "synth drum");
        NUMBER_TO_INSTRUMENT.put(120, "reverse cymbal");
        NUMBER_TO_INSTRUMENT.put(121, "guitar fret noise");
        NUMBER_TO_INSTRUMENT.put(122, "breath noise");
        NUMBER_TO_INSTRUMENT.put(123, "seashore");
        NUMBER_TO_INSTRUMENT.put(124, "bird tweet");
        NUMBER_TO_INSTRUMENT.put(125, "telephone ring");
        NUMBER_TO_INSTRUMENT.put(126, "helicopter");
        NUMBER_TO_INSTRUMENT.put(127, "applause");
        NUMBER_TO_INSTRUMENT.put(128, "gunshot");
    }
    
    public static String getInstrument(int num)
    {
        String name = NUMBER_TO_INSTRUMENT.get(num);
        if (name == null)
            name = "#"+num;
        return name;
    }

    // utilities
    public long getVoice()
    {
        long v = ((long)getTrack()<<32) | ((long)getBank()<<16) | ((long)getProgram()<<0);
        return v;
    }
    
    public long getLoud()
    {
        return mVelocity + mExpression + mVolume;
    }
    
    @Override
    public int compareTo(MIDINote o)
    {
        return (int)Math.signum(mTick - o.mTick);
    }
    
    // getters and setters
    public int getPitch()
    {
        return mPitch;
    }
    public void setPitch(int pitch)
    {
        mPitch = pitch;
    }
    public long getTick()
    {
        return mTick;
    }
    public void setTick(long Tick)
    {
        mTick = Tick;
    }
    public long getDuration()
    {
        return mDuration;
    }
    public void setDuration(long duration)
    {
        mDuration = duration;
    }
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
    public int getVelocity()
    {
        return mVelocity;
    }
    public void setVelocity(int velocity)
    {
        mVelocity = velocity;
    }

    public int getExpression()
    {
        return mExpression;
    }

    public void setExpression(int expression)
    {
        mExpression = expression;
    }

    public int getVolume()
    {
        return mVolume;
    }

    public void setVolume(int volume)
    {
        mVolume = volume;
    }
}
