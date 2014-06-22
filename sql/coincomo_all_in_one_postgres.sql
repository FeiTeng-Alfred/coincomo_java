----------------------------------------------------------------------
--  Drop COINCOMO Database Functions, Tables, Composite Types, and Enums
--  
--  Author:
--  Larry Chen
--  
--  Acronyms used:
--  EAF = Effort Adjustment Factor
--  SF  = Scale Factor
--  FP  = Function Point
--  AAR = Adaptation And Reuse
--  ILF = Internal Logical Files
--  EIF = External Interface Files
--  EI  = External Inputs
--  EO  = External Outputs
--  EQ  = External Inquiries
--  PM  = Person Month
----------------------------------------------------------------------

---///////////////////////////////////////////////////////////////////
-- COINCOMO Drops for Functions
---///////////////////////////////////////////////////////////////////

DROP TYPE IF EXISTS coincomo_copsemo_type CASCADE;
DROP TYPE IF EXISTS coincomo_function_point_type CASCADE;
DROP TYPE IF EXISTS coincomo_function_point_weight_type CASCADE;
DROP TYPE IF EXISTS coincomo_cost_driver_type CASCADE;
DROP TYPE IF EXISTS coincomo_cost_driver_weight_type CASCADE;

DROP TYPE IF EXISTS rating_enum CASCADE;
DROP TYPE IF EXISTS increment_enum CASCADE;
DROP TYPE IF EXISTS ratio_type_enum CASCADE;
DROP TYPE IF EXISTS calculation_method_enum CASCADE;

CREATE TYPE rating_enum AS ENUM ('VLO', 'LO', 'NOM', 'HI', 'VHI', 'XHI');
CREATE TYPE increment_enum AS ENUM ('0%', '25%', '50%', '75%');
CREATE TYPE ratio_type_enum AS ENUM ('Jones', 'David');
CREATE TYPE calculation_method_enum AS ENUM ('Using Table', 'Input Calculated Function Points');

CREATE TYPE coincomo_cost_driver_weight_type AS (
	_vlo	NUMERIC,
	_lo	NUMERIC,
	_nom	NUMERIC,
	_hi	NUMERIC,
	_vhi	NUMERIC,
	_xhi	NUMERIC
);

CREATE TYPE coincomo_cost_driver_type AS (
	_rating		rating_enum,		-- 'VLO', 'LO', 'NOM', 'HI', 'VHI', 'XHI'
	_increment	increment_enum		-- '0%', '25%', '50%', '75%'
);

CREATE TYPE coincomo_function_point_weight_type AS (
	_low		INTEGER,
	_average	INTEGER,
	_high		INTEGER
);

CREATE TYPE coincomo_function_point_type AS (
	_low		INTEGER,
	_average	INTEGER,
	_high		INTEGER,
	subtotal	INTEGER
);

CREATE TYPE coincomo_copsemo_type AS (
	_effort_percentage	NUMERIC,
	_schedule_percentage	NUMERIC,
	effort			NUMERIC,
	month			NUMERIC,
	personnel		NUMERIC
);

DROP FUNCTION IF EXISTS Insert_System(VARCHAR(60));
DROP FUNCTION IF EXISTS Insert_SubSystem(VARCHAR(60), BIGINT);
DROP FUNCTION IF EXISTS Insert_Component(VARCHAR(60), BIGINT);
DROP FUNCTION IF EXISTS Insert_SubComponent(VARCHAR(60), BIGINT);
DROP FUNCTION IF EXISTS Insert_AdaptationAndReuse(VARCHAR(60), BIGINT);

DROP FUNCTION IF EXISTS Get_AllSystems();
DROP FUNCTION IF EXISTS Get_System(BIGINT);
DROP FUNCTION IF EXISTS Get_AllSubSystems(BIGINT);
DROP FUNCTION IF EXISTS Get_AllComponents(BIGINT);
DROP FUNCTION IF EXISTS Get_Component_COPSEMO(BIGINT);
DROP FUNCTION IF EXISTS Get_Component_Parameters(BIGINT);
DROP FUNCTION IF EXISTS Get_AllSubComponents(BIGINT);
DROP FUNCTION IF EXISTS Get_AllAdaptationAndReuses(BIGINT);

DROP FUNCTION IF EXISTS Update_System(BIGINT, VARCHAR(60), BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC);
DROP FUNCTION IF EXISTS Update_SubSystem(BIGINT, VARCHAR(60), BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, INTEGER);
DROP FUNCTION IF EXISTS Update_Component(
		BIGINT, VARCHAR(60), BIGINT, BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, INTEGER, INTEGER,
		rating_enum, increment_enum,
		rating_enum, increment_enum, rating_enum, increment_enum, rating_enum, increment_enum, rating_enum, increment_enum, rating_enum, increment_enum);
DROP FUNCTION IF EXISTS Update_Component_COPSEMO(
		BIGINT,							-- Component ID PK
		NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC,		-- Inception
		NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC,		-- Elaboration
		NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC,		-- Construction
		NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC,		-- Transition
		INTEGER);						-- Revision
DROP FUNCTION IF EXISTS Update_Component_Parameters(
		BIGINT,
		coincomo_cost_driver_weight_type,	-- EAF RELY
		coincomo_cost_driver_weight_type,	-- EAF DATA
		coincomo_cost_driver_weight_type,	-- EAF DOCU
		coincomo_cost_driver_weight_type,	-- EAF CPLX
		coincomo_cost_driver_weight_type,	-- EAF RUSE
		coincomo_cost_driver_weight_type,	-- EAF TIME
		coincomo_cost_driver_weight_type,	-- EAF STOR
		coincomo_cost_driver_weight_type,	-- EAF PVOL
		coincomo_cost_driver_weight_type,	-- EAF ACAP
		coincomo_cost_driver_weight_type,	-- EAF APEX
		coincomo_cost_driver_weight_type,	-- EAF PCAP
		coincomo_cost_driver_weight_type,	-- EAF PLEX
		coincomo_cost_driver_weight_type,	-- EAF LTEX
		coincomo_cost_driver_weight_type,	-- EAF PCON
		coincomo_cost_driver_weight_type,	-- EAF TOOL
		coincomo_cost_driver_weight_type,	-- EAF SITE
		coincomo_cost_driver_weight_type,	-- EAF USR1
		coincomo_cost_driver_weight_type,	-- EAF USR2
		coincomo_cost_driver_weight_type,	-- EAF SCED
		coincomo_cost_driver_weight_type,	-- SF PREC
		coincomo_cost_driver_weight_type,	-- SF FLEX
		coincomo_cost_driver_weight_type,	-- SF RESL
		coincomo_cost_driver_weight_type,	-- SF TEAM
		coincomo_cost_driver_weight_type,	-- SF PMAT
		INTEGER, INTEGER, INTEGER,		-- FP ILF
		INTEGER, INTEGER, INTEGER,		-- FP EIF
		INTEGER, INTEGER, INTEGER,		-- FP EI
		INTEGER, INTEGER, INTEGER,		-- FP EO
		INTEGER, INTEGER, INTEGER,		-- FP EQ
		NUMERIC,				-- EQ A
		NUMERIC,				-- EQ B
		NUMERIC,				-- EQ C
		NUMERIC,				-- EQ D
		NUMERIC,				-- Hours Per PM
		INTEGER					-- Revision
	);
DROP FUNCTION IF EXISTS Update_SubComponent(
		BIGINT, VARCHAR(60), BIGINT, BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, BIGINT,  NUMERIC, NUMERIC, VARCHAR(60),
		rating_enum, increment_enum,				-- EAF RELY
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,				-- EAF USR2
		BIGINT,							-- NEW _new_sloc
		INTEGER, ratio_type_enum, calculation_method_enum,	-- FP _multiplier, _ratio_type, _calculation_method
		INTEGER, INTEGER, INTEGER, INTEGER,			-- FP ILF (_low, _average, _high, subtotal)
		INTEGER, INTEGER, INTEGER, INTEGER,			-- FP EIF
		INTEGER, INTEGER, INTEGER, INTEGER,			-- FP EI
		INTEGER, INTEGER, INTEGER, INTEGER,			-- FP EO
		INTEGER, INTEGER, INTEGER, INTEGER,			-- FP EQ
		INTEGER,						-- FP _total_unadjusted_function_points
		BIGINT							-- FP equivalent_sloc
	);
DROP FUNCTION IF EXISTS Update_AdaptationAndReuse(BIGINT, VARCHAR(60), BIGINT, BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, BIGINT);

DROP FUNCTION IF EXISTS Delete_System(BIGINT);
DROP FUNCTION IF EXISTS Delete_SubSystem(BIGINT);
DROP FUNCTION IF EXISTS Delete_Component(BIGINT);
DROP FUNCTION IF EXISTS Delete_SubComponent(BIGINT);
DROP FUNCTION IF EXISTS Delete_AdaptationAndReuse(BIGINT);

DROP FUNCTION IF EXISTS Copy_AdaptationAndReuse(BIGINT, BIGINT);
DROP FUNCTION IF EXISTS Copy_SubComponent(BIGINT, BIGINT);
DROP FUNCTION IF EXISTS Copy_Component(BIGINT, BIGINT);
DROP FUNCTION IF EXISTS Copy_SubSystem(BIGINT, BIGINT);
DROP FUNCTION IF EXISTS Copy_System(BIGINT);

DROP FUNCTION IF EXISTS Update_SystemName(BIGINT, VARCHAR(60));
DROP FUNCTION IF EXISTS Update_SubSystemName(BIGINT, VARCHAR(60));
DROP FUNCTION IF EXISTS Update_ComponentName(BIGINT, VARCHAR(60));
DROP FUNCTION IF EXISTS Update_SubComponentName(BIGINT, VARCHAR(60));
DROP FUNCTION IF EXISTS Update_AdaptationAndReuseName(BIGINT, VARCHAR(60));

DROP FUNCTION IF EXISTS Clear_AllSubSystems(BIGINT);

DROP FUNCTION IF EXISTS Has_SystemName(VARCHAR(60));
DROP FUNCTION IF EXISTS Get_DefaultSystemName();

---///////////////////////////////////////////////////////////////////
-- COINCOMO Drops for Enums, Composite Types and Tables
---///////////////////////////////////////////////////////////////////

DROP TABLE IF EXISTS coincomo_adaptation_and_reuses_table;
DROP TABLE IF EXISTS coincomo_subcomponent_function_points_table;
DROP TABLE IF EXISTS coincomo_subcomponent_new_slocs_table;
DROP TABLE IF EXISTS coincomo_subcomponent_eafs_table;
DROP TABLE IF EXISTS coincomo_subcomponents_table;
DROP TABLE IF EXISTS coincomo_component_parameter_hours_table;
DROP TABLE IF EXISTS coincomo_component_parameter_eqs_table;
DROP TABLE IF EXISTS coincomo_component_parameter_fps_table;
DROP TABLE IF EXISTS coincomo_component_parameter_sfs_table;
DROP TABLE IF EXISTS coincomo_component_parameter_eafs_table;
DROP TABLE IF EXISTS coincomo_component_copsemos_table;
DROP TABLE IF EXISTS coincomo_component_eafs_table;
DROP TABLE IF EXISTS coincomo_component_sfs_table;
DROP TABLE IF EXISTS coincomo_components_Table;
DROP TABLE IF EXISTS coincomo_subsystems_table;
DROP TABLE IF EXISTS coincomo_systems_table;

DROP TYPE IF EXISTS coincomo_copsemo_type;
DROP TYPE IF EXISTS coincomo_function_point_type;
DROP TYPE IF EXISTS coincomo_function_point_weight_type;
DROP TYPE IF EXISTS coincomo_cost_driver_type;
DROP TYPE IF EXISTS coincomo_cost_driver_weight_type;

DROP TYPE IF EXISTS rating_enum;
DROP TYPE IF EXISTS increment_enum;
DROP TYPE IF EXISTS ratio_type_enum;
DROP TYPE IF EXISTS calculation_method_enum;




----------------------------------------------------------------------
--  Create COINCOMO Database Enums, Composite Types, and Tables
--  
--  Author:
--  Larry Chen
--  
--  Acronyms used:
--  EAF = Effort Adjustment Factor
--  SF  = Scale Factor
--  FP  = Function Point
--  AAR = Adaptation And Reuse
--  ILF = Internal Logical Files
--  EIF = External Interface Files
--  EI  = External Inputs
--  EO  = External Outputs
--  EQ  = External Inquiries
--  PM  = Person Month
----------------------------------------------------------------------


---///////////////////////////////////////////////////////////////////
-- COINCOMO Enums for Tables
---///////////////////////////////////////////////////////////////////

CREATE TYPE rating_enum AS ENUM ('VLO', 'LO', 'NOM', 'HI', 'VHI', 'XHI');
CREATE TYPE increment_enum AS ENUM ('0%', '25%', '50%', '75%');
CREATE TYPE ratio_type_enum AS ENUM ('Jones', 'David');
CREATE TYPE calculation_method_enum AS ENUM ('Using Table', 'Input Calculated Function Points');


---///////////////////////////////////////////////////////////////////
-- COINCOMO Composite Types for Tables
---///////////////////////////////////////////////////////////////////

CREATE TYPE coincomo_cost_driver_weight_type AS (
	_vlo	NUMERIC,
	_lo	NUMERIC,
	_nom	NUMERIC,
	_hi	NUMERIC,
	_vhi	NUMERIC,
	_xhi	NUMERIC
);

CREATE TYPE coincomo_cost_driver_type AS (
	_rating		rating_enum,		-- 'VLO', 'LO', 'NOM', 'HI', 'VHI', 'XHI'
	_increment	increment_enum		-- '0%', '25%', '50%', '75%'
);

CREATE TYPE coincomo_function_point_weight_type AS (
	_low		INTEGER,
	_average	INTEGER,
	_high		INTEGER
);

CREATE TYPE coincomo_function_point_type AS (
	_low		INTEGER,
	_average	INTEGER,
	_high		INTEGER,
	subtotal	INTEGER
);

CREATE TYPE coincomo_copsemo_type AS (
	_effort_percentage	NUMERIC,
	_schedule_percentage	NUMERIC,
	effort			NUMERIC,
	month			NUMERIC,
	personnel		NUMERIC
);


---///////////////////////////////////////////////////////////////////
-- COINCOMO Tables
---///////////////////////////////////////////////////////////////////

----------------------------------------
-- COINCOMO USERS Table
----------------------------------------

CREATE TABLE coincomo_user_table
(
  user_id bigserial NOT NULL,
  user_login_id character varying(60) NOT NULL,
  password character varying(60) NOT NULL,
  priority integer NOT NULL,
  CONSTRAINT coincomo_user_table_pkey PRIMARY KEY (user_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE coincomo_user_table
  OWNER TO postgres;

  
----------------------------------------------
-- COINCOMO USERS AND SYSTEM RELATIONAL Table
----------------------------------------------

CREATE TABLE coincomo_user_system_table
(
  user_id bigint NOT NULL,
  system_id bigint NOT NULL,
  CONSTRAINT coincomo_user_system_table_pkey PRIMARY KEY (user_id, system_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE coincomo_user_system_table
  OWNER TO postgres;  
  
  
----------------------------------------
-- COINCOMO GROUP Table
----------------------------------------

CREATE TABLE coincomo_group_table
(
  user_id bigint NOT NULL,
  system_id bigint NOT NULL,
  user_login_id character varying(60) NOT NULL,
  CONSTRAINT coincomo_group_table_pkey PRIMARY KEY (user_id, system_id, user_login_id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE coincomo_group_table
  OWNER TO postgres;




----------------------------------------
-- COINCOMO Systems Table
----------------------------------------
CREATE TABLE coincomo_systems_table (
	system_id	BIGSERIAL PRIMARY KEY,
	_system_name	VARCHAR(60) NOT NULL,
	sloc		BIGINT NOT NULL DEFAULT 0 CHECK (sloc >= 0),
	cost		NUMERIC NOT NULL DEFAULT 0.0 CHECK (cost >= 0.0),
	staff		NUMERIC NOT NULL DEFAULT 0.0 CHECK (staff >= 0.0),
	effort		NUMERIC NOT NULL DEFAULT 0.0 CHECK (effort >= 0.0),
	schedule	NUMERIC NOT NULL DEFAULT 0.0 CHECK (schedule >= 0.0)
);

----------------------------------------
-- COINCOMO SubSystems Table
----------------------------------------
CREATE TABLE coincomo_subsystems_table (
	subsystem_id	BIGSERIAL PRIMARY KEY,
	_subsystem_name	VARCHAR(60) NOT NULL,
	system_id	BIGINT NOT NULL REFERENCES coincomo_systems_table (system_id) ON DELETE CASCADE,
	sloc		BIGINT NOT NULL DEFAULT 0 CHECK (sloc >= 0),
	cost		NUMERIC NOT NULL DEFAULT 0.0 CHECK (cost >= 0.0),
	staff		NUMERIC NOT NULL DEFAULT 0.0 CHECK (staff >= 0.0),
	effort		NUMERIC NOT NULL DEFAULT 0.0 CHECK (effort >= 0.0),
	schedule	NUMERIC NOT NULL DEFAULT 0.0 CHECK (schedule >= 0.0),
	_zoom_level	INTEGER NOT NULL DEFAULT 100 CHECK (_zoom_level >= 0 AND _zoom_level <= 100)
);

----------------------------------------
-- COINCOMO Components Table
--
-- HAS 4 one-to-one associated tables
----------------------------------------
CREATE TABLE coincomo_components_table (
	component_id		BIGSERIAL PRIMARY KEY,
	_component_name		VARCHAR(60) NOT NULL,
	subsystem_id		BIGINT NOT NULL REFERENCES coincomo_subsystems_table (subsystem_id) ON DELETE CASCADE,
	sloc			BIGINT NOT NULL DEFAULT 0 CHECK (sloc >= 0),
	cost			NUMERIC NOT NULL DEFAULT 0.0 CHECK (cost >= 0.0),
	staff			NUMERIC NOT NULL DEFAULT 0.0 CHECK (staff >= 0.0),
	effort			NUMERIC NOT NULL DEFAULT 0.0 CHECK (effort >= 0.0),
	schedule		NUMERIC NOT NULL DEFAULT 0.0 CHECK (schedule >= 0.0),
	sf			NUMERIC NOT NULL DEFAULT 0.0 CHECK (sf >= 0.0),
	sced			NUMERIC NOT NULL DEFAULT 0.0 CHECK (sced >= 0.0),
	scedPercent		NUMERIC NOT NULL DEFAULT 0.0 CHECK (scedPercent >= 0.0),
	_multiBuildShift	INTEGER NOT NULL DEFAULT 0 CHECK(_multiBuildShift >= 0),
	_revision		INTEGER NOT NULL DEFAULT 1 CHECK (_revision >= 1)
);

----------------------------------------
-- COINCOMO EAFs Table for Components
--
-- One-to-one relationship with coincomo_components_table
--
-- STORES ONLY SCED (schedule) cost driver's settings
----------------------------------------
CREATE TABLE coincomo_component_eafs_table (
	component_id	BIGINT PRIMARY KEY REFERENCES coincomo_components_table (component_id) ON DELETE CASCADE,
	_eaf_sced	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%')	-- row(_rating, _increment)
);

----------------------------------------
-- COINCOMO SFs Table for Components
--
-- One-to-one relationship with coincomo_components_table
--
-- STORES ALL scale factors' cost drivers' settings
----------------------------------------
CREATE TABLE coincomo_component_sfs_table (
	component_id	BIGINT PRIMARY KEY REFERENCES coincomo_components_table (component_id) ON DELETE CASCADE,
	_sf_prec	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),	-- row(_rating, _increment)
	_sf_flex	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_sf_resl	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_sf_team	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_sf_pmat	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%')
);

----------------------------------------
-- COINCOMO COPSEMOs Table for Components
--
-- One-to-one relationship with coincomo_components_table
--
-- STORES all COPSEMO values
----------------------------------------
CREATE TABLE coincomo_component_copsemos_table (
	component_id	BIGINT PRIMARY KEY REFERENCES coincomo_components_table (component_id) ON DELETE CASCADE,
	_inception	coincomo_copsemo_type NOT NULL DEFAULT row(6.0, 12.5, 0.0, 0.0, 0.0)						-- row(_effort_percentage, _schedule_percentage, effort, month, personnel)
				CHECK (     (_inception)._effort_percentage >= 2.0 AND (_inception)._effort_percentage <= 15.0
					AND (_inception)._schedule_percentage >= 2.0 AND (_inception)._schedule_percentage <= 30.0
					AND (_inception).effort >= 0.0
					AND (_inception).month >= 0.0
					AND (_inception).personnel >= 0.0
				),
	_elaboration	coincomo_copsemo_type NOT NULL DEFAULT row(24.0, 37.5, 0.0, 0.0, 0.0)
				CHECK (     (_elaboration)._effort_percentage >= 20.0 AND (_elaboration)._effort_percentage <= 28.0
					AND (_elaboration)._schedule_percentage >= 33.0 AND (_elaboration)._schedule_percentage <= 42.0
					AND (_elaboration).effort >= 0.0
					AND (_elaboration).month >= 0.0
					AND (_elaboration).personnel >= 0.0
				),
	_construction	coincomo_copsemo_type NOT NULL DEFAULT row(76.0, 62.5, 0.0, 0.0, 0.0)
				CHECK (     (_construction)._effort_percentage >= 72.0 AND (_construction)._effort_percentage <= 80.0
					AND (_construction)._schedule_percentage >= 58.0 AND (_construction)._schedule_percentage <= 67.0
					AND (_construction).effort >= 0.0
					AND (_construction).month >= 0.0
					AND (_construction).personnel >= 0.0
				),
	_transition	coincomo_copsemo_type NOT NULL DEFAULT row(12.0, 12.5, 0.0, 0.0, 0.0)
				CHECK (     (_transition)._effort_percentage >= 0.0 AND (_transition)._effort_percentage <= 20.0
					AND (_transition)._schedule_percentage >= 0.0 AND (_transition)._schedule_percentage <= 20.0
					AND (_transition).effort >= 0.0
					AND (_transition).month >= 0.0
					AND (_transition).personnel >= 0.0
				),
	_revision	INTEGER NOT NULL DEFAULT 1 CHECK (_revision >= 1),
	CHECK ( ((_elaboration)._effort_percentage + (_construction)._effort_percentage) = 100.0 AND ((_elaboration)._schedule_percentage + (_construction)._schedule_percentage) = 100.0)
);

----------------------------------------
-- COINCOMO Parameters Tables for Components
--
-- One-to-one relationship with coincomo_components_table
--
-- STORES all Parameters' weights and values
--    1. EAF cost driver weights
--    2. SF cost driver weights
--    3. FP weights
--    4. Equation constants
--    5. Hours per PM
----------------------------------------
CREATE TABLE coincomo_component_parameter_eafs_table (
	component_id			BIGINT PRIMARY KEY REFERENCES coincomo_components_table (component_id) ON DELETE CASCADE,
	_eaf_rely			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(0.82, 0.92, 1.00, 1.10, 1.26, 0.00)	-- row(_vlo, _lo, _nom, _hi, _vhi, _xhi)
						CHECK (     (_eaf_rely)._vlo >= 0.0
							AND (_eaf_rely)._lo >= 0.0
							AND (_eaf_rely)._nom >= 0.0
							AND (_eaf_rely)._hi >= 0.0
							AND (_eaf_rely)._vhi >= 0.0
							AND (_eaf_rely)._xhi >= 0.0
						),
	_eaf_data			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(0.00, 0.90, 1.00, 1.14, 1.28, 0.00)
						CHECK (     (_eaf_data)._vlo >= 0.0
							AND (_eaf_data)._lo >= 0.0
							AND (_eaf_data)._nom >= 0.0
							AND (_eaf_data)._hi >= 0.0
							AND (_eaf_data)._vhi >= 0.0
							AND (_eaf_data)._xhi >= 0.0
						),
	_eaf_docu			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(0.81, 0.91, 1.00, 1.11, 1.23, 0.00)
						CHECK (     (_eaf_docu)._vlo >= 0.0
							AND (_eaf_docu)._lo >= 0.0
							AND (_eaf_docu)._nom >= 0.0
							AND (_eaf_docu)._hi >= 0.0
							AND (_eaf_docu)._vhi >= 0.0
							AND (_eaf_docu)._xhi >= 0.0
						),
	_eaf_cplx			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(0.73, 0.87, 1.00, 1.17, 1.34, 1.74)
						CHECK (     (_eaf_cplx)._vlo >= 0.0
							AND (_eaf_cplx)._lo >= 0.0
							AND (_eaf_cplx)._nom >= 0.0
							AND (_eaf_cplx)._hi >= 0.0
							AND (_eaf_cplx)._vhi >= 0.0
							AND (_eaf_cplx)._xhi >= 0.0
						),
	_eaf_ruse			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(0.00, 0.95, 1.00, 1.07, 1.15, 1.24)
						CHECK (     (_eaf_ruse)._vlo >= 0.0
							AND (_eaf_ruse)._lo >= 0.0
							AND (_eaf_ruse)._nom >= 0.0
							AND (_eaf_ruse)._hi >= 0.0
							AND (_eaf_ruse)._vhi >= 0.0
							AND (_eaf_ruse)._xhi >= 0.0
						),
	_eaf_time			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(0.00, 0.00, 1.00, 1.11, 1.29, 1.63)
						CHECK (     (_eaf_time)._vlo >= 0.0
							AND (_eaf_time)._lo >= 0.0
							AND (_eaf_time)._nom >= 0.0
							AND (_eaf_time)._hi >= 0.0
							AND (_eaf_time)._vhi >= 0.0
							AND (_eaf_time)._xhi >= 0.0
						),
	_eaf_stor			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(0.00, 0.00, 1.00, 1.05, 1.17, 1.46)
						CHECK (     (_eaf_stor)._vlo >= 0.0
							AND (_eaf_stor)._lo >= 0.0
							AND (_eaf_stor)._nom >= 0.0
							AND (_eaf_stor)._hi >= 0.0
							AND (_eaf_stor)._vhi >= 0.0
							AND (_eaf_stor)._xhi >= 0.0
						),
	_eaf_pvol			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(0.00, 0.87, 1.00, 1.15, 1.30, 0.00)
						CHECK (     (_eaf_pvol)._vlo >= 0.0
							AND (_eaf_pvol)._lo >= 0.0
							AND (_eaf_pvol)._nom >= 0.0
							AND (_eaf_pvol)._hi >= 0.0
							AND (_eaf_pvol)._vhi >= 0.0
							AND (_eaf_pvol)._xhi >= 0.0
						),
	_eaf_acap			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.42, 1.19, 1.00, 0.85, 0.71, 0.00)
						CHECK (     (_eaf_acap)._vlo >= 0.0
							AND (_eaf_acap)._lo >= 0.0
							AND (_eaf_acap)._nom >= 0.0
							AND (_eaf_acap)._hi >= 0.0
							AND (_eaf_acap)._vhi >= 0.0
							AND (_eaf_acap)._xhi >= 0.0
						),
	_eaf_apex			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.22, 1.10, 1.00, 0.88, 0.81, 0.00)
						CHECK (     (_eaf_apex)._vlo >= 0.0
							AND (_eaf_apex)._lo >= 0.0
							AND (_eaf_apex)._nom >= 0.0
							AND (_eaf_apex)._hi >= 0.0
							AND (_eaf_apex)._vhi >= 0.0
							AND (_eaf_apex)._xhi >= 0.0
						),
	_eaf_pcap			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.34, 1.15, 1.00, 0.88, 0.76, 0.00)
						CHECK (     (_eaf_pcap)._vlo >= 0.0
							AND (_eaf_pcap)._lo >= 0.0
							AND (_eaf_pcap)._nom >= 0.0
							AND (_eaf_pcap)._hi >= 0.0
							AND (_eaf_pcap)._vhi >= 0.0
							AND (_eaf_pcap)._xhi >= 0.0
						),
	_eaf_plex			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.19, 1.09, 1.00, 0.91, 0.85, 0.00)
						CHECK (     (_eaf_plex)._vlo >= 0.0
							AND (_eaf_plex)._lo >= 0.0
							AND (_eaf_plex)._nom >= 0.0
							AND (_eaf_plex)._hi >= 0.0
							AND (_eaf_plex)._vhi >= 0.0
							AND (_eaf_plex)._xhi >= 0.0
						),
	_eaf_ltex			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.20, 1.09, 1.00, 0.91, 0.84, 0.00)
						CHECK (     (_eaf_ltex)._vlo >= 0.0
							AND (_eaf_ltex)._lo >= 0.0
							AND (_eaf_ltex)._nom >= 0.0
							AND (_eaf_ltex)._hi >= 0.0
							AND (_eaf_ltex)._vhi >= 0.0
							AND (_eaf_ltex)._xhi >= 0.0
						),
	_eaf_pcon			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.29, 1.12, 1.00, 0.90, 0.81, 0.00)
						CHECK (     (_eaf_pcon)._vlo >= 0.0
							AND (_eaf_pcon)._lo >= 0.0
							AND (_eaf_pcon)._nom >= 0.0
							AND (_eaf_pcon)._hi >= 0.0
							AND (_eaf_pcon)._vhi >= 0.0
							AND (_eaf_pcon)._xhi >= 0.0
						),
	_eaf_tool			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.17, 1.09, 1.00, 0.90, 0.78, 0.00)
						CHECK (     (_eaf_tool)._vlo >= 0.0
							AND (_eaf_tool)._lo >= 0.0
							AND (_eaf_tool)._nom >= 0.0
							AND (_eaf_tool)._hi >= 0.0
							AND (_eaf_tool)._vhi >= 0.0
							AND (_eaf_tool)._xhi >= 0.0
						),
	_eaf_site			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.43, 1.14, 1.00, 1.00, 1.00, 0.00)
						CHECK (     (_eaf_site)._vlo >= 0.0
							AND (_eaf_site)._lo >= 0.0
							AND (_eaf_site)._nom >= 0.0
							AND (_eaf_site)._hi >= 0.0
							AND (_eaf_site)._vhi >= 0.0
							AND (_eaf_site)._xhi >= 0.0
						),
	_eaf_usr1			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.00, 1.00, 1.00, 1.00, 1.00, 1.00)
						CHECK (     (_eaf_usr1)._vlo >= 0.0
							AND (_eaf_usr1)._lo >= 0.0
							AND (_eaf_usr1)._nom >= 0.0
							AND (_eaf_usr1)._hi >= 0.0
							AND (_eaf_usr1)._vhi >= 0.0
							AND (_eaf_usr1)._xhi >= 0.0
						),
	_eaf_usr2			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.00, 1.00, 1.00, 1.00, 1.00, 1.00)
						CHECK (     (_eaf_usr2)._vlo >= 0.0
							AND (_eaf_usr2)._lo >= 0.0
							AND (_eaf_usr2)._nom >= 0.0
							AND (_eaf_usr2)._hi >= 0.0
							AND (_eaf_usr2)._vhi >= 0.0
							AND (_eaf_usr2)._xhi >= 0.0
						),
	_eaf_sced			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(1.22, 1.09, 1.00, 0.93, 0.86, 0.80)
						CHECK (     (_eaf_sced)._vlo >= 0.0
							AND (_eaf_sced)._lo >= 0.0
							AND (_eaf_sced)._nom >= 0.0
							AND (_eaf_sced)._hi >= 0.0
							AND (_eaf_sced)._vhi >= 0.0
							AND (_eaf_sced)._xhi >= 0.0
						),
	_revision			INTEGER NOT NULL DEFAULT 1 CHECK (_revision >= 1)
);

CREATE TABLE coincomo_component_parameter_sfs_table (
	component_id			BIGINT PRIMARY KEY REFERENCES coincomo_components_table (component_id) ON DELETE CASCADE,
	_sf_prec			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(6.20, 4.96, 3.72, 2.48, 1.24, 0.00)
						CHECK (     (_sf_prec)._vlo >= 0.0
							AND (_sf_prec)._lo >= 0.0
							AND (_sf_prec)._nom >= 0.0
							AND (_sf_prec)._hi >= 0.0
							AND (_sf_prec)._vhi >= 0.0
							AND (_sf_prec)._xhi >= 0.0
						),
	_sf_flex			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(5.07, 4.05, 3.04, 2.03, 1.01, 0.00)
						CHECK (     (_sf_flex)._vlo >= 0.0
							AND (_sf_flex)._lo >= 0.0
							AND (_sf_flex)._nom >= 0.0
							AND (_sf_flex)._hi >= 0.0
							AND (_sf_flex)._vhi >= 0.0
							AND (_sf_flex)._xhi >= 0.0
						),
	_sf_resl			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(7.07, 5.65, 4.24, 2.83, 1.41, 0.00)
						CHECK (     (_sf_resl)._vlo >= 0.0
							AND (_sf_resl)._lo >= 0.0
							AND (_sf_resl)._nom >= 0.0
							AND (_sf_resl)._hi >= 0.0
							AND (_sf_resl)._vhi >= 0.0
							AND (_sf_resl)._xhi >= 0.0
						),
	_sf_team			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(5.48, 4.38, 3.29, 2.19, 1.10, 0.00)
						CHECK (     (_sf_team)._vlo >= 0.0
							AND (_sf_team)._lo >= 0.0
							AND (_sf_team)._nom >= 0.0
							AND (_sf_team)._hi >= 0.0
							AND (_sf_team)._vhi >= 0.0
							AND (_sf_team)._xhi >= 0.0
						),
	_sf_pmat			coincomo_cost_driver_weight_type NOT NULL DEFAULT row(7.80, 6.24, 4.68, 3.12, 1.56, 0.00)
						CHECK (     (_sf_pmat)._vlo >= 0.0
							AND (_sf_pmat)._lo >= 0.0
							AND (_sf_pmat)._nom >= 0.0
							AND (_sf_pmat)._hi >= 0.0
							AND (_sf_pmat)._vhi >= 0.0
							AND (_sf_pmat)._xhi >= 0.0
						),
	_revision			INTEGER NOT NULL DEFAULT 1 CHECK (_revision >= 1)
);

CREATE TABLE coincomo_component_parameter_fps_table (
	component_id			BIGINT PRIMARY KEY REFERENCES coincomo_components_table (component_id) ON DELETE CASCADE,
	_fp_internal_logical_files	coincomo_function_point_weight_type NOT NULL DEFAULT row(7, 10, 15)				-- row(_low, _average, _high)
						CHECK (     (_fp_internal_logical_files)._low >= 0 AND (_fp_internal_logical_files)._low <= (_fp_internal_logical_files)._average
							AND (_fp_internal_logical_files)._average >= 0 AND (_fp_internal_logical_files)._average <= (_fp_internal_logical_files)._high
							AND (_fp_internal_logical_files)._high >= 0
						),
	_fp_external_interface_files	coincomo_function_point_weight_type NOT NULL DEFAULT row(5, 7, 10)
						CHECK (     (_fp_external_interface_files)._low >= 0 AND (_fp_external_interface_files)._low <= (_fp_external_interface_files)._average
							AND (_fp_external_interface_files)._average >= 0 AND (_fp_external_interface_files)._average <= (_fp_external_interface_files)._high
							AND (_fp_external_interface_files)._high >= 0
						),
	_fp_external_inputs		coincomo_function_point_weight_type NOT NULL DEFAULT row(3, 4, 6)
						CHECK (     (_fp_external_inputs)._low >= 0 AND (_fp_external_inputs)._low <= (_fp_external_inputs)._average
							AND (_fp_external_inputs)._average >= 0 AND (_fp_external_inputs)._average <= (_fp_external_inputs)._high
							AND (_fp_external_inputs)._high >= 0
						),
	_fp_external_outputs		coincomo_function_point_weight_type NOT NULL DEFAULT row(4, 5, 7)
						CHECK (     (_fp_external_outputs)._low >= 0 AND (_fp_external_outputs)._low <= (_fp_external_outputs)._average
							AND (_fp_external_outputs)._average >= 0 AND (_fp_external_outputs)._average <= (_fp_external_outputs)._high
							AND (_fp_external_outputs)._high >= 0
						),
	_fp_external_inquiries		coincomo_function_point_weight_type NOT NULL DEFAULT row(3, 4, 6)
						CHECK (     (_fp_external_inquiries)._low >= 0 AND (_fp_external_inquiries)._low <= (_fp_external_inquiries)._average
							AND (_fp_external_inquiries)._average >= 0 AND (_fp_external_inquiries)._average <= (_fp_external_inquiries)._high
							AND (_fp_external_inquiries)._high >= 0
						),
	_revision			INTEGER NOT NULL DEFAULT 1 CHECK (_revision >= 1)
);

CREATE TABLE coincomo_component_parameter_eqs_table (
	component_id			BIGINT PRIMARY KEY REFERENCES coincomo_components_table (component_id) ON DELETE CASCADE,
	_eq_a				NUMERIC NOT NULL DEFAULT 2.94 CHECK (_eq_a >= 0.0 AND _eq_d <= 10.0),
	_eq_b				NUMERIC NOT NULL DEFAULT 0.91 CHECK (_eq_b >= 0.0 AND _eq_d <= 10.0),
	_eq_c				NUMERIC NOT NULL DEFAULT 3.67 CHECK (_eq_c >= 0.0 AND _eq_d <= 10.0),
	_eq_d				NUMERIC NOT NULL DEFAULT 0.28 CHECK (_eq_d >= 0.0 AND _eq_d <= 10.0),
	_revision			INTEGER NOT NULL DEFAULT 1 CHECK (_revision >= 1)
);

CREATE TABLE coincomo_component_parameter_hours_table (
	component_id			BIGINT PRIMARY KEY REFERENCES coincomo_components_table (component_id) ON DELETE CASCADE,
	_hours_per_pm			NUMERIC NOT NULL DEFAULT 152.0 CHECK (_hours_per_pm >= 120.0 AND _hours_per_pm <= 184.0),
	_revision			INTEGER NOT NULL DEFAULT 1 CHECK (_revision >= 1)
);
----------------------------------------
-- COINCOMO SubComponents Table
--
-- HAS 3 one-to-one associated tables
----------------------------------------
CREATE TABLE coincomo_subcomponents_table (
	subcomponent_id				BIGSERIAL PRIMARY KEY,
	_subcomponent_name			VARCHAR(60) NOT NULL,
	component_id				BIGINT NOT NULL REFERENCES coincomo_components_table (component_id) ON DELETE CASCADE,
	sloc					BIGINT NOT NULL DEFAULT 0 CHECK (sloc >= 0),
	cost					NUMERIC NOT NULL DEFAULT 0.0 CHECK (cost >= 0.0),
	staff					NUMERIC NOT NULL DEFAULT 0.0 CHECK (staff >= 0.0),
	effort					NUMERIC NOT NULL DEFAULT 0.0 CHECK (effort >= 0.0),
	schedule				NUMERIC NOT NULL DEFAULT 0.0 CHECK (schedule >= 0.0),
	productivity				NUMERIC NOT NULL DEFAULT 0.0 CHECK (productivity >= 0.0),
	instruction_cost			NUMERIC NOT NULL DEFAULT 0.0 CHECK (instruction_cost >= 0.0),
	risk					NUMERIC NOT NULL DEFAULT 0.0 CHECK (risk >= 0.0),
	nominal_effort				NUMERIC NOT NULL DEFAULT 0.0 CHECK (nominal_effort >= 0.0),
	estimated_effort			NUMERIC NOT NULL DEFAULT 0.0 CHECK (estimated_effort >= 0.0),
	eaf					NUMERIC NOT NULL DEFAULT 1.0 CHECK (eaf >= 0.0),
	sum_of_new_sloc_fp_sloc_aar_slocs	BIGINT NOT NULL DEFAULT 0 CHECK (sum_of_new_sloc_fp_sloc_aar_slocs >= 0),
	_labor_rate				NUMERIC NOT NULL DEFAULT 0.0 CHECK (_labor_rate >= 0.0),
	_revl					NUMERIC NOT NULL DEFAULT 0.0 CHECK (_revl >= 0.0 AND _revl <= 100.0),
	_language				VARCHAR(60) NOT NULL DEFAULT 'Non-specified',
	CHECK (sloc >= sum_of_new_sloc_fp_sloc_aar_slocs)
);

----------------------------------------
-- COINCOMO EAFs Table for SubComponent
--
-- One-to-one relationship with coincomo_subcomponents_table
--
-- STORES ALL EAF cost drivers' settings EXCEPT SCED
----------------------------------------
CREATE TABLE coincomo_subcomponent_eafs_table (
	subcomponent_id	BIGINT PRIMARY KEY REFERENCES coincomo_subcomponents_table (subcomponent_id) ON DELETE CASCADE,
	_eaf_rely	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),	-- row(_rating, _increment)
	_eaf_data	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_docu	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_cplx	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_ruse	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_time	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_stor	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_pvol	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_acap	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_apex	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_pcap	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_plex	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_ltex	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_pcon	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_tool	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_site	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_usr1	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%'),
	_eaf_usr2	coincomo_cost_driver_type NOT NULL DEFAULT row('NOM', '0%')
);

----------------------------------------
-- COINCOMO New SLOCs Table for SubComponent
--
-- One-to-one relationship with coincomo_subcomponents_table
--
-- STORES ONLY the new sloc value in New tab
----------------------------------------
CREATE TABLE coincomo_subcomponent_new_slocs_table (
	subcomponent_id	BIGINT PRIMARY KEY REFERENCES coincomo_subcomponents_table (subcomponent_id) ON DELETE CASCADE,
	_new_sloc	BIGINT NOT NULL DEFAULT 0 CHECK (_new_sloc >= 0)
);

----------------------------------------
-- COINCOMO Function Points Table for SubComponent
--
-- One-to-one relationship with coincomo_subcomponents_table
--
-- STORES ALL values related to Function Point tab
----------------------------------------
CREATE TABLE coincomo_subcomponent_function_points_table (
	subcomponent_id				BIGINT PRIMARY KEY REFERENCES coincomo_subcomponents_table (subcomponent_id) ON DELETE CASCADE,
	_multiplier				INTEGER NOT NULL DEFAULT 0 CHECK (_multiplier >= 0),
	_ratio_type				ratio_type_enum NOT NULL DEFAULT 'Jones',				-- enum 'Jones', 'David'
	_calculation_method			calculation_method_enum NOT NULL DEFAULT 'Using Table',			-- enum 'Using Table', 'Input Calculated Function Points'
	_internal_logical_files			coincomo_function_point_type NOT NULL DEFAULT row(0, 0, 0, 0)		-- row(_low, _average, _high, subtotal)
							CHECK (     (_internal_logical_files)._low >= 0 AND (_internal_logical_files)._low <= (_internal_logical_files)._average
								AND (_internal_logical_files)._average >= 0 AND (_internal_logical_files)._average <= (_internal_logical_files)._high
								AND (_internal_logical_files)._high >= 0
								AND (_internal_logical_files).subtotal >= 0
							),
	_external_interface_files		coincomo_function_point_type NOT NULL DEFAULT row(0, 0, 0, 0)
							CHECK (     (_external_interface_files)._low >= 0 AND (_external_interface_files)._low <= (_external_interface_files)._average
								AND (_external_interface_files)._average >= 0 AND (_external_interface_files)._average <= (_external_interface_files)._high
								AND (_external_interface_files)._high >= 0
								AND (_external_interface_files).subtotal >= 0
							),
	_external_inputs			coincomo_function_point_type NOT NULL DEFAULT row(0, 0, 0, 0)
							CHECK (     (_external_inputs)._low >= 0 AND (_external_inputs)._low <= (_external_inputs)._average
								AND (_external_inputs)._average >= 0 AND (_external_inputs)._average <= (_external_inputs)._high
								AND (_external_inputs)._high >= 0
								AND (_external_inputs).subtotal >= 0
							),
	_external_outputs			coincomo_function_point_type NOT NULL DEFAULT row(0, 0, 0, 0)
							CHECK (     (_external_outputs)._low >= 0 AND (_external_outputs)._low <= (_external_outputs)._average
								AND (_external_outputs)._average >= 0 AND (_external_outputs)._average <= (_external_outputs)._high
								AND (_external_outputs)._high >= 0
								AND (_external_outputs).subtotal >= 0
							),
	_external_inquiries			coincomo_function_point_type NOT NULL DEFAULT row(0, 0, 0, 0),
							CHECK (     (_external_inquiries)._low >= 0 AND (_external_inquiries)._low <= (_external_inquiries)._average
								AND (_external_inquiries)._average >= 0 AND (_external_inquiries)._average <= (_external_inquiries)._high
								AND (_external_inquiries)._high >= 0
								AND (_external_inquiries).subtotal >= 0
							),
	_total_unadjusted_function_points	INTEGER NOT NULL DEFAULT 0 CHECK (_total_unadjusted_function_points >= 0),
	equivalent_sloc				BIGINT NOT NULL DEFAULT 0 CHECK (equivalent_sloc >= 0)
);

----------------------------------------
-- COINCOMO AdaptationAndReuses Table
----------------------------------------
CREATE TABLE coincomo_adaptation_and_reuses_table (
	adaptation_and_reuse_id			BIGSERIAL PRIMARY KEY,
	_adaptation_and_reuse_name		VARCHAR(60) NOT NULL,
	subcomponent_id				BIGINT NOT NULL REFERENCES coincomo_subcomponents_table (subcomponent_id) ON DELETE CASCADE,
	_initial_sloc				BIGINT NOT NULL DEFAULT 0 CHECK (_initial_sloc >= 0),
	_design_modified			NUMERIC NOT NULL DEFAULT 0.0 CHECK (_design_modified >= 0.0 AND _design_modified <= 100.0),
	_code_modified				NUMERIC NOT NULL DEFAULT 0.0 CHECK (_code_modified >= 0.0 AND _code_modified <= 100.0),
	_integration_modified			NUMERIC NOT NULL DEFAULT 0.0 CHECK (_integration_modified >= 0.0 AND _integration_modified <= 100.0),
	_software_understanding			NUMERIC NOT NULL DEFAULT 30.0 CHECK (_software_understanding >= 0.0 AND _software_understanding <= 50.0),
	_assessment_and_assimilation		NUMERIC NOT NULL DEFAULT 4.0 CHECK (_assessment_and_assimilation >= 0.0 AND _assessment_and_assimilation <= 8.0),
	_unfamiliarity_with_software		NUMERIC NOT NULL DEFAULT 0.4 CHECK (_unfamiliarity_with_software >= 0.0 AND _unfamiliarity_with_software <= 1.0),
	_automatic_translation			NUMERIC NOT NULL DEFAULT 0.0 CHECK (_automatic_translation >= 0.0 AND _automatic_translation <= 100.0),
	_automatic_translation_productivity	NUMERIC NOT NULL DEFAULT 2400.0 CHECK (_automatic_translation_productivity >= 0.0),
	adaptation_adjustment_factor		NUMERIC NOT NULL DEFAULT 0.0 CHECK (adaptation_adjustment_factor >= 0.0),
	adapted_sloc				BIGINT NOT NULL DEFAULT 0 CHECK (adapted_sloc >= 0)
);




----------------------------------------------------------------------
--  Create COINCOMO Database Functions
--  
--  Author:
--  Larry Chen
--  
--  Acronyms used:
--  EAF = Effort Adjustment Factor
--  SF  = Scale Factor
--  FP  = Function Point
--  AAR = Adaptation And Reuse
--  ILF = Internal Logical Files
--  EIF = External Interface Files
--  EI  = External Inputs
--  EO  = External Outputs
--  EQ  = External Inquiries
--  PM  = Person Month
----------------------------------------------------------------------


---///////////////////////////////////////////////////////////////////
-- COINCOMO Insert Functions
---///////////////////////////////////////////////////////////////////
CREATE OR REPLACE FUNCTION insert_group(bigint, bigint, character varying)
  RETURNS bigint AS
$BODY$
DECLARE
	v_user_id	BIGINT;
BEGIN
	INSERT INTO coincomo_group_table (user_id,system_id,user_login_id) VALUES ($1,$2,$3) RETURNING user_id INTO v_user_id;

	IF FOUND THEN
		RETURN v_user_id;
	ELSE
		RETURN -1;
	END IF;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_group(bigint, bigint, character varying)
  OWNER TO postgres;
---*******************************************************************
-- INSERT a COINCOMO System record with given VARCHAR(60) name
--    and all other fields with default values
--    into the database
-- RETURN the inserted COINCOMO System record PK
---*******************************************************************
CREATE OR REPLACE FUNCTION insert_system(character varying, bigint)
  RETURNS bigint AS
$BODY$
DECLARE
	v_system_id	BIGINT;
BEGIN
	INSERT INTO coincomo_systems_table (_system_name) VALUES ($1) RETURNING system_id INTO v_system_id;

	INSERT INTO coincomo_user_system_table (user_id,system_id) VALUES ($2,v_system_id);
	
	IF FOUND THEN
		RETURN v_system_id;
	ELSE
		RETURN -1;
	END IF;
	
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION insert_system(character varying, bigint)
  OWNER TO postgres;

---*******************************************************************
-- INSERT a COINCOMO SubSystem record with given VARCHAR(60) name,
--    INTEGER as the parent System PK
--    and all other fields with default values
--    into the database
-- RETURN the inserted COINCOMO SubSystem record PK
---*******************************************************************
CREATE OR REPLACE FUNCTION Insert_SubSystem(VARCHAR(60), BIGINT) RETURNS BIGINT AS $$
DECLARE
	v_subsystem_id	BIGINT;
BEGIN
	INSERT INTO coincomo_subsystems_table (_subsystem_name, system_id) VALUES ($1, $2) RETURNING subsystem_id INTO v_subsystem_id;

	IF FOUND THEN
		RETURN v_subsystem_id;
	ELSE
		RETURN -1;
	END IF;
END;
$$ LANGUAGE plpgsql;

---*******************************************************************
-- INSERT a COINCOMO Component record with given VARCHAR(60) name,
--    INTEGER as the parent SubSystem PK
--     and all other fields with default values
--     into the database.
-- Additionally, INSERT the corresponding EAF, SF, COPSEMO,
--     and Parameters records with default values
--     into the database.
-- RETURN the inserted COINCOMO Component record PK
---*******************************************************************
CREATE OR REPLACE FUNCTION Insert_Component(VARCHAR(60), BIGINT) RETURNS BIGINT AS $$
DECLARE
	v_component_id	BIGINT;
BEGIN
	INSERT INTO coincomo_components_table (_component_name, subsystem_id) VALUES ($1, $2) RETURNING component_id INTO v_component_id;

	INSERT INTO coincomo_component_eafs_table (component_id) VALUES (v_component_id);
	INSERT INTO coincomo_component_sfs_table (component_id) VALUES (v_component_id);
	INSERT INTO coincomo_component_copsemos_table (component_id) VALUES (v_component_id);
	INSERT INTO coincomo_component_parameter_eafs_table (component_id) VALUES (v_component_id);
	INSERT INTO coincomo_component_parameter_sfs_table (component_id) VALUES (v_component_id);
	INSERT INTO coincomo_component_parameter_fps_table (component_id) VALUES (v_component_id);
	INSERT INTO coincomo_component_parameter_eqs_table (component_id) VALUES (v_component_id);
	INSERT INTO coincomo_component_parameter_hours_table (component_id) VALUES (v_component_id);

	IF FOUND THEN
		RETURN v_component_id;
	ELSE
		RETURN -1;
	END IF;
END;
$$ LANGUAGE plpgSQL;

---*******************************************************************
-- INSERT a COINCOMO SubComponent record with given VARCHAR(60) name,
--    INTEGER as the parent Component PK
--     and all other fields with default values
--     into the database.
-- Additionally, insert the corresponding EAF, New Sloc,
--     and FP records with default values
--     into the database.
-- Return the inserted COINCOMO SubComponent record PK
---*******************************************************************
CREATE OR REPLACE FUNCTION Insert_SubComponent(VARCHAR(60), BIGINT) RETURNS BIGINT AS $$
DECLARE
	v_subcomponent_id	BIGINT;
BEGIN
	INSERT INTO coincomo_subcomponents_table (_subcomponent_name, component_id) VALUES ($1, $2) RETURNING subcomponent_id INTO v_subcomponent_id;

	INSERT INTO coincomo_subcomponent_eafs_table (subcomponent_id) VALUES (v_subcomponent_id);
	INSERT INTO coincomo_subcomponent_new_slocs_table (subcomponent_id) VALUES (v_subcomponent_id);
	INSERT INTO coincomo_subcomponent_function_points_table (subcomponent_id) VALUES (v_subcomponent_id);

	IF FOUND THEN
		RETURN v_subcomponent_id;
	ELSE
		RETURN -1;
	END IF;
END;
$$ LANGUAGE plpgsql;

---*******************************************************************
-- INSERT a COINCOMO AdaptationAndReuse record with given VARCHAR(60) name,
--    INTEGER as the parent SubComponent PK
--     and all other fields with default values
--     into the database.
-- Return the inserted COINCOMO AdaptationAndReuse record PK
---*******************************************************************
CREATE OR REPLACE FUNCTION Insert_AdaptationAndReuse(VARCHAR(60), BIGINT) RETURNS BIGINT AS $$
DECLARE
	v_adaptation_and_reuse_id	BIGINT;
BEGIN
	INSERT INTO coincomo_adaptation_and_reuses_table (_adaptation_and_reuse_name, subcomponent_id) VALUES ($1, $2) RETURNING adaptation_and_reuse_id INTO v_adaptation_and_reuse_id;

	IF FOUND THEN
		RETURN v_adaptation_and_reuse_id;
	ELSE
		RETURN -1;
	END IF;
END;
$$ LANGUAGE plpgsql;


---///////////////////////////////////////////////////////////////////
-- COINCOMO Get Functions
---///////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION get_allavailableuserinfo(bigint, bigint)
  RETURNS SETOF character varying AS
$BODY$
DECLARE
	v_user_id	BIGINT;
	v_user_priority	INT;
BEGIN
	SELECT US.user_id FROM coincomo_user_system_table AS US WHERE US.system_id = $2 INTO v_user_id;

	SELECT U.priority FROM coincomo_user_table AS U WHERE U.user_id = $1 INTO v_user_priority;
	
	IF v_user_priority=0 THEN
	
		RETURN QUERY 	SELECT	S.user_login_id
			FROM coincomo_user_table AS S
			WHERE S.user_login_id not in (
				SELECT G.user_login_id
				FROM coincomo_group_table AS G
				WHERE system_id = $2
			) and s.user_id <> $1 and s.priority <> 0 and s.user_id <> v_user_id
			ORDER BY S.user_id ASC;
	END IF;
			
	IF v_user_id <> $1 THEN
		RETURN;
	ELSE 
		RETURN QUERY 	SELECT	S.user_login_id
			FROM coincomo_user_table AS S
			WHERE S.user_login_id not in (
				SELECT G.user_login_id
				FROM coincomo_group_table AS G
				WHERE system_id = $2
			) and s.user_id <> $1 and s.priority <> 0 
			ORDER BY S.user_id ASC;
	END IF;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION get_allavailableuserinfo(bigint, bigint)
  OWNER TO postgres;

CREATE OR REPLACE FUNCTION get_alluserinfo()
  RETURNS SETOF character varying AS
$BODY$
BEGIN
	RETURN QUERY 	SELECT	S.user_login_id
			FROM coincomo_user_table AS S
			ORDER BY S.user_id ASC;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION get_alluserinfo()
  OWNER TO postgres;



CREATE OR REPLACE FUNCTION get_assigneduserinfo(bigint, bigint)
  RETURNS SETOF character varying AS
$BODY$
DECLARE
	v_user_id	BIGINT;
BEGIN
	SELECT US.user_id FROM coincomo_user_system_table AS US WHERE US.system_id = $2 INTO v_user_id;

	IF v_user_id <> $1 THEN
		RETURN;
	ELSE
		RETURN QUERY 	SELECT G.user_login_id
				FROM coincomo_group_table AS G
				WHERE system_id = $2
				ORDER BY G.user_id ASC;

	END IF;
	
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION get_assigneduserinfo(bigint, bigint)
  OWNER TO postgres;

  
CREATE OR REPLACE FUNCTION get_allsystems(IN bigint)
  RETURNS TABLE(system_id bigint, _system_name character varying, sloc bigint, cost numeric, staff numeric, effort numeric, schedule numeric) AS
$BODY$
DECLARE
	v_priority BIGINT;
BEGIN
	SELECT priority FROM coincomo_user_table WHERE user_id = $1 INTO v_priority;

	IF v_priority = 0 THEN
	
	RETURN QUERY 	SELECT	S.system_id,
				S._system_name,
				S.sloc,
				S.cost,
				S.staff,
				S.effort,
				S.schedule
			FROM coincomo_systems_table AS S
			ORDER BY S.system_id ASC;
			
	ELSE
	
	RETURN QUERY 	SELECT	S.system_id,
				S._system_name,
				S.sloc,
				S.cost,
				S.staff,
				S.effort,
				S.schedule
			FROM coincomo_systems_table AS S
			WHERE S.system_id in (

			select US.system_id
			FROM coincomo_user_system_table AS US
			WHERE  US.user_id = $1 

			UNION
			
			SELECT	G.system_id
			FROM coincomo_group_table AS G, coincomo_user_table as U
			WHERE  U.user_id = $1 and U.user_login_id = G.user_login_id
			)
			ORDER BY S.system_id ASC;

	END IF;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION get_allsystems(bigint)
  OWNER TO postgres;

  
  
CREATE OR REPLACE FUNCTION Get_System(BIGINT) RETURNS TABLE (
	system_id	BIGINT,
	_system_name	VARCHAR(60),
	sloc		BIGINT,
	cost		NUMERIC,
	staff		NUMERIC,
	effort		NUMERIC,
	schedule	NUMERIC
	) AS $$
BEGIN
	RETURN QUERY 	SELECT	S.system_id,
				S._system_name,
				S.sloc,
				S.cost,
				S.staff,
				S.effort,
				S.schedule
			FROM coincomo_systems_table AS S
			WHERE S.system_id = $1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Get_AllSubSystems(BIGINT) RETURNS TABLE (
	subsystem_id	BIGINT,
	_subsystem_name	VARCHAR(60),
	system_id	BIGINT,
	sloc		BIGINT,
	cost		NUMERIC,
	staff		NUMERIC,
	effort		NUMERIC,
	schedule	NUMERIC,
	_zoom_level	INTEGER
	) AS $$
BEGIN
	RETURN QUERY	SELECT	S.subsystem_id,
				S._subsystem_name,
				S.system_id,
				S.sloc,
				S.cost,
				S.staff,
				S.effort,
				S.schedule,
				S._zoom_level
			FROM coincomo_subsystems_table AS S
			WHERE S.system_id = $1
			ORDER BY S.subsystem_id ASC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Get_AllComponents(BIGINT) RETURNS TABLE (
	component_id		BIGINT,
	_component_name		VARCHAR(60),
	subsystem_id		BIGINT,
	sloc			BIGINT,
	cost			NUMERIC,
	staff			NUMERIC,
	effort			NUMERIC,
	schedule		NUMERIC,
	sf			NUMERIC,
	sced			NUMERIC,
	scedPercent		NUMERIC,
	_multiBuildShift	INTEGER,
	_revision		INTEGER,
	_eaf_sced_rating	rating_enum,
	_eaf_sced_increment	increment_enum,
	_sf_prec_rating		rating_enum,
	_sf_prec_increment	increment_enum,
	_sf_flex_rating		rating_enum,
	_sf_flex_increment	increment_enum,
	_sf_resl_rating		rating_enum,
	_sf_resl_increment	increment_enum,
	_sf_team_rating		rating_enum,
	_sf_team_increment	increment_enum,
	_sf_pmat_rating		rating_enum,
	_sf_pmat_increment	increment_enum
	) AS $$
BEGIN
	RETURN QUERY	SELECT	C.component_id,
				C._component_name,
				C.subsystem_id,
				C.sloc,
				C.cost,
				C.staff,
				C.effort,
				C.schedule,
				C.sf,
				C.sced,
				C.scedPercent,
				C._multiBuildShift,
				C._revision,
				(EAF._eaf_sced)._rating,
				(EAF._eaf_sced)._increment,
				(SF._sf_prec)._rating,
				(SF._sf_prec)._increment,
				(SF._sf_flex)._rating,
				(SF._sf_flex)._increment,
				(SF._sf_resl)._rating,
				(SF._sf_resl)._increment,
				(SF._sf_team)._rating,
				(SF._sf_team)._increment,
				(SF._sf_pmat)._rating,
				(SF._sf_pmat)._increment
			FROM coincomo_components_table AS C
			INNER JOIN coincomo_component_eafs_table AS EAF
			ON C.component_id = EAF.component_id
			INNER JOIN coincomo_component_sfs_table AS SF
			ON EAF.component_id = SF.component_id
			WHERE C.subsystem_id = $1
			ORDER BY C.component_id ASC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Get_Component_COPSEMO(BIGINT) RETURNS TABLE (
	component_id				BIGINT,
	_inception_effort_percentage		NUMERIC,
	_inception_schedule_percentage		NUMERIC,
	inception_effort			NUMERIC,
	inception_month				NUMERIC,
	inception_personnel			NUMERIC,
	_elaboration_effort_percentage		NUMERIC,
	_elaboration_schedule_percentage	NUMERIC,
	elaboration_effort			NUMERIC,
	elaboration_month			NUMERIC,
	elaboration_personnel			NUMERIC,
	_ocnstruction_effort_percentage		NUMERIC,
	_ocnstruction_schedule_percentage	NUMERIC,
	ocnstruction_effort			NUMERIC,
	ocnstruction_month			NUMERIC,
	ocnstruction_personnel			NUMERIC,
	_transition_effort_percentage		NUMERIC,
	_transition_schedule_percentage		NUMERIC,
	transition_effort			NUMERIC,
	transition_month			NUMERIC,
	transition_personnel			NUMERIC,
	_revision				INT
	) AS $$
BEGIN
	RETURN QUERY	SELECT	C.component_id,
				(C._inception)._effort_percentage,
				(C._inception)._schedule_percentage,
				(C._inception).effort,
				(C._inception).month,
				(C._inception).personnel,
				(C._elaboration)._effort_percentage,
				(C._elaboration)._schedule_percentage,
				(C._elaboration).effort,
				(C._elaboration).month,
				(C._elaboration).personnel,
				(C._construction)._effort_percentage,
				(C._construction)._schedule_percentage,
				(C._construction).effort,
				(C._construction).month,
				(C._construction).personnel,
				(C._transition)._effort_percentage,
				(C._transition)._schedule_percentage,
				(C._transition).effort,
				(C._transition).month,
				(C._transition).personnel,
				C._revision
			FROM coincomo_component_copsemos_table AS C
			WHERE C.component_id = $1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Get_Component_Parameters(BIGINT) RETURNS TABLE (
	component_id				BIGINT,
	_eaf_rely_vlo				NUMERIC,
	_eaf_rely_lo				NUMERIC,
	_eaf_rely_nom				NUMERIC,
	_eaf_rely_hi				NUMERIC,
	_eaf_rely_vhi				NUMERIC,
	_eaf_rely_xhi				NUMERIC,
	_eaf_data_vlo				NUMERIC,
	_eaf_data_lo				NUMERIC,
	_eaf_data_nom				NUMERIC,
	_eaf_data_hi				NUMERIC,
	_eaf_data_vhi				NUMERIC,
	_eaf_data_xhi				NUMERIC,
	_eaf_docu_vlo				NUMERIC,
	_eaf_docu_lo				NUMERIC,
	_eaf_docu_nom				NUMERIC,
	_eaf_docu_hi				NUMERIC,
	_eaf_docu_vhi				NUMERIC,
	_eaf_docu_xhi				NUMERIC,
	_eaf_cplx_vlo				NUMERIC,
	_eaf_cplx_lo				NUMERIC,
	_eaf_cplx_nom				NUMERIC,
	_eaf_cplx_hi				NUMERIC,
	_eaf_cplx_vhi				NUMERIC,
	_eaf_cplx_xhi				NUMERIC,
	_eaf_ruse_vlo				NUMERIC,
	_eaf_ruse_lo				NUMERIC,
	_eaf_ruse_nom				NUMERIC,
	_eaf_ruse_hi				NUMERIC,
	_eaf_ruse_vhi				NUMERIC,
	_eaf_ruse_xhi				NUMERIC,
	_eaf_time_vlo				NUMERIC,
	_eaf_time_lo				NUMERIC,
	_eaf_time_nom				NUMERIC,
	_eaf_time_hi				NUMERIC,
	_eaf_time_vhi				NUMERIC,
	_eaf_time_xhi				NUMERIC,
	_eaf_stor_vlo				NUMERIC,
	_eaf_stor_lo				NUMERIC,
	_eaf_stor_nom				NUMERIC,
	_eaf_stor_hi				NUMERIC,
	_eaf_stor_vhi				NUMERIC,
	_eaf_stor_xhi				NUMERIC,
	_eaf_pvol_vlo				NUMERIC,
	_eaf_pvol_lo				NUMERIC,
	_eaf_pvol_nom				NUMERIC,
	_eaf_pvol_hi				NUMERIC,
	_eaf_pvol_vhi				NUMERIC,
	_eaf_pvol_xhi				NUMERIC,
	_eaf_acap_vlo				NUMERIC,
	_eaf_acap_lo				NUMERIC,
	_eaf_acap_nom				NUMERIC,
	_eaf_acap_hi				NUMERIC,
	_eaf_acap_vhi				NUMERIC,
	_eaf_acap_xhi				NUMERIC,
	_eaf_apex_vlo				NUMERIC,
	_eaf_apex_lo				NUMERIC,
	_eaf_apex_nom				NUMERIC,
	_eaf_apex_hi				NUMERIC,
	_eaf_apex_vhi				NUMERIC,
	_eaf_apex_xhi				NUMERIC,
	_eaf_pcap_vlo				NUMERIC,
	_eaf_pcap_lo				NUMERIC,
	_eaf_pcap_nom				NUMERIC,
	_eaf_pcap_hi				NUMERIC,
	_eaf_pcap_vhi				NUMERIC,
	_eaf_pcap_xhi				NUMERIC,
	_eaf_plex_vlo				NUMERIC,
	_eaf_plex_lo				NUMERIC,
	_eaf_plex_nom				NUMERIC,
	_eaf_plex_hi				NUMERIC,
	_eaf_plex_vhi				NUMERIC,
	_eaf_plex_xhi				NUMERIC,
	_eaf_ltex_vlo				NUMERIC,
	_eaf_ltex_lo				NUMERIC,
	_eaf_ltex_nom				NUMERIC,
	_eaf_ltex_hi				NUMERIC,
	_eaf_ltex_vhi				NUMERIC,
	_eaf_ltex_xhi				NUMERIC,
	_eaf_pcon_vlo				NUMERIC,
	_eaf_pcon_lo				NUMERIC,
	_eaf_pcon_nom				NUMERIC,
	_eaf_pcon_hi				NUMERIC,
	_eaf_pcon_vhi				NUMERIC,
	_eaf_pcon_xhi				NUMERIC,
	_eaf_tool_vlo				NUMERIC,
	_eaf_tool_lo				NUMERIC,
	_eaf_tool_nom				NUMERIC,
	_eaf_tool_hi				NUMERIC,
	_eaf_tool_vhi				NUMERIC,
	_eaf_tool_xhi				NUMERIC,
	_eaf_site_vlo				NUMERIC,
	_eaf_site_lo				NUMERIC,
	_eaf_site_nom				NUMERIC,
	_eaf_site_hi				NUMERIC,
	_eaf_site_vhi				NUMERIC,
	_eaf_site_xhi				NUMERIC,
	_eaf_usr1_vlo				NUMERIC,
	_eaf_usr1_lo				NUMERIC,
	_eaf_usr1_nom				NUMERIC,
	_eaf_usr1_hi				NUMERIC,
	_eaf_usr1_vhi				NUMERIC,
	_eaf_usr1_xhi				NUMERIC,
	_eaf_usr2_vlo				NUMERIC,
	_eaf_usr2_lo				NUMERIC,
	_eaf_usr2_nom				NUMERIC,
	_eaf_usr2_hi				NUMERIC,
	_eaf_usr2_vhi				NUMERIC,
	_eaf_usr2_xhi				NUMERIC,
	_eaf_sced_vlo				NUMERIC,
	_eaf_sced_lo				NUMERIC,
	_eaf_sced_nom				NUMERIC,
	_eaf_sced_hi				NUMERIC,
	_eaf_sced_vhi				NUMERIC,
	_eaf_sced_xhi				NUMERIC,
	_sf_prec_vlo				NUMERIC,
	_sf_prec_lo				NUMERIC,
	_sf_prec_nom				NUMERIC,
	_sf_prec_hi				NUMERIC,
	_sf_prec_vhi				NUMERIC,
	_sf_prec_xhi				NUMERIC,
	_sf_flex_vlo				NUMERIC,
	_sf_flex_lo				NUMERIC,
	_sf_flex_nom				NUMERIC,
	_sf_flex_hi				NUMERIC,
	_sf_flex_vhi				NUMERIC,
	_sf_flex_xhi				NUMERIC,
	_sf_resl_vlo				NUMERIC,
	_sf_resl_lo				NUMERIC,
	_sf_resl_nom				NUMERIC,
	_sf_resl_hi				NUMERIC,
	_sf_resl_vhi				NUMERIC,
	_sf_resl_xhi				NUMERIC,
	_sf_team_vlo				NUMERIC,
	_sf_team_lo				NUMERIC,
	_sf_team_nom				NUMERIC,
	_sf_team_hi				NUMERIC,
	_sf_team_vhi				NUMERIC,
	_sf_team_xhi				NUMERIC,
	_sf_pmat_vlo				NUMERIC,
	_sf_pmat_lo				NUMERIC,
	_sf_pmat_nom				NUMERIC,
	_sf_pmat_hi				NUMERIC,
	_sf_pmat_vhi				NUMERIC,
	_sf_pmat_xhi				NUMERIC,
	_fp_internal_logical_files_low		INTEGER,
	_fp_internal_logical_files_average	INTEGER,
	_fp_internal_logical_files_high		INTEGER,
	_fp_external_interface_files_low	INTEGER,
	_fp_external_interface_files_average	INTEGER,
	_fp_external_interface_files_high	INTEGER,
	_fp_external_inputs_low			INTEGER,
	_fp_external_inputs_average		INTEGER,
	_fp_external_inputs_high		INTEGER,
	_fp_external_outputs_low		INTEGER,
	_fp_external_outputs_average		INTEGER,
	_fp_external_outputs_high		INTEGER,
	_fp_external_inquiries_low		INTEGER,
	_fp_external_inquiries_average		INTEGER,
	_fp_external_inquiries_high		INTEGER,
	_eq_a					NUMERIC,
	_eq_b					NUMERIC,
	_eq_c					NUMERIC,
	_eq_d					NUMERIC,
	_hours_per_pm				NUMERIC,
	_revision				INTEGER
	) AS $$
BEGIN
	RETURN QUERY	SELECT	EAF.component_id,
				(EAF._eaf_rely)._vlo,
				(EAF._eaf_rely)._lo,
				(EAF._eaf_rely)._nom,
				(EAF._eaf_rely)._hi,
				(EAF._eaf_rely)._vhi,
				(EAF._eaf_rely)._xhi,
				(EAF._eaf_data)._vlo,
				(EAF._eaf_data)._lo,
				(EAF._eaf_data)._nom,
				(EAF._eaf_data)._hi,
				(EAF._eaf_data)._vhi,
				(EAF._eaf_data)._xhi,
				(EAF._eaf_docu)._vlo,
				(EAF._eaf_docu)._lo,
				(EAF._eaf_docu)._nom,
				(EAF._eaf_docu)._hi,
				(EAF._eaf_docu)._vhi,
				(EAF._eaf_docu)._xhi,
				(EAF._eaf_cplx)._vlo,
				(EAF._eaf_cplx)._lo,
				(EAF._eaf_cplx)._nom,
				(EAF._eaf_cplx)._hi,
				(EAF._eaf_cplx)._vhi,
				(EAF._eaf_cplx)._xhi,
				(EAF._eaf_ruse)._vlo,
				(EAF._eaf_ruse)._lo,
				(EAF._eaf_ruse)._nom,
				(EAF._eaf_ruse)._hi,
				(EAF._eaf_ruse)._vhi,
				(EAF._eaf_ruse)._xhi,
				(EAF._eaf_time)._vlo,
				(EAF._eaf_time)._lo,
				(EAF._eaf_time)._nom,
				(EAF._eaf_time)._hi,
				(EAF._eaf_time)._vhi,
				(EAF._eaf_time)._xhi,
				(EAF._eaf_stor)._vlo,
				(EAF._eaf_stor)._lo,
				(EAF._eaf_stor)._nom,
				(EAF._eaf_stor)._hi,
				(EAF._eaf_stor)._vhi,
				(EAF._eaf_stor)._xhi,
				(EAF._eaf_pvol)._vlo,
				(EAF._eaf_pvol)._lo,
				(EAF._eaf_pvol)._nom,
				(EAF._eaf_pvol)._hi,
				(EAF._eaf_pvol)._vhi,
				(EAF._eaf_pvol)._xhi,
				(EAF._eaf_acap)._vlo,
				(EAF._eaf_acap)._lo,
				(EAF._eaf_acap)._nom,
				(EAF._eaf_acap)._hi,
				(EAF._eaf_acap)._vhi,
				(EAF._eaf_acap)._xhi,
				(EAF._eaf_apex)._vlo,
				(EAF._eaf_apex)._lo,
				(EAF._eaf_apex)._nom,
				(EAF._eaf_apex)._hi,
				(EAF._eaf_apex)._vhi,
				(EAF._eaf_apex)._xhi,
				(EAF._eaf_pcap)._vlo,
				(EAF._eaf_pcap)._lo,
				(EAF._eaf_pcap)._nom,
				(EAF._eaf_pcap)._hi,
				(EAF._eaf_pcap)._vhi,
				(EAF._eaf_pcap)._xhi,
				(EAF._eaf_plex)._vlo,
				(EAF._eaf_plex)._lo,
				(EAF._eaf_plex)._nom,
				(EAF._eaf_plex)._hi,
				(EAF._eaf_plex)._vhi,
				(EAF._eaf_plex)._xhi,
				(EAF._eaf_ltex)._vlo,
				(EAF._eaf_ltex)._lo,
				(EAF._eaf_ltex)._nom,
				(EAF._eaf_ltex)._hi,
				(EAF._eaf_ltex)._vhi,
				(EAF._eaf_ltex)._xhi,
				(EAF._eaf_pcon)._vlo,
				(EAF._eaf_pcon)._lo,
				(EAF._eaf_pcon)._nom,
				(EAF._eaf_pcon)._hi,
				(EAF._eaf_pcon)._vhi,
				(EAF._eaf_pcon)._xhi,
				(EAF._eaf_tool)._vlo,
				(EAF._eaf_tool)._lo,
				(EAF._eaf_tool)._nom,
				(EAF._eaf_tool)._hi,
				(EAF._eaf_tool)._vhi,
				(EAF._eaf_tool)._xhi,
				(EAF._eaf_site)._vlo,
				(EAF._eaf_site)._lo,
				(EAF._eaf_site)._nom,
				(EAF._eaf_site)._hi,
				(EAF._eaf_site)._vhi,
				(EAF._eaf_site)._xhi,
				(EAF._eaf_usr1)._vlo,
				(EAF._eaf_usr1)._lo,
				(EAF._eaf_usr1)._nom,
				(EAF._eaf_usr1)._hi,
				(EAF._eaf_usr1)._vhi,
				(EAF._eaf_usr1)._xhi,
				(EAF._eaf_usr2)._vlo,
				(EAF._eaf_usr2)._lo,
				(EAF._eaf_usr2)._nom,
				(EAF._eaf_usr2)._hi,
				(EAF._eaf_usr2)._vhi,
				(EAF._eaf_usr2)._xhi,
				(EAF._eaf_sced)._vlo,
				(EAF._eaf_sced)._lo,
				(EAF._eaf_sced)._nom,
				(EAF._eaf_sced)._hi,
				(EAF._eaf_sced)._vhi,
				(EAF._eaf_sced)._xhi,
				(SF._sf_prec)._vlo,
				(SF._sf_prec)._lo,
				(SF._sf_prec)._nom,
				(SF._sf_prec)._hi,
				(SF._sf_prec)._vhi,
				(SF._sf_prec)._xhi,
				(SF._sf_flex)._vlo,
				(SF._sf_flex)._lo,
				(SF._sf_flex)._nom,
				(SF._sf_flex)._hi,
				(SF._sf_flex)._vhi,
				(SF._sf_flex)._xhi,
				(SF._sf_resl)._vlo,
				(SF._sf_resl)._lo,
				(SF._sf_resl)._nom,
				(SF._sf_resl)._hi,
				(SF._sf_resl)._vhi,
				(SF._sf_resl)._xhi,
				(SF._sf_team)._vlo,
				(SF._sf_team)._lo,
				(SF._sf_team)._nom,
				(SF._sf_team)._hi,
				(SF._sf_team)._vhi,
				(SF._sf_team)._xhi,
				(SF._sf_pmat)._vlo,
				(SF._sf_pmat)._lo,
				(SF._sf_pmat)._nom,
				(SF._sf_pmat)._hi,
				(SF._sf_pmat)._vhi,
				(SF._sf_pmat)._xhi,
				(FP._fp_internal_logical_files)._low,
				(FP._fp_internal_logical_files)._average,
				(FP._fp_internal_logical_files)._high,
				(FP._fp_external_interface_files)._low,
				(FP._fp_external_interface_files)._average,
				(FP._fp_external_interface_files)._high,
				(FP._fp_external_inputs)._low,
				(FP._fp_external_inputs)._average,
				(FP._fp_external_inputs)._high,
				(FP._fp_external_outputs)._low,
				(FP._fp_external_outputs)._average,
				(FP._fp_external_outputs)._high,
				(FP._fp_external_inquiries)._low,
				(FP._fp_external_inquiries)._average,
				(FP._fp_external_inquiries)._high,
				EQ._eq_a,
				EQ._eq_b,
				EQ._eq_c,
				EQ._eq_d,
				H._hours_per_pm,
				EAF._revision
			FROM coincomo_component_parameter_eafs_table AS EAF
			INNER JOIN coincomo_component_parameter_sfs_table AS SF
			ON EAF.component_id = SF.component_id
			INNER JOIN coincomo_component_parameter_fps_table AS FP
			ON SF.component_id = FP.component_id
			INNER JOIN coincomo_component_parameter_eqs_table AS EQ
			ON FP.component_id = EQ.component_id
			INNER JOIN coincomo_component_parameter_hours_table AS H
			ON EQ.component_id = H.component_id
			WHERE EAF.component_id = $1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Get_AllSubComponents(BIGINT) RETURNS TABLE (
	subcomponent_id				BIGINT,
	_subcomponent_name			VARCHAR(60),
	component_id				BIGINT,
	sloc					BIGINT,
	cost					NUMERIC,
	staff					NUMERIC,
	effort					NUMERIC,
	schedule				NUMERIC,
	productivity				NUMERIC,
	instruction_cost			NUMERIC,
	risk					NUMERIC,
	nominal_effort				NUMERIC,
	estimated_effort			NUMERIC,
	eaf					NUMERIC,
	sum_of_new_sloc_fp_sloc_aar_slocs	BIGINT,
	_labor_rate				NUMERIC,
	_revl					NUMERIC,
	_language				VARCHAR(60),
	_eaf_rely_rating			rating_enum,
	_eaf_rely_increment			increment_enum,
	_eaf_data_rating			rating_enum,
	_eaf_data_increment			increment_enum,
	_eaf_docu_rating			rating_enum,
	_eaf_docu_increment			increment_enum,
	_eaf_cplx_rating			rating_enum,
	_eaf_cplx_increment			increment_enum,
	_eaf_ruse_rating			rating_enum,
	_eaf_ruse_increment			increment_enum,
	_eaf_time_rating			rating_enum,
	_eaf_time_increment			increment_enum,
	_eaf_stor_rating			rating_enum,
	_eaf_stor_increment			increment_enum,
	_eaf_pvol_rating			rating_enum,
	_eaf_pvol_increment			increment_enum,
	_eaf_acap_rating			rating_enum,
	_eaf_acap_increment			increment_enum,
	_eaf_apex_rating			rating_enum,
	_eaf_apex_increment			increment_enum,
	_eaf_pcap_rating			rating_enum,
	_eaf_pcap_increment			increment_enum,
	_eaf_plex_rating			rating_enum,
	_eaf_plex_increment			increment_enum,
	_eaf_ltex_rating			rating_enum,
	_eaf_ltex_increment			increment_enum,
	_eaf_pcon_rating			rating_enum,
	_eaf_pcon_increment			increment_enum,
	_eaf_tool_rating			rating_enum,
	_eaf_tool_increment			increment_enum,
	_eaf_site_rating			rating_enum,
	_eaf_site_increment			increment_enum,
	_eaf_usr1_rating			rating_enum,
	_eaf_usr1_increment			increment_enum,
	_eaf_usr2_rating			rating_enum,
	_eaf_usr2_increment			increment_enum,
	_new_sloc				BIGINT,
	_multiplier				INTEGER,
	_ratio_type				ratio_type_enum,
	_calculation_method			calculation_method_enum,
	_internal_logical_files_low		INTEGER,
	_internal_logical_files_average		INTEGER,
	_internal_logical_files_high		INTEGER,
	_internal_logical_files_subtotal	INTEGER,
	_external_interface_files_low		INTEGER,
	_external_interface_files_average	INTEGER,
	_external_interface_files_high		INTEGER,
	_external_interface_files_subtotal	INTEGER,
	_external_inputs_low			INTEGER,
	_external_inputs_average		INTEGER,
	_external_inputs_high			INTEGER,
	_external_inputs_subtotal		INTEGER,
	_external_outputs_low			INTEGER,
	_external_outputs_average		INTEGER,
	_external_outputs_high			INTEGER,
	_external_outputs_subtotal		INTEGER,
	_external_inquiries_low			INTEGER,
	_external_inquiries_average		INTEGER,
	_external_inquiries_high		INTEGER,
	_external_inquiries_subtotal		INTEGER,
	_total_unadjusted_function_points	INTEGER,
	equivalent_sloc				BIGINT
	) AS $$
BEGIN
	RETURN QUERY	SELECT	S.subcomponent_id,
				S._subcomponent_name,
				S.component_id,
				S.sloc,
				S.cost,
				S.staff,
				S.effort,
				S.schedule,
				S.productivity,
				S.instruction_cost,
				S.risk,
				S.nominal_effort,
				S.estimated_effort,
				S.eaf,
				S.sum_of_new_sloc_fp_sloc_aar_slocs,
				S._labor_rate,
				S._revl,
				S._language,
				(EAF._eaf_rely)._rating,
				(EAF._eaf_rely)._increment,
				(EAF._eaf_data)._rating,
				(EAF._eaf_data)._increment,
				(EAF._eaf_docu)._rating,
				(EAF._eaf_docu)._increment,
				(EAF._eaf_cplx)._rating,
				(EAF._eaf_cplx)._increment,
				(EAF._eaf_ruse)._rating,
				(EAF._eaf_ruse)._increment,
				(EAF._eaf_time)._rating,
				(EAF._eaf_time)._increment,
				(EAF._eaf_stor)._rating,
				(EAF._eaf_stor)._increment,
				(EAF._eaf_pvol)._rating,
				(EAF._eaf_pvol)._increment,
				(EAF._eaf_acap)._rating,
				(EAF._eaf_acap)._increment,
				(EAF._eaf_apex)._rating,
				(EAF._eaf_apex)._increment,
				(EAF._eaf_pcap)._rating,
				(EAF._eaf_pcap)._increment,
				(EAF._eaf_plex)._rating,
				(EAF._eaf_plex)._increment,
				(EAF._eaf_ltex)._rating,
				(EAF._eaf_ltex)._increment,
				(EAF._eaf_pcon)._rating,
				(EAF._eaf_pcon)._increment,
				(EAF._eaf_tool)._rating,
				(EAF._eaf_tool)._increment,
				(EAF._eaf_site)._rating,
				(EAF._eaf_site)._increment,
				(EAF._eaf_usr1)._rating,
				(EAF._eaf_usr1)._increment,
				(EAF._eaf_usr2)._rating,
				(EAF._eaf_usr2)._increment,
				N._new_sloc,
				FP._multiplier,
				FP._ratio_type,
				FP._calculation_method,
				(FP._internal_logical_files)._low,
				(FP._internal_logical_files)._average,
				(FP._internal_logical_files)._high,
				(FP._internal_logical_files).subtotal,
				(FP._external_interface_files)._low,
				(FP._external_interface_files)._average,
				(FP._external_interface_files)._high,
				(FP._external_interface_files).subtotal,
				(FP._external_inputs)._low,
				(FP._external_inputs)._average,
				(FP._external_inputs)._high,
				(FP._external_inputs).subtotal,
				(FP._external_outputs)._low,
				(FP._external_outputs)._average,
				(FP._external_outputs)._high,
				(FP._external_outputs).subtotal,
				(FP._external_inquiries)._low,
				(FP._external_inquiries)._average,
				(FP._external_inquiries)._high,
				(FP._external_inquiries).subtotal,
				FP._total_unadjusted_function_points,
				FP.equivalent_sloc
			FROM coincomo_subcomponents_table AS S
			INNER JOIN coincomo_subcomponent_eafs_table AS EAF
			ON S.subcomponent_id = EAF.subcomponent_id
			INNER JOIN coincomo_subcomponent_new_slocs_table AS N
			ON S.subcomponent_id = N.subcomponent_id
			INNER JOIN coincomo_subcomponent_function_points_table AS FP
			ON S.subcomponent_id = FP.subcomponent_id
			WHERE S.component_id = $1
			ORDER BY S.subcomponent_id ASC;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Get_AllAdaptationAndReuses(BIGINT) RETURNS TABLE (
	adaptation_and_reuse_id			BIGINT,
	_adaptation_and_reuse_name		VARCHAR(60),
	subcomponent_id				BIGINT,
	_initial_sloc				BIGINT,
	_design_modified			NUMERIC,
	_code_modified				NUMERIC,
	_integration_modified			NUMERIC,
	_software_understanding			NUMERIC,
	_assessment_and_assimilation		NUMERIC,
	_unfamiliarity_with_software		NUMERIC,
	_automatic_translation			NUMERIC,
	_automatic_translation_productivity	NUMERIC,
	adaptation_adjustment_factor		NUMERIC,
	adapted_sloc				BIGINT
	) AS $$
BEGIN
	RETURN QUERY	SELECT	A.adaptation_and_reuse_id,
				A._adaptation_and_reuse_name,
				A.subcomponent_id,
				A._initial_sloc,
				A._design_modified,
				A._code_modified,
				A._integration_modified,
				A._software_understanding,
				A._assessment_and_assimilation,
				A._unfamiliarity_with_software,
				A._automatic_translation,
				A._automatic_translation_productivity,
				A.adaptation_adjustment_factor,
				A.adapted_sloc
			FROM coincomo_adaptation_and_reuses_table AS A
			WHERE A.subcomponent_id = $1
			ORDER BY A.adaptation_and_reuse_id ASC;
END;
$$ LANGUAGE plpgsql;


---///////////////////////////////////////////////////////////////////
-- COINCOMO Update Functions
---///////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION update_group(integer, bigint, bigint, character varying)
  RETURNS bigint AS
$BODY$
DECLARE
	v_user_id	BIGINT;
BEGIN
	IF $1=1 THEN
		INSERT INTO coincomo_group_table (user_id,system_id,user_login_id) VALUES ($2,$3,$4) RETURNING user_id INTO v_user_id;
		RETURN v_user_id;
	ELSE
		DELETE FROM coincomo_group_table WHERE system_id = $3 and user_login_id = $4;
		RETURN 0;
	END IF;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION update_group(integer, bigint, bigint, character varying)
  OWNER TO postgres;



CREATE OR REPLACE FUNCTION Update_System(BIGINT, VARCHAR(60), BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC) RETURNS BOOLEAN AS $$
BEGIN
	UPDATE coincomo_systems_table
	SET	_system_name = $2,
		sloc = $3,
		cost = $4,
		staff = $5,
		effort = $6,
		schedule = $7
	WHERE system_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_SubSystem(BIGINT, VARCHAR(60), BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, INTEGER) RETURNS BOOLEAN AS $$
BEGIN
	UPDATE coincomo_subsystems_table
	SET	_subsystem_name = $2,
		system_id = $3,
		sloc = $4,
		cost = $5,
		staff = $6,
		effort = $7,
		schedule = $8,
		_zoom_level = $9
	WHERE subsystem_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_Component(
		BIGINT, VARCHAR(60), BIGINT, BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, INTEGER, INTEGER,
		rating_enum, increment_enum,
		rating_enum, increment_enum, rating_enum, increment_enum, rating_enum, increment_enum, rating_enum, increment_enum, rating_enum, increment_enum
	) RETURNS BOOLEAN AS $$
DECLARE
	v_success	BOOLEAN;
BEGIN
	v_success := false;

	UPDATE coincomo_components_table
	SET	_component_name = $2,
		subsystem_id = $3,
		sloc = $4,
		cost = $5,
		staff = $6,
		effort = $7,
		schedule = $8,
		sf = $9,
		sced = $10,
		scedPercent = $11,
		_multiBuildShift = $12,
		_revision = $13
	WHERE component_id = $1;

	IF FOUND THEN
		v_success := true;
	ELSE
		v_success := false;
	END IF;

	UPDATE coincomo_component_eafs_table
	SET	_eaf_sced._rating = $14,
		_eaf_sced._increment = $15
	WHERE component_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	UPDATE coincomo_component_sfs_table
	SET 	_sf_prec._rating = $16,
		_sf_prec._increment = $17,
		_sf_flex._rating = $18,
		_sf_flex._increment = $19,
		_sf_resl._rating = $20,
		_sf_resl._increment = $21,
		_sf_team._rating = $22,
		_sf_team._increment = $23,
		_sf_pmat._rating = $24,
		_sf_pmat._increment = $25
	WHERE component_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	RETURN v_success;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_Component_COPSEMO(
		BIGINT,
		NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC,
		NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC,
		NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC,
		NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC,
		INTEGER
	) RETURNS BOOLEAN AS $$
DECLARE
	v_success	BOOLEAN;
BEGIN
	UPDATE coincomo_component_copsemos_table
	SET	_inception._effort_percentage = $2,
		_inception._schedule_percentage = $3,
		_inception.effort = $4,
		_inception.month = $5,
		_inception.personnel = $6,
		_elaboration._effort_percentage = $7,
		_elaboration._schedule_percentage = $8,
		_elaboration.effort = $9,
		_elaboration.month = $10,
		_elaboration.personnel = $11,
		_construction._effort_percentage = $12,
		_construction._schedule_percentage = $13,
		_construction.effort = $14,
		_construction.month = $15,
		_construction.personnel = $16,
		_transition._effort_percentage = $17,
		_transition._schedule_percentage = $18,
		_transition.effort = $19,
		_transition.month = $20,
		_transition.personnel = $21,
		_revision = $22
	WHERE component_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_Component_Parameters(
		BIGINT,
		coincomo_cost_driver_weight_type,	-- EAF RELY
		coincomo_cost_driver_weight_type,	-- EAF DATA
		coincomo_cost_driver_weight_type,	-- EAF DOCU
		coincomo_cost_driver_weight_type,	-- EAF CPLX
		coincomo_cost_driver_weight_type,	-- EAF RUSE
		coincomo_cost_driver_weight_type,	-- EAF TIME
		coincomo_cost_driver_weight_type,	-- EAF STOR
		coincomo_cost_driver_weight_type,	-- EAF PVOL
		coincomo_cost_driver_weight_type,	-- EAF ACAP
		coincomo_cost_driver_weight_type,	-- EAF APEX
		coincomo_cost_driver_weight_type,	-- EAF PCAP
		coincomo_cost_driver_weight_type,	-- EAF PLEX
		coincomo_cost_driver_weight_type,	-- EAF LTEX
		coincomo_cost_driver_weight_type,	-- EAF PCON
		coincomo_cost_driver_weight_type,	-- EAF TOOL
		coincomo_cost_driver_weight_type,	-- EAF SITE
		coincomo_cost_driver_weight_type,	-- EAF USR1
		coincomo_cost_driver_weight_type,	-- EAF USR2
		coincomo_cost_driver_weight_type,	-- EAF SCED
		coincomo_cost_driver_weight_type,	-- SF PREC
		coincomo_cost_driver_weight_type,	-- SF FLEX
		coincomo_cost_driver_weight_type,	-- SF RESL
		coincomo_cost_driver_weight_type,	-- SF TEAM
		coincomo_cost_driver_weight_type,	-- SF PMAT
		INTEGER, INTEGER, INTEGER,		-- FP ILF
		INTEGER, INTEGER, INTEGER,		-- FP EIF
		INTEGER, INTEGER, INTEGER,		-- FP EI
		INTEGER, INTEGER, INTEGER,		-- FP EO
		INTEGER, INTEGER, INTEGER,		-- FP EQ
		NUMERIC,				-- EQ A
		NUMERIC,				-- EQ B
		NUMERIC,				-- EQ C
		NUMERIC,				-- EQ D
		NUMERIC,				-- Hours Per PM
		INTEGER					-- Revision
	) RETURNS BOOLEAN AS $$
DECLARE
	v_success	BOOLEAN;
BEGIN
	v_success := true;

	UPDATE coincomo_component_parameter_eafs_table
	SET	_eaf_rely = $2,
		_eaf_data = $3,
		_eaf_docu = $4,
		_eaf_cplx = $5,
		_eaf_ruse = $6,
		_eaf_time = $7,
		_eaf_stor = $8,
		_eaf_pvol = $9,
		_eaf_acap = $10,
		_eaf_apex = $11,
		_eaf_pcap = $12,
		_eaf_plex = $13,
		_eaf_ltex = $14,
		_eaf_pcon = $15,
		_eaf_tool = $16,
		_eaf_site = $17,
		_eaf_usr1 = $18,
		_eaf_usr2 = $19,
		_eaf_sced = $20,
		_revision = $46
	WHERE component_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	UPDATE coincomo_component_parameter_sfs_table
	SET	_sf_prec = $21,
		_sf_flex = $22,
		_sf_resl = $23,
		_sf_team = $24,
		_sf_pmat = $25,
		_revision = $46
	WHERE component_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	UPDATE coincomo_component_parameter_fps_table
	SET	_fp_internal_logical_files._low = $26,
		_fp_internal_logical_files._average = $27,
		_fp_internal_logical_files._high = $28,
		_fp_external_interface_files._low = $29,
		_fp_external_interface_files._average = $30,
		_fp_external_interface_files._high = $31,
		_fp_external_inputs._low = $32,
		_fp_external_inputs._average = $33,
		_fp_external_inputs._high = $34,
		_fp_external_outputs._low = $35,
		_fp_external_outputs._average = $36,
		_fp_external_outputs._high = $37,
		_fp_external_inquiries._low = $38,
		_fp_external_inquiries._average = $39,
		_fp_external_inquiries._high = $40,
		_revision = $46
	WHERE component_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	UPDATE coincomo_component_parameter_eqs_table
	SET	_eq_a = $41,
		_eq_b = $42,
		_eq_c = $43,
		_eq_d = $44,
		_revision = $46
	WHERE component_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	UPDATE coincomo_component_parameter_hours_table
	SET	_hours_per_pm = $45,
		_revision = $46
	WHERE component_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	RETURN v_success;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_SubComponent(
		BIGINT, VARCHAR(60), BIGINT, BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, BIGINT,  NUMERIC, NUMERIC, VARCHAR(60),
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		rating_enum, increment_enum,
		BIGINT,
		INTEGER, ratio_type_enum, calculation_method_enum,
		INTEGER, INTEGER, INTEGER, INTEGER,
		INTEGER, INTEGER, INTEGER, INTEGER,
		INTEGER, INTEGER, INTEGER, INTEGER,
		INTEGER, INTEGER, INTEGER, INTEGER,
		INTEGER, INTEGER, INTEGER, INTEGER,
		INTEGER,
		BIGINT
	) RETURNS BOOLEAN AS $$
DECLARE
	v_success	BOOLEAN;
BEGIN
	v_success := true;

	UPDATE coincomo_subcomponents_table
	SET	_subcomponent_name = $2,
		component_id = $3,
		sloc = $4,
		cost = $5,
		staff = $6,
		effort = $7,
		schedule = $8,
		productivity = $9,
		instruction_cost = $10,
		risk = $11,
		nominal_effort = $12,
		estimated_effort = $13,
		eaf = $14,
		sum_of_new_sloc_fp_sloc_aar_slocs = $15,
		_labor_rate = $16,
		_revl = $17,
		_language = $18
	WHERE subcomponent_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	UPDATE coincomo_subcomponent_eafs_table
	SET	_eaf_rely._rating = $19,
		_eaf_rely._increment = $20,
		_eaf_data._rating = $21,
		_eaf_data._increment = $22,
		_eaf_docu._rating = $23,
		_eaf_docu._increment = $24,
		_eaf_cplx._rating = $25,
		_eaf_cplx._increment = $26,
		_eaf_ruse._rating = $27,
		_eaf_ruse._increment = $28,
		_eaf_time._rating = $29,
		_eaf_time._increment = $30,
		_eaf_stor._rating = $31,
		_eaf_stor._increment = $32,
		_eaf_pvol._rating = $33,
		_eaf_pvol._increment = $34,
		_eaf_acap._rating = $35,
		_eaf_acap._increment = $36,
		_eaf_apex._rating = $37,
		_eaf_apex._increment = $38,
		_eaf_pcap._rating = $39,
		_eaf_pcap._increment = $40,
		_eaf_plex._rating = $41,
		_eaf_plex._increment = $42,
		_eaf_ltex._rating = $43,
		_eaf_ltex._increment = $44,
		_eaf_pcon._rating = $45,
		_eaf_pcon._increment = $46,
		_eaf_tool._rating = $47,
		_eaf_tool._increment = $48,
		_eaf_site._rating = $49,
		_eaf_site._increment = $50,
		_eaf_usr1._rating = $51,
		_eaf_usr1._increment = $52,
		_eaf_usr2._rating = $53,
		_eaf_usr2._increment = $54
	WHERE subcomponent_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	UPDATE coincomo_subcomponent_new_slocs_table
	SET	_new_sloc = $55
	WHERE subcomponent_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	UPDATE coincomo_subcomponent_function_points_table
	SET	_multiplier = $56,
		_ratio_type = $57,
		_calculation_method = $58,
		_internal_logical_files._low = $59,
		_internal_logical_files._average = $60,
		_internal_logical_files._high = $61,
		_internal_logical_files.subtotal = $62,
		_external_interface_files._low = $63,
		_external_interface_files._average = $64,
		_external_interface_files._high = $65,
		_external_interface_files.subtotal = $66,
		_external_inputs._low = $67,
		_external_inputs._average = $68,
		_external_inputs._high = $69,
		_external_inputs.subtotal = $70,
		_external_outputs._low = $71,
		_external_outputs._average = $72,
		_external_outputs._high = $73,
		_external_outputs.subtotal = $74,
		_external_inquiries._low  = $75,
		_external_inquiries._average = $76,
		_external_inquiries._high = $77,
		_external_inquiries.subtotal = $78,
		_total_unadjusted_function_points = $79,
		equivalent_sloc = $80
	WHERE subcomponent_id = $1;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	RETURN v_success;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_AdaptationAndReuse(BIGINT, VARCHAR(60), BIGINT, BIGINT, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, NUMERIC, BIGINT) RETURNS BOOLEAN AS $$
BEGIN
	UPDATE coincomo_adaptation_and_reuses_table
	SET	_adaptation_and_reuse_name = $2,
		subcomponent_id = $3,
		_initial_sloc = $4,
		_design_modified = $5,
		_code_modified = $6,
		_integration_modified = $7,
		_software_understanding = $8,
		_assessment_and_assimilation = $9,
		_unfamiliarity_with_software = $10,
		_automatic_translation = $11,
		_automatic_translation_productivity = $12,
		adaptation_adjustment_factor = $13,
		adapted_sloc = $14
	WHERE adaptation_and_reuse_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;


---///////////////////////////////////////////////////////////////////
-- COINCOMO Delete Functions
---///////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION Delete_System(BIGINT) RETURNS BOOLEAN AS $$
BEGIN
	DELETE
	FROM coincomo_systems_table
	WHERE system_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Delete_SubSystem(BIGINT) RETURNS BOOLEAN AS $$
BEGIN
	DELETE
	FROM coincomo_subsystems_table
	WHERE subsystem_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Delete_Component(BIGINT) RETURNS BOOLEAN AS $$
BEGIN
	DELETE
	FROM coincomo_components_table
	WHERE component_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Delete_SubComponent(BIGINT) RETURNS BOOLEAN AS $$
BEGIN
	DELETE
	FROM coincomo_subcomponents_table
	WHERE subcomponent_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Delete_AdaptationAndReuse(BIGINT) RETURNS BOOLEAN AS $$
BEGIN
	DELETE
	FROM coincomo_adaptation_and_reuses_table
	WHERE adaptation_and_reuse_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;


---///////////////////////////////////////////////////////////////////
-- COINCOMO Copy Functions
---///////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION Copy_AdaptationAndReuse(BIGINT, BIGINT) RETURNS BIGINT AS $$
DECLARE
	v_from_adaptation_and_reuse_id	ALIAS FOR $1;
	v_to_subcomponent_id		ALIAS FOR $2;
	v_adaptation_and_reuse_id	BIGINT;
BEGIN
	INSERT INTO coincomo_adaptation_and_reuses_table (
		_adaptation_and_reuse_name,
		subcomponent_id,
		_initial_sloc,
		_design_modified,
		_code_modified,
		_integration_modified,
		_software_understanding,
		_assessment_and_assimilation,
		_unfamiliarity_with_software,
		_automatic_translation,
		_automatic_translation_productivity,
		adaptation_adjustment_factor,
		adapted_sloc
		)
	SELECT	'Copy of ' || C._adaptation_and_reuse_name,
		v_to_subcomponent_id,
		C._initial_sloc,
		C._design_modified,
		C._code_modified,
		C._integration_modified,
		C._software_understanding,
		C._assessment_and_assimilation,
		C._unfamiliarity_with_software,
		C._automatic_translation,
		C._automatic_translation_productivity,
		C.adaptation_adjustment_factor,
		C.adapted_sloc
	FROM coincomo_adaptation_and_reuses_table AS C
	WHERE C.adaptation_and_reuse_id = v_from_adaptation_and_reuse_id
	RETURNING adaptation_and_reuse_id INTO v_adaptation_and_reuse_id;

	IF FOUND THEN
		RETURN v_adaptation_and_reuse_id;
	ELSE
		RETURN -1;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Copy_SubComponent(BIGINT, BIGINT) RETURNS BIGINT AS $$
DECLARE
	v_from_subcomponent_id	ALIAS FOR $1;
	v_to_component_id	ALIAS FOR $2;
	v_success		BOOLEAN;
	v_subcomponent_id	BIGINT;
BEGIN
	INSERT INTO coincomo_subcomponents_table (
		_subcomponent_name,
		component_id,
		sloc,
		cost,
		staff,
		effort,
		schedule,
		productivity,
		instruction_cost,
		risk,
		nominal_effort,
		estimated_effort,
		eaf,
		sum_of_new_sloc_fp_sloc_aar_slocs,
		_labor_rate,
		_revl,
		_language
		)
	SELECT	'Copy of ' || S._subcomponent_name,
		v_to_component_id,
		S.sloc,
		S.cost,
		S.staff,
		S.effort,
		S.schedule,
		S.productivity,
		S.instruction_cost,
		S.risk,
		S.nominal_effort,
		S.estimated_effort,
		S.eaf,
		S.sum_of_new_sloc_fp_sloc_aar_slocs,
		S._labor_rate,
		S._revl,
		S._language
	FROM coincomo_subcomponents_table AS S
	WHERE S.subcomponent_id = v_from_subcomponent_id
	RETURNING subcomponent_id INTO v_subcomponent_id;

	IF FOUND THEN
		v_success := true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_subcomponent_eafs_table (
		subcomponent_id,
		_eaf_rely,
		_eaf_data,
		_eaf_docu,
		_eaf_cplx,
		_eaf_ruse,
		_eaf_time,
		_eaf_stor,
		_eaf_pvol,
		_eaf_acap,
		_eaf_apex,
		_eaf_pcap,
		_eaf_plex,
		_eaf_ltex,
		_eaf_pcon,
		_eaf_tool,
		_eaf_site,
		_eaf_usr1,
		_eaf_usr2
		)
	SELECT	v_subcomponent_id,
		EAF._eaf_rely,
		EAF._eaf_data,
		EAF._eaf_docu,
		EAF._eaf_cplx,
		EAF._eaf_ruse,
		EAF._eaf_time,
		EAF._eaf_stor,
		EAF._eaf_pvol,
		EAF._eaf_acap,
		EAF._eaf_apex,
		EAF._eaf_pcap,
		EAF._eaf_plex,
		EAF._eaf_ltex,
		EAF._eaf_pcon,
		EAF._eaf_tool,
		EAF._eaf_site,
		EAF._eaf_usr1,
		EAF._eaf_usr2
	FROM coincomo_subcomponent_eafs_table AS EAF
	WHERE EAF.subcomponent_id = v_from_subcomponent_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_subcomponent_new_slocs_table (
		subcomponent_id,
		_new_sloc
		)
	SELECT	v_subcomponent_id,
		N._new_sloc
	FROM coincomo_subcomponent_new_slocs_table AS N
	WHERE N.subcomponent_id = v_from_subcomponent_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_subcomponent_function_points_table (
		subcomponent_id,
		_multiplier,
		_ratio_type,
		_calculation_method,
		_internal_logical_files,
		_external_interface_files,
		_external_inputs,
		_external_outputs,
		_external_inquiries,
		_total_unadjusted_function_points,
		equivalent_sloc
		)
	SELECT	v_subcomponent_id,
		FP._multiplier,
		FP._ratio_type,
		FP._calculation_method,
		FP._internal_logical_files,
		FP._external_interface_files,
		FP._external_inputs,
		FP._external_outputs,
		FP._external_inquiries,
		FP._total_unadjusted_function_points,
		FP.equivalent_sloc
	FROM coincomo_subcomponent_function_points_table AS FP
	WHERE FP.subcomponent_id = v_from_subcomponent_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	PERFORM Copy_AdaptationAndReuse(AAR.adaptation_and_reuse_id, v_subcomponent_id)
	FROM coincomo_adaptation_and_reuses_table AS AAR
	WHERE AAR.subcomponent_id = v_from_subcomponent_id;

	IF v_success THEN
		RETURN v_subcomponent_id;
	ELSE
		RETURN -1;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Copy_Component(BIGINT, BIGINT) RETURNS BIGINT AS $$
DECLARE
	v_from_component_id	ALIAS FOR $1;
	v_to_subsystem_id	ALIAS FOR $2;
	v_success		BOOLEAN;
	v_component_id		BIGINT;
BEGIN
	INSERT INTO coincomo_components_table (
		_component_name,
		subsystem_id,
		sloc,
		cost,
		staff,
		effort,
		schedule,
		sf,
		sced,
		scedPercent,
		_revision
		)
	SELECT	'Copy of ' || C._component_name,
		v_to_subsystem_id,
		C.sloc,
		C.cost,
		C.staff,
		C.effort,
		C.schedule,
		C.sf,
		C.sced,
		C.scedPercent,
		C._revision
	FROM coincomo_components_table AS C
	WHERE C.component_id = v_from_component_id
	RETURNING component_id INTO v_component_id;

	IF FOUND THEN
		v_success := true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_component_eafs_table (
		component_id,
		_eaf_sced
		)
	SELECT	v_component_id,
		EAF._eaf_sced
	FROM coincomo_component_eafs_table AS EAF
	WHERE EAF.component_id = v_from_component_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_component_sfs_table (
		component_id,
		_sf_prec,
		_sf_flex,
		_sf_resl,
		_sf_team,
		_sf_pmat
		)
	SELECT	v_component_id,
		SF._sf_prec,
		SF._sf_flex,
		SF._sf_resl,
		SF._sf_team,
		SF._sf_pmat
	FROM coincomo_component_sfs_table AS SF
	WHERE SF.component_id = v_from_component_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_component_copsemos_table (
		component_id,
		_inception,
		_elaboration,
		_construction,
		_transition,
		_revision
		)
	SELECT	v_component_id,
		COPSEMO._inception,
		COPSEMO._elaboration,
		COPSEMO._construction,
		COPSEMO._transition,
		COPSEMO._revision
	FROM coincomo_component_copsemos_table AS COPSEMO
	WHERE COPSEMO.component_id = v_from_component_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_component_parameter_eafs_table (
		component_id,
		_eaf_rely,
		_eaf_data,
		_eaf_docu,
		_eaf_cplx,
		_eaf_ruse,
		_eaf_time,
		_eaf_stor,
		_eaf_pvol,
		_eaf_acap,
		_eaf_apex,
		_eaf_pcap,
		_eaf_plex,
		_eaf_ltex,
		_eaf_pcon,
		_eaf_tool,
		_eaf_site,
		_eaf_usr1,
		_eaf_usr2,
		_eaf_sced
		)
	SELECT	v_component_id,
		EAF._eaf_rely,
		EAF._eaf_data,
		EAF._eaf_docu,
		EAF._eaf_cplx,
		EAF._eaf_ruse,
		EAF._eaf_time,
		EAF._eaf_stor,
		EAF._eaf_pvol,
		EAF._eaf_acap,
		EAF._eaf_apex,
		EAF._eaf_pcap,
		EAF._eaf_plex,
		EAF._eaf_ltex,
		EAF._eaf_pcon,
		EAF._eaf_tool,
		EAF._eaf_site,
		EAF._eaf_usr1,
		EAF._eaf_usr2,
		EAF._eaf_sced
	FROM coincomo_component_parameter_eafs_table AS EAF
	WHERE EAF.component_id = v_from_component_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_component_parameter_sfs_table (
		component_id,
		_sf_prec,
		_sf_flex,
		_sf_resl,
		_sf_team,
		_sf_pmat,
		_revision
		)
	SELECT	v_component_id,
		SF._sf_prec,
		SF._sf_flex,
		SF._sf_resl,
		SF._sf_team,
		SF._sf_pmat,
		SF._revision
	FROM coincomo_component_parameter_sfs_table AS SF
	WHERE SF.component_id = v_from_component_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_component_parameter_fps_table (
		component_id,
		_fp_internal_logical_files,
		_fp_external_interface_files,
		_fp_external_inputs,
		_fp_external_outputs,
		_fp_external_inquiries,
		_revision
		)
	SELECT	v_component_id,
		FP._fp_internal_logical_files,
		FP._fp_external_interface_files,
		FP._fp_external_inputs,
		FP._fp_external_outputs,
		FP._fp_external_inquiries,
		FP._revision
	FROM coincomo_component_parameter_fps_table AS FP
	WHERE FP.component_id = v_from_component_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_component_parameter_eqs_table (
		component_id,
		_eq_a,
		_eq_b,
		_eq_c,
		_eq_d,
		_revision
		)
	SELECT	v_component_id,
		EQ._eq_a,
		EQ._eq_b,
		EQ._eq_c,
		EQ._eq_d,
		EQ._revision
	FROM coincomo_component_parameter_eqs_table AS EQ
	WHERE EQ.component_id = v_from_component_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	INSERT INTO coincomo_component_parameter_hours_table (
		component_id,
		_hours_per_pm,
		_revision
		)
	SELECT	v_component_id,
		H._hours_per_pm,
		H._revision
	FROM coincomo_component_parameter_hours_table AS H
	WHERE H.component_id = v_from_component_id;

	IF FOUND THEN
		v_success := v_success AND true;
	ELSE
		v_success := false;
	END IF;

	PERFORM Copy_SubComponent(S.subcomponent_id, v_component_id)
	FROM coincomo_subcomponents_table AS S
	WHERE S.component_id = v_from_component_id;

	IF v_success THEN
		RETURN v_component_id;
	ELSE
		RETURN -1;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Copy_SubSystem(BIGINT, BIGINT) RETURNS BIGINT AS $$
DECLARE
	v_from_subsystem_id	ALIAS FOR $1;
	v_to_system_id		ALIAS FOR $2;
	v_success		BOOLEAN;
	v_subsystem_id		BIGINT;
BEGIN
	INSERT INTO coincomo_subsystems_table (
		_subsystem_name,
		system_id,
		sloc,
		cost,
		staff,
		effort,
		schedule,
		_zoom_level
		)
	SELECT	'Copy of ' || S._subsystem_name,
		v_to_system_id,
		S.sloc,
		S.cost,
		S.staff,
		S.effort,
		S.schedule,
		S._zoom_level
	FROM coincomo_subsystems_table AS S
	WHERE S.subsystem_id = v_from_subsystem_id
	RETURNING subsystem_id INTO v_subsystem_id;

	IF FOUND THEN
		v_success := true;
	ELSE
		v_success := false;
	END IF;

	PERFORM Copy_Component(C.component_id, v_subsystem_id)
	FROM coincomo_components_table AS C
	WHERE C.subsystem_id = v_from_subsystem_id;

	IF v_success THEN
		RETURN v_subsystem_id;
	ELSE
		RETURN -1;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Copy_System(BIGINT) RETURNS BIGINT AS $$
DECLARE
	v_from_system_id	ALIAS FOR $1;
	v_success		BOOLEAN;
	v_system_id		BIGINT;
BEGIN
	INSERT INTO coincomo_systems_table (
		_system_name,
		sloc,
		cost,
		staff,
		effort,
		schedule
		)
	SELECT	'Copy of ' || S._system_name,
		S.sloc,
		S.cost,
		S.staff,
		S.effort,
		S.schedule
	FROM coincomo_systems_table AS S
	WHERE S.system_id = v_from_system_id
	RETURNING system_id INTO v_system_id;

	IF FOUND THEN
		v_success := true;
	ELSE
		v_success := false;
	END IF;

	PERFORM Copy_SubSystem(S.subsystem_id, v_system_id)
	FROM coincomo_subsystems_table AS S
	WHERE S.system_id = v_from_system_id;

	IF v_success THEN
		RETURN v_system_id;
	ELSE
		RETURN -1;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_SystemName(BIGINT, VARCHAR(60)) RETURNS BOOLEAN AS
$$
BEGIN
	UPDATE coincomo_systems_table
	SET	_system_name = $2
	WHERE system_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
EXCEPTION
        WHEN unique_violation THEN
        RETURN false;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_SubSystemName(BIGINT, VARCHAR(60)) RETURNS BOOLEAN AS $$
BEGIN
	UPDATE coincomo_subsystems_table
	SET	_subsystem_name = $2
	WHERE subsystem_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
EXCEPTION
        WHEN unique_violation THEN
        RETURN false;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_ComponentName(BIGINT, VARCHAR(60)) RETURNS BOOLEAN AS $$
BEGIN
	UPDATE coincomo_components_table
	SET	_component_name = $2
	WHERE component_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
EXCEPTION
        WHEN unique_violation THEN
        RETURN false;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_SubComponentName(BIGINT, VARCHAR(60)) RETURNS BOOLEAN AS $$
BEGIN
	UPDATE coincomo_subcomponents_table
	SET	_subcomponent_name = $2
	WHERE subcomponent_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
EXCEPTION
        WHEN unique_violation THEN
        RETURN false;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Update_AdaptationAndReuseName(BIGINT, VARCHAR(60)) RETURNS BOOLEAN AS $$
BEGIN
	UPDATE coincomo_adaptation_and_reuses_table
	SET	_adaptation_and_reuse_name = $2
	WHERE adaptation_and_reuse_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
EXCEPTION
        WHEN unique_violation THEN
        RETURN false;
END;
$$ LANGUAGE plpgsql;

---///////////////////////////////////////////////////////////////////
-- COINCOMO Clear Functions
---///////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION Clear_AllSubSystems(BIGINT) RETURNS BOOLEAN AS $$
BEGIN
	DELETE
	FROM	coincomo_subsystems_table
	WHERE system_id = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;

---///////////////////////////////////////////////////////////////////
-- COINCOMO Helper Functions
---///////////////////////////////////////////////////////////////////

CREATE OR REPLACE FUNCTION Has_SystemName(VARCHAR(60)) RETURNS BOOLEAN AS $$
BEGIN
        PERFORM _system_name
	FROM	coincomo_systems_table
	WHERE _system_name = $1;

	IF FOUND THEN
		RETURN true;
	ELSE
		RETURN false;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION Get_DefaultSystemName() RETURNS VARCHAR(60) AS $$
DECLARE
        v_system_name   VARCHAR(60);
BEGIN
	SELECT _system_name
        INTO    v_system_name
	FROM	coincomo_systems_table
	WHERE _system_name LIKE '(System%)'
        ORDER BY _system_name DESC
        LIMIT 1;

	IF FOUND THEN
		RETURN v_system_name;
	ELSE
		RETURN '(System)';
	END IF;
END;
$$ LANGUAGE plpgsql;

-- Function: match_user(character varying, character varying)

-- DROP FUNCTION match_user(character varying, character varying);

CREATE OR REPLACE FUNCTION match_user(character varying, character varying)
  RETURNS bigint AS
$BODY$
DECLARE
	v_user_id	BIGINT;
BEGIN
	select user_id from coincomo_user_table where user_login_id = $1 and password = md5($2) INTO v_user_id;

	IF FOUND THEN
		RETURN v_user_id;
	ELSE
		RETURN -1;
	END IF;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION match_user(character varying, character varying)
  OWNER TO postgres;