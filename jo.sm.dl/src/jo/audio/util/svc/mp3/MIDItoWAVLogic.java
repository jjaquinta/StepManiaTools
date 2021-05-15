package jo.audio.util.svc.mp3;
/*
 * Computoser is a music-composition algorithm and a website to present the results 
 * Copyright (C) 2012-2014  Bozhidar Bozhanov 
 * 
 * Computoser is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version. 
 * 
 * Computoser is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU Affero General Public License for more details. 
 * 
 * You should have received a copy of the GNU Affero General Public License 
 * along with Computoser.  If not, see <http://www.gnu.org/licenses/>. 
 */
 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem; 
 
public class MIDItoWAVLogic { 
    public static void main(String[] argv) throws IOException
    {
    	FileInputStream fis = new FileInputStream("c:\\temp\\test.mid");
    	FileOutputStream fos = new FileOutputStream("c:\\temp\\test.wav");
    	midi2wav(fis, fos);
    	fis.close();
    	fos.close();
    }
 
    public static void midi2wav(InputStream is, OutputStream os) { 
        try { 
            Sequence sequence = MidiSystem.getSequence(is); 
            render(sequence, os); 
        } catch (Exception e) { 
            throw new RuntimeException(e); 
        } 
    } 
    
    /*
     * Render sequence using selected or default soundbank into wave audio file. 
     */ 
    public static void render(Sequence sequence, OutputStream outStream) { 
        try { 
            // Find available AudioSynthesizer. 
            Synthesizer synth = findAudioSynthesizer(); 
            if (synth == null) { 
                System.err.println("No AudioSynhtesizer was found!"); 
                return; 
            } 
 
            // Open AudioStream from AudioSynthesizer. 
            AudioFormat format = null;//new AudioFormat(44100, 16, 2, true, false);
            AudioInputStream stream = (AudioInputStream)getOpenStream(synth).invoke(synth, format, null); 
 
            //Generator.loadSoundbankInstruments(synth); 
            // Play Sequence into AudioSynthesizer Receiver. 
            format = new AudioFormat(44100, 16, 2, true, false);
            double total = send(sequence, synth.getReceiver()); 
 
            // Calculate how long the WAVE file needs to be. 
            long len = (long) (stream.getFormat().getFrameRate() * (total + 4)); 
            stream = new AudioInputStream(stream, stream.getFormat(), len); 
 
            /*long bytesWritten =*/ AudioSystem.write(stream, AudioFileFormat.Type.WAVE, outStream);
            //int frameSize = format.getFrameSize();
            //float frameRate = format.getFrameRate();
            //float durationInSeconds = (bytesWritten / (frameSize * frameRate));
            
            synth.close(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    } 
 
    /*
     * Send entiry MIDI Sequence into Receiver using timestamps. 
     */ 
    public static double send(Sequence seq, Receiver recv) { 
        float divtype = seq.getDivisionType(); 
        assert (seq.getDivisionType() == Sequence.PPQ); 
        Track[] tracks = seq.getTracks(); 
        int[] trackspos = new int[tracks.length]; 
        int mpq = 500000; 
        int seqres = seq.getResolution(); 
        long lasttick = 0; 
        long curtime = 0; 
        while (true) { 
            MidiEvent selevent = null; 
            int seltrack = -1; 
            for (int i = 0; i < tracks.length; i++) { 
                int trackpos = trackspos[i]; 
                Track track = tracks[i]; 
                if (trackpos < track.size()) { 
                    MidiEvent event = track.get(trackpos); 
                    if (selevent == null || event.getTick() < selevent.getTick()) { 
                        selevent = event; 
                        seltrack = i; 
                    } 
                } 
            } 
            if (seltrack == -1) 
                break; 
            trackspos[seltrack]++; 
            long tick = selevent.getTick(); 
            if (divtype == Sequence.PPQ) 
                curtime += ((tick - lasttick) * mpq) / seqres; 
            else 
                curtime = (long) ((tick * 1000000.0 * divtype) / seqres); 
            lasttick = tick; 
            MidiMessage msg = selevent.getMessage(); 
            if (msg instanceof MetaMessage) { 
                if (divtype == Sequence.PPQ) 
                    if (((MetaMessage) msg).getType() == 0x51) { 
                        byte[] data = ((MetaMessage) msg).getData(); 
                        mpq = ((data[0] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[2] & 0xff); 
                    } 
            } else { 
                if (recv != null) 
                {
                    recv.send(msg, curtime);
                    if (msg instanceof ShortMessage)
                    {
                        ShortMessage smsg = (ShortMessage)msg;
                        if (smsg.getCommand() == 0x90)
                        {   // NOTE ON
                            //int pitch = smsg.getData1();
                            //System.out.println("Tick="+tick+", time="+curtime+", pitch="+pitch);
                        }
                    }
                }
            } 
        } 
 
        return curtime / 1000000.0; 
    } 
 
    /*
     * Find available AudioSynthesizer.
     */
    public static Synthesizer findAudioSynthesizer()
            throws MidiUnavailableException
    {
        // First check if default synthesizer is AudioSynthesizer.
        Synthesizer synth = MidiSystem.getSynthesizer();
        if (getOpenStream(synth) != null)
            return synth;

        // If default synhtesizer is not AudioSynthesizer, check others.
        Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++)
        {
            MidiDevice dev = MidiSystem.getMidiDevice(infos[i]);
            if (getOpenStream(dev) != null)
                return (Synthesizer)synth;
        }

        // No AudioSynthesizer was found, return null.
        return null;
    }
    
    private static Method getOpenStream(Object synthesizer)
    {
        for (Method m : synthesizer.getClass().getMethods())
            if (m.getName().equals("openStream"))
            	return m;
        return null;
    }
}