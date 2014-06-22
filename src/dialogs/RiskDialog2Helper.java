/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import core.COINCOMOComponent;
import core.COINCOMOComponentParameters;
import core.COINCOMOConstants;
import core.COINCOMOConstants.EAF;
import core.COINCOMOConstants.Rating;
import core.COINCOMOConstants.SF;
import core.COINCOMOSubComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.util.HashMap;
import javax.swing.JDialog;
import main.COINCOMO;

/**
 *
 * @author Larry
 */
public class RiskDialog2Helper extends JDialog implements ActionListener, FocusListener {

    private COINCOMO coincomo = null;
    private COINCOMOComponent component = null;
    private COINCOMOComponentParameters parameters = null;
    private COINCOMOSubComponent subComponent = null;
    
    /**
     * Creates new form RiskDialog
     */
    public RiskDialog2Helper(COINCOMO coincomo, COINCOMOSubComponent subComponent) {
        super(coincomo);
        
        this.coincomo = coincomo;
        this.subComponent = subComponent;
        this.component = (COINCOMOComponent) this.subComponent.getParent();
        parameters = this.component.getParameters();
        
        initComponents();
        initializeRiskLevelMaps();
        renderTextFields();
        
        this.setModal(true);
        this.setTitle("Risk - " + subComponent.getName());
        this.setVisible(true);
    }

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
        Schedule_SUM.setText(nf.format(schedule));
        Schedule_COEFFICIENT.setText(nf.format(scheduleCoefficient));
        Schedule_VALUE.setText(nf.format(schedule * scheduleCoefficient));
        
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
        Product_SUM.setText(nf.format(product));
        Product_COEFFICIENT.setText(nf.format(productCoefficient));
        Product_VALUE.setText(nf.format(product * productCoefficient));
        
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
        Personnel_SUM.setText(nf.format(personnel));
        Personnel_COEFFICIENT.setText(nf.format(personnelCoefficient));
        Personnel_VALUE.setText(nf.format(personnel * personnelCoefficient));
        
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
        Process_SUM.setText(nf.format(process));
        Process_COEFFICIENT.setText(nf.format(processCofficient));
        Process_VALUE.setText(nf.format(process * processCofficient));        
        
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
        Platform_SUM.setText(nf.format(platform));
        Platform_COEFFICIENT.setText(nf.format(platformCoefficient));
        Platform_VALUE.setText(nf.format(platform * platformCoefficient));
        
        Platform_SCED_TIME.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.TIME)));
        Platform_SCED_PVOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.SCED, EAF.PVOL)));
        Platform_STOR_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.ACAP)));
        Platform_TIME_ACAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.ACAP)));
        Platform_STOR_PCAP.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.STOR, EAF.PCAP)));
        Platform_PVOL_PLEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.PVOL, EAF.PLEX)));
        Platform_TIME_TOOL.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.TIME, EAF.TOOL)));
        
        // Reuse
        double reuse = 0.0d;
        double reuseCoefficient = 100.0d / 100.0d;
        reuse += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX);
        reuse += calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX);
        Reuse_SUM.setText(nf.format(reuse));
        Reuse_COEFFICIENT.setText(nf.format(reuseCoefficient));
        Reuse_VALUE.setText(nf.format(reuse * reuseCoefficient));
                
        Reuse_RUSE_APEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.APEX)));
        Reuse_RUSE_LTEX.setText(nf.format(calculateEffortMultiplierProductEAFEAF(EAF.RUSE, EAF.LTEX)));
    }

    private HashMap<String, byte[][]> riskLevelMaps = new HashMap<String, byte[][]>(40, 0.9f);
    
    private double calculateEffortMultiplierProductEAFEAF(EAF eaf1, EAF eaf2) {
        double riskLevelMultiplier = 1.0d;
        double eaf1Multiplier = 1.0d;
        Rating eaf1Rating = Rating.NOM;
        double eaf2Multiplier = 1.0d;
        Rating eaf2Rating = Rating.NOM;
        double effortMultiplierProduct = 1.0d;

        double[][] eafWeights = parameters.getEAFWeights();
        Rating[] eafRatings = subComponent.getEAFRatings();
        Rating scedRating = component.getSCEDRating();
        
        double[][] sfWeights = parameters.getSFWeights();
        Rating[] sfRatings = component.getSFRatings();
        
        if (eaf1 == EAF.SCED) {
            eaf1Multiplier = calculateSCEDMultiplier();
            eaf1Rating = scedRating;
        } else {
            eaf1Multiplier = calculateEAFMultiplier(eaf1);
            eaf1Rating = eafRatings[eaf1.ordinal()];
        }
        
        if (eaf2 == EAF.SCED) {
            eaf2Multiplier = calculateSCEDMultiplier();
            eaf2Rating = scedRating;
        } else {
            eaf2Multiplier = calculateEAFMultiplier(eaf2);
            eaf2Rating = eafRatings[eaf2.ordinal()];
        }

        riskLevelMultiplier = calculateRiskLevelMultiplierEAFEAF(eaf1, eaf1Rating, eaf2, eaf2Rating);
        
        effortMultiplierProduct = riskLevelMultiplier * eaf1Multiplier * eaf2Multiplier;
        
        return effortMultiplierProduct;
    }
    
    private double calculateEffortMultiplierProductEAFSF(EAF eaf, SF sf) {
        double riskLevelMultiplier = 1.0d;
        double eafMultiplier = 1.0d;
        Rating eafRating = Rating.NOM;
        double sfMultiplier = 1.0d;
        Rating sfRating = Rating.NOM;
        double effortMultiplierProduct = 1.0d;

        double[][] eafWeights = parameters.getEAFWeights();
        Rating[] eafRatings = subComponent.getEAFRatings();
        Rating scedRating = component.getSCEDRating();
        
        double[][] sfWeights = parameters.getSFWeights();
        Rating[] sfRatings = component.getSFRatings();
        
        if (eaf == EAF.SCED) {
            eafMultiplier = calculateSCEDMultiplier();
            eafRating = scedRating;
        } else {
            eafMultiplier = calculateEAFMultiplier(eaf);
            eafRating = eafRatings[eaf.ordinal()];
        }
        
        sfMultiplier = calculateSFMultiplier(sf);
        sfRating = sfRatings[sf.ordinal()];

        riskLevelMultiplier = calculateRiskLevelMultiplierEAFSF(eaf, eafRating, sf, sfRating);
        
        effortMultiplierProduct = riskLevelMultiplier * eafMultiplier * sfMultiplier;
        
        return effortMultiplierProduct;
    }

    private double calculateEffortMultiplierProductSFEAF(SF sf, EAF eaf) {
        double riskLevelMultiplier = 1.0d;
        double sfMultiplier = 1.0d;
        Rating sfRating = Rating.NOM;
        double eafMultiplier = 1.0d;
        Rating eafRating = Rating.NOM;

        double effortMultiplierProduct = 1.0d;

        double[][] eafWeights = parameters.getEAFWeights();
        Rating[] eafRatings = subComponent.getEAFRatings();
        Rating scedRating = component.getSCEDRating();
        
        double[][] sfWeights = parameters.getSFWeights();
        Rating[] sfRatings = component.getSFRatings();

        sfMultiplier = calculateSFMultiplier(sf);
        sfRating = sfRatings[sf.ordinal()];

        if (eaf == EAF.SCED) {
            eafMultiplier = calculateSCEDMultiplier();
            eafRating = scedRating;
        } else {
            eafMultiplier = calculateEAFMultiplier(eaf);
            eafRating = eafRatings[eaf.ordinal()];
        }
        
        riskLevelMultiplier = calculateRiskLevelMultiplierSFEAF(sf, sfRating, eaf, eafRating);
        
        effortMultiplierProduct = riskLevelMultiplier * sfMultiplier * eafMultiplier;
        
        return effortMultiplierProduct;
    }
    
    private double calculateRiskLevelMultiplierEAFEAF(EAF eaf1, Rating eafRating1, EAF eaf2, Rating eafRating2) {
        if (riskLevelMaps.containsKey(eaf1.toString() + eaf2.toString())) {
            byte[][] riskLevelMap = riskLevelMaps.get(eaf1.toString() + eaf2.toString());
            
            return riskLevelMap[eafRating1.ordinal()][eafRating2.ordinal()];
        } else {
            return 0.0d;
        }
    }
    
    private double calculateRiskLevelMultiplierEAFSF(EAF eaf, Rating eafRating, SF sf, Rating sfRating) {
        if (riskLevelMaps.containsKey(eaf.toString() + sf.toString())) {
            byte[][] riskLevelMap = riskLevelMaps.get(eaf.toString() + sf.toString());
            
            return riskLevelMap[eafRating.ordinal()][sfRating.ordinal()];
        } else {
            return 0.0d;
        }
    }
    
    private double calculateRiskLevelMultiplierSFEAF(SF sf, Rating sfRating, EAF eaf, Rating eafRating) {
        if (riskLevelMaps.containsKey(sf.toString() + eaf.toString())) {
            byte[][] riskLevelMap = riskLevelMaps.get(sf.toString() + eaf.toString());
            
            return riskLevelMap[sfRating.ordinal()][eafRating.ordinal()];
        } else {
            return 0.0d;
        }
    }

    private double calculateSCEDMultiplier() {
        double[][] eafWeights = parameters.getEAFWeights();
        return eafWeights[EAF.SCED.ordinal()][component.getSCEDRating().ordinal()];
    }

    private double calculateSFMultiplier(SF sf) {
        double[][] sfWeights = parameters.getSFWeights();
        Rating[] sfRatings = component.getSFRatings();
        /*
        if (subComponent.getSLOC() == 0) {
            return 0.0d;
        } else {
            return Math.pow(subComponent.getSLOC(), (0.01d * sfWeights[sf.ordinal()][sfRatings[sf.ordinal()].ordinal()]))
                    / Math.pow(subComponent.getSLOC(), (0.01d * 3.0d));
        }
        */
        switch (sfRatings[sf.ordinal()]) {
            case VLO:
                return Math.pow(subComponent.getSLOC() / 1000.0d, 0.02d);
            case LO:
                return Math.pow(subComponent.getSLOC() / 1000.0d, 0.01d);
            default:
                return 0.0d;
        }
    }

    private double calculateEAFMultiplier(EAF eaf) {
        double[][] eafWeights = parameters.getEAFWeights();
        Rating[] eafRatings = subComponent.getEAFRatings();
        return eafWeights[eaf.ordinal()][eafRatings[eaf.ordinal()].ordinal()];
    }
    
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
        
        // TOOL vs ACAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
        riskLevelMap[COINCOMOConstants.Rating.LO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMaps.put(COINCOMOConstants.EAF.TOOL.toString() + COINCOMOConstants.EAF.ACAP.toString(), riskLevelMap);
        
        // TOOL vs PCAP map
        riskLevelMap = new byte[COINCOMOConstants.Ratings.length][COINCOMOConstants.Ratings.length];
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.VLO.ordinal()] = 1;
        riskLevelMap[COINCOMOConstants.Rating.VLO.ordinal()][COINCOMOConstants.Rating.LO.ordinal()] = 2;
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
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        Schedule_SCED_RELY = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        Schedule_SCED_TIME = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Schedule_SCED_PVOL = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        Schedule_SCED_TOOL = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        Schedule_SCED_ACAP = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        Schedule_SCED_APEX = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        Schedule_SCED_PCAP = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        Schedule_SCED_PLEX = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        Schedule_SCED_LTEX = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        Schedule_SCED_PMAT = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        Product_RELY_ACAP = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        Product_RELY_PCAP = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        Product_CPLX_ACAP = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        Product_CPLX_PCAP = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        Product_CPLX_TOOL = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        Product_RELY_PMAT = new javax.swing.JTextField();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        Product_SCED_CPLX = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        Product_SCED_RELY = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        Product_SCED_TIME = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        Product_RUSE_APEX = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        Product_RUSE_LTEX = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        Personnel_PMAT_ACAP = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        Personnel_STOR_ACAP = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        Personnel_TIME_ACAP = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        Personnel_TOOL_ACAP = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        Personnel_TOOL_PCAP = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        Personnel_RUSE_APEX = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        Personnel_RUSE_LTEX = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        Personnel_PMAT_PCAP = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        Personnel_STOR_PCAP = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        Personnel_TIME_PCAP = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        Personnel_LTEX_PCAP = new javax.swing.JTextField();
        jLabel71 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        Personnel_PVOL_PLEX = new javax.swing.JTextField();
        jLabel85 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        Personnel_SCED_ACAP = new javax.swing.JTextField();
        jLabel84 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        Personnel_SCED_APEX = new javax.swing.JTextField();
        jLabel72 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        Personnel_SCED_PCAP = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        Personnel_SCED_PLEX = new javax.swing.JTextField();
        jLabel88 = new javax.swing.JLabel();
        jLabel87 = new javax.swing.JLabel();
        Personnel_SCED_LTEX = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        Personnel_RELY_ACAP = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        Personnel_RELY_PCAP = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        Personnel_CPLX_ACAP = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        Personnel_CPLX_PCAP = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        Personnel_TEAM_APEX = new javax.swing.JTextField();
        jLabel93 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        Process_TOOL_PMAT = new javax.swing.JTextField();
        jLabel111 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        Process_TIME_TOOL = new javax.swing.JTextField();
        jLabel110 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        Process_TOOL_PMAT_2 = new javax.swing.JTextField();
        jLabel94 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        Process_TEAM_APEX = new javax.swing.JTextField();
        jLabel95 = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        Process_TEAM_SCED = new javax.swing.JTextField();
        jLabel114 = new javax.swing.JLabel();
        jLabel113 = new javax.swing.JLabel();
        Process_TEAM_SITE = new javax.swing.JTextField();
        jLabel109 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        Process_SCED_TOOL = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        jLabel101 = new javax.swing.JLabel();
        Process_SCED_PMAT = new javax.swing.JTextField();
        jLabel100 = new javax.swing.JLabel();
        jLabel99 = new javax.swing.JLabel();
        Process_CPLX_TOOL = new javax.swing.JTextField();
        jLabel112 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        Process_PMAT_ACAP = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        Process_TOOL_ACAP = new javax.swing.JTextField();
        jLabel105 = new javax.swing.JLabel();
        jLabel106 = new javax.swing.JLabel();
        Process_TOOL_PCAP = new javax.swing.JTextField();
        jLabel103 = new javax.swing.JLabel();
        jLabel104 = new javax.swing.JLabel();
        Process_PMAT_PCAP = new javax.swing.JTextField();
        jLabel122 = new javax.swing.JLabel();
        jLabel117 = new javax.swing.JLabel();
        Platform_SCED_TIME = new javax.swing.JTextField();
        jLabel126 = new javax.swing.JLabel();
        jLabel120 = new javax.swing.JLabel();
        Platform_SCED_PVOL = new javax.swing.JTextField();
        jLabel125 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        Platform_STOR_ACAP = new javax.swing.JTextField();
        jLabel123 = new javax.swing.JLabel();
        jLabel124 = new javax.swing.JLabel();
        Platform_TIME_ACAP = new javax.swing.JTextField();
        jLabel119 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        Platform_STOR_PCAP = new javax.swing.JTextField();
        jLabel128 = new javax.swing.JLabel();
        jLabel127 = new javax.swing.JLabel();
        Platform_PVOL_PLEX = new javax.swing.JTextField();
        jLabel118 = new javax.swing.JLabel();
        jLabel115 = new javax.swing.JLabel();
        Platform_TIME_TOOL = new javax.swing.JTextField();
        jLabel129 = new javax.swing.JLabel();
        jLabel130 = new javax.swing.JLabel();
        Reuse_RUSE_APEX = new javax.swing.JTextField();
        jLabel131 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        Reuse_RUSE_LTEX = new javax.swing.JTextField();
        jLabel137 = new javax.swing.JLabel();
        Schedule_SUM = new javax.swing.JTextField();
        jLabel138 = new javax.swing.JLabel();
        Product_SUM = new javax.swing.JTextField();
        jLabel139 = new javax.swing.JLabel();
        Personnel_SUM = new javax.swing.JTextField();
        jLabel140 = new javax.swing.JLabel();
        Process_SUM = new javax.swing.JTextField();
        jLabel141 = new javax.swing.JLabel();
        Platform_SUM = new javax.swing.JTextField();
        jLabel142 = new javax.swing.JLabel();
        Reuse_SUM = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        Schedule_VALUE = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        Product_VALUE = new javax.swing.JTextField();
        jLabel133 = new javax.swing.JLabel();
        Personnel_VALUE = new javax.swing.JTextField();
        jLabel134 = new javax.swing.JLabel();
        Process_VALUE = new javax.swing.JTextField();
        jLabel135 = new javax.swing.JLabel();
        Platform_VALUE = new javax.swing.JTextField();
        jLabel136 = new javax.swing.JLabel();
        Reuse_VALUE = new javax.swing.JTextField();
        jLabel143 = new javax.swing.JLabel();
        Schedule_COEFFICIENT = new javax.swing.JTextField();
        jLabel144 = new javax.swing.JLabel();
        Product_COEFFICIENT = new javax.swing.JTextField();
        jLabel145 = new javax.swing.JLabel();
        Personnel_COEFFICIENT = new javax.swing.JTextField();
        jLabel146 = new javax.swing.JLabel();
        Process_COEFFICIENT = new javax.swing.JTextField();
        jLabel147 = new javax.swing.JLabel();
        Platform_COEFFICIENT = new javax.swing.JTextField();
        jLabel148 = new javax.swing.JLabel();
        Reuse_COEFFICIENT = new javax.swing.JTextField();

        setForeground(new java.awt.Color(255, 0, 0));
        setFocusTraversalPolicyProvider(true);
        setMaximumSize(new java.awt.Dimension(900, 800));
        setMinimumSize(new java.awt.Dimension(900, 800));
        setPreferredSize(new java.awt.Dimension(900, 800));

        jLabel1.setForeground(new java.awt.Color(255, 0, 0));
        jLabel1.setText("SCED");
        jLabel1.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel1.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel1.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel2.setText("RELY");
        jLabel2.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel2.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel2.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_RELY.setEditable(false);
        Schedule_SCED_RELY.setText("");
        Schedule_SCED_RELY.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_RELY.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_RELY.setPreferredSize(new java.awt.Dimension(59, 20));
        

        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("SCED");
        jLabel3.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel3.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel3.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel4.setText("TIME");
        jLabel4.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel4.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel4.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_TIME.setEditable(false);
        Schedule_SCED_TIME.setText("");
        Schedule_SCED_TIME.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_TIME.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_TIME.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel5.setForeground(new java.awt.Color(255, 0, 0));
        jLabel5.setText("SCED");
        jLabel5.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel5.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel5.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel6.setText("PVOL");
        jLabel6.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel6.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel6.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_PVOL.setEditable(false);
        Schedule_SCED_PVOL.setText("");
        Schedule_SCED_PVOL.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_PVOL.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_PVOL.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setText("SCED");
        jLabel7.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel7.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel7.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel8.setText("TOOL");
        jLabel8.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel8.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel8.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_TOOL.setEditable(false);
        Schedule_SCED_TOOL.setText("");
        Schedule_SCED_TOOL.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_TOOL.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_TOOL.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel9.setForeground(new java.awt.Color(255, 0, 0));
        jLabel9.setText("SCED");
        jLabel9.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel9.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel9.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel10.setText("ACAP");
        jLabel10.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel10.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel10.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_ACAP.setEditable(false);
        Schedule_SCED_ACAP.setText("");
        Schedule_SCED_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel11.setForeground(new java.awt.Color(255, 0, 0));
        jLabel11.setText("SCED");
        jLabel11.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel11.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel11.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel12.setText("APEX");
        jLabel12.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel12.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel12.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_APEX.setEditable(false);
        Schedule_SCED_APEX.setText("");
        Schedule_SCED_APEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_APEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_APEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel13.setForeground(new java.awt.Color(255, 0, 0));
        jLabel13.setText("SCED");
        jLabel13.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel13.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel13.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel14.setText("PCAP");
        jLabel14.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel14.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel14.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_PCAP.setEditable(false);
        Schedule_SCED_PCAP.setText("");
        Schedule_SCED_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel15.setForeground(new java.awt.Color(255, 0, 0));
        jLabel15.setText("SCED");
        jLabel15.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel15.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel15.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel16.setText("PLEX");
        jLabel16.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel16.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel16.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_PLEX.setEditable(false);
        Schedule_SCED_PLEX.setText("");
        Schedule_SCED_PLEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_PLEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_PLEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel17.setForeground(new java.awt.Color(255, 0, 0));
        jLabel17.setText("SCED");
        jLabel17.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel17.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel17.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel18.setText("LTEX");
        jLabel18.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel18.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel18.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_LTEX.setEditable(false);
        Schedule_SCED_LTEX.setText("");
        Schedule_SCED_LTEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_LTEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_LTEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel19.setForeground(new java.awt.Color(255, 0, 0));
        jLabel19.setText("SCED");
        jLabel19.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel19.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel19.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel20.setForeground(new java.awt.Color(23, 122, 56));
        jLabel20.setText("PMAT");
        jLabel20.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel20.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel20.setPreferredSize(new java.awt.Dimension(40, 14));

        Schedule_SCED_PMAT.setEditable(false);
        Schedule_SCED_PMAT.setText("");
        Schedule_SCED_PMAT.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_PMAT.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SCED_PMAT.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel21.setText("RELY");
        jLabel21.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel21.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel21.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel22.setText("ACAP");
        jLabel22.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel22.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel22.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_RELY_ACAP.setEditable(false);
        Product_RELY_ACAP.setText("");
        Product_RELY_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_RELY_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_RELY_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel38.setText("RELY");
        jLabel38.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel38.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel38.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel39.setText("PCAP");
        jLabel39.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel39.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel39.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_RELY_PCAP.setEditable(false);
        Product_RELY_PCAP.setText("");
        Product_RELY_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_RELY_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_RELY_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel37.setText("CPLX");
        jLabel37.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel37.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel37.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel35.setText("ACAP");
        jLabel35.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel35.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel35.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_CPLX_ACAP.setEditable(false);
        Product_CPLX_ACAP.setText("");
        Product_CPLX_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_CPLX_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_CPLX_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel33.setText("CPLX");
        jLabel33.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel33.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel33.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel34.setText("PCAP");
        jLabel34.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel34.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel34.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_CPLX_PCAP.setEditable(false);
        Product_CPLX_PCAP.setText("");
        Product_CPLX_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_CPLX_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_CPLX_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel41.setText("CPLX");
        jLabel41.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel41.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel41.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel40.setText("TOOL");
        jLabel40.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel40.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel40.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_CPLX_TOOL.setEditable(false);
        Product_CPLX_TOOL.setText("");
        Product_CPLX_TOOL.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_CPLX_TOOL.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_CPLX_TOOL.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel23.setText("RELY");
        jLabel23.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel23.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel23.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel24.setForeground(new java.awt.Color(23, 122, 56));
        jLabel24.setText("PMAT");
        jLabel24.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel24.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel24.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_RELY_PMAT.setEditable(false);
        Product_RELY_PMAT.setText("");
        Product_RELY_PMAT.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_RELY_PMAT.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_RELY_PMAT.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel26.setForeground(new java.awt.Color(255, 0, 0));
        jLabel26.setText("SCED");
        jLabel26.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel26.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel26.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel27.setText("CPLX");
        jLabel27.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel27.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel27.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_SCED_CPLX.setEditable(false);
        Product_SCED_CPLX.setText("");
        Product_SCED_CPLX.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_SCED_CPLX.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_SCED_CPLX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel31.setForeground(new java.awt.Color(255, 0, 0));
        jLabel31.setText("SCED");
        jLabel31.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel31.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel31.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel30.setText("RELY");
        jLabel30.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel30.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel30.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_SCED_RELY.setEditable(false);
        Product_SCED_RELY.setText("");
        Product_SCED_RELY.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_SCED_RELY.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_SCED_RELY.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel28.setForeground(new java.awt.Color(255, 0, 0));
        jLabel28.setText("SCED");
        jLabel28.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel28.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel28.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel29.setText("TIME");
        jLabel29.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel29.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel29.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_SCED_TIME.setEditable(false);
        Product_SCED_TIME.setText("");
        Product_SCED_TIME.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_SCED_TIME.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_SCED_TIME.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel36.setText("RUSE");
        jLabel36.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel36.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel36.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel32.setText("APEX");
        jLabel32.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel32.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel32.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_RUSE_APEX.setEditable(false);
        Product_RUSE_APEX.setText("");
        Product_RUSE_APEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_RUSE_APEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_RUSE_APEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel43.setText("RUSE");
        jLabel43.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel43.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel43.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel44.setText("LTEX");
        jLabel44.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel44.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel44.setPreferredSize(new java.awt.Dimension(40, 14));

        Product_RUSE_LTEX.setEditable(false);
        Product_RUSE_LTEX.setText("");
        Product_RUSE_LTEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_RUSE_LTEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_RUSE_LTEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel64.setForeground(new java.awt.Color(23, 122, 56));
        jLabel64.setText("PMAT");
        jLabel64.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel64.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel64.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel47.setText("ACAP");
        jLabel47.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel47.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel47.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_PMAT_ACAP.setEditable(false);
        Personnel_PMAT_ACAP.setText("");
        Personnel_PMAT_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_PMAT_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_PMAT_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel51.setText("STOR");
        jLabel51.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel51.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel51.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel55.setText("ACAP");
        jLabel55.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel55.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel55.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_STOR_ACAP.setEditable(false);
        Personnel_STOR_ACAP.setText("");
        Personnel_STOR_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_STOR_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_STOR_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel52.setText("TIME");
        jLabel52.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel52.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel52.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel49.setText("ACAP");
        jLabel49.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel49.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel49.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_TIME_ACAP.setEditable(false);
        Personnel_TIME_ACAP.setText("");
        Personnel_TIME_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_TIME_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_TIME_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel63.setText("TOOL");
        jLabel63.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel63.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel63.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel62.setText("ACAP");
        jLabel62.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel62.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel62.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_TOOL_ACAP.setEditable(false);
        Personnel_TOOL_ACAP.setText("");
        Personnel_TOOL_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_TOOL_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_TOOL_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel53.setText("TOOL");
        jLabel53.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel53.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel53.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel54.setText("PCAP");
        jLabel54.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel54.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel54.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_TOOL_PCAP.setEditable(false);
        Personnel_TOOL_PCAP.setText("");
        Personnel_TOOL_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_TOOL_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_TOOL_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel45.setText("RUSE");
        jLabel45.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel45.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel45.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel46.setText("APEX");
        jLabel46.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel46.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel46.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_RUSE_APEX.setEditable(false);
        Personnel_RUSE_APEX.setText("");
        Personnel_RUSE_APEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_RUSE_APEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_RUSE_APEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel48.setText("RUSE");
        jLabel48.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel48.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel48.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel56.setText("LTEX");
        jLabel56.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel56.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel56.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_RUSE_LTEX.setEditable(false);
        Personnel_RUSE_LTEX.setText("");
        Personnel_RUSE_LTEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_RUSE_LTEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_RUSE_LTEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel61.setForeground(new java.awt.Color(23, 122, 56));
        jLabel61.setText("PMAT");
        jLabel61.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel61.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel61.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel59.setText("PCAP");
        jLabel59.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel59.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel59.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_PMAT_PCAP.setEditable(false);
        Personnel_PMAT_PCAP.setText("");
        Personnel_PMAT_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_PMAT_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_PMAT_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel58.setText("STOR");
        jLabel58.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel58.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel58.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel57.setText("PCAP");
        jLabel57.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel57.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel57.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_STOR_PCAP.setEditable(false);
        Personnel_STOR_PCAP.setText("");
        Personnel_STOR_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_STOR_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_STOR_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel50.setText("TIME");
        jLabel50.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel50.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel50.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel60.setText("PCAP");
        jLabel60.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel60.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel60.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_TIME_PCAP.setEditable(false);
        Personnel_TIME_PCAP.setText("");
        Personnel_TIME_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_TIME_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_TIME_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel65.setText("LTEX");
        jLabel65.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel65.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel65.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel66.setText("PCAP");
        jLabel66.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel66.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel66.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_LTEX_PCAP.setEditable(false);
        Personnel_LTEX_PCAP.setText("");
        Personnel_LTEX_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_LTEX_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_LTEX_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel71.setText("PVOL");
        jLabel71.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel71.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel71.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel82.setText("PLEX");
        jLabel82.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel82.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel82.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_PVOL_PLEX.setEditable(false);
        Personnel_PVOL_PLEX.setText("");
        Personnel_PVOL_PLEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_PVOL_PLEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_PVOL_PLEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel85.setForeground(new java.awt.Color(255, 0, 0));
        jLabel85.setText("SCED");
        jLabel85.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel85.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel85.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel74.setText("ACAP");
        jLabel74.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel74.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel74.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_SCED_ACAP.setEditable(false);
        Personnel_SCED_ACAP.setText("");
        Personnel_SCED_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel84.setForeground(new java.awt.Color(255, 0, 0));
        jLabel84.setText("SCED");
        jLabel84.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel84.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel84.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel81.setText("APEX");
        jLabel81.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel81.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel81.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_SCED_APEX.setEditable(false);
        Personnel_SCED_APEX.setText("");
        Personnel_SCED_APEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_APEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_APEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel72.setForeground(new java.awt.Color(255, 0, 0));
        jLabel72.setText("SCED");
        jLabel72.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel72.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel72.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel67.setText("PCAP");
        jLabel67.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel67.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel67.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_SCED_PCAP.setEditable(false);
        Personnel_SCED_PCAP.setText("");
        Personnel_SCED_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel73.setForeground(new java.awt.Color(255, 0, 0));
        jLabel73.setText("SCED");
        jLabel73.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel73.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel73.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel75.setText("PLEX");
        jLabel75.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel75.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel75.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_SCED_PLEX.setEditable(false);
        Personnel_SCED_PLEX.setText("");
        Personnel_SCED_PLEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_PLEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_PLEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel88.setForeground(new java.awt.Color(255, 0, 0));
        jLabel88.setText("SCED");
        jLabel88.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel88.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel88.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel87.setText("LTEX");
        jLabel87.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel87.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel87.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_SCED_LTEX.setEditable(false);
        Personnel_SCED_LTEX.setText("");
        Personnel_SCED_LTEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_LTEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_SCED_LTEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel83.setText("RELY");
        jLabel83.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel83.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel83.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel76.setText("ACAP");
        jLabel76.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel76.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel76.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_RELY_ACAP.setEditable(false);
        Personnel_RELY_ACAP.setText("");
        Personnel_RELY_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_RELY_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_RELY_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel68.setText("RELY");
        jLabel68.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel68.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel68.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel79.setText("PCAP");
        jLabel79.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel79.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel79.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_RELY_PCAP.setEditable(false);
        Personnel_RELY_PCAP.setText("");
        Personnel_RELY_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_RELY_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_RELY_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel78.setText("CPLX");
        jLabel78.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel78.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel78.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel77.setText("ACAP");
        jLabel77.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel77.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel77.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_CPLX_ACAP.setEditable(false);
        Personnel_CPLX_ACAP.setText("");
        Personnel_CPLX_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_CPLX_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_CPLX_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel86.setText("CPLX");
        jLabel86.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel86.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel86.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel80.setText("PCAP");
        jLabel80.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel80.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel80.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_CPLX_PCAP.setEditable(false);
        Personnel_CPLX_PCAP.setText("");
        Personnel_CPLX_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_CPLX_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_CPLX_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel70.setForeground(new java.awt.Color(23, 122, 56));
        jLabel70.setText("TEAM");
        jLabel70.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel70.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel70.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel69.setText("APEX");
        jLabel69.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel69.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel69.setPreferredSize(new java.awt.Dimension(40, 14));

        Personnel_TEAM_APEX.setEditable(false);
        Personnel_TEAM_APEX.setText("");
        Personnel_TEAM_APEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_TEAM_APEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_TEAM_APEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel93.setText("TOOL");
        jLabel93.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel93.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel93.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel108.setForeground(new java.awt.Color(23, 122, 56));
        jLabel108.setText("PMAT");
        jLabel108.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel108.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel108.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_TOOL_PMAT.setEditable(false);
        Process_TOOL_PMAT.setText("");
        Process_TOOL_PMAT.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_TOOL_PMAT.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_TOOL_PMAT.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel111.setText("TIME");
        jLabel111.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel111.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel111.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel96.setText("TOOL");
        jLabel96.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel96.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel96.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_TIME_TOOL.setEditable(false);
        Process_TIME_TOOL.setText("");
        Process_TIME_TOOL.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_TIME_TOOL.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_TIME_TOOL.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel110.setText("TOOL");
        jLabel110.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel110.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel110.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel107.setForeground(new java.awt.Color(23, 122, 56));
        jLabel107.setText("PMAT");
        jLabel107.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel107.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel107.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_TOOL_PMAT_2.setEditable(false);
        Process_TOOL_PMAT_2.setText("");
        Process_TOOL_PMAT_2.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_TOOL_PMAT_2.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_TOOL_PMAT_2.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel94.setForeground(new java.awt.Color(23, 122, 56));
        jLabel94.setText("TEAM");
        jLabel94.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel94.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel94.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel89.setText("APEX");
        jLabel89.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel89.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel89.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_TEAM_APEX.setEditable(false);
        Process_TEAM_APEX.setText("");
        Process_TEAM_APEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_TEAM_APEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_TEAM_APEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel95.setForeground(new java.awt.Color(23, 122, 56));
        jLabel95.setText("TEAM");
        jLabel95.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel95.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel95.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel97.setForeground(new java.awt.Color(255, 0, 0));
        jLabel97.setText("SCED");
        jLabel97.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel97.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel97.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_TEAM_SCED.setEditable(false);
        Process_TEAM_SCED.setText("");
        Process_TEAM_SCED.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_TEAM_SCED.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_TEAM_SCED.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel114.setForeground(new java.awt.Color(23, 122, 56));
        jLabel114.setText("TEAM");
        jLabel114.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel114.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel114.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel113.setText("PMAT");
        jLabel113.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel113.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel113.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_TEAM_SITE.setEditable(false);
        Process_TEAM_SITE.setText("");
        Process_TEAM_SITE.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_TEAM_SITE.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_TEAM_SITE.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel109.setForeground(new java.awt.Color(255, 0, 0));
        jLabel109.setText("SCED");
        jLabel109.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel109.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel109.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel98.setText("TOOL");
        jLabel98.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel98.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel98.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_SCED_TOOL.setEditable(false);
        Process_SCED_TOOL.setText("");
        Process_SCED_TOOL.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_SCED_TOOL.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_SCED_TOOL.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel90.setForeground(new java.awt.Color(255, 0, 0));
        jLabel90.setText("SCED");
        jLabel90.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel90.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel90.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel101.setForeground(new java.awt.Color(23, 122, 56));
        jLabel101.setText("PMAT");
        jLabel101.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel101.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel101.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_SCED_PMAT.setEditable(false);
        Process_SCED_PMAT.setText("");
        Process_SCED_PMAT.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_SCED_PMAT.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_SCED_PMAT.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel100.setText("CPLX");
        jLabel100.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel100.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel100.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel99.setText("TOOL");
        jLabel99.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel99.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel99.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_CPLX_TOOL.setEditable(false);
        Process_CPLX_TOOL.setText("");
        Process_CPLX_TOOL.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_CPLX_TOOL.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_CPLX_TOOL.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel112.setForeground(new java.awt.Color(23, 122, 56));
        jLabel112.setText("PMAT");
        jLabel112.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel112.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel112.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel102.setText("ACAP");
        jLabel102.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel102.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel102.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_PMAT_ACAP.setEditable(false);
        Process_PMAT_ACAP.setText("");
        Process_PMAT_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_PMAT_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_PMAT_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel92.setText("TOOL");
        jLabel92.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel92.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel92.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel91.setText("ACAP");
        jLabel91.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel91.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel91.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_TOOL_ACAP.setEditable(false);
        Process_TOOL_ACAP.setText("");
        Process_TOOL_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_TOOL_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_TOOL_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel105.setText("TOOL");
        jLabel105.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel105.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel105.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel106.setText("PCAP");
        jLabel106.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel106.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel106.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_TOOL_PCAP.setEditable(false);
        Process_TOOL_PCAP.setText("");
        Process_TOOL_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_TOOL_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_TOOL_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel103.setForeground(new java.awt.Color(23, 122, 56));
        jLabel103.setText("PMAT");
        jLabel103.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel103.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel103.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel104.setText("PCAP");
        jLabel104.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel104.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel104.setPreferredSize(new java.awt.Dimension(40, 14));

        Process_PMAT_PCAP.setEditable(false);
        Process_PMAT_PCAP.setText("");
        Process_PMAT_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_PMAT_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_PMAT_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel122.setForeground(new java.awt.Color(255, 0, 0));
        jLabel122.setText("SCED");
        jLabel122.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel122.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel122.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel117.setText("TIME");
        jLabel117.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel117.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel117.setPreferredSize(new java.awt.Dimension(40, 14));

        Platform_SCED_TIME.setEditable(false);
        Platform_SCED_TIME.setText("");
        Platform_SCED_TIME.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_SCED_TIME.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_SCED_TIME.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel126.setForeground(new java.awt.Color(255, 0, 0));
        jLabel126.setText("SCED");
        jLabel126.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel126.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel126.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel120.setText("PVOL");
        jLabel120.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel120.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel120.setPreferredSize(new java.awt.Dimension(40, 14));

        Platform_SCED_PVOL.setEditable(false);
        Platform_SCED_PVOL.setText("");
        Platform_SCED_PVOL.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_SCED_PVOL.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_SCED_PVOL.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel125.setText("STOR");
        jLabel125.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel125.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel125.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel116.setText("ACAP");
        jLabel116.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel116.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel116.setPreferredSize(new java.awt.Dimension(40, 14));

        Platform_STOR_ACAP.setEditable(false);
        Platform_STOR_ACAP.setText("");
        Platform_STOR_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_STOR_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_STOR_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel123.setText("TIME");
        jLabel123.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel123.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel123.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel124.setText("ACAP");
        jLabel124.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel124.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel124.setPreferredSize(new java.awt.Dimension(40, 14));

        Platform_TIME_ACAP.setEditable(false);
        Platform_TIME_ACAP.setText("");
        Platform_TIME_ACAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_TIME_ACAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_TIME_ACAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel119.setText("STOR");
        jLabel119.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel119.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel119.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel121.setText("PCAP");
        jLabel121.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel121.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel121.setPreferredSize(new java.awt.Dimension(40, 14));

        Platform_STOR_PCAP.setEditable(false);
        Platform_STOR_PCAP.setText("");
        Platform_STOR_PCAP.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_STOR_PCAP.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_STOR_PCAP.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel128.setText("PVOL");
        jLabel128.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel128.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel128.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel127.setText("PLEX");
        jLabel127.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel127.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel127.setPreferredSize(new java.awt.Dimension(40, 14));

        Platform_PVOL_PLEX.setEditable(false);
        Platform_PVOL_PLEX.setText("");
        Platform_PVOL_PLEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_PVOL_PLEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_PVOL_PLEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel118.setText("TIME");
        jLabel118.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel118.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel118.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel115.setText("TOOL");
        jLabel115.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel115.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel115.setPreferredSize(new java.awt.Dimension(40, 14));

        Platform_TIME_TOOL.setEditable(false);
        Platform_TIME_TOOL.setText("");
        Platform_TIME_TOOL.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_TIME_TOOL.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_TIME_TOOL.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel129.setText("RUSE");
        jLabel129.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel129.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel129.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel130.setText("APEX");
        jLabel130.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel130.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel130.setPreferredSize(new java.awt.Dimension(40, 14));

        Reuse_RUSE_APEX.setEditable(false);
        Reuse_RUSE_APEX.setText("");
        Reuse_RUSE_APEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Reuse_RUSE_APEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Reuse_RUSE_APEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel131.setText("RUSE");
        jLabel131.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel131.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel131.setPreferredSize(new java.awt.Dimension(40, 14));

        jLabel132.setText("LTEX");
        jLabel132.setMaximumSize(new java.awt.Dimension(40, 14));
        jLabel132.setMinimumSize(new java.awt.Dimension(40, 14));
        jLabel132.setPreferredSize(new java.awt.Dimension(40, 14));

        Reuse_RUSE_LTEX.setEditable(false);
        Reuse_RUSE_LTEX.setText("");
        Reuse_RUSE_LTEX.setMaximumSize(new java.awt.Dimension(59, 20));
        Reuse_RUSE_LTEX.setMinimumSize(new java.awt.Dimension(59, 20));
        Reuse_RUSE_LTEX.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel137.setText("Sum");

        Schedule_SUM.setEditable(false);
        Schedule_SUM.setText("0");
        Schedule_SUM.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_SUM.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_SUM.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel138.setText("Sum");

        Product_SUM.setEditable(false);
        Product_SUM.setText("0");
        Product_SUM.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_SUM.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_SUM.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel139.setText("Sum");

        Personnel_SUM.setEditable(false);
        Personnel_SUM.setText("0");
        Personnel_SUM.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_SUM.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_SUM.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel140.setText("Sum");

        Process_SUM.setEditable(false);
        Process_SUM.setText("0");
        Process_SUM.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_SUM.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_SUM.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel141.setText("Sum");

        Platform_SUM.setEditable(false);
        Platform_SUM.setText("0");
        Platform_SUM.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_SUM.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_SUM.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel142.setText("Sum");

        Reuse_SUM.setEditable(false);
        Reuse_SUM.setText("0");
        Reuse_SUM.setMaximumSize(new java.awt.Dimension(59, 20));
        Reuse_SUM.setMinimumSize(new java.awt.Dimension(59, 20));
        Reuse_SUM.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel25.setText("Schedule");
        Schedule_VALUE.setText("0");
        Schedule_VALUE.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_VALUE.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_VALUE.setPreferredSize(new java.awt.Dimension(59, 20));
        Schedule_VALUE.addActionListener(this);
        Schedule_VALUE.addFocusListener(this);

        jLabel42.setText("Product");

        Product_VALUE.setText("0");
        Product_VALUE.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_VALUE.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_VALUE.setPreferredSize(new java.awt.Dimension(59, 20));
        Product_VALUE.addActionListener(this);
        Product_VALUE.addFocusListener(this);

        jLabel133.setText("Personnel");

        Personnel_VALUE.setText("0");
        Personnel_VALUE.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_VALUE.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_VALUE.setPreferredSize(new java.awt.Dimension(59, 20));
        Personnel_VALUE.addActionListener(this);
        Personnel_VALUE.addFocusListener(this);

        jLabel134.setText("Process");

        Process_VALUE.setText("0");
        Process_VALUE.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_VALUE.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_VALUE.setPreferredSize(new java.awt.Dimension(59, 20));
        Process_VALUE.addActionListener(this);
        Process_VALUE.addFocusListener(this);

        jLabel135.setText("Platform");

        Platform_VALUE.setText("0");
        Platform_VALUE.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_VALUE.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_VALUE.setPreferredSize(new java.awt.Dimension(59, 20));
        Platform_VALUE.addActionListener(this);
        Platform_VALUE.addFocusListener(this);

        jLabel136.setText("Reuse");

        Reuse_VALUE.setText("0");
        Reuse_VALUE.setMaximumSize(new java.awt.Dimension(59, 20));
        Reuse_VALUE.setMinimumSize(new java.awt.Dimension(59, 20));
        Reuse_VALUE.setPreferredSize(new java.awt.Dimension(59, 20));
        Reuse_VALUE.addActionListener(this);
        Reuse_VALUE.addFocusListener(this);

        jLabel143.setText("Coefficient");

        Schedule_COEFFICIENT.setEditable(false);
        Schedule_COEFFICIENT.setText("0");
        Schedule_COEFFICIENT.setMaximumSize(new java.awt.Dimension(59, 20));
        Schedule_COEFFICIENT.setMinimumSize(new java.awt.Dimension(59, 20));
        Schedule_COEFFICIENT.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel144.setText("Coefficient");

        Product_COEFFICIENT.setEditable(false);
        Product_COEFFICIENT.setText("");
        Product_COEFFICIENT.setMaximumSize(new java.awt.Dimension(59, 20));
        Product_COEFFICIENT.setMinimumSize(new java.awt.Dimension(59, 20));
        Product_COEFFICIENT.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel145.setText("Coefficient");

        Personnel_COEFFICIENT.setEditable(false);
        Personnel_COEFFICIENT.setText("0");
        Personnel_COEFFICIENT.setMaximumSize(new java.awt.Dimension(59, 20));
        Personnel_COEFFICIENT.setMinimumSize(new java.awt.Dimension(59, 20));
        Personnel_COEFFICIENT.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel146.setText("Coefficient");

        Process_COEFFICIENT.setEditable(false);
        Process_COEFFICIENT.setText("0");
        Process_COEFFICIENT.setMaximumSize(new java.awt.Dimension(59, 20));
        Process_COEFFICIENT.setMinimumSize(new java.awt.Dimension(59, 20));
        Process_COEFFICIENT.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel147.setText("Coefficient");

        Platform_COEFFICIENT.setEditable(false);
        Platform_COEFFICIENT.setText("0");
        Platform_COEFFICIENT.setMaximumSize(new java.awt.Dimension(59, 20));
        Platform_COEFFICIENT.setMinimumSize(new java.awt.Dimension(59, 20));
        Platform_COEFFICIENT.setPreferredSize(new java.awt.Dimension(59, 20));

        jLabel148.setText("Coefficient");

        Reuse_COEFFICIENT.setEditable(false);
        Reuse_COEFFICIENT.setText("0");
        Reuse_COEFFICIENT.setMaximumSize(new java.awt.Dimension(59, 20));
        Reuse_COEFFICIENT.setMinimumSize(new java.awt.Dimension(59, 20));
        Reuse_COEFFICIENT.setPreferredSize(new java.awt.Dimension(59, 20));
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Personnel_TEAM_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Personnel_SCED_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Personnel_SCED_PLEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Personnel_SCED_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Personnel_SCED_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Personnel_SCED_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Personnel_PVOL_PLEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Personnel_RELY_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Personnel_RELY_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Personnel_CPLX_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Personnel_CPLX_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Product_RUSE_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel143)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Schedule_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel137)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Schedule_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Schedule_SCED_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Schedule_SCED_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Schedule_SCED_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Schedule_SCED_PVOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Schedule_SCED_TIME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Schedule_SCED_RELY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Schedule_SCED_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Schedule_SCED_PLEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Schedule_SCED_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Schedule_SCED_PMAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Schedule_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(Product_RELY_PMAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(Product_CPLX_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(Product_CPLX_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(Product_CPLX_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(Product_RELY_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGroup(layout.createSequentialGroup()
                                                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addComponent(Product_RELY_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Product_SCED_CPLX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Product_SCED_RELY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Product_SCED_TIME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Product_RUSE_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel138)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(Product_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel42)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(Product_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel144)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(Product_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(Personnel_LTEX_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Personnel_RUSE_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Personnel_TOOL_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Personnel_TOOL_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Personnel_TIME_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Personnel_STOR_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(Personnel_PMAT_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Personnel_RUSE_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Personnel_PMAT_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Personnel_STOR_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(Personnel_TIME_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel139)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Personnel_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel133)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Personnel_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel145)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(Personnel_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Process_PMAT_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Process_TOOL_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(Process_TOOL_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel114, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel113, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Process_TEAM_SITE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Process_TEAM_SCED, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Process_TEAM_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel110, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Process_TOOL_PMAT_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel96, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Process_TIME_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel108, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Process_TOOL_PMAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel109, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Process_SCED_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Process_SCED_PMAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Process_CPLX_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel102, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Process_PMAT_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel134)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Process_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel140)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Process_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel146)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Process_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel118, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel115, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Platform_TIME_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel128, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel127, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Platform_PVOL_PLEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel119, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel121, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Platform_STOR_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel123, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel124, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Platform_TIME_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel125, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel116, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Platform_STOR_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel126, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel120, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Platform_SCED_PVOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel122, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jLabel117, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(Platform_SCED_TIME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel135)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Platform_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel141)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Platform_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel147)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Platform_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel142)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Reuse_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel131, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel132, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Reuse_RUSE_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel129, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel130, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Reuse_RUSE_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel136)
                                    .addComponent(jLabel148))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(Reuse_COEFFICIENT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(Reuse_VALUE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_PMAT_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_STOR_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_TIME_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_TOOL_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_TOOL_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_RUSE_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_RUSE_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_PMAT_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_STOR_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_TIME_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Personnel_LTEX_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_RELY_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_RELY_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_CPLX_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_CPLX_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_CPLX_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_RELY_PMAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_SCED_CPLX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_SCED_RELY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_SCED_TIME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Product_RUSE_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_RELY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_TIME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_PVOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_PLEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Schedule_SCED_PMAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Product_RUSE_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Personnel_PVOL_PLEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel82, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel71, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Personnel_SCED_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel74, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel85, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Process_TOOL_PMAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel108, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel93, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Process_TIME_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel96, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Process_TOOL_PMAT_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel107, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel110, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Process_TEAM_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel94, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Process_TEAM_SCED, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel95, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Process_TEAM_SITE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel113, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel114, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Process_SCED_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel98, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel109, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Platform_SCED_TIME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel117, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel122, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Platform_SCED_PVOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel120, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel126, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Reuse_RUSE_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel130, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel129, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(Reuse_RUSE_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel132, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel131, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Platform_STOR_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel116, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel125, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Platform_TIME_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel124, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel123, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Platform_STOR_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel121, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel119, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Platform_PVOL_PLEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel127, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel128, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(Platform_TIME_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel115, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel118, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Process_SCED_PMAT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel101, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Process_CPLX_TOOL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel100, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Process_PMAT_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel102, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Process_TOOL_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Process_TOOL_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel105, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Process_PMAT_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel104, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel103, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Personnel_SCED_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Personnel_SCED_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel67, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Personnel_SCED_PLEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel75, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel73, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Personnel_SCED_LTEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Personnel_RELY_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Personnel_RELY_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Personnel_CPLX_ACAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel77, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Personnel_CPLX_PCAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Personnel_TEAM_APEX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel69, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel70, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel137)
                    .addComponent(jLabel138)
                    .addComponent(jLabel139)
                    .addComponent(jLabel140)
                    .addComponent(jLabel141)
                    .addComponent(jLabel142)
                    .addComponent(Schedule_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Product_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Personnel_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Process_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Platform_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Reuse_SUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(jLabel133)
                    .addComponent(jLabel134)
                    .addComponent(jLabel135)
                    .addComponent(jLabel136)
                    .addComponent(jLabel25)
                    .addComponent(Schedule_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Product_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Personnel_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Process_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Platform_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Reuse_VALUE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel143)
                    .addComponent(jLabel144)
                    .addComponent(jLabel145)
                    .addComponent(jLabel146)
                    .addComponent(jLabel147)
                    .addComponent(jLabel148)
                    .addComponent(Schedule_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Product_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Personnel_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Process_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Platform_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Reuse_COEFFICIENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>
    // Variables declaration - do not modify
    private javax.swing.JTextField Personnel_CPLX_ACAP;
    private javax.swing.JTextField Personnel_CPLX_PCAP;
    private javax.swing.JTextField Personnel_LTEX_PCAP;
    private javax.swing.JTextField Personnel_PMAT_ACAP;
    private javax.swing.JTextField Personnel_PMAT_PCAP;
    private javax.swing.JTextField Personnel_PVOL_PLEX;
    private javax.swing.JTextField Personnel_RELY_ACAP;
    private javax.swing.JTextField Personnel_RELY_PCAP;
    private javax.swing.JTextField Personnel_RUSE_APEX;
    private javax.swing.JTextField Personnel_RUSE_LTEX;
    private javax.swing.JTextField Personnel_SCED_ACAP;
    private javax.swing.JTextField Personnel_SCED_APEX;
    private javax.swing.JTextField Personnel_SCED_LTEX;
    private javax.swing.JTextField Personnel_SCED_PCAP;
    private javax.swing.JTextField Personnel_SCED_PLEX;
    private javax.swing.JTextField Personnel_STOR_ACAP;
    private javax.swing.JTextField Personnel_STOR_PCAP;
    private javax.swing.JTextField Personnel_TEAM_APEX;
    private javax.swing.JTextField Personnel_TIME_ACAP;
    private javax.swing.JTextField Personnel_TIME_PCAP;
    private javax.swing.JTextField Personnel_TOOL_ACAP;
    private javax.swing.JTextField Personnel_TOOL_PCAP;
    private javax.swing.JTextField Personnel_SUM;
    private javax.swing.JTextField Personnel_COEFFICIENT;
    private javax.swing.JTextField Personnel_VALUE;
    private javax.swing.JTextField Platform_SCED_PVOL;
    private javax.swing.JTextField Platform_PVOL_PLEX;
    private javax.swing.JTextField Platform_SCED_TIME;
    private javax.swing.JTextField Platform_STOR_ACAP;
    private javax.swing.JTextField Platform_STOR_PCAP;
    private javax.swing.JTextField Platform_TIME_ACAP;
    private javax.swing.JTextField Platform_TIME_TOOL;
    private javax.swing.JTextField Platform_SUM;
    private javax.swing.JTextField Platform_COEFFICIENT;
    private javax.swing.JTextField Platform_VALUE;
    private javax.swing.JTextField Process_CPLX_TOOL;
    private javax.swing.JTextField Process_PMAT_ACAP;
    private javax.swing.JTextField Process_PMAT_PCAP;
    private javax.swing.JTextField Process_SCED_PMAT;
    private javax.swing.JTextField Process_SCED_TOOL;
    private javax.swing.JTextField Process_TEAM_APEX;
    private javax.swing.JTextField Process_TEAM_SITE;
    private javax.swing.JTextField Process_TEAM_SCED;
    private javax.swing.JTextField Process_TIME_TOOL;
    private javax.swing.JTextField Process_TOOL_ACAP;
    private javax.swing.JTextField Process_TOOL_PCAP;
    private javax.swing.JTextField Process_TOOL_PMAT;
    private javax.swing.JTextField Process_TOOL_PMAT_2;
    private javax.swing.JTextField Process_SUM;
    private javax.swing.JTextField Process_COEFFICIENT;
    private javax.swing.JTextField Process_VALUE;
    private javax.swing.JTextField Product_CPLX_ACAP;
    private javax.swing.JTextField Product_CPLX_PCAP;
    private javax.swing.JTextField Product_CPLX_TOOL;
    private javax.swing.JTextField Product_RELY_ACAP;
    private javax.swing.JTextField Product_RELY_PCAP;
    private javax.swing.JTextField Product_RELY_PMAT;
    private javax.swing.JTextField Product_RUSE_APEX;
    private javax.swing.JTextField Product_RUSE_LTEX;
    private javax.swing.JTextField Product_SCED_CPLX;
    private javax.swing.JTextField Product_SCED_RELY;
    private javax.swing.JTextField Product_SCED_TIME;
    private javax.swing.JTextField Product_SUM;
    private javax.swing.JTextField Product_COEFFICIENT;
    private javax.swing.JTextField Product_VALUE;
    private javax.swing.JTextField Reuse_RUSE_APEX;
    private javax.swing.JTextField Reuse_RUSE_LTEX;
    private javax.swing.JTextField Reuse_SUM;
    private javax.swing.JTextField Reuse_COEFFICIENT;
    private javax.swing.JTextField Reuse_VALUE;
    private javax.swing.JTextField Schedule_SCED_ACAP;
    private javax.swing.JTextField Schedule_SCED_APEX;
    private javax.swing.JTextField Schedule_SCED_LTEX;
    private javax.swing.JTextField Schedule_SCED_PCAP;
    private javax.swing.JTextField Schedule_SCED_PLEX;
    private javax.swing.JTextField Schedule_SCED_PMAT;
    private javax.swing.JTextField Schedule_SCED_PVOL;
    private javax.swing.JTextField Schedule_SCED_RELY;
    private javax.swing.JTextField Schedule_SCED_TIME;
    private javax.swing.JTextField Schedule_SCED_TOOL;
    private javax.swing.JTextField Schedule_SUM;
    private javax.swing.JTextField Schedule_COEFFICIENT;
    private javax.swing.JTextField Schedule_VALUE;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    // End of variables declaration

    @Override
    public void actionPerformed(ActionEvent e) {
        setCoefficientText(e.getSource());
    }

    @Override
    public void focusGained(FocusEvent e) {
        // do nothing
    }

    @Override
    public void focusLost(FocusEvent e) {
        setCoefficientText(e.getSource());
    }
    
    private void setCoefficientText(Object o) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        
        if (o == Schedule_VALUE) {
            try {
                double sum = Double.parseDouble(Schedule_SUM.getText());
                double value = Double.parseDouble(Schedule_VALUE.getText());
                if (value == 0.0d) {
                    Schedule_COEFFICIENT.setText("...");
                } else {
                    double coefficient = value / sum;
                    Schedule_COEFFICIENT.setText("" + nf.format(coefficient));
                }
            } catch (NumberFormatException ex) {
                Schedule_COEFFICIENT.setText("...");
            }
        } else if (o == Product_VALUE) {
            try {
                double sum = Double.parseDouble(Product_SUM.getText());
                double value = Double.parseDouble(Product_VALUE.getText());
                if (value == 0.0d) {
                    Product_COEFFICIENT.setText("...");
                } else {
                    double coefficient = value / sum;
                    Product_COEFFICIENT.setText("" + nf.format(coefficient));
                }
            } catch (NumberFormatException ex) {
                Product_COEFFICIENT.setText("...");
            }
        } else if (o == Personnel_VALUE) {
            try {
                double sum = Double.parseDouble(Personnel_SUM.getText());
                double value = Double.parseDouble(Personnel_VALUE.getText());
                if (value == 0.0d) {
                    Personnel_COEFFICIENT.setText("...");
                } else {
                    double coefficient = value / sum;
                    Personnel_COEFFICIENT.setText("" + nf.format(coefficient));
                }
            } catch (NumberFormatException ex) {
                Personnel_COEFFICIENT.setText("...");
            }
        } else if (o == Process_VALUE) {
            try {
                double sum = Double.parseDouble(Process_SUM.getText());
                double value = Double.parseDouble(Process_VALUE.getText());
                if (value == 0.0d) {
                    Process_COEFFICIENT.setText("...");
                } else {
                    double coefficient = value / sum;
                    Process_COEFFICIENT.setText("" + nf.format(coefficient));
                }
            } catch (NumberFormatException ex) {
                Process_COEFFICIENT.setText("...");
            }
        } else if (o == Platform_VALUE) {
            try {
                double sum = Double.parseDouble(Platform_SUM.getText());
                double value = Double.parseDouble(Platform_VALUE.getText());
                if (value == 0.0d) {
                    Platform_COEFFICIENT.setText("...");
                } else {
                    double coefficient = value / sum;
                    Platform_COEFFICIENT.setText("" + nf.format(coefficient));
                }
            } catch (NumberFormatException ex) {
                Platform_COEFFICIENT.setText("...");
            }
        } else if (o == Reuse_VALUE) {
            try {
                double sum = Double.parseDouble(Reuse_SUM.getText());
                double value = Double.parseDouble(Reuse_VALUE.getText());
                if (value == 0.0d) {
                    Reuse_COEFFICIENT.setText("...");
                } else {
                    double coefficient = value / sum;
                    Reuse_COEFFICIENT.setText("" + nf.format(coefficient));
                }
            } catch (NumberFormatException ex) {
                Reuse_COEFFICIENT.setText("...");
            }
        }        
    }
}