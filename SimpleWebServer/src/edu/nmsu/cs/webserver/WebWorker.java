package edu.nmsu.cs.webserver;

//NOTE: The current working directory when using relative paths (./) is the directory you were in when you executed the java command.

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor the object executes on its "run" method, and leaves
 * when it is done.
 *
 * One WebWorker object is only responsible for one client connection. This code uses Java threads
 * to parallelize the handling of clients: each WebWorker runs in its own thread. This means that
 * you can essentially just think about what is happening on one client at a time, ignoring the fact
 * that the entirety of the webserver execution might be handling other clients, too.
 *
 * This WebWorker class (i.e., an object of this class) is where all the client interaction is done.
 * The "run()" method is the beginning -- think of it as the "main()" for a client interaction. It
 * does three things in a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it writes out some HTML
 * content for the response content. HTTP requests and responses are just lines of text (in a very
 * particular format).
 // ./ means current working directory.

 //C:/NMSU/CS 371 Software Development/Programs/SimpleWebServer/www
 * 
 * @author Jon Cook, Ph.D.
 *
 **/

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WebWorker implements Runnable
{

	//-----------------------------------Socket/Date/ Handling------------------------------
	private Socket socket;
	Date currentDate = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
	
	//-------------------------------------File Handling-------------------------------------
	File foundFile;
	FileInputStream myFileHandler;
	long fileLength = 0;
	private String filePath;

	//------------------------------------String Processing----------------------------------
	String[] replaceTagList = {"<cs371date>", "<cs371server>"};
	String[] targetPhraseList = {formatter.format(currentDate), "Dane's CS 371 Server!"};
	private String contentType = new String();
	

	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker(Socket s)
	{
		socket = s;
	}

	//Purpose: Finds a given tag listed in "replaceTagList" and replaces it with the corresponding "targetPhraseList"
	//Parameters: input string to edit.
	public String findTagAndReplace(String input){
		if (input == null)
			return null;

		String output = input;

		for (int i = 0; i < replaceTagList.length; i++){
			if(input.indexOf(replaceTagList[i]) != -1)
				output = input.replace(replaceTagList[i], targetPhraseList[i]);
		}//end for
		return output;
	}//end method

	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 **/
	public void run()
	{
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			//Next three lines is where all the work for this assignment happens.
			readHTTPRequest(is);
			System.out.println("here");
			writeHTTPHeader(os);
			writeContent(os);
			os.flush();
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/
	private void readHTTPRequest(InputStream is)
	{
		String line;
		String extension;
		boolean flag = false;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");//request line 1 recieves whatever is typed into the web browser. //these lines will be useful for assignment.
				if (!flag){
					filePath = line.substring(line.indexOf(' ') + 2,line.lastIndexOf(' '));
					flag = true;
				}

					if (line.length() == 0)
					break;
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
		return;
	}

	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os) throws Exception
	{	
		Date d = new Date();
		boolean fileFound = true;
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			foundFile = new File(filePath);
			fileLength = foundFile.length();
			myFileHandler = new FileInputStream(foundFile);
			System.out.println("We did find the file");
		} catch (Exception e){
			System.out.println("Failed to find file, defaulting to 404 page");
			fileFound = false;
			foundFile = new File("res/acc/PageNotFound.html");
		}
		if(fileFound) //If the file was found, send a 200 status code and set the content type.
		{
			os.write("HTTP/1.1 200 OK\n".getBytes()); //Our code should 404 not found if the browser requests something that doesn't exist.
			setMime(filePath);
		}
		else //If file was not found, send a 404 status code and set content type to html.
		{
			os.write("HTTP/1.1 404 File Not Found\n".getBytes());
			contentType = "text/html";
		}

		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Jon's very own server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes()); // this is the type of content we want the browser to interpret. 
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
		return;
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os) throws Exception
	{

		if (contentType.equals("text/html")) //If the content type is html, write to the socket output stream one line at at time. This way, we can use findAndReplace()
		{
			String unparsedLine = new String("");
			Scanner myScan = new Scanner(foundFile);

		    while(myScan.hasNextLine()) {
				unparsedLine = myScan.nextLine();
				System.out.println(unparsedLine);
				System.out.println(findTagAndReplace(unparsedLine));
				os.write((findTagAndReplace(unparsedLine) + "\n").getBytes());
		    }//end while
		}//end if
		else //If it is anything but html, use the FileInputStream to get raw binary data and put it into a byte array.
		{
			byte[] outputBytes = new byte[(int)fileLength];
			myFileHandler.read(outputBytes); //outputBytes now contains the raw bytes of the file.
			os.write(outputBytes); //Write the entire outputBytes array to the output stream.
		}//end else

	}

	//Pre-condition: the file that is about to be served must actually exist or this method will break.
	//Purpose: sets the content type depending on the file requested.
	//Parameters: The line containing the filepath. Will grab the file extension from this line.
	private void setMime(String line)
	{
		String extension = line.substring(line.indexOf('.')); //Grabs extension.
		System.out.println(extension);
		//If statement below sets contentType based on grabed extension.
		if (extension.equals(".html"))
		{
			contentType = "text/html";
		}
		else if (extension.equals(".png"))
		{
			contentType = "image/png";
		}
		else if (extension.equals(".jpeg") || extension.equals(".jpg"))
		{
			contentType = "image/jpeg";
		}
		else if (extension.equals(".gif"))
		{
			contentType = "image/gif";
		}
		else if (extension.equals(".ico"))
		{
			contentType = "image/ico";
		}

	}//end method

} // end class
