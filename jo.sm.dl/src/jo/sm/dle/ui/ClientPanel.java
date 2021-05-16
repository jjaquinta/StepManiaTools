package jo.sm.dle.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.PlayEvent;
import jo.sm.dl.data.SMBeat;
import jo.sm.dl.data.SMChart;
import jo.sm.dl.data.SMMeasure;
import jo.sm.dl.data.ScoreDrawData;
import jo.sm.dl.logic.PlayLogic;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.sm.dle.logic.SongLogic;
import jo.sm.dle.ui.ctrl.JoImageScroller;
import jo.sm.dle.ui.score.ScoreCanvas;
import jo.util.ui.swing.logic.FontUtils;
import jo.util.ui.swing.utils.MouseUtils;
import jo.util.utils.PCSBeanUtils;

public class ClientPanel extends JComponent
{
    private SongBean    mSong;
 
    private JScrollPane mScroller;
    private ScoreCanvas mCanvas;
    private JoImageScroller mArrows;
    
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
        mArrows = new JoImageScroller();
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
        List<MIDITrack> tracks = new ArrayList<>();
        Integer[] trackNums = mSong.getTracks().toArray(new Integer[0]);
        Arrays.sort(trackNums);
        for (Integer i : trackNums)
            tracks.add(mSong.getProject().getMIDI().getTrackInfos().get(i));
        mCanvas.setTracks(tracks);
        updateArrows();
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
                else if (feature instanceof MIDITrack)
                {
                    SongLogic.selectTrack((MIDITrack)feature);
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
            PCSBeanUtils.unlisten(mSong, "tracks,selectedChart");
        mSong = song;
        mCanvas.setTune(mSong.getProject().getMIDI());
        updateArrows();
        repaint();
        PCSBeanUtils.listen(mSong, "tracks,selectedChart", (ov,nv)->updateTracks());
        updateTracks();
    }

    public void updateArrows()
    {
        BufferedImage img = makeArrowImage();
        if (img != null)
            mArrows.setImage(img);
    }

    private static final String ARROWS = "\u25c0\u1405\u1403\u1401";
    
    private BufferedImage makeArrowImage()
    {
        if (mSong == null)
            return null;
        ScoreDrawData data = mCanvas.getData();
        if (data == null)
            return null;
        Rectangle tune = data.getFeaturePosition(mSong.getProject().getMIDI());
        BufferedImage img = new BufferedImage(data.getImage().getWidth(), 32, BufferedImage.TYPE_INT_ARGB);
        SMChart chart = mSong.getProject().getTune().getChart(mSong.getSelectedChart());
        if (chart == null)
            return img;
        Graphics g = img.getGraphics();
        g.setColor(Color.ORANGE);
        g.setFont(FontUtils.getFont("Dialog", 8, Font.BOLD));
        int ppq = mSong.getProject().getMIDI().getPulsesPerQuarter();
        for (SMMeasure measure : chart.getMeasures())
        {
            for (SMBeat beat : measure.getBeats())
            {
                if (!beat.isAnySteps())
                    continue;
                double m = beat.getTick()/4.0/ppq;
                int x = tune.x + (int)(m*data.getMeasureWidth());
                char[] notes = beat.getNotes();
                for (int i = 0; i < notes.length; i++)
                    if (notes[i] != '0')
                        if (i == 0)
                            g.fillPolygon(new int[] { x, x + 8, x + 8 }, new int[] { i*8 + 4, i*8, i*8 + 8}, 3);
                        else if (i == 1)
                            g.fillPolygon(new int[] { x + 8, x, x }, new int[] { i*8 + 4, i*8, i*8 + 8}, 3);
                        else if (i == 2)
                            g.fillPolygon(new int[] { x + 8, x, x + 8 }, new int[] { i*8 + 8, i*8, i*8 + 8}, 3);
                        else if (i == 3)
                            g.fillPolygon(new int[] { x, x + 8, x }, new int[] { i*8 + 8, i*8, i*8 + 8}, 3);
            }
        }
        return img;
    }
}
