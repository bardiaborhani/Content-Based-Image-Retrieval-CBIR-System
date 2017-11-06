/* 
  Project 2
  Bardia Borhani 
  4/23/17
  Last day of modification: 4/29/17
  
  Description: Implement a simple Content-Based Image Retrieval system (CBIR)
  based on two different histogram comparison methods - Intensity and Color Code
  WITH INTENSITY + COLOR-CODE AND RELEVANCE FEEDBACK OPTION
  * Intensity of pixels are compared using the intensity method 
  * I = 0.299R + 0.587G +0.114B 
  * This project consists of two class - readImage.java and CBIR.java
  
  Assumptions:
  Test image database includes 100 true-color images in .jpg format
*/ 

import java.awt.image.BufferedImage;
import java.lang.Object.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

// I imported
import java.awt.Color;
import java.io.PrintWriter; 

public class readImage
{
  int imageCount = 1;
  
  // This variable is not used - intensities are just stored straight into the intensityMatrix
  // holds intensity for a single picture
  // each bin holds 0-10  
  //int intensityBins [] = new int [26];
  //double intensityBins [] = new double [26];
  
  // holds intensity for all images 
  int intensityMatrix [][] = new int[101][26]; // ADDED 
  //double intensityMatrix [][] = new double[100][26];	//originally here
  
  
  // This variable is not used - colorCode are just stored straight into the colorCodeMatrix
  // holds the color code for a single image
  //double colorCodeBins [] = new double [64];
  
  // holds colorCode values of each pixel in every image (100 images)
  int colorCodeMatrix [][] = new int[101][65];	// ADDED 
  // double colorCodeMatrix [][] = new double[100][64];	//originally here


  /*Each image is retrieved from the file.  The height and width are found for the image and the getIntensity and
   * getColorCode methods are called.
  */
  public readImage() 
  {
	
    while(imageCount < 101){
      try	// statements that may cause a exception
      {
   
    	//BufferedImage img = ImageIO.read(new File("C:/Users/Bardia/Desktop/Multimedia Data Processing/Assignment 1/images/images/" + imageCount + ".jpg"));
    	//BufferedImage img = ImageIO.read(new File("C:/Users/Bardia/Desktop/Multimedia Data Processing/Assignment 1/images/images/1.jpg"));
     
    	// System.out.println("Working Directory = " + System.getProperty("user.dir"));  
    	  
    	// the line that reads the image file
    	BufferedImage img = ImageIO.read(new File("images/" + imageCount + ".jpg"));
     
    	  
    	
        //File file = new File("images/" + imageCount + ".jpg"); // I have bear.jpg in my working directory  
	   // FileInputStream fis = new FileInputStream(file);  
	    //BufferedImage img = ImageIO.read(fis); //reading the image file    
    	  
    	  
    	// get width of image
    	int width = img.getWidth(); 
    	
    	// get height of image
        int height = img.getHeight();
        
        // These AREN"T ACCESSORS - they calculate the intensity and color code, respectively, and put
        // the values into the matrixes (look at this class' fields)
        // the getInternsity and getColorCode methods aren't actually accessors (like they sound like)
        // because they return void - they are used to fill in the matrix arrays
    	getIntensity(img, height, width); 
    	getColorCode(img, height, width);
    	
    	// look at next image for next loop
    	imageCount++; 
    	
    	 
      } // IOException can be thrown when reading a local file that is no longer available. 
      catch (IOException e)	// indicated by failed input/output operations -- typically object is named "e" for exception types
      {
        System.out.println("Error occurred when reading the file.");
      }
    }
    
    // goes through the values in the intensityMatrix matrix and stores them in the txt file
    writeIntensity();
    
    // goes through the values in the colorCodeMatrix matrix and stores them in the txt file
    writeColorCode();
    
  }
  

  /* intensity method 
   * All the pixels of the passed image are read and the red, green, and blue values of each pixel is retrived
   * Using the intensity method (I = 0.299R + 0.587G + 0.114B ) the intensity of every pixel is found
   * and the value is between 0 - 255. The bin which contains value that matches the intensity is incremented by one
   * so that every bin holds a number representing the number of pixels whose intensity is within the proper range
   * Pre-condition: a BufferedImage object and two int variables need to passed in (representing width and height of image)
   * Post-condition: The values of the bins are stored in the intensityMatrix matrix
   */
  public void getIntensity(BufferedImage image, int height, int width){

	// When reading each pixel, these variables will hold the values of the RGB of each pixel
	int redValue;
	int greenValue;
	int blueValue;

	// Will hold the intensity value of each pixel - intensity is calculated through an equation
	double intensity;
	
	int[] pixelArray = image.getRGB(0, 0, width, height, null, 0, width); 
	
	for(int i = 0; i < pixelArray.length; i++){
		
		// grabs a pixel from the pixelArray
		Color pixel = new Color(pixelArray[i]);
		
		// values returned will be between 0 - 255
		redValue = pixel.getRed();
		greenValue = pixel.getGreen();
		blueValue = pixel.getBlue();
		
		// Intensity equation (as stated on the guidelines)
		// Intensity value will also be between 0 - 255 
		// I = 0.299R + 0.587G + 0.114B 
		intensity = (0.299 * redValue) + (0.587 * greenValue) + (0.114 * blueValue);
		
		// converting intensity variable from double to int so I can see where to place 
		// the intensity value in the array
		int intValue = (int) intensity;
		
		// finding which bin to put the intensity value in
		intValue = (intValue / 10) + 1;
		//System.out.print(intensityBins[intValue]);
		
		// the last bin is special - it contains 15 values - from 240 to 255
		if(intValue == 26){
			
			//intensityBins[intValue - 1] += 1;
			
			// put intensity of picture in the matrix that holds the intensity values of all the pictures
			intensityMatrix[imageCount][intValue - 1] += 1; 
			
		} else{
			
			// increment the values by 1 so that each bin contains a value that represents the number of pixels
			// that have intensities within the bin range
			//intensityBins[intValue] += 1;    
	
			// put intensity of picture in the matrix that holds the intensity values of all the pictures
			intensityMatrix[imageCount][intValue] += 1;
		}
	}
	
  }
  
 
  /* color code method
   * All the pixels of the passed image are read and the red, green, and blue values of each pixel is retrived
   * The number of the colored values are turned into a binary form that is in a string form - 0s are added to the numbers
   * so all colored values have 8 digits - then the two left-most numbers of each value is added together 
   * (in order of red, green, blue) to get the colorCode - the bin that matches the colorCode is found in the 
   * colorCodeMatrix matrix and the value in it incremented by 1
   * Pre-condition: a BufferedImage object and two int variables need to passed in (representing width and height of image)
   * Post-condition: The values of the bins are stored in the intensityMatrix matrix
   */
  public void getColorCode(BufferedImage image, int height, int width){
	
	int[] pixelArray = image.getRGB(0, 0, width, height, null, 0, width); 
	
	for(int i = 0; i < pixelArray.length; i++){
		
		// grabs a pixel from the pixelArray
		Color pixel = new Color(pixelArray[i]);
		
		// values returned will be between 0 - 255
		// the RGB value of each pixel is read and stored into the respective int variables
		int redValue = pixel.getRed();
		int greenValue = pixel.getGreen(); 
		int blueValue = pixel.getBlue();
		
		// the int variables will be converted to their binary form
		// example: if red is 128 - its binary form would be 10000000
		String redValueString = Integer.toBinaryString(redValue);
		String greenValueString = Integer.toBinaryString(greenValue);
		String blueValueString = Integer.toBinaryString(blueValue);
		
		int zerosToAdd = 0;
		
		// make sure every value is 8 bits long - if not add 0s to the left of them
		if(redValueString.length() < 8){
			// determines how many 0s to add to the left of the value so 
			// the value will end up haivng 8 digits
			zerosToAdd = 8 - redValueString.length();
			while(zerosToAdd != 0){
				redValueString = "0" + redValueString;
				zerosToAdd--;
			}
		}
		if(greenValueString.length() < 8){
			// determines how many 0s to add to the left of the value so 
			// the value will end up haivng 8 digits
			zerosToAdd = 8 - greenValueString.length();
			while(zerosToAdd != 0){
				greenValueString = "0" + greenValueString;
				zerosToAdd--;
			}
		}
		if(blueValueString.length() < 8){
			// determines how many 0s to add to the left of the value so 
			// the value will end up haivng 8 digits
			zerosToAdd = 8 - blueValueString.length();
			while(zerosToAdd != 0){
				blueValueString = "0" + blueValueString;
				zerosToAdd--;
			}
		}
		
		// the first two numbers of each of the above strings will be put together - in order from red, green, then blue
		// this is how the colorCode is calculated
		String colorCodeString = redValueString.substring(0, 2) + greenValueString.substring(0, 2) + blueValueString.substring(0, 2);
		
		// converting from String to int
		int colorCode = Integer.parseInt(colorCodeString);
		
		
		// convert colorCode from binary to decimal (normal)
		int exponent = 0;
		int colorCodeBin = 0;
		
		while(true){
			
			if(colorCode == 0){
				
				// break once we have finished converting the colorCode from binary to decimal
				break;
				
			} else{
				 
			    // get the right-most number in the colorCode (should be 0 or 1)
			    int binaryNumber = colorCode %  10;
			    
			    // get the value of the binaryNumber
			    // increase exponent for next loop
			    colorCodeBin += binaryNumber * Math.pow(2, exponent);
			    
			    // increase exponent after each loop so that every position of a number in the binary number matches
			    // its base 2 value
			    exponent += 1;
			    
			    // devide colorCode by 10 so at the next loop we can look at the next right-most number
			    colorCode = colorCode / 10;
			   
			}
			
		}
		
		// the value in the colorCode bin for the image is increased by 1 to now represent so far how many
		// pixels have the same colorCode given ranges by the bins
		colorCodeMatrix[imageCount][colorCodeBin + 1] += 1;
		
	} 
	
  } 
   
  
  
  //This method writes the contents of the colorCode matrix to a file named colorCodes.txt.
  public void writeColorCode(){ 
	  
	  try{
		  
		  	// makes it able to print something to the text
			PrintWriter pw = new PrintWriter("colorCodes.txt");  
			
			// i = image 
			// j = colorCode bin for a image
			for(int i = 1; i <= 100; i++){ 
				for(int j = 1; j <= 64; j++){
				      // the values of the colorCodeMatrix are put into the txt file
					  // the values of the bins are written separated by whitespace
					  pw.print(colorCodeMatrix[i][j] + " "); 
				} 
				pw.println(); 
			}
			
		    pw.close();
		    
		} catch(Exception e){
			
			// throw error message if files is not found
			System.out.println("colorCodes.txt file doesn't exists.");
			
		}
		
  }
  
  //This method writes the contents of the intensity matrix to a file called intensity.txt
  public void writeIntensity(){
	  
	try{
		
		// makes it able to print something to the text
		PrintWriter pw = new PrintWriter("intensity.txt");  
		
		// i = image
		// j = intensity bin for a image
		for(int i = 1; i <= 100; i++){ 
			for(int j = 1; j <= 25; j++){
				  // the values of the intensityMatrix are put into the txt file
				  // the values of the bins are written separated by whitespace
				  pw.print(intensityMatrix[i][j] + " "); 
			} 
			pw.println();
		}
		
	    pw.close();
	    
	} catch(Exception e){
		
		// throw error message if files is not found
		System.out.println("intensity.txt file doesn't exists.");
		
	}
	
  }
  
  public static void main(String[] args)
  {
    new readImage();
  }

}
