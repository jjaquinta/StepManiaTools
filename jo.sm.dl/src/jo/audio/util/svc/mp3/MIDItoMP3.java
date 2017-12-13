package jo.audio.util.svc.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import net.sourceforge.lame.mp3.WAVtoMP3Logic;

public class MIDItoMP3
{
    public static final void convert(File midi, File mp3) throws Exception
    {
        FileInputStream midiIn = new FileInputStream(midi);
        PipedOutputStream wavOut = new PipedOutputStream();
        PipedInputStream wavIn = new PipedInputStream(wavOut);
        FileOutputStream mp3Out = new FileOutputStream(mp3);
        Thread t1 = new Thread() { public void run() { MIDItoWAVLogic.midi2wav(midiIn, wavOut); } };
        Thread t2 = new Thread() { public void run() { 
            try
            {
                WAVtoMP3Logic.convert(wavIn, mp3Out);
                mp3Out.close(); 
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
            } };
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
    
    public static final void main(String[] argv) throws Exception
    {
        // test
        convert(new File("c:\\temp\\test.mid"), new File("c:\\temp\\test.mp3"));
    }
}
