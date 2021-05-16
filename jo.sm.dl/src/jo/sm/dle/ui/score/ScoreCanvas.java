package jo.sm.dle.ui.score;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JComponent;

import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.data.ScoreDrawData;
import jo.sm.dl.logic.ScoreLogic;

public class ScoreCanvas extends JComponent
{
    private ScoreDrawData            mData = new ScoreDrawData();
    private Dimension                mPreferredSize;
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
        if (mData.getTune() == null)
            return super.getPreferredSize();
        return mPreferredSize;
    }

    @Override
    public void paint(Graphics g1)
    {
        g1.drawImage(mData.getImage(), 0, 0, null);
        if (mCaret >= 0)
        {
            g1.setColor(Color.green);
            g1.drawLine(mCaret, 0, mCaret, mData.getImage().getHeight());
        }
    }

    private void indexTune()
    {
        if (mData.getTune() == null)
            return;
        ScoreLogic.drawScore(mData);
        mPreferredSize = new Dimension(mData.getImage().getWidth(), mData.getImage().getHeight());
    }
    
    public List<Object> getFeatureAt(int x, int y)
    {
        return mData.getFeatureAt(x, y);
    }
    
    public Rectangle getFeaturePosition(Object feature)
    {
        return mData.getFeaturePosition(feature);
    }

    public MIDITune getTune()
    {
        return mData.getTune();
    }

    public void setTune(MIDITune tune)
    {
        mData.setTune(tune);
        indexTune();
        repaint();
    }

    public List<MIDITrack> getTracks()
    {
        return mData.getTracks();
    }

    public void setTracks(List<MIDITrack> tracks)
    {
        mData.setTracks(tracks);
        indexTune();
        repaint();
    }

    public int getNoteHeight()
    {
        return mData.getNoteHeight();
    }

    public void setNoteHeight(int noteHeight)
    {
        mData.setNoteHeight(noteHeight);
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

    public ScoreDrawData getData()
    {
        return mData;
    }

    public void setData(ScoreDrawData data)
    {
        mData = data;
        indexTune();
        repaint();
    }
}
