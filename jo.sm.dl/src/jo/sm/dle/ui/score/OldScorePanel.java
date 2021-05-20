package jo.sm.dle.ui.score;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import jo.sm.dl.data.midi.MIDINotation;
import jo.sm.dl.data.midi.MIDITune;
import jo.sm.dl.data.sm.SMBeat;
import jo.sm.dl.data.sm.SMChart;
import jo.sm.dl.data.sm.SMMeasure;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.util.ui.swing.logic.FontUtils;
import jo.util.utils.PCSBeanUtils;
import jo.util.utils.obj.StringUtils;

public class OldScorePanel extends JComponent
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
    private Font        mNoteFont;
    private Font        mTextFont;
    
    public OldScorePanel()
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
        setFont(FontUtils.getFont(Font.DIALOG, RuntimeLogic.getInstance().getZoomSize(), Font.PLAIN));
    }

    private void initLink()
    {
        RuntimeLogic.listen("zoomSize", (ov,nv)->repaint());
    }
    
    @Override
    public Dimension getPreferredSize()
    {
        if (mSong == null)
            return super.getPreferredSize();
        mG = (Graphics2D)getGraphics();
        mSize = getSize();
        updateMetrics();
        long quarters = mTune.getLengthInTicks()/mTune.getPulsesPerQuarter();
        int measures = (int)Math.ceil(quarters/4.0);
        int lines = (int)Math.ceil(measures/(double)mMeasuresPerLine);
        Dimension pref = new Dimension();
        pref.width = mLeftMargin + mMeasuresPerLine*mMeasureWidth + mLeftMargin;
        pref.height = mTopMargin + lines*mStaffAdvance;
        return pref;
    }
    
    private static final String BARS = StringUtils.uniStr(0x1d11A);
    private static final String TREBLE_CLEF = StringUtils.uniStr(0x1d11E);
    private static final String BASS_CLEF = StringUtils.uniStr(0x1d122);
    //private static final String BRACE = StringUtils.uniStr(0x1d114);
    //private static final String NOTE_WHOLE = StringUtils.uniStr(0x1d15D);
    private static final String NOTE_SHARP = StringUtils.uniStr(0x1d130);
    private static final String NOTE_FLAT = StringUtils.uniStr(0x1d12C);
    private static final String NOTE_DOT = StringUtils.uniStr(0x1d16D);
    //private static final String NOTE_BAR = StringUtils.uniStr(0x1d17D);

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
        drawArrows();
    }
    
    private static final String ARROWS = "<>^v";
    
    private void drawArrows()
    {
        SMChart chart = mSong.getProject().getTune().getChart(mSong.getSelectedChart());
        if (chart == null)
            return;
        mG.setColor(Color.ORANGE);
        setFont(mTextFont);
        int ppq = mSong.getProject().getMIDI().getPulsesPerQuarter();
        for (SMMeasure measure : chart.getMeasures())
        {
            for (SMBeat beat : measure.getBeats())
            {
                if (!beat.isAnySteps())
                    continue;
                double m = beat.getTick()/4.0/ppq;
                mapMeasureToPixel(m);
                mY += mStaffHeight*2;
                char[] notes = beat.getNotes();
                for (int i = 0; i < notes.length; i++)
                    if (notes[i] != '0')
                    {
                        mG.drawString(ARROWS.substring(i, i+1), mX, mY);
                        mY += mTextFont.getSize();
                    }
            }
        }
    }

    private void drawMeasures()
    {
        mG.setColor(Color.BLACK);
        mG.setFont(mNoteFont);
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
    
    public static final Color getTrackColor(int track)
    {
        return TRACK_COLORS[track%TRACK_COLORS.length];
    }
    
    public static final ImageIcon getTrackSwatch(int track)
    {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(OldScorePanel.getTrackColor(track));
        g.fillRect(0, 0, 16, 16);
        g.dispose();
        return new ImageIcon(img);
    }
    
    private void drawNote(MIDINotation n)
    {
        if (!mSong.getTracks().contains(n.getNote().getTrack()))
            return;
        mG.setColor(TRACK_COLORS[n.getNote().getTrack()%TRACK_COLORS.length]);
        //System.out.println("pitch="+n.getNote().getPitch()+", dur="+n.getNote().getDuration()+", y="+n.getYAdjust());
        double m = n.getMeasure();
        mapMeasureToPixel(m);
        mY -= (n.getYAdjust() - 3)*mNoteHeight/2;
        mG.setFont(mNoteFont);
        mG.drawString(n.getSymbol(), mX, mY);
        mG.setFont(mTextFont);
        if (n.getYAdjust() == 0)
            mG.drawLine(mX, mY-mNoteHeight/2, mX+mNoteWidth, mY-mNoteHeight/2);
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

    private void mapMeasureToPixel(double m)
    {
        int line = (int)Math.floor(m/mMeasuresPerLine);
        double off = m - line*mMeasuresPerLine;
        mY = mTopMargin + mStaffAdvance*line;
        mX = mLeftMargin + (int)(off*4*mQuarterAdv);
    }

    private void updateMetrics()
    {
        int zs = RuntimeLogic.getInstance().getZoomSize();
        mNoteFont = FontUtils.getFont(Font.DIALOG, zs, Font.PLAIN);
        mTextFont = FontUtils.getFont(Font.DIALOG, zs/2, Font.PLAIN);
        mG.setFont(mNoteFont);
        mMetrics = mG.getFontMetrics();
        Rectangle2D barsBounds = mMetrics.getStringBounds(BARS, mG);
        mQuarterAdv = (int)(barsBounds.getWidth());
        mLeftMargin = mQuarterAdv*3;
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
            PCSBeanUtils.unlisten(mSong, "tracks,selectedChart");
        mSong = song;
        mTune = mSong.getProject().getMIDI();
        repaint();
        PCSBeanUtils.listen(mSong, "tracks,selectedChart", (ov,nv)->repaint());
    }
}
