/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package core;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Larry Chen
 */
public class COINCOMOConstants {

    public static final String COINCOMO_TITLE = "USC COINCOMO 2.0";

    public static enum CoincomoMode {
        DESKTOP_ONLY, DATABASE_ONLY, UNIFIED
    }

    public static enum OperationMode {
        DESKTOP, DATABASE;
    };

    public static enum DatabaseType {
        PostgreSQL, MySQL
    };

    public static enum LocalCalibrationMode {
        COEFFICIENTS_ONLY, COEFFICIENTS_AND_EXPONENTS
    }
    public static enum RISK{
       SCHEDELE,PRODUCT,PLATFORM,PERSONNEL,PROCESS,REUSE,TOTAL       
    };

    public static enum EAF {
        RELY, DATA, DOCU, CPLX, RUSE, TIME, STOR, PVOL, ACAP, APEX, PCAP, PLEX, LTEX, PCON, TOOL, SITE, USR1, USR2, SCED
    };

    public static enum SF {
        PREC, FLEX, RESL, TEAM, PMAT
    };

    public static enum FP {
        ILF, EIF, EI, EO, EQ
    };

    public static enum FT {
        Low, Average, High, SubTotal
    };

    public static enum COPSEMO {
        Inception, Elaboration, Construction, Transition
    }

    public static enum Rating {
        VLO, LO, NOM, HI, VHI, XHI
    }

    public static enum Increment {
        Percent0("0%", 0.0d), Percent25("25%", 0.25d), Percent50("50%", 0.50d), Percent75("75%", 0.75d);
        
        private final String stringValue;
        private final double doubleValue;

        Increment(String stringValue, double doubleValue) {
            this.stringValue = stringValue;
            this.doubleValue = doubleValue;
        }

        public String getStringValue() {
            return this.stringValue;
        }

        public double getDoubleValue() {
            return this.doubleValue;
        }

        @Override
        public String toString() {
            return this.getStringValue();
        }

        private static final Map<String, Increment> stringToEnum = new HashMap<String, Increment>();

        static {
            for (Increment increment : values()) {
                stringToEnum.put(increment.toString(), increment);
            }
        }

        public static Increment getValueOf(String value) {
            return stringToEnum.get(value);
        }
    }

    public static enum RatioType {
        Jones, David
    }

    public static enum CalculationMethod {
        UsingTable("Using Table"), InputCalculatedFunctionPoints("Input Calculated Function Points");

        private final String stringValue;

        CalculationMethod(String stringValue) {
            this.stringValue = stringValue;
        }

        public String getStringValue() {
            return this.stringValue;
        }

        @Override
        public String toString() {
            return this.getStringValue();
        }

        private static final Map<String, CalculationMethod> stringToEnum = new HashMap<String, CalculationMethod>();

        static {
            for (CalculationMethod calculationMethod : values()) {
                stringToEnum.put(calculationMethod.toString(), calculationMethod);
            }
        }

        public static CalculationMethod getValueOf(String value) {
            return stringToEnum.get(value);
        }
    }

    public static enum Scenario {
        Optimistic, MostLikely, Pessimistic
    };

    public static final int NAME_LENGTH = 60;

    public static final String EAFS[] = {"RELY", "DATA", "DOCU", "CPLX", "RUSE", "TIME", "STOR", "PVOL", "ACAP", "APEX", "PCAP", "PLEX", "LTEX", "PCON", "TOOL", "SITE", "USR1", "USR2", "SCED"};
    public static final String SFS[] = {"PREC", "FLEX", "RESL", "TEAM", "PMAT"};
    public static final String FPS[] = {"ILF", "EIF", "EI", "EO", "EQ"};
    public static final String FPS2[] = {"InternalLogicalFiles", "ExternalInterfaceFiles", "ExternalInputs", "ExternalOutputs", "ExternalInquiries"};
    public static final String RISKS[]={"SCHEDELE","PRODUCT","PLATFORM","PERSONNEL","PROCESS","REUSE","TOTAL"};
    public static final String FTS[] = {"Low", "Average", "High", "SubTotal"};
    public static final String COPSEMOS[] = {"Inception", "Elaboration", "Construction", "Transition"};
    public static final String Ratings[] = {"VLO", "LO", "NOM", "HI", "VHI", "XHI"};
    public static final String Increments[] = {"0%", "25%", "50%", "75%"};
    public static final String RatioTypes[] = {"Jones", "David"};
    public static final String CalculationMethods[] = {"Using Table", "Input Calculated Function Points"};
}
