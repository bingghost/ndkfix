package ndkfix;

import java.io.File;

public class NdkFix {
	
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	public static void deleteFile(String sPath) {   
		File file = new File(sPath);  
	    if (file.isFile() && file.exists()) {  
	        file.delete();  
	    }  
	}
	
	public static void fixNdkBugs(String path) {
		String project_path = path + "/.project"; 
		String cproject_path = path + "/.cproject";
		
		XmlOprate xmlOprate = new XmlOprate(project_path);
		xmlOprate.fixProjectConfig();
		
		deleteFile(cproject_path);
	}
	
	public static Boolean isFileExist(String path) {
		File file = new File(path);
		return file.exists();
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("usage: java -jar ndkfix.jar [project root path]");
			return;
		}
		
		if (!isFileExist(args[0])) {
			System.out.println("error: your path is not exist!!!");
			return;
		}
		
		fixNdkBugs(args[0]);		
	}
}
