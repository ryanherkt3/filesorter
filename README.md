# File Sorter GUI

A set of classes which make up a GUI in which text files can be sorted alphabetically, written in Java.

## Classes

FileSorter: 

* A class which sorts files by using quick sort and merge sort (two stages). 
* Implements the Runnable interface.
* Has a constructor with parameters for the max. number of strings in a file and input and output Files
* Has a run method, which calls the first sorting stage (quick sort), then the second sorting stage (merge sort)
* Stage One quick sorts all the Strings in a list and puts these sorted Strings in a text file, which is added to a queue of files.
* Stage Two merge sorts all the files from stage one, by getting the Strings from the first two files from the queue (if it can) and sorting these into one file, which is then added to the queue. The first two files are removed from the queue.

FileSorterGUI:

* A class which represents a File Sorting GUI.
* A user is asked for the max. number of strings they'd like to store in memory, the file (name) to sort and the name of the sorted file.
* 'Enqueue Task' puts a task in a FileSorter queue (if all above inputs are valid); 'Process Task' executes the task at the head of the FileSorter queue.
* Multiple tasks can be run concurrently as all FileSorter Tasks are multi-threaded (i.e. have their own thread)

QuickSort:

* The class which uses quick sort to sort a list of any E-type object.
