package jo.sm.dle.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import jo.sm.dl.data.PlayEvent;
import jo.sm.dl.data.midi.MIDINote;
import jo.sm.dl.data.midi.MIDITrack;
import jo.sm.dl.logic.PlayLogic;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.sm.dle.logic.SelectionLogic;
import jo.sm.dle.logic.SongLogic;
import jo.sm.dle.ui.score.ArrowPanel;
import jo.sm.dle.ui.score.ScoreCanvas;
import jo.util.ui.swing.utils.MouseUtils;
import jo.util.utils.PCSBeanUtils;

public class ClientPanel extends JComponent
{
    private SongBean    mSong;
 
    private JScrollPane mScroller;
    private ScoreCanvas mCanvas;
    private ArrowPanel mArrows;
    
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
        mArrows = new ArrowPanel();
        mArrows.setScroller(mScroller.getHorizontalScrollBar());
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mScroller);
        add("South", mArrows);
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
        updateArrows();
        mScroller.invalidate();
    }

    public void updateTracks()
    {
        List<MIDITrack> tracks = mSong.getTracks();
        mCanvas.getData().setSelected(mSong.getSelectedNotes());
        mCanvas.getData().setHighlights(mSong.getNoteHighlights());
        mCanvas.setTracks(tracks);
        updateArrows();
        if (mSong.getSelectedNotes().size() > 0)
            mCanvas.setCaretTo(mSong.getSelectedNotes().iterator().next());
        updateScroller();
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
            List<MIDINote> notes = new ArrayList<>();
            for (Object feature : features)
                if (feature instanceof MIDINote)
                {
                    notes.add((MIDINote)feature);
                }
                else if (feature instanceof MIDITrack)
                {
                    SongLogic.selectTrack((MIDITrack)feature);
                }
            if (notes.size() > 0)
                if (ev.isControlDown())
                    SelectionLogic.toggleSelection(notes.toArray(new MIDINote[0]));
                else
                    SelectionLogic.setSelection(notes.toArray(new MIDINote[0]));
        }
    }
    
    private void doPlay(PlayEvent ev)
    {
        if (ev.getAction() == PlayEvent.ON)
        {
            mCanvas.setCaretTo(ev.getNote());
            updateScroller();
            //SongLogic.addToHighlights(ev.getNote());
        }
        else if (ev.getAction() == PlayEvent.OFF)
            ;//SongLogic.removeFromHighlights(ev.getNote());
        else if ((ev.getAction() == PlayEvent.START) || (ev.getAction() == PlayEvent.STOP))
            ;//SongLogic.clearHighlights();
    }

    public SongBean getSong()
    {
        return mSong;
    }

    public void setSong(SongBean song)
    {
        if (mSong != null)
            PCSBeanUtils.unlisten(mSong, "tracks,selectedChart,selectedNotes,noteHighlights");
        mSong = song;
        mCanvas.setTune(mSong.getProject().getMIDI());
        updateArrows();
        repaint();
        PCSBeanUtils.listen(mSong, "tracks,selectedChart,selectedNotes,noteHighlights", (ov,nv)->updateTracks());
        updateTracks();
    }

    public void updateArrows()
    {
        mArrows.makeArrowImage(mCanvas.getData());
        mArrows.repaint();
    }
}
