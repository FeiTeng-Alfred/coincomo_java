/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package main;

import core.COINCOMOUnit;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Raed Shomali
 */
public class GlobalMethods {

    /**
     * @param status is used to set the status bar content
     * @param color is used to set the status bar color
     */
    public static void updateStatusBar(String status, Color color) {
        //TODO (Larry) How to deal with dialogs that have no connection to COINCOMO? 
        System.out.println(color.toString() + ": " + status);
    }
    /**
     *
     * @param status is used to set the Status bar content
     */
    private static String lastUsedDirectory = null;

    public static void updateStatusBar(String status, COINCOMO c) {
        c.getStatusBar().setForeground(COINCOMO.defaultColor);
        c.getStatusBar().setText(" " + status.trim());
    }

    /**
     *
     * @param status is used to set the status bar content
     * @param color is used to set the status bar color
     */
    public static void updateStatusBar(String status, Color color, COINCOMO c) {
        c.getStatusBar().setForeground(color);
        c.getStatusBar().setText(" " + status.trim());
    }

    /**
     *
     * @param list is the Unordered Iterator
     * @return ordered Vector
     */
    public static ArrayList<COINCOMOUnit> getOrderedVector(HashMap list) {
        ArrayList<COINCOMOUnit> orderedVector = new ArrayList<COINCOMOUnit>();

        // Iterator Of the Hash map
        Iterator it = list.values().iterator();

        NextUnit:
        while (it.hasNext()) {
            // Get Next Unit ...
            COINCOMOUnit unit = (COINCOMOUnit) it.next();

            for (int i = 0; i < orderedVector.size(); i++) {
                // Temporary Unit For Comparison ..
                COINCOMOUnit tempUnit = (COINCOMOUnit) orderedVector.get(i);

                // If In Place ?
                if (unit.getUnitID() < tempUnit.getUnitID()) {
                    // Insert it ...
                    orderedVector.add(i, unit);

                    // Go Get the Next Unit ...
                    continue NextUnit;
                }
            }

            // Append to the end
            orderedVector.add(unit);
        }

        return orderedVector;
    }

    /**
     *
     * @param value to be formatted
     * @param by determines the rounding
     * @return the formatted result
     */
    public static float roundOff(double value, int by) {
        float p = (float) Math.pow(10, by);

        value = value * p;

        float tempValue = Math.round(value);

        return (float) tempValue / p;
    }

    /**
     *
     * @param value to be tested whether or not Non Negative Float
     * @return true if Non Negative, False otherwise
     */
    public static boolean isNonNegativeFloat(String value) {
        // To Check Duration Being Positive Float
        try {
            float result = Float.parseFloat(value.replaceAll(",", ""));

            // Value Must Be Nonnegative
            if (result < 0.0f) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param value to be tested whether or not Non Negative Double
     * @return true if Non Double, False otherwise
     */
    public static boolean isNonNegativeDouble(String value) {
        // To Check Duration Being Positive Double
        try {
            double result = Double.parseDouble(value.replaceAll(",", ""));

            // Value Must Be Nonnegative
            if (result < 0.0d) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param value to be tested whether or not to be of a Non Negative Int
     * Value
     * @return true if Non Negative Int Value, false otherwise
     */
    public static boolean isNonNegativeInt(String value) {
        // To Check Duration Being Positive Int
        try {
            // Remove the commas that separate the thousands/millions before parsing
            int result = Integer.parseInt(value.replaceAll(",", ""));

            // Value Must Be Nonnegative
            if (result < 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     *
     * @param value to be tested whether or not to be of a Non Negative Long
     * Value
     * @return true if Non Negative Long Value, false otherwise
     */
    public static boolean isNonNegativeLong(String value) {
        // To Check Duration Being Positive Long
        try {
            // Remove the commas that separate the thousands/millions before parsing
            long result = Long.parseLong(value.replaceAll(",", ""));

            // Value Must Be Nonnegative
            if (result < 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     *
     * @param value to be formatted with comma separating the thousands from the
     * rest of input
     * @return a value with comma separated number
     */
    public static String FormatFloatWithComma(float value) {
        String number = null;
        Locale locale = Locale.US;

        try {
            number = NumberFormat.getNumberInstance(locale).format(value);
        } catch (ArithmeticException e) {
            System.out.println(e.toString());
            number = "";
        }

        return number;
    }

    /**
     *
     * @param value the string with comma separating the thousands from the rest
     * of input
     * @return a value of type float from the string
     */
    public static float ParseFloatWithComma(String value) {
        float result = 0;

        try {
            result = Float.parseFloat(value.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            System.out.println(e.toString());
            result = 0;
        }

        return result;
    }

    /**
     *
     * @param value to be formatted with comma separating the thousands from the
     * rest of input
     * @return a value with comma separated number
     */
    public static String FormatDoubleWithComma(double value) {
        String number = null;
        Locale locale = Locale.US;

        try {
            number = NumberFormat.getNumberInstance(locale).format(value);
        } catch (ArithmeticException e) {
            System.out.println(e.toString());
            number = "";
        }

        return number;
    }

    /**
     *
     * @param value the string with comma separating the thousands from the rest
     * of input
     * @return a value of type double from the string
     */
    public static double ParseDoubleWithComma(String value) {
        double result = 0;

        try {
            result = Double.parseDouble(value.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            System.out.println(e.toString());
            result = 0;
        }

        return result;
    }

   /**
     *
     * @param value to be formatted with comma separating the thousands from the
     * rest of input
     * @return a value with comma separated number
     */
    public static String FormatLongWithComma(long value) {
        String number = null;
        Locale locale = Locale.US;

        try {
            number = NumberFormat.getNumberInstance(locale).format(value);
        } catch (ArithmeticException e) {
            System.out.println(e.toString());
            number = "";
        }

        return number;
    }

    /**
     *
     * @param value the string with comma separating the thousands from the rest
     * of input
     * @return a value of type integer from the string
     */
    public static int ParseIntWithComma(String value) {
        int result = 0;

        try {
            result = Integer.parseInt(value.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            System.out.println(e.toString());
            result = 0;
        }

        return result;
    }

    /**
     *
     * @param value to be formatted with comma separating the thousands from the
     * rest of input
     * @return a value with comma separated number
     */
    public static String FormatIntWithComma(int value) {
        String number = null;
        Locale locale = Locale.US;

        try {
            number = NumberFormat.getNumberInstance(locale).format(value);
        } catch (ArithmeticException e) {
            System.out.println(e.toString());
            number = "";
        }

        return number;
    }

    /**
     *
     * @param value the string with comma separating the thousands from the rest
     * of input
     * @return a value of type long from the string
     */
    public static long ParseLongWithComma(String value) {
        long result = 0;

        try {
            result = Long.parseLong(value.replaceAll(",", ""));
        } catch (NumberFormatException e) {
            System.out.println(e.toString());
            result = 0;
        }

        return result;
    }

    /**
     *
     * @param text is the rating in String
     * @return corresponding rating value
     */
    public static int convertFromRating(String text) {
        if (text.equals("VLO")) {
            return 1;
        } else if (text.equals("LO")) {
            return 2;
        } else if (text.equals("NOM")) {
            return 3;
        } else if (text.equals("HI")) {
            return 4;
        } else if (text.equals("VHI")) {
            return 5;
        } else {
            return 6;
        }
    }

    /**
     *
     * @param text is the increment percentage
     * @return the respective value
     */
    public static int convertFromPercent(String text) {
        if (text.equals("0%")) {
            return 0;
        } else if (text.equals("25%")) {
            return 1;
        } else if (text.equals("50%")) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     *
     * @param i is used to determine the Rating
     * @return the rating
     */
    public static String convertToRating(int i) {
        switch (i) {
            case 1:
                return "VLO";
            case 2:
                return "LO";
            case 3:
                return "NOM";
            case 4:
                return "HI";
            case 5:
                return "VHI";
            default:
                return "XHI";
        }
    }

    /**
     *
     * @param i Used to determine the Increment Percentage
     * @return the percentage
     */
    public static String convertToPercent(int i) {
        switch (i) {
            case 0:
                return "0%";
            case 1:
                return "25%";
            case 2:
                return "50%";
            default:
                return "75%";
        }
    }

    public static File getFile(COINCOMO frame, String dFilename, String[] validExt, String btnTxt, boolean export) {
        JFileChooser fc = new JFileChooser();

        //Set up custom icons for different file/folder extensions
        fc.setFileView(new IconFileView(validExt, export));

        fc.updateUI();
        if (dFilename == null) {
            dFilename = "";
        }
        if (validExt[0].equals("csv")) {
            //String currentDir = System.getProperty("user.dir");
            //File file = new File(currentDir);
            //File[] filesArray = file.listFiles();
            FileFilter csvFilter = new CsvFilter();
            fc.addChoosableFileFilter(csvFilter);
            fc.setFileFilter(csvFilter);
        } else if (validExt[0].equals("html")) {
            FileFilter htmlFilter = new HtmlFilter();
            fc.addChoosableFileFilter(htmlFilter);
            fc.setFileFilter(htmlFilter);
        } else if (validExt[0].equals("xml")) {
            FileFilter xmlFilter = new XmlFilter();
            fc.addChoosableFileFilter(xmlFilter);
            fc.setFileFilter(xmlFilter);
        } else if (validExt[0].equals("cet")) {
            FileFilter cetFilter = new cetFilter();
            fc.addChoosableFileFilter(cetFilter);
            fc.setFileFilter(cetFilter);
        } else if (validExt[0].equals("cal")) {
            FileFilter calFilter = new CalFilter();
            fc.addChoosableFileFilter(calFilter);
            fc.setFileFilter(calFilter);
        }

        if (lastUsedDirectory == null) {
            lastUsedDirectory = Utility.getUserDocumentFolder();
        }

        if (dFilename.equals("")) {
            fc.setCurrentDirectory(new File(lastUsedDirectory));
        } else {
            fc.setSelectedFile(new File(lastUsedDirectory + File.separatorChar + dFilename));
        }
        fc.setMultiSelectionEnabled(false);

        int result = -1;
        while (true) {
            result = fc.showDialog(frame, btnTxt);
            // result = fc.showSaveDialog(frame);
            File f = fc.getSelectedFile();
            /* if(f!=null)
             {
             String[] temp;
             String delimiter = "\\.";
             String fileName=f.getName();
             //System.out.println(fileName);
             temp = fileName.split(delimiter);
             //System.out.println("filename"+temp[0]);

             if(temp[0].contains("/") || temp[0].contains("\\") ||
             temp[0].contains("\n") ||  temp[0].contains("\r") ||
             * 
             temp[0].contains("\t") ||  temp[0].contains("\0") ||
             temp[0].contains("`") ||  temp[0].contains("\f") ||
             temp[0].contains("?") || temp[0].contains("*") ||
             temp[0].contains("<") || temp[0].contains(">") ||
             temp[0].contains("|") || temp[0].contains("\"") ||
             temp[0].contains(":") || temp[0].equals(".xml")){

             //", "SPECIAL CHARACTERS IN NAME ERROR
             JOptionPane.showMessageDialog(null, "File Name shouldn't contain <, >, /, \\, &, /, newline, carriage return, tab, null, '\\f', `, ?, *, |, \" and :", "SAVE ERROR", 0);
            
            
             }

            
             }*/

            if (result == JFileChooser.CANCEL_OPTION) {
                return null;
            }
            if (f != null) {
                String extList = "";
                int flag = 0;
                for (int i = 0; i < validExt.length; i++) {
                    extList += validExt[i] + ", ";

                    if (f.getName().toLowerCase().endsWith(validExt[i])) {
                        String[] temp;
                        String delimiter = "\\.";
                        String fileName = f.getName();
                        temp = fileName.split(delimiter);
                        //System.out.println("filename"+temp[0]);

                        if (!temp[0].contains("/") && !temp[0].contains("\\")
                                && !temp[0].contains("\n") && !temp[0].contains("\r")
                                && !temp[0].contains("\t") && !temp[0].contains("\0")
                                && !temp[0].contains("`") && !temp[0].contains("\f")
                                && !temp[0].contains("?") && !temp[0].contains("*")
                                && !temp[0].contains("<") && !temp[0].contains(">")
                                && !temp[0].contains("|") && !temp[0].contains("\"")
                                && !temp[0].contains(":") && !temp[0].toLowerCase().equals(".xml")
                                && !temp[0].contains("&")) {

                            return fc.getSelectedFile();
                        } else {
                            flag = 1;
                            JOptionPane.showMessageDialog(null, "File Name shouldn't contain <, >, /, \\, &, /, newline, carriage return, tab, null, '\\f', `, ?, *, |, \" and :", "SAVE ERROR", 0);
                        }

                    }
                }
                if (flag != 1) {
                    JOptionPane.showMessageDialog(null, "Enter file with valid extension. \n("
                            + extList.substring(0, extList.length() - 2) + ")");
                }
                flag = 0;
            }

            //GlobalMethods.updateStatusBar("File has to have valid extension.", Color.RED, frame);

        }
    }

    public static void setLastUsedDirectory(String path) {
        lastUsedDirectory = path;
    }

    private static void log(Level level, String message) {
        Logger.getLogger(GlobalMethods.class.getName()).log(level, message);
    }
}

class HtmlFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept(File file) {

        String filename = file.getName();
        if (file.isDirectory()) {
            return true;
        }
        return (filename.toLowerCase().endsWith(".html") || filename.toLowerCase().endsWith(".htm") || filename.toLowerCase().endsWith(".lnk"));
    }

    public String getDescription() {

        return "*.html, *.htm";

    }
}

class XmlFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept(File file) {

        String filename = file.getName();
        if (file.isDirectory()) {
            return true;
        }
        return (filename.toLowerCase().endsWith(".xml") || filename.toLowerCase().endsWith(".lnk"));
    }

    public String getDescription() {

        return "*.xml";

    }
}

class CsvFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept(File file) {

        String filename = file.getName();
        if (file.isDirectory()) {
            return true;
        }

        return (filename.toLowerCase().endsWith(".csv") || filename.toLowerCase().endsWith(".lnk"));
    }

    public String getDescription() {

        return "*.csv";

    }
}

class cetFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept(File file) {

        String filename = file.getName();
        if (file.isDirectory()) {
            return true;
        }
        return (filename.toLowerCase().endsWith(".cet") || filename.toLowerCase().endsWith(".lnk"));
    }

    public String getDescription() {

        return "*.cet";

    }
}

class CalFilter extends javax.swing.filechooser.FileFilter {

    public boolean accept(File file) {

        String filename = file.getName();
        if (file.isDirectory()) {
            return true;
        }
        return (filename.toLowerCase().endsWith(".cal") || filename.toLowerCase().endsWith(".lnk"));
    }

    public String getDescription() {

        return "*.cal";

    }
}

class Utility {

    static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /*
     * Determine the default home user directory
     * 
     * For Windows, it should be C:\Users\[User Name]\My Documents or C:\Documents and Settings\[User Name]\My Documents
     * For *nix, it should be /home/[User Name]/ or the ~/
     * For Mac OS X, it should be /Users/[User Name]/
     */
    static String getUserDocumentFolder() {
        String dir = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.startsWith("windows")) {
            dir += File.separatorChar + "Documents";
        } else if (os.startsWith("mac os x")) {
            dir += File.separatorChar + "Documents";
        }

        return dir;
    }
}

/*
 * The following codes are taken from http://stackoverflow.com/questions/309495/windows-shortcut-lnk-parser-in-java
 * for dealing with .lnk shortcuts in Windows.
 * 
 * The code plan9assembler linked to appears to work with minor modification. I think it's just the "& 0xff" to prevent sign extension when bytes are upcast 
 * to ints in the bytes2short function that need changing. I've added the functionality described in http://www.i2s-lab.com/Papers/The_Windows_Shortcut_File_Format.pdf 
 * to concatenate the "final part of the pathname" even though in practice this doesn't seem to be used in my examples. I've not added any error checking to 
 * the header or dealt with network shares. Here's what I'm using now:
 *
 */
class LnkParser {

    public LnkParser(File f) throws Exception {
        parse(f);
    }
    private boolean is_dir;

    public boolean isDirectory() {
        return is_dir;
    }
    private String real_file;

    public String getRealFilename() {
        return real_file;
    }

    private void parse(File f) throws Exception {
        // read the entire file into a byte buffer
        FileInputStream fin = new FileInputStream(f);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buff = new byte[256];
        while (true) {
            int n = fin.read(buff);
            if (n == -1) {
                break;
            }
            bout.write(buff, 0, n);
        }
        fin.close();
        byte[] link = bout.toByteArray();

        // get the flags byte
        byte flags = link[0x14];

        // get the file attributes byte
        final int file_atts_offset = 0x18;
        byte file_atts = link[file_atts_offset];
        byte is_dir_mask = (byte) 0x10;
        if ((file_atts & is_dir_mask) > 0) {
            is_dir = true;
        } else {
            is_dir = false;
        }

        // if the shell settings are present, skip them
        final int shell_offset = 0x4c;
        final byte has_shell_mask = (byte) 0x01;
        int shell_len = 0;
        if ((flags & has_shell_mask) > 0) {
            // the plus 2 accounts for the length marker itself
            shell_len = bytes2short(link, shell_offset) + 2;
        }

        // get to the file settings
        int file_start = 0x4c + shell_len;

        // get the local volume and local system values
        final int basename_offset_offset = 0x10;
        final int finalname_offset_offset = 0x18;
        int basename_offset = link[file_start + basename_offset_offset]
                + file_start;
        int finalname_offset = link[file_start + finalname_offset_offset]
                + file_start;
        String basename = getNullDelimitedString(link, basename_offset);
        String finalname = getNullDelimitedString(link, finalname_offset);
        real_file = basename + finalname;
    }

    private static String getNullDelimitedString(byte[] bytes, int off) {
        int len = 0;
        // count bytes until the null character (0)
        while (true) {
            if (bytes[off + len] == 0) {
                break;
            }
            len++;
        }
        return new String(bytes, off, len);
    }

    /*
     * convert two bytes into a short note, this is little endian because it's
     * for an Intel only OS.
     */
    private static int bytes2short(byte[] bytes, int off) {
        return ((bytes[off + 1] & 0xff) << 8) | (bytes[off] & 0xff);
    }
}
