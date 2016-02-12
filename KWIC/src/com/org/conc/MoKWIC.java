package com.org.conc;

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

public class MoKWIC {
		
static String text = "";
static int matchesCount = 0;
static int fileWordsCount = 0;



	public static void main (String [] args) throws Exception {
		
		System.out.println("Welcome to MoKWIC");
		 if ((args.length < 4)) {
			 System.out.println("Incomplete arguments !!!\nTo run MoKWIC: \"Path\" \"Keyword\" \"WordsToTheLeft\" \"WordsToTheRight\"");
		 }
	
		 
		 
		final float startTime = System.nanoTime();
		int printMethod = Integer.parseInt(args[4]); //when 1 prints output to file. 0: prints output toconsole 
		
/*        String search = "there";
        Path path = Paths.get("test/testBigFile.txt");
        int wordsLeft = 10;
        int wordsRight = 10;*/
        //new MoKWIC().readFile(path, search, wordsLeft, wordsRight);
        new MoKWIC().readFile(Paths.get(args[0].toString()), args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        
		final float duration = (System.nanoTime() - startTime)/1000000000;
		System.out.println("\n");
        
		System.out.println("==================================================================");
		customFormat("###,###.###", fileWordsCount);
		System.out.format("Found %d matches"+ " of the word << %s >> in "+ "%.4f, seconds", matchesCount, args[1].trim(), duration, printMethod);
		
		 }
	
	
	@SuppressWarnings("resource")
	private void readFile(Path path, String searchString, int wordsLeft, int wordsRight, int printMethod)
			throws IOException {
		
		
		//Formatter fmtCon = new Formatter(System.out);
	    Formatter fmtFile;
	    fmtFile = new Formatter(new FileOutputStream("test.txt"));
	    
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
                     
                     if(printMethod == 0){
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