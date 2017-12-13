package jo.sm.dl.data;

import java.util.ArrayList;
import java.util.List;

public class SMTune
{
    private String          mTitle;
    private String          mSubTitle;
    private String          mArtist;
    private String          mTitleTranslit;
    private String          mSubTitleTranslit;
    private String          mArtistTranslit;
    private String          mCredit;
    private String          mBanner;
    private String          mBackground;
    private String          mLyricsPath;
    private String          mCDTitle;
    private String          mMusic;
    private float           mOffset;
    private float           mSampleStart;
    private float           mSampleLength;
    private boolean         mSelectable = true;
    private float           mDisplayBPM;
    private List<SMMark>    mBPMs     = new ArrayList<>();
    private List<SMMark>    mStops    = new ArrayList<>();
    private List<SMMark>    mLabels   = new ArrayList<>();
    private List<SMMark>    mLyrics   = new ArrayList<>();
    // BGCHANGES
    // FGCHANGES
    // note data
    private String          mNotesChartType;
    private String          mNotesDescription;
    private String          mNotesDifficulty;
    private int             mNotesMeter;
    private float[]         mGrooveMeter = new float[5];
    private List<SMMeasure> mMeasures = new ArrayList<>();
    
    public String getTitle()
    {
        return mTitle;
    }
    public void setTitle(String title)
    {
        mTitle = title;
    }
    public String getSubTitle()
    {
        return mSubTitle;
    }
    public void setSubTitle(String subTitle)
    {
        mSubTitle = subTitle;
    }
    public String getArtist()
    {
        return mArtist;
    }
    public void setArtist(String artist)
    {
        mArtist = artist;
    }
    public String getTitleTranslit()
    {
        return mTitleTranslit;
    }
    public void setTitleTranslit(String titleTranslit)
    {
        mTitleTranslit = titleTranslit;
    }
    public String getSubTitleTranslit()
    {
        return mSubTitleTranslit;
    }
    public void setSubTitleTranslit(String subTitleTranslit)
    {
        mSubTitleTranslit = subTitleTranslit;
    }
    public String getArtistTranslit()
    {
        return mArtistTranslit;
    }
    public void setArtistTranslit(String artistTranslit)
    {
        mArtistTranslit = artistTranslit;
    }
    public String getCredit()
    {
        return mCredit;
    }
    public void setCredit(String credit)
    {
        mCredit = credit;
    }
    public String getBanner()
    {
        return mBanner;
    }
    public void setBanner(String banner)
    {
        mBanner = banner;
    }
    public String getBackground()
    {
        return mBackground;
    }
    public void setBackground(String background)
    {
        mBackground = background;
    }
    public String getLyricsPath()
    {
        return mLyricsPath;
    }
    public void setLyricsPath(String lyricsPath)
    {
        mLyricsPath = lyricsPath;
    }
    public String getCDTitle()
    {
        return mCDTitle;
    }
    public void setCDTitle(String cDTitle)
    {
        mCDTitle = cDTitle;
    }
    public String getMusic()
    {
        return mMusic;
    }
    public void setMusic(String music)
    {
        mMusic = music;
    }
    public float getOffset()
    {
        return mOffset;
    }
    public void setOffset(float offset)
    {
        mOffset = offset;
    }
    public float getSampleStart()
    {
        return mSampleStart;
    }
    public void setSampleStart(float sampleStart)
    {
        mSampleStart = sampleStart;
    }
    public float getSampleLength()
    {
        return mSampleLength;
    }
    public void setSampleLength(float sampleLength)
    {
        mSampleLength = sampleLength;
    }
    public boolean isSelectable()
    {
        return mSelectable;
    }
    public void setSelectable(boolean selectable)
    {
        mSelectable = selectable;
    }
    public float getDisplayBPM()
    {
        return mDisplayBPM;
    }
    public void setDisplayBPM(float displayBPM)
    {
        mDisplayBPM = displayBPM;
    }
    public List<SMMark> getBPMs()
    {
        return mBPMs;
    }
    public void setBPMs(List<SMMark> bPMs)
    {
        mBPMs = bPMs;
    }
    public List<SMMark> getStops()
    {
        return mStops;
    }
    public void setStops(List<SMMark> stops)
    {
        mStops = stops;
    }
    public String getNotesChartType()
    {
        return mNotesChartType;
    }
    public void setNotesChartType(String notesChartType)
    {
        mNotesChartType = notesChartType;
    }
    public String getNotesDescription()
    {
        return mNotesDescription;
    }
    public void setNotesDescription(String notesDescription)
    {
        mNotesDescription = notesDescription;
    }
    public String getNotesDifficulty()
    {
        return mNotesDifficulty;
    }
    public void setNotesDifficulty(String notesDifficulty)
    {
        mNotesDifficulty = notesDifficulty;
    }
    public int getNotesMeter()
    {
        return mNotesMeter;
    }
    public void setNotesMeter(int notesMeter)
    {
        mNotesMeter = notesMeter;
    }
    public float[] getGrooveMeter()
    {
        return mGrooveMeter;
    }
    public void setGrooveMeter(float[] grooveMeter)
    {
        mGrooveMeter = grooveMeter;
    }
    public List<SMMeasure> getMeasures()
    {
        return mMeasures;
    }
    public void setMeasures(List<SMMeasure> measures)
    {
        mMeasures = measures;
    }
    public List<SMMark> getLabels()
    {
        return mLabels;
    }
    public void setLabels(List<SMMark> labels)
    {
        mLabels = labels;
    }
    public List<SMMark> getLyrics()
    {
        return mLyrics;
    }
    public void setLyrics(List<SMMark> lyrics)
    {
        mLyrics = lyrics;
    }
}
