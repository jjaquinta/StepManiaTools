package jo.sm.dle.cmd.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.util.utils.PCSBeanUtils;

public class DLEPanel extends JPanel
{
    private SongBean    mSong;

    private ScoreCanvas    mScore;
    private TracksPanel   mTracks;
    private PatternsPanel mPatterns;
    
    public DLEPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mScore = new ScoreCanvas();
        mTracks = new TracksPanel();
        mPatterns = new PatternsPanel();
    }

    private void initLayout()
    {
        JPanel west = new JPanel();
        west.setLayout(new GridLayout(2, 1));
        west.add(mTracks);
        west.add(mPatterns);
        
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(mScore));
        add("West", west);
    }

    private void initLink()
    {
        PCSBeanUtils.listen(RuntimeLogic.getInstance(), "selectedSong", (ov,nv) -> doNewSong((SongBean)nv));
    }
    
    private void doNewSong(SongBean song)
    {
        mSong = song;
        mScore.setTune(mSong.getProject().getMIDI());
        mTracks.setSong(mSong);
        mPatterns.setSong(mSong);
        //invalidate();
        mScore.getParent().invalidate();
    }
}
