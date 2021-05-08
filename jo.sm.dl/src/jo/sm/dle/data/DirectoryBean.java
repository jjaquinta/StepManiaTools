package jo.sm.dle.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DirectoryBean
{
    private Properties     mInSettings;
    private Properties     mOutSettings;
    private String         mName;
    private File           mInDir;
    private File           mOutDir;
    private List<SongBean> mSongs = new ArrayList<>();

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

    public File getInDir()
    {
        return mInDir;
    }

    public void setInDir(File dir)
    {
        mInDir = dir;
    }

    public File getOutDir()
    {
        return mOutDir;
    }

    public void setOutDir(File outDir)
    {
        mOutDir = outDir;
    }

    public Properties getOutSettings()
    {
        return mOutSettings;
    }

    public void setOutSettings(Properties outSettings)
    {
        mOutSettings = outSettings;
    }

    public List<SongBean> getSongs()
    {
        return mSongs;
    }

    public void setSongs(List<SongBean> songs)
    {
        mSongs = songs;
    }
}
