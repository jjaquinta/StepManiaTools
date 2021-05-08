package jo.sm.dle.cmd.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import jo.sm.dl.data.MIDINotation;
import jo.sm.dl.data.MIDITune;
import jo.sm.dle.data.SongBean;
import jo.util.ui.swing.logic.FontUtils;
import jo.util.utils.PCSBeanUtils;
import jo.util.utils.obj.StringUtils;

public class ScorePanel extends JComponent
{
    private SongBean    mSong;
    private MIDITune    mTune;
    
    private Graphics2D  mG;
    private Dimension   mSize;
    private FontMetrics mMetrics;
    private int         mQuarterAdv;
    private int         mNoteWidth;
    private int         mNoteHeight;
    private int         mTopMargin;
    private int         mLeftMargin;
    private int         mStaffAdvance;
    private int         mMeasuresPerLine;
    private int         mStaffHeight;
    private int         mStaffGap;
    private int         mMeasureWidth;
    private int         mX;
    private int         mY;
    
    public ScorePanel()
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
        setFont(FontUtils.getFont(Font.DIALOG, 48, Font.PLAIN));
    }

    private void initLink()
    {
    }
    
    private static final String BARS = StringUtils.uniStr(0x1d11A);
    private static final String TREBLE_CLEF = StringUtils.uniStr(0x1d11E);
    private static final String BASS_CLEF = StringUtils.uniStr(0x1d122);
    //private static final String BRACE = StringUtils.uniStr(0x1d114);
    private static final String NOTE_WHOLE = StringUtils.uniStr(0x1d15D);
    private static final String NOTE_SHARP = StringUtils.uniStr(0x1d130);
    private static final String NOTE_FLAT = StringUtils.uniStr(0x1d12C);
    private static final String NOTE_DOT = StringUtils.uniStr(0x1d16D);
    private static final String NOTE_BAR = StringUtils.uniStr(0x1d17D);

    @Override
    public void paint(Graphics g1)
    {
        mG = (Graphics2D)g1;
        mSize = getSize();
        mG.setColor(Color.WHITE);
        mG.fillRect(0, 0, mSize.width, mSize.height);
        if (mTune == null)
            return;
        updateMetrics();
        drawMeasures();
        for (MIDINotation n : mTune.getNotation())
            drawNote(n);
    }

    private void drawMeasures()
    {
        mG.setColor(Color.BLACK);
        int measure = 0;
        long quarters = mTune.getLengthInTicks()/mTune.getPulsesPerQuarter();
        int measures = (int)Math.ceil(quarters/4.0);
        mX = mLeftMargin;
        mY = mTopMargin;
        while (measure < measures)
        {
            int numMeasures = mMeasuresPerLine;
            if (measure + numMeasures > measures)
                numMeasures = measures - measure;
            //mG.drawString(BRACE, mX - mQuarterAdv, mY);
            mG.drawString(BARS, mX - mQuarterAdv, mY);
            mG.drawString(TREBLE_CLEF, mX - mQuarterAdv, mY);
            mG.drawString(BARS, mX - mQuarterAdv, mY + mStaffHeight + mStaffGap);
            mG.drawString(BASS_CLEF, mX - mQuarterAdv, mY + mStaffHeight + mStaffGap);
            mG.drawLine(mX - mQuarterAdv, mY - mStaffHeight, mX - mQuarterAdv, mY + mStaffHeight + mStaffGap);
            for (int m = 0; m < numMeasures; m++)
            {
                for (int q = 0; q < 4; q++)
                {
                    mG.drawString(BARS, mX, mY);
                    mG.drawString(BARS, mX, mY + mStaffHeight + mStaffGap);
                    mX += mQuarterAdv;
                }
                mG.drawLine(mX, mY - mStaffHeight, mX, mY + mStaffHeight + mStaffGap);
            }
            measure += numMeasures;
            mY += mStaffAdvance;
            mX = mLeftMargin;
        }
    }
    
    private static final Color[] TRACK_COLORS = {
            Color.red,
            Color.blue,
            Color.green,
            Color.cyan,
            Color.magenta,
            Color.yellow,
            Color.black
    };
    
    private void drawNote(MIDINotation n)
    {
        if (!mSong.getTracks().contains(n.getNote().getTrack()))
            return;
        mG.setColor(TRACK_COLORS[n.getNote().getTrack()%TRACK_COLORS.length]);
        //System.out.println("pitch="+n.getNote().getPitch()+", dur="+n.getNote().getDuration()+", y="+n.getYAdjust());
        double m = n.getMeasure();
        int line = (int)Math.floor(m/mMeasuresPerLine);
        double off = m - line*mMeasuresPerLine;
        mY = mTopMargin + mStaffAdvance*line;
        mX = mLeftMargin + (int)(off*4*mQuarterAdv);
        mY -= (n.getYAdjust() - 3)*mNoteHeight/2;
        mG.drawString(n.getSymbol(), mX, mY);
        if (n.getYAdjust() == 0)
            mG.drawLine(mX, mY, mX+mNoteWidth, mY-mNoteHeight/2);
        if (n.getSharps() > 0)
            for (int i = 0; i < n.getSharps(); i++)
                mG.drawString(NOTE_SHARP, mX - mNoteWidth*(i +1), mY);
        if (n.getFlats() > 0)
            for (int i = 0; i < n.getFlats(); i++)
                mG.drawString(NOTE_FLAT, mX - mNoteWidth*(i +1), mY);
        if (n.getDots() > 0)
            for (int i = 0; i < n.getDots(); i++)
                mG.drawString(NOTE_DOT, mX + mNoteWidth*(i +1), mY);
        //mG.drawString(String.valueOf(n.getNote().getPitch()), mX+mNoteWidth, mY);
    }

    private void updateMetrics()
    {
        mMetrics = mG.getFontMetrics();
        Rectangle2D barsBounds = mMetrics.getStringBounds(BARS, mG);
        Rectangle2D wholeBounds = mMetrics.getStringBounds(NOTE_WHOLE, mG);
        System.out.println("bounds bars="+barsBounds.getWidth()+"x"+barsBounds.getHeight()+", note="+wholeBounds.getWidth()+"x"+wholeBounds.getHeight());
        mQuarterAdv = (int)(barsBounds.getWidth());
        mLeftMargin = mQuarterAdv*5;
        mStaffHeight = (int)(barsBounds.getHeight()) - mMetrics.getDescent()*2;
        mNoteWidth = mQuarterAdv/2;
        mNoteHeight = mStaffHeight/4;
        mTopMargin = mStaffHeight*2;
        mStaffGap = (int)(mNoteHeight*1.8);
        mStaffAdvance = mStaffHeight + mStaffGap + mStaffHeight + mStaffHeight*2;
        mMeasureWidth = mQuarterAdv*4;
        mMeasuresPerLine = (mSize.width - mLeftMargin*2)/mMeasureWidth;
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
        mTune = mSong.getProject().getMIDI();
        repaint();
        PCSBeanUtils.listen(mSong, "tracks", (ov,nv)->repaint());
    }
}
