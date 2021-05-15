package jo.sm.dle.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jo.sm.dl.data.JProperties;
import jo.util.beans.PCSBean;
import jo.util.utils.obj.IntegerUtils;

public class RuntimeBean extends PCSBean
{
    private String[]            mArgs;
    private File                mBaseDir;
    private File                mPropsFile;
    private JProperties          mSettings;
    private List<DirectoryBean> mDirectories = new ArrayList<>();
    
    private DirectoryBean       mSelectedDirectory;
    private SongBean            mSelectedSong;
    private String              mError;

    // pseudo getters and setters
    public void setZoomSize(int zoom)
    {
        mSettings.put("zoomSize", String.valueOf(zoom));
        fireMonotonicPropertyChange("zoomSize");
    }
    
    public int getZoomSize()
    {
        return IntegerUtils.parseInt(mSettings.getProperty("zoomSize", "48"));
    }
    
    // getters and setters
    
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

    public JProperties getSettings()
    {
        return mSettings;
    }

    public void setSettings(JProperties settings)
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

    public String getError()
    {
        return mError;
    }

    public void setError(String error)
    {
        queuePropertyChange("error", mError, error);
        mError = error;
        firePropertyChange();
    }

    public File getPropsFile()
    {
        return mPropsFile;
    }

    public void setPropsFile(File propsFile)
    {
        mPropsFile = propsFile;
    }

}
