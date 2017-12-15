package jo.sm.dl.cmd;

import java.io.File;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

import jo.util.utils.obj.ByteUtils;

public class MetronomeGenerate
{
    private String[]    mArgs;
    
    private File        mOutFile;
    private int         mBPM;
    private int         mLength;
    
    public MetronomeGenerate(String[] argv)
    {
        mArgs = argv;
        mBPM = 60;
        mLength = 120;
    }
    
    public void run()
    {
        parseArgs();
        try
        {
            Sequence midi = generateSequence();
            MidiSystem.write(midi, 1, mOutFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private Sequence generateSequence() throws InvalidMidiDataException
    {
        float spq = 60f/mBPM;
        float mspq = spq*1000;
        float uspq = mspq*1000;
        long microsPerQuarter = (long)uspq;
        Sequence seq = new Sequence(Sequence.PPQ, 16);
        Track t = seq.createTrack();

      //****  General MIDI sysex -- turn on General MIDI sound set  ****
        byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
        SysexMessage sm = new SysexMessage();
        sm.setMessage(b, 6);
        MidiEvent me = new MidiEvent(sm,(long)0);
        t.add(me);

//****  set tempo (meta event)  ****
        byte[] tempo = ByteUtils.toBytes(microsPerQuarter);
        MetaMessage mt = new MetaMessage();
        byte[] bt = {tempo[5], tempo[6], tempo[7]};
        mt.setMessage(0x51 ,bt, 3);
        me = new MidiEvent(mt,(long)0);
        t.add(me);

//****  set track name (meta event)  ****
        mt = new MetaMessage();
        String TrackName = new String("midifile track");
        mt.setMessage(0x03 ,TrackName.getBytes(), TrackName.length());
        me = new MidiEvent(mt,(long)0);
        t.add(me);

//****  set omni on  ****
        ShortMessage mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7D,0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

//****  set poly on  ****
        mm = new ShortMessage();
        mm.setMessage(0xB0, 0x7F,0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

//****  set instrument to Piano  ****
        mm = new ShortMessage();
        mm.setMessage(0xC0, 0x00, 0x00);
        me = new MidiEvent(mm,(long)0);
        t.add(me);

        int tick = 0;
        for (int i = 0; i < mLength; i++)
        {
            int noteNum = 48;
            int duration = 8;
            int velocity = 64;
            ShortMessage smsg = new ShortMessage(ShortMessage.NOTE_ON, 1, noteNum, velocity);
            MidiEvent ev = new MidiEvent(smsg, tick);
            t.add(ev);
            smsg = new ShortMessage(ShortMessage.NOTE_OFF, 1, noteNum, velocity);
            ev = new MidiEvent(smsg, tick+duration-1);
            t.add(ev);
            tick += duration;
        }
        return seq;
    }

    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            if ("-o".equals(mArgs[i]))
                mOutFile = new File(mArgs[++i]);
            else if ("-bpm".equals(mArgs[i]))
                mBPM = Integer.parseInt(mArgs[++i]);
            else if ("-l".equals(mArgs[i]))
                mLength = Integer.parseInt(mArgs[++i]);
    }
    
    public static void main(String[] argv)
    {
        MetronomeGenerate app = new MetronomeGenerate(argv);
        app.run();
    }
}
