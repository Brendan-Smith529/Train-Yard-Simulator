/*
 *  Name:Brendan Smith
 *  Course: CNT 4714 Fall 2023
 *  Assignment title: Projet 2 - Multi-threaded programming in Java
 *  Date: October 10, 2023
 *
 *  Class: Switch.Java
 *
 *  Description: Defines what a Switch object is and the properties
 *               it contains
 */

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Switch
{
  // Class Variables
  int switchNum;
  private Lock lock = new ReentrantLock();

  public Switch(int num)
  {
    this.switchNum = num;
  }

  public boolean lockSwitch()
  {
    return lock.tryLock(); // Tries to lock the thread; returns true if succesful
  }

  public void unlockSwitch()
  {
    lock.unlock(); // Unlocks the thread
  }
}
