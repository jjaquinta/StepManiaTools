package jo.sm.dl.logic;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

import jo.audio.util.svc.mp3.MIDItoMP3;
import jo.audio.util.svc.mp3.MIDItoOGG;
import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.data.SMProject;
import jo.sm.dl.data.SMTune;
import jo.util.utils.io.FileUtils;

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
        float[] sample = findSample(proj);
        proj.getTune().setSampleStart(sample[0]);
        proj.getTune().setSampleLength(sample[1]);
        // stepfile
        try
        {
            if (proj.isFlag("drawBanner"))
                makeBanner(proj, output, name);
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
            if (proj.getProps().containsKey("background"))
            {
                String bg = proj.getProps().getProperty("background");
                proj.getTune().setBackground(bg);
                FileUtils.copy(new File(proj.getInput().getParentFile(), bg), new File(output, bg));
            }
            else if (proj.isFlag(SMProject.ENERGY_GRAPH))
            {
                File eg = new File(output, "energy_graph.png");
                MIDILogic.writeEnergyGraph(proj, 4, eg);
                proj.getTune().setBackground(eg.getName());
            }
            else if (proj.isFlag(SMProject.NOTE_GRAPH))
            {
                File ng = new File(output, "note_graph.png");
                MIDILogic.writeNoteGraph(proj, 4, ng);
                proj.getTune().setBackground(ng.getName());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    private static float[] findSample(SMProject proj)
    {
        float[] best = null;
        long bestv = 0L;
        for (float start = 0; start < proj.getMIDI().getLengthInSeconds(); start += 5)
        {
            long tickStart = proj.getMIDI().minutesToTick(start/60);
            long tickEnd = proj.getMIDI().minutesToTick((start+15)/60);
            long loud = 0L;
            for (MIDINote note : proj.getMIDI().getNotes())
                if (note.getTick() < tickStart)
                    continue;
                else if (note.getTick() < tickEnd)
                    loud += note.getLoud();
                else
                    break;
            if ((loud > bestv) || (best == null))
                best = new float[] { start, 15 };
        }
        return best;
    }

    private static final Color[] METALS = { Color.WHITE, Color.YELLOW };
    private static final Color[] COLORS = { Color.RED, Color.GREEN, Color.BLUE, Color.BLACK, new Color(128,0,128)};

    private static void makeBanner(SMProject proj, File output, String name) throws IOException
    {
        File bannerName = new File(output, name+"_banner.png");
        proj.getTune().setBanner(bannerName.getName());
        BufferedImage img = new BufferedImage(512, 160, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        Color fg;
        Color bg;
        if (DanceLogic.RND.nextBoolean())
        {
            fg = METALS[DanceLogic.RND.nextInt(METALS.length)];
            bg = COLORS[DanceLogic.RND.nextInt(COLORS.length)];
        }
        else
        {
            bg = METALS[DanceLogic.RND.nextInt(METALS.length)];
            fg = COLORS[DanceLogic.RND.nextInt(COLORS.length)];
        }
        g.setColor(bg);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        Font f = g.getFont();
        int w = 0;
        FontMetrics fm = null;
        for (float s = 72; s > 10; s--)
        {
            f = f.deriveFont(s);
            fm = g.getFontMetrics(f);
            w = fm.stringWidth(proj.getTune().getTitle());
            if (w < img.getWidth()*8/10)
                break;
        }
        int fx = (img.getWidth() - w)/2;
        int fy = (img.getHeight() - fm.getAscent())/2 + fm.getAscent();
        g.setFont(f);
        g.setColor(Color.gray);
        g.drawString(proj.getTune().getTitle(), fx+2, fy+2);
        g.setColor(fg);
        g.drawString(proj.getTune().getTitle(), fx, fy);
        g.dispose();
        ImageIO.write(img, "PNG", bannerName);
    }
}
