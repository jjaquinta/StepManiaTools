package jo.sm.dle.ui.score;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import jo.sm.dl.data.SMBeat;
import jo.sm.dl.data.SMChart;
import jo.sm.dl.data.ScoreDrawData;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.sm.dle.logic.SelectionLogic;
import jo.sm.dle.ui.ctrl.JoImageScroller;
import jo.util.ui.swing.logic.FontUtils;
import jo.util.ui.swing.utils.MouseUtils;

public class ArrowPanel extends JoImageScroller
{
    private Map<SMBeat, Rectangle>  mBeatToRect = new HashMap<>();
    private Map<Rectangle, SMBeat>  mRectToBeat = new HashMap<>();
    
    //private static final String ARROWS = "\u25c0\u1405\u1403\u1401";
    private static final Color ARROW_COLOR = Color.yellow;
    private static final Color MINE_COLOR = Color.red;
    private static final Color ARROW_BACK = Color.darkGray;
    private static final Color ARROW_SELECT = Color.blue;
    
    public ArrowPanel()
    {
        super();
        initLink();
    }
    
    private void initLink()
    {
        MouseUtils.mousePressed(this, (ev) -> doMousePressed(ev));
    }
    
    public void makeArrowImage(ScoreDrawData data)
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        if (data == null)
            return;
        Rectangle tune = data.getFeaturePosition(song.getProject().getMIDI());
        BufferedImage img = new BufferedImage(data.getImage().getWidth(), 32, BufferedImage.TYPE_INT_ARGB);
        SMChart chart = song.getProject().getTune().getChart(song.getSelectedChart());
        if (chart == null)
        {
            setImage(img);
            return;
        }
        Graphics g = img.getGraphics();
        g.setFont(FontUtils.getFont("Dialog", 8, Font.BOLD));
        int ppq = song.getProject().getMIDI().getPulsesPerQuarter();
        int[] startx = new int[4];
        for (SMBeat beat : chart.getAllBeats())
        {
            double m = beat.getTick()/4.0/ppq;
            int x = tune.x + (int)(m*data.getMeasureWidth());
            char[] notes = beat.getNotes();
            boolean any = false;
            g.setColor(ARROW_BACK);
            g.fillRect(x-1, 0, 10, 31);
            for (int i = 0; i < notes.length; i++)
                if (notes[i] != '0')
                {
                    if (notes[i] == SMBeat.NOTE_NORMAL)
                        drawNormal(g, i, x, i*8);
                    else if (notes[i] == SMBeat.NOTE_HOLD_HEAD)
                        startx[i] = x;
                    else if (notes[i] == SMBeat.NOTE_HOLD_RELEASE)
                        drawHold(g, i, startx[i], x, i*8);
                    else if (notes[i] == SMBeat.NOTE_MINE)
                        drawMine(g, x, i*8);
                    any = true;
                }
            if (any)
            {
                add(beat, new Rectangle(x, 0, 8, 32));
                if (song.getSelectedNotes().contains(beat.getNote()))
                {
                    g.setColor(ARROW_SELECT);
                    g.drawRect(x-1, 0, 10, 31);
                }
            }
        }
        setImage(img);
    }

    public void drawNormal(Graphics g, int shape, int x, int y)
    {
        g.setColor(ARROW_COLOR);
        g.fillPolygon(getShape(shape, x, y));
    }

    public void drawHold(Graphics g, int shape, int x1, int x2, int y)
    {
        g.setColor(ARROW_COLOR);
        g.fillPolygon(getShape(shape, x1, y));
        for (int x = x1 + 8; x < x2; x += 8)
            g.drawPolygon(getShape(shape, x, y));
        g.fillPolygon(getShape(shape, x2, y));
    }
    
    private Polygon getShape(int shape, int x, int y)
    {
        if (shape == 0)
            return new Polygon(new int[] { x, x + 8, x + 8 }, new int[] { y + 4, y, y + 8}, 3);
        else if (shape == 1)
            return new Polygon(new int[] { x + 8, x, x }, new int[] { y + 4, y, y + 8}, 3);
        else if (shape == 2)
            return new Polygon(new int[] { x, x + 4, x + 8 }, new int[] { y + 8, y, y + 8}, 3);
        else if (shape == 3)
            return new Polygon(new int[] { x, x + 4, x + 8}, new int[] { y, y + 8, y}, 3);
        throw new IllegalArgumentException();
    }

    public void drawMine(Graphics g, int x, int y)
    {
        g.setColor(MINE_COLOR);
        g.fillOval(x, y, 8, 8);
    }
    
    private void add(SMBeat beat, Rectangle r)
    {
        mBeatToRect.put(beat, r);
        mRectToBeat.put(r, beat);
    }

    public SMBeat getBeatAt(int x, int y)
    {
        for (Rectangle r : mRectToBeat.keySet())
            if (r.contains(x, y))
                return mRectToBeat.get(r);
        return null;
    }
    
    private void doMousePressed(MouseEvent ev)
    {
        if (ev.getButton() == MouseEvent.BUTTON1)
        {
            SMBeat beat = getBeatAt(ev.getX() - mX, ev.getY() - mY);
            if (beat != null)
                if (ev.isControlDown())
                {
                    SelectionLogic.toggleSelection(beat.getNote());
                    SelectionLogic.toggleBeats(beat);
                }
                else
                {
                    SelectionLogic.setSelection(beat.getNote());
                    SelectionLogic.setBeats(beat);
                }
            else
                System.out.println(ev.getX()+","+ev.getY()+" has no beat");
        }
    }

}
