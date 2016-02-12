package com.org.conc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.nio.file.Path;
/**
 * The system takes text file as input, and shows a keyword in context aligned with 
 * a user defined number of words to appear on the left and right side of the input keyword
 * @author elhaj
 *
 */
public class MoKWIC {
		
static String text = "";
static int matchesCount = 0;
static int fileWordsCount = 0;



	public static void main (String [] args) throws Exception {
		
		System.out.println("Welcome to MoKWIC");
		if (args[0].equalsIgnoreCase("help")){
				 System.out.println("To run MoKWIC: \"InputFile\" \"Keyword\" \"WordsToTheLeft\" \"WordsToTheRight\" \"OutputFile\"\n"+
						 "Example: Input.txt yesterday 10 10 Output.txt \n"+
						 "InputFile could be any text file; Keyword the term you want to search for (case insensitive);\n"+
						 "Words to the left and right are number of words you would like to see before and after the input keyowrd;\n"+
						 "OutputFile could be any text file.");		 
		System.exit(0);
		}
		if (((args.length < 4 || args.length > 5 ) && !args[0].equalsIgnoreCase("help"))) {
			 System.out.println("Wrong number of arguments !!!\nTo run MoKWIC: \"Path\" \"Keyword\" \"WordsToTheLeft\" \"WordsToTheRight\" \"OutputFile\""+
		 "Example: Input.txt yesterday 10 10 Output.txt\n"+
					 "If no Output file provdied system will print to console by default!");
			 System.exit(0);
		 }
	
		 
		 
		final float startTime = System.nanoTime();
		
/*        String search = "there";
        Path path = Paths.get("test/testBigFile.txt");
        int wordsLeft = 10;
        int wordsRight = 10;*/
        //new MoKWIC().readFile(path, search, wordsLeft, wordsRight);
		
		
		if(!new File(args[0]).exists()){
			System.out.println("Input file doesn't exist!!");
			System.exit(0);

		}
		
		if(args.length > 4 && args[4].trim().length()<1){
			System.out.println("Output file not valid!");
			System.exit(0);

		}
		
		
			
		if(args.length == 4){
        new MoKWIC().readFile(Paths.get(args[0].toString()), args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), "");
		}
		else{
			new MoKWIC().readFile(Paths.get(args[0].toString()), args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4]);	
		}
		final float duration = (System.nanoTime() - startTime)/1000000000;
		System.out.println("\n");
        
		System.out.println("==================================================================");
		customFormat("###,###.###", fileWordsCount);
		System.out.format("Found %d matches"+ " of the word << %s >> in "+ "%.4f, seconds", matchesCount, args[1].trim(), duration);
		
		 }
	
		private void readFile(Path path, String searchString, int wordsLeft, int wordsRight, String outputFile)
			throws IOException {
		    
		Formatter fmtFile = null;
		
		if(outputFile.length()>0)
	    fmtFile = new Formatter(new FileOutputStream(outputFile));
	    
		FileChannel fileChannel = FileChannel.open(path);
		
		ByteBuffer buffer = ByteBuffer.allocate(3000);

		int noOfBytesRead = fileChannel.read(buffer);
			
		while (noOfBytesRead != -1) {
			
			//System.out.println("Number of bytes read: " + noOfBytesRead);

			buffer.flip();

			//System.out.print("Buffer contents: ");
			
			while (buffer.hasRemaining()) {
				
				//System.out.print((char) buffer.get());
				CharBuffer line = Charset.defaultCharset().decode(buffer);
                text = line.toString();

			
            text = text.replace("\n", "").replace("\r", "");//remove line breaks so the output can align on one line.
           text = text.replaceAll("\\s+", " "); 
                //String[] words = text.trim().split("[ -+*/=,()\";:?!.\\s+]");
                List<String> wordsList = getWords(text);
                String[] words = (String[]) wordsList.toArray(new String[0]);
                fileWordsCount += words.length; //counting number of words in the file.
                
            	for(int i=0;i<words.length;i++){
                if(words[i].trim().equalsIgnoreCase((searchString.trim()))){
                	matchesCount++;
            		
            		String leftHandSide = "";
                	int index = 0;
                	if(i<wordsLeft)
                		index = i;
                	else
                		index = i-wordsLeft;
                	for(int a = index;a<i;a++){
                		leftHandSide+= words[a] + " ";
                	}
                	
                   String rightHandSide = "";
                   int from = 0;   
                   if(i+1>words.length)
                   from = i;   
                   else
                   from = i+1;
                  	int index2 = 0;
                  	if(i+wordsRight+1>words.length)
                  		index2 = i;
                  	else
                  		index2 = i+wordsRight+1;//plus one to compensate for the search term
                  	
                      for(int a=from;a<index2;a++){
                  		rightHandSide += words[a]+ " ";
                  	}
                     int gapFormat = wordsLeft*10; 
                     
                     if(outputFile.length()<1){
                     System.out.println("\n");
                     System.out.format("%"+gapFormat+"s %s %s", leftHandSide.trim(), " << " + words[i] + " >> ", rightHandSide.trim());
                     }
                     else{
	                    	 fmtFile.format("%"+gapFormat+"s %s %s", leftHandSide.trim(), " << " + words[i] + " >> ", rightHandSide.trim());
	                         fmtFile.format("%s", "\n");
                     }
    			}
                
    	     }
    			
                }
				buffer.clear();
    			noOfBytesRead = fileChannel.read(buffer);
    			 text = "";//empty text
		}
  		fileChannel.close();

  	
	
	}
			

	public static List<String> getWords(String text) {
	    List<String> words = new ArrayList<String>();
	    BreakIterator breakIterator = BreakIterator.getWordInstance();
	    breakIterator.setText(text);
	    int lastIndex = breakIterator.first();
	    while (BreakIterator.DONE != lastIndex) {
	        int firstIndex = lastIndex;
	        lastIndex = breakIterator.next();
	        if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex))) {
	            words.add(text.substring(firstIndex, lastIndex));
	        }
	    }

	    return words;
	}

	
	//Display number of words with comma decimal format (I'm just being fussy here) :-)
	   static public void customFormat(String pattern, double value ) {
		      DecimalFormat myFormatter = new DecimalFormat(pattern);
		      String output = myFormatter.format(value);
		      System.out.println("Input text number of words: " + output);
		   }
	   

}