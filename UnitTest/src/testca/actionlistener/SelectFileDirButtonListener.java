package testca.actionlistener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class SelectFileDirButtonListener implements ActionListener {
	private JFrame jframe;
	
	public SelectFileDirButtonListener(JFrame jframe){
		this.jframe = jframe ; 
	}
	
	
	public void actionPerformed(ActionEvent event) {
		JFileChooser fchFile = new JFileChooser();
        fchFile.showSaveDialog(jframe);
       String currentFileName = fchFile.getSelectedFile().getPath();
       System.out.println(currentFileName);
	}

}
