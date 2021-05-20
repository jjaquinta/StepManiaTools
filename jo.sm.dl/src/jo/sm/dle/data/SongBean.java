package jo.sm.dle.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.sm.dl.data.JProperties;
import jo.sm.dl.data.midi.MIDINote;
import jo.sm.dl.data.midi.MIDITrack;
import jo.sm.dl.data.sm.SMBeat;
import jo.sm.dl.data.sm.SMProject;
import jo.sm.dl.data.sm.pat.PatDef;
import jo.util.beans.PCSBean;

public class SongBean extends PCSBean
{
    private DirectoryBean mDir;
    private JProperties    mInSettings;
    private JProperties    mOutSettings;
    private String        mName;
    private File          mMidiFile;
    private File          mOutDir;
    private File          mInSettingsFile;
    private File          mOutSettingsFile;
    private SMProject     mProject;
    private String        mSelectedChart;
    private MIDITrack     mSelectedTrack;
    private PatDef        mSelectedPattern;
    private Set<MIDINote> mSelectedNotes = new HashSet<>();
    private Set<MIDINote> mNotesHighlights = new HashSet<>();
    private Set<SMBeat>   mSelectedBeats = new HashSet<>();
    
    public SongBean()
    {        
    }

    // utilities
    
    public List<MIDITrack> getTracks()
    {
        List<MIDITrack> tracks = new ArrayList<>();
        for (MIDITrack track : getProject().getMIDI().getTrackInfos())
            if (track.getType() != MIDITrack.IGNORE)
                tracks.add(track);
        return tracks;
    }

    // getters and setters

    public JProperties getInSettings()
    {
        return mInSettings;
    }

    public void setInSettings(JProperties settings)
    {
        mInSettings = settings;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public JProperties getOutSettings()
    {
        return mOutSettings;
    }

    public void setOutSettings(JProperties outSettings)
    {
        mOutSettings = outSettings;
    }

    public File getMidiFile()
    {
        return mMidiFile;
    }

    public void setMidiFile(File midiFile)
    {
        mMidiFile = midiFile;
    }

    public File getOutDir()
    {
        return mOutDir;
    }

    public void setOutDir(File outDir)
    {
        mOutDir = outDir;
    }

    public File getOutSettingsFile()
    {
        return mOutSettingsFile;
    }

    public void setOutSettingsFile(File outSettingsFile)
    {
        mOutSettingsFile = outSettingsFile;
    }

    public SMProject getProject()
    {
        return mProject;
    }

    public void setProject(SMProject project)
    {
        mProject = project;
    }

    public String getSelectedChart()
    {
        return mSelectedChart;
    }

    public void setSelectedChart(String selectedChart)
    {
        queuePropertyChange("selectedChart", mSelectedChart, selectedChart);
        mSelectedChart = selectedChart;
        firePropertyChange();
    }

    public MIDITrack getSelectedTrack()
    {
        return mSelectedTrack;
    }

    public void setSelectedTrack(MIDITrack selectedTrack)
    {
        queuePropertyChange("selectedTrack", mSelectedTrack, selectedTrack);
        mSelectedTrack = selectedTrack;
        firePropertyChange();
    }

    public File getInSettingsFile()
    {
        return mInSettingsFile;
    }

    public void setInSettingsFile(File inSettingsFile)
    {
        mInSettingsFile = inSettingsFile;
    }

    public DirectoryBean getDir()
    {
        return mDir;
    }

    public void setDir(DirectoryBean dir)
    {
        mDir = dir;
    }

    public PatDef getSelectedPattern()
    {
        return mSelectedPattern;
    }

    public void setSelectedPattern(PatDef selectedPattern)
    {
        mSelectedPattern = selectedPattern;
    }

    public Set<MIDINote> getSelectedNotes()
    {
        return mSelectedNotes;
    }

    public void setSelectedNotes(Set<MIDINote> selected)
    {
        queuePropertyChange("selectedNotes", mSelectedNotes, selected);
        mSelectedNotes = selected;
        firePropertyChange();
    }

    public Set<MIDINote> getNoteHighlights()
    {
        return mNotesHighlights;
    }

    public void setNoteHighlights(Set<MIDINote> highlights)
    {
        queuePropertyChange("noteHighlights", mNotesHighlights, highlights);
        mNotesHighlights = highlights;
        firePropertyChange();
    }

    public Set<SMBeat> getSelectedBeats()
    {
        return mSelectedBeats;
    }

    public void setSelectedBeats(Set<SMBeat> selectedBeats)
    {
        queuePropertyChange("selectedBeats", mSelectedBeats, selectedBeats);
        mSelectedBeats = selectedBeats;
        firePropertyChange();
    }
}
