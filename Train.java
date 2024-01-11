/*
 *  Name:Brendan Smith
 *  Course: CNT 4714 Fall 2023
 *  Assignment title: Projet 2 - Multi-threaded programming in Java
 *  Date: October 10, 2023
 *
 *  Class: Train.Java
 *
 *  Description: Defines what a Train object is and runs the operations that the threads go through
 */

import java.util.Random;

public class Train implements Runnable
{
  /* Thread properties of the train */
  private Random gen = new Random();
  private final static int MAXWAITONLOCK = 1000;
  private final static int MOVETIME = 300;

  /* Regular properties of the train */
  private int trainNum;
  private int inbound, outbound;
  private boolean hold, moved, allLocks;
  private Switch firstSwitch, secondSwitch, thirdSwitch;

  public Train(String [] props)
  {
    this.trainNum = Integer.parseInt(props[0]);
    this.inbound  = Integer.parseInt(props[1]);
    this.outbound = Integer.parseInt(props[2]);

    this.hold = false;
    this.moved = false;
    this.allLocks = false;
  }

  /* Thread Methods */

  // Put trains to sleep after failing to get necessary switches
  public void lockRequestWaitTime()
  {
    try
    {
      Thread.sleep(gen.nextInt(MAXWAITONLOCK));
    }

    catch(InterruptedException e)
    {
      e.printStackTrace();
    }
  }

  public void moveTrain()
  {
    System.out.println("\nTrain " + this.trainNum + " HOLDS ALL NEEDED SWITCH LOCKS - Train movement begins.\n\n");

    try
    {
      Thread.sleep(MOVETIME);
    }

    catch(InterruptedException e)
    {
      e.printStackTrace();
    }

    System.out.println("Train " + trainNum + " clear of yard control.\n");

    System.out.println("Train " + trainNum + ": Releasing all switch locks.");
    System.out.println("Train " + trainNum + ": Unlocks/relases lock on switch " + firstSwitch.switchNum);
    System.out.println("Train " + trainNum + ": Unlocks/relases lock on switch " + secondSwitch.switchNum);
    System.out.println("Train " + trainNum + ": Unlocks/relases lock on switch " + thirdSwitch.switchNum);

    System.out.println("\t\t\t\t\t\t\t\t\tTRAIN " + trainNum + ": Has been dispatched and moves on down the line out of yard control into CTC.");
    System.out.println("\t\t\t\t\t\t\t\t\t@ @ @ @ @ @ @ TRAIN " + trainNum + ": DISPATCHED @ @ @ @ @ @ @\n");

    this.moved = true;
    this.firstSwitch.unlockSwitch();
    this.secondSwitch.unlockSwitch();
    this.thirdSwitch.unlockSwitch();
  }

  public void run()
  {
    if (hold)
    {
      noRoute();
      return;
    }

    while (!moved)
    {
      while (!allLocks)
      {
        // Get lock  on first switch
        if (firstSwitch.lockSwitch())
        {
          printSwitchCanLock(firstSwitch.switchNum);

          // Gets lock on second switch
          if (secondSwitch.lockSwitch())
          {
            printSwitchCanLock(secondSwitch.switchNum);

            // Gets lock on third switch
            if (thirdSwitch.lockSwitch())
            {
              printSwitchCanLock(thirdSwitch.switchNum);

              this.allLocks = true;
              moveTrain();
              break;
            }

            // If third lock isn't available
            else
            {
              printSwitchCannotLockThree();

              firstSwitch.unlockSwitch();
              secondSwitch.unlockSwitch();
              lockRequestWaitTime();
            }
          }

          // If second lock isn't available
          else
          {
            printSwitchCannotLockTwo();
            // System.out.println("Train " + trainNum + ": UNABLE TO LOCK second required switch: Switch "+ secondSwitch.switchNum + ".");
            // System.out.println("Releasing lock on first required switch: Switch " + firstSwitch.switchNum + ". Train will wait...\n");

            firstSwitch.unlockSwitch();
            lockRequestWaitTime();
          }
        }

        // If first lock isn't available
        else
        {
          printSwitchCannotLockOne();
          lockRequestWaitTime();
        }
      }
    }
  }


  /* Printing Statements */

  // Prints switch num
  public void printSwitchCanLock(int switchVal)
  {
    System.out.println("Train " + trainNum + ": HOLDS LOCK on Switch " + switchVal + ".\n");
  }

  public void printSwitchCannotLockOne()
  {
    System.out.print("Train " + trainNum + ": UNABLE TO LOCK first required switch: Switch " + firstSwitch.switchNum
                     + ". Train will wait...\n");
  }

  public void printSwitchCannotLockTwo()
  {
    System.out.println("Train " + trainNum + ": UNABLE TO LOCK second required switch: Switch " + secondSwitch.switchNum + ".");
    System.out.println("Releasing lock on first required switch: Switch " + firstSwitch.switchNum + ". Train will wait...\n");
  }

  public void printSwitchCannotLockThree()
  {
    System.out.println("Train " + trainNum + ": UNABLE TO LOCK third required switch: Switch "+ thirdSwitch.switchNum + ".");
    System.out.println("Releasing locks on first and second required switches: Switch " + firstSwitch.switchNum + " and Switch "
                        + secondSwitch.switchNum + ". Train will wait...\n");
  }

  public void noRoute()
  {
    System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************");
    System.out.println("\t\t\t\t\t\t\t\t\t\t\t\tTrain " + trainNum + " is on permanent hold and cannot be dispatched.");
    System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t*******************************************************");
  }

  /* Getter Methods */

  // Train Num
  public int getTrainNum() {return this.trainNum;}

  // Return in/outbound
  public int getInbound() {return this.inbound;}
  public int getOutbound() {return this.outbound;}

  // Switch numbers
  public int getFirstSwitch() {return this.firstSwitch.switchNum;}
  public int getSecondSwitch() {return this.secondSwitch.switchNum;}
  public int getThirdSwitch() {return this.thirdSwitch.switchNum;}

  // Whether the train is on permanent hold
  public boolean getHold() {return this.hold;}

  /* Setter Methods for Switches */
  public void setFirstSwitch(Switch s) {this.firstSwitch = s;}
  public void setSecondSwitch(Switch s) {this.secondSwitch = s;}
  public void setThirdSwitch(Switch s) {this.thirdSwitch = s;}

  /* Set hold if no path */
  public void setHold() {this.hold = true;}
}
