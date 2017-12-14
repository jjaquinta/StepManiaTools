package jo.audio.util.svc.mp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MIDItoOGG
{
    public static final void convert(File midi, File ogg) throws Exception
    {
        File tmp = File.createTempFile("dllama", ".wav");
        FileInputStream midiIn = new FileInputStream(midi);
        FileOutputStream wavOut = new FileOutputStream(tmp);
        MIDItoWAVLogic.midi2wav(midiIn, wavOut);
        midiIn.close();
        wavOut.close();
        FileInputStream wavIn = new FileInputStream(tmp);
        FileOutputStream oggOut = new FileOutputStream(ogg);
        WAVtoOGGLogic.convert(wavIn, oggOut);
        wavIn.close();
        oggOut.close();
        /*
        FileInputStream midiIn = new FileInputStream(midi);
        PipedOutputStream wavOut = new PipedOutputStream();
        PipedInputStream wavIn = new PipedInputStream(wavOut);
        FileOutputStream mp3Out = new FileOutputStream(ogg);
        Thread t1 = new Thread() { public void run() { MIDItoWAVLogic.midi2wav(midiIn, wavOut); } };
        Thread t2 = new Thread() { public void run() { 
            try
            {
                WAVtoOGGLogic.convert(wavIn, mp3Out);
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
        */
    }
    
    public static final void main(String[] argv) throws Exception
    {
        // test
        convert(new File("c:\\temp\\test.mid"), new File("c:\\temp\\test.mp3"));
    }
}
