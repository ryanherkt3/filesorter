package filesorter;

/**
  * This QuickSort routine has been taken from the ArraySorter java file, which 
  * was in the 'chapter3' zip file on Blackboard. The assignment brief says I 
  * can use an existing class for the quick sort routine, which is what this 
  * class is.
  * 
  * @author Ryan Herkt (ID: 18022861) (original author: sehall/Andrew Ensor)
  * @param <E>
  */

public class QuickSort<E extends Comparable>
{
    public void quickSort(E[] list)
    {  
        quickSortSegment(list, 0, list.length);
    }
   
    // recursive method which applies quick sort to the portion
    // of the array between start (inclusive) and end (exclusive)
    private void quickSortSegment(E[] list, int start, int end)
    {  
        if (end-start>1) // then more than one element to sort
        {  
            // partition the segment into two segments
            int indexPartition = partition(list, start, end);
            // sort the segment to the left of the partition element
            quickSortSegment(list, start, indexPartition);
            // sort the segment to the right of the partition element
            quickSortSegment(list, indexPartition+1, end);
        }
    }
   
   
    /**
     * use the index start to partition the segment of the list 
     * with the element at start as the partition element 
     * separating the list segment into two parts, one less than 
     * the partition, the other greater than the partition 
     * returns the index where the partition element ends up
     * 
     * @param list
     * @param start
     * @param end
     * @return 
     */
    private int partition(E[] list, int start, int end)
    {  
        E temp; // temporary reference to an element for swapping
        E partitionElement = list[start];
        int leftIndex = start; // start at the left end
        int rightIndex = end-1; // start at the right end

        // swap elements so elements at left part are less than
        // partition element and at right part are greater
        while (leftIndex<rightIndex)
        {  
            // find element starting from left greater than partition
            while (list[leftIndex].compareTo(partitionElement) <= 0 && leftIndex<rightIndex)
                leftIndex++; // this index is on correct side of partition

            // find element starting from right less than partition
            while (list[rightIndex].compareTo(partitionElement) > 0)
                rightIndex--; // this index is on correct side of partition

            if (leftIndex<rightIndex)
            {   // swap these two elements
                temp = list[leftIndex];
                list[leftIndex] = list[rightIndex];
                list[rightIndex] = temp;
            }
        }
        
        // put the partition element between the two parts at rightIndex
        list[start] = list[rightIndex];
        list[rightIndex] = partitionElement;
        return rightIndex;
    }
}
