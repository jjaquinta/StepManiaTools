package jo.sm.dl.logic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.sm.dl.data.MIDINotation;
import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.data.MIDITune;
import jo.sm.dl.data.ScoreDrawData;
import jo.util.ui.swing.logic.FontUtils;
import jo.util.utils.obj.StringUtils;

public class ScoreLogic
{
    private static final int                TREBLE         = 1;
    private static final int                BASS           = 2;

    private static final int                GRANULARITY    = 32;

    private static ScoreDrawData            mData;
    private static MIDITune                 mTune;
    private static List<MIDITrack>          mTracks;
    private static Map<MIDITrack, Integer>  mTrackStaff    = new HashMap<>();
    private static Dimension                mPreferredSize;
    private static int                      mNoteHeight    = 8;

    private static Graphics2D               mG;
    private static int                      mX;
    private static int                      mY;
    private static int                      mMeasures;
    private static int                      mNoteWidth;
    private static int                      mStaffHeight;
    private static int                      mMeasureWidth;
    private static Font                     mClefFont;
    private static Font                     mTextFont;
    private static int                      mLeftMargin;

    public static synchronized BufferedImage drawScore(ScoreDrawData data)
    {
        mData = data;
        mTune = mData.getTune();
        if (mData.getTracks().size() == 0)
            mData.getTracks().addAll(mTune.getTrackInfos());
        mTracks = mData.getTracks();
        mNoteHeight = mData.getNoteHeight();
        mData.getFeaturePositions().clear();
        mData.getPositionFeatures().clear();
        indexTune();
        BufferedImage img = new BufferedImage(mPreferredSize.width,
                mPreferredSize.height, BufferedImage.TYPE_INT_ARGB);
        mG = (Graphics2D)img.getGraphics();
        paint(mG);
        mG.dispose();
        mData.setImage(img);
        mData.setMeasureWidth(mMeasureWidth);
        return img;
    }

    private static final String TREBLE_CLEF = StringUtils.uniStr(0x1d11E);
    private static final String BASS_CLEF   = StringUtils.uniStr(0x1d122);
    private static final String NOTE_SHARP  = StringUtils.uniStr(0x1d130);
    private static final String NOTE_FLAT   = StringUtils.uniStr(0x1d12C);
    private static final String NOTE_DOT    = StringUtils.uniStr(0x1d16D);

    public static void paint(Graphics g1)
    {
        mG = (Graphics2D)g1;
        mG.setColor(Color.WHITE);
        mG.fillRect(0, 0, mPreferredSize.width, mPreferredSize.height);
        if (mTune == null)
            return;
        mY = mNoteHeight * 4;
        for (int i = 0; i < mTracks.size(); i++)
        {
            MIDITrack track = mTracks.get(i);
            paintTrack(track);
        }
        mData.addFeaturePosition(new Rectangle(mLeftMargin, mNoteHeight*4, mMeasures*mMeasureWidth, mY - mNoteHeight*8), mTune);
    }

    private static void paintTrack(MIDITrack track)
    {
        mX = mLeftMargin;
        int staffType = mTrackStaff.get(track);
        int staffTop = mY;
        int staffBot = staffTop + mStaffHeight;
        if (staffType == (TREBLE | BASS))
            staffBot += mStaffHeight * 2;
        int staffLeft = mX;
        int staffRight = mX + mMeasures*mMeasureWidth;
        mG.setColor(Color.BLACK);
        //mG.setColor(ScorePanel.getTrackColor(track.getTrack()));
        for (int i = 0; i < 5; i++)
            mG.drawLine(staffLeft, staffTop + i * mNoteHeight,
                    staffRight, staffTop + i * mNoteHeight);
        mData.addFeaturePosition(new Rectangle(staffLeft, staffTop, staffRight, staffTop + 4*mNoteHeight), track);
        if (staffType == (TREBLE | BASS))
        {
            for (int i = 0; i < 5; i++)
                mG.drawLine(staffLeft, staffBot - i * mNoteHeight,
                        staffRight,
                        staffBot - i * mNoteHeight);
            mData.addFeaturePosition(new Rectangle(staffLeft, staffBot - 4 * mNoteHeight, staffRight, staffBot), track);
        }
        for (int i = 0; i <= mMeasures; i++)
            mG.drawLine(staffLeft + mMeasureWidth * i, staffTop,
                    staffLeft + mMeasureWidth * i, staffBot);
        mG.drawLine(staffLeft + mNoteWidth, staffTop,
                staffLeft + mNoteWidth, staffBot);
        mG.drawLine(staffRight - mNoteWidth, staffTop,
                staffRight - mNoteWidth, staffBot);

        mG.setFont(mTextFont);
        mG.drawString(track.toString()+", type="+staffType, staffLeft - mNoteWidth * 4, mY - mNoteHeight);
        mG.setFont(mClefFont);
        int clefLeft = staffLeft - mNoteWidth * 4;
        if (staffType == TREBLE)
        {
            mG.drawString(TREBLE_CLEF, clefLeft, mY + mStaffHeight);
            mData.addFeaturePosition(new Rectangle(clefLeft, mY, mNoteWidth*4, mStaffHeight), track);
        }
        else if (staffType == BASS)
        {
            mG.drawString(BASS_CLEF, clefLeft, mY + mStaffHeight);
            mData.addFeaturePosition(new Rectangle(clefLeft, mY, mNoteWidth*4, mStaffHeight), track);
        }
        else if (staffType == (TREBLE | BASS))
        {
            mG.drawString(TREBLE_CLEF, clefLeft, mY + mStaffHeight);
            mG.drawString(BASS_CLEF, clefLeft, mY + mStaffHeight * 3);
            mData.addFeaturePosition(new Rectangle(clefLeft, mY, mNoteWidth*4, mStaffHeight), track);
            mData.addFeaturePosition(new Rectangle(clefLeft, mY, mNoteWidth*4, mStaffHeight), track);
        }
        mG.setFont(mTextFont);
        for (MIDINotation note : track.getNotation())
            drawNote(note, staffType);
        mY = staffBot + mStaffHeight * 3;
    }

    private static void drawNote(MIDINotation n, int staffType)
    {
        double m = n.getMeasure();
        int x = mX + (int)(m * GRANULARITY * mNoteWidth);
        int y;
        if (staffType == BASS)
            y = mY - (n.getYAdjust() + 2) * mNoteHeight / 2;
        else
            y = mY - (n.getYAdjust() - 10) * mNoteHeight / 2;
        if (NotationLogic.NOTE_WHOLE.equals(n.getSymbol()))
            drawNoteSymbol(n, x, y, false, false, 0);
        else if (NotationLogic.NOTE_HALF.equals(n.getSymbol()))
            drawNoteSymbol(n, x, y, false, true, 0);
        else if (NotationLogic.NOTE_QUARTER.equals(n.getSymbol()))
            drawNoteSymbol(n, x, y, true, true, 0);
        else if (NotationLogic.NOTE_EIGTH.equals(n.getSymbol()))
            drawNoteSymbol(n, x, y, true, true, 1);
        else if (NotationLogic.NOTE_SIXTEENTH.equals(n.getSymbol()))
            drawNoteSymbol(n, x, y, true, true, 1);
        else if (NotationLogic.NOTE_THIRTYSECOND.equals(n.getSymbol()))
            drawNoteSymbol(n, x, y, true, true, 2);
        else if (NotationLogic.NOTE_SIXTYFORTH.equals(n.getSymbol()))
            drawNoteSymbol(n, x, y, true, true, 3);
        if (n.getSharps() > 0)
            for (int i = 0; i < n.getSharps(); i++)
                mG.drawString(NOTE_SHARP, x - mNoteWidth * i, y);
        if (n.getFlats() > 0)
            for (int i = 0; i < n.getFlats(); i++)
                mG.drawString(NOTE_FLAT, x - mNoteWidth * i, y);
        if (n.getDots() > 0)
            for (int i = 0; i < n.getDots(); i++)
                mG.drawString(NOTE_DOT, x + mNoteWidth * (i + 1), y);
        mData.addFeaturePosition(new Rectangle(x, y - mNoteHeight / 2, mNoteWidth, mNoteHeight), n.getNote());
        //mG.drawString(MIDINote.NOTES[n.getNote().getPitch()]+"/"+n.getYAdjust(), mX + x, y + mNoteWidth);
    }

    private static void drawNoteSymbol(MIDINotation n, int x, int y, boolean filled, boolean staff, int brevets)
    {
        if (mData.getHighlights().contains(n.getNote()))
        {
            mG.setColor(mData.getHighlightColor());
            mG.fillRect(x-1, y - mNoteHeight / 2-1, mNoteWidth+2,
                    mNoteHeight+2);
            mG.setColor(Color.black);
        }
        if (filled)
            mG.fillOval(x, y - mNoteHeight / 2, mNoteWidth,
                    mNoteHeight);
        else
            mG.drawOval(x, y - mNoteHeight / 2, mNoteWidth,
                    mNoteHeight);
        if (staff)
        {
            mG.drawLine(x + mNoteWidth, y - mStaffHeight,
                    x + mNoteWidth, y);
            for (int i = 0; i < brevets; i++)
                mG.drawLine(x + mNoteWidth,
                        y - mStaffHeight + (mNoteHeight / 2 * i),
                        x + mNoteWidth * 2,
                        y - mStaffHeight - (mNoteHeight / 2 * i));
        }
        if (mData.getSelected().contains(n.getNote()))
        {
            mG.setColor(mData.getSelectedColor());
            mG.drawRect(x-1, y - mNoteHeight / 2-1, mNoteWidth+2,
                    mNoteHeight+2);
            mG.setColor(Color.black);
        }
    }
    
    private static void indexTune()
    {
        mTrackStaff.clear();
        NotationLogic.makeNotation(mTune);
        int stavesHigh = 0;
        for (MIDITrack t : mTracks)
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
                mTrackStaff.put(t, BASS | TREBLE);
                stavesHigh += 5;
            }
        }
        stavesHigh += mTracks.size() + 1;
        mPreferredSize = new Dimension();
        long ticks = mTune.getLengthInTicks();
        long ppq = mTune.getPulsesPerQuarter();
        int quarters = (int)Math.ceil((double)ticks / ppq);
        mMeasures = (int)Math.ceil((double)quarters / 4);
        mNoteWidth = mNoteHeight + mNoteHeight / 2;
        mMeasureWidth = GRANULARITY * mNoteWidth;
        mPreferredSize.width = mLeftMargin + mMeasures*mMeasureWidth + mLeftMargin;
        mStaffHeight = 4 * mNoteHeight;
        mPreferredSize.height = mStaffHeight * stavesHigh;
        mClefFont = FontUtils.getFont(Font.DIALOG, mStaffHeight, Font.PLAIN);
        mTextFont = FontUtils.getFont(Font.DIALOG, mNoteHeight * 2,
                Font.PLAIN);
        mLeftMargin = mMeasureWidth/4;
    }
}
