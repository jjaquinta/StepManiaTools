package jo.sm.dl.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import jo.sm.dl.data.SMBeat;
import jo.sm.dl.data.SMMark;
import jo.sm.dl.data.SMMeasure;
import jo.sm.dl.data.SMTune;

public class SMIOLogic
{
    public static void writeSM(SMTune tune, File out) throws IOException
    {
        BufferedWriter wtr = new BufferedWriter(new FileWriter(out));
        write(wtr, "TITLE", tune.getTitle());
        write(wtr, "SUBTITLE", tune.getSubTitle());
        write(wtr, "ARTIST", tune.getArtist());
        write(wtr, "TITLETRANSLIT", tune.getTitleTranslit());
        write(wtr, "SUBTITLETRANSLIT", tune.getSubTitleTranslit());
        write(wtr, "ARTISTTRANSLIT", tune.getArtistTranslit());
        write(wtr, "GENRE", "");
        write(wtr, "CREDIT", tune.getCredit());
        write(wtr, "BANNER", tune.getBanner());
        write(wtr, "BACKGROUND", tune.getBackground());
        write(wtr, "LYRICSPATH", tune.getLyricsPath());
        write(wtr, "CDTITLE", tune.getCDTitle());
        write(wtr, "MUSIC", tune.getMusic());
        write(wtr, "OFFSET", "0.000000");
        write(wtr, "SAMPLESTART", "63.157894");
        write(wtr, "SAMPLELENGTH", "12.000000");
        write(wtr, "SELECTABLE", tune.isSelectable());
        write(wtr, "DISPLAYBPM", tune.getDisplayBPM());
        write(wtr, "BPMS", tune.getBPMs());
        write(wtr, "STOPS", tune.getStops());
        write(wtr, "BGCHANGES", "");
        write(wtr, "KEYSOUNDS", "");
        write(wtr, "ATTACKS", "");
        wtr.write("//---------------"+tune.getNotesChartType()+" - "+tune.getNotesDescription()+"----------------");
        wtr.newLine();
        wtr.write("#NOTES:");
        wtr.newLine();
        wtr.write("    "+tune.getNotesChartType()+":");
        wtr.newLine();
        wtr.write("    "+tune.getNotesDescription()+":");
        wtr.newLine();
        wtr.write("    "+tune.getNotesDifficulty()+":");
        wtr.newLine();
        wtr.write("    "+tune.getNotesMeter()+":");
        wtr.newLine();
        wtr.write("    0.004150,0.004357,0.000000,0.000000,0.000000,4.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.004150,0.004357,0.000000,0.000000,0.000000,4.000000,0.000000,0.000000,0.000000,0.000000,0.000000:");
        for (int m = 0; m < tune.getMeasures().size(); m++)
        {
            SMMeasure measure = tune.getMeasures().get(m);
            wtr.write("  // measure "+(m+1));
            wtr.newLine();
            for (SMBeat b : measure.getBeats())
            {
                wtr.write(SMIOLogic.toString(b));
                wtr.newLine();
            }
            if (m + 1 < tune.getMeasures().size())
                wtr.write(",");
            else
                wtr.write(";");
        }
        wtr.newLine();
        wtr.close();
    }
    public static void writeSSC(SMTune tune, File out) throws IOException
    {
        BufferedWriter wtr = new BufferedWriter(new FileWriter(out));
        write(wtr, "VERSION", "0.77");
        write(wtr, "TITLE", tune.getTitle());
        write(wtr, "SUBTITLE", tune.getSubTitle());
        write(wtr, "ARTIST", tune.getArtist());
        write(wtr, "TITLETRANSLIT", tune.getTitleTranslit());
        write(wtr, "SUBTITLETRANSLIT", tune.getSubTitleTranslit());
        write(wtr, "ARTISTTRANSLIT", tune.getArtistTranslit());
        write(wtr, "GENRE", "");
        write(wtr, "ORIGIN", "");
        write(wtr, "CREDIT", tune.getCredit());
        write(wtr, "BANNER", tune.getBanner());
        write(wtr, "BACKGROUND", tune.getBackground());
        write(wtr, "LYRICSPATH", tune.getLyricsPath());
        write(wtr, "CDTITLE", tune.getCDTitle());
        write(wtr, "MUSIC", tune.getMusic());
        write(wtr, "OFFSET", tune.getOffset());
        write(wtr, "SAMPLESTART", tune.getSampleStart());
        write(wtr, "SAMPLELENGTH", tune.getSampleLength());
        write(wtr, "SELECTABLE", tune.isSelectable());
        write(wtr, "BPMS", tune.getBPMs());
        write(wtr, "STOPS", tune.getStops());
        write(wtr, "DELAYS", "");
        write(wtr, "WARPS", "");
        write(wtr, "TIMESIGNATURES", "");
        write(wtr, "TICKCOUNTS", "");
        write(wtr, "COMBOS", "");
        write(wtr, "SPEEDS", "");
        write(wtr, "SCROLLS", "");
        write(wtr, "LABELS", "");
        write(wtr, "BGCHANGES", "");
        write(wtr, "KEYSOUNDS", "");
        write(wtr, "ATTACKS", "");
        wtr.write("//---------------dance-single----------------");
        wtr.newLine();
        write(wtr, "NOTEDATA", "");
        write(wtr, "CHARTNAME", "Jo");
        write(wtr, "STEPSTYLE", "dance-single");
        write(wtr, "DESCRIPTION", "");
        write(wtr, "CHARTSTYLE", "");
        write(wtr, "DIFFICULTY", "Challenge");
        write(wtr, "METER", "11");
        write(wtr, "RADARVALUES", "0.954,1.000,0.629,0.600,0.500,572.000,66.000,63.000,128.000,0.000,0.000,0.000,0.000,0.954,1.000,0.629,0.600,0.500,572.000,66.000,63.000,128.000,0.000,0.000,0.000,0.000");
        write(wtr, "CREDIT", "Jo");
        wtr.write("#NOTES:");
        wtr.newLine();
        for (int m = 0; m < tune.getMeasures().size(); m++)
        {
            SMMeasure measure = tune.getMeasures().get(m);
            wtr.write("  // measure "+(m+1));
            wtr.newLine();
            for (SMBeat b : measure.getBeats())
            {
                wtr.write(SMIOLogic.toString(b));
                wtr.newLine();
            }
            if (m + 1 < tune.getMeasures().size())
                wtr.write(",");
            else
                wtr.write(";");
        }
        wtr.close();
    }

    public static String toString(SMBeat beat)
    {
        StringBuffer sb = new StringBuffer();
        for (char ch : beat.getNotes())
            if (ch == 0)
                sb.append('0');
            else
                sb.append(ch);
        return sb.toString();
    }

    public static String toString(float[] arr)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++)
        {
            if (i > 0)
                sb.append(",");
            sb.append(arr[i]);
        }
        return sb.toString();
    }

    private static void write(BufferedWriter wtr, String tag, List<SMMark> marks) throws IOException
    {
        if (marks.size() == 0)
        {
            wtr.write("#"+tag+":;");
            wtr.newLine();
            return;
        }
        for (int i = 0; i < marks.size(); i++)
        {
            if (i == 0)
                wtr.write("#"+tag+":");
            else
                wtr.write("    ");
            write(wtr, marks.get(i));
            if (i + 1 == marks.size())
                wtr.write(";");
            else
                wtr.write(",");
            wtr.newLine();
        }
    }
    
    private static void write(BufferedWriter wtr, SMMark mark) throws IOException
    {
        wtr.write(String.valueOf(mark.getMark())+"="+String.valueOf(mark.getValue()));
    }
    
    private static void write(BufferedWriter wtr, String tag, float value) throws IOException
    {
        write(wtr, tag, String.valueOf(value));
    }
    
    private static void write(BufferedWriter wtr, String tag, boolean value) throws IOException
    {
        write(wtr, tag, value ? "YES" : "NO");
    }
    
    private static void write(BufferedWriter wtr, String tag, String value) throws IOException
    {
        if (value == null)
            value = "";
        wtr.write("#"+tag+":"+value+";");
        wtr.newLine();
    }
}
