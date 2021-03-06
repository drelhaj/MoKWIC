# MoKWIC
Language independent simple KWIC Concordance

The system takes text file as input shows a keyword in context aligned with a user defined number of words to appear on the left and right side of the input keyword.
The system is language independent and works with English and other languages including right to left input such as Arabic.

# Requirements
* Requires java to run, the system was not tested on Java 8 but considering the library used it should run smoothly.
* Has been tested on both Linux and Windows and run smoothly on both.
* Has been tested on large files (c7 million words) with a processing time not exceeding 4 seconds.
* The system runs in a UTF-8 environment by default and was confirmed to work with Linux, however Windows users: by setting the (Windows) environment variable JAVA_TOOL_OPTIONS to -Dfile.encoding=UTF8, the (Java) System property will be set automatically every time a JVM is started. 

# Execution
* Non technical users can go ahead and double click the MoKWIC.jar file in the runnable_jar folder
You can also run the GUI interface by running the MoKWICgui java class/file.

* Running MoKWIC from command line is fairly simple.
For further information you can use the help command.

To run MoKWIC you need InputFile, Keyword, Number of Words to the left, Number of Words to the right, OutputFile (Optional):
* Example: Input.txt yesterday 10 10 Output.txt
* InputFile could be any text file; Keyword the term you want to search for (case insensitive);
* Words to the left and right are number of words you would like to see before and after the input keyowrd;
* OutputFile could be any text file.

# Interface
MoKWIC is an open source command-line application to make it easy to integrate with other systems.
The system comes also in a simple GUI interface, which is implemented in a seprate file (MoKWICgui)

# Input/Output file format
MoKWIC works with UTF-8 file format for both input and output.
