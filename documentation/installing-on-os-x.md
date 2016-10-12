# board-of-symbols (BoS)

### Installation on Mac OS X (English)
##### Guide contributed by @rjhllr - Fall 2016
##### German version available [here!](./installing-on-os-x-de.md)


#### Preamble

This guide covers how to install and test the Board-of-Symbols (henceforth BoS) starters' programming environment on OS X. It is assumed that you want to use C as the programming language. Java is also supported by BoS, but not yet covered in-depth by this guide. Basically you need to substitute installing gcc for installing the JDK (Java Development Kit). This guide has been tested on OS X El Capitan (10.11).
This guide uses the open-source package manager [Homebrew](http://brew.sh/index.html) for installing gcc and git. 

> If you're an advanced user you may as well skip installing Homebrew and install the XCode command line tools directly and download BoS as a prepackaged archive from GitHub. The solution using Homebrew seems more future-proof to me and git should be installed on every developer's machine anyway.

---

#### Step 1 - Installing JRE

In order for the BoS application to run, the Java Runtime Environment (JRE) is needed. You can head right to the [Oracle JRE download page](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html), accept the license agreement and download the JRE.

This guide assumes you download the version packaged as a **.dmg** file. 

After downloading, open the file and double-click the installer. Follow the steps as described by the installer.


#### Step 2 - Installing Homebrew

Homebrew needs to be installed according to the instructions on the [Homebrew website](http://brew.sh/index.html). 

First off, open your terminal. You can do this by going into Launchpad (F4) and searching for "Terminal". What opens now is called a *command line*, *terminal* or *shell*. It is a text-based way to trigger actions on your computer. It's a very versatile tool.

Then paste the command as listed on the Homebrew website:

    /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

and press **Enter**. The installation process of Homebrew has now started. You will be asked for your account password over the course of the installation.


#### Step 3 - Installing git and gcc

After the Homebrew installation has finished, still in the same Terminal window, you need to set off another command.

    brew install git gcc

Homebrew will now attempt to install git and gcc. You will be asked about installing the XCode command line tools. Please confirm this installation, since gcc is shipped with the command line tools. After the installation has finished without errors, you can go ahead.

#### Step 4 - Downloading BoS

Still in Terminal, you can create a new folder and download BoS into it. 

    mkdir ~/Documents/BoS/ && cd ~/Documents/BoS

These two commands create a new folder (*mkdir*, make directory) and navigate into it (*cd*, change directory).

    git clone https://github.com/stephaneuler/board-of-symbols.git --depth 1 ./

this will download BoS into the newly created directory.

#### Step 5 - Testing

Open the Finder and navigate to the newly created folder "BoS" in "Documents". Double-click the **jserver.jar** Java archive. If everything went according to plan, you will now have a working copy of BoS. Open the Code Window and try compiling a snippet along the lines of:

    farbe(1, 0xff);

which should color one of the circles in the main window red.

