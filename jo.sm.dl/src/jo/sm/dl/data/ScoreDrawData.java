package jo.sm.dl.data;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreDrawData
{
    // inputs
    private MIDITune mTune;
    private int mNoteHeight = 8; 
    private List<MIDITrack> mTracks = new ArrayList<>();
    // outputs
    private BufferedImage   mImage;
    private Map<Rectangle,Object> mFeaturePositions = new HashMap<>();
    private Map<Object,Rectangle> mPositionFeatures = new HashMap<>();
    private int mMeasureWidth; 
    
    // utilities
    
    public void addFeaturePosition(Rectangle position, Object feature)
    {
        mFeaturePositions.put(position, feature);
        mPositionFeatures.put(feature, position);
    }
    
    public List<Object> getFeatureAt(int x, int y)
    {
        List<Object> features = new ArrayList<>();
        for (Rectangle r : mFeaturePositions.keySet())
            if (r.contains(x, y))
                features.add(mFeaturePositions.get(r));
        Collections.sort(features, new Comparator<Object>(){
            @Override
            public int compare(Object o1, Object o2)
            {
                Rectangle r1 = getFeaturePosition(o1);
                Rectangle r2 = getFeaturePosition(o2);
                return r1.width*r1.height - r2.width - r2.height;
            }
        });
        return features;
    }

    
    public Rectangle getFeaturePosition(Object feature)
    {
        return mPositionFeatures.get(feature);
    }
    
    // getters and setters
    public MIDITune getTune()
    {
        return mTune;
    }
    public void setTune(MIDITune tune)
    {
        mTune = tune;
    }
    public int getNoteHeight()
    {
        return mNoteHeight;
    }
    public void setNoteHeight(int noteHeight)
    {
        mNoteHeight = noteHeight;
    }
    public List<MIDITrack> getTracks()
    {
        return mTracks;
    }
    public void setTracks(List<MIDITrack> tracks)
    {
        mTracks = tracks;
    }
    public BufferedImage getImage()
    {
        return mImage;
    }
    public void setImage(BufferedImage image)
    {
        mImage = image;
    }
    public Map<Rectangle, Object> getFeaturePositions()
    {
        return mFeaturePositions;
    }
    public void setFeaturePositions(Map<Rectangle, Object> featurePositions)
    {
        mFeaturePositions = featurePositions;
    }

    public Map<Object, Rectangle> getPositionFeatures()
    {
        return mPositionFeatures;
    }

    public void setPositionFeatures(Map<Object, Rectangle> positionFeatures)
    {
        mPositionFeatures = positionFeatures;
    }

    public int getMeasureWidth()
    {
        return mMeasureWidth;
    }

    public void setMeasureWidth(int measureWidth)
    {
        mMeasureWidth = measureWidth;
    }
}
