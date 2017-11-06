
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

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.*;

public class CBIR extends JFrame {

	// container to hold a large photograph
	// JLabal: set area in a JFrame to display short text string or image, or
	// both
	private JLabel photographLabel = new JLabel();

	JCheckBox mainRelevant;
	
	// creates an array of JButtons
	// JButton: Implementation of a "push" button - a button to press in the
	// frame
	private JButton[] button;
	
	// 100 checkboxes 
	private JCheckBox[] imageCheckBox;

	// creates an array to keep up with the image order
	// 100 indexes because there are 100 pictures
	// array of ints
	private int[] buttonOrder = new int[101];
	
	// all checkboxes must match the button/picture it is with 
	// example: check box 87 is the checkbox that affects image 87
	private int[] checkBoxOrder = new int[101];
	

	// keeps up with the image sizes
	//private double[] imageSize = new double[101];	// Original
	private int[] imageSize = new int[101];	// ADDED
	
	// GridLayout: Layout manager that lays out a container's components in a
	// rectangular grid
	// the container is divided into equal-sized rectangles and one component is
	// placed into
	// each rectangle - example: buttons are placed into the rectangles
	private GridLayout gridLayout1;
	private GridLayout gridLayout2;
	private GridLayout gridLayout3;
	private GridLayout gridLayout4;
	private GridLayout gridLayout5;	// after relevant is checked - look at checkbox actionlistener

	// JPanel: Generic lightweight container -- similar to JFrame - where
	// components go
	private JPanel panelBottom1;
	private JPanel panelBottom2;
	private JPanel panelTop;
	private JPanel buttonPanel;

	//private Double[][] intensityMatrix = new Double[101][26]; // original
	//private Double[][] colorCodeMatrix = new Double[100][64]; // original
	
	private double[][] intensityMatrix = new double[101][26]; // ADDED
	private double[][] colorCodeMatrix = new double[101][65]; // ADDED
	
	// Holds the average of all the columns in the intensity and colorCode array
	// Example: finding the average of all the images' bin 1 values
	// index 0 not used 
	// IMPORTANT: index 1 - 25 to represent the intensity bin values
	// IMPORTANT: index 26 - 89 to represent the colorCode bin values
	private double[] average = new double[90];
	
	// calculate the standard deviation of each feature and store into the matrix
	// each value represents the standard deviation for each of the 89 features 
	private double[] standardDeviation = new double[90];
	
	// New matrix is for the Intensity + Color-code button
	// calculateNormalizedMatrix() function fills in the values of the matrix 
	// IMPORTANT: index 1 - 25 to represent the intensity bin values
	// IMPORTANT: index 26 - 89 to represent the colorCode bin values
	private double[][] normalizedMatrix = new double[101][90];
	
	
	
	// FOR RELEVANCE FEEDBACK
	
	// keeps the number of pictures the user has checked as relevant
	int numOfRelevantImages = 0; 
	
	// keeps the normalized values of the relevant images in a matrix
	private double[][] relevantMatrix = new double[101][90];	
	
	// the average of each bin is found and put into this array
	private double[] averageRelevant = new double[90];

	// The standard deviation of the relevant images are stored in this array
	private double[] relevantSD = new double[90];
	
	// the updated weight is calculated which is  1 / standard deviation of each bin (each index in relevantSD)
	private double[] updatedWeight = new double[90];
	
	// normalized weight is calculated which is -> updated weight / sum of all updated weights
	// this new weight will replace the "1/89" weight when inserting values into the normalizedMatrix
	private double[] normalizedWeight = new double[90];
	
	
	
	
	// private Map <Double , LinkedList<Integer>> map; // original
	//private Map<Integer, LinkedList<Integer>> map; // ADDED
	int picNo = 0;
	int imageCount = 1; // keeps up with the number of images displayed since
						// the first page.
	int pageNo = 1;

	// MAIN METHOD
	public static void main(String args[]) {
		new readImage();	//ADDED FOR "EXECUTABLE ASSIGNMENT 2" - MAKES IT EXECUTABLE
		// dont undestand these bottom lines - passing in parameter
		// that is creating a new object that has a method??
		// SwingUtilities is a class that is imported from the JDK
		// just the thing needed to write to pull up the GUI
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				// create new CBIR object - this class is the CBIR class
				// think of the main method as seperate from this class
				CBIR app = new CBIR();

				// JFrames always need to be manually set to true to be visible
				app.setVisible(true);
			}
		});
	}

	public CBIR() {
		// The following lines set up the interface including the layout of the
		// buttons and JPanels.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // makes the close
														// button close the
														// window
		// the title is displayed at the top of the window
		setTitle("Icon Demo: Please Select an Image");
		panelBottom1 = new JPanel();
		panelBottom2 = new JPanel();
		panelTop = new JPanel();
		
		// the part of the window that holds the "next page", "previous page", "intensity", and "Color Code" buttons
		buttonPanel = new JPanel();
		
		// ORIGINAL
		//gridLayout1 = new GridLayout(4, 5, 5, 5);
		//gridLayout2 = new GridLayout(2, 1, 5, 5);
		//gridLayout3 = new GridLayout(1, 2, 5, 5);
		//gridLayout4 = new GridLayout(2, 3, 5, 5);
		
		// ADDED
		// GridLayout(int rows, int columns, int horizontalGap, int verticalGap)
		gridLayout1 = new GridLayout(4, 5, 5, 5);	// for panelBottom1 - bottom half of window
		gridLayout2 = new GridLayout(2, 1, 5, 5);
		gridLayout3 = new GridLayout(1, 2, 5, 5); // panelTop - whole top part - row = 1 is from top of window to half way down
		gridLayout4 = new GridLayout(3, 1, 5, 3); // for top right buttons
		gridLayout5 = new GridLayout(4, 10, 5, 5);	// panelBottom 1 changes layout once relevant checkbox is activated
		
		
		
		setLayout(gridLayout2);
		panelBottom1.setLayout(gridLayout1);
		panelBottom2.setLayout(gridLayout1);
		panelTop.setLayout(gridLayout3);

		// function from extended class - JFrame - adds the panels to the frame
		add(panelTop);	// WHOLE TOP PART
		add(panelBottom1);	// WHOLE BOTTOM PART

		// photographLabel - name of the JLabel
		// shows the picture selected
		photographLabel.setVerticalTextPosition(JLabel.BOTTOM);
		photographLabel.setHorizontalTextPosition(JLabel.CENTER);
		photographLabel.setHorizontalAlignment(JLabel.CENTER);
		photographLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPanel.setLayout(gridLayout4);

		// these two components are added to the "panelTop" panel which is
		// located on the top part of the window
		// NOTE: THESE ARE ADDED TO PANELTOP NOT TO THIS JFRAM - THEY ARE ADDED BOTH TO THE TOP PART OF THE WINDOW
		panelTop.add(photographLabel);
		panelTop.add(buttonPanel);

		// 5 buttons are created
		JButton previousPage = new JButton("Previous Page");
		JButton nextPage = new JButton("Next Page"); 
		JButton intensity = new JButton("Intensity");
		JButton colorCode = new JButton("Color Code");
		JButton intensityPlusColorCode = new JButton("Intensity + Color Code");
		mainRelevant  = new JCheckBox("Relevant"); 	// add the relevant checkbox beside "Intensity + Color Code"
		
		// Changes the 4 button colors from blue to green
		previousPage.setBackground(Color.GREEN);
		nextPage.setBackground(Color.GREEN);
		intensity.setBackground(Color.CYAN);
		colorCode.setBackground(Color.ORANGE); 
		intensityPlusColorCode.setBackground(Color.YELLOW);
		

		// adding the buttons to the panel - so it can be displayed onto the
		// panel
		// buttons NEED to be added to the panel to be displayed
		buttonPanel.add(previousPage);
		buttonPanel.add(nextPage);
		buttonPanel.add(intensity);
		buttonPanel.add(colorCode);
		buttonPanel.add(intensityPlusColorCode);
		buttonPanel.add(mainRelevant);

		// adds action listener to the buttons --- tells the button what to do
		// when pressed (when action is done towards the button)
		nextPage.addActionListener(new nextPageHandler());
		previousPage.addActionListener(new previousPageHandler());
		intensity.addActionListener(new intensityHandler());
		colorCode.addActionListener(new colorCodeHandler());
		intensityPlusColorCode.addActionListener(new intensityColorCodeHandler());
		mainRelevant.addActionListener(new mainRelevantHandler());
		
		setSize(1100, 750);
		// this centers the frame on the screen
		setLocationRelativeTo(null);

		// ADDED - shows me where the directory is so I can add the image file
		// to this directory
		// System.out.println("Working Directory = " +
		// System.getProperty("user.dir"));

		button = new JButton[101];

		imageCheckBox = new JCheckBox[101];
		
		/*
		 * This for loop goes through the images in the database and stores them
		 * as icons and adds the images to JButtons and then to the JButton
		 * array
		 */
		// so when you click on the image it appears as the large image on the
		// top of the window???
		for (int i = 1; i < 101; i++) {
			// System.out.println(getClass().getResource("C:\\\\Users\\\\Bardia\\\\Desktop\\\\Multimedia
			// Data Processing\\\\Assignment 1\\\\images\\\\images\\\\" + i +
			// ".jpg"));

			ImageIcon icon;

			// didnt end up putting the image file on desktop because I
			// couldnt't figure out how to read it soooo.
			// icon = new
			// ImageIcon(getClass().getResource("C://Users//Bardia//Desktop//Multimedia
			// Data Processing//Assignment 1//images//images//" + i + ".jpg"));

			// I just moved the image file to the directory of these java files
			// (C:\Users\Bardia\workspace\490 Assignment 1)
			icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
			
			// decrease the size of the image to fit into the size of the button
			Image imageObj = icon.getImage();
			imageObj = imageObj.getScaledInstance(imageObj.getWidth(null)/2, imageObj.getHeight(null)/2, imageObj.SCALE_SMOOTH);
		    icon.setImage(imageObj); 
			
			if (icon != null) {
				
				button[i] = new JButton(icon);
				imageCheckBox[i] = new JCheckBox("Relevant" /*+ i*/); 				
				
				// load the image again (in it's full size) then pass it through the action listener
				// so when the image is clicked it will display its full size in the top left corner
				// of the window
				ImageIcon originalSize = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
				button[i].addActionListener(new IconButtonHandler(i, originalSize));
				
				imageCheckBox[i].addActionListener(new imageRelevantHandler(i));  
				
				buttonOrder[i] = i;
				checkBoxOrder[i] = i;
				
				// panelBottom1.add(button[i]); // this was commented
				//panelBottom1.add(button[i]); // ADDED - because this is in displayFirstPage()
			}

		}

		// reads the intensity values of each image from intensity.txt and stores them into intensityMatrix matrix
		readIntensityFile();
		
		// reads the color code values of each image from colorCodes.txt and stores them into colorCode matrix
		readColorCodeFile();
		
		// displays the first 20 pictures in the window - this is called two different times - 
		// once when the program is run and once when either the intensity or colorcode buttons are pressed
		displayFirstPage(false);
		
		// calculate relevance feedback
		// Use the Gaussian normalization equation (provided by guidelines)
		// to create a new matrix of normalized feature values to help with relevance feedback
		// Normalized feature value = (feature value - average feature value) / standard deviation
		calculateNormalizedMatrix();
		
		
	}

	// Use the Gaussian normalization equation (provided by guidelines)
	// to create a new matrix of normalized feature values to help with relevance feedback
	// Normalized feature value = (feature value - average feature value) / standard deviation
	// look at excel sheet provided for help with steps that need to be taken
	public void calculateNormalizedMatrix(){
		
		// fills the average array - finds the average of each bin in both the intensity and colorcode matrix
		// average is found by adding up all the images' values for a certain bin and dividing by the
		// number of images (100 images)
		calculateAverage();
		
		// Prints the average array
		//System.out.println(Arrays.toString(average));
		
		// fills the standard deviation array
		calculateStandardDeviation();
		
		// Prints the standard deviation array
	    //System.out.println(Arrays.toString(standardDeviation));
		
		// add the standard deviations of the relevant images (will have different standard deviation)
		// due to user feedback on being relevant
		if(numOfRelevantImages > 0){
			calculateRelevantImages(); 
		}

		
		// need to convert all the bin values first with the image size before doing anything else
		double convertBinValue = 0; 
				
		
		// Use the Gaussian normalization equation to fill in the normalized matrix 
		// Normalized feature value = (feature value - average feature value) / standard deviation
		
		// FILL normalizedMatrix USING EQUATION
		// The normalized matrix created at start of file -> private double[][] normalizedMatrix = new double[101][90];
		for(int binNum = 1; binNum <= 25; binNum++){
			for(int imageNum = 1; imageNum <= 100; imageNum++){
				
				if(standardDeviation[binNum] == 0){
					
					// if a standard deviation is 0 then leave that part in the matrix 0 - we dont want to divide by a 0 - throws a error
					normalizedMatrix[imageNum][binNum] = 0.0;
					
				} else{
					
					// first must convert the value  - find value in matrix and divide by size
					convertBinValue = intensityMatrix[imageNum][binNum] / imageSize[imageNum];
					
					// finally use Gaussian normalization equation then place new normalized value into the normalizedMatrix
					normalizedMatrix[imageNum][binNum] = (convertBinValue - average[binNum]) / standardDeviation[binNum];
	
				}
				
			}
		} 
		
		for(int binNum = 26; binNum <= 89; binNum++){
			for(int imageNum = 1; imageNum <= 100; imageNum++){
				   
				if(standardDeviation[binNum] == 0){
					
					// if a standard deviation is 0 then leave that part in the matrix 0 - we dont want to divide by a 0 - throws a error
					normalizedMatrix[imageNum][binNum] = 0.0;
					
				} else{ 
					
					// first must convert the value  - find value in matrix and divide by size
					convertBinValue = colorCodeMatrix[imageNum][binNum-25] / imageSize[imageNum];
					
					// finally use Gaussian normalization equation then place new normalized value into the normalizedMatrix
					normalizedMatrix[imageNum][binNum] = (convertBinValue - average[binNum]) / standardDeviation[binNum];
					
				}
			} 
		}
		
		
		// PRINT NORMALIZED MATRIX FOR TESTING
		/*
		System.out.println("NORMALIZED MATRIX");
		for (int i = 1; i <= 100; i++) {	
		    for (int j = 1; j <= 89; j++) {
		        System.out.print(normalizedMatrix[i][j] + " ");
		    }
		    System.out.println();
		}
		*/	
	
		
		
	}
	
	
	// fills the average array - finds the average of each bin in both the intensity and colorcode matrix
	// average is found by adding up all the images' values for a certain bin and dividing by the
	// number of images (100 images)
	public void calculateAverage(){
		 
		// used to find average - down below
		double sum = 0; 
		
		// need to convert all the bin values first -> binValue / image size -> then I add to sum -> then into average array
		double convertBinValue = 0; 
		
		// In average array
		// IMPORTANT: index 1 - 25 to represent the intensity bin values
		// IMPORTANT: index 26 - 89 to represent the colorCode bin values
		
		// First fill the first 25 (average intensity bins)
		for(int binNum = 1; binNum <= 25; binNum++){
			for(int imageNum = 1; imageNum <= 100; imageNum++){
				convertBinValue = intensityMatrix[imageNum][binNum] / imageSize[imageNum];
				//System.out.println(convertBinValue);
				sum += convertBinValue;
			}
			
			// use the sum to get the average then insert into the average array
			average[binNum] = sum / 100;
			
			// sum is set to 0 again to find the sum of the next column during next loop 
			sum = 0;
		}
		
		// Then fill the last 64 (average color-code bins)
		for(int binNum = 26; binNum <= 89; binNum++){
			for(int imageNum = 1; imageNum <= 100; imageNum++){
				convertBinValue = colorCodeMatrix[imageNum][binNum-25] / imageSize[imageNum];
				//System.out.println(convertBinValue);
				sum += convertBinValue;
			}
			
			// use the sum to get the average then insert into the average array
			average[binNum] = sum / 100; 

			// sum is set to 0 again to find the sum of the next column during next loop 
			sum = 0; 
		}
	
	}
	
	
	// Use standard deviation formula to find standard deviation 
	// FORMULA (in English): sd = sqrt(  sigma(feature value - average feature value) / (number of images - 1)  )
	// store the standard deviation 
	// The standard deviation created at start of file -> private double[] standardDeviation = new double[90];
	public void calculateStandardDeviation(){
		
		// caluclate the top half of the formula inside the square root
		double summation = 0;
		
		// part of the standard deviation formula -> "feature value - average feature value"
		double valueMinusAverage = 0;
		
		// need to convert all the bin values first with the image size before doing anything else
		double convertBinValue = 0; 
		
		// IMPORTANT: index 1 - 25 to represent the intensity bin values
		// IMPORTANT: index 26 - 89 to represent the colorCode bin values
		for(int binNum = 1; binNum <= 25; binNum++){
			for(int imageNum = 1; imageNum <= 100; imageNum++){ 
				//sd = sqrt(  sigma(FEATURE VALUE - AVERAGE FEATURE VALUE) / (number of images - 1)  )
				convertBinValue = intensityMatrix[imageNum][binNum] / imageSize[imageNum];
				valueMinusAverage = convertBinValue - average[binNum];
				
				// sd = sqrt(  SIGMA(feature value - average feature value) / (number of images - 1) )
				// summation (sigma) is the valueMinusAverage of all the images summed up 
				summation += Math.pow(valueMinusAverage, 2); 
			}
			
			// sd = SQRT(  sigma(feature value - average feature value) / (number of images - 1)  )
			standardDeviation[binNum] = Math.sqrt(summation / 99);
			
			// set back to 0 so it is ready to find the sum of all the images for the next feature - in the next loop
			summation = 0;
		}
		
		// set the variables back to 0 so it is ready for the next batch of features
		valueMinusAverage = 0;
		summation = 0;
		
		for(int binNum = 26; binNum <= 89; binNum++){
			for(int imageNum = 1; imageNum <= 100; imageNum++){
				//sd = sqrt(  sigma(FEATURE VALUE - AVERAGE FEATURE VALUE) / (number of images - 1)  )
				convertBinValue = colorCodeMatrix[imageNum][binNum-25] / imageSize[imageNum];
				valueMinusAverage = convertBinValue - average[binNum];
				
				// sd = sqrt(  SIGMA(feature value - average feature value) / (number of images - 1) )
				// summation (sigma) is the valueMinusAverage of all the images summed up
				summation += Math.pow(valueMinusAverage, 2);
			}
			
			// sd = SQRT(  sigma(feature value - average feature value) / (number of images - 1)  )
			standardDeviation[binNum] = Math.sqrt(summation / 99);
			
			// set back to 0 so it is ready to find the sum of all the images for the next feature - in the next loop
			summation = 0;
		}
		
	}
	
	// Calculates the normalized values and places them int he normalizedWeight matrix
	// the values in the normalizedWeight matrix will replace the original weight
	// of (1/89) when the "intensity + color-code" is calculated
	// Calculates the standard deviation of the bins in the relevant images which is used to find the
	// normalized values
	// The values of the standard deviation will be used with the averageintensityColorCodeHandler
	public void calculateRelevantImages(){ 
				
		// FIRST NEED TO FIND THE AVERAGE OF THE BINS OF THE RELEVANT IMAGES
		// need to use the aveage values to find the standard deviation values
		averageRelevantMatrix();
			
		
		// Use standard deviation formula to find standard deviation 
		// FORMULA (in English): sd = sqrt(  sigma(feature value - average feature value) / (number of images - 1)  )
		
		// store the standard deviation 
		// The standard deviation created at start of file -> private double[] standardDeviation = new double[90];
		
		// caluclate the top half of the formula inside the square root
		double summation = 0;
		
		// part of the standard deviation formula -> "feature value - average feature value"
		double valueMinusAverage = 0;
		
		// Sum of all the standard deviations
		int sumOfUpdatedWeights = 0;
		
		// IMPORTANT: index 1 - 25 to represent the intensity bin values
		// IMPORTANT: index 26 - 89 to represent the colorCode bin values
		for(int binNum = 1; binNum <= 89; binNum++){
			for(int imageNum = 1; imageNum <= 100; imageNum++){ 
				//sd = sqrt(  sigma(FEATURE VALUE - AVERAGE FEATURE VALUE) / (number of images - 1)  )
				if(relevantMatrix[imageNum][binNum] != 0){
					valueMinusAverage = relevantMatrix[imageNum][binNum] - averageRelevant[binNum];
					
					// sd = sqrt(  SIGMA(feature value - average feature value) / (number of images - 1) )
					// summation (sigma) is the valueMinusAverage of all the images summed up 
					summation += Math.pow(valueMinusAverage, 2);  
				}
			}
			
			// sd = SQRT(  sigma(feature value - average feature value) / (number of images - 1)  )
			relevantSD[binNum] = Math.sqrt(summation / (numOfRelevantImages-1) ); 
			if(relevantSD[binNum] != 0){
				updatedWeight[binNum] = 1 / relevantSD[binNum];
			} 
			sumOfUpdatedWeights += updatedWeight[binNum];
			
			// set back to 0 so it is ready to find the sum of all the images for the next feature - in the next loop
			summation = 0;
		}
		
		// calculates the normalized weight in the relevant images
		calculateRelevantWeight(sumOfUpdatedWeights);
		
	}
	
	// the average of each bin in the relevant matrix is found and put into this array
	// the average is used to later find the standard deviation
	public void averageRelevantMatrix(){
		
		// used to find average - down below
		double sum = 0; 
		
		for(int binNum = 1; binNum <= 89; binNum++){
			for(int imageNum = 1; imageNum <= 100; imageNum++){ 
				sum += relevantMatrix[imageNum][binNum];
			}
			
			// use the sum to get the average then insert into the average array
			averageRelevant[binNum] = sum / numOfRelevantImages; 

			// sum is set to 0 again to find the sum of the next column during next loop 
			sum = 0;
		}
		
	}
	
	/*
	 * Called from the calculateRelevantImages() function - this uses the updatedWeight array
	 * and the sum of all of the updated weights - stored in variable sumOfUpdatedWeights
	 * Uses the normalized weight equation -> normalized weight = updated weight / sum of all updated weights
	 */
	public void calculateRelevantWeight(int sumOfUpdatedWeights){
		// private double[] normalizedWeight = new double[90]; // GLOBAL ARRAY DECLARED TOP OF FILE
		for(int binNum = 1; binNum <= 89; binNum++){
			if(updatedWeight[binNum] != 0 && sumOfUpdatedWeights != 0){
				
				// the values in the normalizedWeight matrix will replace the original weight
				// of (1/89) when the "intensity + color-code" is calculated
				normalizedWeight[binNum] = (updatedWeight[binNum]) / sumOfUpdatedWeights;
			}
		}
		
		
		//Print Normalized Weight Array
		//System.out.println(Arrays.toString(normalizedWeight));
		
		
	}
	 
	/*
	 * This method opens the intensity text file containing the intensity matrix
	 * with the histogram bin values for each image. The contents of the matrix
	 * are processed and stored in a two dimensional array called
	 * intensityMatrix.
	 */
	public void readIntensityFile() {
		// System.out.println("Hello");
		StringTokenizer token;
		Scanner read;
		//Double intensityBin; // original
		int intensityBin;	// ADDED
		
		// a line is read in the text file and put into this variable
		// example: line = "13965 8104 4781 3993 3107 2501 2300 2327 3038...."
		String line = "";
		
		//int lineNumber = 0; // original
		int lineNumber = 1; // ADDED
		
		try {
			read = new Scanner(new File("intensity.txt"));
			
			// A line in the intensity.txt (100 lines total for 100 pictures)
			// represents the intensity bins of every image
			while (read.hasNextLine()) {

				// get intensity bins of a picture
				// example: line = "13965 8104 4781 3993 3107 2501 2300 2327 3038...."
				line = read.nextLine();
				
				// TEST to see if each line in intensity.txt is put into
				// variable "line" 
				// System.out.println(line);

				// value of each intensity bin is in string and is split up and put into an array of string
				// this makes the bins easy to access
				String[] imageStringIntensities = line.split(" ");

				for (intensityBin = 1; intensityBin <= 25; intensityBin++) {
					//imageIntIntensities.add(Integer.parseInt(imageStringIntensities[i]));
					
					// convert the bin values from string to int an put them into the matrix
					intensityMatrix[lineNumber][intensityBin] = Integer.parseInt(imageStringIntensities[intensityBin-1]);
					
					// all the bins added up equal the number of pixels of the image which represents the image size
					imageSize[lineNumber] = imageSize[lineNumber] + (Integer.parseInt(imageStringIntensities[intensityBin-1]) );
				}

				// the image we are looking at
				lineNumber++;

			}
			
		} catch (FileNotFoundException EE) {
			// if the txt file isnt found then throw an exception
			System.out.println("The file intensity.txt does not exist");
		}

	}

	/*
	 * This method opens the color code text file containing the color code
	 * matrix with the histogram bin values for each image. The contents of the
	 * matrix are processed and stored in a two dimensional array called
	 * colorCodeMatrix.
	 */
	private void readColorCodeFile() {
		
		//StringTokenizer token;
		Scanner read;
		//Double colorCodeBin;	// original
		int colorCodeBin;	// ADDED
		
		// a line is read in the text file and put into this variable
		// example: line = "33853 214 0 0 5894 1261 8078 1836 0 0 33 ...."
		String line = ""; 
		
		//int lineNumber = 0; // original
		int lineNumber = 1; // ADDED
		
		try {
			// Scanner reads the colorCodes.txt file
			read = new Scanner(new File("colorCodes.txt"));

			// A line in the colorCodes.txt (100 lines total for 100 pictures)
			// represents the colorCode bins of each image
			while (read.hasNextLine()) {

				// get colorCode bins of a picture
				// example: line = "33853 214 0 0 5894 1261 8078 1836 0 0 33...."
				line = read.nextLine();

				// value of each colorCode bin is in string and is split up and put into an array of string
				// this makes the bins easy to access
				String[] imageStringColorCode = line.split(" ");
				
				for (colorCodeBin = 1; colorCodeBin <= 64; colorCodeBin++) {
										
					// convert the bin values from string to int an put them into the matrix
					colorCodeMatrix[lineNumber][colorCodeBin] = Integer.parseInt(imageStringColorCode[colorCodeBin-1]);
					 
				}

				// the image we are looking at
				lineNumber++;  

			}
		} catch (FileNotFoundException EE) {
			// if the txt file isnt found then throw an exception
			System.out.println("The file colorCodes.txt does not exist");
		}

	}

	/*
	 * This method displays the first twenty images in the panelBottom. The for
	 * loop starts at number one and gets the image number stored in the
	 * buttonOrder array and assigns the value to imageButNo. The button
	 * associated with the image is then added to panelBottom1. The for loop
	 * continues this process until twenty images are displayed in the
	 * panelBottom1
	 */
	private void displayFirstPage(boolean showBoxes) {
		
		// makes sure that the first page is only the first 20 pictures that are presented and that
		// the next page button can be pressed another 4 times
		imageCount = 1;	// ADDED
		
		int imageButNo = 0; 
		
		// remove all current pictures displayed in the window
		panelBottom1.removeAll();
		
		// print the first 20 pictures into the window - according to the buttonOrder array
		// the buttonOrder array values match the indexes when the program is first run
		// then when either the intensity or colorCode button is pressed, the buttonOrder changes
		// to put images in order of most similar in terms of intensity or colorCode
		for (int i = 1; i < 21; i++) {
			
			//imageButNo = buttonOrder[i];
			//panelBottom1.add(button[imageButNo]);
			//imageCount++;
			//checkbox[i] = new JCheckBox("Relevant");
			//panelBottom1.add(checkbox[i]);
			 
			imageButNo = buttonOrder[i]; 
			panelBottom1.add(button[imageButNo]);
			
			// if the mainRelevatn checkbox is set, display checkboxes next to all the images
			// Checkboxes associated with an image are located to the right side of each image
			if(showBoxes){
				panelBottom1.add(imageCheckBox[imageButNo]);
			}
			imageCount++;
			
		}
		panelBottom1.revalidate();
		panelBottom1.repaint();

	}

	/*
	 * This class implements an ActionListener for each iconButton. When an icon
	 * button is clicked, the image on the the button is added to the
	 * photographLabel and the picNo is set to the image number selected and
	 * being displayed.
	 */
	private class IconButtonHandler implements ActionListener {
		int pNo = 0;
		ImageIcon iconUsed;

		IconButtonHandler(int i, ImageIcon j) {
			pNo = i;
			iconUsed = j; // sets the icon to the one used in the button
		}

		public void actionPerformed(ActionEvent e) {
			photographLabel.setIcon(iconUsed);
			picNo = pNo;
		}

	}

	/*
	 * This class implements an ActionListener for the nextPageButton. The last
	 * image number to be displayed is set to the current image count plus 20.
	 * If the endImage number equals 101, then the next page button does not
	 * display any new images because there are only 100 images to be displayed.
	 * The first picture on the next page is the image located in the
	 * buttonOrder array at the imageCount
	 */
	private class nextPageHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int imageButNo = 0;
			// prints the next 20 pictures in the button array according to the value in the buttonOrder array
			int endImage = imageCount + 20;
			if (endImage <= 101) {
				panelBottom1.removeAll();
				for (int i = imageCount; i < endImage; i++) {
					// prints according to value in buttonOrder array - this says which picture to add
					imageButNo = buttonOrder[i];
					
					// button is added to panel - can now be seen
					panelBottom1.add(button[imageButNo]);
					
					// add checkboxes if the relevant checkbox is checked
					// if the mainRelevatn checkbox is set, display checkboxes next to all the images
					// Checkboxes associated with an image are located to the right side of each image
					if(mainRelevant.isSelected()){
						panelBottom1.add(imageCheckBox[imageButNo]);
					}
					
					imageCount++;

				}

				panelBottom1.revalidate();
				panelBottom1.repaint();
			}
		}

	}

	/*
	 * This class implements an ActionListener for the previousPageButton. The
	 * last image number to be displayed is set to the current image count minus
	 * 40. If the endImage number is less than 1, then the previous page button
	 * does not display any new images because the starting image is 1. The
	 * first picture on the next page is the image located in the buttonOrder
	 * array at the imageCount
	 */
	private class previousPageHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			int imageButNo = 0;

			// because the panelBottom1 shows 20 pictures at a time
			int startImage = imageCount - 40;
			int endImage = imageCount - 20;

			if (startImage >= 1) {

				// remove all the current images shown in the panel
				panelBottom1.removeAll();

				/*
				 * The for loop goes through the buttonOrder array starting with
				 * the startImage value and retrieves the image at that place
				 * and then adds the button to the panelBottom1.
				 */
				for (int i = startImage; i < endImage; i++) { 
					imageButNo = buttonOrder[i];
					panelBottom1.add(button[imageButNo]);
					
					// add checkboxes if the relevant checkbox is checked
					// if the mainRelevatn checkbox is set, display checkboxes next to all the images
					// Checkboxes associated with an image are located to the right side of each image
					if(mainRelevant.isSelected()){
						panelBottom1.add(imageCheckBox[imageButNo]);
					}
					 
					imageCount--;

				}

				panelBottom1.revalidate();
				panelBottom1.repaint();
			}
		}

	}

	/*
	 * This class implements an ActionListener when the user selects the
	 * intensityHandler button. The image number that the user would like to
	 * find similar images for is stored in the variable pic. pic takes the
	 * image number associated with the image selected and subtracts one to
	 * account for the fact that the intensityMatrix starts with zero and not
	 * one. The size of the image is retrieved from the imageSize array. The
	 * selected image's intensity bin values are compared to all the other
	 * image's intensity bin values and a score is determined for how well the
	 * images compare. The images are then arranged from most similar to the
	 * least.
	 */
	private class intensityHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			// distance between this "pic" (variable below) and every other
			// picture - the smaller the distance the more intensity matches
			double[] distance = new double[101];	// Original
		    // int [] distance = new int[101];	// ADDED

		    // the distance between this "pic" and a picture we are comparing it to
			// double d = 0; // Original
			double d = 0; // ADDED

			// the picture that we want compared with other pictures
			int pic = (picNo - 1);
			
			int picSize = imageSize[pic+1]; 
			
			// pic is 0 when click on first picture
			// System.out.println(pic);
			
			// loops through all the images - to be able to compare the intensity of thisPic to other pictures
			for(int imgCount = 0; imgCount < 100; imgCount++){
						
					// Manhattan distance set to 0 again for calucating the manhattan distance of another picture
					d = 0;
					
					for(int bins = 1; bins <= 25; bins++){
						// Manhattan distance gives us images that are most similar to this picture in terms
						// of intensity
						d += Math.abs( (intensityMatrix[pic+1][bins] / picSize) - (intensityMatrix[imgCount+1][bins]/ imageSize[imgCount+1]) );
					}
					
					// the value is placed into the distance array where all the manhatten distances are stored
					// to later be compared to when displaying the images in the window
					distance[imgCount+1] = d;
					 
					//System.out.println("Image Number " + imgCount + " = " + d);
					//System.out.println(d);
					
			}
			
			// another double array is created that will store the same values as distance[] but sorted
			double [] sortedDistance = new double[101];
			
			// make a duplicate array - so sortedDistance array holds exact 
			// values that distance array holds
			System.arraycopy( distance, 0, sortedDistance, 0, distance.length );

			// sort the sortedDistance array so the first index represents the image with the closest
			// intensity match to the image we are comparing all the other images to
			Arrays.sort(sortedDistance); 
			
			// EXAMPLE (realistically they are in double and smaller numbers: 
			// distance[] = [10][79][50]
			// sortedDistance[] = [2][4][10][19][50][79][100]
			// buttonOrder[] = [6][7][1][4][3][2][5]
			for(int order = 1; order <= 100; order++){
				
				int index = 1;
				
				// find the index that holds the value being looked at by "sortedDistance[order]"
				while(sortedDistance[order] != distance[index]){
					index++;
				}
				
				// buttonOrder is what is used to print buttons to the window so the buttonOrder array
				// needs to be reordered in terms of intensity
				buttonOrder[order] = index; 
			}
			 
			// the first page needs to be re-displayed to show the correct order after "intensity" button is pressed
			// if the mainRelevatn checkbox is set, display checkboxes next to all the images
			// Checkboxes associated with an image are located to the right side of each image
			if(mainRelevant.isSelected()){	
				displayFirstPage(true);
			} else{
				displayFirstPage(false);
			}

		}

	}

	/*
	 * This class implements an ActionListener when the user selects the
	 * colorCode button. The image number that the user would like to find
	 * similar images for is stored in the variable pic. pic takes the image
	 * number associated with the image selected and subtracts one to account
	 * for the fact that the intensityMatrix starts with zero and not one. The
	 * size of the image is retrieved from the imageSize array. The selected
	 * image's intensity bin values are compared to all the other image's
	 * intensity bin values and a score is determined for how well the images
	 * compare. The images are then arranged from most similar to the least.
	 */
	private class colorCodeHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			// distance between this "pic" (variable below) and every other
			// picture - the smaller the distance the more colorCode matches
			double[] distance = new double[101];	// Original
		    // int [] distance = new int[101];	// ADDED

		    // the distance between this "pic" and a picture we are comparing it to
			// double d = 0; // Original
			double d = 0; // ADDED

			// the picture that we want compared with other pictures
			int pic = (picNo - 1);
			
			int picSize = imageSize[pic+1]; 
			
			// pic is 0 when click on first picture
			// System.out.println(pic);
			
			// EXAMPLE (realistically they are in double and smaller numbers: 
			// distance[] = [10][79][50]
			// sortedDistance[] = [2][4][10][19][50][79][100]
			// buttonOrder[] = [6][7][1][4][3][2][5]
			// loops through all the images - to be able to compare the intensity of thisPic to other pictures
			for(int imgCount = 0; imgCount < 100; imgCount++){
						
					// Manhattan distance set to 0 again for calucating the manhattan distance of another picture
					d = 0; 
					 
					for(int bins = 1; bins <= 64; bins++){
						// Manhattan distance gives us images that are most similar to this picture in terms
						// of colorCode
						d += Math.abs( (colorCodeMatrix[pic+1][bins] / picSize) - (colorCodeMatrix[imgCount+1][bins]/ imageSize[imgCount+1]) );
					}
					
					distance[imgCount+1] = d;
					
					//System.out.println("Image Number " + imgCount + " = " + d);
					// System.out.println(d);
					
			}
			
			double [] sortedColorCode = new double[101];
			
			// make a duplicate array - so sortedDistance array holds exact  
			// values that distance array holds
			System.arraycopy( distance, 0, sortedColorCode, 0, distance.length );

			// sort the sortedDistance array so the first index represents the image with the closest
			// intensity match to the image we are comparing all the other images to
			Arrays.sort(sortedColorCode); 
			
			for(int order = 1; order <= 100; order++){ 
				
				int index = 1;
				
				// find the index that holds the value being looked at by "sortedDistance[order]"
				while(sortedColorCode[order] != distance[index]){
					index++;
				}
				
				// buttonOrder is what is used to print buttons to the window so the buttonOrder array
				// needs to be reordered in terms of colorCode
				buttonOrder[order] = index; 
				// System.out.println(index);
			}
			
			// the first page needs to be re-displayed to show the correct order after "intensity" button is pressed
			if(mainRelevant.isSelected()){	
				displayFirstPage(true);
			} else{
				displayFirstPage(false);
			}
			
		}
	}
	
	
	/*
	 * This class implements an ActionListener when the user selects the
	 * Intensity + ColorCode button. The image number that the user would like to find
	 * similar images for is stored in the variable pic. pic takes the image
	 * number associated with the image selected and subtracts one to account
	 * for the fact that the normalizedMatrix starts with zero and not one. The
	 * size of the image is retrieved from the imageSize array. The selected
	 * image's normalizedMatrix bin values are compared to all the other image's
	 * normalized bin values and a score is determined for how well the images
	 * compare. The images are then arranged from most similar to the least.
	 */
	private class intensityColorCodeHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			
			// Use the Gaussian normalization equation (provided by guidelines)
			// to create a new matrix of normalized feature values to help with relevance feedback
			// Normalized feature value = (feature value - average feature value) / standard deviation
			calculateNormalizedMatrix();
		
			
			// distance between this "pic" (variable below) and every other
			// picture - the smaller the distance the more intensity+colorcode matches
			double[] distance = new double[101];	// Original
		    // int [] distance = new int[101];	// ADDED

		    // the distance between this "pic" and a picture we are comparing it to
			// double d = 0; // Original
			double d = 0; // ADDED

			// the picture that we want compared with other pictures
			int pic = (picNo - 1);
			
			int picSize = imageSize[pic+1]; 
			
			// pic is 0 when click on first picture
			// System.out.println(pic);
			
			// EXAMPLE (realistically they are in double and smaller numbers: 
			// distance[] = [10][79][50]
			// sortedDistance[] = [2][4][10][19][50][79][100]
			// buttonOrder[] = [6][7][1][4][3][2][5]
			// loops through all the images - to be able to compare the intensity of thisPic to other pictures
			for(int imgCount = 0; imgCount < 100; imgCount++){
						
					// Manhattan distance set to 0 again for calucating the manhattan distance of another picture
					d = 0; 
					
					for(int bins = 1; bins <= 89; bins++){
						// Manhattan distance gives us images that are most similar to this picture in terms
						// of colorCode
						// The weight is 1/N where N is the number of features - there are 89 features
						//d +=  ( (1.0/89.0)* (Math.abs( normalizedMatrix[pic+1][bins] - normalizedMatrix[imgCount+1][bins])) );
						//System.out.println(normalizedMatrix[pic+1][bins]);
						
						if(normalizedWeight[bins] != 0){ 
							// Need to divide normalizedWeight[bins] by 89????
							d +=  ( (normalizedWeight[bins])* (Math.abs( normalizedMatrix[pic+1][bins] - normalizedMatrix[imgCount+1][bins])) );
						} else{
							d +=  ( (1.0/89.0)* (Math.abs( normalizedMatrix[pic+1][bins] - normalizedMatrix[imgCount+1][bins])) );
						}
					}
					  
					distance[imgCount+1] = d;
					
					//System.out.println("Image Number " + imgCount + " = " + d);
					// System.out.println(d);
					
			}
			
			double [] sortedIntensityColorCode = new double[101];
			
			// make a duplicate array - so sortedDistance array holds exact  
			// values that distance array holds
			System.arraycopy( distance, 0, sortedIntensityColorCode, 0, distance.length );			
			
			// sort the sortedDistance array so the first index represents the image with the closest
			// intensity match to the image we are comparing all the other images to
			Arrays.sort(sortedIntensityColorCode); 
		
			// reorder the images so that they are displayed in the order we want them to be
			for(int order = 1; order <= 100; order++){ 
				
				int index = 1; 
				
				// find the index that holds the value being looked at by "sortedDistance[order]"
				while(sortedIntensityColorCode[order] != distance[index]){
					index++;
				}
				
				// buttonOrder is what is used to print buttons to the window so the buttonOrder array
				// needs to be reordered in terms of colorCode
				buttonOrder[order] = index; 
				// System.out.println(index);
			}
			
			// the first page needs to be re-displayed to show the correct order after "intensity" button is pressed
			if(mainRelevant.isSelected()){ 	
				displayFirstPage(true);
			} else{
				displayFirstPage(false);
			}
			
		}
		
	}
	

	
	/*
	 * Action listener - this is called when the "relevant" checkbox is checked or unchecked
	 * changes the size of the images to fir the new sized buttons
	 */
	private class mainRelevantHandler implements ActionListener{
		
		public void actionPerformed(ActionEvent e) { 
			
			if(mainRelevant.isSelected()){
				
				// decreased the image so it fits in the button size when it downsizess
				for(int i = 1; i <= 100; i++){

					// get the image from the folder
					ImageIcon icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
					
					// convert it into an Image object
					Image imageObj = icon.getImage();
					
					// it is then decreased in size to fit the button when there ARE checkboxes displayed
					imageObj = imageObj.getScaledInstance(imageObj.getWidth(null)/4, imageObj.getHeight(null)/4, imageObj.SCALE_SMOOTH);
				    icon.setImage(imageObj); 
				     
				    // new object for the button is created to place this different image size to it
			 	    button[i] = new JButton(icon);
				    
				    // now get the image again (at original size) and dont change size this time as we
				    // want to pass it to the IconButtonHandler which displays each image into the 
				    // top left corner of the window when an image is selected
				    ImageIcon originalSize = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
				    button[i].addActionListener(new IconButtonHandler(i, originalSize));
				}
				
				// give the bottom panel (bottom half of screen) 10 columns - leaving space for checkboxes
				panelBottom1.setLayout(gridLayout5);
				
				// call currentPageHandler class to redisplay the current images with the checkboxes
				currentPageHandler curHandlerObj = new currentPageHandler();
				curHandlerObj.actionPerformed(e);	
				
				
			} else{
				
				// enlargen the image so it fits in the button size when it upsizes
				for(int i = 1; i <= 100; i++){
					
					// get the image from the folder
					ImageIcon icon = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
					
					// convert it into an Image object
					Image imageObj = icon.getImage();
					
					// it is then decreased in size to fit the button when there are NO checkboxes displayed
					imageObj = imageObj.getScaledInstance(imageObj.getWidth(null)/2, imageObj.getHeight(null)/2, imageObj.SCALE_SMOOTH);
				    icon.setImage(imageObj); 
				    
				    // new object for the button is created to place this different image size to it
				    button[i] = new JButton(icon);
				    
				    // now get the image again (at original size) and dont change size this time as we
				    // want to pass it to the IconButtonHandler which displays each image into the 
				    // top left corner of the window when an image is selected
				    ImageIcon originalSize = new ImageIcon(getClass().getResource("images/" + i + ".jpg"));
				    button[i].addActionListener(new IconButtonHandler(i, originalSize));
				}
				
				// go back to original layout - 5 columns leaving space for 5 pictures horizontally
				panelBottom1.setLayout(gridLayout1);
				//System.out.println("set off");
				// call currentPageHandler class to redisplay the current images with the checkboxes
				currentPageHandler curHandlerObj2 = new currentPageHandler();
				curHandlerObj2.actionPerformed(e);
				
				
				
			}
			
			
			
		}
	}
	
	
	/*
	 * Handler for all of the checkboxes beside all of the images - when a checkbox is set
	 * it will call this class and pass in an int (passed into the 1 parameter constructor)
	 * to add the images' normalized values into the relevantMatrix to then calculate the new weight
	 * for each bin - RELEVANCE FEEDBACK
	 */
	private class imageRelevantHandler implements ActionListener{
		
		int imageNum = 0;
		
		// constructor with 1 parameter - used to get the information about which image's checkbox was set
		imageRelevantHandler(int i){ 
			imageNum = i;
		}
		
		public void actionPerformed(ActionEvent e) { 
			
			if(imageCheckBox[imageNum].isSelected()){ // IF CHECKBOX IS SELECTED 
				
				numOfRelevantImages++;
				// System.out.println(numOfRelevantImages);
				
				// imageCheckBox[imageNum] where "imageNum" is the number of the image
				// look up the image in the normalized matrix and put into relevantMatrix[][]
				for(int relevantBins = 1; relevantBins <= 89; relevantBins++){
					relevantMatrix[imageNum][relevantBins] = normalizedMatrix[imageNum][relevantBins];
				}
				
				//System.out.println("checked " + imageNum);
				//System.out.println(Arrays.toString(relevantMatrix[imageNum]));
				//System.out.println(Arrays.toString(normalizedMatrix[imageNum]));
				
				
			} else{	// IF CHECKBOX IS UNSELECTED 
				
				numOfRelevantImages--; 
				// System.out.println(numOfRelevantImages);
				
				//System.out.println("UNchecked " + imageNum);
				for(int relevantBins = 1; relevantBins <= 89; relevantBins++){
					relevantMatrix[imageNum][relevantBins] = 0.0; 
				}
				
				//System.out.println("UNchecked " + imageNum);
				//System.out.println(Arrays.toString(relevantMatrix[imageNum]));
				//System.out.println(Arrays.toString(normalizedMatrix[imageNum]));
				
				
			}
			
		}
	}
	
	
	/*
	 * This class is called for when the "relevant" checkbox is checked or unchecked 
	 * This will is a way "refresh" the page and show all the images that currently shown
	 * but this time alongside checkboxes (or otherway around if the checkbox is being unchecked)
	 */ 
	private class currentPageHandler implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			int imageButNo = 0;

			// because the panelBottom1 shows 20 pictures at a time
			// imageCount is on the last page that is displayed on the page
			// display the current page's 20 images again
			int startImage = imageCount - 20;
			int endImage = imageCount;

			if (startImage >= 1) { 

				// remove all the current images shown in the panel
				panelBottom1.removeAll();

				/*
				 * The for loop goes through the buttonOrder array starting with
				 * the startImage value and retrieves the image at that place
				 * and then adds the button to the panelBottom1.
				 */
				for (int i = startImage; i < endImage; i++) { 
					imageButNo = buttonOrder[i];
					panelBottom1.add(button[imageButNo]);
					
					// add checkboxes if the relevant checkbox is checked
					// this is the reason for this whole function - to remove or add the checkboxes
					// beside each image - they are removed or added depending on if the relevant checkbox
					// has be checked (selected) or unchecked
					if(mainRelevant.isSelected()){
						panelBottom1.add(imageCheckBox[imageButNo]);
					}
					 
				}
 
				panelBottom1.revalidate();
				panelBottom1.repaint(); 
			}
		}

	}
	
	
}
