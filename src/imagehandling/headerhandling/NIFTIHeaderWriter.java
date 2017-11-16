package imagehandling.headerhandling;

import ij.ImagePlus;
import ij.plugin.NiftiHeader;

/**
 * @author Dominik Ridder
 *
 */
public class NIFTIHeaderWriter {

	private StringBuilder header;

	/**
	 * This method generates a Nifti header to the given Nifti data.
	 * 
	 * @param data
	 *            The data, that contains the header information
	 * @return The generated NiftiHeader
	 */
	public String writeHeader(ImagePlus data) {
		header = new StringBuilder();
		NiftiHeader info = (NiftiHeader) data.getProperty("nifti");
		/*** HEADER STRUCTUR ********/
		// header.append("int sizeof_hdr: 348\n");
		// header.append("char data_type[10]: (*Unused)\n");
		// header.append("char ddb_name[10]: (*Unused)\n");
		// header.append("int extents: (*Unused)\n");
		// header.append("short session_error: (*Unused)\n");
		// header.append("char regular: (*Unused)\n");
		// header.append("int glmax: (*Unused)\n");
		// header.append("int glmin: (*Unused)\n");
		// header.append("*Unused: These Fields are not used in the NIFTI-1 format.\n");
		//
		// header.append("\n");

		header.append("MRI slice ordering: " + (char) info.dim_info + "\n");
		header.append("dim[8]: {");
		header.append(info.dim[0]);
		for (int i = 1; i < info.dim.length; i++) {
			header.append(", ");
			header.append(info.dim[i]);
		}
		header.append("}\n");

		header.append("intent_p1: " + info.intent_p1 + "\n");
		header.append("intent_p2: " + info.intent_p2 + "\n");
		header.append("intent_p3: " + info.intent_p3 + "\n");
		header.append("intent_code: " + info.intent_code + "\n\n");
		header.append("Datatype: " + getDataType(info.datatype) + " ("
				+ info.datatype + ")\n");
		header.append("bitpix: " + info.bitpix + "\n");
		header.append("slice_start: " + info.slice_start + "\n");

		header.append("pixdim[8]: {");
		header.append(info.pixdim[0]);
		for (int i = 1; i < info.pixdim.length; i++) {
			header.append(", ");
			header.append(info.pixdim[i]);
		}
		header.append("}\n");

		header.append("Voxel offset: " + info.vox_offset + "\n");
		header.append("Data scaling slope: " + info.scl_slope + "\n");
		header.append("Data scaling offset: " + info.scl_inter + "\n");
		header.append("slice_end: " + info.slice_end + "\n");
		header.append("slice_code: " + info.slice_code + "\n");
		header.append("xyzt_units: " + info.xyzt_units + "\n");
		header.append("Max display intensity: " + info.cal_max + "\n");
		header.append("Min display intensity: " + info.cal_min + "\n");
		header.append("Slice duration: " + info.slice_duration + "\n");
		header.append("Time axis shift: " + info.toffset + "\n");
		header.append("description: " + info.descrip + "\n");
		header.append("Auxiliary filename: " + info.aux_file + "\n");
		header.append("qform_code: " + info.qform_code + "\n");
		header.append("sform_code: " + info.sform_code + "\n");
		header.append("Quanternion b: " + info.quatern_b + "\n");
		header.append("Quanternion c: " + info.quatern_c + "\n");
		header.append("Quanternion d: " + info.quatern_d + "\n");
		header.append("Quanternion shift: {" + info.qoffset_x + ", "
				+ info.qoffset_y + ", " + info.qoffset_z + "}\n");

		header.append("\nAffine Matrix:\n");
		String spaceOffset = "   ";
		header.append(spaceOffset
				+ String.format("%.3f", info.srow_x[0]).replace(",", "."));
		for (int i = 1; i < info.srow_x.length; i++) {
			header.append(" ");
			header.append(String.format("%.3f", info.srow_x[i]).replace(",",
					"."));
		}
		header.append("\n");

		header.append(spaceOffset
				+ String.format("%.3f", info.srow_y[0]).replace(",", "."));
		for (int i = 1; i < info.srow_y.length; i++) {
			header.append(" ");
			header.append(String.format("%.3f", info.srow_y[i]).replace(",",
					"."));
		}
		header.append("\n");

		header.append(spaceOffset
				+ String.format("%.3f", info.srow_z[0]).replace(",", "."));
		for (int i = 1; i < info.srow_z.length; i++) {
			header.append(" ");
			header.append(String.format("%.3f", info.srow_z[i]).replace(",",
					"."));
		}
		header.append("\n\n");

		header.append("Name/Meaning of Data: " + info.intent_name + "\n");
		/***************************/

		/*** DICOM ***/

		if (info.dicom_extension != null) {
			header.append("\n\nDICOM Extension:\n\n");
			header.append(info.dicom_extension);
		}

		/*************/

		return header.toString();
	}

	public String getDataType(int key) {
		switch (key) {
		case 1:
			return "BINARY";
		case 2:
			return "UINT8";
		case 4:
			return "INT16";
		case 8:
			return "INT32";
		case 16:
			return "FLOAT32";
		case 32:
			return "COMPLEX64";
		case 64:
			return "FLOAT64";
		case 128:
			return "RGB24";
		case 256:
			return "INT8";
		case 512:
			return "UINT16";
		case 768:
			return "UINT32";
		case 1024:
			return "INT64";
		case 1280:
			return "UINT64";
		case 1536:
			return "FLOAT128";
		case 1792:
			return "COMPLEX128";
		case 2048:
			return "COMPLEX256";
		case 2304:
			return "RGBA32";
		default:
			return "Unkown";
		}
	}

}
