package jo.sm.dle.cmd.ui.score;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.logic.ScoreLogic;

public class ScoreCanvas extends JComponent
{
    private MIDITune                 mTune;
    private List<MIDITrack>          mTracks;
    private Map<MIDINote, Rectangle> mNotePositions = new HashMap<>();
    private BufferedImage            mImage;
    private Dimension                mPreferredSize;
    private int                      mNoteHeight    = 8;

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
    }

    private void indexTune()
    {
        if (mTune == null)
            return;
        mImage = ScoreLogic.drawScore(mTune, mNoteHeight, mNotePositions,
                mTracks);
        mPreferredSize = new Dimension(mImage.getWidth(), mImage.getHeight());
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
}