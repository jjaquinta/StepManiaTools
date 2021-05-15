package jo.sm.dle.cmd.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import jo.sm.dl.data.MIDITrack;
import jo.sm.dle.cmd.ui.score.ScoreCanvas;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.util.utils.PCSBeanUtils;

public class ClientPanel extends JComponent
{
    private SongBean    mSong;
 
    private ScoreCanvas mCanvas;
    
    public ClientPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mCanvas = new ScoreCanvas();
        updateZoomSize();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mCanvas);
    }

    private void initLink()
    {
        RuntimeLogic.listen("zoomSize", (ov,nv)->updateZoomSize());
    }

    public void updateZoomSize()
    {
        mCanvas.setNoteHeight(RuntimeLogic.getInstance().getZoomSize());
        if (getParent() != null)
            getParent().invalidate();
    }

    public void updateTracks()
    {
        List<MIDITrack> tracks = new ArrayList<>();
        Integer[] trackNums = mSong.getTracks().toArray(new Integer[0]);
        Arrays.sort(trackNums);
        for (Integer i : trackNums)
            tracks.add(mSong.getProject().getMIDI().getTrackInfos().get(i));
        mCanvas.setTracks(tracks);
        getParent().invalidate();
    }
    

    public SongBean getSong()
    {
        return mSong;
    }

    public void setSong(SongBean song)
    {
        if (mSong != null)
            PCSBeanUtils.unlisten(mSong, "tracks");
        mSong = song;
        mCanvas.setTune(mSong.getProject().getMIDI());
        repaint();
        PCSBeanUtils.listen(mSong, "tracks", (ov,nv)->updateTracks());
        updateTracks();
    }
}
