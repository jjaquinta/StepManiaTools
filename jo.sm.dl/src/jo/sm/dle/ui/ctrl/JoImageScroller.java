package jo.sm.dle.ui.ctrl;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JComponent;
import javax.swing.JScrollBar;

public class JoImageScroller extends JComponent implements AdjustmentListener
{
    private Image       mImage;
    private JScrollBar  mScroller;
    private int         mX;
    private int         mY;

    @Override
    public Dimension getPreferredSize()
    {
        if ((mScroller == null) || (mImage == null))
            return super.getPreferredSize();
        if (mScroller.getOrientation() == JScrollBar.HORIZONTAL)
            return new Dimension(mScroller.getSize().width, mImage.getHeight(null));
        else
            return new Dimension(mImage.getWidth(null), mScroller.getSize().height);
    }
    
    @Override
    public void adjustmentValueChanged(AdjustmentEvent e)
    {
        if (mImage == null)
            return;
        int val = mScroller.getValue();
        if (mScroller.getOrientation() == JScrollBar.HORIZONTAL)
        {
            mY = 0;
            mX = -val;
        }
        else
        {
            mX = 0;
            mY = -val;
        }
        repaint();
    }
    
    @Override
    public void paint(Graphics g)
    {
        g.drawImage(mImage, mX, mY, null);
    }
    
    public Image getImage()
    {
        return mImage;
    }
    public void setImage(Image image)
    {
        mImage = image;
    }
    public JScrollBar getScroller()
    {
        return mScroller;
    }
    public void setScroller(JScrollBar scroller)
    {
        if (mScroller != null)
            mScroller.addAdjustmentListener(this);
        mScroller = scroller;
        if (mScroller != null)
            mScroller.addAdjustmentListener(this);
        adjustmentValueChanged(null);
    }
    
}
