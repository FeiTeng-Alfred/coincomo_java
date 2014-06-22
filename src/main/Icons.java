/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 *
 * CREDITS
 * Icons being used by COINCOMO are part of the SILK ICONS package from http://www.famfamfam.com/ .
 * License under Creative Commons Attribution 2.5 License, http://creativecommons.org/licenses/by/2.5/ .
 * Original creator Mark James, a web developer from Birmingham, UK.
 */
package main;

import javax.swing.ImageIcon;

public class Icons {

    public static final ImageIcon CONNECT_ICON = getIcon("database_connect.png");
    public static final ImageIcon DISCONNECT_ICON = getIcon("disconnect.png");
    public static final ImageIcon NEW_ICON_DESKTOP = getIcon("page_add.png");
    public static final ImageIcon NEW_ICON_DATABASE = getIcon("database_add.png");
    public static final ImageIcon VIEW_ICON = getIcon("folder_database.png");
    public static final ImageIcon IMPORT_ICON = getIcon("database_go.png");
    public static final ImageIcon SYNCHRONIZE_ICON = getIcon("database_lightning.png");
    public static final ImageIcon OPEN_ICON = getIcon("folder_page.png");
    public static final ImageIcon SAVE_ICON = getIcon("page_save.png");
    public static final ImageIcon SAVE_AS_ICON = getIcon("page_edit.png");
    public static final ImageIcon EXPORT_ICON = getIcon("page_white_stack.png");
    public static final ImageIcon EXPORT_CVS_ICON = getIcon("page_white_c.png");
    public static final ImageIcon EXPORT_HTML_ICON = getIcon("page_white_world.png");
    public static final ImageIcon EXPORT_XML_ICON = getIcon("page_white_code.png");
    public static final ImageIcon EXPORT_SHORTCUT_ICON = getIcon("page_white_link.png");
    public static final ImageIcon EXIT_ICON = getIcon("door_open.png");
    public static final ImageIcon CLOSE_ICON = getIcon("cancel.png");
    public static final ImageIcon LOAD_PROJECT_ICON = getIcon("database_refresh.png");
    public static final ImageIcon DELETE_PROJECT_ICON = getIcon("database_delete.png");
    public static final ImageIcon CUT_ICON = getIcon("cut.png");
    public static final ImageIcon COPY_ICON = getIcon("page_copy.png");
    public static final ImageIcon EAF_ICON = getIcon("table.png");
    public static final ImageIcon SCALE_FACTOR_ICON = getIcon("page_white_excel.png");
    public static final ImageIcon EQUATION_EDITOR_ICON = getIcon("sum.png");
    public static final ImageIcon FUNCTION_POINTS_ICON = getIcon("chart_line.png");
    public static final ImageIcon PERSON_MONTH_ICON = getIcon("time.png");
    public static final ImageIcon ABOUT_ICON = getIcon("award_star_gold_2.png");
    public static final ImageIcon MANUAL_ICON = getIcon("book.png");
    public static final ImageIcon COLLAPSE_ICON = getIcon("delete.png");
    public static final ImageIcon EXPAND_ICON = getIcon("add.png");
    public static final ImageIcon SYSTEM_ICON = getIcon("box.png");
    public static final ImageIcon SUBSYSTEM_ICON = getIcon("package.png");
    public static final ImageIcon COMPONENT_ICON = getIcon("brick.png");
    public static final ImageIcon SUBCOMPONENT_ICON = getIcon("cog.png");
    public static final ImageIcon ADD_SUBSYSTEM_ICON = getIcon("package_add.png");
    public static final ImageIcon DELETE_SUBSYSTEM_ICON = getIcon("package_delete.png");
    public static final ImageIcon ADD_COMPONENT_ICON = getIcon("brick_add.png");
    public static final ImageIcon DELETE_COMPONENT_ICON = getIcon("brick_delete.png");
    public static final ImageIcon ADD_SUBCOMPONENT_ICON = getIcon("add.png");
    public static final ImageIcon DELETE_SUBCOMPONENT_ICON = getIcon("delete.png");
    public static final ImageIcon ZOOM_IN_ICON = getIcon("zoom_in.png");
    public static final ImageIcon ZOOM_OUT_ICON = getIcon("zoom_out.png");
    public static final ImageIcon RESET_ICON = getIcon("book_open.png");
    public static final ImageIcon RENAME_ICON = getIcon("folder_edit.png");
    // Icons for file chooser dialog, to differentiate between folders and shortcuts, files and shorcuts
    public static final ImageIcon FOLDER_ICON = getIcon("folder.png");
    public static final ImageIcon FOLDER_SHORTCUT_ICON = getIcon("folder_link.png");
    public static final ImageIcon FILE_ICON = getIcon("page.png");
    public static final ImageIcon FILE_SHORTCUT_ICON = getIcon("page_link.png");
    // Icons for the operation mode (desktop and database)
    public static final ImageIcon MODE_ICON_DESKTOP = getIcon("page_gear.png");
    public static final ImageIcon MODE_ICON_DATABASE = getIcon("database_gear.png");
    // Icons for COINCOMO file type (CET extension)
    public static final ImageIcon COINCOMO_FILE_ICON = getCoincomoIcon("coincomo16x16.png");
    public static final ImageIcon COINCOMO_PROGRAM_ICON = getCoincomoIcon("coincomo48x48.png");
    // Icons for other file types
    public static final ImageIcon XML_ICON = getIcon("page_white_code.png");
    public static final ImageIcon HTML_ICON = getIcon("page_white_world.png");
    public static final ImageIcon CSV_ICON = getIcon("page_white_c.png");

    static ImageIcon getIcon(String name) {
        return new ImageIcon(Icons.class.getResource("/com/famfamfam/silk/" + name));
    }

    static ImageIcon getCoincomoIcon(String name) {
        return new ImageIcon(Icons.class.getResource("/coincomo/" + name));
    }
}