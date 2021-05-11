package jo.sm.dle.logic;

import java.io.File;
import java.util.Properties;

import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.SMProject;
import jo.sm.dl.logic.NotationLogic;
import jo.sm.dl.logic.ProjectLogic;
import jo.sm.dle.data.DirectoryBean;
import jo.sm.dle.data.SongBean;
import jo.util.utils.obj.BooleanUtils;

public class SongLogic
{

    public static void addSong(DirectoryBean dir, File fin)
    {
        String name = fin.getName();
        if (!name.endsWith(".mid"))
            return;
        if (dir.getInSettings().containsKey("filter") && (name.indexOf(dir.getInSettings().getProperty("filter")) < 0))
            return;
        SongBean song = new SongBean();
        song.setMidiFile(fin);
        song.setName(name);
        Properties songProps = RuntimeLogic.newProperties(dir.getOutSettings());
        song.setInSettings(songProps);
        songProps.setProperty("title", name.substring(0, name.length() - 4));
        File s1Props = new File(dir.getInDir(), name.substring(0, name.length() - 4)+".properties");
        if (!RuntimeLogic.readProperties(songProps, s1Props))
            return;
        if (BooleanUtils.parseBoolean(songProps.get("skip")))
            return;
        song.setOutSettings(RuntimeLogic.newProperties(song.getInSettings()));
        File fout = new File(dir.getOutDir(), name.substring(0, name.length() - 4));
        song.setOutDir(fout);
        File s2Props = new File(fout, "dl.properties");
        song.setOutSettingsFile(s2Props);
        if (!RuntimeLogic.readProperties(song.getOutSettings(), s2Props))
            return;
        dir.getSongs().add(song);
    }
    
    public static void load(SongBean song)
    {
        SMProject proj = ProjectLogic.newInstance(song.getInSettings());
        ProjectLogic.load(proj, song.getMidiFile());
        ProjectLogic.dance(proj);
        song.setProject(proj);
        proj.getMIDI().setNotation(NotationLogic.makeNotation(proj.getMIDI()));
        if (song.getTracks().size() == 0)
            for (int i = proj.getMIDI().getTracks() - 1; i >= 0; i--)
                song.getTracks().add(i);
        song.setSelectedChart(null);
    }

    public static void toggleTrack(int track)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song.getTracks().contains(track))
            song.getTracks().remove(track);
        else
            song.getTracks().add(track);
        song.fireMonotonicPropertyChange("tracks");
    }

    public static void selectChart(String chart)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        song.setSelectedChart(chart);
        song.fireMonotonicPropertyChange("selectedChart");
    }

    public static void selectTrack(MIDITrack track)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        song.setSelectedTrack(track);
    }
}
