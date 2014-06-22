/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package main;

import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.filechooser.FileView;

/**
 * Implementation details taken from
 * http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
 *
 * This class extends FileView in order to display custom icons for specific
 * folder types and file types
 *
 * This feature is linked to defect #89 "icon for shortcuts".
 *
 * As of R246 implementation only default folder and shortcut icons are
 * implemented, as no icon available for XML/CVS file types from the icons
 * package used by COINCOMO.
 *
 * @author Larry
 */
public class IconFileView extends FileView {

    private Icon folder_icon = Icons.FOLDER_ICON;
    private Icon folder_shortcut_icon = Icons.FOLDER_SHORTCUT_ICON;
    private Icon file_icon;
    private Icon file_shortcut_icon;

    public IconFileView(String[] validExt, boolean export) {
        super();

        if (export) {
            if (validExt[0].equals("csv")) {
                file_icon = Icons.EXPORT_CVS_ICON;
            } else if (validExt[0].equals("html")) {
                file_icon = Icons.EXPORT_HTML_ICON;
            } else if (validExt[0].equals("xml")) {
                file_icon = Icons.EXPORT_XML_ICON;
            } else if (validExt[0].equals("cet")) {
                //file_icon = Icons.FILE_COINCOMO_ICON;
                file_icon = Icons.FILE_ICON;
            }

            file_shortcut_icon = Icons.EXPORT_SHORTCUT_ICON;
        } else {
            file_icon = Icons.FILE_ICON;
            file_shortcut_icon = Icons.FILE_SHORTCUT_ICON;
        }
    }

    public String getDescription(File f) {
        return null; //let the L&F FileView figure this out
    }

    public Icon getIcon(File f) {
        boolean isDirectory = f.isDirectory();
        String extension = Utility.getExtension(f);
        Icon icon = null;

        if (isDirectory) {
            if (extension != null) {
                // Windows solution, shortcuts all end with .lnk extension
                if (extension.equals("lnk")) {
                    icon = Icons.FOLDER_SHORTCUT_ICON;
                } else {
                    icon = Icons.FOLDER_ICON;
                }
            } else {
                // *nix solution, checking against symbolic links
                try {
                    if (f.getAbsolutePath().equals(f.getCanonicalPath())) {
                        icon = Icons.FOLDER_ICON;
                    } else {
                        icon = Icons.FOLDER_SHORTCUT_ICON;
                    }
                } catch (IOException e) {
                    // Do nothing
                } finally {
                    if (icon == null) {
                        icon = Icons.FOLDER_ICON;
                    }
                }
            }
        } else {
            if (extension != null) {
                // Windows solution, shortcuts all end with .lnk extension
                if (extension.equals("lnk")) {
                    icon = file_shortcut_icon;
                } else {
                    icon = file_icon;
                }

                if (extension.equals("xml")) {
                    icon = Icons.XML_ICON;
                } else if (extension.equals("html")) {
                    icon = Icons.HTML_ICON;
                } else if (extension.equals("csv")) {
                    icon = Icons.CSV_ICON;
                } else if (extension.equals("cet")) {
                    icon = Icons.COINCOMO_FILE_ICON;
                }
            } else {
                // *nix solution, checking against symbolic links
                try {
                    if (f.getAbsolutePath().equals(f.getCanonicalPath())) {
                        icon = file_icon;
                    } else {
                        icon = file_shortcut_icon;
                    }
                } catch (IOException e) {
                    // Do nothing
                } finally {
                    if (icon == null) {
                        icon = file_icon;
                    }
                }
            }
        }

        return icon;
    }

    public String getName(File f) {
        return null; //let the L&F FileView figure this out
    }

    public String getTypeDescription(File f) {
        return null; //let the L&F FileView figure this out
    }

    public Boolean isTraversable(File f) {
        return null; //let the L&F FileView figure this out
    }
}