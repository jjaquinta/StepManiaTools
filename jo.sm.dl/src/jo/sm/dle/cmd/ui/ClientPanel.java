package jo.sm.dle.cmd.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.PlayEvent;
import jo.sm.dl.logic.PlayLogic;
import jo.sm.dle.cmd.ui.score.ScoreCanvas;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.util.ui.swing.utils.MouseUtils;
import jo.util.utils.PCSBeanUtils;

public class ClientPanel extends JComponent
{
    private SongBean    mSong;
 
    private JScrollPane mScroller;
    private ScoreCanvas mCanvas;
    
    public ClientPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
        updateZoomSize();
    }

    private void initInstantiate()
    {
        mCanvas = new ScoreCanvas();
        mScroller = new JScrollPane(mCanvas);
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mScroller);
    }

    private void initLink()
    {
        RuntimeLogic.listen("zoomSize", (ov,nv)->updateZoomSize());
        MouseUtils.mousePressed(mCanvas, (ev) -> doMousePressed(ev));
        PlayLogic.listen((ev) -> doPlay(ev));
    }

    public void updateZoomSize()
    {
        mCanvas.setNoteHeight(RuntimeLogic.getInstance().getZoomSize());
        mScroller.invalidate();
    }

    public void updateTracks()
    {
        List<MIDITrack> tracks = new ArrayList<>();
        Integer[] trackNums = mSong.getTracks().toArray(new Integer[0]);
        Arrays.sort(trackNums);
        for (Integer i : trackNums)
            tracks.add(mSong.getProject().getMIDI().getTrackInfos().get(i));
        mCanvas.setTracks(tracks);
        mScroller.invalidate();
    }
    
    private void updateScroller()
    {
        int caret = mCanvas.getCaret();
        Dimension size = mScroller.getViewport().getExtentSize();
        int at = mScroller.getHorizontalScrollBar().getValue();
        if ((caret > at) && (caret < at + size.width))
            return;
        mScroller.getHorizontalScrollBar().setValue(caret - size.width/2);
    }
    
    private void doMousePressed(MouseEvent ev)
    {
        if (ev.getButton() == MouseEvent.BUTTON1)
        {
            List<Object> features = mCanvas.getFeatureAt(ev.getX(), ev.getY());
            for (Object feature : features)
                if (feature instanceof MIDINote)
                {
                    mCanvas.setCaretTo(feature);
                    updateScroller();
                }
        }
    }
    
    private void doPlay(PlayEvent ev)
    {
        if (ev.getAction() == PlayEvent.ON)
        {
            mCanvas.setCaretTo(ev.getNote());
            updateScroller();
        }
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
