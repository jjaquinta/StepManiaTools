package jo.sm.dl.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import jo.sm.dl.data.MIDINote;
import jo.util.logic.ThreadLogic;

public class PlayLogic
{
    private static Thread   mPlayer = null;
    private static boolean  mStop = false;
    
    public static void play(final double msPerTick, final List<MIDINote> notes)
    {
        if (mPlayer != null)
            stop();
        if ((notes == null) || (notes.size() == 0))
            return;
        Collections.sort(notes, new Comparator<MIDINote>(){
            @Override
            public int compare(MIDINote o1, MIDINote o2)
            {
                return (int)Math.signum(o1.getTick() - o2.getTick());
            }
        });
        mPlayer = new Thread("midiplayer") { public void run() { PlayLogic.doPlay(msPerTick, notes); } };
        mStop = false;
        mPlayer.start();
    }
    
    public static void stop()
    {
        mStop = true;
        mPlayer.interrupt();
        while (mPlayer != null)
            ThreadLogic.sleep(100);
    }
    
    private static void doPlay(double msPerTick, List<MIDINote> notes)
    {
        try
        {
            Synthesizer midiSynth = MidiSystem.getSynthesizer(); 
            midiSynth.open();
            
            //Instrument[] instr = midiSynth.getDefaultSoundbank().getInstruments();
            MidiChannel[] mChannels = midiSynth.getChannels();
            
            List<long[]> directives = new ArrayList<>();
            Map<Integer,Integer> trackToChannel = new HashMap<>();
            long first = (long)(notes.get(0).getTick()*msPerTick);
            for (MIDINote note : notes)
            {
                int track = note.getTrack();
                if (!trackToChannel.containsKey(track))
                {
                    int channel = trackToChannel.size();
                    Instrument i = MIDILogic.getInstrument(note.getBank(), note.getProgram());
                    midiSynth.loadInstrument(i);
                    System.out.println("Assigning "+i.getName()+" to channel "+channel+" for track "+track);
                    mChannels[channel].programChange(note.getBank(), note.getProgram());
                    trackToChannel.put(track, channel);
                }
                int channel = trackToChannel.get(track);
                long on = (long)(msPerTick*note.getTick()) - first;
                long off = (long)(msPerTick*(note.getTick()+note.getDuration())) - first;
                directives.add(new long[] { on, 1, channel, note.getPitch(), note.getVelocity() });
                directives.add(new long[] { off, 0, channel, note.getPitch() });
            }
            Collections.sort(directives, new Comparator<long[]>() {
                @Override
                public int compare(long[] o1, long[] o2)
                {
                    return (int)Math.signum(o1[0] - o2[0]);
                }
            });
            System.out.println(directives.size()+" directives");
            long start = System.currentTimeMillis();
            while ((directives.size() > 0) && !mStop)
            {
                long[] directive = directives.get(0);
                long now = System.currentTimeMillis() - start;
                if (directive[0] > now)
                {
                    System.out.println("Waiting "+(directive[0] - now));
                    try { Thread.sleep(directive[0] - now); // wait time in milliseconds to control duration
                    } catch( InterruptedException e ) {
                    }
                    continue;
                }
                if (directive[1] == 1)
                {
                    int channel = (int)directive[2];
                    int pitch = (int)directive[3];
                    int velocity = (int)directive[4];
                    mChannels[channel].noteOn(pitch, velocity);                     
                    System.out.println("On "+pitch+", v="+velocity);
                }
                else if (directive[1] == 0)
                {
                    int channel = (int)directive[2];
                    int pitch = (int)directive[3];
                    mChannels[channel].noteOff(pitch);                     
                    System.out.println("Off "+pitch);
                }
                directives.remove(0);
            }
            for (int i = 0; i < trackToChannel.size(); i++)
                mChannels[i].allNotesOff();
            midiSynth.close();
        }
        catch (Exception e)
        {
            
        }
        mPlayer = null;
    }
}
