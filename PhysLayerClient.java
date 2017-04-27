/**
 * @author Michael Acosta
 * CS 380
 * Project 2
 * This project simulates the physical layer of communication with 64 bits to establish
 * a baseline, and then 32 encoded bytes that need to be decoded and sent back.
 */

import java.io.*;
import java.net.*; // For Socket interaction
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

public class PhysLayerClient {

	/**
	 * Contains the client and all the interactions
	 */
	public static void main(String[] args) {
		try {
			Socket proj2Client = new Socket("codebank.xyz", 38002);
			System.out.println("Connected to server.");
			
			InputStream inputStream = proj2Client.getInputStream();
			long total = 0;
			double baseline = 0;
			int current = 0, count = 0, signal = 0;
			
			while (count < 64) {
				current = inputStream.read();
				total += current;
				count++;
			}
			baseline = (double)total / count;
			System.out.println("Baseline established from preamble: " + baseline);
			
			int[] encodedData = new int[320];
			while (count < (384)) {
				current = inputStream.read();
				if ((double)(current) >= baseline) {
					if (signal == 1)
						signal = 0;
					else
						signal = 1;
				}
				
				encodedData[count - 64] = signal;
				total += current;
				count++;
		}
			
			String decodedData = "";
			int[] zero = {1, 1, 1, 1, 0}, one = {0, 1, 0, 0, 1}, two = {1, 0, 1, 0, 0},
					three = {1, 0, 1, 0, 1}, four = {0, 1, 0, 1, 0}, five = {0, 1, 0, 1, 1},
					six = {0, 1, 1, 1, 0}, seven = {0, 1, 1, 1, 1}, eight = {1, 0, 0, 1, 0},
					nine = {1, 0, 0, 1, 1}, a = {1, 0, 1, 1, 0}, b = {1, 0, 1, 1, 1},
					c = {1, 1, 0, 1, 0}, d = {1, 1, 0, 1, 1}, e = {1, 1, 1, 0, 0},
					f = {1, 1, 1, 0, 1};
			for (int i = 0; i + 5 <= encodedData.length; i += 5) {
				if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), zero))
					decodedData += "0";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), one))
					decodedData += "1";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), two))
					decodedData += "2";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), three))
					decodedData += "3";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), four))
					decodedData += "4";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), five))
					decodedData += "5";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), six))
					decodedData += "6";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), seven))
					decodedData += "7";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), eight))
					decodedData += "8";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), nine))
					decodedData += "9";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), a))
					decodedData += "A";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), b))
					decodedData += "B";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), c))
					decodedData += "C";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), d))
					decodedData += "D";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), e))
					decodedData += "E";
				else if (Arrays.equals(Arrays.copyOfRange(encodedData, i, (i + 5)), f))
					decodedData += "F";
			}
			
			System.out.println("Received 32 bytes: " + decodedData);
			byte [] response = DatatypeConverter.parseHexBinary(decodedData);
			
			// Create an output stream to send the error code
			DataOutputStream outputStream = new DataOutputStream(proj2Client.getOutputStream());
			// writeInt send the four bytes individually
			outputStream.write(response, 0, response.length);
			
			// Obtain a response from the server, and display an appropriate message
			int answer = inputStream.read();
			if (answer == 1) { // Correct 32 bytes received
				System.out.println("Response good.");
			}
			else if (answer == 0) { // Incorrect bytes received
				System.out.println("Response bad.");
			}
			
			// Close all streams and the socket
			inputStream.close();
			outputStream.close();
			proj2Client.close();
			System.out.println("Disconnected from server.");
		}
		catch (IOException e){
			System.out.println(e);
		}
	}
}