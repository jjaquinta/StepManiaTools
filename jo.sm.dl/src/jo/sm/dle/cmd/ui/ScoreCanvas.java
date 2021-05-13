package jo.sm.dle.cmd.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import jo.sm.dl.data.MIDINotation;
import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.logic.NotationLogic;
import jo.util.utils.obj.StringUtils;

public class ScoreCanvas extends JComponent
{
    private static final int TREBLE = 1;
    private static final int BASS = 2;
    
    private MIDITune    mTune;
    private Map<MIDINote, Rectangle>    mNotePositions = new HashMap<>();
    private Map<MIDITrack, Integer>    mTrackStaff = new HashMap<>();
    private Map<MIDITrack, List<MIDINotation>>    mTrackNotes = new HashMap<>();
    private Dimension   mPreferredSize;
    private int         mNoteHeight = 8;
    
    private Graphics2D  mG;
    private Dimension   mSize;
    private int         mX;
    private int         mY;
    private int mMeasures;
    private int mNoteWidth;
    private int mStaffHeight;        
    private int mMeasureWidth;
    
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
    
    private static final String TREBLE_CLEF = StringUtils.uniStr(0x1d11E);
    private static final String BASS_CLEF = StringUtils.uniStr(0x1d122);
    private static final String NOTE_SHARP = StringUtils.uniStr(0x1d130);
    private static final String NOTE_FLAT = StringUtils.uniStr(0x1d12C);
    private static final String NOTE_DOT = StringUtils.uniStr(0x1d16D);

    @Override
    public void paint(Graphics g1)
    {
        mG = (Graphics2D)g1;
        mSize = getSize();
        mG.setColor(Color.WHITE);
        mG.fillRect(0, 0, mSize.width, mSize.height);
        if (mTune == null)
            return;
        mY = mNoteHeight*4;
        for (int i = 0; i < mTune.getTracks(); i++)
        {
            MIDITrack track = mTune.getTrackInfos().get(i);
            paintTrack(track);
        }
    }
    
    private void paintTrack(MIDITrack track)
    {
        mX = mMeasureWidth;
        int staffType = mTrackStaff.get(track);
        int staffTop = mY;
        int staffBot = staffTop + mStaffHeight;
        if (staffType == (TREBLE|BASS))
            staffBot += mStaffHeight*2;
        mG.setColor(Color.BLACK);
        for (int i = 0; i < 5; i++)
            mG.drawLine(mX, staffTop + i*mNoteHeight, mX + mMeasures*mMeasureWidth, staffTop + i*mNoteHeight);
        if (staffType == (TREBLE|BASS))
            for (int i = 0; i < 5; i++)
                mG.drawLine(mX, staffBot - i*mNoteHeight, mX + mMeasures*mMeasureWidth, staffBot - i*mNoteHeight);
        for (int i = 0; i < mMeasures; i++)
            mG.drawLine(mX + mMeasureWidth*i, staffTop, mX + mMeasureWidth*i, staffBot);
        mG.drawString(track.toString(), mX - mNoteWidth*16, mY - mNoteHeight);
        if (staffType == TREBLE)
        {
            mG.drawString(TREBLE_CLEF, mX - mNoteWidth*16, mY + mStaffHeight);
        }
        else if (staffType == BASS)
        {
            mG.drawString(BASS_CLEF, mX - mNoteWidth*16, mY + mStaffHeight);
        }
        else if (staffType == (TREBLE|BASS))
        {
            mG.drawString(TREBLE_CLEF, mX - mNoteWidth*16, mY + mStaffHeight);
            mG.drawString(BASS_CLEF, mX - mNoteWidth*16, mY + mStaffHeight*3);
        }
        for (MIDINotation note : mTrackNotes.get(track))
            drawNote(note, staffType);
    }
    
    private void drawNote(MIDINotation n, int staffType)
    {
        double m = n.getMeasure();
        int x = mX + (int)(m*64*mNoteWidth);
        int y;
        if (staffType == BASS)
            y = mY - (n.getYAdjust() + 1)*mNoteHeight/2;
        else
            y = mY - (n.getYAdjust() - 5)*mNoteHeight/2;
        if (NotationLogic.NOTE_WHOLE.equals(n.getSymbol()))
        {
            mG.drawOval(mX + x, mY + y - mNoteHeight/2, mNoteWidth, mNoteHeight);
        }
        else if (NotationLogic.NOTE_HALF.equals(n.getSymbol()))
        {
            mG.drawOval(mX + x, mY + y - mNoteHeight/2, mNoteWidth, mNoteHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth, mY + y);
        }
        else if (NotationLogic.NOTE_QUARTER.equals(n.getSymbol()))
        {
            mG.fillOval(mX + x, mY + y - mNoteHeight/2, mNoteWidth, mNoteHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth, mY + y);
        }
        else if (NotationLogic.NOTE_EIGTH.equals(n.getSymbol()))
        {
            mG.fillOval(mX + x, mY + y - mNoteHeight/2, mNoteWidth, mNoteHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth, mY + y);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth*2, mY + y - mStaffHeight);
        }
        else if (NotationLogic.NOTE_SIXTEENTH.equals(n.getSymbol()))
        {
            mG.fillOval(mX + x, mY + y - mNoteHeight/2, mNoteWidth, mNoteHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth, mY + y);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth*2, mY + y - mStaffHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight - mNoteHeight/2, mX + x + mNoteWidth*2, mY + y - mStaffHeight - mNoteHeight/2);
        }
        else if (NotationLogic.NOTE_THIRTYSECOND.equals(n.getSymbol()))
        {
            mG.fillOval(mX + x, mY + y - mNoteHeight/2, mNoteWidth, mNoteHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth, mY + y);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth*2, mY + y - mStaffHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight - mNoteHeight/2, mX + x + mNoteWidth*2, mY + y - mStaffHeight - mNoteHeight/2);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight - mNoteHeight, mX + x + mNoteWidth*2, mY + y - mStaffHeight - mNoteHeight);
        }
        else if (NotationLogic.NOTE_SIXTYFORTH.equals(n.getSymbol()))
        {
            mG.fillOval(mX + x, mY + y - mNoteHeight/2, mNoteWidth, mNoteHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth, mY + y);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight, mX + x + mNoteWidth*2, mY + y - mStaffHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight - mNoteHeight/2, mX + x + mNoteWidth*2, mY + y - mStaffHeight - mNoteHeight/2);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight - mNoteHeight, mX + x + mNoteWidth*2, mY + y - mStaffHeight - mNoteHeight);
            mG.drawLine(mX + x + mNoteWidth, mY + y - mStaffHeight - mNoteHeight*3/2, mX + x + mNoteWidth*2, mY + y - mStaffHeight - mNoteHeight*3/2);
        }
        if (n.getSharps() > 0)
            for (int i = 0; i < n.getSharps(); i++)
                mG.drawString(NOTE_SHARP, mX + x, mY + y - mNoteWidth*i);
        if (n.getFlats() > 0)
            for (int i = 0; i < n.getFlats(); i++)
                mG.drawString(NOTE_FLAT, mX + x, mY + y - mNoteWidth*i);
        if (n.getDots() > 0)
            for (int i = 0; i < n.getDots(); i++)
                mG.drawString(NOTE_DOT, mX + x, mY + y + mNoteWidth*(i + 1));
        mNotePositions.put(n.getNote(), new Rectangle(mX + x, mY - mNoteHeight/2, mNoteWidth, mNoteHeight));
    }
    
    private void indexTune()
    {
        mTrackStaff.clear();
        mTrackNotes.clear();
        int stavesHigh = 0;
        for (MIDITrack t : mTune.getTrackInfos())
        {
            if (t.getLowPitch() > 51)
            {
                mTrackStaff.put(t, TREBLE);
                stavesHigh += 3;
            }
            else if (t.getHighPitch() < 69)
            {
                mTrackStaff.put(t, BASS);
                stavesHigh += 3;
            }
            else
            {
                mTrackStaff.put(t, BASS|TREBLE);
                stavesHigh += 5;
            }
            mTrackNotes.put(t, NotationLogic.makeNotation(mTune, t.getNotes()));
        }
        stavesHigh += mTune.getTracks() + 1;
        mPreferredSize = new Dimension();
        long ticks = mTune.getLengthInTicks();
        long ppq = mTune.getPulsesPerQuarter();
        int quarters = (int)Math.ceil((double)ticks/ppq);
        mMeasures = (int)Math.ceil((double)quarters/4);
        int sixtyfourths = (mMeasures + 2)*64;
        mNoteWidth = mNoteHeight + mNoteHeight/2;
        mPreferredSize.width = sixtyfourths*mNoteWidth;
        mStaffHeight = 4*mNoteHeight;        
        mPreferredSize.height = mStaffHeight*stavesHigh;
        mMeasureWidth = 64*mNoteWidth;
    }

    public MIDITune getTune()
    {
        return mTune;
    }

    public void setSong(MIDITune tune)
    {
        mTune = tune;
        indexTune();
        repaint();
    }
}
