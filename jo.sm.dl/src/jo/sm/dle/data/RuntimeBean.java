package jo.sm.dle.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jo.util.beans.PCSBean;

public class RuntimeBean extends PCSBean
{
    private String[]            mArgs;
    private File                mBaseDir;
    private Properties          mSettings;
    private List<DirectoryBean> mDirectories = new ArrayList<>();
    
    private DirectoryBean       mSelectedDirectory;
    private SongBean            mSelectedSong;

    public String[] getArgs()
    {
        return mArgs;
    }

    public void setArgs(String[] args)
    {
        mArgs = args;
    }

    public File getBaseDir()
    {
        return mBaseDir;
    }

    public void setBaseDir(File baseDir)
    {
        mBaseDir = baseDir;
    }

    public Properties getSettings()
    {
        return mSettings;
    }

    public void setSettings(Properties settings)
    {
        mSettings = settings;
    }

    public List<DirectoryBean> getDirectories()
    {
        return mDirectories;
    }

    public void setDirectories(List<DirectoryBean> directories)
    {
        mDirectories = directories;
    }

    public DirectoryBean getSelectedDirectory()
    {
        return mSelectedDirectory;
    }

    public void setSelectedDirectory(DirectoryBean selectedDirectory)
    {
        queuePropertyChange("selectedDirectory", mSelectedDirectory, selectedDirectory);
        mSelectedDirectory = selectedDirectory;
        firePropertyChange();
    }

    public SongBean getSelectedSong()
    {
        return mSelectedSong;
    }

    public void setSelectedSong(SongBean selectedSong)
    {
        queuePropertyChange("selectedSong", mSelectedSong, selectedSong
                );
        mSelectedSong = selectedSong;
        firePropertyChange();
    }

}
