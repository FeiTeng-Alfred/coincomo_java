/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//Class created by Roopa Dharap

package dialogs;

import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import core.COINCOMOConstants;
import core.COINCOMOConstants.EAF;
import core.COINCOMOConstants.RISK;
import core.COINCOMOConstants.Rating;
import static core.COINCOMOConstants.Rating.LO;
import static core.COINCOMOConstants.Rating.VLO;
import core.COINCOMOConstants.SF;
import core.COINCOMOSubComponent;
import database.COINCOMOSubComponentManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.util.HashMap;
import javax.swing.JDialog;
import main.COINCOMO;
import main.Icons;
/**
 *
 * @author Roopa
 */
public class RiskDialog extends JDialog implements ActionListener, FocusListener {
    
    private COINCOMO coincomo = null;
    private COINCOMOComponent component = null;
    private COINCOMOComponentParameters parameters = null;
    private COINCOMOSubComponent subComponent = null;
    
    public RiskDialog(COINCOMO coincomo, COINCOMOSubComponent subComponent) {
        super(coincomo);
        
        this.coincomo = coincomo;
        this.subComponent = subComponent;
        this.component = (COINCOMOComponent) this.subComponent.getParent();
        parameters = this.component.getParameters();
        
        initComponents();
        initializeRiskLevelMaps();
      renderTextFields();
        //COINCOMOSubComponentManager.calculateRisk(subComponent);

        
        this.setModal(true);
        this.setTitle("Risk Level - " + subComponent.getName());
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setSize(new Dimension(200,300));
        this.setResizable(false);
        this.setVisible(true);
    }
    
    /*
    private void renderTextFields() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        
        // Schedule
        double schedule = 0.0d;
        double scheduleCoefficient = 100.0d / 45.8d;
        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.RELY);
        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME);
        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PVOL);
        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TOOL);
        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.ACAP);
        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.APEX);
        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PCAP);
        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PLEX);
        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.LTEX);
        schedule += calculateEffortMultiplierProductEAFSF(EAF.SCED, SF.PMAT);

        lblScedRisk.setText(nf.format(schedule * scheduleCoefficient));
        
        
        // Product
        double product = 0.0d;
        double productCoefficient = 100.0d / 82.5d;
        product += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.ACAP);
        product += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.PCAP);
        product += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.ACAP);
        product += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.PCAP);
        product += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.TOOL);
        product += calculateEffortMultiplierProductEAFSF(EAF.RELY, SF.PMAT);
        product += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.CPLX);
        product += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.RELY);
        product += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME);
        product += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX);
        product += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX);
        lblProdRisk.setText(nf.format(product * productCoefficient));
        
        
        
        
        // Personnel
        double personnel = 0.0d;
        double personnelCoefficient = 100.0d / 138.7d;
        personnel += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.ACAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.ACAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.ACAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.ACAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.PCAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX);
        personnel += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.PCAP);
        personnel += (calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.PCAP));
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.PCAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.LTEX, EAF.PCAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.PVOL, EAF.LTEX);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.ACAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.APEX);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PCAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PLEX);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.LTEX);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.ACAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.PCAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.ACAP);
        personnel += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.PCAP);
        personnel += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.APEX);
        lblPersonnelRisk.setText(nf.format(personnel * personnelCoefficient));
        
        
        // Process
        double process = 0.0d;
        double processCofficient = 100.0d / 44.1d;
        process += calculateEffortMultiplierProductEAFSF(EAF.TOOL, SF.PMAT);
        process += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.TOOL);
        process += calculateEffortMultiplierProductEAFSF(EAF.TOOL, SF.PMAT);
        process += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.APEX);
        process += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.SCED);
        process += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.SITE);
        process += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TOOL);
        process += calculateEffortMultiplierProductEAFSF(EAF.SCED, SF.PMAT);
        process += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.TOOL);
        process += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.ACAP);
        process += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.ACAP);
        process += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.PCAP);
        process += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.PCAP);
        lblProcessRisk.setText(nf.format(process * processCofficient));
        
        
        // Platform
        double platform = 0.0d;
        double platformCoefficient = 100.0d / 46.5d;
        platform += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME);
        platform += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PVOL);
        platform += calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.ACAP);
        platform += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.ACAP);
        platform += calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.PCAP);
        platform += calculateEffortMultiplierProductEAFEAF(EAF.PVOL, EAF.PLEX);
        platform += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.TOOL);
        lblPlatformRisk.setText(nf.format(platform * platformCoefficient));
        
                
        // Reuse
        double reuse = 0.0d;
        double reuseCoefficient = 100.0d / 100.0d;
        reuse += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX);
        reuse += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX);
        lblReuseRisk.setText(nf.format(reuse * reuseCoefficient));
                
        
    }
*/
    
    private void renderTextFields() {
        
        //double risks[] = null;
        //COINCOMOSubComponentManager.calculateRisk(subComponent);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        
        // Schedule
//        double schedule = 0.0d;
//        double scheduleCoefficient = 100.0d / 45.8d;
//        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.RELY);
//        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME);
//        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PVOL);
//        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TOOL);
//        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.ACAP);
//        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.APEX);
//        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PCAP);
//        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PLEX);
//        schedule += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.LTEX);
//        schedule += calculateEffortMultiplierProductEAFSF(EAF.SCED, SF.PMAT);
        //Schedule_SUM.setText(nf.format(schedule));
        //Schedule_COEFFICIENT.setText(nf.format(scheduleCoefficient));
        lblScedRisk.setText(nf.format(COINCOMOSubComponentManager.calculateRisk(subComponent,RISK.SCHEDELE)));
        /*
        Schedule_SCED_RELY.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.RELY)));
        Schedule_SCED_TIME.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME)));
        Schedule_SCED_PVOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PVOL)));
        Schedule_SCED_TOOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TOOL)));
        Schedule_SCED_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.ACAP)));
        Schedule_SCED_APEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.APEX)));
        Schedule_SCED_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PCAP)));
        Schedule_SCED_PLEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PLEX)));
        Schedule_SCED_LTEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.LTEX)));
        Schedule_SCED_PMAT.setText(nf.format(calculateEffortMultiplierProductEAFSF(EAF.SCED, SF.PMAT)));
        */
        
        // Product
//        double product = 0.0d;
//        double productCoefficient = 100.0d / 82.5d;
//        product += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.ACAP);
//        product += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.PCAP);
//        product += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.ACAP);
//        product += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.PCAP);
//        product += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.TOOL);
//        product += calculateEffortMultiplierProductEAFSF(EAF.RELY, SF.PMAT);
//        product += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.CPLX);
//        product += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.RELY);
//        product += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME);
//        product += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX);
//        product += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX);
        //Product_SUM.setText(nf.format(product));
        //Product_COEFFICIENT.setText(nf.format(productCoefficient));
        lblProdRisk.setText(nf.format(COINCOMOSubComponentManager.calculateRisk(subComponent,RISK.PRODUCT)));
        
        /*
        Product_RELY_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.ACAP)));
        Product_RELY_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.PCAP)));
        Product_CPLX_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.ACAP)));
        Product_CPLX_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.PCAP)));
        Product_CPLX_TOOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.TOOL)));
        Product_RELY_PMAT.setText(nf.format(calculateEffortMultiplierProductEAFSF(EAF.RELY, SF.PMAT)));
        Product_SCED_CPLX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.CPLX)));
        Product_SCED_RELY.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.RELY)));
        Product_SCED_TIME.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME)));
        Product_RUSE_APEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX)));
        Product_RUSE_LTEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX)));
        */
        
        // Personnel
//        double personnel = 0.0d;
//        double personnelCoefficient = 100.0d / 138.7d;
//        personnel += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.ACAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.ACAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.ACAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.ACAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.PCAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX);
//        personnel += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.PCAP);
//        personnel += (calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.PCAP));
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.PCAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.LTEX, EAF.PCAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.PVOL, EAF.LTEX);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.ACAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.APEX);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PCAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PLEX);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.LTEX);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.ACAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.PCAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.ACAP);
//        personnel += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.PCAP);
//        personnel += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.APEX);
        //Personnel_SUM.setText(nf.format(personnel));
        //Personnel_COEFFICIENT.setText(nf.format(personnelCoefficient));
        lblPersonnelRisk.setText(nf.format(COINCOMOSubComponentManager.calculateRisk(subComponent,RISK.PERSONNEL)));
        /*
        Personnel_PMAT_ACAP.setText(nf.format(calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.ACAP)));
        Personnel_STOR_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.ACAP)));
        Personnel_TIME_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.ACAP)));
        Personnel_TOOL_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.ACAP)));
        Personnel_TOOL_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.PCAP)));
        Personnel_RUSE_APEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX)));
        Personnel_RUSE_LTEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX)));
        Personnel_PMAT_PCAP.setText(nf.format(calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.PCAP)));
        Personnel_STOR_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.PCAP)));
        Personnel_TIME_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.PCAP)));
        Personnel_LTEX_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.LTEX, EAF.PCAP)));
        Personnel_PVOL_PLEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.PVOL, EAF.LTEX)));
        Personnel_SCED_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.ACAP)));
        Personnel_SCED_APEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.APEX)));
        Personnel_SCED_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PCAP)));
        Personnel_SCED_PLEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PLEX)));
        Personnel_SCED_LTEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.LTEX)));
        Personnel_RELY_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.ACAP)));
        Personnel_RELY_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RELY, EAF.PCAP)));
        Personnel_CPLX_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.ACAP)));
        Personnel_CPLX_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.PCAP)));
        Personnel_TEAM_APEX.setText(nf.format(calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.APEX)));
        */
        // Process
//        double process = 0.0d;
//        double processCofficient = 100.0d / 44.1d;
//        process += calculateEffortMultiplierProductEAFSF(EAF.TOOL, SF.PMAT);
//        process += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.TOOL);
//        process += calculateEffortMultiplierProductEAFSF(EAF.TOOL, SF.PMAT);
//        process += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.APEX);
//        process += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.SCED);
//        process += calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.SITE);
//        process += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TOOL);
//        process += calculateEffortMultiplierProductEAFSF(EAF.SCED, SF.PMAT);
//        process += calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.TOOL);
//        process += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.ACAP);
//        process += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.ACAP);
//        process += calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.PCAP);
//        process += calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.PCAP);
        //Process_SUM.setText(nf.format(process));
        //Process_COEFFICIENT.setText(nf.format(processCofficient));
        lblProcessRisk.setText(nf.format(COINCOMOSubComponentManager.calculateRisk(subComponent,RISK.PROCESS)));        
        /*
        Process_TOOL_PMAT.setText(nf.format(calculateEffortMultiplierProductEAFSF(EAF.TOOL, SF.PMAT)));
        Process_TIME_TOOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.TOOL)));
        Process_TOOL_PMAT_2.setText(nf.format(calculateEffortMultiplierProductEAFSF(EAF.TOOL, SF.PMAT)));
        Process_TEAM_APEX.setText(nf.format(calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.APEX)));
        Process_TEAM_SCED.setText(nf.format(calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.SCED)));
        Process_TEAM_SITE.setText(nf.format(calculateEffortMultiplierProductSFEAF(SF.TEAM, EAF.SITE)));
        Process_SCED_TOOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TOOL)));
        Process_SCED_PMAT.setText(nf.format(calculateEffortMultiplierProductEAFSF(EAF.SCED, SF.PMAT)));
        Process_CPLX_TOOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.CPLX, EAF.TOOL)));
        Process_PMAT_ACAP.setText(nf.format(calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.ACAP)));
        Process_TOOL_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.ACAP)));
        Process_TOOL_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TOOL, EAF.PCAP)));
        Process_PMAT_PCAP.setText(nf.format(calculateEffortMultiplierProductSFEAF(SF.PMAT, EAF.PCAP)));
        */
        // Platform
//        double platform = 0.0d;
//        double platformCoefficient = 100.0d / 46.5d;
//        platform += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME);
//        platform += calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PVOL);
//        platform += calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.ACAP);
//        platform += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.ACAP);
//        platform += calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.PCAP);
//        platform += calculateEffortMultiplierProductEAFEAF(EAF.PVOL, EAF.PLEX);
//        platform += calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.TOOL);
        //Platform_SUM.setText(nf.format(platform));
        //Platform_COEFFICIENT.setText(nf.format(platformCoefficient));
        lblPlatformRisk.setText(nf.format(COINCOMOSubComponentManager.calculateRisk(subComponent,RISK.PLATFORM)));
        /*
        Platform_SCED_TIME.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME)));
        Platform_SCED_PVOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PVOL)));
        Platform_STOR_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.ACAP)));
        Platform_TIME_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.ACAP)));
        Platform_STOR_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.PCAP)));
        Platform_PVOL_PLEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.PVOL, EAF.PLEX)));
        Platform_TIME_TOOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.TOOL)));
        */
        // Reuse
//        double reuse = 0.0d;
//        double reuseCoefficient = 100.0d / 100.0d;
//        reuse += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX);
//        reuse += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX);
        //Reuse_SUM.setText(nf.format(reuse));
        //Reuse_COEFFICIENT.setText(nf.format(reuseCoefficient));
        lblReuseRisk.setText(nf.format(COINCOMOSubComponentManager.calculateRisk(subComponent,RISK.REUSE)));
                
        //Reuse_RUSE_APEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX)));
        //Reuse_RUSE_LTEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX)));
    }
    
    private HashMap<String, byte[][]> riskLevelMaps = new HashMap<String, byte[][]>(40, 0.9f);
    
//    private double calculateEffortMultiplierProductEAFEAF(EAF eaf1, EAF eaf2) {
//        double riskLevelMultiplier = 1.0d;
//        double eaf1Multiplier = 1.0d;
//        Rating eaf1Rating = Rating.NOM;
//        double eaf2Multiplier = 1.0d;
//        Rating eaf2Rating = Rating.NOM;
//        double effortMultiplierProduct = 1.0d;
//
//        double[][] eafWeights = parameters.getEAFWeights();
//        Rating[] eafRatings = subComponent.getEAFRatings();
//        Rating scedRating = component.getSCEDRating();
//        
//        double[][] sfWeights = parameters.getSFWeights();
//        Rating[] sfRatings = component.getSFRatings();
//        
//        if (eaf1 == EAF.SCED) {
//            eaf1Multiplier = COINCOMOSubComponentManager.calculateSCEDMultiplier(subComponent);
//            eaf1Rating = scedRating;
//        } else {
//            //Code changed by Roopa Dharap -------------------------
//            //changed per cocomo expert code - error implemented deliberately
//            if(eaf2 == EAF.SCED && eaf1 == EAF.CPLX)
//            {
//                eaf1Multiplier = COINCOMOSubComponentManager.calculateEAFMultiplier(EAF.RELY,subComponent);
//            }
//            else if(eaf2 == EAF.SCED && eaf1 == EAF.ACAP)
//            {
//                eaf1Multiplier = COINCOMOSubComponentManager.calculateEAFMultiplier(EAF.PVOL,subComponent);
//            }
//            else
//            {
//                eaf1Multiplier = COINCOMOSubComponentManager.calculateEAFMultiplier(eaf1,subComponent);
//            }
//            //----------------------------------------------------
//            eaf1Rating = eafRatings[eaf1.ordinal()];
//        }
//        
//        if (eaf2 == EAF.SCED) {
//            eaf2Multiplier = COINCOMOSubComponentManager.calculateSCEDMultiplier(subComponent);
//            eaf2Rating = scedRating;
//        } else {
//            //Code changed by Roopa Dharap -------------------------
//            //changed per cocomo expert code - error implemented deliberately
//            if(eaf1 == EAF.SCED && eaf2 == EAF.CPLX)
//            {
//                eaf2Multiplier = COINCOMOSubComponentManager.calculateEAFMultiplier(EAF.RELY,subComponent);
//            }
//            else if(eaf1 == EAF.SCED && eaf2 == EAF.ACAP)
//            {
//                eaf2Multiplier = COINCOMOSubComponentManager.calculateEAFMultiplier(EAF.PVOL,subComponent);
//            }
//            else
//            {
//                eaf2Multiplier = COINCOMOSubComponentManager.calculateEAFMultiplier(eaf2,subComponent);
//            }
//            //------------------------------------
//            eaf2Rating = eafRatings[eaf2.ordinal()];
//        }
//
//        
//        
//        riskLevelMultiplier = calculateRiskLevelMultiplierEAFEAF(eaf1, eaf1Rating, eaf2, eaf2Rating);
//        
//        effortMultiplierProduct = riskLevelMultiplier * eaf1Multiplier * eaf2Multiplier;
//        
//        return effortMultiplierProduct;
//    }
    
//    private double calculateEffortMultiplierProductEAFSF(EAF eaf, SF sf) {
//        double riskLevelMultiplier = 1.0d;
//        double eafMultiplier = 1.0d;
//        Rating eafRating = Rating.NOM;
//        double sfMultiplier = 1.0d;
//        Rating sfRating = Rating.NOM;
//        double effortMultiplierProduct = 1.0d;
//
//        double[][] eafWeights = parameters.getEAFWeights();
//        Rating[] eafRatings = subComponent.getEAFRatings();
//        Rating scedRating = component.getSCEDRating();
//        
//        double[][] sfWeights = parameters.getSFWeights();
//        Rating[] sfRatings = component.getSFRatings();
//        
//        if (eaf == EAF.SCED) {
//            eafMultiplier = COINCOMOSubComponentManager.calculateSCEDMultiplier(subComponent);
//            eafRating = scedRating;
//        } else {
//            eafMultiplier = COINCOMOSubComponentManager.calculateEAFMultiplier(eaf,subComponent);
//            eafRating = eafRatings[eaf.ordinal()];
//        }
//        
//        sfMultiplier = COINCOMOSubComponentManager.calculateSFMultiplier(sf,subComponent);
//        sfRating = sfRatings[sf.ordinal()];
//        
//        riskLevelMultiplier = calculateRiskLevelMultiplierEAFSF(eaf, eafRating, sf, sfRating);
//        
//        effortMultiplierProduct = riskLevelMultiplier * eafMultiplier * sfMultiplier;
//        
//        return effortMultiplierProduct;
//    }
//
//    private double calculateEffortMultiplierProductSFEAF(SF sf, EAF eaf) {
//        double riskLevelMultiplier = 1.0d;
//        double sfMultiplier = 1.0d;
//        Rating sfRating = Rating.NOM;
//        double eafMultiplier = 1.0d;
//        Rating eafRating = Rating.NOM;
//
//        double effortMultiplierProduct = 1.0d;
//
//        double[][] eafWeights = parameters.getEAFWeights();
//        Rating[] eafRatings = subComponent.getEAFRatings();
//        Rating scedRating = component.getSCEDRating();
//        
//        double[][] sfWeights = parameters.getSFWeights();
//        Rating[] sfRatings = component.getSFRatings();
//
//        sfMultiplier = COINCOMOSubComponentManager.calculateSFMultiplier(sf,subComponent);
//        sfRating = sfRatings[sf.ordinal()];
//
//        if (eaf == EAF.SCED) {
//            eafMultiplier = COINCOMOSubComponentManager.calculateSCEDMultiplier(subComponent);
//            eafRating = scedRating;
//        } else {
//            eafMultiplier = COINCOMOSubComponentManager.calculateEAFMultiplier(eaf,subComponent);
//            eafRating = eafRatings[eaf.ordinal()];
//        }
//        
//        riskLevelMultiplier = calculateRiskLevelMultiplierSFEAF(sf, sfRating, eaf, eafRating);
//        
//        effortMultiplierProduct = riskLevelMultiplier * sfMultiplier * eafMultiplier;
//        
//        return effortMultiplierProduct;
//    }
//    
//    private double calculateRiskLevelMultiplierEAFEAF(EAF eaf1, Rating eafRating1, EAF eaf2, Rating eafRating2) {
//        if (riskLevelMaps.containsKey(eaf1.toString() + eaf2.toString())) {
//            byte[][] riskLevelMap = riskLevelMaps.get(eaf1.toString() + eaf2.toString());
//            
//            return riskLevelMap[eafRating1.ordinal()][eafRating2.ordinal()];
//        } else {
//            return 0.0d;
//        }
//    }
//    
//    private double calculateRiskLevelMultiplierEAFSF(EAF eaf, Rating eafRating, SF sf, Rating sfRating) {
//        if (riskLevelMaps.containsKey(eaf.toString() + sf.toString())) {
//            byte[][] riskLevelMap = riskLevelMaps.get(eaf.toString() + sf.toString());
//            
//            return riskLevelMap[eafRating.ordinal()][sfRating.ordinal()];
//        } else {
//            return 0.0d;
//        }
//    }
//    
//    private double calculateRiskLevelMultiplierSFEAF(SF sf, Rating sfRating, EAF eaf, Rating eafRating) {
//        if (riskLevelMaps.containsKey(sf.toString() + eaf.toString())) {
//            byte[][] riskLevelMap = riskLevelMaps.get(sf.toString() + eaf.toString());
//            
//            return riskLevelMap[sfRating.ordinal()][eafRating.ordinal()];
//        } else {
//            return 0.0d;
//        }
//    }

//    private double calculateSCEDMultiplier() {
//        double[][] eafWeights = parameters.getEAFWeights();
//        return eafWeights[EAF.SCED.ordinal()][component.getSCEDRating().ordinal()];
//    }

//    private double calculateSFMultiplier(SF sf) {
//        double[][] sfWeights = parameters.getSFWeights();
//        Rating[] sfRatings = component.getSFRatings();
//        /*
//        if (subComponent.getSLOC() == 0) {
//            return 0.0d;
//        } else {
//            return Math.pow(subComponent.getSLOC(), (0.01d * sfWeights[sf.ordinal()][sfRatings[sf.ordinal()].ordinal()]))
//                    / Math.pow(subComponent.getSLOC(), (0.01d * 3.0d));
//        }
//        */
//        switch (sfRatings[sf.ordinal()]) {
//            case VLO:
//                return Math.pow(subComponent.getSLOC() / 1000.0d, 0.02d);
//            case LO:
//                return Math.pow(subComponent.getSLOC() / 1000.0d, 0.01d);
//            default:
//                //Code changed by Roopa Dharap -------------------------
//                //If SF is PMAT, it should return 1
//                if(sf == sf.PMAT)
//                    return 1.0d;
//                else
//                    return 0.0d;
//                //----------------------------------------------
//        }
//    }

//    private double calculateEAFMultiplier(EAF eaf) {
//        double[][] eafWeights = parameters.getEAFWeights();
//        Rating[] eafRatings = subComponent.getEAFRatings();
//        return eafWeights[eaf.ordinal()][eafRatings[eaf.ordinal()].ordinal()];
//    }
    
    /**
     * 
     */
    
    private void initializeRiskLevelMaps() {
        byte[][] riskLevelMap = null;
        
        // SCED vs RELY map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.HI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.RELY.toString(), riskLevelMap);
        
        // SCED vs CPLX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.HI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.CPLX.toString(), riskLevelMap);
        
        // SCED vs TIME map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.HI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.XHI.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.TIME.toString(), riskLevelMap);
        
        // SCED vs PVOL map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.HI.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VHI.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.PVOL.toString(), riskLevelMap);
        
        // SCED vs TOOL map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.TOOL.toString(), riskLevelMap);
        
        // SCED vs PLEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.PLEX.toString(), riskLevelMap);
        
        // SCED vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);
        
        // SCED vs APEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.APEX.toString(), riskLevelMap);
        
        // SCED vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);
        
        // SCED vs LTEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.EAF.LTEX.toString(), riskLevelMap);
        
        // SCED vs PMAT map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.SCED.toString() + COINCOMOConstants.SF.PMAT.toString(), riskLevelMap);
        
        //==========
        
        // RELY vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RELY.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);
        
        // RELY vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RELY.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);
        
        // CPLX vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.CPLX.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);
        
        // CPLX vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.CPLX.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);
        
        // CPLX vs TOOL map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.CPLX.toString() + COINCOMOConstants.EAF.TOOL.toString(), riskLevelMap);
        
        // RELY vs PMAT map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RELY.toString() + COINCOMOConstants.SF.PMAT.toString(), riskLevelMap);
        
        // PMAT vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.PMAT.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);
        
        // STOR vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.STOR.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);
        
        // TIME vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TIME.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);
        
        //Code changed by Roopa Dharap -------------------------
        //Mapping for TOOL_ACAP and TOOL_PCAP were wrong. Changed now.
        
        // TOOL vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TOOL.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);
        
        // TOOL vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.STOR.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);
        
        //==========
        
        // RUSE vs APEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RUSE.toString() + COINCOMOConstants.EAF.APEX.toString(), riskLevelMap);
        
        // RUSE vs LTEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.RUSE.toString() + COINCOMOConstants.EAF.LTEX.toString(), riskLevelMap);
        
        // PMAT vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.PMAT.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);
        
        // STOR vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.STOR.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);
        
        // TIME vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TIME.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);
        
        // LTEX vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 4;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.NOM.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.NOM.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.LTEX.toString() + COINCOMOConstants.EAF.PCAP.toString(), riskLevelMap);
        
        // PVOL vs PLEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.HI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.PVOL.toString() + COINCOMOConstants.EAF.PLEX.toString(), riskLevelMap);
        
        // TOOL vs PMAT map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TOOL.toString() + COINCOMOConstants.SF.PMAT.toString(), riskLevelMap);
        
        // TIME vs TOOL map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.XHI.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TIME.toString() + COINCOMOConstants.EAF.TOOL.toString(), riskLevelMap);
        
        // TEAM vs APEX map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.TEAM.toString() + COINCOMOConstants.EAF.APEX.toString(), riskLevelMap);
        
        // TEAM vs SCED map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.TEAM.toString() + COINCOMOConstants.EAF.SCED.toString(), riskLevelMap);
        
        // TEAM vs SITE map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.SF.TEAM.toString() + COINCOMOConstants.EAF.SITE.toString(), riskLevelMap);
    }
    
    
     /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lblScedRisk = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblProdRisk = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblPlatformRisk = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblPersonnelRisk = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblProcessRisk = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        lblReuseRisk = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        //setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Schedule Risk");
        jLabel1.setFont(new Font("arial", 1, 11));

        lblScedRisk.setText("lblScedRisk");
        lblScedRisk.setFont(new Font("arial", 1, 11));

        jLabel3.setText("Product Risk");
        jLabel3.setFont(new Font("arial", 1, 11));

        lblProdRisk.setText("lblProdRisk");
        lblProdRisk.setFont(new Font("arial", 1, 11));

        jLabel5.setText("Platform Risk");
        jLabel5.setFont(new Font("arial", 1, 11));

        lblPlatformRisk.setText("lblPlatformRisk");
        lblPlatformRisk.setFont(new Font("arial", 1, 11));

        jLabel7.setText("Personnel Risk");
        jLabel7.setFont(new Font("arial", 1, 11));

        lblPersonnelRisk.setText("lblPersonnelRisk");
        lblPersonnelRisk.setFont(new Font("arial", 1, 11));

        jLabel9.setText("Process Risk");
        jLabel9.setFont(new Font("arial", 1, 11));

        lblProcessRisk.setText("lblProcessRisk");
        lblProcessRisk.setFont(new Font("arial", 1, 11));

        jLabel11.setText("Reuse Risk");
        jLabel11.setFont(new Font("arial", 1, 11));

        lblReuseRisk.setText("lblReuseRisk");
        lblReuseRisk.setFont(new Font("arial", 1, 11));

        jButton1.setText("Close");
        jButton1.addActionListener(this);
        jButton1.setIcon(Icons.CLOSE_ICON);
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3)
                                .addComponent(jLabel1)
                                .addComponent(jLabel5))
                            .addGap(4, 4, 4))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel9)
                                .addComponent(jLabel11))
                            .addGap(12, 12, 12)))
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblScedRisk)
                    .addComponent(lblProdRisk)
                    .addComponent(lblPlatformRisk)
                    .addComponent(lblPersonnelRisk)
                    .addComponent(lblProcessRisk)
                    .addComponent(lblReuseRisk))
                .addGap(30, 30, 30))
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(70, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60))
            /*.addGroup(layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(jButton1)
                .addContainerGap(151, Short.MAX_VALUE))*/
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap( 30,  30,  30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblScedRisk)
                    .addComponent(jLabel1))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProdRisk)
                    .addComponent(jLabel3))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPlatformRisk)
                    .addComponent(jLabel5))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPersonnelRisk)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(lblProcessRisk))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReuseRisk)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(30, 30, 30)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>
    
    // Variables declaration - do not modify
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblProcessRisk;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel lblReuseRisk;
    private javax.swing.JLabel lblScedRisk;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblProdRisk;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblPlatformRisk;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel lblPersonnelRisk;
    private javax.swing.JLabel jLabel9;
    // End of variables declaration

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jButton1) {
            this.dispose();
        }
        
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void focusGained(FocusEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void focusLost(FocusEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
