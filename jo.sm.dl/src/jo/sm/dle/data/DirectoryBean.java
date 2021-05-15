package jo.sm.dle.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jo.sm.dl.data.JProperties;

public class DirectoryBean
{
    private JProperties     mInSettings;
    private JProperties     mOutSettings;
    private String         mName;
    private File           mInDir;
    private File           mOutDir;
    private List<SongBean> mSongs = new ArrayList<>();

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

    public JProperties getOutSettings()
    {
        return mOutSettings;
    }

    public void setOutSettings(JProperties outSettings)
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
