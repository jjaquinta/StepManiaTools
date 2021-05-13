package jo.sm.dl.logic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import jo.sm.dl.data.MIDINotation;
import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.MIDITune;
import jo.util.utils.obj.StringUtils;

public class ScoreLogic
{
    private static final int TREBLE = 1;
    private static final int BASS = 2;
    
    private static MIDITune    mTune;
    private static Map<MIDINote, Rectangle>    mNotePositions = new HashMap<>();
    private static Map<MIDITrack, Integer>    mTrackStaff = new HashMap<>();
    private static Dimension   mPreferredSize;
    private static int         mNoteHeight = 8;
    
    private static Graphics2D  mG;
    private static int         mX;
    private static int         mY;
    private static int mMeasures;
    private static int mNoteWidth;
    private static int mStaffHeight;        
    private static int mMeasureWidth;
    
    public static synchronized BufferedImage drawScore(MIDITune tune, int noteHeight, Map<MIDINote, Rectangle> notePositions)
    {
        mTune = tune;
        mNoteHeight = noteHeight;
        mNotePositions = notePositions;
        indexTune();
        BufferedImage img = new BufferedImage(mPreferredSize.width, mPreferredSize.height, BufferedImage.TYPE_INT_ARGB);
        mG = (Graphics2D)img.getGraphics();
        paint(mG);
        mG.dispose();
        return img;
    }
    
    private static final String TREBLE_CLEF = StringUtils.uniStr(0x1d11E);
    private static final String BASS_CLEF = StringUtils.uniStr(0x1d122);
    private static final String NOTE_SHARP = StringUtils.uniStr(0x1d130);
    private static final String NOTE_FLAT = StringUtils.uniStr(0x1d12C);
    private static final String NOTE_DOT = StringUtils.uniStr(0x1d16D);

    public static void paint(Graphics g1)
    {
        mG = (Graphics2D)g1;
        mG.setColor(Color.WHITE);
        mG.fillRect(0, 0, mPreferredSize.width, mPreferredSize.height);
        if (mTune == null)
            return;
        mY = mNoteHeight*4;
        for (int i = 0; i < mTune.getTracks(); i++)
        {
            MIDITrack track = mTune.getTrackInfos().get(i);
            paintTrack(track);
        }
    }
    
    private static void paintTrack(MIDITrack track)
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
        for (MIDINotation note : track.getNotation())
            drawNote(note, staffType);
    }
    
    private static void drawNote(MIDINotation n, int staffType)
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
        if (mNotePositions != null)
            mNotePositions.put(n.getNote(), new Rectangle(mX + x, mY - mNoteHeight/2, mNoteWidth, mNoteHeight));
    }
    
    private static void indexTune()
    {
        mTrackStaff.clear();
        NotationLogic.makeNotation(mTune);
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
}
