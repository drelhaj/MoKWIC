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
 * @url Tool web page: http://drelhaj.github.io/MoKWIC/
 * @url Available on GitHub: https://github.com/drelhaj/MoKWIC
 */
public class MoKWIC {
		
static String text = "";
static int matchesCount = 0;
static int fileWordsCount = 0;

	public static void main (String [] args) throws Exception {

		//noting the sytem start time to calculate running time

		
		//Welcome and help
		System.out.println("Welcome to MoKWIC");
		if (args[0].equalsIgnoreCase("help")){
				 System.out.println("To run MoKWIC: \"InputFile\" \"Keyword\" \"WordsToTheLeft\" \"WordsToTheRight\" \"OutputFile\"\n"+
						 "Example: Input.txt yesterday 10 10 Output.txt \n"+
						 "InputFile could be any text file; Keyword the term you want to search for (case insensitive);\n"+
						 "Words to the left and right are number of words you would like to see before and after the input keyowrd;\n"+
						 "OutputFile could be any text file.");		 
		System.exit(0);
		}
		
		//make sure users inserted the correct number of arguments, if first argument is help the rest will be ignored and help will be provided to the user
		if (((args.length < 4 || args.length > 5 ) && !args[0].equalsIgnoreCase("help"))) {
			 System.out.println("Wrong number of arguments !!!\nTo run MoKWIC: \"Path\" \"Keyword\" \"WordsToTheLeft\" \"WordsToTheRight\" \"OutputFile\""+
		 "Example: Input.txt yesterday 10 10 Output.txt\n"+
					 "If no Output file provdied system will print to console by default!");
			 System.exit(0);
		 }
	
		 	
		//make sure input file exists before calling the Kwic readFile method
		if(!new File(args[0]).exists()){
			System.out.println("Input file doesn't exist!!");
			System.exit(0);
		}
		
		//Make sure output file is valid (further validation could be helpful here e.g. Linux vs Windows naming the use of special characters and reserved names)
		if(args.length > 4 && args[4].trim().length()<1){
			System.out.println("Output file not valid!");
			System.exit(0);
		}
		
		
		//Make sure output file is valid (further validation could be helful here e.g. Linux vs Windows naming the use of special characters and reserved names)
		if(!isParsable(args[2]) || !isParsable(args[3])){
			System.out.println("Invalid number of words!");
			System.exit(0);
		}
		else{
			if(Integer.parseInt(args[2]) <= 0 || Integer.parseInt(args[3]) <= 0){
				System.out.println("Number of words can't be less than 1");
				System.exit(0);
			}
		}
		
	//check number of arguments as the output file is optional the method can be called with the output file length = 0 and output will be printed on console.		
		if(args.length == 4){
        new MoKWIC().readFile(Paths.get(args[0].toString()), args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), "");
		}
		else{
			new MoKWIC().readFile(Paths.get(args[0].toString()), args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4]);	
		}
		
		
		 }
	

	
	// This is the Kwic main method. Reads a file and it's window size and provide output either on console or output file provided by the user.
	void readFile(Path path, String searchString, int wordsLeft, int wordsRight, String outputFile)
			throws IOException {
				final float startTime = System.nanoTime();

		
		//need to write to file and preserving formatting (i.e. aligning keyword in different concordance sentences).
		Formatter fmtFile = null;
		
		//if input file exists output will be written to file.
		if(outputFile.length()>0)
	    fmtFile = new Formatter(new FileOutputStream(outputFile));
	    
		//create a file channel and buffer. Here I use Non-blocking I/O libraries needed when dealing with intensive input/output.
		//Conventional read file methods (e.g. StringBuffer, InputStream, Scanner) may overload the system when dealing with large files as the 
		//buffer gets full quickly reducing efficiency and taking long time to process data, which may also lead to a java heap space error.
		FileChannel fileChannel = FileChannel.open(path);
		ByteBuffer buffer = ByteBuffer.allocate(3000);

		int noOfBytesRead = fileChannel.read(buffer);
			
		while (noOfBytesRead != -1) {
			
			//flip buffer between reading and writing.
			buffer.flip();

			//loop through buffer contents
			while (buffer.hasRemaining()) {
				
				//extract text from the buffer
				CharBuffer line = Charset.defaultCharset().decode(buffer);
                text = line.toString();

           
           text = text.replace("\n", "").replace("\r", "");//remove line breaks so the output can align on one line.
           text = text.replaceAll("\\s+", " "); //remove leading, trailing and in between extra spaces.
           
                //I decided to switch from using split(delimiters) to using BreakIterator as I believe it's much better in detecting text boundaries than split, as with the 
               //latter I did struggle with defining the delimiters especially when dealing with different languages.
                List<String> wordsList = getWords(text);
                String[] words = (String[]) wordsList.toArray(new String[0]);
                fileWordsCount += words.length; //counting number of words in the file.
                
                //get the n number of words to the left and right (they don't need to be equal n is metaphorical here)
            	for(int i=0;i<words.length;i++){
            		//I use a simple match here (exact match rather than contains) can be scalable if needed in the future.
            		//IgnoreCase doesn't affect case-less (no capital letters) languages such as Arabic.
                if(words[i].trim().equalsIgnoreCase((searchString.trim()))){
                	matchesCount++;
            		
                	//words to the left of the search term
            		String leftHandSide = "";
                	int index = 0;
                	if(i<wordsLeft)
                		index = i;
                	else
                		index = i-wordsLeft;
                	for(int a = index;a<i;a++){
                		leftHandSide+= words[a] + " ";
                	}

                	//words to the right of the search term
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
                    
                      //this to make sure the text doesn't get glued to the left side of the screen
                      //I make the distance from the left side of the screen dynamic thus relying on the number of words to the left of the search term.
                      //requires scrolling right sometimes but still preserves format.
                      int gapFormat = wordsLeft*10; 
                     
                      //if no output file provided, output will be printed to console formated as below, otherwise printed to user output file
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
			//clears buffer after each chunk processing is completed 
				buffer.clear();
    			noOfBytesRead = fileChannel.read(buffer);
    			 text = "";//empty text
		}
		//close channel when no more text (bytes) remaining in the buffer.
  		fileChannel.close();

		//calculate running time in seconds
		final float duration = (System.nanoTime() - startTime)/1000000000;
		System.out.println("\n");
        
		//display output to the user on console (will also be printed to Outputfile if it exists
		System.out.println("==================================================================");
		customFormat("###,###.###", fileWordsCount);
		System.out.format("Found %d matches"+ " of the word << %s >> in "+ "%.4f, seconds", matchesCount, searchString.trim(), duration);
		System.out.println("\n==================================================================");

	
	
	}
			

	//iterates over text to extract words through detecting boundaries in text.
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