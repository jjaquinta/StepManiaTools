package jo.sm.dl.logic;

import java.io.File;
import java.io.IOException;

import jo.audio.util.svc.mp3.MIDItoMP3;
import jo.audio.util.svc.mp3.MIDItoOGG;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.data.SMProject;
import jo.sm.dl.data.SMTune;

public class ProjectLogic
{
    public static SMProject newInstance(String[] argv)
    {
        SMProject proj = new SMProject();
        for (int i = 0; i < argv.length; i++)
            if ("--markPatterns".equalsIgnoreCase(argv[i]))
                proj.getFlags().add(SMProject.MARK_PATTERNS);
            else if ("--energyGraph".equalsIgnoreCase(argv[i]))
                proj.getFlags().add(SMProject.ENERGY_GRAPH);
            else if ("--noteGraph".equalsIgnoreCase(argv[i]))
                proj.getFlags().add(SMProject.NOTE_GRAPH);
            else if ("--ogg".equalsIgnoreCase(argv[i]))
                proj.getFlags().add(SMProject.OGG_OUT);
            else if ("--mp3".equalsIgnoreCase(argv[i]))
                proj.getFlags().add(SMProject.MP3_OUT);
        return proj;
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
        File soundFile;
        if (proj.getFlags().contains(SMProject.MP3_OUT))
            soundFile = new File(output, name+".mp3");
        else
            soundFile = new File(output, name+".ogg");
        try
        {
            if (!soundFile.exists())
                if (proj.getFlags().contains(SMProject.MP3_OUT))
                    MIDItoMP3.convert(proj.getInput(), soundFile);
                else
                    MIDItoOGG.convert(proj.getInput(), soundFile);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            return false;
        }
        proj.getTune().setMusic(soundFile.getName());
        proj.getTune().setArtist("Unknown artist");
        // stepfile
        File sm = new File(output, name+".sm");
        File ssc = new File(output, name+".ssc");
        try
        {
            SMIOLogic.writeSM(proj.getTune(), sm);
            SMIOLogic.writeSSC(proj.getTune(), ssc);
            if (proj.getFlags().contains(SMProject.ENERGY_GRAPH))
            {
                File eg = new File(output, "energy_graph.png");
                MIDILogic.writeEnergyGraph(proj, 4, eg);
            }
            if (proj.getFlags().contains(SMProject.NOTE_GRAPH))
            {
                File ng = new File(output, "note_graph.png");
                MIDILogic.writeNoteGraph(proj, 4, ng);
            }
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
            String doOnly = "All My Lov";//null;//
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
                if ((doOnly != null) && (name.indexOf(doOnly) < 0))
                    continue;
                System.out.println("\n"+name);
                File fout = new File(outdir, name.substring(0, name.length() - 4));
                SMProject proj = ProjectLogic.newInstance(argv);
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
