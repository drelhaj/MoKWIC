package com.org.conc;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class MoKWICgui {

	private JFrame frmMokwic;
	private JTextField leftWordsTF;
	private JTextField keywordTF;
	private JTextField rightWordsTF;
	private JTable table;
	private JButton openButton;
	private JTextField chosenFileTF;
	private JTextArea textArea;
	private JButton btnNewButton;
    

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		Charset.defaultCharset();
        System.setProperty("file.encoding", "UTF-8");
        
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MoKWICgui window = new MoKWICgui();
					window.frmMokwic.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MoKWICgui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		final JPanel panel = new JPanel();
		final JFileChooser fc;
		fc = new JFileChooser();
		 
		frmMokwic = new JFrame();
		frmMokwic.setTitle("MoKWIC");
		frmMokwic.setBounds(100, 100, 1074, 651);
		frmMokwic.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMokwic.getContentPane().setLayout(null);
		
		leftWordsTF = new JTextField();
		leftWordsTF.setBounds(84, 45, 39, 20);
		leftWordsTF.setText("10");
		frmMokwic.getContentPane().add(leftWordsTF);
		leftWordsTF.setColumns(10);
			
				
		keywordTF = new JTextField();
		keywordTF.setBounds(133, 45, 169, 20);
		keywordTF.setText("Yesterday");
		frmMokwic.getContentPane().add(keywordTF);
		keywordTF.setColumns(10);
		
		rightWordsTF = new JTextField();
		rightWordsTF.setBounds(312, 45, 39, 20);
		rightWordsTF.setText("10");
		frmMokwic.getContentPane().add(rightWordsTF);
		rightWordsTF.setColumns(10);
		
		textArea = new JTextArea();
		textArea.setBounds(10, 103, 1038, 500);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(10,103,1038,500);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		frmMokwic.getContentPane().add(scrollPane);
		
		final JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {
					
					if(!isParsable(leftWordsTF.getText())){
						leftWordsTF.setText("1");	
						JOptionPane.showMessageDialog(panel, "Word count must be a digit!\nSetting it back to 1", "Error", JOptionPane.ERROR_MESSAGE);
					}
					else
					if(!(Integer.parseInt(leftWordsTF.getText())>0)){
						leftWordsTF.setText("1");	
						JOptionPane.showMessageDialog(panel, "Word count should be grater than zero!\nSetting it back to 1", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
						if(!isParsable(rightWordsTF.getText())){
							rightWordsTF.setText("1");
							JOptionPane.showMessageDialog(panel, "Word count must be a digit!\nSetting it back to 1", "Error", JOptionPane.ERROR_MESSAGE);
						}
						else
						if(!(Integer.parseInt(rightWordsTF.getText())>0)){
							rightWordsTF.setText("1");	
							JOptionPane.showMessageDialog(panel, "Word count should be grater than zero!\nSetting it back to 1", "Error", JOptionPane.ERROR_MESSAGE);
	
						}
						//make sure file exists before calling the MoKWIC 
					if(new File(chosenFileTF.getText()).exists()){								
					redirectSystemStreams(); //to display console output on the textArea
					 String keyw = new String(keywordTF.getText().getBytes(), "UTF-8");//send UTF-8 query 
					new MoKWIC().readFile(Paths.get(chosenFileTF.getText()), keyw, Integer.parseInt(leftWordsTF.getText()), Integer.parseInt(rightWordsTF.getText()), "");
					}
					else
					{
						JOptionPane.showMessageDialog(panel, "Input File doesn't exist!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			
				
			}
		});
		searchButton.setBounds(388, 45, 89, 23);
		frmMokwic.getContentPane().add(searchButton);
		
		table = new JTable();
		table.setBackground(Color.YELLOW);
		table.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		table.setBounds(644, 578, -553, -455);
		frmMokwic.getContentPane().add(table);
		
		openButton = new JButton("Select File");
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				if (e.getSource() == openButton) {
				      int returnVal = fc.showOpenDialog(frmMokwic);

				      if (returnVal == JFileChooser.APPROVE_OPTION) {
				        File file = fc.getSelectedFile();
				        //This is where a real application would open the file.
				        chosenFileTF.setText(file.getAbsolutePath());
				      } else {
				    	  chosenFileTF.setText("");
				      }
			
				      
				}
			}
		});
		
		openButton.setBounds(10, 11, 117, 23);
		frmMokwic.getContentPane().add(openButton);
		
		chosenFileTF = new JTextField();
		chosenFileTF.setBounds(137, 11, 580, 20);
		chosenFileTF.setText("E:/test/test/3.txt");
		frmMokwic.getContentPane().add(chosenFileTF);
		chosenFileTF.setColumns(10);
		
		btnNewButton = new JButton("Highlight Keyword");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			highlight();
			}
		});
		btnNewButton.setBounds(487, 45, 147, 23);
		frmMokwic.getContentPane().add(btnNewButton);
		
		JLabel lblWords = new JLabel("#Left");
		lblWords.setBounds(84, 65, 46, 14);
		frmMokwic.getContentPane().add(lblWords);
		
		JLabel lblWords_1 = new JLabel("#Right");
		lblWords_1.setBounds(312, 65, 46, 14);
		frmMokwic.getContentPane().add(lblWords_1);
		
		JLabel lblInputKeyword = new JLabel("Keyword");
		lblInputKeyword.setBounds(133, 65, 79, 14);
		frmMokwic.getContentPane().add(lblInputKeyword);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
				leftWordsTF.setText("");
				rightWordsTF.setText("");
				keywordTF.setText("");
				chosenFileTF.setText("");
			}
		});
		btnClear.setBounds(644, 44, 89, 23);
		frmMokwic.getContentPane().add(btnClear);
		
		JLabel imgLabel = new JLabel("");
		imgLabel.setBounds(861, 11, 187, 86);
		frmMokwic.getContentPane().add(imgLabel);
		
		ImageIcon imgThisImg = new ImageIcon("logo.png");

		imgLabel.setIcon(imgThisImg);
		
	}
	
	private void updateTextArea(final String text) {
		  SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		      textArea.append(text);
		    }
		  });
		}
		 
		private void redirectSystemStreams() {
		  OutputStream out = new OutputStream() {
		    @Override
		    public void write(int b) throws IOException {
		      updateTextArea(String.valueOf((char) b));
		    }
		 
		    @Override
		    public void write(byte[] b, int off, int len) throws IOException {
		      updateTextArea(new String(b, off, len));
		    }
		 
		    @Override
		    public void write(byte[] b) throws IOException {
		      write(b, 0, b.length);
		    }
		  };
		 
		  System.setOut(new PrintStream(out, true));
		  System.setErr(new PrintStream(out, true));
		}

		  private void highlight(){


			  String charsToHighlight = keywordTF.getText().toLowerCase();
			    Highlighter h = textArea.getHighlighter();
			    h.removeAllHighlights();
			    String text = textArea.getText();
			    int index = text.indexOf(charsToHighlight);
                while(index >= 0){
                    try {                
                        h.addHighlight(index, index + charsToHighlight.length(), DefaultHighlighter.DefaultPainter);
                        index = text.toLowerCase().indexOf(charsToHighlight, index + charsToHighlight.length());
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                }
            
			  }
		

			//To check wether integer window size inserter by the user are parsable to integer
			public static boolean isParsable(String input){
			    boolean parsable = true;
			    try{
			        Integer.parseInt(input);
			    }catch(NumberFormatException e){
			        parsable = false;
			    }
			    return parsable;
			}
}
