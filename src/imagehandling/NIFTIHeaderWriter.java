package imagehandling;

import java.util.HashMap;

import ij.ImagePlus;
import ij.io.FileInfo;
import ij.plugin.NiftiHeader;

public class NIFTIHeaderWriter {
	
	private StringBuilder header;
	
	public String writeHeader(ImagePlus data){
		header = new StringBuilder();
		NiftiHeader info = (NiftiHeader) data.getProperty("nifti");
		/*** HEADER STRUCTUR ********/
//		header.append("int sizeof_hdr: 348\n");
//		header.append("char data_type[10]: (*Unused)\n");
//		header.append("char ddb_name[10]: (*Unused)\n");
//		header.append("int extents: (*Unused)\n");
//		header.append("short session_error: (*Unused)\n");
//		header.append("char regular: (*Unused)\n");
//		header.append("int glmax: (*Unused)\n");
//		header.append("int glmin: (*Unused)\n");
//		header.append("*Unused: These Fields are not used in the NIFTI-1 format.\n");
//		
//		header.append("\n");
		
		header.append("dim_info: "+(char)info.dim_info+"\n");
		header.append("dim[8]: {");
		header.append(info.dim[0]);
		for (int i=1; i<info.dim.length; i++){
			header.append(", ");
			header.append(info.dim[i]);
		}
		header.append("}\n");
		
		header.append("intent_p1: "+info.intent_p1+"\n");
		header.append("intent_p2: "+info.intent_p2+"\n");
		header.append("intent_p3: "+info.intent_p3+"\n");
		header.append("intent_code: "+info.intent_code+"\n\n");
		header.append("datatype: "+info.datatype+"\n");
		header.append("bitpix: "+info.bitpix+"\n");
		header.append("slice_start: "+info.slice_start+"\n");
		
		header.append("pixdim[8]: {");
		header.append(info.pixdim[0]);
		for (int i=1; i<info.pixdim.length; i++){
			header.append(", ");
			header.append(info.pixdim[i]);
		}
		header.append("}\n");
		
		header.append("vox_offset: "+info.vox_offset+ "\n");
		header.append("scl_slope: "+info.scl_slope+ "\n");
		header.append("scl_inter: "+info.scl_inter+ "\n");
		header.append("slice_end: "+info.slice_end+ "\n");
		header.append("slice_code: "+info.slice_code+"\n");
		header.append("xyzt_units: "+info.xyzt_units+"\n");
		header.append("cal_max: "+info.cal_max+"\n");
		header.append("cal_min: "+info.cal_min+"\n");
		header.append("slice_duration: "+info.slice_duration+"\n");
		header.append("toffset: "+info.toffset+"\n");
		header.append("description: "+info.descrip+"\n");
		header.append("aux_file: "+info.aux_file+"\n");
		header.append("qform_code: "+info.qform_code+"\n");
		header.append("sform_code: "+info.sform_code+"\n");
		header.append("quatern_b: "+info.quatern_b+"\n");
		header.append("quatern_c: "+info.quatern_c+"\n");
		header.append("quatern_d: "+info.quatern_d+"\n");
		header.append("qoffset_x: "+info.qoffset_x+"\n");
		header.append("qoffset_y: "+info.qoffset_y+"\n");
		header.append("qoffset_z: "+info.qoffset_z+"\n\n");
		
		header.append("srow_x[4]: {");
		header.append(info.srow_x[0]);
		for (int i=1; i<info.srow_x.length; i++){
			header.append(", ");
			header.append(info.srow_x[i]);
		}
		header.append("}\n");
		
		header.append("srow_y[4]: {");
		header.append(info.srow_y[0]);
		for (int i=1; i<info.srow_y.length; i++){
			header.append(", ");
			header.append(info.srow_y[i]);
		}
		header.append("}\n");
		
		header.append("srow_z[4]: {");
		header.append(info.srow_z[0]);
		for (int i=1; i<info.srow_z.length; i++){
			header.append(", ");
			header.append(info.srow_z[i]);
		}
		header.append("}\n\n");
		
		header.append("intent_name: "+info.intent_name+"\n");
		/***************************/
		
		/*** DICOM ***/
		
		if (info.dicom_extension != null){
			header.append("\n\nDICOM Extension:\n\n");
			header.append(info.dicom_extension);
		}
		
		/*************/
		
		return header.toString();
	}
	
}
