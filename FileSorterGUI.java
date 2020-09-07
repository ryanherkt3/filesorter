package filesorter;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

/**
 * A GUI class which allows the user to enqueue multiple FileSorter tasks and 
 * then process these tasks. The user is told when the task is complete, 
 * however...
 * 
 * Whilst the progress bars do update, I don't think they update correctly as 
 * they only update *after* the particular sorting stage is done, rather than 
 * during the task.
 * 
 * @author Ryan Herkt (ID: 18022861)
 */
public class FileSorterGUI extends JPanel implements ActionListener
{
    private final JLabel input, output, numItems;
    private final JTextField limitNum;
    
    private final JLabel quickSort, mergeSort;
    private final JProgressBar quickProgressBar, mergeProgressBar;
    
    private final JButton enqueueTask, processTask;
        
    private final Queue<FileSorter> taskQueue = new LinkedList();
    private FileSorter currentTask;
    
    private Thread aThread;
    private final Timer timer;
    
    public FileSorterGUI()
    {
        super(new GridLayout(7,1));
        
        numItems = new JLabel("Number of items in queue: " + taskQueue.size());
        
        limitNum = new JTextField();
        TitledBorder tb1;
        tb1 = BorderFactory.createTitledBorder("Max Strings in memory:");
        limitNum.setBorder(tb1);
        Font f = limitNum.getFont();
        limitNum.setFont(f.deriveFont(f.getStyle() & ~Font.BOLD));
        
        input = new JLabel();
        TitledBorder tb2;
        tb2 = BorderFactory.createTitledBorder("Input file:");
        input.setBorder(tb2);
        input.setText("N/A");
        Font f1 = input.getFont();
        input.setFont(f1.deriveFont(f1.getStyle() & ~Font.BOLD));
        
        output = new JLabel();
        TitledBorder tb3;
        tb3 = BorderFactory.createTitledBorder("Output file:");
        output.setBorder(tb3);
        output.setText("N/A");
        Font f2 = output.getFont();
        output.setFont(f2.deriveFont(f2.getStyle() & ~Font.BOLD));
        
        JPanel quickPanel = new JPanel(new GridLayout(2,1));
        quickSort = new JLabel("Quick sort progress:");
        quickProgressBar = new JProgressBar(0, 100);
        quickProgressBar.setValue(0);
        quickProgressBar.setStringPainted(false);
        quickPanel.add(quickSort);
        quickPanel.add(quickProgressBar);
        
        JPanel mergePanel = new JPanel(new GridLayout(2,1));
        mergeSort = new JLabel("Merge sort progress:");
        mergeProgressBar = new JProgressBar(0, 100);
        mergeProgressBar.setValue(0);
        mergeProgressBar.setStringPainted(false);
        mergePanel.add(mergeSort);
        mergePanel.add(mergeProgressBar);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1,2));
        enqueueTask = new JButton("Enqueue Task");
        enqueueTask.addActionListener(this);
        processTask = new JButton("Process Task");
        processTask.addActionListener(this);
        processTask.setEnabled(false);
        buttonPanel.add(enqueueTask);
        buttonPanel.add(processTask);
        
        add(numItems);
        add(limitNum);
        add(input);
        add(output);
        add(quickPanel);
        add(mergePanel);
        add(buttonPanel);
        
        timer = new Timer(20, this); //create a timer    
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        
        if (source == enqueueTask)
        {
            JFileChooser inputF = new JFileChooser();
            JFileChooser outputF = new JFileChooser();

            inputF.setCurrentDirectory(new java.io.File("."));
            inputF.setDialogTitle("Input File");
            inputF.setFileSelectionMode(JFileChooser.FILES_ONLY);
            outputF.setCurrentDirectory(new java.io.File("."));
            outputF.setDialogTitle("Output File");
            outputF.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            if (inputF.showOpenDialog(enqueueTask) == JFileChooser.APPROVE_OPTION)
            {}
            File toSort = inputF.getSelectedFile();
            
            File sortedFile;
            if (toSort != null)
            {
                if (outputF.showOpenDialog(enqueueTask) == JFileChooser.APPROVE_OPTION)
                {}
                sortedFile = outputF.getSelectedFile();
                
                if (sortedFile != null)
                {
                    if (!limitNum.getText().isEmpty())
                    {
                        try
                        {
                            if (Integer.parseInt(limitNum.getText()) <= 0)
                                JOptionPane.showMessageDialog(this, "Enter a "
                                        + "number above zero!", "ERROR",
                                    JOptionPane.ERROR_MESSAGE);
                            else
                            {
                                taskQueue.offer(new FileSorter
                                (Integer.parseInt(limitNum.getText()), toSort, 
                                        sortedFile));
                                processTask.setEnabled(true);
                                input.setText(taskQueue.peek().getInputFile().getName());
                                output.setText(taskQueue.peek().getOutputFile().getName());
                                numItems.setText("Number of items in queue: " + 
                                        taskQueue.size());
                            }
                        }
                        //won't work if max strings isn't an integer:
                        catch (NumberFormatException ex)
                        {
                            JOptionPane.showMessageDialog(this, 
                                    "Invalid number input! " + ex, "ERROR",
                                  JOptionPane.ERROR_MESSAGE);  
                        }
                    }
                    else    //won't work if user fails to enter string limit
                        JOptionPane.showMessageDialog(this, "Enter a string limit!", "ERROR",
                                JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        if (source == processTask)
        {
            currentTask = taskQueue.peek();
            processTask.setEnabled(false);

            aThread = new Thread(currentTask);
            timer.start();
            aThread.start();
        }
        
        if (source == timer)
        {
            quickProgressBar.setValue(currentTask.getSplitProgress());
            mergeProgressBar.setValue(currentTask.getMergeProgress());
                
            if (currentTask.isMergeDone())
            {
                timer.stop();
                taskQueue.poll();

                JOptionPane.showMessageDialog(this, "Merge sort of '" + 
                        currentTask.getInputFile().getName() + "' completed!", 
                            "INFO", JOptionPane.INFORMATION_MESSAGE);

                if (taskQueue.size() > 0)
                {
                    currentTask = taskQueue.peek();
                    input.setText(taskQueue.peek().getInputFile().getName());
                    output.setText(taskQueue.peek().getOutputFile().getName());
                    processTask.setEnabled(true);
                }
                else
                {
                    currentTask = null;
                    input.setText("N/A");
                    output.setText("N/A");
                    limitNum.setText("0");
                    processTask.setEnabled(false);
                }

                quickProgressBar.setValue(0);
                mergeProgressBar.setValue(0);
                numItems.setText("Number of items in queue: " + taskQueue.size());
            }
        }
    }
    
    public static void main (String args[])
    {
        JFrame frame = new JFrame("File Sorter");
        
        frame.setMinimumSize(new Dimension(450, 300));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new FileSorterGUI());
        frame.pack();
        
        // position the frame in the middle of the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenDimension = tk.getScreenSize();
        Dimension frameDimension = frame.getSize();
        frame.setLocation((screenDimension.width-frameDimension.width)/2,
           (screenDimension.height-frameDimension.height)/2);
        frame.setVisible(true);
    }
}