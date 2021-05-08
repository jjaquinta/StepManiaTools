package jo.sm.dle.data;

import java.io.File;
import java.util.Properties;

public class SongBean
{
    private Properties  mInSettings;
    private Properties  mOutSettings;
    private String      mName;
    private File        mMidiFile;
    private File        mOutDir;
    private File        mOutSettingsFile;
    
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
}
