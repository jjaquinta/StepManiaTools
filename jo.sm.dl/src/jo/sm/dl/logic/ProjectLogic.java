package jo.sm.dl.logic;

import java.io.File;
import java.io.IOException;

import jo.audio.util.svc.mp3.MIDItoMP3;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.data.SMProject;
import jo.sm.dl.data.SMTune;

public class ProjectLogic
{
    public static SMProject newInstance()
    {
        return new SMProject();
    }
    
    public static boolean load(SMProject proj, File input)
    {
        try
        {
            MIDITune midi = MIDILogic.getNotes(input);
            proj.setMIDI(midi);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }        
        proj.setInput(input);
        return true;
    }
    
    public static boolean dance(SMProject proj)
    {
        PatternLogic.findPatterns(proj);
        proj.setTune(new SMTune());
        DanceLogic.dance(proj);
        return true;
    }
    
    public static boolean save(SMProject proj, File output)
    {
        output.mkdirs();
        proj.setOutput(output);
        String name = output.getName();
        // MP3
        File mp3 = new File(output, name+".mp3");
        try
        {
            if (!mp3.exists())
                MIDItoMP3.convert(proj.getInput(), mp3);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            return false;
        }
        proj.getTune().setMusic(mp3.getName());
        proj.getTune().setArtist("Unknown artist");
        // stepfile
        File sm = new File(output, name+".sm");
        //File ssc = new File(output, name+".ssc");
        try
        {
            SMIOLogic.writeSM(proj.getTune(), sm);
            //SMIOLogic.writeSSC(proj.getTune(), ssc);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static void main(String[] argv)
    {
        try
        {
            //File indir = new File("d:\\temp\\data\\sm");
            //File outdir = new File("d:\\Program Files (x86)\\StepMania 5\\Songs\\generated");
            File indir = new File("d:\\temp\\data\\sm\\Beatles");
            File outdir = new File("d:\\Program Files (x86)\\StepMania 5\\Songs\\Beatles");
            outdir.mkdirs();
            File[] fin = indir.listFiles();
            for (int i = 0; i < fin.length; i++)
            {
                String name = fin[i].getName();
                if (!name.endsWith(".mid"))
                    continue;
                System.out.println("\n"+name);
                File fout = new File(outdir, name.substring(0, name.length() - 4));
                SMProject proj = ProjectLogic.newInstance();
                ProjectLogic.load(proj, fin[i]);
                ProjectLogic.dance(proj);
                proj.getTune().setTitle(fout.getName());
                ProjectLogic.save(proj, fout);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
