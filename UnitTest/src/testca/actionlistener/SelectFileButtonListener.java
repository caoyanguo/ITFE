package testca.actionlistener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;

import testca.windows.AddCAFrame;

public class SelectFileButtonListener implements ActionListener {
	private AddCAFrame jframe;
	
	public SelectFileButtonListener(AddCAFrame jframe){
		this.jframe = jframe ; 
	}
	
	
	public void actionPerformed(ActionEvent event) {
		JFileChooser fchFile = new JFileChooser();
        fchFile.showOpenDialog(jframe);
       String currentFileName = fchFile.getSelectedFile().getPath();
       jframe.text1.setText(currentFileName);
	}

}
