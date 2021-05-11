package jo.sm.dle.data;

import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.SMProject;
import jo.util.beans.PCSBean;

public class SongBean extends PCSBean
{
    private Properties   mInSettings;
    private Properties   mOutSettings;
    private String       mName;
    private File         mMidiFile;
    private File         mOutDir;
    private File         mOutSettingsFile;
    private SMProject    mProject;
    private Set<Integer> mTracks = new HashSet<>();
    private String       mSelectedChart;
    private MIDITrack    mSelectedTrack;

    public Properties getInSettings()
    {
        return mInSettings;
    }

    public void setInSettings(Properties settings)
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

    public Properties getOutSettings()
    {
        return mOutSettings;
    }

    public void setOutSettings(Properties outSettings)
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

    public Set<Integer> getTracks()
    {
        return mTracks;
    }

    public void setTracks(Set<Integer> tracks)
    {
        queuePropertyChange("tracks", mTracks, tracks);
        mTracks = tracks;
        firePropertyChange();
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
}
