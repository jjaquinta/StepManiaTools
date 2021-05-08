package jo.sm.dle.cmd.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.util.utils.PCSBeanUtils;

public class DLEPanel extends JPanel
{
    private SongBean    mSong;

    private ScorePanel  mScore;
    
    public DLEPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mScore = new ScorePanel();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mScore);
    }

    private void initLink()
    {
        PCSBeanUtils.listen(RuntimeLogic.getInstance(), "selectedSong", (ov,nv) -> doNewSong((SongBean)nv));
    }
    
    private void doNewSong(SongBean song)
    {
        mSong = song;
        mScore.setSong(mSong);
    }
}
