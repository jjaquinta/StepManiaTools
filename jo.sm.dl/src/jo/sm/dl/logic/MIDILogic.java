package jo.sm.dl.logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.data.PatDef;
import jo.sm.dl.data.PatInst;
import jo.sm.dl.data.SMBeat;
import jo.sm.dl.data.SMMeasure;
import jo.sm.dl.data.SMProject;

public class MIDILogic
{
    private static Instrument[] mAllInstruments = null;
    
    public static Instrument getInstrument(int bank, int program)
    {
        if (mAllInstruments == null)
            try
            {
                mAllInstruments = MidiSystem.getSynthesizer().getAvailableInstruments();
            }
            catch (MidiUnavailableException e)
            {
                throw new IllegalStateException("No MIDI!", e);
            }
        for (Instrument i : mAllInstruments)
            if ((i.getPatch().getBank() == bank) && (i.getPatch().getProgram() == program))
                return i;
        return null;
    }
    
    public static MIDITune getNotes(File midiFile) throws Exception
    {
        MIDITune tune = new MIDITune();
        List<MIDINote> notes = new ArrayList<>();
        tune.setNotes(notes);
        Sequence sequence = MidiSystem.getSequence(midiFile);
        float msPerTick = (float)sequence.getMicrosecondLength()/(float)sequence.getTickLength()/1000;
        tune.setMSPerTick(msPerTick);
        tune.setPulsesPerQuarter(sequence.getResolution());
        float quartersPerSong = sequence.getTickLength()/tune.getPulsesPerQuarter();
        float lengthInSeconds = sequence.getMicrosecondLength()/1000/1000;
        float quartersPerSecond = quartersPerSong/lengthInSeconds;
        float quartersPerMinute = quartersPerSecond*60;
        float beatLengthMS = tune.getMSPerTick()*tune.getPulsesPerQuarter();
        float bpm = 60000L/beatLengthMS;
        tune.setLengthInTicks(sequence.getTickLength());
        tune.setLengthInSeconds(lengthInSeconds);
        tune.setBeatsPerMinute(quartersPerMinute);
        System.out.println("divisionType="+sequence.getDivisionType()+", resolution="+sequence.getResolution()+", PPQ="+Sequence.PPQ);
        System.out.println("usLength="+sequence.getMicrosecondLength()+", tickLength="+sequence.getTickLength()+", ms/tick="+msPerTick);
        System.out.println("qpm="+quartersPerMinute+", bpm="+bpm);
        Track[] tracks = sequence.getTracks();
        tune.setTracks(tracks.length);
        //System.out.println(tracks.length+" tracks");
        for (int i = 0; i < tracks.length; i++)
        {
            Track track = tracks[i];
            //System.out.println("Track "+(i+1)+": events="+track.size());
            Map<Integer,MIDINote> onNotes = new HashMap<>();
            int bank = 0;
            int program = 0;
            for (int j = 0; j < track.size(); j++)
            {
                MidiEvent event = track.get(j);
                MidiMessage msg = event.getMessage();
                if (msg instanceof ShortMessage)
                {
                    ShortMessage smsg = (ShortMessage)msg;
                    if (smsg.getCommand() == 0x90)
                    {   // NOTE ON
                        int pitch = smsg.getData1();
                        int velocity = smsg.getData2();
                        MIDINote note = new MIDINote();
                        note.setTick(event.getTick());
                        note.setBank(bank);
                        note.setProgram(program);
                        note.setTrack(i);
                        note.setPitch(pitch);
                        note.setVelocity(velocity);
                        notes.add(note);
                        if (onNotes.containsKey(pitch))
                        {
                            MIDINote on = onNotes.get(pitch);
                            on.setDuration(event.getTick() - on.getTick());
                        }
                        onNotes.put(pitch, note);
                    }
                    else if (smsg.getCommand() == 0x80)
                    {   // NOTE OFF
                        int pitch = smsg.getData1();
                        MIDINote note = onNotes.get(pitch);
                        if (note != null)
                            note.setDuration(event.getTick() - note.getTick());
                        onNotes.remove(pitch);
                    }
                    else if (smsg.getCommand() == 0xc0)
                    {   // Patch number change
                        program = smsg.getData1();
                        bank = smsg.getData2();
                        //Instrument instrument = getInstrument(bank, program);
                        //System.out.print(" ("+instrument.getName()+")");
                    }
                    else if (smsg.getCommand() == 0xb0)
                    {   // channel mode message
                        //int v = smsg.getData1();
                        //int c = smsg.getData2();
                        //System.out.print(" c("+c+","+v+")");
                    }
                    //else
                    //    System.out.print(" 0x"+Integer.toHexString(smsg.getCommand()));
                }
                else
                {
                    byte[] data = msg.getMessage();
                    if (((data[0]&0xff) == 0xff) && ((data[1]&0xff) == 0x51))
                    {   // tempo
                        //int msPerQuarter = ByteUtils.toInt((byte)0, data[2], data[3], data[4]);
                        //int bpm = 60000000/msPerQuarter;
                        //System.out.print("  (tempo ms/q="+msPerQuarter+", bpm="+bpm+")");
                    }
                    //else
                    //    System.out.print(" 0X"+Integer.toHexString(msg.getMessage()[0]&0xFF)+"."+Integer.toHexString(msg.getMessage()[1]&0xFF));
                }
            }
            //System.out.println();
            //System.out.println(onNotes.size()+" open notes");
        }
        determineGranularity(tune);
        //System.out.println("granularity="+tune.getGranularity()+", bpm="+tune.getBeatsPerMinute());
        return tune;
    }
    
    private static void determineGranularity(MIDITune tune)
    {
        // find lowest common denominator
        long lcd = -1;
        for (MIDINote note : tune.getNotes())
        {
            //System.out.println(lcd+", "+note.getTick());
            lcd = lcd(lcd, note.getTick());
            if (lcd == 1)
                break;
        }
        tune.setGranularity(lcd);
    }
    
    public static long lcd(long a, long b)
    {
        if (a <= 0)
            return b;
        if (b <= 0)
            return a;
        while (b > 0)
        {
            long temp = b;
            b = a % b; // % is remainder
            a = temp;
        }
        return a;
    }

    public static void writeEnergyGraph(SMProject proj, int scale, File eg) throws IOException
    {
        int q = proj.getMIDI().getPulsesPerQuarter()/scale;
        Map<Long, Integer> energy = new HashMap<>();
        for (MIDINote n : proj.getMIDI().getNotes())
        {
            long t = n.getTick();
            t /= q;
            if (!energy.containsKey(t) || (n.getVelocity() > energy.get(t)))
                energy.put(t,  n.getVelocity());
        }
        List<Long> keys = new ArrayList<>();        
        keys.addAll(energy.keySet());
        Collections.sort(keys);
        BufferedImage img = new BufferedImage(keys.size(), 256, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.YELLOW);
        for (int x = 0; x < keys.size(); x++)
        {
            int y = energy.get(keys.get(x));
            g.drawLine(x, img.getHeight()-1, x, img.getHeight() - y);
        }
        g.dispose();
        ImageIO.write(img, "PNG", eg);
    }
    
    private static final Color[] VOICE_COLOR = {
      Color.YELLOW, Color.BLUE, Color.GREEN, Color.CYAN, Color.GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED,       
    };

    public static void writeNoteGraph(SMProject proj, int scale, File ng) throws IOException
    {
        int q = proj.getMIDI().getPulsesPerQuarter()/scale;
        BufferedImage img = new BufferedImage((int)(proj.getMIDI().getLengthInTicks()/q), 256, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        // add notes
        int minNote = 256;
        int maxNote = 0;
        Map<Long, Color> voiceToColor = new HashMap<>();
        for (MIDINote n : proj.getMIDI().getNotes())
        {
            int x1 = (int)(n.getTick()/q);
            int x2 = (int)((n.getTick() + n.getDuration())/q);
            x2 = x1 + 1;
            Color c = voiceToColor.get(n.getVoice());
            if (c == null)
            {
                c = VOICE_COLOR[voiceToColor.size()%VOICE_COLOR.length];
                voiceToColor.put(n.getVoice(), c);
            }
            g.setColor(c);
            int y = n.getPitch();
            g.drawLine(x1, y, x2, y);
            maxNote = Math.max(maxNote, y);
            minNote = Math.max(minNote, y);
        }
        // add patterns
        for (int i = 0; i < proj.getPatterns().size(); i++)
        {
            int y = maxNote + i*2 + 1;
            if (y >= img.getHeight())
                break;
            PatDef pat = proj.getPatterns().get(i);
            for (int j = 0; j < pat.getInstances().size(); j++)
            {
                PatInst inst = pat.getInstances().get(j);
                int x1 = (int)(inst.getNotes().get(0).getTick()/q);
                int x2 = (int)(inst.getNotes().get(inst.getNotes().size() - 1).getTick()/q);
                if (inst.isUsed())
                    g.setColor(VOICE_COLOR[i%VOICE_COLOR.length]);
                else
                    g.setColor(Color.DARK_GRAY);
                g.drawLine(x1, y + j%2, x2, y + j%2);
            }
        }
        // add steps
        g.setColor(Color.WHITE);
        for (SMMeasure measure : proj.getTune().getMeasures())
            for (SMBeat beat : measure.getBeats())
            {
                int x = (int)(beat.getTick()/q);
                for (int y = 0; y < beat.getNotes().length; y++)
                    if ('0' != beat.getNotes()[y])
                        g.drawLine(x, y, x, y);
            }
        g.dispose();
        ImageIO.write(img, "PNG", ng);
    }
}
