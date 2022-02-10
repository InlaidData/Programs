package edu.nmsu.cs.webserver;

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
import java.io.FileNotFoundException;
import java.io.IOException;

public class WebWorker implements Runnable
{

	private Socket socket;
	private String filePath;
	Date currentDate = new Date();
	SimpleDateFormat formatter = new SimpleDateFormat("mm-dd-yyyy");
	Scanner myFileHandler;
	String[] replaceTagList = {"<cs371date>", "<cs371server>"};
	String[] targetPhraseList = {formatter.format(currentDate), "Dane's CS 371 Server!"};
	

	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker(Socket s)
	{
		socket = s;
	}

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
			writeHTTPHeader(os, "text/html");
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
		boolean flag = false;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				if (!flag){
					filePath = "C:/NMSU/CS 371 Software Development/Programs/SimpleWebServer" + line.substring(line.indexOf(' ') + 1,line.lastIndexOf(' '));
					flag = true;
					System.out.println(filePath);
				}

				System.err.println("Request line: (" + line + ")");//request line 1 recieves whatever is typed into the web browser. //these lines will be useful for assignment.
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
	private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
	{
		Date d = new Date();
		boolean fileFound = true;
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		try {
			myFileHandler = new Scanner(new File(filePath));
		} catch (FileNotFoundException e){
			fileFound = false;
			myFileHandler = new Scanner(new File("C:/NMSU/CS 371 Software Development/Programs/SimpleWebServer/res/acc/PageNotFound.html"));
		}
		if(fileFound)
			os.write("HTTP/1.1 200 OK\n".getBytes()); //Our code should 404 not found if the browser requests something that doesn't exist.
		else 
			os.write("HTTP/1.1 404 File Not Found\n".getBytes());

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
		String unparsedLine = new String("");
		while(myFileHandler.hasNextLine()) {
			unparsedLine = myFileHandler.nextLine();
			System.out.println(unparsedLine);
			System.out.println(findTagAndReplace(unparsedLine));

			os.write((findTagAndReplace(unparsedLine) + "\n").getBytes());
		 }

		/*os.write("<html><head></head><body>\n".getBytes());
		os.write("<h3>My web server works!</h3>\n".getBytes());
		os.write("</body></html>\n".getBytes()); */
	}

} // end class
