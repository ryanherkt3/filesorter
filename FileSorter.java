package filesorter;

import java.io.*;
import java.util.*;

/**
 * A class which can sort files by firstly using the quick sort sorting 
 * algorithm by splitting the input file into smaller temporary files, then 
 * using the merge sort sorting algorithm to get the contents of two small files 
 * by reading them at the same time, and adding the contents of these two files 
 * in alphabetical order into one big sorted file until only one file remains, 
 * which is renamed to the name of the output file (in the constructor).
 * 
 * @author Ryan Herkt (ID: 18022861)
 */
public class FileSorter implements Runnable
{
    private final int limit;
    private int splits;
    private int merges;
    
    private int maxSplits;
    private int maxMerges;
    
    private final File input;
    private final File output;
    private final Queue<File> files = new LinkedList<>();
    
    public FileSorter(int limit, File input, File output)
    {
        this.limit = limit;
        this.input = input;
        this.output = output;
        this.splits = 0;
        this.merges = 0;
        setMaxSplits(maxSplits);
        setMaxMerges(maxSplits);
    }

    @Override
    public void run() 
    {
        firstStage();
        mergeStage();
    }
    
    public void firstStage()
    {
        try
        {
            BufferedReader inputStream = new BufferedReader(new FileReader(input));
            String line;
            String[] itemsToSort;

            ArrayList<String> items = new ArrayList<>();
            int linesRead = 0;
            while((line=inputStream.readLine())!=null)
            {
                items.add(line);
                ++linesRead;

                if (linesRead == limit)
                {
                    linesRead = 0;
                    ++this.splits;
                    itemsToSort = items.toArray(new String[0]);
                    writeToFile(itemsToSort);
                    items = new ArrayList<>();  //reset
                }
            }
            inputStream.close();
            
            itemsToSort = items.toArray(new String[0]);
            if (itemsToSort.length != 0)  //don't want to create empty files
            {
                ++this.splits;
                writeToFile(itemsToSort);
            }
        }
        catch(FileNotFoundException e) 
        {
            System.out.println("File not found.");
        }
        catch(IOException e) 
        {
            System.out.println("Error reading from text file.");
        }
    }
    
    public void writeToFile(String[] items)
    {
        try
        {
            QuickSort<String> sorter = new QuickSort<>();
            sorter.quickSort(items);
            
            String line;
            
            File temp = new File(this.splits + " split.txt");
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp, false));
            
            for (String item : items) 
            {
                line = item;
                //formatting:
                if (item.compareTo(items[items.length-1]) != 0)
                    bw.append(line + "\n");
                else
                    bw.append(line);
            }
            
            files.offer(temp);  //add to queue
            bw.close();
        }
        catch(FileNotFoundException e) 
        {
            System.out.println("File not found.");
        }
        catch(IOException e) 
        {
            System.out.println("Error reading from text file.");
        }
    }
    
    public void mergeStage()
    {
        String line;
        try
        {
            this.merges = 1;
        
            while(files.size() > 1)
            {
                File f1 = files.poll();
                File f2 = files.poll();
                
                //create two string arrays, one for each file:
                ArrayList<String> one = new ArrayList<>();
                ArrayList<String> two = new ArrayList<>();
                
                BufferedReader br1 = new BufferedReader(new FileReader(f1));
                BufferedReader br2 = new BufferedReader(new FileReader(f2));

                while((line=br1.readLine())!=null)
                    one.add(line);
                
                while((line=br2.readLine())!=null)
                    two.add(line);

                //close the streams and delete the two files:
                br1.close();
                br2.close();
                f1.delete();
                f2.delete();
                                
                File temp = new File(merges + " merge.txt");
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp, false));
                
                //create counter for number of items written from first and 
                //second files:
                int firstItemsWritten = 0;
                int secondItemsWritten = 0;
                
                //write in items to temp file 
                for (int i = firstItemsWritten; i < one.size(); i++) 
                {
                    for (int j = secondItemsWritten; j < two.size(); j++)
                    {
                        if (one.get(firstItemsWritten).compareTo(two.get(secondItemsWritten)) <= 0)
                        {
                            line = one.get(firstItemsWritten++);
                            j = two.size(); //exit inner loop
                        }
                        else
                        {
                            line = two.get(secondItemsWritten++);
                        }

                        line += "\n";
                        bw.append(line);
                    }
                }
                
                //conditions if one text file has all it's items written to 
                //the new file, but other text file doesn't:
                if (firstItemsWritten < one.size())
                {
                    for (int i = firstItemsWritten; i < one.size(); i++)
                    {
                        line = one.get(firstItemsWritten++);
                        //formatting:
                        if (i < one.size()-1)
                            line += "\n";
                        bw.append(line);
                    }
                }
                else if (secondItemsWritten < two.size())
                {
                    for (int i = secondItemsWritten; i < two.size(); i++)
                    {
                        line = two.get(secondItemsWritten++);
                        //formatting:
                        if (i < two.size()-1)
                            line += "\n";
                        bw.append(line);
                    }
                }

                files.offer(temp);  //add to queue
                bw.close(); //close stream
                
                ++this.merges;
            }
            File last = files.poll();
            last.renameTo(output);  //rename last remaining, sorted file
            last.delete();
        }
        catch(FileNotFoundException e) 
        {
            System.out.println("File not found.");
        }
        catch(IOException e) 
        {
            System.out.println("Error reading from text file.");
        }
    }
    
    /**
     * @return the input
     */
    public File getInputFile() 
    {
        return input;
    }
    
    /**
     * @return the output
     */
    public File getOutputFile() 
    {
        return output;
    }

    /**
     * @return the maxSplits
     */
    public boolean isMergeDone() 
    {
        return getMergeProgress() == 100;
    }

    /**
     * @return the maxMerges
     */
    public boolean isSplitDone() 
    {
        return getSplitProgress() == 100;
    }

    /**
     * @param maxSplits the maxSplits to set
     */
    public void setMaxSplits(int maxSplits) 
    {
        try
        {
            int lineCount = 0;
            BufferedReader inputStream = new BufferedReader(new FileReader(input));
            
            while(inputStream.readLine()!=null)
                ++lineCount;
            System.out.println("Lines to read: " + lineCount);
            
            if (lineCount % limit > 0)
                this.maxSplits = (lineCount / limit) + 1;
            else
                this.maxSplits = lineCount / limit;
            
            inputStream.close();
        }
        catch(FileNotFoundException e) 
        {
            System.out.println("File not found.");
        }
        catch(IOException e) 
        {
            System.out.println("Error reading from text file.");
        }
    }

    /**
     * @return the splits
     */
    public int getSplitProgress() 
    {
        //multiply by 100
        return (splits / maxSplits) * 100;
    }

    /**
     * @return the merges
     */
    public int getMergeProgress() 
    {
        //multiply by 100
        return (merges / maxMerges) * 100;
    }

    public void setMaxMerges(int maxSplits) 
    {
        this.maxMerges = maxSplits-1;
    }
}