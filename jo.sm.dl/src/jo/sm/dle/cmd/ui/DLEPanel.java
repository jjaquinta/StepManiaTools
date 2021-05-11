package jo.sm.dle.cmd.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.util.utils.PCSBeanUtils;

public class DLEPanel extends JPanel
{
    private SongBean    mSong;

    private ScorePanel  mScore;
    private TracksPanel mTracks;
    
    public DLEPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mScore = new ScorePanel();
        mTracks = new TracksPanel();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(mScore));
        add("West", mTracks);
    }

    private void initLink()
    {
        PCSBeanUtils.listen(RuntimeLogic.getInstance(), "selectedSong", (ov,nv) -> doNewSong((SongBean)nv));
    }
    
    private void doNewSong(SongBean song)
    {
        mSong = song;
        mScore.setSong(mSong);
        mTracks.setSong(mSong);
    }
}
