package com.softtechdesign.ga.examples;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.softtechdesign.ga.*;

public class GAApplet extends JApplet
{
    boolean isStandalone = false;
    String[] galist = { "Sort", "Binary All Ones", "Curve Fit", "Traveling Salesman", "Trig Func", "Maze", "Horaire" };
    JList jListGA = new JList(galist);
    JLabel jLabel1 = new JLabel();
    JButton jButtonRun = new JButton();
    Image imgDev = null;
    Image imgFit = null;
    JLabel jLabelBestChromosome = new JLabel();

    public String getParameter(String key, String def)
    {
        return isStandalone
            ? System.getProperty(key, def)
            : (getParameter(key) != null ? getParameter(key) : def);
    }

    public String[][] getParameterInfo()
    {
        return null;
    }

    public GAApplet()
    {
    }

    public void init()
    {
        try
        {
            jbInit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void jbInit() throws Exception
    {
        this.setSize(new Dimension(491, 317));
        Container contentPane = this.getContentPane();
        contentPane.setLayout(null);
        JScrollPane jScrollPane = new JScrollPane(jListGA);
        jScrollPane.setBounds(new Rectangle(162, 13, 176, 23));
        jListGA.setVisibleRowCount(1);
        jListGA.setSelectedIndex(1);
        jLabel1.setHorizontalAlignment(SwingConstants.RIGHT);
        jLabel1.setText("Genetic Algorithm");
        jLabel1.setBounds(new Rectangle(52, 16, 104, 17));
        jButtonRun.setText("Run");
        jButtonRun.setBounds(new Rectangle(341, 11, 63, 27));
        jButtonRun.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jButtonRun_actionPerformed(e);
            }
        });
        contentPane.setBackground(Color.white);
        jLabelBestChromosome.setForeground(Color.red);
        jLabelBestChromosome.setHorizontalAlignment(SwingConstants.CENTER);
        jLabelBestChromosome.setBounds(new Rectangle(2, 45, 486, 17));
        contentPane.add(jLabel1, null);
        contentPane.add(jScrollPane, null);
        contentPane.add(jButtonRun, null);
        contentPane.add(jLabelBestChromosome, null);
    }

    public void start()
    {
    }

    public void stop()
    {
    }

    public void destroy()
    {
    }

    public String getAppletInfo()
    {
        return "Genetic Algorithm Applet written by Jeff Smith";
    }

    public void paint(Graphics g)
    {
        super.paint(g);
        if (imgDev != null)
            g.drawImage(imgDev, 10, 75, this);
        if (imgFit != null)
            g.drawImage(imgFit, 10, 275, this);
    }

    void plotDeviationAndFitness(GA ga)
    {
        int xPos, yPos;
        double xScaleFactor, yScaleFactor;
        int xDim = 470;
        int yDim = 170;

        //PLOT THE AVERAGE DEVIATION OF CHROMOSOMES
        imgDev = createImage(xDim, yDim);
        imgDev.getGraphics().drawRect(10, 0, xDim - 11, yDim - 13);

        double maxDeviation = ga.getAvgDeviation(0);
        yScaleFactor = (double) (yDim - 24) / maxDeviation;
        xScaleFactor = (double) (xDim - 10) / ga.getMaxGenerations();

        imgDev.getGraphics().drawString("Average Deviation of Chromosomes", xDim / 2 - 90, 12);
        imgDev.getGraphics().drawString("0", 9, yDim);
        imgDev.getGraphics().drawString("" + ga.getMaxGenerations(), xDim - 30, yDim);
        imgDev.getGraphics().drawString("Number of Generations", xDim / 2 - 46, yDim);

        for (int i = 0; i < ga.getMaxGenerations(); i++)
        {
            xPos = 10 + (int) (xScaleFactor * (double)i);
            yPos = yDim - ((int) (yScaleFactor * ga.getAvgDeviation(i)) + 18);
            imgDev.getGraphics().drawOval(xPos, yPos, 2, 2);
        }

        //NOW PLOT THE AVERAGE FITNESS OF THE CHROMOSOMES
        imgFit = createImage(xDim, yDim);
        imgFit.getGraphics().drawRect(10, 0, xDim - 11, yDim - 13);

        double maxFitness = ga.getFittestChromosomesFitness();
        yScaleFactor = (double) (yDim - 10) / maxFitness;
        xScaleFactor = (double) (xDim - 10) / ga.getMaxGenerations();

        imgFit.getGraphics().drawString("Average Fitness of Chromosomes", xDim / 2 - 90, 12);
        imgFit.getGraphics().drawString("0", 9, yDim);
        imgFit.getGraphics().drawString("" + ga.getMaxGenerations(), xDim - 30, yDim);
        imgFit.getGraphics().drawString("Number of Generations", xDim / 2 - 46, yDim);

        for (int i = 0; i < ga.getMaxGenerations(); i++)
        {
            xPos = 10 + (int) (xScaleFactor * (double)i);
            yPos = 8 + yDim - ((int) (yScaleFactor * ga.getAvgFitness(i)));
            imgFit.getGraphics().setColor(Color.blue);
            imgFit.getGraphics().drawOval(xPos, yPos, 2, 2);
        }

        repaint(); //calls paint() and displays plot images
    }

    void jButtonRun_actionPerformed(ActionEvent e)
    {
        imgDev = imgFit = null;
        jLabelBestChromosome.setText("Evolution simulation is running, please wait...");
        paint(this.getContentPane().getGraphics());
        int index = jListGA.getFirstVisibleIndex();
         
        switch (index)
        {
            case 0 :
            	System.out.println("GASort GA...");
                try
                {
                    GASort gaSort = new GASort();
                    gaSort.evolve();
                    plotDeviationAndFitness(gaSort);
                    jLabelBestChromosome.setText(
                        "Best Chrom= "
                            + gaSort.getFittestChromosome().toString()
                            + " (fitness= "
                            + gaSort.getFittestChromosomesFitness()
                            + ")");
                    System.out.println(gaSort.getFittestChromosomesFitness());
                }
                catch (GAException gae)
                {
                    System.out.println(gae.getMessage());
                }
                break;

            case 1 :
                System.out.println("GACurveFit GA...");
                double[] curveData = { 1.01, 0, 1, 3.98, 9.01, 16.01, 25, 35.99 };
                try
                {
                    GACurveFit curveFit = new GACurveFit(curveData);
                    curveFit.evolve();
                    plotDeviationAndFitness(curveFit);
                    jLabelBestChromosome.setText(
                        "Best Chrom= "
                            + curveFit.getFittestChromosome().toString()
                            + " (fitness= "
                            + curveFit.getFittestChromosomesFitness()
                            + ")");
                }
                catch (GAException gae)
                {
                    System.out.println(gae.getMessage());
                }
                break;

            case 2 :
                System.out.println("Starting GASalesman GA...");
                try
                {
                    GASalesman salesman = new GASalesman();
                    salesman.evolve();
                    plotDeviationAndFitness(salesman);
                    jLabelBestChromosome.setText(
                        "Best Chrom= "
                            + salesman.getFittestChromosome().toString()
                            + " (fitness= "
                            + salesman.getFittestChromosomesFitness()
                            + ")");
                }
                catch (GAException gae)
                {
                    System.out.println(gae.getMessage());
                }
                break;

            case 3 :
                System.out.println("GATrigFunc GA...");
                try
                {
                    GATrigFunc trigFunc = new GATrigFunc();
                    trigFunc.evolve();
                    plotDeviationAndFitness(trigFunc);
                    jLabelBestChromosome.setText(
                        "Best Chrom= "
                            + trigFunc.getFittestChromosome().toString()
                            + " (fitness= "
                            + trigFunc.getFittestChromosomesFitness()
                            + ")");
                }
                catch (GAException gae)
                {
                    System.out.println(gae.getMessage());
                }
                break;

            case 4 :
                System.out.println("GAMaze GA...");
                try
                {
                    GAMaze gaMaze = new GAMaze();
                    gaMaze.evolve();
                    plotDeviationAndFitness(gaMaze);
                    jLabelBestChromosome.setText(
                        "Best Chrom= "
                            + gaMaze.getFittestChromosome().toString()
                            + " (fitness= "
                            + gaMaze.getFittestChromosomesFitness()
                            + ")");
                }
                catch (GAException gae)
                {
                    System.out.println(gae.getMessage());
                }
                break;
            case 5 :
            	System.out.println("Starting GAHoraire...");
                try
                {
                    GAHoraire binaryOnes = new GAHoraire();
                    binaryOnes.evolve();
                    plotDeviationAndFitness(binaryOnes);
                    jLabelBestChromosome.setText(
                        "Best Chrom= "
                            //+ binaryOnes.getFittestChromosome().toString()
                            + " (fitness= "
                            + binaryOnes.getFittestChromosomesFitness()
                            + ")");
                }
                catch (GAException gae)
                {
                    System.out.println(gae.getMessage());
                }
                break;
        }
    }
}