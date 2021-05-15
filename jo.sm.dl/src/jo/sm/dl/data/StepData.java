package jo.sm.dl.data;

import java.util.ArrayList;
import java.util.List;

public class StepData
{
    private JProperties  mRawProps = new JProperties();
    private List<String> mRawNotes = new ArrayList<>();
    private float       mBPM;
    private List<StepNotesData> mNotes = new ArrayList<>();
    
    public JProperties getRawProps()
    {
        return mRawProps;
    }
    public void setRawProps(JProperties rawProps)
    {
        mRawProps = rawProps;
    }
    public List<String> getRawNotes()
    {
        return mRawNotes;
    }
    public void setRawNotes(List<String> rawNotes)
    {
        mRawNotes = rawNotes;
    }
    public float getBPM()
    {
        return mBPM;
    }
    public void setBPM(float bPM)
    {
        mBPM = bPM;
    }

    public List<StepNotesData> getNotes()
    {
        return mNotes;
    }
    public void setNotes(List<StepNotesData> notes)
    {
        mNotes = notes;
    }

    public class StepNotesData
    {
        public String  mType;
        public String  mDifficulty;
        public int     mMeter;
        public int     mNotes;
        public int     mHolds;
        public int     mRolls;
        public int     mMines;
        public int     mNote4ths;
        public int     mNote8ths;
        public int     mNote12ths;
        public int     mNote16ths;
        public int     mNote24ths;
        public int     mNote32nds;
        public int     mNote48ths;
        public int     mNote64ths;
        public int     mNote192nds;
        public float   mBPM;
        public int     mMeasures;
        public float   mSongLength;
        public float   mNPM;
        public float   mDoublePC;
    }
}
