package jo.sm.dle.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.util.utils.PCSBeanUtils;

public class DLEPanel extends JPanel
{
    private SongBean    mSong;

    private ClientPanel    mScore;
    private TracksPanel   mTracks;
    private PatternsPanel mPatterns;
    private NotePanel     mNote;
    private BeatPanel     mBeat;
    
    public DLEPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mScore = new ClientPanel();
        mTracks = new TracksPanel();
        mPatterns = new PatternsPanel();
        mNote = new NotePanel();
        mBeat = new BeatPanel();
    }

    private void initLayout()
    {
        JPanel west = new JPanel();
        west.setLayout(new GridLayout(4, 1));
        mTracks.setBorder(new TitledBorder("Track"));
        west.add(mTracks);
        mPatterns.setBorder(new TitledBorder("Pattern"));
        west.add(mPatterns);
        mNote.setBorder(new TitledBorder("Note"));
        west.add(mNote);
        mBeat.setBorder(new TitledBorder("Beat"));
        west.add(mBeat);
        
        setLayout(new BorderLayout());
        add("Center", mScore);
        add("West", west);
    }

    private void initLink()
    {
        PCSBeanUtils.listen(RuntimeLogic.getInstance(), "selectedSong", (ov,nv) -> doNewSong((SongBean)nv));
    }
    
    private void doNewSong(SongBean song)
    {
        if (mSong != null)
            PCSBeanUtils.unlisten(mSong, "selectedNotes,selectedBeats");
        mSong = song;
        mScore.setSong(mSong);
        mTracks.setSong(mSong);
        mPatterns.setSong(mSong);
        //invalidate();
        mScore.getParent().invalidate();
        if (mSong != null)
            PCSBeanUtils.listen(mSong, "selectedNotes,selectedBeats", (ov,nv) -> doNewNote());
    }
    
    private void doNewNote()
    {
        if ((mSong == null) || (mSong.getSelectedNotes().size() == 0))
            mNote.setNote(null);
        else
            mNote.setNote(mSong.getSelectedNotes().iterator().next());
        if ((mSong == null) || (mSong.getSelectedBeats().size() == 0))
            mBeat.setBeat(null);
        else
            mBeat.setBeat(mSong.getSelectedBeats().iterator().next());
    }
}
