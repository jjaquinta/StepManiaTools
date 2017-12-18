package jo.sm.dl.logic;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import jo.audio.util.svc.mp3.MIDItoMP3;
import jo.audio.util.svc.mp3.MIDItoOGG;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.data.SMProject;
import jo.sm.dl.data.SMTune;

public class ProjectLogic
{
    public static SMProject newInstance(Properties props)
    {
        DifficultyLogic.init(props);
        SMProject proj = new SMProject();
        proj.setProps(props);
        proj.getDifficulties().put(SMProject.DIFF_BEGINNER, 1);
        proj.getDifficulties().put(SMProject.DIFF_EASY, 2);
        proj.getDifficulties().put(SMProject.DIFF_MEDIUM, 4);
        proj.getDifficulties().put(SMProject.DIFF_HARD, 8);
        proj.getDifficulties().put(SMProject.DIFF_CHALLENGE, 12);
        if (props.containsKey("ssc"))
            proj.getProps().setProperty(SMProject.SSC_OUT, "true");
        if (props.containsKey("beginner"))
            proj.getDifficulties().put(SMProject.DIFF_BEGINNER, Integer.parseInt(props.getProperty("beginner")));
        if (props.containsKey("easy"))
            proj.getDifficulties().put(SMProject.DIFF_EASY, Integer.parseInt(props.getProperty("easy")));
        if (props.containsKey("medium"))
            proj.getDifficulties().put(SMProject.DIFF_MEDIUM, Integer.parseInt(props.getProperty("medium")));
        if (props.containsKey("hard"))
            proj.getDifficulties().put(SMProject.DIFF_HARD, Integer.parseInt(props.getProperty("hard")));
        if (props.containsKey("challenge"))
            proj.getDifficulties().put(SMProject.DIFF_CHALLENGE, Integer.parseInt(props.getProperty("challenge")));
        
        if (!proj.isFlag(SMProject.SM_OUT) && !proj.isFlag(SMProject.SSC_OUT))
            proj.getProps().put(SMProject.SM_OUT, "true");
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
        if (proj.isFlag(SMProject.MP3_OUT))
            soundFile = new File(output, name+".mp3");
        else
            soundFile = new File(output, name+".ogg");
        try
        {
            if (!soundFile.exists())
                if (proj.isFlag(SMProject.MP3_OUT))
                    MIDItoMP3.convert(proj.getInput(), soundFile);
                else
                    MIDItoOGG.convert(proj.getInput(), soundFile);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            return false;
        }
        proj.getTune().setTitle(proj.getProps().getProperty("title", proj.getTune().getTitle()));
        proj.getTune().setMusic(soundFile.getName());
        proj.getTune().setArtist(proj.getProps().getProperty("artist", "Unknown artist"));
        proj.getTune().setCDTitle(proj.getProps().getProperty("cdtitle", ""));
        proj.getTune().setCredit(proj.getProps().getProperty("credit", ""));
        proj.getTune().setSubTitle(proj.getProps().getProperty("subtitle", ""));
        proj.getTune().setTitleTranslit(proj.getProps().getProperty("titletranslit", ""));
        proj.getTune().setSubTitleTranslit(proj.getProps().getProperty("subtitletranslit", ""));
        proj.getTune().setArtistTranslit(proj.getProps().getProperty("artisttranslit", ""));
        // stepfile
        try
        {
            File sm = new File(output, name+".sm");
            if (proj.isFlag(SMProject.SM_OUT))
                SMIOLogic.writeSM(proj.getTune(), sm);
            else
                sm.delete();
            File ssc = new File(output, name+".ssc");
            if (proj.isFlag(SMProject.SSC_OUT))
                SMIOLogic.writeSSC(proj.getTune(), ssc);
            else
                ssc.delete();
            if (proj.isFlag(SMProject.ENERGY_GRAPH))
            {
                File eg = new File(output, "energy_graph.png");
                MIDILogic.writeEnergyGraph(proj, 4, eg);
            }
            if (proj.isFlag(SMProject.NOTE_GRAPH))
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
}
