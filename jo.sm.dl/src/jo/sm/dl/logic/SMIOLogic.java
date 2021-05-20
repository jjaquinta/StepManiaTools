package jo.sm.dl.logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jo.sm.dl.data.sm.SMBeat;
import jo.sm.dl.data.sm.SMChart;
import jo.sm.dl.data.sm.SMMark;
import jo.sm.dl.data.sm.SMMeasure;
import jo.sm.dl.data.sm.SMTune;
import jo.util.utils.obj.StringUtils;

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
        addLyrics(wtr, tune, out);
        write(wtr, "CDTITLE", tune.getCDTitle());
        write(wtr, "MUSIC", tune.getMusic());
        write(wtr, "OFFSET", "0.000000");
        write(wtr, "SAMPLESTART", String.valueOf(tune.getSampleStart()));
        write(wtr, "SAMPLELENGTH", String.valueOf(tune.getSampleLength()));
        write(wtr, "SELECTABLE", tune.isSelectable());
        write(wtr, "DISPLAYBPM", tune.getDisplayBPM());
        write(wtr, "BPMS", tune.getBPMs());
        write(wtr, "STOPS", tune.getStops());
        write(wtr, "BGCHANGES", "");
        write(wtr, "KEYSOUNDS", "");
        write(wtr, "ATTACKS", "");
        for (SMChart chart : tune.getCharts())
        {
            wtr.write("//---------------"+chart.getNotesChartType()+" - "+chart.getNotesDescription()+"----------------");
            wtr.newLine();
            wtr.write("#NOTES:");
            wtr.newLine();
            wtr.write("    "+chart.getNotesChartType()+":");
            wtr.newLine();
            wtr.write("    "+chart.getNotesDescription()+":");
            wtr.newLine();
            wtr.write("    "+chart.getNotesDifficulty()+":");
            wtr.newLine();
            wtr.write("    "+chart.getNotesMeter()+":");
            wtr.newLine();
            wtr.write("    0.004150,0.004357,0.000000,0.000000,0.000000,4.000000,0.000000,0.000000,0.000000,0.000000,0.000000,0.004150,0.004357,0.000000,0.000000,0.000000,4.000000,0.000000,0.000000,0.000000,0.000000,0.000000:");
            for (int m = 0; m < chart.getMeasures().size(); m++)
            {
                SMMeasure measure = chart.getMeasures().get(m);
                wtr.write("  // measure "+(m+1));
                wtr.newLine();
                for (SMBeat b : measure.getBeats())
                {
                    wtr.write(SMIOLogic.toString(b));
                    wtr.newLine();
                }
                if (m + 1 < chart.getMeasures().size())
                    wtr.write(",");
                else
                    wtr.write(";");
            }
            wtr.newLine();
        }
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
        addLyrics(wtr, tune, out);
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
        write(wtr, "LABELS", tune.getLabels());
        write(wtr, "BGCHANGES", "");
        write(wtr, "KEYSOUNDS", "");
        write(wtr, "ATTACKS", "");
        for (SMChart chart : tune.getCharts())
        {
            wtr.write("//---------------"+chart.getNotesChartType()+"----------------");
            wtr.newLine();
            write(wtr, "NOTEDATA", "");
            write(wtr, "CHARTNAME", "Jo");
            write(wtr, "STEPSTYLE", chart.getNotesChartType());
            write(wtr, "DESCRIPTION", chart.getNotesDescription());
            write(wtr, "CHARTSTYLE", "");
            write(wtr, "DIFFICULTY", chart.getNotesDifficulty());
            write(wtr, "METER", chart.getNotesMeter());
            write(wtr, "RADARVALUES", "0.954,1.000,0.629,0.600,0.500,572.000,66.000,63.000,128.000,0.000,0.000,0.000,0.000,0.954,1.000,0.629,0.600,0.500,572.000,66.000,63.000,128.000,0.000,0.000,0.000,0.000");
            write(wtr, "CREDIT", "Jo");
            wtr.write("#NOTES:");
            wtr.newLine();
            for (int m = 0; m < chart.getMeasures().size(); m++)
            {
                SMMeasure measure = chart.getMeasures().get(m);
                wtr.write("  // measure "+(m+1));
                wtr.newLine();
                for (SMBeat b : measure.getBeats())
                {
                    wtr.write(SMIOLogic.toString(b));
                    wtr.newLine();
                }
                if (m + 1 < chart.getMeasures().size())
                    wtr.write(",");
                else
                    wtr.write(";");
            }
            wtr.newLine();
        }
        wtr.close();
    }

    private static void addLyrics(BufferedWriter wtr, SMTune tune, File base) throws IOException
    {
        if (tune.getLyricsPath() != null)
            write(wtr, "LYRICSPATH", tune.getLyricsPath());
        else if (tune.getLyrics().size() > 0)
        {
            sortMarks(tune.getLyrics());
            String lyricsName = base.getName();
            int o = lyricsName.lastIndexOf('.');
            if (o > 0)
                lyricsName = lyricsName.substring(0, o) + ".lrc";
            else
                lyricsName += ".lrc";
            File lyricsFile = new File(base.getParentFile(), lyricsName);
            BufferedWriter lwtr = new BufferedWriter(new FileWriter(lyricsFile));
            for (SMMark mark : tune.getLyrics())
            {
                int m = (int)Math.floor(mark.getMark()/60);
                int s = ((int)Math.floor(mark.getMark()))%60;
                int c = ((int)Math.floor(((mark.getMark() - Math.floor(mark.getMark()))*100)));
                String line = "["+m+":"+StringUtils.zeroPrefix(s, 2)+"."+StringUtils.zeroPrefix(c, 2)+"]"+mark.getStrValue();
                lwtr.write(line);
                lwtr.newLine();
            }
            lwtr.close();
            write(wtr, "LYRICSPATH", lyricsName);
        }
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
        sortMarks(marks);
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
    public static void sortMarks(List<SMMark> marks)
    {
        Collections.sort(marks, new Comparator<SMMark>() {
            @Override
            public int compare(SMMark o1, SMMark o2)
            {
                return (int)Math.signum(o1.getMark() - o2.getMark());
            }
        });
    }
    
    private static void write(BufferedWriter wtr, SMMark mark) throws IOException
    {
        if (mark.getNumValue() != null)
            wtr.write(String.valueOf(mark.getMark())+"="+String.valueOf(mark.getNumValue()));
        else
            wtr.write(String.valueOf(mark.getMark())+"="+String.valueOf(mark.getStrValue()));
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
