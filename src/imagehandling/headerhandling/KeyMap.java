package imagehandling.headerhandling;

/**
 * The KeyMap is used for some of the methods get Attribute in the classes image
 * and volume.
 * 
 * @author Dominik Ridder
 */
public enum KeyMap {
	/**
	 * TODO:JAVADOC field description.
	 */
	KEY_MEDIA_STORAGE_SOP_CLASS_UID,
	/**
	 * TODO:JAVADOC field description.
	 */
	KEY_MEDIA_STORAGE_SOP_INST_UID,
	/**
	 * TODO:JAVADOC field description.
	 */
	KEY_TRANSFER_SYNTAX_UID,
	/**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMPLEMENTATION_CLASS_UID,
	/**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMPLEMENTATION_VERSION_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SPECIFIC_CHARACTER_SET, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMAGE_TYPE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_INSTANCE_CREATION_DATE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_INSTANCE_CREATION_TIME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SOP_CLASS_UID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SOP_INSTANCE_UID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_STUDY_DATE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SERIES_DATE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ACQUISITION_DATE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMAGE_DATE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_STUDY_TIME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SERIES_TIME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ACQUISITION_TIME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMAGE_TIME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ACCESSION_NUMBER, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_MODALITY, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_MANUFACTURER, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_INSTITUTION_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_INSTITUTION_ADDRESS, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_REFERRING_PHYSICIANS_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_STATION_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_STUDY_DESCRIPTION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SERIES_DESCRIPTION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_INSTITUTIONAL_DEPARTMENT_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ATTENDING_PHYSICIANS_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_OPERATORS_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_MANUFACTURERS_MODEL_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_REFERENCED_IMAGE_SEQUENCE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_REFERENCED_SOP_CLASS_UID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_REFERENCED_SOP_INSTANCE_UID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PATIENTS_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PATIENT_ID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PATIENTS_BIRTH_DATE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PATIENTS_SEX, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PATIENTS_AGE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PATIENTS_SIZE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PATIENTS_WEIGHT, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_BODY_PART_EXAMINED, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SCANNING_SEQUENCE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SEQUENCE_VARIANT, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SCAN_OPTIONS, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_MR_ACQUISITION_TYPE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SEQUENCE_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ANGIO_FLAG, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SLICE_THICKNESS, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_REPETITION_TIME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ECHO_TIME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_NUMBER_OF_AVERAGES, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMAGING_FREQUENCY, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMAGED_NUCLEUS, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ECHO_NUMBERS_S, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_MAGNETIC_FIELD_STRENGTH, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SPACING_BETWEEN_SLICES, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_NUMBER_OF_PHASE_ENCODING_STEPS, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ECHO_TRAIN_LENGTH, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PERCENT_SAMPLING, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PERCENT_PHASE_FIELD_OF_VIEW, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PIXEL_BANDWIDTH, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_DEVICE_SERIAL_NUMBER, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SOFTWARE_VERSIONS_S, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PROTOCOL_NAME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_DATE_OF_LAST_CALIBRATION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_TIME_OF_LAST_CALIBRATION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_TRANSMITTING_COIL, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ACQUISITION_MATRIX, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PHASE_ENCODING_DIRECTION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_FLIP_ANGLE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_VARIABLE_FLIP_ANGLE_FLAG, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SAR, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_DB_DT, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PATIENT_POSITION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_STUDY_INSTANCE_UID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SERIES_INSTANCE_UID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_STUDY_ID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SERIES_NUMBER, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ACQUISITION_NUMBER, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMAGE_NUMBER, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMAGE_POSITION_PATIENT, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_IMAGE_ORIENTATION_PATIENT, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_FRAME_OF_REFERENCE_UID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_POSITION_REFERENCE_INDICATOR, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SLICE_LOCATION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SAMPLES_PER_PIXEL, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PHOTOMETRIC_INTERPRETATION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_ROWS, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_COLUMNS, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PIXEL_SPACING, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_BITS_ALLOCATED, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_BITS_STORED, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_HIGH_BIT, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PIXEL_REPRESENTATION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_SMALLEST_IMAGE_PIXEL_VALUE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_LARGEST_IMAGE_PIXEL_VALUE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_WINDOW_CENTER, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_WINDOW_WIDTH, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_WINDOW_CENTER_AND_WIDTH_EXPLANATION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_REQUESTED_PROCEDURE_DESCRIPTION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PERFORMED_PROCEDURE_STEP_START_DATE, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PERFORMED_PROCEDURE_STEP_START_TIME, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PERFORMED_PROCEDURE_STEP_ID, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_PERFORMED_PROCEDURE_STEP_DESCRIPTION, /**
	 * TODO:JAVADOC field description.
	 */
	KEY_COMMENTS_ON_THE_PERFORMED_PROCEDURE_STEPS, /**
	 * TODO:JAVADOC field
	 * description.
	 */
	KEY_PIXEL_DATA;

	/**
	 * This method returns the value of an Enum. If the given type is equal to
	 * "dcm" or "IMA", than the value is a String, which contains 8 numbers
	 * like: "0002,0002".
	 * 
	 * @param type
	 * @return
	 */
	public String getValue(String type) {
		String str = "";
		switch (type) {
		case "dcm":
		case "IMA":
			str = imaValue();
			break;
		default:
			System.out.println("The Image type " + type
					+ " is not supported by the KeyMap.");
			break;
		}
		return str;
	}

	/**
	 * This Method is used to get the field representation of a tag. For
	 * Example:
	 * <p>
	 * KEY_STUDY_DATE is represented by "0008,0020"
	 * 
	 * @return The String, that is needed to find the information in the header.
	 */
	public String imaValue() {
		String str = "";
		switch (this) {
		case KEY_MEDIA_STORAGE_SOP_CLASS_UID:
			str = "0002,0002";
			break;
		case KEY_MEDIA_STORAGE_SOP_INST_UID:
			str = "0002,0003";
			break;
		case KEY_TRANSFER_SYNTAX_UID:
			str = "0002,0010";
			break;
		case KEY_IMPLEMENTATION_CLASS_UID:
			str = "0002,0012";
			break;
		case KEY_IMPLEMENTATION_VERSION_NAME:
			str = "0002,0013";
			break;
		case KEY_SPECIFIC_CHARACTER_SET:
			str = "0008,0005";
			break;
		case KEY_IMAGE_TYPE:
			str = "0008,0008";
			break;
		case KEY_INSTANCE_CREATION_DATE:
			str = "0008,0012";
			break;
		case KEY_INSTANCE_CREATION_TIME:
			str = "0008,0013";
			break;
		case KEY_SOP_CLASS_UID:
			str = "0008,0016";
			break;
		case KEY_SOP_INSTANCE_UID:
			str = "0008,0018";
			break;
		case KEY_STUDY_DATE:
			str = "0008,0020";
			break;
		case KEY_SERIES_DATE:
			str = "0008,0021";
			break;
		case KEY_ACQUISITION_DATE:
			str = "0008,0022";
			break;
		case KEY_IMAGE_DATE:
			str = "0008,0023";
			break;
		case KEY_STUDY_TIME:
			str = "0008,0030";
			break;
		case KEY_SERIES_TIME:
			str = "0008,0031";
			break;
		case KEY_ACQUISITION_TIME:
			str = "0008,0032";
			break;
		case KEY_IMAGE_TIME:
			str = "0008,0033";
			break;
		case KEY_ACCESSION_NUMBER:
			str = "0008,0050";
			break;
		case KEY_MODALITY:
			str = "0008,0060";
			break;
		case KEY_MANUFACTURER:
			str = "0008,0070";
			break;
		case KEY_INSTITUTION_NAME:
			str = "0008,0080";
			break;
		case KEY_INSTITUTION_ADDRESS:
			str = "0008,0081";
			break;
		case KEY_REFERRING_PHYSICIANS_NAME:
			str = "0008,0090";
			break;
		case KEY_STATION_NAME:
			str = "0008,1010";
			break;
		case KEY_STUDY_DESCRIPTION:
			str = "0008,1030";
			break;
		case KEY_SERIES_DESCRIPTION:
			str = "0008,103E";
			break;
		case KEY_INSTITUTIONAL_DEPARTMENT_NAME:
			str = "0008,1040";
			break;
		case KEY_ATTENDING_PHYSICIANS_NAME:
			str = "0008,1050";
			break;
		case KEY_OPERATORS_NAME:
			str = "0008,1070";
			break;
		case KEY_MANUFACTURERS_MODEL_NAME:
			str = "0008,1090";
			break;
		case KEY_REFERENCED_IMAGE_SEQUENCE:
			str = "0008,1140";
			break;
		case KEY_REFERENCED_SOP_CLASS_UID:
			str = "0008,1150";
			break;
		case KEY_REFERENCED_SOP_INSTANCE_UID:
			str = "0008,1155";
			break;
		case KEY_PATIENTS_NAME:
			str = "0010,0010";
			break;
		case KEY_PATIENT_ID:
			str = "0010,0020";
			break;
		case KEY_PATIENTS_BIRTH_DATE:
			str = "0010,0030";
			break;
		case KEY_PATIENTS_SEX:
			str = "0010,0040";
			break;
		case KEY_PATIENTS_AGE:
			str = "0010,1010";
			break;
		case KEY_PATIENTS_SIZE:
			str = "0010,1020";
			break;
		case KEY_PATIENTS_WEIGHT:
			str = "0010,1030";
			break;
		case KEY_BODY_PART_EXAMINED:
			str = "0018,0015";
			break;
		case KEY_SCANNING_SEQUENCE:
			str = "0018,0020";
			break;
		case KEY_SEQUENCE_VARIANT:
			str = "0018,0021";
			break;
		case KEY_SCAN_OPTIONS:
			str = "0018,0022";
			break;
		case KEY_MR_ACQUISITION_TYPE:
			str = "0018,0023";
			break;
		case KEY_SEQUENCE_NAME:
			str = "0018,0024";
			break;
		case KEY_ANGIO_FLAG:
			str = "0018,0025";
			break;
		case KEY_SLICE_THICKNESS:
			str = "0018,0050";
			break;
		case KEY_REPETITION_TIME:
			str = "0018,0080";
			break;
		case KEY_ECHO_TIME:
			str = "0018,0081";
			break;
		case KEY_NUMBER_OF_AVERAGES:
			str = "0018,0083";
			break;
		case KEY_IMAGING_FREQUENCY:
			str = "0018,0084";
			break;
		case KEY_IMAGED_NUCLEUS:
			str = "0018,0085";
			break;
		case KEY_ECHO_NUMBERS_S:
			str = "0018,0086";
			break;
		case KEY_MAGNETIC_FIELD_STRENGTH:
			str = "0018,0087";
			break;
		case KEY_SPACING_BETWEEN_SLICES:
			str = "0018,0088";
			break;
		case KEY_NUMBER_OF_PHASE_ENCODING_STEPS:
			str = "0018,0089";
			break;
		case KEY_ECHO_TRAIN_LENGTH:
			str = "0018,0091";
			break;
		case KEY_PERCENT_SAMPLING:
			str = "0018,0093";
			break;
		case KEY_PERCENT_PHASE_FIELD_OF_VIEW:
			str = "0018,0094";
			break;
		case KEY_PIXEL_BANDWIDTH:
			str = "0018,0095";
			break;
		case KEY_DEVICE_SERIAL_NUMBER:
			str = "0018,1000";
			break;
		case KEY_SOFTWARE_VERSIONS_S:
			str = "0018,1020";
			break;
		case KEY_PROTOCOL_NAME:
			str = "0018,1030";
			break;
		case KEY_DATE_OF_LAST_CALIBRATION:
			str = "0018,1200";
			break;
		case KEY_TIME_OF_LAST_CALIBRATION:
			str = "0018,1201";
			break;
		case KEY_TRANSMITTING_COIL:
			str = "0018,1251";
			break;
		case KEY_ACQUISITION_MATRIX:
			str = "0018,1310";
			break;
		case KEY_PHASE_ENCODING_DIRECTION:
			str = "0018,1312";
			break;
		case KEY_FLIP_ANGLE:
			str = "0018,1314";
			break;
		case KEY_VARIABLE_FLIP_ANGLE_FLAG:
			str = "0018,1315";
			break;
		case KEY_SAR:
			str = "0018,1316";
			break;
		case KEY_DB_DT:
			str = "0018,1318";
			break;
		case KEY_PATIENT_POSITION:
			str = "0018,5100";
			break;
		case KEY_STUDY_INSTANCE_UID:
			str = "0020,000D";
			break;
		case KEY_SERIES_INSTANCE_UID:
			str = "0020,000E";
			break;
		case KEY_STUDY_ID:
			str = "0020,0010";
			break;
		case KEY_SERIES_NUMBER:
			str = "0020,0011";
			break;
		case KEY_ACQUISITION_NUMBER:
			str = "0020,0012";
			break;
		case KEY_IMAGE_NUMBER:
			str = "0020,0013";
			break;
		case KEY_IMAGE_POSITION_PATIENT:
			str = "0020,0032";
			break;
		case KEY_IMAGE_ORIENTATION_PATIENT:
			str = "0020,0037";
			break;
		case KEY_FRAME_OF_REFERENCE_UID:
			str = "0020,0052";
			break;
		case KEY_POSITION_REFERENCE_INDICATOR:
			str = "0020,1040";
			break;
		case KEY_SLICE_LOCATION:
			str = "0020,1041";
			break;
		case KEY_SAMPLES_PER_PIXEL:
			str = "0028,0002";
			break;
		case KEY_PHOTOMETRIC_INTERPRETATION:
			str = "0028,0004";
			break;
		case KEY_ROWS:
			str = "0028,0010";
			break;
		case KEY_COLUMNS:
			str = "0028,0011";
			break;
		case KEY_PIXEL_SPACING:
			str = "0028,0030";
			break;
		case KEY_BITS_ALLOCATED:
			str = "0028,0100";
			break;
		case KEY_BITS_STORED:
			str = "0028,0101";
			break;
		case KEY_HIGH_BIT:
			str = "0028,0102";
			break;
		case KEY_PIXEL_REPRESENTATION:
			str = "0028,0103";
			break;
		case KEY_SMALLEST_IMAGE_PIXEL_VALUE:
			str = "0028,0106";
			break;
		case KEY_LARGEST_IMAGE_PIXEL_VALUE:
			str = "0028,0107";
			break;
		case KEY_WINDOW_CENTER:
			str = "0028,1050";
			break;
		case KEY_WINDOW_WIDTH:
			str = "0028,1051";
			break;
		case KEY_WINDOW_CENTER_AND_WIDTH_EXPLANATION:
			str = "0028,1055";
			break;
		case KEY_REQUESTED_PROCEDURE_DESCRIPTION:
			str = "0032,1060";
			break;
		case KEY_PERFORMED_PROCEDURE_STEP_START_DATE:
			str = "0040,0244";
			break;
		case KEY_PERFORMED_PROCEDURE_STEP_START_TIME:
			str = "0040,0245";
			break;
		case KEY_PERFORMED_PROCEDURE_STEP_ID:
			str = "0040,0253";
			break;
		case KEY_PERFORMED_PROCEDURE_STEP_DESCRIPTION:
			str = "0040,0254";
			break;
		case KEY_COMMENTS_ON_THE_PERFORMED_PROCEDURE_STEPS:
			str = "0040,0280";
			break;
		case KEY_PIXEL_DATA:
			str = "7FE0,0010";
			break;
		default:
			System.out.println("l/dcm key was found with this enum");
			break;
		}
		return str;

	}
}
