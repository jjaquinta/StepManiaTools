package jo.sm.dle.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import jo.sm.dl.data.JProperties;
import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.PatDef;
import jo.sm.dl.data.SMBeat;
import jo.sm.dl.data.SMChart;
import jo.sm.dl.data.SMProject;
import jo.sm.dl.logic.NotationLogic;
import jo.sm.dl.logic.PlayLogic;
import jo.sm.dl.logic.ProjectLogic;
import jo.sm.dle.data.DirectoryBean;
import jo.sm.dle.data.SongBean;
import jo.util.utils.MapUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

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
        song.setDir(dir);
        song.setMidiFile(fin);
        song.setName(name);
        JProperties songProps = RuntimeLogic.newProperties(dir.getOutSettings());
        song.setInSettings(songProps);
        songProps.setProperty("title", name.substring(0, name.length() - 4));
        File s1Props = new File(dir.getInDir(), name.substring(0, name.length() - 4)+".properties");
        if (!RuntimeLogic.readProperties(songProps, s1Props))
            return;
        if (BooleanUtils.parseBoolean(songProps.get("skip")))
            return;
        song.setInSettingsFile(s1Props);
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
        {
            if (!propToSet(song, "activeTracks", song.getTracks()))
            {
                for (MIDITrack t : proj.getMIDI().getTrackInfos())
                    if (t.getNotes().size() > 0)
                        song.getTracks().add(t.getTrack());
            }
            propToSet(song, "melodyTracks", song.getMelodyTracks());
        }
        song.setSelectedChart(null);
    }
    
    private static boolean propToSet(SongBean song, String propName, Set<Integer> tracks)
    {
        tracks.clear();
        if (!song.getInSettings().containsKey(propName))
            return false;
        for (StringTokenizer st = new StringTokenizer(song.getInSettings().getProperty(propName), ","); st.hasMoreTokens(); )
            tracks.add(Integer.parseInt(st.nextToken()));
        return true;
    }
    
    public static void recalc()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song != null)
            load(song);
    }
    
    public static void save()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        song.getInSettings().put("activeTracks", StringUtils.fromStringArray(song.getTracks().toArray(), ","));
        song.getInSettings().put("melodyTracks", StringUtils.fromStringArray(song.getMelodyTracks().toArray(), ","));
        MapUtils.save(song.getInSettings(), song.getInSettingsFile());
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
    
    public static void setTrack(int track, boolean set)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (!set)
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

    public static void setMelody(int track, boolean set)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (set)
            song.getMelodyTracks().add(track);
        else
            song.getMelodyTracks().remove(track);
        song.fireMonotonicPropertyChange("melodyTracks");
    }

    public static void selectPattern(PatDef pattern)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        song.setSelectedPattern(pattern);
    }
    
    public static void doPlayAll()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        List<MIDINote> notes = new ArrayList<>();
        for (Integer t : song.getTracks())
            notes.addAll(song.getProject().getMIDI().getTrackInfos().get(t).getNotes());
        PlayLogic.play(song.getProject().getMIDI().getMSPerTick(), notes);
    }
    
    public static void doPlayTrack()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        if (song.getSelectedTrack() == null)
            return;
        List<MIDINote> notes = new ArrayList<>();
        notes.addAll(song.getSelectedTrack().getNotes());
        PlayLogic.play(song.getProject().getMIDI().getMSPerTick(), notes);
    }
    
    public static void doPlayFromCaret()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        List<MIDINote> notes = new ArrayList<>();
        PlayLogic.play(song.getProject().getMIDI().getMSPerTick(), notes);
    }

    public static void nextBeat()
    {
        SMBeat next = getNextBeat();
        if (next != null)
        {
            SMBeat beat = RuntimeLogic.getInstance().getSelectedSong().getSelectedBeats().iterator().next();
            SelectionLogic.toggleBeats(beat, next);
            SelectionLogic.toggleSelection(beat.getNote(), next.getNote());
        }
    }

    public static SMBeat getNextBeat()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return null;
        if (song.getSelectedBeats().size() == 0)
            return null;
        SMBeat beat = song.getSelectedBeats().iterator().next();
        for (SMChart chart : song.getProject().getTune().getCharts())
        {
            int idx = chart.getAllBeats().indexOf(beat);
            if (idx >= 0)
                if (idx + 1 < chart.getAllBeats().size())
                    return chart.getAllBeats().get(idx + 1);
                else
                    return null;
        }
        return null;
    }

    public static void previousBeat()
    {
        SMBeat prev = getPrevBeat();
        if (prev != null)
        {
            SMBeat beat = RuntimeLogic.getInstance().getSelectedSong().getSelectedBeats().iterator().next();
            SelectionLogic.toggleBeats(beat, prev);
            SelectionLogic.toggleSelection(beat.getNote(), prev.getNote());
        }
    }

    public static SMBeat getPrevBeat()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return null;
        if (song.getSelectedBeats().size() == 0)
            return null;
        SMBeat beat = song.getSelectedBeats().iterator().next();
        for (SMChart chart : song.getProject().getTune().getCharts())
        {
            int idx = chart.getAllBeats().indexOf(beat);
            if (idx >= 0)
            {
                if (idx > 0)
                    return chart.getAllBeats().get(idx - 1);
                else
                    return null;
            }
        }
        return null;
    }
    
}
