package jo.sm.dl.logic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import jo.sm.dl.data.midi.MIDINote;
import jo.sm.dl.data.midi.MIDITrack;
import jo.sm.dl.data.midi.MIDITune;
import jo.sm.dl.data.sm.SMBeat;
import jo.sm.dl.data.sm.SMMark;
import jo.sm.dl.data.sm.SMMeasure;
import jo.sm.dl.data.sm.SMProject;
import jo.sm.dl.data.sm.pat.PatDef;
import jo.sm.dl.data.sm.pat.PatInst;
import jo.util.utils.obj.ByteUtils;

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
    
    public static String getInstrumentName(int bank, int program)
    {
        Instrument i = getInstrument(bank, program);
        if (i == null)
            return program+"?"+bank;
        else
            return i.getName();
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
        for (int i = 0; i < tracks.length; i++)
        {
            MIDITrack t = new MIDITrack();
            t.setTrack(i);
            tune.getTrackInfos().add(t);
        }
        //System.out.println(tracks.length+" tracks");
        for (int i = 0; i < tracks.length; i++)
        {
            Track track = tracks[i];
            //System.out.println("Track "+(i+1)+": events="+track.size());
            Map<Integer,MIDINote> onNotes = new HashMap<>();
            int bank = 0;
            int program = 0;
            int[] controlValues = new int[256];
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
                        note.setVolume(controlValues[7]);
                        note.setExpression(controlValues[11]);
                        notes.add(note);
                        if (onNotes.containsKey(pitch))
                        {
                            MIDINote on = onNotes.get(pitch);
                            on.setDuration(event.getTick() - on.getTick());
                        }
                        onNotes.put(pitch, note);
                        MIDITrack t = tune.getTrackInfos().get(note.getTrack());
                        t.add(note);
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
                    else if ((smsg.getCommand()&0xF0) == 0xb0)
                    {   // channel mode message
                        int c = smsg.getData1();
                        int v = smsg.getData2();
                        controlValues[c] = v;
                        if (c == 11)
                            System.out.print(" exp("+v+")."+event.getTick());
                        else if (c == 7)
                            System.out.print(" vol("+v+")."+event.getTick());
                    }
                    //else
                    //    System.out.print(" 0x"+Integer.toHexString(smsg.getCommand()));
                }
                else
                {
                    byte[] data = msg.getMessage();
                    if (((data[0]&0xff) == 0xff) && ((data[1]&0xff) == 0x51))
                    {   // tempo
                        int msPerQuarter = ByteUtils.toInt((byte)0, data[3], data[4], data[5]);
                        float tbpm = 60000000f/msPerQuarter;
                        float beat = (event.getTick()/(float)tune.getPulsesPerQuarter());
                        System.out.println("Track "+i+" tick="+event.getTick()+" beat="+beat+"  (tempo ms/q="+msPerQuarter+", bpm="+tbpm+")");
                        if (i == 0)
                            tune.getBPMs().add(new SMMark(beat, tbpm));
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
        Map<Long, Integer> velocity = new HashMap<>();
        Map<Long, Integer> expression = new HashMap<>();
        Map<Long, Integer> volume = new HashMap<>();
        for (MIDINote n : proj.getMIDI().getNotes())
        {
            long t = n.getTick();
            t /= q;
            if (!velocity.containsKey(t) || (n.getVelocity() > velocity.get(t)))
                velocity.put(t,  n.getVelocity());
            if (!expression.containsKey(t) || (n.getExpression() > expression.get(t)))
                expression.put(t,  n.getExpression());
            if (!volume.containsKey(t) || (n.getVolume() > volume.get(t)))
                volume.put(t,  n.getVolume());
        }
        List<Long> keys = new ArrayList<>();        
        keys.addAll(velocity.keySet());
        Collections.sort(keys);
        BufferedImage img = new BufferedImage(keys.size(), 256*3, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.YELLOW);
        for (int x = 0; x < keys.size(); x++)
        {
            int y = velocity.get(keys.get(x));
            g.drawLine(x, 256-1, x, 256 - y);
        }
        g.setColor(Color.GREEN);
        for (int x = 0; x < keys.size(); x++)
        {
            int y = expression.get(keys.get(x));
            g.drawLine(x, 512-1, x, 512- y);
        }
        g.setColor(Color.BLUE);
        for (int x = 0; x < keys.size(); x++)
        {
            int y = volume.get(keys.get(x));
            g.drawLine(x, 768-1, x, 768 - y);
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
        Collections.sort(proj.getPatterns(), new Comparator<PatDef>() {
            @Override
            public int compare(PatDef o1, PatDef o2)
            {
                return o2.getUsed() - o1.getUsed();
            }
        });
        int paty = maxNote + 1;
        for (int i = 0; i < proj.getPatterns().size(); i++)
        {
            PatDef pat = proj.getPatterns().get(i);
            if (pat.getUsed() == 0)
                continue;
            for (int j = 0; j < pat.getInstances().size(); j++)
            {
                PatInst inst = pat.getInstances().get(j);
                int x1 = (int)(inst.getNotes().get(0).getTick()/q);
                int x2 = (int)(inst.getNotes().get(inst.getNotes().size() - 1).getTick()/q);
                if (inst.isUsed())
                    g.setColor(VOICE_COLOR[i%VOICE_COLOR.length]);
                else
                    g.setColor(Color.DARK_GRAY);
                g.drawLine(x1, paty + j%2, x2, paty + j%2);
            }
            paty += 2;
            if (paty >= img.getHeight())
                break;
        }
        // add steps
        g.setColor(Color.WHITE);
        for (SMMeasure measure : proj.getTune().getCharts().get(0).getMeasures())
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
