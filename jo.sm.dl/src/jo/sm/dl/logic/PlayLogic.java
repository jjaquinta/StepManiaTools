package jo.sm.dl.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.PlayEvent;
import jo.util.logic.ThreadLogic;

public class PlayLogic
{
    private static Thread   mPlayer = null;
    private static boolean  mStop = false;
    
    private static final List<IPlayListener> mListeners = new ArrayList<>();
    
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
            
            List<Object[]> directives = new ArrayList<>();
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
                    //System.out.println("Assigning "+i.getName()+" to channel "+channel+" for track "+track);
                    mChannels[channel].programChange(note.getBank(), note.getProgram());
                    trackToChannel.put(track, channel);
                }
                int channel = trackToChannel.get(track);
                long on = (long)(msPerTick*note.getTick()) - first;
                long off = (long)(msPerTick*(note.getTick()+note.getDuration())) - first;
                directives.add(new Object[] { on, PlayEvent.ON, channel, note });
                directives.add(new Object[] { off, PlayEvent.OFF, channel, note });
            }
            Collections.sort(directives, new Comparator<Object[]>() {
                @Override
                public int compare(Object[] o1, Object[] o2)
                {
                    return (int)Math.signum((long)o1[0] - (long)o2[0]);
                }
            });
            //System.out.println(directives.size()+" directives");
            long start = System.currentTimeMillis();
            while ((directives.size() > 0) && !mStop)
            {
                Object[] directive = directives.get(0);
                long now = System.currentTimeMillis() - start;
                if ((long)(directive[0]) > now)
                {
                    //System.out.println("Waiting "+(directive[0] - now));
                    try { Thread.sleep((long)directive[0] - now); // wait time in milliseconds to control duration
                    } catch( InterruptedException e ) {
                    }
                    continue;
                }
                int action = (Integer)directive[1];
                if (action == PlayEvent.ON)
                {
                    int channel = (int)directive[2];
                    MIDINote note = (MIDINote)directive[3];
                    mChannels[channel].noteOn(note.getPitch(), note.getVelocity());                     
                    firePlayEvent(action, note);
                }
                else if (action == PlayEvent.OFF)
                {
                    int channel = (int)directive[2];
                    MIDINote note = (MIDINote)directive[3];
                    mChannels[channel].noteOff(note.getPitch());                     
                    firePlayEvent(action, note);
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
    
    public static void listen(final Consumer<PlayEvent> ev)
    {
        addPlayListener(new IPlayListener() {
            @Override
            public void event(PlayEvent e)
            {
                ev.accept(e);
            }
        });
    }

    
    public static void addPlayListener(IPlayListener l)
    {
        synchronized (mListeners)
        {
            mListeners.add(l);
        }
    }
    
    public static void removePlayListener(IPlayListener l)
    {
        synchronized (mListeners)
        {
            mListeners.remove(l);
        }
    }
    
    private static void firePlayEvent(int action, MIDINote note)
    {
        IPlayListener[] listeners;
        synchronized (mListeners)
        {
            listeners = mListeners.toArray(new IPlayListener[0]);
        }
        PlayEvent ev = new PlayEvent();
        ev.setAction(action);
        ev.setNote(note);
        for (IPlayListener l : listeners)
            l.event(ev);
    }
}
