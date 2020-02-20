package process6103;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

public class SelectFileDirButtonListener implements ActionListener {
	private PrcocessWindow jframe;
	
	public SelectFileDirButtonListener(PrcocessWindow jframe){
		this.jframe = jframe ; 
	}
	
	
	public void actionPerformed(ActionEvent event) {
		JFileChooser fchFile = new JFileChooser();
		File f=new File("c:\\filePath.obj");
		if(!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("创建文件时出错！"+e.getMessage());
			}
		}
		String path=FileUtil.getInstance().readFile("c:\\filePath.obj");
		if(!path.equals("")){
			fchFile.setCurrentDirectory(new File(path));
		}
		fchFile.setMultiSelectionEnabled(true);
        fchFile.showOpenDialog(jframe);
        File[] files=fchFile.getSelectedFiles();
        StringBuffer sbf=new StringBuffer("");
        String parentPath="";
        for(int i=0;i<files.length;i++){
        	if(i==files.length-1){
        		sbf.append(files[i].getAbsolutePath());
        		parentPath=files[i].getParent();
        	}else{
        		sbf.append(files[i].getAbsolutePath()+";");
        	}
        }
        FileUtil.getInstance().writeFile("c:\\filePath.obj", parentPath);
        String currentFileName = sbf.toString();
        jframe.text1.setText(currentFileName);
	}

}
