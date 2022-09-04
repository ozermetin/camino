Introductory user guide for the Camino toolkit
______________________________________________

$Id: README.txt 58 2006-10-26 11:38:18Z ucacmgh $

This document provides a quick introduction to the Camino toolkit to
help new users get started.  Each program in the toolkit has a man
page, which provides more detailed information on specific tools.


Installation
============

Unpack the toolkit:

% bunzip2 camino.tar.bz2
% tar -xvf camino

Compile (you will need to have the java SDK 1.4 or later and have javac 
and java in your path):

% cd camino
% make


Cygwin
======

Although the individual components of Camino will run from a commandline under windows, 
in order to get the most out of Camino it is necessary to have a UNIX-like shell 
environment that allows data pipes and redirection. Without these facilities, it is 
extremely difficult to use Camino in the way it was designed.

Fortunately, it is extremely easy to install Camino under windows with Cygwin, this 
section explains the procedure step-by-step. Firstly, download the Cygwin installer 
from Sun is installed. Make sure you have the SDK as well as the usual JRE! 
This is not installed under windows as standard!

Once Cygwin and the Java SDK are installed on your system, check that the location of 
the Java SDK is added to you windows path. You can do this as follows:

1) From the desktop, click start and right-click on "My Computer"
2) In the "System Properties" window that appears, select the "advanced" tab
3) Click the "Environment variables" button.
4) Highlight the "path" variable and click the "edit" button
5) If the path to your Java SDK is not in the list, add the FULL path to the end 
   of the list, using a semi-colon to separate it from the previous entry

Now start Cygwin and follow the instructions for installing Camino under linux/unix. 
For instructions on how to install geomview under windows and Cygwin, click here (SaVi 
is not required)


Getting Started
===============

Read the camino man page for an overview of the toolkit:

(% cd camino)
% man -M man camino

Further help getting started is available from the Camino website

http://www.cs.ucl.ac.uk/research/medic/camino/guide.htm

The website also contains case studies using Camino to perform some common tasks
in diffusion imaging. See

http://www.cs.ucl.ac.uk/research/medic/camino/case/case.htm 

for details.



Testing (for developers)
=======================

There are two sets of tests distributed with the code. The unit tests are low 
level, object-orientated tests of the Java classes. To make and run the unit 
tests: 

% cd camino/test
% make
% ./runtest.sh

The JUnit tests are known to work on a Linux PC, and also on an Intel iMac. 
Some tests may fail on other platforms because of machine precision issues. 
Currently, the following tests are known to behave differently on some platforms:

Class			   Fails on

sphfunc.TestImagSH	   Mac OS X (G3 CPU)
sphfunc.TestRealSH	   Mac OS X (G3 CPU)


The other level of testing in Camino is at the application level. To run this test:

% cd camino
% test/ScriptTest > testoutput
% diff testoutput test/ScriptTest.out

A successful test will produce no differences between the file testoutput and 
test/ScriptTest.out. The file ScriptTest.out that is distributed with Camino is 
generated on an Intel PC running Linux. The output may be different on other 
platforms. If you plan to modify Camino code, then you should run ScriptTest
on your local machine before making changes and save the results. You can then
make your changes and compare the output of ScriptTest to your baseline results.


Contributions
=============

The developers welcome contributions to the Camino toolkit.  However,
to maintain the readability and extendability of Camino, please ensure
that you follow the coding and documentation style in the core system.
Only contributions that conform to the Camino style and are
sufficiently documented, i.e., include javadoc and man pages, will be
added to the general release.


Queries and bug reports
=======================

Queries, bug report and feature requests for future releases of Camino should
go to camino@cs.ucl.ac.uk.
