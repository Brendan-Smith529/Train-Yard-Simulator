/*
 *  Name:Brendan Smith
 *  Course: CNT 4714 Fall 2023
 *  Assignment title: Projet 2 - Multi-threaded programming in Java
 *  Date: October 8, 2023
 *
 *  Class: TrainMovementSimulator.Java
 *
 *  Description: Initializes the trains, switches, and the yard. After doing so,
 *               it then creates and executes the trains as threads and waits until
 *               all the threads have been executed to finish
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class TrainMovementSimulator
{
  private static final int MAXTRAINS = 30;
  private static final int MAXALIGNMENTS = 60;
  private static final int MAXSWITCHES = 10;

  private static int numTrains, numAlignments, numSwitches;

  private static Train [] fleet;
  private static int [][] yard;

  /* Display Functions */
  private static void trainDetails()
  {
    System.out.println("Details of the Train Fleet being simulated in this run\n");
    System.out.println("Train Number            Inbound Track           Outbound Track          Hold");
    System.out.println("-----------------------------------------------------------------------------");

    for (int i = 0; i < numTrains; ++i)
      System.out.println(fleet[i].getTrainNum() + "\t\t\t" + fleet[i].getInbound() + "\t\t\t" + fleet[i].getOutbound() + "\t\t\t" + fleet[i].getHold());
  }

  private static void yardDetails()
  {
    System.out.println("\n\nDetails of the Track/Switch Alignments being simulated in this run\n");
    System.out.println("Inbound Track\t\tSwitch1\t\t\tSwitch2\t\t\tSwitch3\t\t\tOutbound Track");
    System.out.println("--------------------------------------------------------------------------------------------------------------");

    for (int i = 0; i < numAlignments; ++i)
      System.out.println(yard[i][0] + "\t\t\t" + yard[i][1] + "\t\t\t" + yard[i][2] + "\t\t\t" + yard[i][3] + "\t\t\t" + yard[i][4]);
  }

  /* Initializations */
  private static int getFleetInfo() throws IOException
  {
    fleet = new Train[MAXTRAINS];
    String [] getNums = new String[3];
    int trainNum = 0;

    Scanner in = new Scanner(new File("theFleetFile.csv"));

    // Adds train info from file
    while (in.hasNextLine())
    {
      // Temporarily stores train info
      getNums = in.nextLine().split(",");

      // Initializes the train
      fleet[trainNum++] = new Train(getNums);
    }

    return trainNum;
  }

  private static int getYardInfo() throws IOException
  {
    yard = new int[MAXALIGNMENTS][5];
    String [] getNums = new String[5];
    int yardNum = 0;

    Scanner in = new Scanner(new File("theYardFile.csv"));

    // Adds yard info from file
    while (in.hasNextLine())
    {
      // Temporarily stores yard info
      getNums = in.nextLine().split(",");

      // Initialize the switch alignments
      for (int i = 0; i < 5; ++i)
        yard[yardNum][i] = Integer.parseInt(getNums[i]);

      ++yardNum;
    }

    return yardNum;
  }

  private static int getNumSwitches()
  {
    int max = -1;

    for (int i = 0; i < yard.length; ++i)
    {
      // Stop after reading through all of the alignments
      if (yard[i][0] == 0)
        return max;

      for (int j = 1; j < 4; ++j)
      {
        if (yard[i][j] > max)
          max = yard[i][j];
      }
    }

    return max;
  }

  private static Switch[] initializeSwitchArray()
  {
    Switch[] switches = new Switch[numSwitches];

    // Initialize the array
    for (int i = 0; i < numSwitches; ++i)
      switches[i] = new Switch(i + 1);

    return switches;
  }

  private static void assignSwitches(Switch [] switches)
  {
    // Variable to check if the train is able to move
    boolean path = false;

    // Goes through alignments and sets the switches for all trains
    for (int i = 0; i < numTrains; ++i)
    {
      for (int j = 0; j < numAlignments; ++j)
      {
        // If the inbound is the same as the outbound, set the switches
        if (fleet[i].getInbound() == yard[j][0] && fleet[i].getOutbound() == yard[j][4])
        {
          Switch first = switches[yard[j][1] - 1];
          Switch second = switches[yard[j][2] - 1];
          Switch third = switches[yard[j][3] - 1];

          fleet[i].setFirstSwitch(first);
          fleet[i].setSecondSwitch(second);
          fleet[i].setThirdSwitch(third);
          path = true;
          break;
        }
      }

      if (!path)
        fleet[i].setHold();

      path = false;
    }
  }

  public static void main(String [] args) throws InterruptedException
  {
    System.out.println("Fall 2023 - Project 2 - Train Movement Simulator\n");
    System.out.println("********** INITIALIZATION OF SIMULATION DETAILS BEGINS**********\n");

    // Initialize trainInfo and yardInfo
    try
    {
      getFleetInfo();
      getYardInfo();
    }

    catch (IOException ie)
    {
      System.err.println("Could not properly initialize trainInfo or yardInfo");
    }

    // Get the number of trains, alignments, and switches
    try
    {
      numTrains = getFleetInfo();
      numAlignments = getYardInfo();
      numSwitches = getNumSwitches();
    }

    catch (IOException ie)
    {
      System.err.println("Could not properly initialize numTrains or numAlignments");
    }

    // Declare the array of switches
    Switch [] switches = initializeSwitchArray();
    assignSwitches(switches);

    trainDetails();
    yardDetails();
    System.out.println("\n\n********** SIMULATION CONFIGURATION DETAILS COMPLETE **********\n\n");

    System.out.println("$ $ $ TRAIN MOVEMENT SIMULATION BEGINS.......... $ $ $");

    ExecutorService TrainFleet = Executors.newFixedThreadPool(MAXTRAINS);

    for (int i = 0; i < numTrains; ++i)
      TrainFleet.execute(fleet[i]);
    
    TrainFleet.shutdown();

    while (!TrainFleet.isTerminated()) {}

    System.out.println("\n\n$ $ $ SIMULATION ENDS $ $ $");
  }
}
