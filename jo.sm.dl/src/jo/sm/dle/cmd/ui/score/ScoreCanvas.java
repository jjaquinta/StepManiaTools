package jo.sm.dle.cmd.ui.score;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.logic.ScoreLogic;

public class ScoreCanvas extends JComponent
{
    private MIDITune                 mTune;
    private List<MIDITrack>          mTracks;
    private Map<Rectangle,Object>    mFeaturePositions = new HashMap<>();
    private BufferedImage            mImage;
    private Dimension                mPreferredSize;
    private int                      mNoteHeight    = 8;
    private int                      mCaret = -1;

    public ScoreCanvas()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
    }

    private void initLayout()
    {
    }

    private void initLink()
    {
    }

    @Override
    public Dimension getPreferredSize()
    {
        if (mTune == null)
            return super.getPreferredSize();
        return mPreferredSize;
    }

    @Override
    public void paint(Graphics g1)
    {
        g1.drawImage(mImage, 0, 0, null);
        if (mCaret >= 0)
        {
            g1.setColor(Color.green);
            g1.drawLine(mCaret, 0, mCaret, mImage.getHeight());
        }
    }

    private void indexTune()
    {
        if (mTune == null)
            return;
        mFeaturePositions.clear();
        mImage = ScoreLogic.drawScore(mTune, mNoteHeight, mFeaturePositions,
                mTracks);
        mPreferredSize = new Dimension(mImage.getWidth(), mImage.getHeight());
    }
    
    public List<Object> getFeatureAt(int x, int y)
    {
        List<Object> features = new ArrayList<>();
        for (Rectangle r : mFeaturePositions.keySet())
            if (r.contains(x, y))
                features.add(mFeaturePositions.get(r));
        return features;
    }
    
    public Rectangle getFeaturePosition(Object feature)
    {
        for (Rectangle r : mFeaturePositions.keySet())
            if (mFeaturePositions.get(r) == feature)
                return r;
        return null;
    }

    public MIDITune getTune()
    {
        return mTune;
    }

    public void setTune(MIDITune tune)
    {
        mTune = tune;
        indexTune();
        repaint();
    }

    public List<MIDITrack> getTracks()
    {
        return mTracks;
    }

    public void setTracks(List<MIDITrack> tracks)
    {
        mTracks = tracks;
        indexTune();
        repaint();
    }

    public int getNoteHeight()
    {
        return mNoteHeight;
    }

    public void setNoteHeight(int noteHeight)
    {
        mNoteHeight = noteHeight;
        indexTune();
        repaint();
    }

    public int getCaret()
    {
        return mCaret;
    }

    public void setCaret(int caret)
    {
        mCaret = caret;
        repaint();
    }

    public void setCaretTo(Object feature)
    {
        Rectangle r = getFeaturePosition(feature);
        if (r != null)
            setCaret(r.x + r.width/2);
    }
}
